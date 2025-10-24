

<template>
  <n-message-provider>
    <n-dialog-provider>
      <n-layout>
        <n-layout-header bordered class="nav-header">
          <div class="nav-container">
            <h1 class="app-title">📁 Dokumentenverwaltung</h1>

            <div class="nav-links">
              <router-link to="/home" class="nav-link" active-class="active">
                🏠 Home
              </router-link>
              <router-link to="/create" class="nav-link" active-class="active">
                📄 Erstellen
              </router-link>
            </div>
          </div>
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
  display: flex;
  align-items: center;
  height: 64px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
}
.nav-container {
  width: 100%;
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 1.5rem;
}
.app-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #334155;
}
.nav-links {
  display: flex;
  gap: 1.25rem;
}
.nav-link {
  color: #475569;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}
.nav-link:hover {
  color: #007bff;
}
.active {
  color: #007bff;
  font-weight: 600;
}
</style>
