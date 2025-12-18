import { createRouter, createWebHistory } from 'vue-router'
import DocumentForm from '@/components/DocumentForm.vue'
import HomeDocuments from '@/components/HomeDocuments.vue'
import DocumentInsight from '@/components/DocumentInsight.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/home', // default route
    },
    {
      path: '/home',
      name: 'HomeDocuments',
      component: HomeDocuments
    },
    {
      path: '/create',
      name: 'CreateDocument',
      component: DocumentForm,
    },
    {
      path: '/detail/:id',
      name: 'DocumentInsight',
      component: DocumentInsight,
      props: true,
    },
  ],
})

export default router
