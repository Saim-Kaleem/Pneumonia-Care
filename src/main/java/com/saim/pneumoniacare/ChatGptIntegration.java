package com.saim.pneumoniacare;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class ChatGptIntegration {

    public static void main(String[] args) {
        String nationality = "Pakistani";
        boolean cough = true;
        boolean fever = true;
        double percPneumonia = 98.2;
        String context = "";
        String sysPrompt = "You are a health assistant that diagnoses Pneumonia in users. You will be given details like nationality, signs of cough and fever and some additional context. " +
                "You will also be given the patient's chances of having pneumonia, according to a CNN model that detects Pneumonia using Xray images with 83% accuracy. " +
                ". You need to return a report citing patient details, symptoms, course of action and 5 nearby hospitals based on nationality in JSON format, like " +
                "{ \"percPneumonia\":98.2, \"symptoms\": \"high grade fever, cough\", \"suggestion\": \"~course of action by ChatGPT~\", \"hospitals\": [{\"name1\": \"address\"}, ...]}";

        String userPrompt = String.format("I am a " + nationality + "user who " + (cough?"has":"does not have") + " cough and " + (fever?"has":"does not have") + " fever. Additional context: " +
                context + ". According to the CNN model, my chances of having pneumonia are " + percPneumonia);
        JSONObject reportJSON1 = ChatGptIntegration.getGPTResponse(sysPrompt, userPrompt);
        System.out.println(reportJSON1);
    }

    public static JSONObject getGPTResponse(String systemPrompt, String userInput) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost("https://api.openai.com/v1/chat/completions");
            post.setHeader("Content-type", "application/json");
            post.setHeader("Authorization", "Bearer " + System.getenv("OPENAI_API_KEY"));

            JSONObject body = new JSONObject();
            body.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject().put("role", "system").put("content", systemPrompt));
            messages.put(new JSONObject().put("role", "user").put("content", userInput));
            body.put("messages", messages);

            post.setEntity(new StringEntity(body.toString()));

            String response = EntityUtils.toString(client.execute(post).getEntity());
            System.out.println("GPT Response: " + response);

            JSONObject jsonResponse = new JSONObject(response);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            if (choices.length() > 0) {
                String content = choices.getJSONObject(0).getJSONObject("message").getString("content");
                // Parse the content to extract the MCQs array
                return new JSONObject(content);
            } else {
                throw new RuntimeException("No choices in GPT response");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error in GPT call: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
}