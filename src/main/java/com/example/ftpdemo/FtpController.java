package com.example.ftpdemo;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Slf4j
public class FtpController {
    private final FTPUtility ftpUtility;

    public FtpController(FTPUtility ftpUtility) {
        this.ftpUtility = ftpUtility;
    }
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    @PostMapping("/uploadFile")
    public String uploadFile(){
        if(ftpUtility.uploadFile())
            return "success!";
        else
            return "fail!";
    }

    @GetMapping("/readFile")
    public @ResponseBody List<Course> readCSV(){
        List<Course> courses = new ArrayList<>();

        Workbook workbook =null;
        try {
            ClassPathResource resource = new ClassPathResource("demo.xlsx");
            InputStream inputStream = resource.getInputStream();
            workbook = WorkbookFactory.create(inputStream);

            log.info("Number of sheets: ", workbook.getNumberOfSheets());

            workbook.forEach(sheet -> {
                log.info(" => " + sheet.getSheetName());

                DataFormatter dataFormatter = new DataFormatter();

                int index = 0;
                for(Row row : sheet) {
                    if(index++ == 0) continue;
                    Course course = new Course();
                    course.setId(dataFormatter.formatCellValue(row.getCell(0)));
                    course.setName(dataFormatter.formatCellValue(row.getCell(1)));
                    try {
                        course.setDob(sdf.parse(dataFormatter.formatCellValue(row.getCell(2))));
                    } catch (ParseException e) {
                        log.error(e.getMessage(), e);
                    }
                    course.setMark(Integer.parseInt(dataFormatter.formatCellValue(row.getCell(3))));
                    courses.add(course);
                }
            });
        } catch (EncryptedDocumentException | InvalidFormatException | IOException e) {
            log.error(e.getMessage(), e);
        }finally {
            try {
                if(workbook != null) workbook.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return courses;
    }
}
