package com.example.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
@Entity
public class Report {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private long id;
private String eventType;
private String desc;
private String loc;
private String dateTime;
public Report(){
}
public long getId(){
    return id;
}
public String getEventType(){
    return eventType;
}
public String getDesc(){
    return desc;
}
public String getLoc(){
    return loc;
}
public String getDateTime(){
    return dateTime;
}
public void setEventType(String eventType){
    this.eventType = eventType;
}

public void setLoc(String loc){
    this.loc = loc;
}

public void setDateTime(String dateTime){
    this.dateTime = dateTime;
}

public void setDesc(String Desc){
    this.desc = Desc;
}

}
