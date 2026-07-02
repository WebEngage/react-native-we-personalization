/**
 * Tests for WEInlineWidget component
 */

const mockRemove = jest.fn();
const mockAddListener = jest.fn().mockReturnValue({ remove: mockRemove });
const mockEventEmitter = { addListener: mockAddListener };

jest.mock('../src/bridge/WEPersonalizationBridge', () => {
  const React = require('react');
  return {
    __esModule: true,
    default: {
      registerProperty: jest.fn(),
      deregisterProperty: jest.fn(),
    },
    eventEmitter: mockEventEmitter,
    WebengagePersonalizationView: (props) => React.createElement('WebengagePersonalizationView', props),
  };
});

jest.mock('../src/utils/weLogs', () => ({
  weLogs: jest.fn(),
}));

jest.mock('react-native', () => ({
  Platform: { OS: 'android' },
}));

// Suppress prop-types warnings in tests
beforeAll(() => {
  jest.spyOn(console, 'error').mockImplementation(() => {});
});

afterAll(() => {
  jest.restoreAllMocks();
});

describe('WEInlineWidget', () => {
  let React;
  let create;
  let act;
  let WEInlineWidget;

  beforeEach(() => {
    jest.clearAllMocks();
    jest.resetModules();

    React = require('react');
    const renderer = require('react-test-renderer');
    create = renderer.create;
    act = renderer.act;
    WEInlineWidget = require('../src/view/WEInlineWidget').default;
  });

  describe('Props validation', () => {
    it('should export a memoized component', () => {
      expect(WEInlineWidget).toBeDefined();
      expect(WEInlineWidget.$$typeof).toBeDefined();
    });

    it('should have correct propTypes defined', () => {
      const innerComponent = WEInlineWidget.type || WEInlineWidget;
      if (innerComponent.propTypes) {
        expect(innerComponent.propTypes.screenName).toBeDefined();
        expect(innerComponent.propTypes.androidPropertyId).toBeDefined();
        expect(innerComponent.propTypes.iosPropertyId).toBeDefined();
        expect(innerComponent.propTypes.onRendered).toBeDefined();
        expect(innerComponent.propTypes.onDataReceived).toBeDefined();
        expect(innerComponent.propTypes.onPlaceholderException).toBeDefined();
        expect(innerComponent.propTypes.style).toBeDefined();
      }
    });
  });

  describe('Rendering', () => {
    it('should return null when screenName is empty', () => {
      let tree;
      act(() => {
        tree = create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: '',
          })
        );
      });

      expect(tree.toJSON()).toBeNull();
    });

    it('should render WebengagePersonalizationView with correct props on Android', () => {
      const onDataReceived = jest.fn();
      const onRendered = jest.fn();
      const style = { height: 200 };

      let tree;
      act(() => {
        tree = create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'android_prop_1',
            iosPropertyId: 100,
            screenName: 'HomeScreen',
            onDataReceived,
            onRendered,
            style,
          })
        );
      });

      const json = tree.toJSON();
      expect(json).not.toBeNull();
      expect(json.type).toBe('WebengagePersonalizationView');
      expect(json.props.propertyId).toBe('android_prop_1');
      expect(json.props.screenName).toBe('HomeScreen');
      expect(json.props.style).toEqual(style);
    });

    it('should register event listeners on mount', () => {
      act(() => {
        create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
          })
        );
      });

      expect(mockAddListener).toHaveBeenCalledWith('onDataReceived', expect.any(Function));
      expect(mockAddListener).toHaveBeenCalledWith('onRendered', expect.any(Function));
      expect(mockAddListener).toHaveBeenCalledWith('onPlaceholderException', expect.any(Function));
    });

    it('should handle default prop values', () => {
      let tree;
      act(() => {
        tree = create(
          React.createElement(WEInlineWidget, {
            screenName: 'TestScreen',
          })
        );
      });

      const json = tree.toJSON();
      expect(json).not.toBeNull();
      expect(json.props.propertyId).toBe('');
      expect(json.props.screenName).toBe('TestScreen');
    });
  });

  describe('Event handling', () => {
    it('should dispatch onDataReceived events to the correct property callback', () => {
      const onDataReceived = jest.fn();

      act(() => {
        create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
            onDataReceived,
          })
        );
      });

      const listenerCall = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onDataReceived'
      );

      expect(listenerCall).toBeDefined();
      const listenerFn = listenerCall[1];

      listenerFn({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payloadData: '{"content":"hello"}',
      });

      expect(onDataReceived).toHaveBeenCalledWith({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payload: { content: 'hello' },
      });
    });

    it('should dispatch onRendered events to the correct property callback', () => {
      const onRendered = jest.fn();

      act(() => {
        create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
            onRendered,
          })
        );
      });

      const listenerCall = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onRendered'
      );

      expect(listenerCall).toBeDefined();
      const listenerFn = listenerCall[1];

      listenerFn({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payloadData: '{}',
      });

      expect(onRendered).toHaveBeenCalledWith({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payload: {},
      });
    });

    it('should dispatch onPlaceholderException events to the correct property callback', () => {
      const onPlaceholderException = jest.fn();

      act(() => {
        create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
            onPlaceholderException,
          })
        );
      });

      const listenerCall = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onPlaceholderException'
      );

      expect(listenerCall).toBeDefined();
      const listenerFn = listenerCall[1];

      listenerFn({ targetViewId: 'prop1', error: 'timeout' });

      expect(onPlaceholderException).toHaveBeenCalledWith({
        targetViewId: 'prop1',
        error: 'timeout',
      });
    });

    it('should catch error in onDataReceived handler without crashing', () => {
      const onDataReceived = jest.fn().mockImplementation(() => {
        throw new Error('user error');
      });

      act(() => {
        create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
            onDataReceived,
          })
        );
      });

      const listenerCall = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onDataReceived'
      );

      expect(() => {
        listenerCall[1]({
          targetViewId: 'prop1',
          campaignId: 'camp1',
          payloadData: '{}',
        });
      }).not.toThrow();
    });

    it('should catch error in onRendered handler without crashing', () => {
      const onRendered = jest.fn().mockImplementation(() => {
        throw new Error('render error');
      });

      act(() => {
        create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
            onRendered,
          })
        );
      });

      const listenerCall = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onRendered'
      );

      expect(() => {
        listenerCall[1]({
          targetViewId: 'prop1',
          campaignId: 'camp1',
          payloadData: '{}',
        });
      }).not.toThrow();
    });

    it('should catch error in onPlaceholderException handler without crashing', () => {
      const onPlaceholderException = jest.fn().mockImplementation(() => {
        throw new Error('exception error');
      });

      act(() => {
        create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
            onPlaceholderException,
          })
        );
      });

      const listenerCall = mockAddListener.mock.calls.find(
        (call) => call[0] === 'onPlaceholderException'
      );

      expect(() => {
        listenerCall[1]({ targetViewId: 'prop1', error: 'test' });
      }).not.toThrow();
    });
  });

  describe('Cleanup on unmount', () => {
    it('should clean up listeners on unmount', () => {
      let tree;
      act(() => {
        tree = create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
          })
        );
      });

      act(() => {
        tree.unmount();
      });

      expect(mockRemove).toHaveBeenCalled();
    });

    it('should handle listener.remove() throwing during unmount', () => {
      const throwingRemove = jest.fn().mockImplementation(() => {
        throw new Error('remove failed');
      });
      mockAddListener.mockReturnValue({ remove: throwingRemove });

      let tree;
      act(() => {
        tree = create(
          React.createElement(WEInlineWidget, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
          })
        );
      });

      // Should not throw even if remove() fails
      expect(() => {
        act(() => {
          tree.unmount();
        });
      }).not.toThrow();
    });
  });

  describe('eventEmitter null', () => {
    it('should return null when eventEmitter is not available', () => {
      jest.resetModules();
      jest.mock('../src/bridge/WEPersonalizationBridge', () => {
        const React = require('react');
        return {
          __esModule: true,
          default: { registerProperty: jest.fn(), deregisterProperty: jest.fn() },
          eventEmitter: null,
          WebengagePersonalizationView: (props) => React.createElement('WebengagePersonalizationView', props),
        };
      });
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));

      const React2 = require('react');
      const renderer2 = require('react-test-renderer');
      const WEInlineWidget2 = require('../src/view/WEInlineWidget').default;

      let tree;
      renderer2.act(() => {
        tree = renderer2.create(
          React2.createElement(WEInlineWidget2, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
          })
        );
      });

      expect(tree.toJSON()).toBeNull();
    });
  });

  describe('addListener failure', () => {
    it('should catch error when eventEmitter.addListener throws', () => {
      jest.resetModules();

      const mockThrowingEmitter = {
        addListener: jest.fn().mockImplementation(() => {
          throw new Error('addListener failed');
        }),
      };

      jest.mock('../src/bridge/WEPersonalizationBridge', () => {
        const React = require('react');
        return {
          __esModule: true,
          default: { registerProperty: jest.fn(), deregisterProperty: jest.fn() },
          eventEmitter: mockThrowingEmitter,
          WebengagePersonalizationView: (props) => React.createElement('WebengagePersonalizationView', props),
        };
      });
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));

      const React2 = require('react');
      const renderer2 = require('react-test-renderer');
      const WEInlineWidget2 = require('../src/view/WEInlineWidget').default;

      // Should not throw - the component catches the error
      expect(() => {
        renderer2.act(() => {
          renderer2.create(
            React2.createElement(WEInlineWidget2, {
              androidPropertyId: 'prop1',
              iosPropertyId: 1,
              screenName: 'HomeScreen',
            })
          );
        });
      }).not.toThrow();
    });
  });

  describe('iOS platform', () => {
    it('should use iosPropertyId on iOS platform', () => {
      jest.resetModules();
      jest.mock('../src/bridge/WEPersonalizationBridge', () => {
        const React = require('react');
        return {
          __esModule: true,
          default: { registerProperty: jest.fn(), deregisterProperty: jest.fn() },
          eventEmitter: { addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) },
          WebengagePersonalizationView: (props) => React.createElement('WebengagePersonalizationView', props),
        };
      });
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('react-native', () => ({ Platform: { OS: 'ios' } }));

      const React2 = require('react');
      const renderer2 = require('react-test-renderer');
      const WEInlineWidget2 = require('../src/view/WEInlineWidget').default;

      let tree;
      renderer2.act(() => {
        tree = renderer2.create(
          React2.createElement(WEInlineWidget2, {
            androidPropertyId: 'android_prop',
            iosPropertyId: 999,
            screenName: 'HomeScreen',
          })
        );
      });

      const json = tree.toJSON();
      expect(json).not.toBeNull();
      expect(json.props.propertyId).toBe('999');
    });
  });

  describe('Event handler error propagation', () => {
    it('should catch error when sendOnDataReceivedEvent throws unexpectedly', () => {
      jest.resetModules();

      jest.mock('../src/utils/PropertyListUtils', () => ({
        registerPropertyList: jest.fn().mockReturnValue([]),
        removePropertyFromPropertyList: jest.fn().mockReturnValue({ updatedList: [], listenerFlag: false }),
        sendOnDataReceivedEvent: jest.fn().mockImplementation(() => {
          throw new Error('unexpected internal error');
        }),
        sendOnRenderedEvent: jest.fn(),
        sendOnExceptionEvent: jest.fn(),
      }));

      const mockAddListener2 = jest.fn().mockReturnValue({ remove: jest.fn() });
      jest.mock('../src/bridge/WEPersonalizationBridge', () => {
        const React = require('react');
        return {
          __esModule: true,
          default: { registerProperty: jest.fn(), deregisterProperty: jest.fn() },
          eventEmitter: { addListener: mockAddListener2 },
          WebengagePersonalizationView: (props) => React.createElement('WebengagePersonalizationView', props),
        };
      });
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));

      const React2 = require('react');
      const renderer2 = require('react-test-renderer');
      const WEInlineWidget2 = require('../src/view/WEInlineWidget').default;

      renderer2.act(() => {
        renderer2.create(
          React2.createElement(WEInlineWidget2, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
          })
        );
      });

      const listenerCall = mockAddListener2.mock.calls.find((c) => c[0] === 'onDataReceived');
      expect(listenerCall).toBeDefined();
      expect(() => listenerCall[1]({ targetViewId: 'prop1' })).not.toThrow();
    });

    it('should catch error when sendOnRenderedEvent throws unexpectedly', () => {
      jest.resetModules();

      jest.mock('../src/utils/PropertyListUtils', () => ({
        registerPropertyList: jest.fn().mockReturnValue([]),
        removePropertyFromPropertyList: jest.fn().mockReturnValue({ updatedList: [], listenerFlag: false }),
        sendOnDataReceivedEvent: jest.fn(),
        sendOnRenderedEvent: jest.fn().mockImplementation(() => {
          throw new Error('unexpected render error');
        }),
        sendOnExceptionEvent: jest.fn(),
      }));

      const mockAddListener2 = jest.fn().mockReturnValue({ remove: jest.fn() });
      jest.mock('../src/bridge/WEPersonalizationBridge', () => {
        const React = require('react');
        return {
          __esModule: true,
          default: { registerProperty: jest.fn(), deregisterProperty: jest.fn() },
          eventEmitter: { addListener: mockAddListener2 },
          WebengagePersonalizationView: (props) => React.createElement('WebengagePersonalizationView', props),
        };
      });
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));

      const React2 = require('react');
      const renderer2 = require('react-test-renderer');
      const WEInlineWidget2 = require('../src/view/WEInlineWidget').default;

      renderer2.act(() => {
        renderer2.create(
          React2.createElement(WEInlineWidget2, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
          })
        );
      });

      const listenerCall = mockAddListener2.mock.calls.find((c) => c[0] === 'onRendered');
      expect(listenerCall).toBeDefined();
      expect(() => listenerCall[1]({ targetViewId: 'prop1' })).not.toThrow();
    });

    it('should catch error when sendOnExceptionEvent throws unexpectedly', () => {
      jest.resetModules();

      jest.mock('../src/utils/PropertyListUtils', () => ({
        registerPropertyList: jest.fn().mockReturnValue([]),
        removePropertyFromPropertyList: jest.fn().mockReturnValue({ updatedList: [], listenerFlag: false }),
        sendOnDataReceivedEvent: jest.fn(),
        sendOnRenderedEvent: jest.fn(),
        sendOnExceptionEvent: jest.fn().mockImplementation(() => {
          throw new Error('unexpected exception error');
        }),
      }));

      const mockAddListener2 = jest.fn().mockReturnValue({ remove: jest.fn() });
      jest.mock('../src/bridge/WEPersonalizationBridge', () => {
        const React = require('react');
        return {
          __esModule: true,
          default: { registerProperty: jest.fn(), deregisterProperty: jest.fn() },
          eventEmitter: { addListener: mockAddListener2 },
          WebengagePersonalizationView: (props) => React.createElement('WebengagePersonalizationView', props),
        };
      });
      jest.mock('../src/utils/weLogs', () => ({ weLogs: jest.fn() }));
      jest.mock('react-native', () => ({ Platform: { OS: 'android' } }));

      const React2 = require('react');
      const renderer2 = require('react-test-renderer');
      const WEInlineWidget2 = require('../src/view/WEInlineWidget').default;

      renderer2.act(() => {
        renderer2.create(
          React2.createElement(WEInlineWidget2, {
            androidPropertyId: 'prop1',
            iosPropertyId: 1,
            screenName: 'HomeScreen',
          })
        );
      });

      const listenerCall = mockAddListener2.mock.calls.find((c) => c[0] === 'onPlaceholderException');
      expect(listenerCall).toBeDefined();
      expect(() => listenerCall[1]({ targetViewId: 'prop1' })).not.toThrow();
    });
  });
});
