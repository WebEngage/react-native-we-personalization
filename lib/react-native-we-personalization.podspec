require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-we-personalization"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "11.0" }
  s.source       = { :git => "https://github.com/WebEngage/react-native-we-personalization.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift}"
  s.swift_version = '5.0'

  s.pod_target_xcconfig = {
    "HEADER_SEARCH_PATHS" => [
      '"$(PODS_ROOT)/boost"',
      '"$(PODS_ROOT)/RCT-Folly"',
      '"$(PODS_ROOT)/DoubleConversion"'
    ].join(' '),
    "CLANG_CXX_LANGUAGE_STANDARD" => "c++20",
    "DEFINES_MODULE" => "YES",
    "SWIFT_OBJC_INTERFACE_HEADER_NAME" => "react_native_we_personalization-Swift.h"
  }

  install_modules_dependencies(s)

  s.dependency "React-Core"
  s.dependency "React-jsi"
  s.dependency "WebEngage/Core",'>= 6.9.0'
  s.dependency "WEPersonalization"
end
