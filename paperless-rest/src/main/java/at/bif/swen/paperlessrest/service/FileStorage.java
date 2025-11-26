package at.bif.swen.paperlessrest.service;

public interface FileStorage {
    void upload(String filename, byte[] content);
    byte[] download(String filename);
}
