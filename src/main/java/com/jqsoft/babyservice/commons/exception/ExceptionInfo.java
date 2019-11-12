package com.jqsoft.babyservice.commons.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExceptionInfo {

    public ExceptionInfo(){}

    public ExceptionInfo(Exception e){
        this.dateTime = LocalDateTime.now();
        this.name = e.toString();
        this.elements = e.getStackTrace();
    }

    private LocalDateTime dateTime;

    private String name;

    private StackTraceElement[] elements;

}
