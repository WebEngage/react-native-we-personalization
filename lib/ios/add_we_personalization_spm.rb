#!/usr/bin/env ruby
# Adds WebEngagePersonalization SPM product from the webengage-ios-sdk package.
# Requires the base WebEngage SPM package to already be added via react-native-webengage.
#
# Usage in Podfile:
#   require_relative '../node_modules/react-native-we-personalization/ios/add_we_personalization_spm'
#   
#   post_install do |installer|
#     add_webengage_spm(installer)            # from react-native-webengage
#     add_we_personalization_spm(installer)    # from this plugin
#   end

begin
  require 'xcodeproj'
rescue LoadError
  raise "[react-native-we-personalization] The 'xcodeproj' gem is required. Run: sudo gem install xcodeproj"
end

WEP_REPO_URL = defined?(WEP_REPO_URL) ? WEP_REPO_URL : 'https://github.com/WebEngage/webengage-ios-sdk'
WEP_PRODUCT_NAME = defined?(WEP_PRODUCT_NAME) ? WEP_PRODUCT_NAME : 'WebEngagePersonalization'

def add_we_personalization_spm(installer, options = {})
  repo_url = options[:repo_url] || WEP_REPO_URL
  product_name = options[:product_name] || WEP_PRODUCT_NAME

  project = nil
  installer.aggregate_targets.each do |target|
    if target.user_project
      project = target.user_project
      break
    end
  end

  unless project
    puts "[react-native-we-personalization] Could not find user Xcode project, skipping."
    return
  end

  project_modified = false

  # Find the existing webengage-ios-sdk package reference (added by react-native-webengage)
  pkg_ref = project.root_object.package_references.find { |ref| ref.repositoryURL == repo_url }
  unless pkg_ref
    puts "[react-native-we-personalization] WebEngage SPM package not found. Make sure add_webengage_spm(installer) runs first."
    return
  end

  app_target = project.targets.find { |t| t.product_type == 'com.apple.product-type.application' }
  unless app_target
    puts "[react-native-we-personalization] Could not find application target, skipping SPM product setup."
    return
  end

  existing = app_target.package_product_dependencies.any? { |d| d.product_name == product_name }
  unless existing
    dep = project.new(Xcodeproj::Project::Object::XCSwiftPackageProductDependency)
    dep.package = pkg_ref
    dep.product_name = product_name
    app_target.package_product_dependencies << dep
    puts "[react-native-we-personalization] Added SPM product: #{product_name}"
    project_modified = true
  else
    puts "[react-native-we-personalization] SPM product #{product_name} already present, skipping."
  end

  # only save if something actually changed
  project.save if project_modified
end

# Auto-register into CocoaPods post_install hook
Pod::HooksManager.register('react-native-we-personalization', :post_install) do |installer|
  add_we_personalization_spm(installer)
end
