package naukriBot;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NaukriJobApply {
    private static String firstname = ""; // Add your LastName
    private static String lastname = ""; // Add your FirstName
    public static int experience = 2;//Add your experience
    private static List<String> joblink = new ArrayList<String>(); // Initialized list to store links
    private static int maxcount = 100; // Max daily apply quota for Naukri
    private static String[] keywords = {"Java Developer Springboot","Core Java Developer","Java Developer"}; // Add you list of role you want to apply comma separated
    private static String location = ""; // Add your location/city name for within India or remote
    private static int applied = 0; // Count of jobs applied successfully
    private static int failed = 0; // Count of Jobs failed
    private static Map<String, List<String>> applied_list = new HashMap<String, List<String>>();

    static {
        applied_list.put("passed", new ArrayList<String>());
        applied_list.put("failed", new ArrayList<String>());
    } // Saved list of applied and failed job links for manual review

    private static String edgedriverfile = "C:\\Users\\hp5pr\\Downloads\\edgedriver_win64\\msedgedriver.exe"; // Please add your filepath here
    private static String yournaukriemail = ""; //Enter your username/email
    private static String yournaukripass = ""; //Enter your password 
    WebDriver driver;
    String Logs = "";
    public void applyStart() {  
        try {
        	System.setProperty("webdriver.edge.driver", edgedriverfile); //Change driver according to the browser 
            driver = new EdgeDriver();
            driver.get("https://login.naukri.com/");
            WebElement uname = driver.findElement(By.id("usernameField"));
            uname.sendKeys(yournaukriemail);
            WebElement passwd = driver.findElement(By.id("passwordField"));
            passwd.sendKeys(yournaukripass);
            passwd.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            System.out.println("Webdriver exception");
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (String k : keywords) {
            for (int i = 1; i < 5	; i++) {
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
                	List<WebElement> anchors = driver.findElements(By.cssSelector("a.title.ellipsis"));
                	for(WebElement element : anchors) {
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
                    driver.get(str);
                    if (applied <= maxcount) {
                        try {
                            Thread.sleep(3000);
                            driver.findElement(By.xpath("//*[text()='Apply']")).click();
                            try {
                                Thread.sleep(2000);
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
}
