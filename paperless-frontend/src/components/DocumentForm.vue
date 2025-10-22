<template>
  <div class="card">
    <h2> Neues Dokument</h2>

    <form @submit.prevent="onSubmit">
      <input type="text" v-model="form.originalFilename" placeholder="Dateiname" />
      <input type="text" v-model="form.contentType" placeholder="Content Type" />
      <input type="number" v-model="form.size" placeholder="Größe (Bytes)" />
      <button type="submit" :disabled="loading">
        {{ loading ? "Speichern..." : "Speichern" }}
      </button>
    </form>

    <div v-if="response" class="response">
      <p><strong>ID:</strong> {{ response.id }}</p>
      <p><strong>Dateiname:</strong> {{ response.originalFilename }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { createDocument } from "@/api/documents.js";


const form = ref({
  originalFilename: "",
  contentType: "",
  size: 0,
});

const response = ref(null);
const loading = ref(false);

async function onSubmit() {
  loading.value = true;
  try {
    response.value = await createDocument(form.value);
  } catch (err) {
    console.error(err);
    alert("Fehler beim Speichern: " + err.message);
  } finally {
    loading.value = false;
  }
}
</script>

<style>
.card {
  max-width: 400px;
  margin: 2rem auto;
  padding: 1.5rem;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #fff;
}

.card h2 {
  font-size: 1.25rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

form {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

input {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  padding: 0.5rem;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.response {
  margin-top: 1rem;
  padding: 0.75rem;
  background-color: #e6f9e6;
  border-radius: 6px;
}
</style>