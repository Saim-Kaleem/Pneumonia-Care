<template>
    <div class="container">
      <div class="outer-rectangle-box">
        <div class="rectangle-box">
          <img src="@/assets/lung-cancer.png" alt="Description of the image" class="boredlogo">
          <div class="app-name">Pneumonia Care</div>
          <v-text-field v-model="username" class="firstnametextfield" max-width="344" hide-details="auto" label="Full Name"></v-text-field>
          <v-text-field v-model="password" class="passwordtextfield" max-width="344" hint="Enter your password to access this website" label="Password" type="password"></v-text-field>
          
          <v-btn class="login-button" @click="checkLogin" height="60px">
            Login
          </v-btn>
          
          <p class="text">If you already have an account <button class="switchtologin" @click="switchToSignup">
            <u>Sign-up!</u>
          </button></p>
          
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { setUSERNAME } from '../main';
  
  const router = useRouter();
  const username = ref('');
  const password = ref('');
  
  const navigateToChoosing = () => {
    router.push('/panel');
  }
  
  const checkLogin = async () => {
    try {
      const response = await fetch('http://localhost:8080/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          username: username.value,
          password: password.value
        })
      });
  
      if (response.ok) {
        const data = await response.json();
        if (data.status === 'ok') {
          setUSERNAME(username.value)
  
          navigateToChoosing();
        } else {
          // Login failed
          console.error('Failed to login:', data.message);
        }
      } else {
        console.error('Failed to login:', response.statusText);
      }
    } catch (error) {
      console.error('Error:', error);
    }
  }
  
  const switchToSignup = () => {
    router.push('/login');
  }
  </script>
  
  <style scoped>
  .outer-rectangle-box {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    width: 500px;
    height: 650px;
    background-color: rgba(255, 255, 255, 0.9); /* Adjust the alpha value (0.8) for desired transparency */
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
  
  .login-button {
    margin-top: 45px;
    border-radius: 17px;
    background-color: #f3719c;
    font-family: 'Roboto', sans-serif;
    font-size: 18px;
    width: 320px;
    height: 100px; 
    color: #ffffff;
    transition: background-color 0.3s ease;
  }
  
  .login-button:hover {
    background-color: #ff9275;
  }
  
  .firstnametextfield {
    margin-top: 30px;
  }
  
  .passwordtextfield {
    margin-top: 30px;
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
  