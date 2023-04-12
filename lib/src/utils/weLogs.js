
let isDevEnabled = false;
export const enableDevMode = () => {
  console.log('WebEngage: enabled Dev Mode');
  isDevEnabled = true;
};

export const weLogs = (...data) => {
  if (isDevEnabled) {
    console.log('WebEngage: ', ...data);
  }
};
