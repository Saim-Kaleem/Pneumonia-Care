<template>
  <div class="report">
    <h1 class="date">Date, Time: {{ currentDateTime }}</h1>
    <div class="content">
      <div class="percentage">Pneumonia Risk Percentage: {{ reportData.percPneumonia }}%</div>
      <div class="suggestions">
        <h2>Suggestions:</h2>
        <p>{{ reportData.suggestion }}</p>
      </div>
      <div class="hospitals">
        <h2>Relevant Hospitals:</h2>
        <ul>
          <li v-for="hospital in reportData.hospitals" :key="hospital.name">{{ hospital.name }} - {{ hospital.address }}</li>
        </ul>
      </div>
    </div>
    <div class="buttons">
      <v-btn class="back-button" @click="navigateToHome()">Back</v-btn>
      <v-btn class="save-button" @click="saveReport()">Save Report</v-btn>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, defineProps } from 'vue';
import router from '../router';
import { useRoute } from 'vue-router';
import {DATA} from '../main';

const route = useRoute();

const reportData = DATA;
const EMAIL = ref(localStorage.getItem('EMAIL'));


const currentDateTime = ref('');

const updateDateTime = () => {
  const now = new Date();
  currentDateTime.value = now.toLocaleString(); // Format date and time as per locale
};

onMounted(() => {
  console.log('Report component mounted');  
  updateDateTime(); // Update the date and time when the component is mounted
  console.log('Report data:', reportData);
});

const navigateToHome = () => {
  console.log('Navigating to home panel');
  router.push('/panel');
};

const saveReport = async () => {
  try {
    console.log(EMAIL.value);
    const response = await fetch('http://localhost:8080/report', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        email: EMAIL.value,
        report: JSON.stringify(reportData),
        time: currentDateTime.value
      })
    });

    const data = await response.json();
    console.log(data);

  } catch (error) {
    console.error('Error:', error);
  }
};

</script>

<style scoped>
.report {
  max-width: 1000px;
  margin: 0 auto;
  margin-top: 20px;
  padding: 20px;
  border: 2px solid #f3719c;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  font-family: 'Arial', sans-serif;
  color: #333;
  background-color: #fff;
}
.date {
  text-align: center;
  color: #f3719c;
  font-size: 24px;
  margin-bottom: 20px;
}
.content {
  text-align: center;
}
.percentage {
  font-size: 20px;
  color: #f3719c;
  margin-bottom: 20px;
}
.suggestions, .hospitals {
  text-align: left;
  margin: 20px 0;
}
.suggestions h2, .hospitals h2 {
  font-size: 22px;
  color: #f3719c;
  margin-bottom: 10px;
}
.suggestions p {
  background-color: #f9f9f9;
  border: 1px solid #f3719c;
  border-radius: 5px;
  padding: 10px;
  margin: 0;
}
.hospitals ul {
  list-style-type: none;
  padding: 0;
}
.hospitals li {
  background-color: #f9f9f9;
  border: 1px solid #f3719c;
  border-radius: 5px;
  padding: 10px;
  margin-bottom: 10px;
  transition: background-color 0.3s ease;
}
.hospitals li:hover {
  background-color: #f3719c;
  color: #fff;
}
.buttons {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
.save-button, .back-button {
  margin: 0 10px;
  background-color: #f3719c;
}
.save-button:hover, .back-button:hover {
  background-color: #ff9275;
}
</style>
