require "json"

new_arch_enabled = ENV['RCT_NEW_ARCH_ENABLED'] == '1'
ios_platform = new_arch_enabled ? '11.0' : '11.0'

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-we-personalization"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => ios_platform }
  s.source       = { :git => "https://github.com/WebEngage/react-native-we-personalization.git", :tag => "#{s.version}" }

  s.source_files = "ios/**/*.{h,m,mm,swift}"
  s.swift_version = '5.0'

  if defined?(install_modules_dependencies()) != nil
    install_modules_dependencies(s)
  else
    if new_arch_enabled
      folly_compiler_flags = '-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1 -Wno-comma -Wno-shorten-64-to-32'

      s.compiler_flags = folly_compiler_flags + " -DRCT_NEW_ARCH_ENABLED=1"

      s.pod_target_xcconfig = {
        "HEADER_SEARCH_PATHS" => '"$(PODS_ROOT)/boost"',
        "OTHER_CPLUSPLUSFLAGS" => "-DFOLLY_NO_CONFIG -DFOLLY_MOBILE=1 -DFOLLY_USE_LIBCPP=1",
        "CLANG_CXX_LANGUAGE_STANDARD" => "c++17",
        "DEFINES_MODULE" => "YES",
        "SWIFT_OBJC_INTERFACE_HEADER_NAME" => "react_native_we_personalization-Swift.h"
      }

      s.dependency "React-RCTFabric"
      s.dependency "React-Codegen"
      s.dependency "RCT-Folly"
      s.dependency "RCTRequired"
      s.dependency "RCTTypeSafety"
      s.dependency "ReactCommon/turbomodule/core"
    else
      s.pod_target_xcconfig = {
        "CLANG_CXX_LANGUAGE_STANDARD" => "c++20",
        "DEFINES_MODULE" => "YES",
        "SWIFT_OBJC_INTERFACE_HEADER_NAME" => "react_native_we_personalization-Swift.h"
      }
      s.dependency "React-Core"
    end
  end

  s.dependency "React-jsi"
  s.dependency "WebEngage/Core",'>= 6.9.0'
  s.dependency "WEPersonalization"
end
