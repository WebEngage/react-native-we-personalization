require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))



Pod::Spec.new do |s|
  s.name         = "react-native-we-personalization"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => '11.0' }

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

  if respond_to?(:install_modules_dependencies, true)
    install_modules_dependencies(s)
  else
    s.dependency "React-Core"
  end

  # Common WebEngage deps (always needed)
  s.dependency "WebEngage/Core", '>= 6.9.0'
  s.dependency "WEPersonalization"
end
