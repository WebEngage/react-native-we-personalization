#!/usr/bin/env node
/**
 * Post-install script for react-native-we-personalization
 * Automatically adds WebEngage Personalization SPM setup to the iOS Podfile.
 */

const fs = require('fs');
const path = require('path');

const REQUIRE_LINE = "require_relative '../node_modules/react-native-we-personalization/ios/add_we_personalization_spm'";
const HOOK_CALL = '    add_we_personalization_spm(installer)';

function findPodfile() {
  const projectRoot = path.resolve(__dirname, '..', '..', '..');
  const podfilePath = path.join(projectRoot, 'ios', 'Podfile');
  if (fs.existsSync(podfilePath)) {
    return podfilePath;
  }
  return null;
}

function patchPodfile() {
  const podfilePath = findPodfile();
  if (!podfilePath) {
    return;
  }

  let content = fs.readFileSync(podfilePath, 'utf8');
  let modified = false;

  if (!content.includes('react-native-we-personalization/ios/add_we_personalization_spm')) {
    const webengageRequireLine = content.indexOf('react-native-webengage/ios/add_webengage_spm');
    if (webengageRequireLine !== -1) {
      const lineEnd = content.indexOf('\n', webengageRequireLine);
      content = content.slice(0, lineEnd + 1) + REQUIRE_LINE + '\n' + content.slice(lineEnd + 1);
    } else {
      content = REQUIRE_LINE + '\n' + content;
    }
    modified = true;
  }

  if (!content.includes('add_we_personalization_spm(installer)')) {
    const hookIndex = content.indexOf('add_webengage_spm(installer)');
    if (hookIndex !== -1) {
      const lineEnd = content.indexOf('\n', hookIndex);
      content = content.slice(0, lineEnd + 1) + HOOK_CALL + '\n' + content.slice(lineEnd + 1);
      modified = true;
    } else {
      const postInstallRegex = /(post_install\s+do\s+\|installer\|)/;
      if (postInstallRegex.test(content)) {
        content = content.replace(postInstallRegex, `$1\n${HOOK_CALL}`);
        modified = true;
      }
    }
  }

  if (modified) {
    fs.writeFileSync(podfilePath, content, 'utf8');
    console.log('[react-native-we-personalization] ✅ Podfile patched with Personalization SPM setup.');
  }
}

try {
  patchPodfile();
} catch (e) {
  console.warn('[react-native-we-personalization] Could not auto-patch Podfile:', e.message);
}
