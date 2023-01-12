First Commit -> *React native UI component) Rendering native UI components in React native android and ios

Second commit -> React native bridge to send data from Rn to native and vice versa


12-jan-2023
Personalizatino SDK is added via aar file in the Android - library
Steps and tips to remember while adding SDK aar file
* from android repo -> clean build and rebuild -> It will fetch aar file in build - output
* copy paste that in rnPersonalization/Android/listenerCallback
* CoroutinesScope is added again in the rnPersonalization/Android/build.gradle bcz CoroutinesScope was crashing the App
* For DuplicateClassFound issue -> check Android-sdk integration in node-modules/react-native-webengage(Main SDK)
* Incase aar file data is coming as encrypted -> turn off pro gaurd and minifyEnabled as false
