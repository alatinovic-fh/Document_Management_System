<script setup>
import DocumentForm from '@/components/DocumentForm.vue'
import DocumentDetails from '@/components/DocumentDetails.vue'
import NavBar from '@/components/NavBar.vue'
</script>

<template>
  <NavBar/>
  <DocumentForm/>
  <DocumentDetails/>



  <div class="p-8 bg-blue-100 rounded-xl">
    <h1 class="text-3xl font-bold text-blue-700">
      Tailwind funktioniert 🎉
    </h1>
    <p class="mt-2 text-gray-600">
      Wenn du diesen Text blau hinterlegt siehst, ist Tailwind aktiv.
    </p>
  </div>


</template>


