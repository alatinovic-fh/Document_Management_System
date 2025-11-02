<template>
  <n-card title="📤 Datei hochladen" class="max-w-md mx-auto mt-10 shadow-lg">
    <n-upload
      :default-upload="false"
      :max="1"
      :accept="accept"
      :on-before-upload="beforeUpload"
      :custom-request="customRequest"
      :on-remove="onRemove"
      :disabled="loading"
      v-model:file-list="fileList"
    >
      <n-upload-dragger>
        <div class="text-center p-4">
          <div class="text-2xl mb-2">📎</div>
          <div class="font-medium">Datei hierher ziehen</div>
          <div class="text-gray-500">oder klicken, um eine Datei auszuwählen</div>
          <div class="text-xs mt-1 text-gray-400">Erlaubte Typen: {{ accept || 'alle' }}</div>
        </div>
      </n-upload-dragger>
    </n-upload>

    <div v-if="fileList.length" class="mt-4 space-y-2">
      <div class="flex items-center justify-between">
        <div>
          <div class="font-medium">{{ fileList[0].name }}</div>
          <div class="text-xs text-gray-500">{{ formatBytes(fileList[0].file?.size || 0) }}</div>
        </div>
        <n-button size="small" tertiary @click="startUpload" :disabled="loading">
          {{ loading ? 'Lädt...' : 'Hochladen' }}
        </n-button>
      </div>
    </div>

    <n-alert v-if="response" type="success" title="Gespeichert!" class="mt-4" show-icon>
      <div><strong>ID:</strong> {{ response.id }}</div>
      <div><strong>Dateiname:</strong> {{ response.originalFilename }}</div>
    </n-alert>

    <n-alert v-if="error" type="error" title="Fehler" class="mt-4" show-icon>
      {{ error }}
    </n-alert>
  </n-card>
</template>

<script setup>
import { ref } from 'vue'
import {
  NCard,
  NUpload,
  NUploadDragger,
  NAlert,
  NButton
} from 'naive-ui'
import { createDocument } from '@/api/documents.js'

const accept = '.pdf,.png,.jpg,.jpeg,.doc,.docx,.xls,.xlsx,.txt,application/pdf'

const fileList = ref([])
const loading = ref(false)
const response = ref(null)
const error = ref('')

function formatBytes (bytes) {
  if (!bytes) return '0 B'
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return `${(bytes / Math.pow(1024, i)).toFixed(1)} ${sizes[i]}`
}

function beforeUpload () {
  error.value = ''
  return true
}
async function startUpload () {
  if (!fileList.value.length) return
  const file = fileList.value[0]
  await customRequest({ file })
}

async function customRequest ({ file }) {
  loading.value = true
  response.value = null
  error.value = ''
  try {
    const formData = new FormData()
    formData.append('file', file.file)

    response.value = await createDocument(formData)
    window.$message?.success('Upload erfolgreich!')
  } catch (e) {
    console.error(e)
    const msg = e?.message || 'Unbekannter Fehler beim Upload.'
    error.value = msg
    window.$message?.error(msg)
  } finally {
    loading.value = false
  }
}

function onRemove () {
  // Reset States beim Entfernen
  response.value = null
  error.value = ''
}
</script>

<style scoped>
.max-w-md { max-width: 420px; }
.mx-auto { margin-left: auto; margin-right: auto; }
.mt-10 { margin-top: 2.5rem; }
.space-y-2 > * + * { margin-top: 0.5rem; }
</style>
