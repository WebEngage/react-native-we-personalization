import { MyLogs } from "./MyLogs";

let propertyProcessorList = []
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
  MyLogs("PropertyListUtils: screenList list after updation ",list)
  propertyProcessorList = [...list];
  return list;
};

export function getLatestScreenIndex(screen, list) {
  return list?.findIndex((obj) => obj.screenName === screen);
}

export const removePropertyFromPropertyList = (
  list,
  screenName,
  propertyId,
  listenersList,
  listenerFlag
) => {
  let updatedList = list;
  updatedList?.map((val, index) => {
    if (val.screenName === screenName) {
          val?.propertyList?.forEach((property, propertyIndex) => {
            if (property?.propertyId === propertyId) {
              updatedList[index].propertyList.splice(propertyIndex, 1);
            }
          });
          MyLogs(
            'PropertyListUtils: updated list after removing property- ',
            updatedList
          );
          if(!val.propertyList.length) {
            updatedList.splice(index,1)
          }
    }
  });

  if (!updatedList?.length && listenerFlag) {
    listenerFlag = false;
    MyLogs('PropertyListUtils: All the Listeners are removed ',listenersList);
  }

  return { updatedList, listenerFlag };
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
  const { targetViewId = '', campaignId = '', payloadData = '{}' } = data;
  const payload = JSON.parse(payloadData);
  const weCampaignData = {
    targetViewId,
    campaignId,
    payload,
  };
  const propertyItem = getPropertyDetails(list, weCampaignData);
  MyLogs('PropertyListUtils: onDataReceived! - Event Listener called ->', weCampaignData);

  if (propertyItem?.callbacks?.onDataReceived) {
    propertyItem?.callbacks?.onDataReceived(weCampaignData);
  }
};

export const sendOnRenderedEvent = (list, data) => {
  const { targetViewId = '', campaignId = '', payloadData = '{}' } = data;
  const payload = JSON.parse(payloadData);
  const weCampaignData = {
    targetViewId,
    campaignId,
    payload,
  };
  MyLogs('PropertyListUtils: onRendered - Event Listener called ->', weCampaignData);

  const propertyItem = getPropertyDetails(list, weCampaignData);
  if (propertyItem?.callbacks?.onRendered) {
    propertyItem?.callbacks?.onRendered(weCampaignData);
  }
};

export const sendOnExceptionEvent = (list, data) => {
  MyLogs('PropertyListUtils: onPlaceholderException - Event Listener called ->', data);
  const { targetViewId = '' } = data;
  const weCampaignData = {
    targetViewId,
  };
  const propertyItem = getPropertyDetails(list, weCampaignData);
  if (propertyItem?.callbacks?.onPlaceholderException) {
    propertyItem?.callbacks?.onPlaceholderException(data);
  }
};
