<template>
  <div>Hello</div>
  <v-btn class="sem" @click="hello">Button</v-btn>
  <div >{{ x }}</div>
</template>

<script setup>

import { ref, onMounted } from 'vue';
import router from '@/router';

const x = ref(0);

const hello = () => {
  x.value++;
  console.log(x.value);
  router.push("/panel");
}

onMounted(()=>{
console.log("This has Loaded");
})

</script>

<style scoped>
.sem {
color: white;
background-color: red;
width: 200px;
height: 80px !important;
}
</style>