package naukriBot;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.io.*;
import java.util.*;

public class NaukriJobApply {
    private static String firstname = ""; // Add your LastName
    private static String lastname = ""; // Add your FirstName
    public static int experience = 3;//Add your experience
    private static List<String> joblink = new ArrayList<String>(); // Initialized list to store links
    private static int maxcount = 200; // Max daily apply quota for Naukri
    private static List<String> keywords = List.of("Java Developer","Java Backend Developer","Core Java Developer", "Java J2EE " ,"Java Spring boot", "SDE-II Java","Software Developer Java", "SDE-III Java"); // Add you list of role you want to apply comma separated
    private static String location = ""; // Add your location/city name for within India or remote
    private static int applied = 0; // Count of jobs applied successfully
    private static int failed = 0; // Count of Jobs failed
    private static Map<String, List<String>> applied_list = new HashMap<String, List<String>>();

    static {
        applied_list.put("passed", new ArrayList<String>());
        applied_list.put("failed", new ArrayList<String>());
    } // Saved list of applied and failed job links for manual review

    private static String edgedriverfile = "C:\\Users\\hp5pr\\Downloads\\edgedriver_win64_\\msedgedriver.exe";
    private static String yournaukriemail = "mandgevinay16@gmail.com    "; //Enter your username/email
    private static String yournaukripass = "vinay@16599"; //Enter your password
    WebDriver driver;
    String Logs = "";
    private boolean cacheExists() {
        try {
            return new java.io.File("naukri_cache.ser").exists();
        } catch (Exception e) {
            System.out.println("Error checking cache existence: " + e.getMessage());
            return false;
        }
    }
    @SuppressWarnings("unchecked")
    private void loadCache() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("naukri_cache.ser"))) {
            List<Cookie> cookies = (List<Cookie>) ois.readObject();
            driver.get("https://naukri.com"); // visit domain first
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
            if (cacheExists()) {
                driver.get("https://naukri.com"); // must visit first
                loadCache();
                driver.get("https://www.naukri.com/mnjuser/profile?id=");
            } else {
                driver.get("https://login.naukri.com/");
                WebElement uname = driver.findElement(By.id("usernameField"));
                uname.sendKeys(yournaukriemail);
                WebElement passwd = driver.findElement(By.id("passwordField"));
                passwd.sendKeys(yournaukripass);
                passwd.sendKeys(Keys.ENTER);
                Thread.sleep(5000); // wait for login
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
//            Thread.sleep(3000);
//            // Navigate to the profile section
//            driver.get("https://www.naukri.com/mnjuser/profile?id=");
//            Thread.sleep(3000);
//
//            // Example: Update profile details (e.g., update headline)
//            WebElement editHeadlineButton = driver.findElement(By.cssSelector("span.edit.icon"));
//            editHeadlineButton.click();
//            Thread.sleep(2000);
//
//            WebElement headlineInput = driver.findElement(By.id("resumeHeadlineTxt"));
//            headlineInput.clear();
//            headlineInput.sendKeys("Immediate Joiner - 3.5 years of experience of working as a software engineer with hands on experience on Java, Spring boot, Google Cloud Platform and react Js.");
//            WebElement saveButton = driver.findElement(By.xpath("//button[text()='Save']"));
//            saveButton.click();
//            Thread.sleep(2000);

            System.out.println("Profile updated successfully.");
        } catch (Exception e) {
            System.out.println("Failed to update profile: " + e.getMessage());
            e.printStackTrace();
        }

        for (String k : keywords) {
            for (int i = 1; i < 4 ; i++) {
                String url;
                if (location.equals("")) {
                    url = "https://www.naukri.com/" + k.toLowerCase().replace(' ', '-') + "-" + "jobs"+"-"+i +"?experience="+experience;
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
                	for(WebElement element : anchorElements) {
                        if(!element.getAttribute("href").contains("ambitionbox"))
                		joblink.add(element.getAttribute("href"));
                	}
                }catch (Exception e) {
                    e.printStackTrace();
				}                
                for (String str : joblink) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                   if(str!=null)
                    driver.get(str);
                    if (applied <= maxcount) {
                        try {
                            Thread.sleep(3000);
                            driver.findElement(By.xpath("//*[text()='Apply']")).click();
                            try {
                                Thread.sleep(2000);
                                if(!driver.findElements(By.cssSelector("li.botItem .botMsg span")).isEmpty()) {
                                    fillDetails();
                                }
                            } catch (InterruptedException e) {
                                System.out.println("Apply button not found");
                            }
                            applied++;
                            applied_list.get("passed").add(str);
                            System.out.println("Applied for " + i + " Count " + applied);
                        } catch (Exception e) {
                        	try {
                        	if(driver.findElement(By.xpath("//*[text()='Applied']"))!=null)
                                    continue;
                        	if(driver.findElement(By.xpath("//*[text()='I am intrested']"))!=null)
                        		driver.findElement(By.xpath("//*[text()='I am intrested']")).click();
                            
                        	}catch(Exception e1) {
                        	failed++;
                            System.out.println("Apply button not found");
                            Logs+="\n need to applied manually : "+ str;
                            applied_list.get("failed").add(str);
                            System.out.println(" Failed " + failed);
                        	}
                        }
                        try {
                            if (driver.findElement(By.xpath("//*[text()='Your daily quota has been expired.']")) != null) {
                                System.out.println("MAX Limit reached closing browser");
                                driver.close();
                                break;
                            }
                            if (driver.findElement(By.xpath("//*[text()=' 1. First Name']")) != null) {
                                driver.findElement(By.xpath("//input[@id='CUSTOM-FIRSTNAME']"))
                                        .sendKeys(firstname);
                            }
                            if (driver.findElement(By.xpath("//*[text()=' 2. Last Name']")) != null) {
                                driver.findElement(By.xpath("//input[@id='CUSTOM-LASTNAME']"))
                                        .sendKeys(lastname);
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
                try(FileWriter writer = new FileWriter("output.txt");
                    	BufferedWriter file = new BufferedWriter(writer);) {
                		file.write(Logs);
                }catch (Exception e) {
					e.printStackTrace();
				}
                System.out.println("Completed applying closing browser saving in applied jobs csv");
                try {
                    driver.close();
                } catch (Exception e) {
                }
                String csv_file = "naukriapplied.csv";      

        }
        driver.quit();
    }

   private void fillDetails() throws IOException {
       OllamaClient ollamaClient = new OllamaClient();
       String reply = "";
    while (true) {
        try {
            // Locate the last bot message
            List<WebElement> botMessages = driver.findElements(By.cssSelector("li.botItem .botMsg span"));
            WebElement lastMessage = botMessages.get(botMessages.size() - 1); // get the last one
            String messageText = lastMessage.getText().trim();
            System.out.println("Chatbot said: " + messageText);
            // Check for MCQ options
            List<WebElement> labels = driver.findElements(By.cssSelector("label.ssrc__label"));
            reply = ollamaClient.getAnswerForOptions(messageText, labels.stream().map(WebElement::getText).toList());
            if(!labels.isEmpty()){
            for (WebElement label : labels) {
                if (reply.toLowerCase().contains(label.getText().trim().toLowerCase())) {
                    label.click();
                    WebElement sendButton = driver.findElement(By.className("sendMsg")); // or appropriate class
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
