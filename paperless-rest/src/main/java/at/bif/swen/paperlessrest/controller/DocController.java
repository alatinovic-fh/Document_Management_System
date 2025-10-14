package at.bif.swen.paperlessrest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.Document;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocController {

    @GetMapping
    public ResponseEntity<String> addDocument(){
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.valueOf("text/plain"))
                .body("Document added to the database");
    }

}
