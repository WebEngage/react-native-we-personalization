/**
 * Tests for WECampaignData - registerWECampaignCallback & deregisterWECampaignCallback
 */

const mockRemove = jest.fn();
const mockAddListener = jest.fn().mockReturnValue({ remove: mockRemove });
const mockEventEmitter = { addListener: mockAddListener };

const mockBridge = {
  registerWECampaignCallback: jest.fn(),
  deregisterWECampaignCallback: jest.fn(),
};

jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
  __esModule: true,
  default: mockBridge,
  eventEmitter: mockEventEmitter,
}));

jest.mock('../src/utils/weLogs', () => ({
  weLogs: jest.fn(),
}));

describe('WECampaignData', () => {
  let registerWECampaignCallback;
  let deregisterWECampaignCallback;

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

    const module = require('../src/callbacks/WECampaignData');
    registerWECampaignCallback = module.registerWECampaignCallback;
    deregisterWECampaignCallback = module.deregisterWECampaignCallback;
  });

  describe('registerWECampaignCallback', () => {
    it('should register all campaign callbacks', () => {
      const callbacks = {
        onCampaignPrepared: jest.fn(),
        onCampaignShown: jest.fn(),
        onCampaignClicked: jest.fn(),
        onCampaignException: jest.fn(),
      };

      registerWECampaignCallback(callbacks);

      expect(mockBridge.registerWECampaignCallback).toHaveBeenCalled();
      expect(mockAddListener).toHaveBeenCalledTimes(4);
      expect(mockAddListener).toHaveBeenCalledWith('onCampaignPrepared', expect.any(Function));
      expect(mockAddListener).toHaveBeenCalledWith('onCampaignClicked', expect.any(Function));
      expect(mockAddListener).toHaveBeenCalledWith('onCampaignException', expect.any(Function));
      expect(mockAddListener).toHaveBeenCalledWith('onCampaignShown', expect.any(Function));
    });

    it('should only register provided callbacks', () => {
      const callbacks = { onCampaignPrepared: jest.fn() };
      registerWECampaignCallback(callbacks);

      expect(mockBridge.registerWECampaignCallback).toHaveBeenCalled();
      expect(mockAddListener).toHaveBeenCalledTimes(1);
      expect(mockAddListener).toHaveBeenCalledWith('onCampaignPrepared', expect.any(Function));
    });

    it('should not register if callback is not a function', () => {
      const callbacks = {
        onCampaignPrepared: 'not a function',
        onCampaignClicked: 123,
        onCampaignException: null,
        onCampaignShown: undefined,
      };

      registerWECampaignCallback(callbacks);
      expect(mockBridge.registerWECampaignCallback).toHaveBeenCalled();
      expect(mockAddListener).not.toHaveBeenCalled();
    });

    it('should not register if already registered', () => {
      const callbacks = { onCampaignPrepared: jest.fn() };
      registerWECampaignCallback(callbacks);
      registerWECampaignCallback(callbacks);
      expect(mockBridge.registerWECampaignCallback).toHaveBeenCalledTimes(1);
    });

    it('should handle null callback list', () => {
      expect(() => registerWECampaignCallback(null)).not.toThrow();
      expect(mockBridge.registerWECampaignCallback).not.toHaveBeenCalled();
    });

    it('should handle undefined callback list (defaults to empty object)', () => {
      expect(() => registerWECampaignCallback(undefined)).not.toThrow();
      expect(mockAddListener).not.toHaveBeenCalled();
    });

    it('should handle non-object callback list', () => {
      expect(() => registerWECampaignCallback('invalid')).not.toThrow();
      expect(mockBridge.registerWECampaignCallback).not.toHaveBeenCalled();
    });

    it('should handle empty callback list', () => {
      registerWECampaignCallback({});
      expect(mockBridge.registerWECampaignCallback).toHaveBeenCalled();
      expect(mockAddListener).not.toHaveBeenCalled();
    });

    it('should invoke onCampaignPrepared callback when event fires', () => {
      const onCampaignPrepared = jest.fn();
      registerWECampaignCallback({ onCampaignPrepared });

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCampaignPrepared'
      )[1];

      const eventData = { campaignId: 'test123' };
      listenerFn(eventData);
      expect(onCampaignPrepared).toHaveBeenCalledWith(eventData);
    });

    it('should invoke onCampaignClicked callback when event fires', () => {
      const onCampaignClicked = jest.fn();
      registerWECampaignCallback({ onCampaignClicked });

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCampaignClicked'
      )[1];

      const eventData = { campaignId: 'test123', deepLink: 'app://page' };
      listenerFn(eventData);
      expect(onCampaignClicked).toHaveBeenCalledWith(eventData);
    });

    it('should invoke onCampaignException callback when event fires', () => {
      const onCampaignException = jest.fn();
      registerWECampaignCallback({ onCampaignException });

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCampaignException'
      )[1];

      const eventData = { error: 'timeout' };
      listenerFn(eventData);
      expect(onCampaignException).toHaveBeenCalledWith(eventData);
    });

    it('should invoke onCampaignShown callback when event fires', () => {
      const onCampaignShown = jest.fn();
      registerWECampaignCallback({ onCampaignShown });

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCampaignShown'
      )[1];

      const eventData = { campaignId: 'shown123' };
      listenerFn(eventData);
      expect(onCampaignShown).toHaveBeenCalledWith(eventData);
    });

    it('should catch onCampaignPrepared callback error without crashing', () => {
      const onCampaignPrepared = jest.fn().mockImplementation(() => {
        throw new Error('user callback error');
      });
      registerWECampaignCallback({ onCampaignPrepared });

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCampaignPrepared'
      )[1];

      expect(() => listenerFn({ campaignId: 'test' })).not.toThrow();
    });

    it('should catch onCampaignClicked callback error without crashing', () => {
      const onCampaignClicked = jest.fn().mockImplementation(() => {
        throw new Error('click error');
      });
      registerWECampaignCallback({ onCampaignClicked });

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCampaignClicked'
      )[1];

      expect(() => listenerFn({ campaignId: 'test' })).not.toThrow();
    });

    it('should catch onCampaignException callback error without crashing', () => {
      const onCampaignException = jest.fn().mockImplementation(() => {
        throw new Error('exception error');
      });
      registerWECampaignCallback({ onCampaignException });

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCampaignException'
      )[1];

      expect(() => listenerFn({ error: 'test' })).not.toThrow();
    });

    it('should catch onCampaignShown callback error without crashing', () => {
      const onCampaignShown = jest.fn().mockImplementation(() => {
        throw new Error('shown error');
      });
      registerWECampaignCallback({ onCampaignShown });

      const listenerFn = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onCampaignShown'
      )[1];

      expect(() => listenerFn({ campaignId: 'test' })).not.toThrow();
    });

    it('should catch error when bridge.registerWECampaignCallback throws', () => {
      mockBridge.registerWECampaignCallback.mockImplementationOnce(() => {
        throw new Error('bridge error');
      });

      expect(() => registerWECampaignCallback({ onCampaignPrepared: jest.fn() })).not.toThrow();
    });
  });

  describe('registerWECampaignCallback with null eventEmitter', () => {
    it('should return early when eventEmitter is null', () => {
      jest.resetModules();
      jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
        __esModule: true,
        default: mockBridge,
        eventEmitter: null,
      }));
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));

      const mod = require('../src/callbacks/WECampaignData');
      mod.registerWECampaignCallback({ onCampaignPrepared: jest.fn() });

      expect(mockBridge.registerWECampaignCallback).not.toHaveBeenCalled();
    });
  });

  describe('deregisterWECampaignCallback', () => {
    it('should deregister all campaign callbacks', () => {
      const callbacks = {
        onCampaignPrepared: jest.fn(),
        onCampaignShown: jest.fn(),
        onCampaignClicked: jest.fn(),
        onCampaignException: jest.fn(),
      };

      registerWECampaignCallback(callbacks);
      deregisterWECampaignCallback();

      expect(mockBridge.deregisterWECampaignCallback).toHaveBeenCalled();
      expect(mockRemove).toHaveBeenCalledTimes(4);
    });

    it('should do nothing if not registered', () => {
      deregisterWECampaignCallback();
      expect(mockBridge.deregisterWECampaignCallback).not.toHaveBeenCalled();
      expect(mockRemove).not.toHaveBeenCalled();
    });

    it('should allow re-registration after deregister', () => {
      const callbacks = { onCampaignPrepared: jest.fn() };
      registerWECampaignCallback(callbacks);
      deregisterWECampaignCallback();
      jest.clearAllMocks();
      registerWECampaignCallback(callbacks);
      expect(mockBridge.registerWECampaignCallback).toHaveBeenCalledTimes(1);
    });

    it('should handle listener.remove() throwing an error', () => {
      const throwingRemove = jest.fn().mockImplementation(() => {
        throw new Error('remove failed');
      });
      mockAddListener.mockReturnValue({ remove: throwingRemove });

      registerWECampaignCallback({ onCampaignPrepared: jest.fn() });
      expect(() => deregisterWECampaignCallback()).not.toThrow();
    });

    it('should catch error when bridge.deregisterWECampaignCallback throws', () => {
      registerWECampaignCallback({ onCampaignPrepared: jest.fn() });

      mockBridge.deregisterWECampaignCallback.mockImplementationOnce(() => {
        throw new Error('bridge deregister error');
      });

      expect(() => deregisterWECampaignCallback()).not.toThrow();
    });
  });
});
