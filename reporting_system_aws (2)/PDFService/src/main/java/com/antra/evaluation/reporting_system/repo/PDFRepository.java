package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.pojo.report.PDFFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PDFRepository extends MongoRepository<PDFFile, String> {
        PDFFile findPDFFileByReqId(String id);

}
