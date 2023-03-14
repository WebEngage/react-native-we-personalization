
let isDevEnabled = false
export const enableDevMode = () => {
  console.log("WebEngage: enabled Dev Mode")
    isDevEnabled = true;
}

export const MyLogs = (...data) => {
  if(isDevEnabled) {
    console.log("WebEngage: "+ data)
  }
}
