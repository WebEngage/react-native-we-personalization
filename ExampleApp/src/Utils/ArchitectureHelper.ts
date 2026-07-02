import {TurboModuleRegistry} from 'react-native';

interface ArchitectureInfo {
  isTurboModuleEnabled: boolean;
  isFabricEnabled: boolean;
  isBridgeless: boolean;
  isNewArchitecture: boolean;
  architectureMode: string;
  bridgeMode: string;
}

export const getArchitectureInfo = (): ArchitectureInfo => {
  const isTurboModuleEnabled = TurboModuleRegistry?.get != null || (global as any).__turboModuleProxy != null;
  const isFabricEnabled = (global as any).nativeFabricUIManager != null;
  const isBridgeless = (global as any).RN$Bridgeless === true;

  return {
    isTurboModuleEnabled,
    isFabricEnabled,
    isBridgeless,
    isNewArchitecture: isTurboModuleEnabled && isFabricEnabled,
    architectureMode: isTurboModuleEnabled && isFabricEnabled ? 'New Architecture' : 'Old Architecture',
    bridgeMode: isBridgeless ? 'Bridgeless' : 'Bridge',
  };
};
