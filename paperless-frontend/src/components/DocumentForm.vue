<template>
  <n-card title="📄 Neues Dokument" class="max-w-md mx-auto mt-10 shadow-lg">
    <n-form @submit.prevent="onSubmit" :model="form" class="space-y-3">
      <n-form-item label="Dateiname">
        <n-input
          v-model:value="form.originalFilename"
          placeholder="z. B. Bericht.pdf"
        />
      </n-form-item>

      <n-form-item label="Content Type">
        <n-input v-model:value="form.contentType" placeholder="application/pdf" />
      </n-form-item>

      <n-form-item label="Größe (Bytes)">
        <n-input-number
          v-model:value="form.size"
          placeholder="12345"
          style="width: 100%"
        />
      </n-form-item>

      <n-form-item>
        <n-button
          type="primary"
          :loading="loading"
          attr-type="submit"
          block
        >
          {{ loading ? "Speichern..." : "Speichern" }}
        </n-button>
      </n-form-item>
    </n-form>

    <n-alert
      v-if="response"
      type="success"
      title="Gespeichert!"
      class="mt-4"
      show-icon
    >
      <div><strong>ID:</strong> {{ response.id }}</div>
      <div><strong>Dateiname:</strong> {{ response.originalFilename }}</div>
    </n-alert>
  </n-card>
</template>

<script setup>
import { ref } from "vue";
import {
  NCard,
  NForm,
  NFormItem,
  NInput,
  NInputNumber,
  NButton,
  NAlert,
} from "naive-ui";
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
    window.$message?.error("Fehler beim Speichern: " + err.message);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.max-w-md {
  max-width: 420px;
}
.mx-auto {
  margin-left: auto;
  margin-right: auto;
}
.mt-10 {
  margin-top: 2.5rem;
}
.space-y-3 > * + * {
  margin-top: 0.75rem;
}
</style>
