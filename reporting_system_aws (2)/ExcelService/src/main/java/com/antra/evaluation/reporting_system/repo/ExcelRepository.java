package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExcelRepository extends MongoRepository<ExcelFile, String>{
    Optional<ExcelFile> getExcelFileByFileId(String id);
    void deleteExcelFileByFileId(String id);


    List<ExcelFile> getAllBy();
    ExcelFile findExcelFileByReqId(String reqId);
}
