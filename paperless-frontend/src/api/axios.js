import axios from "axios";

const apiClient = axios.create({
  baseURL: "/api/v1",
});

apiClient.interceptors.request.use((config) => {
  // Für Multipart-Requests
    if (config.data instanceof FormData) {
    if (config.headers) {
      delete config.headers["Content-Type"];
    }
    config.transformRequest = (d) => d;
    return config;
  }

  // Für normale JSON-Requests:
  if (config.headers && !config.headers["Content-Type"]) {
    config.headers["Content-Type"] = "application/json";
  }
  if (config.data && typeof config.data !== "string") {
    config.data = JSON.stringify(config.data);
  }
  return config;
});

export default apiClient;