package com.asad.geniusanalysis.controller;

import com.asad.geniusanalysis.Scraper;
import com.asad.geniusanalysis.service.DatabaseManager;
import org.apache.commons.io.FileUtils;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
@CrossOrigin("http://localhost:4200")
public class ListRestController {
    @Autowired
    public DatabaseManager databaseManager;

    // localhost:8080/persistLink[...]
    @RequestMapping(path = "/persistLink/{link}", method = RequestMethod.GET)
    public ResponseEntity<String> test(@PathVariable String link) {
        String baseUrl = "https://genius.com/";

        String geniusUrl = baseUrl + link;

        String title;

        try {
            title = Scraper.createSongJSON(geniusUrl, "temp");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        File file = new File("temp/" + title + ".json");

        // This is the source of the unexpected JSON error. Weird thing with the strings.
        return new ResponseEntity<String>(file.getPath(), HttpStatus.OK);
    }

    @RequestMapping(path = "/getTemp", method = RequestMethod.GET)
    public ResponseEntity<Resource> download() throws FileNotFoundException {
        File dir = new File("temp/");

        File file = dir.listFiles()[0];

        ContentDisposition contentDisposition = ContentDisposition.builder("inline")
                .filename(file.getName())
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(contentDisposition);

        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.APPLICATION_JSON)
                .body(resource);
    }

    @RequestMapping(path = "/clearTemp", method = RequestMethod.GET)
    public void clearTemp() throws IOException {
        File dir = new File("temp/");
        FileUtils.cleanDirectory(dir);
    }

}
