<script setup>
import { NInput } from 'naive-ui'
import { ref } from 'vue'
import { useDocumentsStore } from '@/stores/documents'

const store = useDocumentsStore()
const searchValue = ref('')

function handleSearch() {
  store.performSearch(searchValue.value)
}

function handleInput(value) {
    if (value === '') {
        handleSearch()
    }
}
</script>

<template>
  <div class="search-bar-container">
    <n-input
        v-model:value="searchValue"
        type="text"
        placeholder="Suchen..."
        @keyup.enter="handleSearch"
        @update:value="handleInput"
        class="search-input"
        round
    >
      <template #prefix>
       🔍
      </template>
    </n-input>
  </div>
</template>

<style scoped>
.search-bar-container {
  display: flex;
  justify-content: center;
  width: 100%;
}

.search-input {
  max-width: 600px;
  width: 100%;
}
</style>
