<template>
  <div class="container">
    <v-app-bar class="appbar">
      <div class="profile-container">
        <img src="@/assets/lung-cancer2.png" alt="Description of the image" class="boredlogo">
        <div class="title">Pneumonia Care</div>
      </div>
      <v-spacer></v-spacer>
      <div class="align">
        <div class="f">
          <v-btn color="#f3719c" variant="outlined" class="diag" @click="processDiagnosis">Diagnose</v-btn>
        </div>
        <v-btn color="#f3719c" variant="outlined" class="btpadd" @click="logout">Logout</v-btn>
      </div>
    </v-app-bar>

    <div class="white-container">
      <div class="pf">
        <v-avatar class="pfp" color="grey-darken-3" :image="avatarUrl"></v-avatar>
      </div>
    </div>

    <div class="reports-chart-container">
      <div class="reports-container">
        <div class="font">Recent Reports</div>
        <div class="reports">
          <v-card v-for="(item, index) in allData" :key="index" class="report">
            <v-img height="200px" src="@/assets/report.jpg" cover></v-img>
            <v-card-title>Report {{ index + 1 }}</v-card-title>
            <v-card-actions>
              <v-btn @click="openForm(item)" color="#f3719c" text>Check Report</v-btn>
              <v-spacer></v-spacer>
            </v-card-actions>
          </v-card>
        </div>
      </div>
      <div class="chart-container">
        <h1 class="font2">Past Predictions</h1>
        <canvas id="pneumoniaChart" width="300" height="300"></canvas>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import router from '../router'; // Ensure this path is correct
import { Chart, registerables } from 'chart.js';
import { setREPORTID, SETDATA } from '../main.js'; // Ensure the correct path to the main.js

// Register Chart.js components
Chart.register(...registerables);

// State
const USERNAME = ref(localStorage.getItem('USERNAME'));
const AGE = ref(localStorage.getItem('AGE'));
const EMAIL = ref(localStorage.getItem('EMAIL'));
const show = ref(false);
const allData = ref([]);

// Avatar URL
const avatarUrl = 'https://avataaars.io/?avatarStyle=Transparent&topType=ShortHairShortCurly&accessoriesType=Prescription02&hairColor=Black&facialHairType=Blank&clotheType=Hoodie&clotheColor=White&eyeType=Default&eyebrowType=DefaultNatural&mouthType=Default&skinColor=Light';

// Chart instance
let pneumoniaChart = null;

// Methods
const logout = () => {
  console.log('User logged out');
  router.push('/signup');
};

const openForm = (reportid) => {
  SETDATA(JSON.parse(reportid.report));
  router.push('/report');
};

// Chart data and configuration
const setupChart = () => {
  const chartElement = document.getElementById('pneumoniaChart');
  if (chartElement) {
    const ctx = chartElement.getContext('2d');
    pneumoniaChart = new Chart(ctx, {
      type: 'line',
      data: {
        labels: [], // Initialize with empty labels
        datasets: [{
          label: 'Pneumonia Predictions',
          data: [], // Initialize with empty data
          borderColor: '#ffffff',
          backgroundColor: '#ffffff',
          fill: false,
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        scales: {
          x: {
            display: true,
            grid: {
              color: '#ffffff',
              borderColor: '#ffffff'
            },
            ticks: {
              color: '#ffffff'
            },
            title: {
              display: true,
              text: 'Date/Time',
              color: '#ffffff'
            }
          },
          y: {
            display: true,
            grid: {
              color: '#ffffff',
              borderColor: '#ffffff'
            },
            ticks: {
              color: '#ffffff'
            },
            title: {
              display: true,
              text: 'Percentage %',
              color: '#ffffff'
            }
          }
        },
        plugins: {
          legend: {
            labels: {
              color: '#ffffff'
            }
          }
        },
        layout: {
          backgroundColor: '#ffffff'
        }
      }
    });
  } else {
    console.error('Element with ID "pneumoniaChart" not found.');
  }
};

const updateChart = (labels, data) => {
  if (pneumoniaChart) {
    pneumoniaChart.data.labels = labels;
    pneumoniaChart.data.datasets[0].data = data;
    pneumoniaChart.update();
  }
};

const processDiagnosis = () => {
  router.push('/diagnosis');
};

const loadData = async () => {
  try {
    const response = await fetch('http://localhost:8080/getreport', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
    });
    if (!response.ok) {
      throw new Error('Network response was not ok');
    }
    const data = await response.json();
    console.log(data);
    if (data.status === 'ok') {
      allData.value = data.message; // Ensure the JSON structure matches this
      allData.value = allData.value.filter((e)=>{
        return e.userid == USERNAME.value;
      })

      let arrivingData = [];
      for(let i in allData.value) {
        arrivingData.push(JSON.parse(allData.value[i].report).percPneumonia);
      }

      
      // Update the chart with the new data
      const labels = allData.value.map((_, index) => `Report ${index + 1}`);
      updateChart(labels, arrivingData);
      
    } else {
      console.error('Error in response:', data.message);
    }
  } catch (error) {
    console.error('There was a problem with the fetch operation:', error);
  }
};

onMounted(() => {
  setupChart();
  loadData();
});
</script>

<style scoped>
.container {
  width: 100vw;
  height: 100vh;
  background-size: cover;
  background-position: center;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: fixed;
  overflow-y: auto;
  background: linear-gradient(45deg, #ff9275, #f3719c);
}

.f {
  padding-right: 20px;
}

.align {
  display: flex;
  flex-direction: row;
  padding-right: 20px;
}

.title {
  font-size: 30px;
  font-weight: bold;
  color: #ff9275;
  margin-left: 15px;
}

.pf {
  padding-bottom: 70px;
}

body {
  overflow-y: scroll;
}

.appbar {
  background-color: #f5f5f5;
  padding-left: 10px;
  height: 65px;
  display: flex;
  align-items: center;
  width: 100%; /* Make the app bar span the full width */
}

.profile-container {
  display: flex;
  align-items: center;
}

.pfp {
  margin-left: 10px;
  height: 100px !important;
  width: 100px !important;
}

.info {
  padding-left: 10px;
  display: flex;
  flex-direction: column;
}

.btpadd {
  margin-left: auto;
}

.diag {
  margin-left: 20px;
}

.white-container {
  width: 100%; 
  background-color: white;
  height: 150px; 
  padding-top: 90px;
}

.reports-chart-container {
  display: flex;
  flex-direction: row;
  margin-top: 20px;
}

.reports-container {
  display: flex;
  flex-direction: column;
  margin-right: 20px;
}

.font {
  font-size: 30px;
  font-weight: bold;
  margin-bottom: 40px;
  align-self: center;
  color: #ffffff;
}

.reports {
  display: flex;
  flex-direction: row;
  gap: 15px;
}

.report {
  width: 300px;
  height: 300px; /* Set a fixed height for the cards */
}

.chart-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 490px;
  padding-top: 40px;
  height: 375px; 
  padding-left: 20px;
}

.font2 {
  font-size: 30px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #ffffff;
}

.boredlogo {
  width: 50px;
  height: 50px;
}
</style>
