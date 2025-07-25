package naukriBot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.edge.EdgeDriver;

import java.io.*;
import java.net.http.HttpClient;
import java.util.*;

public class NaukriJobApply {
    // Using configuration from ApplicationConfig
    private static String firstname = ApplicationConfig.FIRST_NAME;
    private static String lastname = ApplicationConfig.LAST_NAME;
    private static String email = ApplicationConfig.EMAIL;
    private static String phone = ApplicationConfig.PHONE;
    private static String resumePath = ApplicationConfig.RESUME_PATH;
    public static int experience = ApplicationConfig.EXPERIENCE_YEARS;
    private static List<String> joblink = new ArrayList<>(); // Initialized list to store links
    private static int maxcount = ApplicationConfig.MAX_DAILY_APPLICATIONS;
    private static List<String> keywords = ApplicationConfig.JOB_KEYWORDS;
    private static String location = ApplicationConfig.LOCATION;
    private static int applied = 0; // Count of jobs applied successfully
    private static int failed = 0; // Count of jobs failed
    private static int companySiteApplied = 0; // Count of company site applications
    private static Map<String, List<String>> applied_list = new HashMap<>();

    static {
        applied_list.put("passed", new ArrayList<>());
        applied_list.put("failed", new ArrayList<>());
        applied_list.put("company_site", new ArrayList<>());
        applied_list.put("company_site_failed", new ArrayList<>());
    } // Saved list of applied and failed job links for manual review

    private static String edgedriverfile = ApplicationConfig.EDGE_DRIVER_PATH;
    private static String yournaukriemail = ApplicationConfig.NAUKRI_EMAIL;
    private static String yournaukripass = ApplicationConfig.NAUKRI_PASSWORD;
    WebDriver driver;
    String Logs = "";
    CompanySiteApplicationHandler companySiteHandler;

    private boolean cacheExists() {
        try {
            return new File("naukri_cache.ser").exists();
        } catch (Exception e) {
            System.out.println("Error checking cache existence: " + e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private void loadCache() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("naukri_cache.ser"))) {
            List<Cookie> cookies = (List<Cookie>) ois.readObject();
            driver.get("https://naukri.com"); // Visit domain first
            for (Cookie cookie : cookies) {
                driver.manage().addCookie(cookie);
            }
            System.out.println("Cookies loaded successfully.");
        } catch (Exception e) {
            System.out.println("Error loading cookies: " + e.getMessage());
        }
    }

    private void saveCache() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("naukri_cache.ser"))) {
            Set<Cookie> cookies = driver.manage().getCookies();
            oos.writeObject(new ArrayList<>(cookies));
            System.out.println("Cookies saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving cookies: " + e.getMessage());
        }
    }

    public void applyStart() {
        try {
            System.setProperty("webdriver.edge.driver", edgedriverfile); // Change driver according to the browser
            driver = new EdgeDriver();
            
            // Initialize company site application handler
            companySiteHandler = new CompanySiteApplicationHandler(
                driver, firstname, lastname, email, phone, resumePath, experience
            );
            
            if (cacheExists()) {
                driver.get("https://naukri.com"); // Must visit first
                loadCache();
                driver.get("https://www.naukri.com/mnjuser/profile?id=");
            } else {
                driver.get("https://login.naukri.com/");
                WebElement uname = driver.findElement(By.id("usernameField"));
                uname.sendKeys(yournaukriemail);
                WebElement passwd = driver.findElement(By.id("passwordField"));
                passwd.sendKeys(yournaukripass);
                passwd.sendKeys(Keys.ENTER);
                Thread.sleep(5000); // Wait for login
                saveCache();
            }
        } catch (Exception e) {
            System.out.println("WebDriver initialization failed: " + e.getMessage());
            e.printStackTrace();
            return; // Exit the method if the driver is not initialized
        }

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("Profile updated successfully.");
        } catch (Exception e) {
            System.out.println("Failed to update profile: " + e.getMessage());
            e.printStackTrace();
        }
//        driver.get("https://www.naukri.com/mnjuser/recommendedjobs");
//        WebElement divElement1 = driver.findElement(By.className("sim-jobs"));
//        // Find all anchor tags within the div
//        List<WebElement> anchorElements1 = divElement1.findElements(By.tagName("a"));
//        for (WebElement element : anchorElements1) {
//            String href = element.getAttribute("href");
//            if (href != null && !href.contains("ambitionbox")) {
//                joblink.add(href);
//            }
//        }

        for (String k : keywords) {
            for (int i = 1; i < 5; i++) {
                String url;
                if (location.equals("")) {
                    url = "https://www.naukri.com/" + k.toLowerCase().replace(' ', '-') + "-jobs-" + i + "?experience=" + experience;
                } else {
                    url = "https://www.naukri.com/" + k.toLowerCase().replace(' ', '-') + "-jobs-in-" + location.toLowerCase().replace(' ', '-') + "-" + (i + 1);
                }
                driver.get(url);
                System.out.println(url);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    WebElement divElement = driver.findElement(By.className("styles_jlc__main__VdwtF"));

                    // Find all anchor tags within the div
                    List<WebElement> anchorElements = divElement.findElements(By.tagName("a"));
                    for (WebElement element : anchorElements) {
                        String href = element.getAttribute("href");
                        if (href != null && !href.contains("ambitionbox")) {
                            joblink.add(href);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (String str : joblink) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (str != null) driver.get(str);
                    if (applied <= maxcount) {
                        if (handleJobApplication(str, i)) {
                            // Job application handled successfully
                        }
                        try {
                            if (driver.findElement(By.xpath("//*[text()='Your daily quota has been expired.']")) != null) {
                                System.out.println("MAX Limit reached closing browser");
                                driver.close();
                                break;
                            }
                            if (driver.findElement(By.xpath("//*[text()=' 1. First Name']")) != null) {
                                driver.findElement(By.xpath("//input[@id='CUSTOM-FIRSTNAME']")).sendKeys(firstname);
                            }
                            if (driver.findElement(By.xpath("//*[text()=' 2. Last Name']")) != null) {
                                driver.findElement(By.xpath("//input[@id='CUSTOM-LASTNAME']")).sendKeys(lastname);
                            }
                            if (driver.findElement(By.xpath("//*[text()='Submit and Apply']")) != null) {
                                driver.findElement(By.xpath("//*[text()='Submit and Apply']")).click();
                            }
                        } catch (Exception e) {
                        }
                    } else {
                        driver.close();
                        break;
                    }
                }
            }
            try (FileWriter writer = new FileWriter("output.txt");
                 BufferedWriter file = new BufferedWriter(writer)) {
                file.write(Logs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Completed applying, closing browser, saving in applied jobs CSV");
            System.out.println("=== APPLICATION SUMMARY ===");
            System.out.println("Direct Applications: " + applied);
            System.out.println("Company Site Applications: " + companySiteApplied);
            System.out.println("Failed Applications: " + failed);
            System.out.println("Total Processed: " + (applied + companySiteApplied + failed));
            
            try {
                driver.close();
            } catch (Exception e) {
            }
            String csv_file = "naukriapplied.csv";
        }
        driver.quit();
    }
    
    private boolean handleJobApplication(String jobUrl, int pageNumber) {
        try {
            Thread.sleep(3000);
            
            // Check if already applied
            if (isAlreadyApplied()) {
                System.out.println("Already applied to this job, skipping...");
                return true;
            }
            
            // Try direct application first
            if (tryDirectApplication(jobUrl, pageNumber)) {
                return true;
            }
            
            // Try company site application
            if (tryCompanySiteApplication(jobUrl, pageNumber)) {
                return true;
            }
            
            // Try other options (Save, I am interested)
            if (tryAlternativeActions(jobUrl)) {
                return true;
            }
            
            // If all else fails, mark as failed
            failed++;
            System.out.println("Apply button not found");
            Logs += "\n need to apply manually: " + jobUrl;
            applied_list.get("failed").add(jobUrl);
            System.out.println("Failed " + failed);
            return false;
            
        } catch (Exception e) {
            System.out.println("Error handling job application: " + e.getMessage());
            failed++;
            applied_list.get("failed").add(jobUrl);
            return false;
        }
    }
    
    private boolean isAlreadyApplied() {
        try {
            WebElement appliedElement = driver.findElement(By.xpath("//*[text()='Applied']"));
            return appliedElement != null && appliedElement.isDisplayed();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return false;
        }
    }
    
    private boolean tryDirectApplication(String jobUrl, int pageNumber) {
        try {
            WebElement applyButton = driver.findElement(By.xpath("//*[text()='Apply']"));
            if (applyButton != null && applyButton.isDisplayed() && applyButton.isEnabled()) {
                applyButton.click();
                
                try {
                    Thread.sleep(2000);
                    if (!driver.findElements(By.cssSelector("li.botItem .botMsg span")).isEmpty()) {
                        fillDetails();
                    }
                } catch (InterruptedException e) {
                    System.out.println("No chatbot interaction needed");
                } catch (IOException e) {
                    System.out.println("Error filling details: " + e.getMessage());
                }
                
                applied++;
                applied_list.get("passed").add(jobUrl);
                System.out.println("Applied directly for page " + pageNumber + " Count " + applied);
                return true;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            // Apply button not found, continue to company site
        }
        return false;
    }
    
    private boolean tryCompanySiteApplication(String jobUrl, int pageNumber) {
        try {
            // Check if "Apply on company site" button exists
            WebElement companySiteButton = driver.findElement(By.xpath("//*[contains(text(), 'Apply on company site')]"));
            if (companySiteButton != null && companySiteButton.isDisplayed()) {
                System.out.println("Found 'Apply on company site' button for: " + jobUrl);
                
                // Use the company site handler
                boolean success = companySiteHandler.handleCompanySiteApplication(jobUrl);
                
                if (success) {
                    companySiteApplied++;
                    applied_list.get("company_site").add(jobUrl);
                    System.out.println("Successfully applied on company site for page " + pageNumber + " Count " + companySiteApplied);
                    return true;
                } else {
                    // Save for manual review
                    applied_list.get("company_site_failed").add(jobUrl);
                    System.out.println("Company site application failed, saved for manual review");
                    Logs += "\n Company site application failed (manual review needed): " + jobUrl;
                    
                    // Try to save the job as fallback
                    try {
                        WebElement saveButton = driver.findElement(By.xpath("//*[text()='Save']"));
                        if (saveButton != null && saveButton.isDisplayed()) {
                            saveButton.click();
                            System.out.println("Job saved for later review");
                        }
                    } catch (org.openqa.selenium.NoSuchElementException e) {
                        // Save button not found
                    }
                    
                    return false;
                }
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            // Company site button not found
        }
        return false;
    }
    
    private boolean tryAlternativeActions(String jobUrl) {
        try {
            // Try "I am interested" button
            WebElement interestedButton = driver.findElement(By.xpath("//*[text()='I am interested']"));
            if (interestedButton != null && interestedButton.isDisplayed() && interestedButton.isEnabled()) {
                interestedButton.click();
                applied++;
                applied_list.get("passed").add(jobUrl);
                System.out.println("Clicked 'I am interested' - Count " + applied);
                return true;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            // Continue
        }
        
        try {
            // Try "Save" button as last resort
            WebElement saveButton = driver.findElement(By.xpath("//*[text()='Save']"));
            if (saveButton != null && saveButton.isDisplayed() && saveButton.isEnabled()) {
                saveButton.click();
                System.out.println("Job saved for later review");
                return true;
            }
        } catch (org.openqa.selenium.NoSuchElementException e) {
            // Continue
        }
        
        return false;
    }

    private void fillDetails() throws IOException {
        OllamaClient ollamaClient = new OllamaClient(HttpClient.newHttpClient(), new ObjectMapper());
        String reply = "";
        while (true) {
            try {
                // Locate the last bot message
                List<WebElement> botMessages = driver.findElements(By.cssSelector("li.botItem .botMsg span"));
                WebElement lastMessage = botMessages.get(botMessages.size() - 1); // Get the last one
                String messageText = lastMessage.getText().trim();
                System.out.println("Chatbot said: " + messageText);

                // Check for MCQ options
                List<WebElement> labels = driver.findElements(By.cssSelector("label.ssrc__label"));
                reply = ollamaClient.getAnswerForOptions(messageText, labels.stream().map(WebElement::getText).toList());
                if (!labels.isEmpty()) {
                    for (WebElement label : labels) {
                        if (reply.toLowerCase().contains(label.getText().trim().toLowerCase())) {
                            label.click();
                            WebElement sendButton = driver.findElement(By.className("sendMsg")); // Or appropriate class
                            sendButton.click();
                            break;
                        }
                    }
                } else {
                    reply = ollamaClient.getPrompt(messageText);
                    // Type reply in input box (contenteditable div)
                    WebElement inputBox = driver.findElement(By.cssSelector("div.textArea[contenteditable='true']"));

                    // Use JavaScript since sendKeys often fails with contenteditable divs
                    JavascriptExecutor js = (JavascriptExecutor) driver;
                    inputBox.click();
                    inputBox.sendKeys(reply);
                    inputBox.sendKeys(Keys.ENTER); // Simulate pressing Enter to send the message
                }

                // Wait for the next bot message
                Thread.sleep(2000);

                // Check if there are no more input boxes
                if (driver.findElements(By.cssSelector("div.textArea[contenteditable='true']")).isEmpty()) {
                    System.out.println("No more input boxes left.");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Error while filling details: " + e.getMessage());
                e.printStackTrace();
                break;
            }
        }
    }
}