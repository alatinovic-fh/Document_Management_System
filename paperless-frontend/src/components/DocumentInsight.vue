<template>
  <n-card class="max-w-4xl mx-auto mt-8" title="📄 Dokument Details">
    <n-space vertical size="large" v-if="doc">
      <n-text><strong>Name:</strong> {{ doc.originalFilename || 'untitled' }}</n-text>
      <n-text><strong>Typ:</strong> {{ getExtension(doc.originalFilename) || 'unknown' }}</n-text>
      <n-text><strong>Größe:</strong> {{ formatBytes(doc.size) }}</n-text>
      <n-text><strong>Erstellt:</strong> {{ formatDate(doc.uploadDate) }}</n-text>

      <n-divider />

      <n-text strong>Zusammenfassung</n-text>

      <n-card size="small">
        <n-space v-if="isSummaryLoading" align="center">
          <n-spin size="small" />
          <n-text depth="3">
            Zusammenfassung wird erstellt …
          </n-text>
        </n-space>

        <n-text v-else>
          {{ doc.summary }}
        </n-text>
      </n-card>
    </n-space>

    <n-skeleton v-else text :repeat="5" />
  </n-card>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import { getDocument } from '@/api/documents.js'

const route = useRoute()
const doc = ref(null)
let interval = null

const isSummaryLoading = computed(() => {
  return doc.value && (!doc.value.summary || doc.value.summary.trim() === '')
})

onMounted(async () => {
  await loadDocument()

  interval = setInterval(async () => {
    await loadDocument()

    if (doc.value && doc.value.summary && doc.value.summary.trim() !== '') {
      clearInterval(interval)
    }
  }, 5000)
})

onUnmounted(() => {
  clearInterval(interval)
})

async function loadDocument() {
  doc.value = await getDocument(route.params.id)
}

function formatBytes(n) {
  if (!n) return '—'
  const units = ['B', 'KB', 'MB', 'GB']
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
  if (!filename) return ''
  return filename.split('.').pop()
}
</script>

<style scoped>
.max-w-4xl {
  max-width: 56rem;
}
.mx-auto {
  margin-left: auto;
  margin-right: auto;
}
.mt-8 {
  margin-top: 2rem;
}
</style>
