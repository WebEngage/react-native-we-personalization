# Example App for WebEngage Inline

This is an example app for WebEngage Inline, demonstrating how to use WebEngage's inline messaging functionality in a React Native app. Before running the Example App, you need to have access to the WebEngage dashboard with a valid license key.

## Steps to run the Example App

1. Clone the repository to your local machine.
2. Navigate to the ExampleApp folder using the command `cd ExampleApp`
3. Install the required dependencies by running `yarn/npm i`
4. In `ExampleApp/android/app/src/main/java/com/exampleapp/MainApplication.java`, replace the WebEngage license code with your own license code in the `setWebEngageKey()` method. For more information, refer to the [WebEngage Android documentation](https://docs.webengage.com/docs/android-getting-started#2-initialization).
5. To run the Android version, use the command `react-native run-android`
6. In Xcode, open your project and update the `WEGLicenseCode` with your license code from the info.plist file. For more information, refer to the [WebEngage iOS documentation](https://docs.webengage.com/docs/ios-getting-started#3-configure-infoplist).
7. To run the iOS version, use the command `cd ios && pod install && cd .. && react-native run-ios`.
8. In the app, go to the Custom Screen section.
9. Choose to add a new screen and fill in the required details as per the table given below:

   | Labels                 | Description                                        | Mandatory |
      | --------------------- | -------------------------------------------------- | --------- |
   | Size                   | Number of Views to create dynamically              | Yes       |
   | Screen Name            | The name of the screen. Campaigns are driven on the basis of screen name | Yes |
   | Event Name             | The name of the event to trigger immediately after navigating to the screen | No |
   | Screen Property Name   | The name of the property to be added to the campaign conditions | No |
   | Screen Property Value  | The value of the property to be added to the campaign conditions | No |

10. Since the above details are added to create the screen, you need to add the `propertyId` details of your view where you would like to render the WebEngage Inline.
11. Click on Add View and fill in the required details as per the table given below:

| Tables               | Description                                                          | Mandatory |
   | --------------------| -------------------------------------------------------------------- | --------- |
| Position             | Position of the view to be displayed                                  | Yes       |
| Height               | Height of the view                                                    | No        |
| Width                | Width of the view                                                     | No        |
| Android PropertyId   | Property ID of Android registered at WebEngage dashboard (String)     | Yes       |
| iOS PropertyId       | Property ID of iOS registered at WebEngage dashboard (Integer)        | Yes       |
| is Custom            | Check this in case you are creating a custom view                      | No        |

12. After adding the required number of views, click on the Save button.
13. Your screen data will be listed in the screen list page, where you can choose to navigate to the desired screen.
14. If the campaign is valid, the WebEngage Inline View will be visible. If not, you will see an exception with cause.
