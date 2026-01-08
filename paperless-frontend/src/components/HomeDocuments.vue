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
        <n-card size="small" hoverable class="cursor-pointer" @click="goToDetail(doc)">
          <!-- Kopf: Titel links, Actions rechts -->
          <div class="card-head">
            <div class="title" >{{ doc.originalFilename }}</div>
            <div class="actions">
              <n-tooltip trigger="hover">
                <template #trigger>
                  <n-button
                    quaternary
                    circle
                    size = "small"
                    @click.stop="onClickEdit(doc)"
                    >
                    <n-icon>
                      <create-outline />
                    </n-icon>
                  </n-button>
                </template>
                edit
              </n-tooltip>
              <n-tooltip trigger="hover">
                <template #trigger>
                  <n-button
                    quaternary
                    circle
                    size="small"
                    :loading="deletingId === doc.id"
                    @click.stop="onClickDelete(doc)"
                  >
                    <n-icon>
                      <trash-outline />
                    </n-icon>
                  </n-button>
                </template>
                Löschen
              </n-tooltip>
              <n-tooltip trigger="hover">
                <template #trigger>
                  <n-button
                      quaternary
                      circle
                      size="small"
                      @click.stop="onClickDownload(doc)"
                  >
                    <n-icon>
                      <arrow-down-circle-sharp/>
                    </n-icon>
                  </n-button>
                </template>
                download
              </n-tooltip>
            </div>
          </div>

          <!-- Body -->
          <n-space vertical size="small">
            <n-tag type="info" size="small">{{ getExtension(doc.originalFilename)|| 'unbekannt' }}</n-tag>
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
  <n-modal
      v-model:show="showEditModal"
      preset="dialog"
      title="Dokument bearbeiten"
      positive-text="Speichern"
      negative-text="Abbrechen"
      @positive-click="saveEdit"
      @negative-click="showEditModal = false"
  >
    <n-space vertical size="large">
      <div>
        <n-text strong>Neuer Name:</n-text>
        <n-input v-model:value="newName" placeholder="Dokumentname" />
      </div>
    </n-space>
  </n-modal>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import {
  NCard, NGrid, NGridItem, NSpace, NTag, NText, NEmpty, NButton, NSkeleton,
  NTooltip, NIcon, useDialog, useMessage, NModal, NInput
} from 'naive-ui'
import { TrashOutline as TrashOutline } from '@vicons/ionicons5'
import CreateOutline from '@vicons/ionicons5/CreateOutline'
import ArrowDownCircleSharp from '@vicons/ionicons5/ArrowDownCircleSharp'
import { deleteDocument, updateDocument, downloadDocument} from '@/api/documents.js'
import { useRouter } from 'vue-router'
import { useDocumentsStore } from '@/stores/documents'
import { storeToRefs } from 'pinia'

const router = useRouter()
const store = useDocumentsStore()
const { filteredDocuments: documents, loading } = storeToRefs(store)

const deletingId = ref(null)
const editingDoc = ref(null)
const newName = ref('')

const dialog = useDialog()
const message = useMessage()

const showEditModal = ref(false)

// simple Breakpoints
const gridCols = computed(() => {
  const w = window.innerWidth
  if (w < 640) return 1
  if (w < 900) return 2
  if (w < 1200) return 3
  return 4
})

onMounted(() => {
    store.fetchDocuments()
})

function goToDetail(doc) {
  router.push(`/detail/${doc.id}`)
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
        // Optimistisch aus der Liste entfernen via Store
        store.removeDocument(doc.id)
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


async function onClickDownload(doc){
  try {
    const res = await downloadDocument(doc.id);

    const blob = new Blob([res.data], { type: res.headers['content-type'] });
    const downloadUrl = URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.href = downloadUrl;
    a.download = doc.originalFilename || 'download'; // optional
    document.body.appendChild(a);
    a.click();
    a.remove();

    URL.revokeObjectURL(downloadUrl);
  } catch (e) {
    console.error("download didnt work", e);
  } finally {
    console.log("download done");
  }
}
function onClickEdit (doc) {
  editingDoc.value = doc
  newName.value = doc.originalFilename
  showEditModal.value = true
}

async function saveEdit() {
  if (!newName.value) {
    message.error('Bitte geben Sie einen Namen ein.')
    return
  }
  try {

    const updatedDocMetadata = {originalFilename: newName.value }
    console.log(updatedDocMetadata)

    const result = await updateDocument(editingDoc.value.id, updatedDocMetadata)
    
    // Update store
    store.updateDocumentLocal(result)
    
    showEditModal.value = false

  } catch (e) {
    message.error('Fehler beim Speichern')
    console.log("fehler beim Speichern")
    console.error(e)
  }
}

function formatBytes (n) {
  if (n == null) return '—'
  const units = ['B','KB','MB','GB','TB']
  let i = 0, v = n
  while (v >= 1024 && i < units.length - 1) { v /= 1024; i++ }
  return `${v.toFixed(v < 10 && i > 0 ? 1 : 0)} ${units[i]}`
}

function getExtension(filename) {
  let extension = filename.split('.').pop();
  return extension;
}

function formatDate (s) {
  if (!s) return '—'
  try { return new Date(s).toLocaleString() } catch { return s }
}


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
  overflow: hidden;
}
.title {
  font-weight: 600;
  line-height: 1.2;
  max-width: 70%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.actions {
  display: flex;
  gap: .25rem;
}

:deep(.n-tag) {
  max-width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
}


</style>
