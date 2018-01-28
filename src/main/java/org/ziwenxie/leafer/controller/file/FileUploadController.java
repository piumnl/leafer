package org.ziwenxie.leafer.controller.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class FileUploadController {

    private Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${upload.folder}")
    private String UPLOADED_FOLDER;

    @Value("${upload.server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private String port;

    private final ResourceLoader resourceLoader;

    @Autowired
    public FileUploadController(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/img/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> getFile(@PathVariable String filename) {
        try {
            String location = "file:" + Paths.get(UPLOADED_FOLDER, filename).toAbsolutePath().toString();
            return ResponseEntity.ok(resourceLoader.getResource(location));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Single file upload
    @PostMapping("/api/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        logger.debug("Single file upload!");

        if (multipartFile.isEmpty()) {
            return new ResponseEntity<>("Please select a file!", HttpStatus.OK);
        }

        try {
            String randomPath = saveUploadedFiles(Collections.singletonList(multipartFile));
            return new ResponseEntity<>(serverAddress + "/img/" + randomPath, new HttpHeaders(), HttpStatus.OK);

        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Multiple file upload
    @PostMapping("/api/upload/multi")
    public ResponseEntity<?> uploadFileMulti(@RequestParam("files") MultipartFile[] multipartFiles) {
        logger.debug("Multiple file upload!");

        String uploadedFileName =
                Arrays.stream(multipartFiles)
                      .map(MultipartFile::getOriginalFilename)
                      .filter(x -> !StringUtils.isEmpty(x))
                      .collect(Collectors.joining(" , "));

        if (StringUtils.isEmpty(uploadedFileName)) {
            return new ResponseEntity<>("please select a file!", HttpStatus.OK);
        }

        try {
            saveUploadedFiles(Arrays.asList(multipartFiles));
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(uploadedFileName, HttpStatus.OK);
    }

    // Save single file
    private String saveUploadedFiles(List<MultipartFile> files) throws IOException {
        if (Files.notExists(Paths.get(UPLOADED_FOLDER))) {
            init();
        }

        String randomPath = "";

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue; //next pls
            }

            byte[] bytes = file.getBytes();

            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);

            randomPath +=  generateRandomPath() + "." + suffix;

            Path path = Paths.get(UPLOADED_FOLDER + randomPath);
            Files.write(path, bytes);

            logger.info("save upload file: {}", randomPath);
        }

        return randomPath;
    }

    private void init() {
        try {
            Files.createDirectory(Paths.get(UPLOADED_FOLDER));
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage", e);
        }
    }

    public static String generateRandomPath() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
