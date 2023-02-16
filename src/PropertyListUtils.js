export const registerPropertyList = (
  list,
  screenName,
  propertyId,
  onDataReceived = null,
  onRendered = null,
  onPlaceholderException = null
) => {
  const screenIndex = getLatestScreenIndex(screenName, list);
  if (screenIndex === -1) {
    list.push({
      screenName: screenName,
      propertyList: [],
    });
  }
  if (
    !list[list?.length - 1]?.propertyList
      .flatMap((curr) => curr.propertyId)
      .includes(propertyId)
  ) {
    let obj = {};
    obj.propertyId = propertyId;
    obj.callbacks = {
      onRendered: onRendered,
      onPlaceholderException: onPlaceholderException,
      onDataReceived: onDataReceived,
    };
    list[list?.length - 1].propertyList.push(obj);
  }

  return list;
};

export function getLatestScreenIndex(screen, list) {
  return list?.findIndex((obj) => obj.screenName === screen);
}

export const removeScreenFromPropertyList = (
  list,
  screenName,
  listenersList,
  listenerFlag
) => {
  let updatedList = list;
  updatedList?.map((val, index) => {
    if (val.screenName === screenName) {
      updatedList.splice(index, 1);
    }
  });
  console.log(
    'listener individual removeScreenFromPropertyList ',
    list,
    listenerFlag
  );

  if (!updatedList?.length && listenerFlag) {
    listenersList?.forEach((listener) => {
      listener?.remove();
    });
    listenerFlag = false;
    console.log('@@@@ All the Listeners are removed ');
  }
  console.log('$$$ list after removing screen ', updatedList);

  return { updatedList, listenerFlag };
  // return list;
};

export const getPropertyDetails = (list, weCampaignData) => {
  let res = null;
  const { targetViewId = '' } = weCampaignData;
  if (list?.length) {
    list[list.length - 1]?.propertyList?.map((val) => {
      if (val.propertyId === targetViewId) {
        res = val;
      }
    });
  }
  return res;
};

export const sendOnDataReceivedEvent = (list, data) => {
  const { targetViewId = '', campaignId = '', payloadData = '' } = data;
  // const payload = JSON.parse(payloadData);
  const weCampaignData = {
    targetViewId,
    campaignId,
    // payload,
  };
  const propertyItem = getPropertyDetails(list, weCampaignData);
  console.log('onDataReceived! - Event Listener called ->', weCampaignData);

  if (propertyItem?.callbacks?.onDataReceived) {
    propertyItem?.callbacks?.onDataReceived(weCampaignData);
  }
};

export const sendOnRenderedEvent = (list, data) => {
  const { targetViewId = '', campaignId = '', payloadData } = data;
  // const payload = JSON.parse(payloadData);
  const weCampaignData = {
    targetViewId,
    campaignId,
    // payload,
  };
  // TODO payload json issue
  console.log('onRendered - Event Listener called ->', weCampaignData);

  const propertyItem = getPropertyDetails(list, weCampaignData);
  if (propertyItem?.callbacks?.onRendered) {
    propertyItem?.callbacks?.onRendered(weCampaignData);
  }
};

export const sendOnExceptionEvent = (list, data) => {
  console.log('onPlaceholderException - Event Listerner called ->', data);
  const { targetViewId = '' } = data;
  const weCampaignData = {
    targetViewId,
  };
  const propertyItem = getPropertyDetails(list, weCampaignData);
  if (propertyItem?.callbacks?.onPlaceholderException) {
    propertyItem?.callbacks?.onPlaceholderException(data);
  }
};
