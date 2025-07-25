package naukriBot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class OllamaClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String OLLAMA_URL = "http://localhost:11434/api/generate";
    
    public OllamaClient(HttpClient httpClient, ObjectMapper objectMapper) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
    }
    
    public String getPrompt(String question) {
        try {
            // Create a professional response for common job application questions
            String prompt = "Answer this job application question professionally and concisely: " + question;
            
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "llama2");
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                ObjectNode responseJson = (ObjectNode) objectMapper.readTree(response.body());
                return responseJson.get("response").asText().trim();
            } else {
                // Fallback responses for common questions
                return getFallbackResponse(question);
            }
        } catch (Exception e) {
            System.out.println("Error with Ollama API: " + e.getMessage());
            return getFallbackResponse(question);
        }
    }
    
    public String getAnswerForOptions(String question, List<String> options) {
        try {
            String prompt = "Choose the best answer from these options for the question: " + question + 
                          "\nOptions: " + String.join(", ", options) + 
                          "\nReturn only the exact option text.";
            
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "llama2");
            requestBody.put("prompt", prompt);
            requestBody.put("stream", false);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OLLAMA_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                ObjectNode responseJson = (ObjectNode) objectMapper.readTree(response.body());
                String answer = responseJson.get("response").asText().trim();
                
                // Find the best matching option
                for (String option : options) {
                    if (answer.toLowerCase().contains(option.toLowerCase()) || 
                        option.toLowerCase().contains(answer.toLowerCase())) {
                        return option;
                    }
                }
                // Return first option as fallback
                return options.isEmpty() ? "Yes" : options.get(0);
            } else {
                return options.isEmpty() ? "Yes" : options.get(0);
            }
        } catch (Exception e) {
            System.out.println("Error with Ollama API for options: " + e.getMessage());
            return options.isEmpty() ? "Yes" : options.get(0);
        }
    }
    
    private String getFallbackResponse(String question) {
        String lowerQuestion = question.toLowerCase();
        
        if (lowerQuestion.contains("experience") || lowerQuestion.contains("years")) {
            return ApplicationConfig.EXPERIENCE_YEARS + " years";
        } else if (lowerQuestion.contains("salary") || lowerQuestion.contains("expected")) {
            return ApplicationConfig.DEFAULT_SALARY_EXPECTATION;
        } else if (lowerQuestion.contains("notice") || lowerQuestion.contains("joining")) {
            return ApplicationConfig.DEFAULT_NOTICE_PERIOD;
        } else if (lowerQuestion.contains("relocate") || lowerQuestion.contains("location")) {
            return ApplicationConfig.DEFAULT_RELOCATION_ANSWER;
        } else if (lowerQuestion.contains("skills") || lowerQuestion.contains("technology")) {
            return ApplicationConfig.DEFAULT_SKILLS;
        } else if (lowerQuestion.contains("why") || lowerQuestion.contains("interest")) {
            return ApplicationConfig.DEFAULT_WHY_INTERESTED;
        } else {
            return "Yes, I am interested and available.";
        }
    }
}