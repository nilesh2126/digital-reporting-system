package com.example.backend.Service;

import com.example.backend.model.Report;
import com.example.backend.Repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
@Service
public class ReportService {
    private final ReportRepository repository;
    public ReportService(ReportRepository repository){
        this.repository = repository;
    }
    public Report createReport(Report report){
        return repository.save(report);
    }
    public List<Report> getAllReports(){
        return repository.findAll();
    }
    public Report getReportById(Long id){
        return repository.findById(id).orElse(null);
    }
    public void deleteReport(Long id){
        repository.deleteById(id);
    }


public Report updateReport(long id, Report newReport){
    Report existingReport = repository.findById(id).orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, "Report not found"));
    if(newReport.getLoc() != null)
    existingReport.setLoc(newReport.getLoc());

    if(newReport.getEventType() != null)
    existingReport.setEventType(newReport.getEventType());
    
    if(newReport.getDateTime() != null)
    existingReport.setDateTime(newReport.getDateTime());
    
    if(newReport.getDesc() != null)
    existingReport.setDesc(newReport.getDesc());
return repository.save(existingReport);
}
}
