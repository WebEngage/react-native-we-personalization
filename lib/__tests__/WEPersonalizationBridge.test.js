/**
 * Tests for WEPersonalizationBridge - initWePersonalization, architecture detection
 */

// Mock WEGConstants to avoid Platform.select issue during module load
jest.mock('../src/utils/WEGConstants', () => ({
  COMPONENT_NAME: 'WEPersonalizationView',
  LINKING_ERROR: 'Library not linked',
}));

describe('WEPersonalizationBridge', () => {
  let originalTurboModuleProxy;
  let originalFabricUIManager;

  beforeEach(() => {
    jest.resetModules();
    jest.spyOn(console, 'log').mockImplementation(() => {});
    jest.spyOn(console, 'error').mockImplementation(() => {});

    originalTurboModuleProxy = global.__turboModuleProxy;
    originalFabricUIManager = global.nativeFabricUIManager;
  });

  afterEach(() => {
    global.__turboModuleProxy = originalTurboModuleProxy;
    global.nativeFabricUIManager = originalFabricUIManager;
    jest.restoreAllMocks();
  });

  describe('Legacy Architecture (no TurboModules)', () => {
    beforeEach(() => {
      delete global.__turboModuleProxy;
      delete global.nativeFabricUIManager;
    });

    it('should use NativeModules.WEPersonalizationBridge', () => {
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: {
            initWePersonalization: jest.fn(),
            addListener: jest.fn(),
            removeListeners: jest.fn(),
          },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({
          addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
          removeAllListeners: jest.fn(),
        })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const bridge = require('../src/bridge/WEPersonalizationBridge');
      expect(bridge.default).toBeDefined();
      expect(bridge.default.initWePersonalization).toBeDefined();
    });

    it('should export initWePersonalization function', () => {
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: {
            initWePersonalization: jest.fn(),
            addListener: jest.fn(),
            removeListeners: jest.fn(),
          },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({
          addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
          removeAllListeners: jest.fn(),
        })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const { initWePersonalization } = require('../src/bridge/WEPersonalizationBridge');
      expect(typeof initWePersonalization).toBe('function');
    });

    it('should call native initWePersonalization', () => {
      const mockInit = jest.fn();
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: {
            initWePersonalization: mockInit,
            addListener: jest.fn(),
            removeListeners: jest.fn(),
          },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({
          addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
          removeAllListeners: jest.fn(),
        })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const { initWePersonalization } = require('../src/bridge/WEPersonalizationBridge');
      initWePersonalization();
      expect(mockInit).toHaveBeenCalled();
    });

    it('should not throw if bridge initWePersonalization throws', () => {
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: {
            initWePersonalization: jest.fn().mockImplementation(() => {
              throw new Error('native error');
            }),
            addListener: jest.fn(),
            removeListeners: jest.fn(),
          },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({
          addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
          removeAllListeners: jest.fn(),
        })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const { initWePersonalization } = require('../src/bridge/WEPersonalizationBridge');
      expect(() => initWePersonalization()).not.toThrow();
    });

    it('should create an event emitter', () => {
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: {
            initWePersonalization: jest.fn(),
            addListener: jest.fn(),
            removeListeners: jest.fn(),
          },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({
          addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
          removeAllListeners: jest.fn(),
        })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const { eventEmitter } = require('../src/bridge/WEPersonalizationBridge');
      expect(eventEmitter).not.toBeNull();
    });

    it('should use requireNativeComponent for view', () => {
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: {
            initWePersonalization: jest.fn(),
            addListener: jest.fn(),
            removeListeners: jest.fn(),
          },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({
          addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
          removeAllListeners: jest.fn(),
        })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({ NativeProps: {} }) },
        requireNativeComponent: jest.fn().mockReturnValue('LegacyView'),
      }));

      const { WebengagePersonalizationView } = require('../src/bridge/WEPersonalizationBridge');
      expect(WebengagePersonalizationView).toBe('LegacyView');
    });
  });

  describe('getArchitectureStatus', () => {
    it('should return turboModule false when not available', () => {
      delete global.__turboModuleProxy;
      delete global.nativeFabricUIManager;
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const { getArchitectureStatus } = require('../src/bridge/WEPersonalizationBridge');
      const status = getArchitectureStatus();
      expect(status.turboModule).toBe(false);
      expect(status.fabric).toBe(false);
    });

    it('should return turboModule true when proxy is available', () => {
      global.__turboModuleProxy = jest.fn();
      delete global.nativeFabricUIManager;
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));
      jest.mock('../src/bridge/NativeWEPersonalizationBridge', () => { throw new Error('not available'); });

      const { getArchitectureStatus } = require('../src/bridge/WEPersonalizationBridge');
      expect(getArchitectureStatus().turboModule).toBe(true);
    });

    it('should return fabric true when nativeFabricUIManager is available', () => {
      delete global.__turboModuleProxy;
      global.nativeFabricUIManager = {};
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));
      jest.mock('../src/bridge/WEPersonalizationViewNativeComponent', () => { throw new Error('not available'); });

      const { getArchitectureStatus } = require('../src/bridge/WEPersonalizationBridge');
      expect(getArchitectureStatus().fabric).toBe(true);
    });
  });

  describe('TurboModule Architecture', () => {
    it('should fall back to NativeModules when TurboModule require fails', () => {
      global.__turboModuleProxy = jest.fn();
      delete global.nativeFabricUIManager;

      jest.mock('../src/bridge/NativeWEPersonalizationBridge', () => { throw new Error('TurboModule not available'); });
      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const bridge = require('../src/bridge/WEPersonalizationBridge');
      expect(bridge.default).toBeDefined();
      expect(bridge.default.initWePersonalization).toBeDefined();
    });

    it('should use TurboModule when available and has initWePersonalization', () => {
      global.__turboModuleProxy = jest.fn();
      delete global.nativeFabricUIManager;

      const mockTurboModule = {
        initWePersonalization: jest.fn(),
        registerWECampaignCallback: jest.fn(),
        addListener: jest.fn(),
        removeListeners: jest.fn(),
      };

      jest.mock('../src/bridge/NativeWEPersonalizationBridge', () => ({
        __esModule: true,
        default: mockTurboModule,
      }));

      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const bridge = require('../src/bridge/WEPersonalizationBridge');
      expect(bridge.default).toBe(mockTurboModule);
    });

    it('should fall back when TurboModule exists but lacks initWePersonalization', () => {
      global.__turboModuleProxy = jest.fn();
      delete global.nativeFabricUIManager;

      // TurboModule exists but doesn't have initWePersonalization as a function
      jest.mock('../src/bridge/NativeWEPersonalizationBridge', () => ({
        __esModule: true,
        default: { someOtherMethod: jest.fn() },
      }));

      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('MockView'),
      }));

      const bridge = require('../src/bridge/WEPersonalizationBridge');
      // Should fall back to NativeModules
      expect(bridge.default.initWePersonalization).toBeDefined();
    });
  });

  describe('Fabric View Component', () => {
    it('should use Fabric view component when nativeFabricUIManager is available', () => {
      delete global.__turboModuleProxy;
      global.nativeFabricUIManager = {};

      const mockFabricView = 'FabricViewComponent';
      jest.mock('../src/bridge/WEPersonalizationViewNativeComponent', () => ({
        __esModule: true,
        default: mockFabricView,
      }));

      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({}) },
        requireNativeComponent: jest.fn().mockReturnValue('LegacyView'),
      }));

      const { WebengagePersonalizationView } = require('../src/bridge/WEPersonalizationBridge');
      expect(WebengagePersonalizationView).toBe(mockFabricView);
    });

    it('should fall back to legacy view when Fabric component fails to load', () => {
      delete global.__turboModuleProxy;
      global.nativeFabricUIManager = {};

      jest.mock('../src/bridge/WEPersonalizationViewNativeComponent', () => { throw new Error('Fabric not available'); });

      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue({ NativeProps: {} }) },
        requireNativeComponent: jest.fn().mockReturnValue('LegacyFallbackView'),
      }));

      const { WebengagePersonalizationView } = require('../src/bridge/WEPersonalizationBridge');
      expect(WebengagePersonalizationView).toBe('LegacyFallbackView');
    });
  });

  describe('View component fallback', () => {
    it('should provide fallback view when native component is not available', () => {
      delete global.__turboModuleProxy;
      delete global.nativeFabricUIManager;

      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue(null) },
        requireNativeComponent: jest.fn(),
      }));

      const { WebengagePersonalizationView } = require('../src/bridge/WEPersonalizationBridge');
      expect(typeof WebengagePersonalizationView).toBe('function');
      expect(WebengagePersonalizationView()).toBeNull();
    });
  });

  describe('Bridge not available', () => {
    it('should throw when native module is not found', () => {
      delete global.__turboModuleProxy;
      delete global.nativeFabricUIManager;

      jest.mock('react-native', () => ({
        NativeModules: {},
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: { getViewManagerConfig: jest.fn().mockReturnValue(null) },
        requireNativeComponent: jest.fn(),
      }));

      expect(() => {
        require('../src/bridge/WEPersonalizationBridge');
      }).toThrow('[WEPersonalization] Native module not found');
    });
  });

  describe('Outer try/catch for view initialization', () => {
    it('should catch error when view initialization throws', () => {
      delete global.__turboModuleProxy;
      delete global.nativeFabricUIManager;

      jest.mock('react-native', () => ({
        NativeModules: {
          WEPersonalizationBridge: { initWePersonalization: jest.fn(), addListener: jest.fn(), removeListeners: jest.fn() },
        },
        NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
        UIManager: {
          getViewManagerConfig: jest.fn().mockImplementation(() => {
            throw new Error('UIManager error');
          }),
        },
        requireNativeComponent: jest.fn(),
      }));

      // Should not throw - the outer try/catch handles it
      const { WebengagePersonalizationView } = require('../src/bridge/WEPersonalizationBridge');
      // Falls back to the error function
      expect(typeof WebengagePersonalizationView).toBe('function');
      expect(WebengagePersonalizationView()).toBeNull();
    });
  });

  describe('Outer try/catch for bridge initialization', () => {
    it('should catch error when bridge initialization throws', () => {
      delete global.__turboModuleProxy;
      delete global.nativeFabricUIManager;

      jest.mock('react-native', () => {
        const obj = {
          NativeModules: {},
          NativeEventEmitter: jest.fn().mockImplementation(() => ({ addListener: jest.fn().mockReturnValue({ remove: jest.fn() }) })),
          UIManager: { getViewManagerConfig: jest.fn().mockReturnValue(null) },
          requireNativeComponent: jest.fn(),
        };
        // Make NativeModules.WEPersonalizationBridge access throw
        Object.defineProperty(obj.NativeModules, 'WEPersonalizationBridge', {
          get: () => { throw new Error('NativeModules access error'); },
        });
        return obj;
      });

      // The module should throw because bridge is null after the catch
      expect(() => {
        require('../src/bridge/WEPersonalizationBridge');
      }).toThrow();
    });
  });
});
