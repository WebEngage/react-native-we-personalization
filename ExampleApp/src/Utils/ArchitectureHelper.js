export const getArchitectureInfo = () => {
  const isTurboModuleEnabled = global.__turboModuleProxy != null;
  const isFabricEnabled = global.nativeFabricUIManager != null;
  const isBridgeless = global.RN$Bridgeless === true;

  return {
    isTurboModuleEnabled,
    isFabricEnabled,
    isBridgeless,
    isNewArchitecture: isTurboModuleEnabled && isFabricEnabled,
    architectureMode: isTurboModuleEnabled && isFabricEnabled 
      ? 'New Architecture' 
      : 'Old Architecture',
    bridgeMode: isBridgeless ? 'Bridgeless' : 'Bridge',
  };
};
