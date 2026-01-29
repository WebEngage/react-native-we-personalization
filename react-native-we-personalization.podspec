require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

new_arch_enabled    = ENV['RCT_NEW_ARCH_ENABLED'] == '1'
ios_platform        = new_arch_enabled ? '11.0' : '9.0'
folly_compiler_flags = '-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1 -Wno-comma -Wno-shorten-64-to-32'

Pod::Spec.new do |s|
  s.name         = "react-native-we-personalization"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  # iOS platform – follows your new-arch podspec behaviour:
  #  - iOS 11.0 when New Architecture is enabled
  #  - iOS 9.0 otherwise
  s.platforms    = { :ios => ios_platform }

  s.source       = {
    :git => "https://github.com/WebEngage/react-native-we-personalization.git",
    :tag => "#{s.version}"
  }

  s.source_files = "ios/**/*.{h,m,mm,swift}"
  s.exclude_files = "ios/headers/**", "ios/PersonalizationBridge-Bridging-Header.h", "ios/WebengagePersonalization-Bridging-Header.h", "ios/bridge/WEPersonalizationBridge-Bridging-Header.h"
  s.swift_version = '5.0'

  # Base Swift module config
  s.pod_target_xcconfig = {
    'DEFINES_MODULE' => 'YES',
    'SWIFT_OBJC_INTERFACE_HEADER_NAME' => 'react_native_we_personalization-Swift.h'
  }

  # React Native 0.71+ helper (Expo / New Arch style)
  if defined?(install_modules_dependencies) != nil
    install_modules_dependencies(s)
  else
    if new_arch_enabled
      # New Architecture (Fabric) manual wiring
      s.compiler_flags = "#{folly_compiler_flags} -DRCT_NEW_ARCH_ENABLED=1"

      # Merge Swift xcconfig with Folly / C++ settings
      s.pod_target_xcconfig.merge!(
        {
          "HEADER_SEARCH_PATHS" => "\"$(PODS_ROOT)/boost\"",
          "OTHER_CPLUSPLUSFLAGS" => "-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1",
          "CLANG_CXX_LANGUAGE_STANDARD" => "c++17"
        }
      )

      s.dependency "React-RCTFabric"
      s.dependency "React-Codegen"
      s.dependency "RCT-Folly"
      s.dependency "RCTRequired"
      s.dependency "RCTTypeSafety"
      s.dependency "ReactCommon/turbomodule/core"
    else
      # Old Architecture – legacy React Core
      s.dependency "React-Core"
    end
  end

  # Common WebEngage deps (always needed)
  s.dependency "WebEngage/Core", '>= 6.9.0'
  s.dependency "WEPersonalization"
end
