package com.saim.pneumoniacare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.json.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@SpringBootApplication
public class PneumoniaCareApplication {
    @Autowired
    private DatabaseInitializer databaseInitializer;

    public static void main(String[] args) {
        SpringApplication.run(PneumoniaCareApplication.class, args);
    }

    @CrossOrigin(origins = "http://localhost:3000") // link
    @PostMapping("/login")
    public String loginUser(@RequestBody String data) {
        JSONObject jsonData = new JSONObject(data);
        String name = jsonData.getString("username");
        String password = jsonData.getString("password");
        return databaseInitializer.loginUser(name, password);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/registerUser")
    public String registerUser(@RequestBody String data) {
        JSONObject jsonData = new JSONObject(data);
        String name = jsonData.getString("name");
        int age = jsonData.getInt("age");
        String nationality = jsonData.getString("nationality");
        String email = jsonData.getString("email");
        String password = jsonData.getString("password");

        return databaseInitializer.registerUser(name, age, nationality, email, password);
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/diagnosis")
    public String processDiagnosis(@RequestParam("image") MultipartFile image,
                                   @RequestParam("cough") boolean cough,
                                   @RequestParam("fever") boolean fever,
                                   @RequestParam("nationality") String nationality,
                                   @RequestParam("context") String context) throws IOException {

        System.out.println("Hello, started!");
        byte[] xray = image.getBytes();
        double percPneumonia = 0;
        try {
             percPneumonia = org.deeplearning4j.examples.PneumoniaClassifierApplication.generateData(image);
        }
        catch(Exception e) {
            return new JSONObject().put("status", 500).put("error", "Unexpected error: " + e.getMessage()).toString();
        }

        String sysPrompt = "You are a health assistant that diagnoses Pneumonia in users. You will be given details like nationality, signs of cough and fever and some additional context. " +
                "You will also be given the patient's chances of having pneumonia, according to a CNN model that detects Pneumonia using Xray images with 83% accuracy. " +
                ". You need to return a report citing patient details, symptoms, course of action and 5 nearby pneumonia-related hospitals based on nationality in JSON format, for example " +
                "{ \"percPneumonia\":98.2, \"symptoms\": \"high grade fever, cough\", \"suggestion\": \"~detailed paragraph for course of action by ChatGPT~\", \"hospitals\": [{\"name\": \"Shifa Hospital \"address\": \"Pitras Bukhari Rd, H-8, Islamabad}, ...]}";

        String userPrompt = String.format("I am a " + nationality + "user who " + (cough?"has":"does not have") + " cough and " + (fever?"has":"does not have") + " fever. Additional context: " +
                context + ". According to the CNN model, my chances of having pneumonia are " + percPneumonia);
        JSONObject reportJSON = ChatGptIntegration.getGPTResponse(sysPrompt, userPrompt);

        JSONObject response = new JSONObject();
        response.put("status",200);
        response.put("message","Pneumonia Diagnosed");
        response.put("report",reportJSON);
        return reportJSON.toString();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/report")
    public String saveReport(@RequestBody String data) {
        JSONObject jsonData = new JSONObject(data);
        String email = jsonData.getString("email");
        String report = jsonData.getString("report");
        String time = jsonData.getString("time");

        int userID = databaseInitializer.getUserID(email);
        System.out.println(userID);
        databaseInitializer.saveReport(userID, report, time);
        return "{\"status\": 200, \"message\": \"Attempt completed\"}";
    }
//
    /*@CrossOrigin(origins = "http://localhost:3000")
    @PostMapping("/report")
    public String getReport(@RequestBody String data) {
        JSONObject jsonData = new JSONObject(data);
        String email = jsonData.getString("email");

        int userID = databaseInitializer.getUserID(email);
        System.out.println(userID);
        databaseInitializer.getReport(userID);
        return "{\"status\": 200, \"message\": \"Attempt completed\"}";
    }*/
@CrossOrigin(origins = "http://localhost:3000")
@PostMapping("/getreport")
public String getReport() {
    try {
        return databaseInitializer.getSQLPosts();
    }
    catch(Exception e) {
        return "{\"status\":\"error\",\"message\":\"" + e.getMessage() + "\"}";
    }
}
}