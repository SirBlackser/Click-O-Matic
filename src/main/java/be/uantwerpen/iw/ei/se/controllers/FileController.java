package be.uantwerpen.iw.ei.se.controllers;

import be.uantwerpen.iw.ei.se.fittsTest.models.FittsTest;
import be.uantwerpen.iw.ei.se.models.JSONResponse;
import be.uantwerpen.iw.ei.se.services.FileService;
import be.uantwerpen.iw.ei.se.services.FittsService;
import be.uantwerpen.iw.ei.se.services.UserService;
import com.fasterxml.jackson.annotation.JacksonAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import java.io.File;

/**
 * Created by Quinten on 15/12/2015.
 */
@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @RequestMapping(value="/UploadTestFile/", method= RequestMethod.POST, headers={"Content-type=multipart/form-data"})
    @PreAuthorize("hasRole('test-management') and hasRole('logon')")
    @ResponseBody
    public JSONResponse uploadTestFile(@RequestBody MultipartFile file_data, final ModelMap model)
    {
        if(file_data != null) {
            String fileName = fileService.saveFile(file_data);
            if(fileName != "") {
                return new JSONResponse("OK", fileName, "", null);
            } else {
                return new JSONResponse("ERROR", "The file could not be uploaded to the database!", "#?errorFileUpload", null);
            }


        } else {
            return new JSONResponse("ERROR", "The file could not be uploaded to the database!", "#?errorFileUpload", null);
        }
    }
}
