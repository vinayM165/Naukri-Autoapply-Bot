package naukriBot;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import java.util.*;
import java.util.List;

public class CompanySiteApplicationHandler {
    private WebDriver driver;
    private WebDriverWait wait;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String resumePath;
    private int experience;
    
    // Common application form field patterns
    private static final String[] FIRST_NAME_PATTERNS = {
        "firstname", "first_name", "fname", "first-name", "givenName", "name"
    };
    
    private static final String[] LAST_NAME_PATTERNS = {
        "lastname", "last_name", "lname", "last-name", "familyName", "surname"
    };
    
    private static final String[] EMAIL_PATTERNS = {
        "email", "emailaddress", "email_address", "mail", "e-mail"
    };
    
    private static final String[] PHONE_PATTERNS = {
        "phone", "mobile", "contact", "phonenumber", "phone_number", "tel", "telephone"
    };
    
    private static final String[] RESUME_PATTERNS = {
        "resume", "cv", "upload", "file", "attachment", "document"
    };
    
    private static final String[] EXPERIENCE_PATTERNS = {
        "experience", "exp", "years", "yoe", "work_experience", "total_experience"
    };
    
    public CompanySiteApplicationHandler(WebDriver driver, String firstname, String lastname, 
                                       String email, String phone, String resumePath, int experience) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.resumePath = resumePath;
        this.experience = experience;
    }
    
    public boolean handleCompanySiteApplication(String jobUrl) {
        try {
            System.out.println("Attempting to apply on company site: " + jobUrl);
            
            // Store original window handle
            String originalWindow = driver.getWindowHandle();
            
            // Click on "Apply on company site" button
            if (!clickApplyOnCompanySite()) {
                return false;
            }
            
            // Wait for new tab/window to open
            Thread.sleep(3000);
            
            // Switch to the new tab
            Set<String> allWindows = driver.getWindowHandles();
            for (String windowHandle : allWindows) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }
            
            // Try to detect and fill application form
            boolean applicationSuccess = detectAndFillApplicationForm();
            
            if (applicationSuccess) {
                System.out.println("Successfully applied on company site!");
                // Close the company site tab
                driver.close();
                driver.switchTo().window(originalWindow);
                return true;
            } else {
                System.out.println("Could not auto-fill company application form. Saving for manual review.");
                // Keep the tab open for manual review and switch back
                driver.switchTo().window(originalWindow);
                return false;
            }
            
        } catch (Exception e) {
            System.out.println("Error handling company site application: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean clickApplyOnCompanySite() {
        try {
            // Try different selectors for "Apply on company site" button
            String[] applySelectors = {
                "//*[contains(text(), 'Apply on company site')]",
                "//*[contains(text(), 'Apply on Company Site')]",
                "//*[contains(text(), 'Apply externally')]",
                "//*[contains(text(), 'Apply on External Site')]",
                "//button[contains(@class, 'external-apply')]",
                "//a[contains(@class, 'external-apply')]"
            };
            
            for (String selector : applySelectors) {
                try {
                    WebElement applyButton = driver.findElement(By.xpath(selector));
                    if (applyButton.isDisplayed() && applyButton.isEnabled()) {
                        applyButton.click();
                        return true;
                    }
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // Continue to next selector
                }
            }
            
            return false;
        } catch (Exception e) {
            System.out.println("Error clicking apply on company site: " + e.getMessage());
            return false;
        }
    }
    
    private boolean detectAndFillApplicationForm() {
        try {
            // Wait for page to load
            Thread.sleep(3000);
            
            // Check if we're on a careers page or application form
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            String pageTitle = driver.getTitle().toLowerCase();
            
            if (!isLikelyApplicationPage(currentUrl, pageTitle)) {
                // Try to find and click apply button on careers page
                if (!findAndClickApplyButton()) {
                    return false;
                }
                Thread.sleep(3000);
            }
            
            // Try to fill the application form
            return fillApplicationForm();
            
        } catch (Exception e) {
            System.out.println("Error detecting and filling application form: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean isLikelyApplicationPage(String url, String title) {
        String[] applicationIndicators = {
            "apply", "application", "job", "career", "hiring", "recruit", "form"
        };
        
        for (String indicator : applicationIndicators) {
            if (url.contains(indicator) || title.contains(indicator)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean findAndClickApplyButton() {
        try {
            String[] applyButtonSelectors = {
                "//button[contains(text(), 'Apply')]",
                "//a[contains(text(), 'Apply')]",
                "//input[@value='Apply']",
                "//button[contains(@class, 'apply')]",
                "//a[contains(@class, 'apply')]",
                "//button[contains(text(), 'Submit Application')]",
                "//a[contains(text(), 'Submit Application')]"
            };
            
            for (String selector : applyButtonSelectors) {
                try {
                    WebElement applyButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(selector)));
                    if (applyButton.isDisplayed()) {
                        applyButton.click();
                        return true;
                    }
                } catch (TimeoutException e) {
                    // Continue to next selector
                }
            }
            
            return false;
        } catch (Exception e) {
            System.out.println("Error finding apply button: " + e.getMessage());
            return false;
        }
    }
    
    private boolean fillApplicationForm() {
        try {
            boolean formFilled = false;
            
            // Fill first name
            if (fillFieldByPatterns(FIRST_NAME_PATTERNS, firstname)) {
                formFilled = true;
            }
            
            // Fill last name
            if (fillFieldByPatterns(LAST_NAME_PATTERNS, lastname)) {
                formFilled = true;
            }
            
            // Fill email
            if (fillFieldByPatterns(EMAIL_PATTERNS, email)) {
                formFilled = true;
            }
            
            // Fill phone
            if (fillFieldByPatterns(PHONE_PATTERNS, phone)) {
                formFilled = true;
            }
            
            // Fill experience
            if (fillFieldByPatterns(EXPERIENCE_PATTERNS, String.valueOf(experience))) {
                formFilled = true;
            }
            
            // Upload resume if path is provided
            if (resumePath != null && !resumePath.isEmpty()) {
                if (uploadResume()) {
                    formFilled = true;
                }
            }
            
            // Try to submit the form
            if (formFilled) {
                return submitApplicationForm();
            }
            
            return false;
            
        } catch (Exception e) {
            System.out.println("Error filling application form: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private boolean fillFieldByPatterns(String[] patterns, String value) {
        for (String pattern : patterns) {
            try {
                // Try by name attribute
                WebElement field = driver.findElement(By.name(pattern));
                if (field.isDisplayed() && field.isEnabled()) {
                    field.clear();
                    field.sendKeys(value);
                    return true;
                }
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // Continue
            }
            
            try {
                // Try by id attribute
                WebElement field = driver.findElement(By.id(pattern));
                if (field.isDisplayed() && field.isEnabled()) {
                    field.clear();
                    field.sendKeys(value);
                    return true;
                }
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // Continue
            }
            
            try {
                // Try by partial attribute match
                WebElement field = driver.findElement(By.xpath("//input[contains(@name, '" + pattern + "') or contains(@id, '" + pattern + "') or contains(@placeholder, '" + pattern + "')]"));
                if (field.isDisplayed() && field.isEnabled()) {
                    field.clear();
                    field.sendKeys(value);
                    return true;
                }
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // Continue
            }
        }
        return false;
    }
    
    private boolean uploadResume() {
        try {
            for (String pattern : RESUME_PATTERNS) {
                try {
                    // Try by name attribute
                    WebElement fileInput = driver.findElement(By.name(pattern));
                    if (fileInput.getAttribute("type").equals("file")) {
                        fileInput.sendKeys(resumePath);
                        return true;
                    }
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // Continue
                }
                
                try {
                    // Try by xpath
                    WebElement fileInput = driver.findElement(By.xpath("//input[@type='file' and (contains(@name, '" + pattern + "') or contains(@id, '" + pattern + "'))]"));
                    fileInput.sendKeys(resumePath);
                    return true;
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // Continue
                }
            }
            return false;
        } catch (Exception e) {
            System.out.println("Error uploading resume: " + e.getMessage());
            return false;
        }
    }
    
    private boolean submitApplicationForm() {
        try {
            String[] submitSelectors = {
                "//button[contains(text(), 'Submit')]",
                "//input[@type='submit']",
                "//button[@type='submit']",
                "//button[contains(text(), 'Apply')]",
                "//button[contains(text(), 'Send Application')]",
                "//a[contains(text(), 'Submit')]"
            };
            
            for (String selector : submitSelectors) {
                try {
                    WebElement submitButton = driver.findElement(By.xpath(selector));
                    if (submitButton.isDisplayed() && submitButton.isEnabled()) {
                        submitButton.click();
                        
                        // Wait for submission confirmation
                        Thread.sleep(3000);
                        
                        // Check for success indicators
                        if (isApplicationSubmitted()) {
                            return true;
                        }
                    }
                } catch (org.openqa.selenium.NoSuchElementException e) {
                    // Continue to next selector
                }
            }
            
            return false;
        } catch (Exception e) {
            System.out.println("Error submitting application form: " + e.getMessage());
            return false;
        }
    }
    
    private boolean isApplicationSubmitted() {
        try {
            String pageSource = driver.getPageSource().toLowerCase();
            String[] successIndicators = {
                "thank you", "application submitted", "success", "received", 
                "confirmation", "applied successfully", "application received"
            };
            
            for (String indicator : successIndicators) {
                if (pageSource.contains(indicator)) {
                    return true;
                }
            }
            
            // Check URL for success indicators
            String currentUrl = driver.getCurrentUrl().toLowerCase();
            if (currentUrl.contains("success") || currentUrl.contains("thank") || currentUrl.contains("confirm")) {
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.out.println("Error checking application submission: " + e.getMessage());
            return false;
        }
    }
}