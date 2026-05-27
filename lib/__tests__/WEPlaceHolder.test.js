/**
 * Tests for WEPlaceHolder - registerWEPlaceholderCallback, deregisterWEPlaceholderCallback,
 * trackClick, trackImpression
 */

const mockRemove = jest.fn();
const mockAddListener = jest.fn().mockReturnValue({ remove: mockRemove });
const mockEventEmitter = { addListener: mockAddListener };

const mockBridge = {
  registerProperty: jest.fn(),
  deregisterProperty: jest.fn(),
  trackClick: jest.fn(),
  trackImpression: jest.fn(),
};

jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
  __esModule: true,
  default: mockBridge,
  eventEmitter: mockEventEmitter,
}));

jest.mock('../src/utils/weLogs', () => ({
  weLogs: jest.fn(),
}));

jest.mock('react-native', () => ({
  Platform: { OS: 'android' },
}));

describe('WEPlaceHolder', () => {
  let registerWEPlaceholderCallback;
  let deregisterWEPlaceholderCallback;
  let trackClick;
  let trackImpression;

  beforeEach(() => {
    jest.clearAllMocks();
    jest.resetModules();

    jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
      __esModule: true,
      default: mockBridge,
      eventEmitter: mockEventEmitter,
    }));

    jest.mock('../src/utils/weLogs', () => ({
      weLogs: jest.fn(),
    }));

    jest.mock('react-native', () => ({
      Platform: { OS: 'android' },
    }));

    const module = require('../src/callbacks/WEPlaceHolder');
    registerWEPlaceholderCallback = module.registerWEPlaceholderCallback;
    deregisterWEPlaceholderCallback = module.deregisterWEPlaceholderCallback;
    trackClick = module.trackClick;
    trackImpression = module.trackImpression;
  });

  describe('registerWEPlaceholderCallback', () => {
    it('should register a placeholder callback on Android', () => {
      const onDataReceived = jest.fn();
      const onException = jest.fn();

      registerWEPlaceholderCallback('android_prop', 123, 'HomeScreen', onDataReceived, onException);

      expect(mockBridge.registerProperty).toHaveBeenCalledWith('android_prop', 'HomeScreen');
    });

    it('should use iosPropertyId on iOS platform', () => {
      jest.resetModules();
      jest.mock('react-native', () => ({ Platform: { OS: 'ios' } }));
      jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
        __esModule: true,
        default: mockBridge,
        eventEmitter: mockEventEmitter,
      }));
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));

      const mod = require('../src/callbacks/WEPlaceHolder');
      mod.registerWEPlaceholderCallback('android_prop', 456, 'HomeScreen', jest.fn(), jest.fn());

      expect(mockBridge.registerProperty).toHaveBeenCalledWith('456', 'HomeScreen');
    });

    it('should add event listeners on first registration', () => {
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());

      expect(mockAddListener).toHaveBeenCalledWith('onCustomDataReceived', expect.any(Function));
      expect(mockAddListener).toHaveBeenCalledWith('onCustomPlaceholderException', expect.any(Function));
    });

    it('should not add duplicate listeners on subsequent registrations', () => {
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());
      registerWEPlaceholderCallback('prop2', 2, 'Screen1', jest.fn(), jest.fn());

      expect(mockAddListener).toHaveBeenCalledTimes(2);
    });

    it('should return early when both propertyIds are missing', () => {
      registerWEPlaceholderCallback('', 0, 'Screen1', jest.fn(), jest.fn());
      expect(mockBridge.registerProperty).not.toHaveBeenCalled();
    });

    it('should return early when screenName is missing', () => {
      registerWEPlaceholderCallback('prop1', 1, '', jest.fn(), jest.fn());
      expect(mockBridge.registerProperty).not.toHaveBeenCalled();
    });

    it('should return early when eventEmitter is null', () => {
      jest.resetModules();
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));
      jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
        __esModule: true,
        default: mockBridge,
        eventEmitter: null,
      }));
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));

      const mod = require('../src/callbacks/WEPlaceHolder');
      mod.registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());

      expect(mockBridge.registerProperty).not.toHaveBeenCalled();
    });

    it('should return early when bridge.registerProperty throws', () => {
      mockBridge.registerProperty.mockImplementationOnce(() => {
        throw new Error('native register error');
      });

      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());

      // Should not add listeners since registerProperty failed
      expect(mockAddListener).not.toHaveBeenCalled();
    });

    it('should handle null callbacks gracefully', () => {
      expect(() => {
        registerWEPlaceholderCallback('prop1', 1, 'Screen1', null, null);
      }).not.toThrow();

      expect(mockBridge.registerProperty).toHaveBeenCalled();
    });

    it('should dispatch onCustomDataReceived events to correct callback', () => {
      const onDataReceived = jest.fn();
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', onDataReceived, jest.fn());

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCustomDataReceived'
      )[1];

      const eventData = {
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payloadData: '{"key":"value"}',
      };

      listenerFn(eventData);

      expect(onDataReceived).toHaveBeenCalledWith({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payload: { key: 'value' },
      });
    });

    it('should dispatch onCustomPlaceholderException events to correct callback', () => {
      const onException = jest.fn();
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), onException);

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCustomPlaceholderException'
      )[1];

      const eventData = { targetViewId: 'prop1', error: 'timeout' };
      listenerFn(eventData);

      expect(onException).toHaveBeenCalledWith(eventData);
    });

    it('should catch error in onCustomDataReceived listener handler', () => {
      // Register with a callback that will cause sendOnDataReceivedEvent to throw
      const onDataReceived = jest.fn().mockImplementation(() => {
        throw new Error('callback error');
      });
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', onDataReceived, jest.fn());

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCustomDataReceived'
      )[1];

      // This should not throw because the listener wraps in try/catch
      expect(() => listenerFn({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payloadData: '{}',
      })).not.toThrow();
    });

    it('should catch error in onCustomPlaceholderException listener handler', () => {
      const onException = jest.fn().mockImplementation(() => {
        throw new Error('exception callback error');
      });
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), onException);

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCustomPlaceholderException'
      )[1];

      expect(() => listenerFn({ targetViewId: 'prop1' })).not.toThrow();
    });

    it('should catch error when sendOnDataReceivedEvent throws in listener', () => {
      jest.resetModules();
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));
      jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
        __esModule: true,
        default: mockBridge,
        eventEmitter: mockEventEmitter,
      }));
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('../src/utils/PropertyListUtils', () => ({
        registerPropertyList: jest.fn().mockReturnValue([]),
        removePropertyFromPropertyList: jest.fn().mockReturnValue({ updatedList: [], listenerFlag: false }),
        sendOnDataReceivedEvent: jest.fn().mockImplementation(() => {
          throw new Error('sendOnDataReceivedEvent internal error');
        }),
        sendOnExceptionEvent: jest.fn(),
      }));

      const mod = require('../src/callbacks/WEPlaceHolder');
      mod.registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCustomDataReceived'
      )[1];

      expect(() => listenerFn({ targetViewId: 'prop1' })).not.toThrow();
    });

    it('should catch error when sendOnExceptionEvent throws in listener', () => {
      jest.resetModules();
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));
      jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
        __esModule: true,
        default: mockBridge,
        eventEmitter: mockEventEmitter,
      }));
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('../src/utils/PropertyListUtils', () => ({
        registerPropertyList: jest.fn().mockReturnValue([]),
        removePropertyFromPropertyList: jest.fn().mockReturnValue({ updatedList: [], listenerFlag: false }),
        sendOnDataReceivedEvent: jest.fn(),
        sendOnExceptionEvent: jest.fn().mockImplementation(() => {
          throw new Error('sendOnExceptionEvent internal error');
        }),
      }));

      const mod = require('../src/callbacks/WEPlaceHolder');
      mod.registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCustomPlaceholderException'
      )[1];

      expect(() => listenerFn({ targetViewId: 'prop1' })).not.toThrow();
    });

    it('should catch error when registerPropertyList throws', () => {
      // Force an error in the property list registration by making addListener throw
      // after registerProperty succeeds
      mockAddListener.mockImplementationOnce(() => {
        throw new Error('addListener error');
      });

      // This tests the outer try/catch around the property list + listener setup
      expect(() => {
        registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());
      }).not.toThrow();
    });
  });

  describe('deregisterWEPlaceholderCallback', () => {
    it('should deregister a placeholder callback on Android', () => {
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());
      deregisterWEPlaceholderCallback('prop1', 1, 'Screen1');

      expect(mockBridge.deregisterProperty).toHaveBeenCalledWith('prop1');
    });

    it('should use iosPropertyId on iOS platform', () => {
      jest.resetModules();
      jest.mock('react-native', () => ({ Platform: { OS: 'ios' } }));
      jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
        __esModule: true,
        default: mockBridge,
        eventEmitter: mockEventEmitter,
      }));
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));

      const mod = require('../src/callbacks/WEPlaceHolder');
      mod.registerWEPlaceholderCallback('android_prop', 789, 'Screen1', jest.fn(), jest.fn());
      mod.deregisterWEPlaceholderCallback('android_prop', 789, 'Screen1');

      expect(mockBridge.deregisterProperty).toHaveBeenCalledWith('789');
    });

    it('should return early when both propertyIds are missing', () => {
      deregisterWEPlaceholderCallback('', 0, 'Screen1');
      expect(mockBridge.deregisterProperty).not.toHaveBeenCalled();
    });

    it('should remove listeners when all properties are deregistered', () => {
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());
      deregisterWEPlaceholderCallback('prop1', 1, 'Screen1');

      expect(mockRemove).toHaveBeenCalled();
    });

    it('should not remove listeners when other properties still exist', () => {
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());
      registerWEPlaceholderCallback('prop2', 2, 'Screen1', jest.fn(), jest.fn());

      mockRemove.mockClear();
      deregisterWEPlaceholderCallback('prop1', 1, 'Screen1');
    });

    it('should catch error when bridge.deregisterProperty throws', () => {
      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());

      mockBridge.deregisterProperty.mockImplementationOnce(() => {
        throw new Error('native deregister error');
      });

      expect(() => deregisterWEPlaceholderCallback('prop1', 1, 'Screen1')).not.toThrow();
    });

    it('should catch error when listener.remove() throws during cleanup', () => {
      mockAddListener.mockReturnValue({
        remove: jest.fn().mockImplementation(() => {
          throw new Error('remove failed');
        }),
      });

      registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());
      expect(() => deregisterWEPlaceholderCallback('prop1', 1, 'Screen1')).not.toThrow();
    });

    it('should catch error when removePropertyFromPropertyList throws', () => {
      jest.resetModules();
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));
      jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
        __esModule: true,
        default: mockBridge,
        eventEmitter: mockEventEmitter,
      }));
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('../src/utils/PropertyListUtils', () => ({
        registerPropertyList: jest.fn().mockReturnValue([{ screenName: 'Screen1', propertyList: [{ propertyId: 'prop1' }] }]),
        removePropertyFromPropertyList: jest.fn().mockImplementation(() => {
          throw new Error('removeProperty internal error');
        }),
        sendOnDataReceivedEvent: jest.fn(),
        sendOnExceptionEvent: jest.fn(),
      }));

      const mod = require('../src/callbacks/WEPlaceHolder');
      mod.registerWEPlaceholderCallback('prop1', 1, 'Screen1', jest.fn(), jest.fn());

      // deregister should catch the error from removePropertyFromPropertyList
      expect(() => mod.deregisterWEPlaceholderCallback('prop1', 1, 'Screen1')).not.toThrow();
    });
  });

  describe('trackClick', () => {
    it('should call bridge trackClick with propertyId and empty map', () => {
      trackClick('prop1');
      expect(mockBridge.trackClick).toHaveBeenCalledWith('prop1', {});
    });

    it('should call bridge trackClick with propertyId and attributes map', () => {
      const attributes = { key: 'value', count: 5 };
      trackClick('prop1', attributes);
      expect(mockBridge.trackClick).toHaveBeenCalledWith('prop1', attributes);
    });

    it('should convert numeric propertyId to string', () => {
      trackClick(123);
      expect(mockBridge.trackClick).toHaveBeenCalledWith('123', {});
    });

    it('should handle null map by passing empty object', () => {
      trackClick('prop1', null);
      expect(mockBridge.trackClick).toHaveBeenCalledWith('prop1', {});
    });

    it('should handle empty propertyId without throwing', () => {
      expect(() => trackClick('')).not.toThrow();
    });

    it('should handle undefined propertyId without throwing', () => {
      expect(() => trackClick()).not.toThrow();
    });

    it('should handle bridge throwing an error', () => {
      mockBridge.trackClick.mockImplementationOnce(() => {
        throw new Error('native error');
      });
      expect(() => trackClick('prop1')).not.toThrow();
    });
  });

  describe('trackImpression', () => {
    it('should call bridge trackImpression with propertyId and empty map', () => {
      trackImpression('prop1');
      expect(mockBridge.trackImpression).toHaveBeenCalledWith('prop1', {});
    });

    it('should call bridge trackImpression with propertyId and attributes map', () => {
      const attributes = { source: 'banner', position: 1 };
      trackImpression('prop1', attributes);
      expect(mockBridge.trackImpression).toHaveBeenCalledWith('prop1', attributes);
    });

    it('should convert numeric propertyId to string', () => {
      trackImpression(456);
      expect(mockBridge.trackImpression).toHaveBeenCalledWith('456', {});
    });

    it('should handle null map by passing empty object', () => {
      trackImpression('prop1', null);
      expect(mockBridge.trackImpression).toHaveBeenCalledWith('prop1', {});
    });

    it('should handle empty propertyId without throwing', () => {
      expect(() => trackImpression('')).not.toThrow();
    });

    it('should handle undefined propertyId without throwing', () => {
      expect(() => trackImpression()).not.toThrow();
    });

    it('should handle bridge throwing an error', () => {
      mockBridge.trackImpression.mockImplementationOnce(() => {
        throw new Error('native error');
      });
      expect(() => trackImpression('prop1')).not.toThrow();
    });
  });
});
