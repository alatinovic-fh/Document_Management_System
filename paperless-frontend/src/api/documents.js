import apiClient from "./axios.js";

export async function createDocument(doc) {
    const res = await apiClient.post("/documents", doc);
    return res.data;
}

export async function getDocument(id) {
    const res = await apiClient.get(`/documents/${id}`);
    return res.data;
}
