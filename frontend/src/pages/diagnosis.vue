<template>
  <div class="container">
    <div class="outer-rectangle-box">
    <h1 class="generate-report-title">Generate Report</h1>
    
    <v-container>
      <v-form @submit.prevent="submitDiagnosis">
        <v-file-input
          label="Upload X-ray Image"
          @change="handleFileUpload"
          accept="image/*"
          required
        ></v-file-input>
        <v-text-field v-model="nationality" label="Nationality" required></v-text-field>

        <div class="q">
          <div class="midq">
            <div class="fontsymp">I have the following key symptoms:</div>
          <div class="smallq">
            <v-checkbox class="checkbox" v-model="cough" label="Cough"></v-checkbox>
            <v-checkbox class="checkbox" v-model="fever" label="Fever"></v-checkbox>
          </div>
        </div>
          <v-textarea v-model="context" class="textarea" label="Additional Context" variant="outlined"></v-textarea>
        </div>


        <div class="diagnosebutton">
          <v-btn class="start-diagnosis-btn" color="#f3719c" text="white" @click="submitDiagnosis">Start Diagnosis</v-btn>
        </div>
        
      </v-form>

      <v-alert v-if="response" type="info" dismissible>
        <h3>Diagnosis Report</h3>
        <pre>{{ response }}</pre>
      </v-alert>
    </v-container>
  </div>
</div>
</template>

<script setup>
import { ref } from 'vue';
import router from '../router';
import {SETDATA} from '../main';

const USERNAME = ref(localStorage.getItem('USERNAME'));
const AGE = ref(localStorage.getItem('AGE'));
const EMAIL = ref(localStorage.getItem('EMAIL'));

const processDiagnosis = () => {
  console.log('Diagnosis started');
  router.push('/report');
};

// Define reactive variables
const image = ref(null);
const cough = ref(false);
const fever = ref(false);
const nationality = ref('');
const context = ref('');
const response = ref(null);
const reportData = ref(null); // Add ref for report data

// Handle file upload
function handleFileUpload(event) {
  image.value = event.target.files[0];
}

// Submit diagnosis function
async function submitDiagnosis() {
  const formData = new FormData();
  formData.append('image', image.value);
  formData.append('cough', cough.value);  // Convert boolean to string
  formData.append('fever', fever.value);  
  formData.append('nationality', nationality.value);
  formData.append('context', context.value);
  console.log([...formData]); // Log formData content

  try {
    const res = await fetch('http://localhost:8080/diagnosis', {
      method: 'POST',
      body: formData,
    });
    const data = await res.json();
    console.log('Diagnosis report:', data);
    reportData.value = data; // Store report data
    SETDATA(data);
    router.push('/report');
  } catch (error) {
    console.error('Error submitting diagnosis:', error);
  } 
}
</script>

<style scoped>
.generate-report-title {
  color: #f3719c;
  padding: 20px;
}

.q {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-top: 20px;
}

.smallq {
  display: flex;
  gap: 10px;
  padding-left: 50px;
  margin-top: 10px;
}
.fontsymp{
  font-weight: bold;
  font-size: 20px;
  justify-content: left;
  color: #f3719c;
}
.outer-rectangle-box {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  width: 500px;
  height: 690px;
  background-color: rgba(255, 255, 255, .9); /* Adjust the alpha value (0.8) for desired transparency */
  border: 1px solid #ccc;
  border-radius: 30px;
  padding: 10px;
  text-align: center;
  font-size: 14px;
  box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.5);
}
.textarea {
  width: 400px;
}

.checkbox {
  margin: 0 10px;
}

.diagnosebutton {
  padding: 20px;
}

.start-diagnosis-btn {
  color: white !important;
  height:60px !important;
  width: 400px !important;
  font-size: 17px !important;
}


.start-diagnosis-btn:hover {
  background-color: #ff9275 !important;
}

.container {
  margin: 20px;
  display: flex;
  justify-content: center;
}

.v-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.midq{
  display: flex;
  flex-direction: column;
  align-items: left;
}
</style>
