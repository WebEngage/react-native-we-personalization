/**
 * Tests for WEGConstants
 */

jest.mock('react-native', () => ({
  Platform: {
    select: (obj) => obj.ios || obj.default || '',
  },
}));

describe('WEGConstants', () => {
  let COMPONENT_NAME;
  let LINKING_ERROR;

  beforeEach(() => {
    jest.resetModules();
    jest.mock('react-native', () => ({
      Platform: {
        select: (obj) => obj.ios || obj.default || '',
      },
    }));

    const constants = require('../src/utils/WEGConstants');
    COMPONENT_NAME = constants.COMPONENT_NAME;
    LINKING_ERROR = constants.LINKING_ERROR;
  });

  it('should export COMPONENT_NAME as WEPersonalizationView', () => {
    expect(COMPONENT_NAME).toBe('WEPersonalizationView');
  });

  it('should export LINKING_ERROR with helpful message', () => {
    expect(LINKING_ERROR).toContain("react-native-we-personalization");
    expect(LINKING_ERROR).toContain("doesn't seem to be linked");
  });

  it('should include pod install instruction for iOS', () => {
    jest.resetModules();
    jest.mock('react-native', () => ({
      Platform: {
        select: (obj) => obj.ios || obj.default || '',
      },
    }));

    const { LINKING_ERROR: error } = require('../src/utils/WEGConstants');
    expect(error).toContain('pod install');
  });

  it('should include rebuild instruction', () => {
    expect(LINKING_ERROR).toContain('rebuilt the app');
  });

  it('should include Expo Go warning', () => {
    expect(LINKING_ERROR).toContain('Expo Go');
  });
});
