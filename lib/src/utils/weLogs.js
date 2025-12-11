
let isDevEnabled = false;

export const enableDevMode = () => {
  console.log('[WE-Inline-JS] Dev mode enabled');
  isDevEnabled = true;
};

export const weLogs = (...data) => {
  if (isDevEnabled) {
    console.log('[WE-Inline-JS]', ...data);
  }
};
