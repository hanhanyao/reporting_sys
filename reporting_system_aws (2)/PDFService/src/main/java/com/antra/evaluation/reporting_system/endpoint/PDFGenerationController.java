package com.antra.evaluation.reporting_system.endpoint;

import com.antra.evaluation.reporting_system.pojo.api.PDFRequest;
import com.antra.evaluation.reporting_system.pojo.api.PDFResponse;
import com.antra.evaluation.reporting_system.pojo.report.PDFFile;
import com.antra.evaluation.reporting_system.service.PDFService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PDFGenerationController {

    private static final Logger log = LoggerFactory.getLogger(PDFGenerationController.class);
    private static final String DOWNLOAD_API_URI = "/pdf/{id}/content";

    private PDFService pdfService;

    @Autowired
    public PDFGenerationController(PDFService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/pdf") // get all PDFs
    public ResponseEntity<List<PDFResponse>> getPDFs(){
        List<PDFFile> list = pdfService.getPDFs();
        var responseList = list.stream().map(file -> {
            PDFResponse response = new PDFResponse();
            System.out.println("reqid: "+ file.getReqId());
            System.out.println("fileId: "+ file.getId());
           // BeanUtils.copyProperties(response, file);
            response.setFileId(file.getId());
            response.setReqId(file.getReqId());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            response.setFailed(false);
            return response;
        }).collect(Collectors.toList());
        return new ResponseEntity<List<PDFResponse>>(responseList, HttpStatus.OK);
    }


    @PostMapping("/pdf")
    public ResponseEntity<PDFResponse> createPDF(@RequestBody @Validated PDFRequest request) {
        log.info("Got request to generate PDF: {}", request);

        PDFResponse response = new PDFResponse();
        PDFFile file = null;
        response.setReqId(request.getReqId());

        try {
            file = pdfService.createPDF(request);
            response.setFileId(file.getId());
            response.setFileLocation(file.getFileLocation());
            response.setFileSize(file.getFileSize());
            log.info("Generated: {}", file);
        } catch (Exception e) {
            response.setFailed(true);
            log.error("Error in generating pdf", e);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/pdf/{id}")
    public ResponseEntity<PDFResponse> deleteExcel(@PathVariable String id) throws FileNotFoundException {
        log.debug("Got Request to Delete File:{}", id);
        var response = new PDFResponse();
        PDFFile fileDeleted = pdfService.deleteFile(id);

        BeanUtils.copyProperties(fileDeleted, response);
        response.setFileLocation(this.generateFileDownloadLink(id));
        log.debug("File Deleted:{}", fileDeleted);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    private String generateFileDownloadLink(String fileId) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme("http").host("localhost:8080").path(DOWNLOAD_API_URI) // localhost:8080 need to be externalized as parameter
                .buildAndExpand(fileId);
        return uriComponents.toUriString();
    }




}
