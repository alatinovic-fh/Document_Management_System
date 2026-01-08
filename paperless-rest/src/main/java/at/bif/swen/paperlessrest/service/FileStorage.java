package at.bif.swen.paperlessrest.service;

public interface FileStorage {
    void upload(String filename, byte[] content);
    byte[] download(String filename);
    void delete(String filename);
    void rename(String oldFilename, String newFilename);
    String getPresignedUrl(String objectKey);

}
