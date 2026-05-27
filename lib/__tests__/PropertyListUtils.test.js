/**
 * Tests for PropertyListUtils
 */

describe('PropertyListUtils', () => {
  let registerPropertyList;
  let removePropertyFromPropertyList;
  let getLatestScreenIndex;
  let getPropertyDetails;
  let sendOnDataReceivedEvent;
  let sendOnRenderedEvent;
  let sendOnExceptionEvent;

  beforeEach(() => {
    jest.resetModules();
    jest.spyOn(console, 'log').mockImplementation(() => {});
    jest.spyOn(console, 'error').mockImplementation(() => {});

    const utils = require('../src/utils/PropertyListUtils');
    registerPropertyList = utils.registerPropertyList;
    removePropertyFromPropertyList = utils.removePropertyFromPropertyList;
    getLatestScreenIndex = utils.getLatestScreenIndex;
    getPropertyDetails = utils.getPropertyDetails;
    sendOnDataReceivedEvent = utils.sendOnDataReceivedEvent;
    sendOnRenderedEvent = utils.sendOnRenderedEvent;
    sendOnExceptionEvent = utils.sendOnExceptionEvent;
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('registerPropertyList', () => {
    it('should register a new property for a new screen', () => {
      const onDataReceived = jest.fn();
      const result = registerPropertyList([], 'HomeScreen', 'prop1', onDataReceived, null, null);

      expect(result).toHaveLength(1);
      expect(result[0].screenName).toBe('HomeScreen');
      expect(result[0].propertyList).toHaveLength(1);
      expect(result[0].propertyList[0].propertyId).toBe('prop1');
      expect(result[0].propertyList[0].callbacks.onDataReceived).toBe(onDataReceived);
    });

    it('should add a property to an existing screen', () => {
      const list = [{ screenName: 'HomeScreen', propertyList: [] }];
      const result = registerPropertyList(list, 'HomeScreen', 'prop1', jest.fn(), null, null);

      expect(result).toHaveLength(1);
      expect(result[0].propertyList).toHaveLength(1);
    });

    it('should not add duplicate propertyId to the same screen', () => {
      let list = [];
      list = registerPropertyList(list, 'HomeScreen', 'prop1', jest.fn(), null, null);
      list = registerPropertyList(list, 'HomeScreen', 'prop1', jest.fn(), null, null);

      expect(list[0].propertyList).toHaveLength(1);
    });

    it('should add different properties to the same screen', () => {
      let list = [];
      list = registerPropertyList(list, 'HomeScreen', 'prop1', jest.fn(), null, null);
      list = registerPropertyList(list, 'HomeScreen', 'prop2', jest.fn(), null, null);

      expect(list[0].propertyList).toHaveLength(2);
    });

    it('should handle null list by returning empty array', () => {
      const result = registerPropertyList(null, 'HomeScreen', 'prop1', jest.fn(), null, null);
      expect(result).toEqual([]);
    });

    it('should handle non-array list by returning empty array', () => {
      const result = registerPropertyList('invalid', 'HomeScreen', 'prop1', jest.fn(), null, null);
      expect(result).toEqual([]);
    });

    it('should return list unchanged when screenName is missing', () => {
      const list = [{ screenName: 'Existing', propertyList: [] }];
      const result = registerPropertyList(list, '', 'prop1', jest.fn(), null, null);
      expect(result).toEqual(list);
    });

    it('should return list unchanged when propertyId is missing', () => {
      const list = [{ screenName: 'Existing', propertyList: [] }];
      const result = registerPropertyList(list, 'HomeScreen', '', jest.fn(), null, null);
      expect(result).toEqual(list);
    });

    it('should store all callback types correctly', () => {
      const onDataReceived = jest.fn();
      const onRendered = jest.fn();
      const onException = jest.fn();

      const result = registerPropertyList([], 'Screen', 'prop1', onDataReceived, onRendered, onException);

      const callbacks = result[0].propertyList[0].callbacks;
      expect(callbacks.onDataReceived).toBe(onDataReceived);
      expect(callbacks.onRendered).toBe(onRendered);
      expect(callbacks.onPlaceholderException).toBe(onException);
    });

    it('should handle undefined callbacks gracefully', () => {
      const result = registerPropertyList([], 'Screen', 'prop1');
      const callbacks = result[0].propertyList[0].callbacks;
      expect(callbacks.onDataReceived).toBeNull();
      expect(callbacks.onRendered).toBeNull();
      expect(callbacks.onPlaceholderException).toBeNull();
    });

    it('should handle list with default empty array parameter', () => {
      // When list is undefined, it defaults to [] via the default param
      const result = registerPropertyList(undefined, 'Screen', 'prop1', jest.fn());
      // Default param makes list = [], then it registers normally
      expect(result).toHaveLength(1);
      expect(result[0].screenName).toBe('Screen');
    });

    it('should handle multiple screens', () => {
      let list = [];
      list = registerPropertyList(list, 'Screen1', 'prop1', jest.fn(), null, null);
      list = registerPropertyList(list, 'Screen2', 'prop2', jest.fn(), null, null);

      expect(list).toHaveLength(2);
      expect(list[0].screenName).toBe('Screen1');
      expect(list[1].screenName).toBe('Screen2');
    });
  });

  describe('getLatestScreenIndex', () => {
    it('should return index of existing screen', () => {
      const list = [
        { screenName: 'Screen1', propertyList: [] },
        { screenName: 'Screen2', propertyList: [] },
      ];
      expect(getLatestScreenIndex('Screen1', list)).toBe(0);
      expect(getLatestScreenIndex('Screen2', list)).toBe(1);
    });

    it('should return -1 for non-existing screen', () => {
      const list = [{ screenName: 'Screen1', propertyList: [] }];
      expect(getLatestScreenIndex('NonExistent', list)).toBe(-1);
    });

    it('should return -1 for empty list', () => {
      expect(getLatestScreenIndex('Screen1', [])).toBe(-1);
    });

    it('should return -1 for null list', () => {
      expect(getLatestScreenIndex('Screen1', null)).toBe(-1);
    });

    it('should return -1 for undefined list', () => {
      expect(getLatestScreenIndex('Screen1', undefined)).toBe(-1);
    });

    it('should return -1 for null screen name', () => {
      const list = [{ screenName: 'Screen1', propertyList: [] }];
      expect(getLatestScreenIndex(null, list)).toBe(-1);
    });

    it('should return -1 for empty screen name', () => {
      const list = [{ screenName: 'Screen1', propertyList: [] }];
      expect(getLatestScreenIndex('', list)).toBe(-1);
    });
  });

  describe('removePropertyFromPropertyList', () => {
    it('should remove a property from the list', () => {
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [
          { propertyId: 'prop1', callbacks: {} },
          { propertyId: 'prop2', callbacks: {} },
        ],
      }];

      const { updatedList, listenerFlag } = removePropertyFromPropertyList(
        list, 'HomeScreen', 'prop1', [], true
      );

      expect(updatedList[0].propertyList).toHaveLength(1);
      expect(updatedList[0].propertyList[0].propertyId).toBe('prop2');
      expect(listenerFlag).toBe(true);
    });

    it('should remove the screen entry when all properties are removed', () => {
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: 'prop1', callbacks: {} }],
      }];

      const { updatedList, listenerFlag } = removePropertyFromPropertyList(
        list, 'HomeScreen', 'prop1', [], true
      );

      expect(updatedList).toHaveLength(0);
      expect(listenerFlag).toBe(false);
    });

    it('should set listenerFlag to false when list becomes empty', () => {
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: 'prop1', callbacks: {} }],
      }];

      const { listenerFlag } = removePropertyFromPropertyList(
        list, 'HomeScreen', 'prop1', [], true
      );

      expect(listenerFlag).toBe(false);
    });

    it('should not modify list for non-existing screen', () => {
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: 'prop1', callbacks: {} }],
      }];

      const { updatedList } = removePropertyFromPropertyList(
        list, 'OtherScreen', 'prop1', [], true
      );

      expect(updatedList).toHaveLength(1);
      expect(updatedList[0].propertyList).toHaveLength(1);
    });

    it('should not modify list for non-existing property', () => {
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: 'prop1', callbacks: {} }],
      }];

      const { updatedList } = removePropertyFromPropertyList(
        list, 'HomeScreen', 'nonExistent', [], true
      );

      expect(updatedList[0].propertyList).toHaveLength(1);
    });

    it('should handle null list', () => {
      const { updatedList, listenerFlag } = removePropertyFromPropertyList(
        null, 'HomeScreen', 'prop1', [], true
      );

      expect(updatedList).toEqual([]);
      expect(listenerFlag).toBe(false);
    });

    it('should handle missing screenName', () => {
      const list = [{ screenName: 'HomeScreen', propertyList: [{ propertyId: 'prop1', callbacks: {} }] }];
      const { updatedList, listenerFlag } = removePropertyFromPropertyList(
        list, '', 'prop1', [], true
      );

      expect(updatedList).toEqual(list);
      expect(listenerFlag).toBe(true);
    });

    it('should handle missing propertyId', () => {
      const list = [{ screenName: 'HomeScreen', propertyList: [{ propertyId: 'prop1', callbacks: {} }] }];
      const { updatedList, listenerFlag } = removePropertyFromPropertyList(
        list, 'HomeScreen', '', [], true
      );

      expect(updatedList).toEqual(list);
      expect(listenerFlag).toBe(true);
    });
  });

  describe('getPropertyDetails', () => {
    it('should find property by targetViewId', () => {
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [
          { propertyId: 'prop1', callbacks: { onDataReceived: jest.fn() } },
          { propertyId: 'prop2', callbacks: { onDataReceived: jest.fn() } },
        ],
      }];

      const result = getPropertyDetails(list, { targetViewId: 'prop1' });
      expect(result.propertyId).toBe('prop1');
    });

    it('should match numeric targetViewId with string propertyId', () => {
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: '123', callbacks: {} }],
      }];

      const result = getPropertyDetails(list, { targetViewId: 123 });
      expect(result.propertyId).toBe('123');
    });

    it('should return null for empty list', () => {
      expect(getPropertyDetails([], { targetViewId: 'prop1' })).toBeNull();
    });

    it('should return null for null list', () => {
      expect(getPropertyDetails(null, { targetViewId: 'prop1' })).toBeNull();
    });

    it('should return null when targetViewId is undefined', () => {
      const list = [{ screenName: 'Screen', propertyList: [{ propertyId: 'prop1', callbacks: {} }] }];
      expect(getPropertyDetails(list, {})).toBeNull();
    });

    it('should return null when targetViewId is null', () => {
      const list = [{ screenName: 'Screen', propertyList: [{ propertyId: 'prop1', callbacks: {} }] }];
      expect(getPropertyDetails(list, { targetViewId: null })).toBeNull();
    });

    it('should return null when property not found', () => {
      const list = [{ screenName: 'Screen', propertyList: [{ propertyId: 'prop1', callbacks: {} }] }];
      expect(getPropertyDetails(list, { targetViewId: 'nonExistent' })).toBeNull();
    });

    it('should return null for null weCampaignData', () => {
      const list = [{ screenName: 'Screen', propertyList: [{ propertyId: 'prop1', callbacks: {} }] }];
      expect(getPropertyDetails(list, null)).toBeNull();
    });

    it('should return null when lastScreen.propertyList is not an array', () => {
      const list = [{ screenName: 'Screen', propertyList: 'not-an-array' }];
      expect(getPropertyDetails(list, { targetViewId: 'prop1' })).toBeNull();
    });

    it('should return null when lastScreen.propertyList is null', () => {
      const list = [{ screenName: 'Screen', propertyList: null }];
      expect(getPropertyDetails(list, { targetViewId: 'prop1' })).toBeNull();
    });

    it('should search in the last screen of the list', () => {
      const list = [
        { screenName: 'Screen1', propertyList: [{ propertyId: 'prop1', callbacks: {} }] },
        { screenName: 'Screen2', propertyList: [{ propertyId: 'prop2', callbacks: {} }] },
      ];

      // Should find prop2 (last screen) but not prop1
      expect(getPropertyDetails(list, { targetViewId: 'prop2' })).not.toBeNull();
      expect(getPropertyDetails(list, { targetViewId: 'prop1' })).toBeNull();
    });
  });

  describe('sendOnDataReceivedEvent', () => {
    it('should call onDataReceived callback with parsed data', () => {
      const onDataReceived = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{
          propertyId: 'prop1',
          callbacks: { onDataReceived },
        }],
      }];

      const data = {
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payloadData: '{"key":"value"}',
      };

      sendOnDataReceivedEvent(list, data);

      expect(onDataReceived).toHaveBeenCalledWith({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payload: { key: 'value' },
      });
    });

    it('should handle invalid JSON in payloadData', () => {
      const onDataReceived = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{
          propertyId: 'prop1',
          callbacks: { onDataReceived },
        }],
      }];

      const data = {
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payloadData: 'invalid-json',
      };

      sendOnDataReceivedEvent(list, data);

      expect(onDataReceived).toHaveBeenCalledWith({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payload: {},
      });
    });

    it('should not throw when data is null', () => {
      expect(() => sendOnDataReceivedEvent([], null)).not.toThrow();
    });

    it('should handle missing payloadData field', () => {
      const onDataReceived = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{
          propertyId: 'prop1',
          callbacks: { onDataReceived },
        }],
      }];

      sendOnDataReceivedEvent(list, { targetViewId: 'prop1', campaignId: 'camp1' });

      expect(onDataReceived).toHaveBeenCalledWith({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payload: {},
      });
    });

    it('should not call callback when property not found', () => {
      const onDataReceived = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{
          propertyId: 'prop1',
          callbacks: { onDataReceived },
        }],
      }];

      sendOnDataReceivedEvent(list, { targetViewId: 'nonExistent', campaignId: 'camp1' });
      expect(onDataReceived).not.toHaveBeenCalled();
    });

    it('should handle callback that throws an error', () => {
      const onDataReceived = jest.fn().mockImplementation(() => {
        throw new Error('callback error');
      });
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{
          propertyId: 'prop1',
          callbacks: { onDataReceived },
        }],
      }];

      expect(() => {
        sendOnDataReceivedEvent(list, { targetViewId: 'prop1', campaignId: 'camp1' });
      }).not.toThrow();
    });
  });

  describe('sendOnRenderedEvent', () => {
    it('should call onRendered callback with parsed data', () => {
      const onRendered = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{
          propertyId: 'prop1',
          callbacks: { onRendered },
        }],
      }];

      const data = {
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payloadData: '{"html":"<div>test</div>"}',
      };

      sendOnRenderedEvent(list, data);

      expect(onRendered).toHaveBeenCalledWith({
        targetViewId: 'prop1',
        campaignId: 'camp1',
        payload: { html: '<div>test</div>' },
      });
    });

    it('should not throw when data is null', () => {
      expect(() => sendOnRenderedEvent([], null)).not.toThrow();
    });

    it('should handle invalid JSON in payloadData', () => {
      const onRendered = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{
          propertyId: 'prop1',
          callbacks: { onRendered },
        }],
      }];

      sendOnRenderedEvent(list, { targetViewId: 'prop1', payloadData: '{bad' });

      expect(onRendered).toHaveBeenCalledWith(
        expect.objectContaining({ payload: {} })
      );
    });

    it('should not call callback when property not found', () => {
      const onRendered = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: 'prop1', callbacks: { onRendered } }],
      }];

      sendOnRenderedEvent(list, { targetViewId: 'other' });
      expect(onRendered).not.toHaveBeenCalled();
    });

    it('should handle onRendered callback that throws an error', () => {
      const onRendered = jest.fn().mockImplementation(() => {
        throw new Error('render callback error');
      });
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: 'prop1', callbacks: { onRendered } }],
      }];

      expect(() => {
        sendOnRenderedEvent(list, { targetViewId: 'prop1', campaignId: 'camp1' });
      }).not.toThrow();
    });
  });

  describe('sendOnExceptionEvent', () => {
    it('should call onPlaceholderException callback', () => {
      const onPlaceholderException = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{
          propertyId: 'prop1',
          callbacks: { onPlaceholderException },
        }],
      }];

      const data = { targetViewId: 'prop1', error: 'something went wrong' };
      sendOnExceptionEvent(list, data);

      expect(onPlaceholderException).toHaveBeenCalledWith(data);
    });

    it('should not throw when data is null', () => {
      expect(() => sendOnExceptionEvent([], null)).not.toThrow();
    });

    it('should not call callback when property not found', () => {
      const onPlaceholderException = jest.fn();
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: 'prop1', callbacks: { onPlaceholderException } }],
      }];

      sendOnExceptionEvent(list, { targetViewId: 'other' });
      expect(onPlaceholderException).not.toHaveBeenCalled();
    });

    it('should handle callback that throws an error', () => {
      const onPlaceholderException = jest.fn().mockImplementation(() => {
        throw new Error('callback error');
      });
      const list = [{
        screenName: 'HomeScreen',
        propertyList: [{ propertyId: 'prop1', callbacks: { onPlaceholderException } }],
      }];

      expect(() => {
        sendOnExceptionEvent(list, { targetViewId: 'prop1' });
      }).not.toThrow();
    });
  });
});
