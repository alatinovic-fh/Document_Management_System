<template>
  <n-card class="max-w-4xl mx-auto mt-8" title="📄 Dokument Details">
    <n-space vertical size="large" v-if="doc">
      <n-text><strong>Name:</strong> {{ doc.originalFilename }}</n-text>
      <n-text><strong>Typ:</strong> {{ getExtension(doc.originalFilename) }}</n-text>
      <n-text><strong>Größe:</strong> {{ formatBytes(doc.size) }}</n-text>
      <n-text><strong>Erstellt:</strong> {{ formatDate(doc.uploadDate) }}</n-text>

      <n-divider />

      <n-text strong>Zusammenfassung</n-text>
      <n-card size="small">
        <n-text v-if="doc.summary">
          {{ doc.summary }}
        </n-text>
        <n-text depth="3" v-else>
          Keine Zusammenfassung vorhanden.
        </n-text>
      </n-card>
    </n-space>

    <n-skeleton v-else text :repeat="5" />
  </n-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getDocument } from '@/api/documents.js'

const route = useRoute()
const doc = ref(null)

onMounted(async () => {
  doc.value = await getDocument(route.params.id)
})

function formatBytes(n) {
  if (!n) return '—'
  const units = ['B','KB','MB','GB']
  let i = 0
  while (n >= 1024 && i < units.length - 1) {
    n /= 1024
    i++
  }
  return `${n.toFixed(1)} ${units[i]}`
}

function formatDate(d) {
  return new Date(d).toLocaleString()
}

function getExtension(filename) {
  let extension = filename.split('.').pop();
  return extension;
}
</script>
