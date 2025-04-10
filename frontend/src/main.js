import { registerPlugins } from '@/plugins'
import '@/styles/index.css'
import App from './App.vue'
import { createApp } from 'vue'

// Function to get the current date in the desired format
function getCurrentDate() {
  const today = new Date();
  const dd = String(today.getDate()).padStart(2, '0');
  const mm = String(today.getMonth() + 1).padStart(2, '0'); // January is 0!
  const yyyy = today.getFullYear();
  return `${mm}/${dd}/${yyyy}`;
}

// Create the app and provide the date function as a global property
const app = createApp(App);
registerPlugins(app);
app.config.globalProperties.$getCurrentDate = getCurrentDate;

export let DATA = null;

export const SETDATA = (data) => {
  DATA = data;
}

app.mount('#app');

export let USERNAME = localStorage.getItem("USERNAME");
export const setUSERNAME = (username) => {
  USERNAME = username;
  localStorage.setItem("USERNAME", username);
}

export let AGE = localStorage.getItem("AGE");
export const setAGE = (age) => {
  AGE = age;
  localStorage.setItem("AGE", age);
}

export let EMAIL = localStorage.getItem("EMAIL");
export const setEMAIL = (email) => {
  EMAIL = email;
  localStorage.setItem("EMAIL", email);
}
export let REPORTID = localStorage.getItem("REPORTID");
export const setREPORTID = (reportid) => {
  REPORTID = reportid;
  localStorage.setItem("REPORTID", reportid);
}