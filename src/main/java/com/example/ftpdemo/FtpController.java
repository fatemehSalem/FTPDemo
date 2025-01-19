package com.example.ftpdemo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class FtpController {
    private final FTPUtility ftpUtility;

    public FtpController(FTPUtility ftpUtility) {
        this.ftpUtility = ftpUtility;
    }

    @PostMapping("/uploadFile")
    public String uploadFile(){
        if(ftpUtility.uploadFile())
            return "success!";
        else
            return "fail!";
    }
}
