<script setup>

</script>

<template>
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
</template>

<style scoped>
.nav-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
}
.app-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #334155;

}
.nav-links {
  font-size: 1.1rem;
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

</style>