<template>
  <div class="container">
    <div v-if="isSignup" class="outer-rectangle-box">
      <div class="rectangle-box">
        <img src="@/assets/lung-cancer.png" alt="Description of the image" class="boredlogo">
        <div class="app-name">Pneumonia Care</div>
        <v-text-field v-model="username" class="firstnametextfield" max-width="344" hide-details="auto" label="Full Name"></v-text-field>
        <div class="horizontal-fields">
          <v-text-field v-model="nationality" class="nationality" max-width="165" label="Nationality"></v-text-field>
          <v-text-field v-model="age" class="age" max-width="165" label="Age"></v-text-field>
        </div>
        <v-text-field v-model="email" class="emailtextfield" max-width="344" hide-details="auto" label="Email address" placeholder="johndoe@gmail.com" type="email"></v-text-field>
        <v-text-field v-model="password" class="passwordtextfield" max-width="344" hint="Enter your password to access this website" label="Password" type="password"></v-text-field>
        <v-btn class="signup-button" height="60px" @click="registerUser()">
          Sign Up
        </v-btn>
        <p class="text">If you already have an account <button class="switchtologin" @click="navigateToSignup()">
            <u>Login!</u>
          </button></p>
      </div>
    </div>
  </div>
</template>

<script setup>
import Router from '../router';
import { ref } from 'vue';

const isSignup = ref(true);
const username = ref('');
const password = ref('');
const email = ref('');
const nationality = ref('');
const age = ref('');

const navigateToChoosing = () => {
  Router.push('/panel');
}

const navigateToSignup = () => {
  Router.push('/signup');
}

const registerUser = async () => {
  try {
    const response = await fetch('http://localhost:8080/registerUser', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        name: username.value,
        age: age.value,
        nationality: nationality.value,
        email: email.value,
        password: password.value
      })
    });

    const data = await response.json();
    console.log(data);

    if (data.status === 'ok') {
      localStorage.setItem('USERNAME', data.username);
      localStorage.setItem('AGE', data.age);
      localStorage.setItem('EMAIL', data.email);
      Router.push('/panel');
    } else {
      console.error('Failed to register:', data.message);
    }
  } catch (error) {
    console.error('Error:', error);
  }
};
</script>

<style scoped>
.outer-rectangle-box {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  align-items: center;
  width: 500px;
  height: 770px;
  background-color: rgba(255, 255, 255, 0.9);
  border: 1px solid #ccc;
  border-radius: 30px;
  padding: 20px;
  text-align: center;
  font-size: 14px;
  box-shadow: 0px 0px 10px 0px rgba(0, 0, 0, 0.5);
}

.container {
  width: 100vw;
  height: 100vh;
  background-size: cover;
  background-position: center;
  display: flex;
  justify-content: center;
  align-items: center;
  position: fixed;
  overflow-y: auto;
  background: linear-gradient(45deg, #ff9275, #f3719c);
}

.boredlogo {
  margin-top: 20px;
  width: 150px;
  height: 150px;
}

.app-name {
    font-size: 30px;
    font-weight: bold;
    margin-top: 10px;
    color: #f3719c;
}

.signup-button {
  margin-top: 20px;
  border-radius: 17px;
  background-color:#f3719c;
  font-family: 'Roboto', sans-serif;
  font-size: 18px;
  width: 320px;
  height: 100px;
  color: #ffffff;
  transition: background-color 0.3s ease;
}

.signup-button:hover {
  background-color: #ff9275;
}

.firstnametextfield,
.emailtextfield,
.passwordtextfield {
  margin-top: 20px;
}

.horizontal-fields {
  display: flex;
  justify-content: space-between;
  width: 344px;
  margin-top: 30px;
}

.age,
.nationality {
  margin-top: 0;
}

.text {
  margin-top: 10px;
}

.switchtologin {
  margin-top: 10px;
  color: blue;
  cursor: pointer;
}
</style>
