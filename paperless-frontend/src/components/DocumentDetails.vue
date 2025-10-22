<template>
  <button @click ="getDocs">hier is ein Knopf</button>

</template>

<script setup>
import { ref, onMounted } from 'vue';
import { getDocument } from '@/api/documents';

const docs = ref([]);
const loading = ref(true);
const error = ref('');

function displayName(d) {
  return d.originalFilename || d.id;
}

async function getDocs() {
  try {
    docs.value = await getDocument(2);
    console.log(docs.value);
  } catch (e) {
    error.value = 'Konnte Dokumente nicht laden.';
  } finally {
    loading.value = false;
  }
}
</script>

<style>

</style>