

<template>
  <n-message-provider>
    <n-dialog-provider>
      <n-layout>
        <n-layout-header bordered class="nav-header">
          <nav-bar/>

        </n-layout-header>

        <n-layout-content content-style="padding: 2rem;">
            <div class="search-section">
                <search-bar />
            </div>
          <router-view />
        </n-layout-content>
      </n-layout>
    </n-dialog-provider>
  </n-message-provider>
</template>

<script setup>
import {
  NLayout,
  NLayoutHeader,
  NLayoutContent,
  NMessageProvider,
  NDialogProvider
} from "naive-ui";

import NavBar from '@/components/NavBar.vue'
import SearchBar from '@/components/SearchBar.vue'

</script>

<style scoped>
.nav-header {
  background-color: #f8fafc;
  height: 64px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

}
.search-section {
    margin-bottom: 2rem;
}
</style>
