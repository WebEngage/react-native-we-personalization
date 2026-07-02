/**
 * Tests for weLogs utility and enableDevMode
 */

describe('weLogs', () => {
  let weLogs;
  let enableDevMode;

  beforeEach(() => {
    jest.resetModules();
    jest.spyOn(console, 'log').mockImplementation(() => {});
    const utils = require('../src/utils/weLogs');
    weLogs = utils.weLogs;
    enableDevMode = utils.enableDevMode;
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('weLogs', () => {
    it('should not log when dev mode is disabled (default)', () => {
      weLogs('test message');
      // Only the enableDevMode call would log, not weLogs
      expect(console.log).not.toHaveBeenCalledWith('[WE-Inline-JS]', 'test message');
    });

    it('should log when dev mode is enabled', () => {
      enableDevMode();
      weLogs('test message');
      expect(console.log).toHaveBeenCalledWith('[WE-Inline-JS]', 'test message');
    });

    it('should log multiple arguments when dev mode is enabled', () => {
      enableDevMode();
      weLogs('message', { key: 'value' }, 123);
      expect(console.log).toHaveBeenCalledWith('[WE-Inline-JS]', 'message', { key: 'value' }, 123);
    });

    it('should handle no arguments', () => {
      enableDevMode();
      weLogs();
      expect(console.log).toHaveBeenCalledWith('[WE-Inline-JS]');
    });

    it('should handle null and undefined arguments', () => {
      enableDevMode();
      weLogs(null, undefined);
      expect(console.log).toHaveBeenCalledWith('[WE-Inline-JS]', null, undefined);
    });
  });

  describe('enableDevMode', () => {
    it('should enable dev mode and log confirmation', () => {
      enableDevMode();
      expect(console.log).toHaveBeenCalledWith('[WE-Inline-JS] Dev mode enabled');
    });

    it('should persist dev mode across multiple weLogs calls', () => {
      enableDevMode();
      weLogs('first');
      weLogs('second');
      expect(console.log).toHaveBeenCalledWith('[WE-Inline-JS]', 'first');
      expect(console.log).toHaveBeenCalledWith('[WE-Inline-JS]', 'second');
    });

    it('should be safe to call multiple times', () => {
      enableDevMode();
      enableDevMode();
      weLogs('test');
      expect(console.log).toHaveBeenCalledWith('[WE-Inline-JS]', 'test');
    });
  });
});
