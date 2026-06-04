/**
 * Tests for the main index.js exports
 * Verifies all public API exports are correctly exposed
 */

jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
  __esModule: true,
  default: {
    initWePersonalization: jest.fn(),
    registerWECampaignCallback: jest.fn(),
    deregisterWECampaignCallback: jest.fn(),
    registerProperty: jest.fn(),
    deregisterProperty: jest.fn(),
    trackClick: jest.fn(),
    trackImpression: jest.fn(),
  },
  eventEmitter: {
    addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
    removeAllListeners: jest.fn(),
  },
  initWePersonalization: jest.fn(),
  WebengagePersonalizationView: 'WebengagePersonalizationView',
}));

jest.mock('react-native', () => ({
  Platform: { OS: 'android' },
  NativeModules: {},
  NativeEventEmitter: jest.fn().mockImplementation(() => ({
    addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
  })),
  UIManager: { getViewManagerConfig: jest.fn() },
  requireNativeComponent: jest.fn().mockReturnValue('MockView'),
}));

jest.mock('../src/utils/weLogs', () => ({
  weLogs: jest.fn(),
  enableDevMode: jest.fn(),
}));

describe('index.js exports', () => {
  let indexModule;

  beforeEach(() => {
    jest.resetModules();

    jest.mock('../src/bridge/WEPersonalizationBridge', () => ({
      __esModule: true,
      default: {
        initWePersonalization: jest.fn(),
        registerWECampaignCallback: jest.fn(),
        deregisterWECampaignCallback: jest.fn(),
        registerProperty: jest.fn(),
        deregisterProperty: jest.fn(),
        trackClick: jest.fn(),
        trackImpression: jest.fn(),
      },
      eventEmitter: {
        addListener: jest.fn().mockReturnValue({ remove: jest.fn() }),
        removeAllListeners: jest.fn(),
      },
      initWePersonalization: jest.fn(),
      WebengagePersonalizationView: 'WebengagePersonalizationView',
    }));

    jest.mock('react-native', () => ({
      Platform: { OS: 'android' },
    }));

    jest.mock('../src/utils/weLogs', () => ({
      weLogs: jest.fn(),
      enableDevMode: jest.fn(),
    }));

    indexModule = require('../src/index');
  });

  it('should export WEInlineWidget', () => {
    expect(indexModule.WEInlineWidget).toBeDefined();
  });

  it('should export initWePersonalization', () => {
    expect(indexModule.initWePersonalization).toBeDefined();
    expect(typeof indexModule.initWePersonalization).toBe('function');
  });

  it('should export enableDevMode', () => {
    expect(indexModule.enableDevMode).toBeDefined();
    expect(typeof indexModule.enableDevMode).toBe('function');
  });

  it('should export registerWECampaignCallback', () => {
    expect(indexModule.registerWECampaignCallback).toBeDefined();
    expect(typeof indexModule.registerWECampaignCallback).toBe('function');
  });

  it('should export deregisterWECampaignCallback', () => {
    expect(indexModule.deregisterWECampaignCallback).toBeDefined();
    expect(typeof indexModule.deregisterWECampaignCallback).toBe('function');
  });

  it('should export registerWEPlaceholderCallback', () => {
    expect(indexModule.registerWEPlaceholderCallback).toBeDefined();
    expect(typeof indexModule.registerWEPlaceholderCallback).toBe('function');
  });

  it('should export deregisterWEPlaceholderCallback', () => {
    expect(indexModule.deregisterWEPlaceholderCallback).toBeDefined();
    expect(typeof indexModule.deregisterWEPlaceholderCallback).toBe('function');
  });

  it('should export trackClick', () => {
    expect(indexModule.trackClick).toBeDefined();
    expect(typeof indexModule.trackClick).toBe('function');
  });

  it('should export trackImpression', () => {
    expect(indexModule.trackImpression).toBeDefined();
    expect(typeof indexModule.trackImpression).toBe('function');
  });

  it('should not export any unexpected properties', () => {
    const expectedExports = [
      'WEInlineWidget',
      'initWePersonalization',
      'enableDevMode',
      'registerWECampaignCallback',
      'deregisterWECampaignCallback',
      'registerWEPlaceholderCallback',
      'deregisterWEPlaceholderCallback',
      'trackClick',
      'trackImpression',
    ];

    const actualExports = Object.keys(indexModule).filter(
      (key) => key !== '__esModule'
    );

    expect(actualExports.sort()).toEqual(expectedExports.sort());
  });
});
