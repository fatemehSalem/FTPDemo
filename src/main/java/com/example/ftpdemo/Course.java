package com.example.ftpdemo;

import lombok.Data;

import java.util.Date;

@Data
public class Course {
    private String id;
    private String name;
    private Date dob;
    private int mark;
}
