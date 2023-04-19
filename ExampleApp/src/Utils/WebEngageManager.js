import WebEngage from 'react-native-webengage';

export let webengageInstance = null;

export const initWebEngage = () => {
  webengageInstance = new WebEngage();

  // In-app notification callbacks
  webengageInstance.notification.onPrepare(function(notificationData) {
    console.log("App: in-app notification prepared");
  });

  webengageInstance.notification.onShown(function(notificationData) {
    var message;
    if (notificationData["title"] && notificationData["title"] !== null) {
      message = "title: " + notificationData["title"];
    } else if (notificationData["description"] && notificationData["description"] !== null) {
      message = "description: " + notificationData["description"];
    }
    console.log("App: in-app notification shown with " + message);
  });

  webengageInstance.notification.onClick(function(notificationData, clickId) {
    console.log("App: in-app notification clicked: click-id: " + clickId + ", deep-link: " + notificationData["deeplink"]);
  });

  webengageInstance.notification.onDismiss(function(notificationData) {
    console.log("App: in-app notification dismissed");
  });

  webengageInstance.push.onClick(function(notificationData) {
    console.log("MyLogs App: push-notiifcation clicked with deeplink: " + notificationData["deeplink"]);
    console.log("MyLogs App: push-notiifcation clicked with payload: " + JSON.stringify(notificationData["userData"]));

  });
  webengageInstance.universalLink.onClick(function(location){
    console.log("App: universal link clicked with location: " + location);
    notifyMessage(location);
  });
};
