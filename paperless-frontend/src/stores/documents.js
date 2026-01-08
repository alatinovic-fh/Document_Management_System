
import { defineStore } from 'pinia'
import { listDocuments, searchDocuments } from '@/api/documents'

export const useDocumentsStore = defineStore('documents', {
    state: () => ({
        documents: [],
        searchIds: [],
        searchQuery: '',
        loading: false,
        error: null
    }),

    getters: {
        filteredDocuments: (state) => {
            if (!state.searchQuery) {
                return state.documents
            }
            return state.documents.filter(doc => state.searchIds.includes(doc.id))
        }
    },

    actions: {
        async fetchDocuments() {
            this.loading = true
            this.error = null
            try {
                this.documents = await listDocuments()
            } catch (err) {
                this.error = err.message || 'Error fetching documents'
                console.error(err)
            } finally {
                this.loading = false
            }
        },

        async performSearch(query) {
            this.searchQuery = query
            if (!query) {
                this.searchIds = []
                return
            }

            this.loading = true // Optional: filtered view might not need full loading state if we want to show existing docs while searching, but let's keep it simple
            this.error = null

            try {
                this.searchIds = await searchDocuments(query)
            } catch (err) {
                this.error = err.message || 'Error searching documents'
                console.error(err)
            } finally {
                this.loading = false
            }
        },

        // Helper to remove document from local state when deleted
        removeDocument(id) {
            this.documents = this.documents.filter(d => d.id !== id)
        },

        // Helper to update document in local state
        updateDocumentLocal(updatedDoc) {
            const index = this.documents.findIndex(d => d.id === updatedDoc.id)
            if (index !== -1) {
                this.documents[index] = { ...this.documents[index], ...updatedDoc }
            }
        }
    }
})
