const path = require('path');
const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');

// Path to the local lib that react-native-we-personalization symlinks to
const personalizationLib = path.resolve(__dirname, '../lib');

const config = {
  watchFolders: [personalizationLib],
  resolver: {
    nodeModulesPaths: [path.resolve(__dirname, 'node_modules')],
  },
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
