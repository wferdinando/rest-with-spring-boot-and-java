package br.com.wfsystems.controllers.docs;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import br.com.wfsystems.data.dto.v1.UploadFileResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@Tag(name = "File Endpoint")
public interface FileControllerDocs {

    UploadFileResponseDTO uploadFile(MultipartFile file);

    List<UploadFileResponseDTO> uploadMultipleFiles(MultipartFile[] files);
    
    ResponseEntity<Resource> downloadFile(String fileName, HttpServletRequest request);
}
