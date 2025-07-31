package naukriBot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OllamaClient {
    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static ObjectMapper objectMapper = new ObjectMapper();
    private static final String baseUrl = "https://api.openai.com/v1";
    private static final String apiKey = "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";
    private static final String  resumeContext = """
                Name: Vinay Mandge
                Date of Birth: 16 May 1999
                Years of total Experience: 3.9
                Years of relevant experience in all technologies: 3.9
                Last Working Day: 03 Aug 2025
                Current Company: Provatosoft
                Skills: Java, Spring Boot, REST APIs, Core Java, Java 8, J2EE, Hibernate, Spring MVC, Microservices, GCP, BigQuery, Pub/Sub, Apache Beam, Kubernetes, REST API, JDBC, JavaScript, Node.js, Jenkins, Docker, Splunk, PostgreSQL, Cassandra, Performance Testing, API Testing, Debugging, SOLID Principles, Design Patterns, React.js
                Current CTC: 9.5 LPA
                Expected CTC: 14 LPA
                Notice Period: 2 months (Serving)
                Current Location: Pune, India
                Highest Qualification: Bachelor of Technology in Computer Science
                Contact Info:
                Phone: +91 9096103432
                Email: mandgevinay16@gmail.com
                LinkedIn: https://www.linkedin.com/in/vinay-mandge
                
                Experience:
                
                1. Software Developer – Provatosoft Private Limited (Vendor at Walmart Tech Global) [09/2023 - Present]
                - Developed flexible APIs and maintained backend systems.
                - Integrated messaging, search, and data warehousing features.
                - Built tools for data reconciliation across systems.
                - Implemented CI/CD pipelines and managed cloud infrastructure.
                - Handled codebase of over 20,000 lines in microservices.
                
                2. Software Developer II – Capgemini [09/2021 - 08/2023]
                - Built and deployed microservices using Java Spring Boot.
                - Developed REST APIs and standardized processes.
                - Acted as the primary contact for production issues.
                
                Projects:
                
                - **Walmart Sam's Club Elixir**: Built stream/batch data pipelines using PubSub, Apache Beam, and cloud tools.
                - **Barclays BBG, Finance**: Delivered business banking modules using microservices architecture.
                - **GE Healthcare**: Developed reusable modules and integrated systems with GE's enterprise platforms.
                
                Summary:
                Experienced Java Developer with 3.9+ years in developing scalable applications and microservices using Java, Spring Boot, and modern cloud-native technologies. Skilled in performance optimization, API design, debugging, and DevOps tools like Docker, Jenkins, and Kubernetes.
                
                Education:
                - HSC – R.C. Patel Junior College, 2017
                - Bachelor of Technology in Computer Science – R.C. Patel Institute Of Technology, 2021
                
                Key Achievements:
                - Employee of the Year at Provatosoft
                - STAR Award for Outstanding Performance (2022) at Capgemini
                """;


    public  String getPrompt(String args) {
        Map<String, String> ollamaPromptCache = new HashMap<>();
        ollamaPromptCache.put("what is your name", "Vinay Mandge");
        ollamaPromptCache.put("your name", "Vinay Mandge");
        ollamaPromptCache.put("date of birth", "16 May 1999");
        ollamaPromptCache.put("experience", "3.9 years");
        ollamaPromptCache.put("current company", "Provatosoft");
        ollamaPromptCache.put("expected salary", "14 LPA");
        ollamaPromptCache.put("current salary", "9.5 LPA");
        ollamaPromptCache.put("notice period", "2 months (Serving)");
        ollamaPromptCache.put("location", "Pune, India");
        ollamaPromptCache.put("qualification", "Bachelor of Technology in Computer Science");
        ollamaPromptCache.put("skills", "Java, Spring Boot, REST APIs, Core Java, Java 8, J2EE, Hibernate, Spring MVC, Microservices, GCP, BigQuery, Pub/Sub, Apache Beam, Kubernetes, REST API, JDBC, JavaScript, Node.js, Jenkins, Docker, Splunk, PostgreSQL, Cassandra, Performance Testing, API Testing, Debugging, SOLID Principles, Design Patterns, React.js");
        try {
            if (ollamaPromptCache.containsKey(args.toLowerCase())) {
                return ollamaPromptCache.get(args.toLowerCase());
            }
            //URL of the Ollama API
            URL url = new URL(baseUrl + "/responses");
            String finalPrompt = getString(args, url);
            String escapedPrompt = finalPrompt
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            // JSON request body
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-4.1",
                    "prompt", escapedPrompt// Limit response length for faster processing
                    );

            String jsonRequest = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/responses"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode jsonResponse = objectMapper.readTree(response.body());
            return jsonResponse.get("response").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "-1";
    }


    public static String generateContextBasedResponse(String context, String question, String instructions) throws Exception {

        // Structure the request with system message (context + instructions) and user message (question)
        Map<String, Object> requestBody = Map.of(
                "model", "o4-mini",
                "messages", List.of(
                        Map.of(
                                "role", "system",
                                "content", buildSystemPrompt(context, instructions)
                        ),
                        Map.of(
                                "role", "user",
                                "content", question
                        )
                ),
                "max_completion_tokens", 800,
                "temperature", 1  // Low temperature for consistent, factual responses
        );

        String jsonRequest = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return extractResponseContent(response.body());
        } else {
            throw new RuntimeException("OpenAI API error: " + response.statusCode() +
                    " - " + response.body());
        }
    }

    private static String buildSystemPrompt(String context, String instructions) {
        return String.format("""
            You are an AI assistant that answers questions based ONLY on the provided context. If unsure about answer return "Not enough information."
            
            CONTEXT:
            %s
            
            INSTRUCTIONS:
            %s
            
            RULES:
            - Answer only based on the provided context
            - Keep responses concise and direct
            -Give me some response dont send empty string
            """, context, instructions);
    }

    private static String extractResponseContent(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);
        return root.path("choices").get(0).path("message").path("content").asText().trim();
    }



    private static String getString(String args, URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set up the connection
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        String userContext = """
            Name: Vinay Mandge
            Date of Birth: 16 May 1999
            Years of total Experience: 3.9
            Years of relevant experience in all technologies: 3.9
            Last Working Day: 03 Aug 2025
            Current Company: Provatosoft
            Skills: Java, Spring Boot, REST APIs, Core Java, Java 8, J2EE, Hibernate, Spring MVC, Microservices, GCP, BigQuery, Pub/Sub, Apache Beam, Kubernetes, REST API, JDBC, JavaScript, Node.js, Jenkins, Docker, Splunk, PostgreSQL, Cassandra, Performance Testing, API Testing, Debugging, SOLID Principles, Design Patterns, React.js
            Current CTC: 9.5 LPA
            Expected CTC: 14 LPA
            Notice Period: 2 months (Serving)
            Current Location: Pune, India
            Highest Qualification: Bachelor of Technology in Computer Science
            Contact Info:
            Phone: +91 9096103432
            Email: mandgevinay16@gmail.com
            LinkedIn: https://www.linkedin.com/in/vinay-mandge
            
            Experience:
            
            1. Software Developer – Provatosoft Private Limited (Vendor at Walmart Tech Global) [09/2023 - Present]
            - Developed flexible APIs and maintained backend systems.
            - Integrated messaging, search, and data warehousing features.
            - Built tools for data reconciliation across systems.
            - Implemented CI/CD pipelines and managed cloud infrastructure.
            - Handled codebase of over 20,000 lines in microservices.
            
            2. Software Developer II – Capgemini [09/2021 - 08/2023]
            - Built and deployed microservices using Java Spring Boot.
            - Developed REST APIs and standardized processes.
            - Acted as the primary contact for production issues.
            
            Projects:
            
            - **Walmart Sam's Club Elixir**: Built stream/batch data pipelines using PubSub, Apache Beam, and cloud tools.
            - **Barclays BBG, Finance**: Delivered business banking modules using microservices architecture.
            - **GE Healthcare**: Developed reusable modules and integrated systems with GE’s enterprise platforms.
            
            Summary:
            Experienced Java Developer with 3.9+ years in developing scalable applications and microservices using Java, Spring Boot, and modern cloud-native technologies. Skilled in performance optimization, API design, debugging, and DevOps tools like Docker, Jenkins, and Kubernetes.
            
            Education:
            - HSC – R.C. Patel Junior College, 2017
            - Bachelor of Technology in Computer Science – R.C. Patel Institute Of Technology, 2021
            
            Key Achievements:
            - Employee of the Year at Provatosoft
            - STAR Award for Outstanding Performance (2022) at Capgemini
            
            Instructions:
            1.if any experience is asked for any skills put 3.9 years for any skills asked.
            2.Say yes to all location but if Pune exists in location choose Pune
            3.never mention that context is not mentioned try to give any related answer as much as possible.
            4.3. if asked being related any military or anything like this say no
            
            Question:
            """;

        String finalPrompt = userContext  + args + "\n\nAnswer only from the given context. and make it as short as possible. If there is any questions you are not able to answer, try to give any related answer as much as possible from the context provided to the best of your ability.";
        return finalPrompt;
    }

    public static String getAnswerForOptions(String prompt, List<String> options) {
    try {
        // URL of the Ollama API
        URL url = new URL("http://localhost:11434/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        // Set up the connection
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        // Prepare the user context and final prompt
        String userContext = """
                Name: Vinay Mandge
                Date of Birth: 16 May 1999
                Years of total Experience: 3.9
                Years of relevant experience in all technologies: 3.9
                Last Working Day: 03 Aug 2025
                Current Company: Provatosoft
                Skills: Java, Spring Boot, REST APIs, Core Java, Java 8, J2EE, Hibernate, Spring MVC, Microservices, GCP, BigQuery, Pub/Sub, Apache Beam, Kubernetes, REST API, JDBC, JavaScript, Node.js, Jenkins, Docker, Splunk, PostgreSQL, Cassandra, Performance Testing, API Testing, Debugging, SOLID Principles, Design Patterns, React.js
                Current CTC: 9.5 LPA
                Expected CTC: 14 LPA
                Notice Period: 2 months (Serving)
                Current Location: Pune, India
                
                Highest Qualification: Bachelor of Technology in Computer Science
                
                Contact Info:
                Phone: +91 9096103432
                Email: mandgevinay16@gmail.com
                LinkedIn: https://www.linkedin.com/in/vinay-mandge
                
                Experience:
                
                1. Software Developer – Provatosoft Private Limited (Vendor at Walmart Tech Global) [09/2023 - Present]
                - Developed flexible APIs and maintained backend systems.
                - Integrated messaging, search, and data warehousing features.
                - Built tools for data reconciliation across systems.
                - Implemented CI/CD pipelines and managed cloud infrastructure.
                - Handled codebase of over 20,000 lines in microservices.
                
                2. Software Developer II – Capgemini [09/2021 - 08/2023]
                - Built and deployed microservices using Java Spring Boot.
                - Developed REST APIs and standardized processes.
                - Acted as the primary contact for production issues.
                
                Projects:
                
                - **Walmart Sam's Club Elixir**: Built stream/batch data pipelines using PubSub, Apache Beam, and cloud tools.
                - **Barclays BBG, Finance**: Delivered business banking modules using microservices architecture.
                - **GE Healthcare**: Developed reusable modules and integrated systems with GE’s enterprise platforms.
                
                Summary:
                Experienced Java Developer with 3.9+ years in developing scalable applications and microservices using Java, Spring Boot, and modern cloud-native technologies. Skilled in performance optimization, API design, debugging, and DevOps tools like Docker, Jenkins, and Kubernetes.
                
                Education:
                - HSC – R.C. Patel Junior College, 2017
                - Bachelor of Technology in Computer Science – R.C. Patel Institute Of Technology, 2021
                
                Key Achievements:
                - Employee of the Year at Provatosoft
                - STAR Award for Outstanding Performance (2022) at Capgemini
                
                Instructions:
                1.if any experience is asked for any skills put 3.9 years for all.
                2.Say yes to all location but if Pune exists in location choose Pune
                3. if asked being related any military or anything like this say no
                
                Question:
                """;

        String finalPrompt = userContext + prompt + "\nOptions: " + String.join(", ", options) + "\n\nAnswer only from the given context. Do not make up answers return only answer from the options given and return same exact option";
        String escapedPrompt = finalPrompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

        // JSON request body
        Map<String, Object> requestBody = Map.of(
                "model", "llama3.1",
                "prompt", finalPrompt,
                "stream", false,
                "options", Map.of(
                        "temperature", 0.0,      // Even lower for MCQ (most deterministic)
                        "top_p", 0.8,           // More focused
                        "max_tokens", 50,       // Very short response needed
                        "stop", Arrays.asList("\n", ".", "because", "since") // Stop early
                )
        );

        String jsonRequest = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequest))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        JsonNode jsonResponse = objectMapper.readTree(response.body());
        return jsonResponse.get("response").asText().trim();

    } catch (Exception e) {
        e.printStackTrace();
    }
    return "-1";
}


    public static String answerResumeQuestion(String resumeData, String question) throws Exception {
        String instructions = """
            1. If any experience is asked for any skills, respond with 3.9 years for any skills asked.
            2. Say yes to all locations but if Pune exists in location options, choose Pune.
            3. Never mention that context is not provided - try to give any related answer as much as possible.
            4. If asked about military service or similar, say no.
            5. Keep answers short and to the point.
            """;

        return generateContextBasedResponse(resumeData, question, instructions);
    }


    public static void main(String[] args) {
        try {


            // Test different questions
            String[] questions = {
                    "What are all skills you have worked upon?",
                    "Tell me about your experience with microservices"
            };


            for (String question : questions) {
                System.out.println("\nQuestion: " + question);
                String answer = answerResumeQuestion(resumeContext, question);
                System.out.println("Answer: " + answer);
                System.out.println("-".repeat(50));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
