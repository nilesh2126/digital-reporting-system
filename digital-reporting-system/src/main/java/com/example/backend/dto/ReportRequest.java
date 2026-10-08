package com.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class ReportRequest {
    @NotBlank @Size(max=80) private String eventType;
    @NotBlank @Size(max=5000) private String description;
    @NotBlank @Size(max=500) private String loc;
    @NotNull private LocalDateTime dateTime;
    public String getEventType(){ return eventType; }
    public void setEventType(String eventType){ this.eventType = eventType; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public String getLoc(){ return loc; }
    public void setLoc(String loc){ this.loc = loc; }
    public LocalDateTime getDateTime(){ return dateTime; }
    public void setDateTime(LocalDateTime dateTime){ this.dateTime = dateTime; }
}
