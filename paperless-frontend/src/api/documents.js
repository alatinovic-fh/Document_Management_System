import apiClient from "./axios.js";

export async function createDocument(doc) {
    const res = await apiClient.post("/documents", doc);
    return res.data;
}

export async function getDocument(id) {
    const res = await apiClient.get(`/documents/${id}`);
    return res.data;
}

export async function listDocuments() {
    const res = await apiClient.get(`/documents`)
    return res.data
}

export async function deleteDocument(id) {
  const response = await apiClient.delete(`/documents/${id}`);
  return response.data;
}

export async function updateDocument(id, doc) {
    const response = await apiClient.patch(`/documents/${id}`, doc);
    return response.data;
}
