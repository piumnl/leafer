package org.ziwenxie.leafer.controller.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileUploadController {

    private final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    //Save the uploaded file to this folder
    private static String UPLOADED_FOLDER = "./src/main/resources/static/upload/";

    @Autowired
    Environment environment;

    //Single file upload
    @PostMapping("/api/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        logger.debug("Single file upload!");

        if (multipartFile.isEmpty()) {
            return new ResponseEntity("Please select a file!", HttpStatus.OK);
        }

        try {
            saveUploadedFiles(Arrays.asList(multipartFile));

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String port = environment.getProperty("server.port");
        String hostAddress =  InetAddress.getLoopbackAddress().getHostAddress();
        // String hostName =  InetAddress.getLoopbackAddress().getHostName();
        return new ResponseEntity("http://" + hostAddress + ":" + port + "/upload/" +
                multipartFile.getOriginalFilename(), new HttpHeaders(), HttpStatus.OK);
    }

    // Multiple file upload
    @PostMapping("/api/upload/multi")
    public ResponseEntity<?> uploadFileMulti(@RequestParam("files") MultipartFile[] multipartFiles) {
        logger.debug("Multiple file upload!");

        String uploadedFileName = Arrays.stream(multipartFiles).map(x -> x.getOriginalFilename())
                .filter(x -> !StringUtils.isEmpty(x)).collect(Collectors.joining(" , "));

        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity("please select a file!", HttpStatus.OK);
        }

        try {
            saveUploadedFiles(Arrays.asList(multipartFiles));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(uploadedFileName, HttpStatus.OK);
    }

    //save file
    private void saveUploadedFiles(List<MultipartFile> files) throws IOException {
        init();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
            Files.write(path, bytes);

        }
    }

    public void init() {
        try {
            Files.createDirectory(Paths.get(UPLOADED_FOLDER));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }
}

