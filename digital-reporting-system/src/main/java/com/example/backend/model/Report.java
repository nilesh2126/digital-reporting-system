package com.example.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 80)
    private String eventType;
    @Column(nullable = false, length = 5000)
    private String description;
    @Column(nullable = false, length = 500)
    private String loc;
    @Column(nullable = false)
    private LocalDateTime dateTime;
    @Column(nullable = false, length = 30)
    private String status = "PENDING";
    @Column(length = 100)
    private String firNumber;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Report() {}
    @PreUpdate public void beforeUpdate(){ updatedAt = LocalDateTime.now(); }
    public Long getId(){ return id; }
    public void setId(Long id){ this.id = id; }
    public String getEventType(){ return eventType; }
    public void setEventType(String eventType){ this.eventType = eventType; }
    public String getDescription(){ return description; }
    public void setDescription(String description){ this.description = description; }
    public String getLoc(){ return loc; }
    public void setLoc(String loc){ this.loc = loc; }
    public LocalDateTime getDateTime(){ return dateTime; }
    public void setDateTime(LocalDateTime dateTime){ this.dateTime = dateTime; }
    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }
    public String getFirNumber(){ return firNumber; }
    public void setFirNumber(String firNumber){ this.firNumber = firNumber; }
    public User getReporter(){ return reporter; }
    public void setReporter(User reporter){ this.reporter = reporter; }
    public LocalDateTime getCreatedAt(){ return createdAt; }
    public LocalDateTime getUpdatedAt(){ return updatedAt; }
}
