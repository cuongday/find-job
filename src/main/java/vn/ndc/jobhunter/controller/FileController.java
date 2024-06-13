package vn.ndc.jobhunter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.ndc.jobhunter.domain.response.file.ResUploadFileDTO;
import vn.ndc.jobhunter.service.FileService;
import vn.ndc.jobhunter.util.annotation.ApiMessage;
import vn.ndc.jobhunter.util.error.StorageException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class FileController {

    @Value("${ndc.upload-file.base-uri}")
    private String baseURI;

    private final FileService fileService;

    @PostMapping("/files")
    @ApiMessage("Upload file")
    public ResponseEntity<ResUploadFileDTO> uploadFile(@RequestParam(name = "file", required = false) MultipartFile file,
                                                       @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {

        //skip validate
        if(file == null || file.isEmpty()){
            throw new StorageException("File is empty. Please upload a file.");
        }

        String fileName = file.getOriginalFilename();
        List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");

        boolean isValid = allowedExtensions.stream().anyMatch(item -> fileName.toLowerCase().endsWith(item));
        if(!isValid){
            throw new StorageException("File extension is not allowed. Please upload a file with extension: " + allowedExtensions);
        }

        // create a directory
        this.fileService.createDirectory(baseURI + folder);
        //store file
        String uploadFile = this.fileService.store(file, folder);

        ResUploadFileDTO res = new ResUploadFileDTO(uploadFile, Instant.now());

        return ResponseEntity.ok().body(res);
    }

    @GetMapping("/files")
    @ApiMessage("Download a file")
    public ResponseEntity<Resource> downloadFile(@RequestParam(name = "fileName", required = false) String fileName,
                                                 @RequestParam(name = "folder", required = false) String folder) throws URISyntaxException, StorageException, IOException {
        if(fileName == null || folder == null){
            throw new StorageException("Missing required parameters: (fileName or folder)");
        }

        //check file exists (and not a directory)
        long fileLength = this.fileService.getFileLength(fileName, folder);
        if(fileLength == 0){
            throw new StorageException("File with name = " + fileName + " not found");
        }

        InputStreamResource resource = this.fileService.getResource(fileName, folder);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentLength(fileLength)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

}
