# react-native-webengage-personalization

Personalisation package for webengage react native

## Installation

```sh
npm install react-native-webengage-personalization
```

## Usage

```js
import { WebengagePersonalizationView } from "react-native-webengage-personalization";

// ...

<WebengagePersonalizationView color="tomato" />

```
Steps to run 
1. Take pull and npm install
2. go to example -> node_modules -> react-native-webengage -> Android
3. Open build.gradlew file and comment android-sdk import method 
4. create libs folder and add android-sdk.aar file
5. inside build.gradlew file add below code
  implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
  implementation files('libs/android-sdk.aar')
 
6. Try Running App now

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
