#!/usr/bin/env node
/**
 * Post-install script for react-native-we-personalization
 * Automatically adds WebEngage Personalization SPM setup to the iOS Podfile.
 */

const fs = require('fs');
const path = require('path');

const REQUIRE_LINE = "require_relative '../node_modules/react-native-we-personalization/ios/add_we_personalization_spm'";
const HOOK_CALL = '    add_we_personalization_spm(installer)'; // 4-space indent

// module-level constants for base plugin strings so they stay in sync
const BASE_REQUIRE = "require_relative '../node_modules/react-native-webengage/ios/add_webengage_spm'";
const BASE_HOOK = '    add_webengage_spm(installer)';

function findPodfile() {
  let dir = process.cwd();
  for (let i = 0; i < 6; i++) {
    const candidate = path.join(dir, 'ios', 'Podfile');
    if (fs.existsSync(candidate)) return candidate;
    const parent = path.dirname(dir);
    if (parent === dir) break;
    dir = parent;
  }
  return null;
}

function patchPodfile() {
  const podfilePath = findPodfile();
  if (!podfilePath) {
    console.warn('[react-native-we-personalization] Could not find ios/Podfile. Add the following manually:');
    console.warn(`  ${REQUIRE_LINE}`);
    console.warn(`  ${HOOK_CALL.trim()} (inside post_install block)`);
    return;
  }

  let content = fs.readFileSync(podfilePath, 'utf8');
  let modified = false;

  // Add require_relative after base webengage line if present
  if (!content.includes('react-native-we-personalization/ios/add_we_personalization_spm')) {
    const webengageRequireIdx = content.indexOf('react-native-webengage/ios/add_webengage_spm');
    if (webengageRequireIdx !== -1) {
      const lineEnd = content.indexOf('\n', webengageRequireIdx);
      content = content.slice(0, lineEnd + 1) + REQUIRE_LINE + '\n' + content.slice(lineEnd + 1);
    } else {
      //  base require missing — inject it first to preserve load order
      content = BASE_REQUIRE + '\n' + REQUIRE_LINE + '\n' + content;
      console.warn('[react-native-we-personalization] react-native-webengage require line was missing — injected it before personalization require to preserve order.');
    }
    modified = true;
  }

  // Add hook call after base webengage hook if present
  if (!content.includes('add_we_personalization_spm(installer)')) {
    const webengageHookIdx = content.indexOf('add_webengage_spm(installer)');
    if (webengageHookIdx !== -1) {
      const lineEnd = content.indexOf('\n', webengageHookIdx);
      content = content.slice(0, lineEnd + 1) + HOOK_CALL + '\n' + content.slice(lineEnd + 1);
      modified = true;
    } else {
      // base hook missing — inject it first so ordering is always correct
      const postInstallRegex = /(post_install\s+do\s+\|installer\|)/g;
      if (postInstallRegex.test(content)) {
        content = content.replace(/(post_install\s+do\s+\|installer\|)/g, `$1\n${BASE_HOOK}\n${HOOK_CALL}`);
        console.warn('[react-native-we-personalization] add_webengage_spm(installer) was missing — injected it before personalization hook to preserve order.');
        modified = true;
      } else {
        content = content + '\npost_install do |installer|\n' + BASE_HOOK + '\n' + HOOK_CALL + '\nend\n';
        modified = true;
        console.warn('[react-native-we-personalization] No post_install block found. Created one with webengage + personalization hooks.');
      }
    }
  }

  if (modified) {
    fs.writeFileSync(podfilePath + '.bak', fs.readFileSync(podfilePath));
    fs.writeFileSync(podfilePath, content, 'utf8');
    console.log('[react-native-we-personalization] ✅ Podfile patched with Personalization SPM setup.');
  }
}

try {
  patchPodfile();
} catch (e) {
  console.warn('[react-native-we-personalization] Could not auto-patch Podfile:', e.message);
}
