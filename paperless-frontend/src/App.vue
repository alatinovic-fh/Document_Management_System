

<template>
  <n-message-provider>
    <n-dialog-provider>
      <n-layout>
        <n-layout-header bordered class="nav-header">
          <nav-bar/>

        </n-layout-header>

        <n-layout-content content-style="padding: 2rem;">
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

import DocumentForm from '@/components/DocumentForm.vue'
import DocumentDetails from '@/components/DocumentDetails.vue'
import NavBar from '@/components/NavBar.vue'

</script>

<style scoped>
.nav-header {
  background-color: #f8fafc;
  height: 64px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

}
</style>
