package naukriBot;

import java.util.List;

public class ApplicationConfig {
    
    // Personal Information - CUSTOMIZE THESE
    public static final String FIRST_NAME = "John";
    public static final String LAST_NAME = "Doe";
    public static final String EMAIL = "john.doe@example.com";
    public static final String PHONE = "9876543210";
    public static final String RESUME_PATH = ""; // Full path to your resume file (leave empty if not available)
    public static final int EXPERIENCE_YEARS = 3;
    
    // Naukri Credentials - CUSTOMIZE THESE
    public static final String NAUKRI_EMAIL = "demo@gmail.com";
    public static final String NAUKRI_PASSWORD = "password";
    
    // WebDriver Configuration - CUSTOMIZE PATH
    public static final String EDGE_DRIVER_PATH = "C:\\Users\\hp5pr\\Downloads\\edgedriver_win64_\\msedgedriver.exe";
    
    // Job Search Configuration
    public static final List<String> JOB_KEYWORDS = List.of(
        "Java Developer", "Java Backend Developer", "Core Java Developer",
        "Java J2EE", "Java Spring boot", "SDE-II Java",
        "Software Developer Java", "SDE-III Java"
    );
    
    public static final String LOCATION = ""; // Leave empty for all locations, or specify city name
    public static final int MAX_DAILY_APPLICATIONS = 200;
    
    // Company Site Application Settings
    public static final boolean ENABLE_COMPANY_SITE_APPLICATIONS = true;
    public static final int COMPANY_SITE_TIMEOUT_SECONDS = 30;
    public static final boolean KEEP_FAILED_TABS_OPEN = false; // Set to true for manual review
    
    // Advanced Settings
    public static final int PAGE_LOAD_WAIT_SECONDS = 3;
    public static final int APPLICATION_WAIT_SECONDS = 2;
    public static final boolean VERBOSE_LOGGING = true;
    
    // Default responses for common application questions
    public static final String DEFAULT_SALARY_EXPECTATION = "As per industry standards";
    public static final String DEFAULT_NOTICE_PERIOD = "Immediate";
    public static final String DEFAULT_RELOCATION_ANSWER = "Yes";
    public static final String DEFAULT_SKILLS = "Java, Spring Boot, Microservices, REST API, MySQL";
    public static final String DEFAULT_WHY_INTERESTED = "I am interested in this role as it aligns with my career goals and technical expertise.";
    
    private ApplicationConfig() {
        // Utility class - prevent instantiation
    }
}