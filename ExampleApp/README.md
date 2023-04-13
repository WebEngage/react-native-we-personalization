
> **_NOTE:_**  Please note that in order to run the Example App, you need to have access to our WebEngage dashboard with a valid license key. Also, make sure to test your package with both Yarn and npm to ensure compatibility, as they may handle certain features or dependencies differently.



**Steps to run Example App**


To run the Example App, please follow these steps:

1. Clone the repository to your local machine.
2. Navigate to the ExampleApp folder using the command `cd ExampleApp`
3. Install the required dependencies by running `yarn/npm i`
4. Go to ExampleApp/android/app/src/main/java/com/exampleapp/MainApplication.java and replace the WebEngage License code with your own license code in the setWebEngageKey() method. For more information, refer to this [docs](https://docs.webengage.com/docs/android-getting-started#2-initialization)
5.  To run the Android version, use the command `react-native run-android`
6. Open your project in Xcode and update the WEGLicenseCode with your license code from the info.plist. For more information, refer to [this](https://docs.webengage.com/docs/ios-getting-started#3-configure-infoplist)
7. To run the iOS version, use the command `cd ios && pod install && cd .. && react-native run-ios`.
8. Go to the Custom Screen section of the app.
9. Choose to add a new screen and fill in the details according to the following table:
    | Labels  | Description                     | Mandatory  |
    | :-------------: |:-------------:                  | :-----:|
    | Size          | Number of Views to create dynamically | Yes |
    | scren Name      | The name of the screen.Campaigns are driven on the basis of screen name      | Yes |
    | Event Name | The name of the event to trigger immediately after navigating to the screen     | No |
    | Screen property Name | The name of the property to be added to the campaign conditions    | No |
    | Screen property value | The value of the property to be added to the campaign conditions    | No |



10.  Since the above details are added to create the screen, you need to add the `propertyId` details of your view where you would like to render the WebEngage Inline.


11.  Click on Add View and fill in the details according to the following table:

| Tables | Description | Mandatory |
| :---:   | :---: | :---: |
| Position | Position of the view to be displayed   | yes   |
| Height | Height of the view   | No   |
| Width | Width of the view   | No   |
| Android PropertyId | PropertyId of Android registered at webengage dashboard (String)   | yes   |
| iOS PropertyId | PropertyId of iOS registered at webengage dashboard (Integer)   | yes   |
| is Custom | Check this incase you are creating customView   | no   |

12. After adding the required number of views, click on the Save button.
13. Your screen data will be listed in the screen list page, where you can choose to navigate to the desired screen.
14. If the campaign is valid, the WebEngage Inline View will be visible. otherwise You will get to see Exception with cause.

