package at.bif.swen.paperlessrest.service.exception;

public class DuplicateDocumentNameException extends RuntimeException {
    public DuplicateDocumentNameException(String name) {
        super("Document with name" + name + "already exists");
    }
}
