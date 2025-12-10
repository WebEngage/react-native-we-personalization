import WebEngage from 'react-native-webengage';

export let webengageInstance: any = null;

export const initWebEngage = () => {
  webengageInstance = new WebEngage();

  webengageInstance.notification.onPrepare(function (notificationData: any) {
    console.log('App: in-app notification prepared');
  });

  webengageInstance.notification.onShown(function (notificationData: any) {
    let message;
    if (notificationData['title'] && notificationData['title'] !== null) {
      message = 'title: ' + notificationData['title'];
    } else if (notificationData['description'] && notificationData['description'] !== null) {
      message = 'description: ' + notificationData['description'];
    }
    console.log('App: in-app notification shown with ' + message);
  });

  webengageInstance.notification.onClick(function (notificationData: any, clickId: string) {
    console.log(
      'App: in-app notification clicked: click-id: ' +
        clickId +
        ', deep-link: ' +
        notificationData['deeplink']
    );
  });

  webengageInstance.notification.onDismiss(function (notificationData: any) {
    console.log('App: in-app notification dismissed');
  });

  webengageInstance.push.onClick(function (notificationData: any) {
    console.log('App: push-notification clicked with deeplink: ' + notificationData['deeplink']);
    console.log(
      'App: push-notification clicked with payload: ' + JSON.stringify(notificationData['userData'])
    );
  });

  webengageInstance.universalLink.onClick(function (location: string) {
    console.log('App: universal link clicked with location: ' + location);
  });
};
