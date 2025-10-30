<template>
  <n-card title="📁 Alle Dokumente" class="max-w-6xl mx-auto mt-8">
    <n-grid :cols="gridCols" :x-gap="16" :y-gap="16">
      <!-- Skeletons während Laden -->
      <n-grid-item v-for="i in (loading ? 8 : 0)" :key="'sk-'+i">
        <n-card size="small">
          <n-skeleton text :repeat="1" style="width: 60%" />
          <n-skeleton text :repeat="2" />
        </n-card>
      </n-grid-item>

      <!-- Dokument-Karten -->
      <n-grid-item v-for="doc in documents" :key="doc.id">
        <n-card size="small" hoverable>
          <!-- Kopf: Titel links, Actions rechts -->
          <div class="card-head">
            <div class="title">{{ doc.originalFilename }}</div>
            <div class="actions">
              <n-tooltip trigger="hover">
                <template #trigger>
                  <n-button
                    quaternary
                    circle
                    size="small"
                    :loading="deletingId === doc.id"
                    @click="onClickDelete(doc)"
                  >
                    <n-icon>
                      <trash-outline />
                    </n-icon>
                  </n-button>
                </template>
                Löschen
              </n-tooltip>
            </div>
          </div>

          <!-- Body -->
          <n-space vertical size="small">
            <n-tag type="info" size="small">{{ doc.contentType || 'unbekannt' }}</n-tag>
            <n-text depth="3">Größe: {{ formatBytes(doc.size) }}</n-text>
            <n-text depth="3">Erstellt: {{ doc.uploadDate }}</n-text>
          </n-space>

          <!-- Footer (optional Öffnen-Button, wenn du eine URL hast) -->
          <template #action>
            <n-button
              v-if="doc.previewUrl"
              tag="a"
              :href="doc.previewUrl"
              target="_blank"
              size="small"
            >
              Öffnen
            </n-button>
          </template>
        </n-card>
      </n-grid-item>
    </n-grid>

    <n-empty
      v-if="!loading && documents.length === 0"
      description="Keine Dokumente vorhanden."
      class="mt-4"
    />
  </n-card>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import {
  NCard, NGrid, NGridItem, NSpace, NTag, NText, NEmpty, NButton, NSkeleton,
  NTooltip, NIcon, useDialog, useMessage
} from 'naive-ui'
import { TrashOutline as TrashOutline } from '@vicons/ionicons5'
import { listDocuments, deleteDocument } from '@/api/documents.js'

const loading = ref(false)
const documents = ref([])
const deletingId = ref(null)

const dialog = useDialog()
const message = useMessage()

// simple Breakpoints
const gridCols = computed(() => {
  const w = window.innerWidth
  if (w < 640) return 1
  if (w < 900) return 2
  if (w < 1200) return 3
  return 4
})

async function fetchDocuments () {
  loading.value = true
  try {
    documents.value = await listDocuments()
    console.log(documents)
  } catch (e) {
    console.error(e)
    message.error(e?.message || 'Fehler beim Laden der Dokumente')
  } finally {
    loading.value = false
  }
}

function onClickDelete (doc) {
  dialog.warning({
    title: 'Löschen bestätigen',
    content: `Dokument „${doc.originalFilename}” wirklich löschen?`,
    positiveText: 'Löschen',
    negativeText: 'Abbrechen',
    async onPositiveClick () {
      deletingId.value = doc.id
      try {
        await deleteDocument(doc.id)
        // Optimistisch aus der Liste entfernen
        documents.value = documents.value.filter(d => d.id !== doc.id)
        message.success('Dokument gelöscht')
      } catch (e) {
        console.error(e)
        message.error(e?.message || 'Fehler beim Löschen')
      } finally {
        deletingId.value = null
      }
    }
  })
}

// Utils
function formatBytes (n) {
  if (n == null) return '—'
  const units = ['B','KB','MB','GB','TB']
  let i = 0, v = n
  while (v >= 1024 && i < units.length - 1) { v /= 1024; i++ }
  return `${v.toFixed(v < 10 && i > 0 ? 1 : 0)} ${units[i]}`
}
function formatDate (s) {
  if (!s) return '—'
  try { return new Date(s).toLocaleString() } catch { return s }
}

onMounted(fetchDocuments)
</script>

<style scoped>
.max-w-6xl { max-width: 1152px; }
.mx-auto { margin-left: auto; margin-right: auto; }
.mt-8 { margin-top: 2rem; }

.card-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: .5rem;
}
.title {
  font-weight: 600;
  line-height: 1.2;
}
.actions {
  display: flex;
  gap: .25rem;
}
</style>
