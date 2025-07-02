package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import vn.JobHunter.domain.respone.file.ResUploadFileDto;
import vn.JobHunter.service.FileService;
import vn.JobHunter.util.exception.SotorageExcpetion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class FileController {
    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @Value("${thanh.upload-file.base-uri}")
    private String baseUri;

    @PostMapping("/files")
    public ResponseEntity<ResUploadFileDto> handleUpLoadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("folder") String folder)
            throws URISyntaxException, IOException, SotorageExcpetion {

        // validation
        if (file == null || file.isEmpty()) {
            throw new SotorageExcpetion("File is empty. Please upload a file");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtension = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValid = allowedExtension.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if (!isValid) {
            throw new SotorageExcpetion("Invalid file extension. Only allow " + allowedExtension.toString());

        }
        // create a directory
        this.fileService.createDirectory(baseUri + folder);

        // store file
        String fileUpload = this.fileService.store(file, folder);

        ResUploadFileDto res = new ResUploadFileDto(fileUpload, Instant.now());
        // return file.getOriginalFilename() + folder;
        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/files")
    public ResponseEntity<Resource> handleDownloadFile(
            @RequestParam(name = "fileName", required = false) String fileName,
            @RequestParam(name = "folder", required = false) String folder)
            throws SotorageExcpetion, URISyntaxException, FileNotFoundException {
        if (fileName == null || folder == null) {
            throw new SotorageExcpetion("File name or folder is empty");
        }

        // check file
        long fileLength = this.fileService.getLengthFile(fileName, folder);
        if (fileLength == 0) {
            throw new SotorageExcpetion("File not found");
        }
        // download file
        InputStreamResource resource = this.fileService.getResource(fileName, folder);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
