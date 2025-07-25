# Naukri Job Application Bot with Company Site Support

An enhanced Selenium Java bot that automates job applications on Naukri.com, including support for "Apply on Company Site" jobs that were previously not handled.

## 🚀 New Features

### ✅ Company Site Application Support
- **Automatic External Applications**: Now handles "Apply on Company Site" jobs automatically
- **Smart Form Detection**: Detects and fills common application forms on company websites
- **Multi-tab Management**: Seamlessly handles redirections to external company sites
- **Fallback Mechanisms**: Saves jobs for manual review when automation isn't possible

### ✅ Enhanced Application Logic
- **Intelligent Job Processing**: Tries multiple application methods in order of preference
- **Duplicate Detection**: Skips already applied jobs automatically
- **Comprehensive Statistics**: Tracks direct applications, company site applications, and failures separately
- **Better Error Handling**: More robust error handling with detailed logging

### ✅ AI-Powered Responses
- **Ollama Integration**: Uses local Ollama AI for intelligent responses to application questions
- **Fallback Responses**: Smart fallback answers when AI is not available
- **Configurable Answers**: Easy customization of default responses

## 📋 Prerequisites

1. **Java 16+** installed on your system
2. **Maven** for dependency management
3. **Microsoft Edge Browser** and **EdgeDriver**
4. **Ollama** (optional, for AI responses)

## 🛠️ Setup Instructions

### 1. Download EdgeDriver
- Download EdgeDriver from [Microsoft Edge WebDriver](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)
- Extract and note the path to `msedgedriver.exe`

### 2. Configure Application Settings
Edit `src/main/java/naukriBot/ApplicationConfig.java`:

```java
// Personal Information - CUSTOMIZE THESE
public static final String FIRST_NAME = "Your_First_Name";
public static final String LAST_NAME = "Your_Last_Name";
public static final String EMAIL = "your.email@example.com";
public static final String PHONE = "your_phone_number";
public static final String RESUME_PATH = "C:\\path\\to\\your\\resume.pdf"; // Optional
public static final int EXPERIENCE_YEARS = 3;

// Naukri Credentials - CUSTOMIZE THESE
public static final String NAUKRI_EMAIL = "your_naukri_email@example.com";
public static final String NAUKRI_PASSWORD = "your_naukri_password";

// WebDriver Configuration - CUSTOMIZE PATH
public static final String EDGE_DRIVER_PATH = "C:\\path\\to\\msedgedriver.exe";
```

### 3. Customize Job Search Preferences
Update job keywords and location in `ApplicationConfig.java`:

```java
public static final List<String> JOB_KEYWORDS = List.of(
    "Java Developer", "Spring Boot Developer", "Backend Developer"
    // Add your preferred job titles
);

public static final String LOCATION = "Bangalore"; // Or leave empty for all locations
```

### 4. Optional: Setup Ollama for AI Responses
- Install [Ollama](https://ollama.ai/)
- Run: `ollama pull llama2`
- Start Ollama service: `ollama serve`

## 🚀 Running the Application

### Using Maven:
```bash
mvn clean compile exec:java -Dexec.mainClass="naukriBot.NaukriMain"
```

### Using IDE:
1. Import the project into your IDE (IntelliJ IDEA, Eclipse, etc.)
2. Run `NaukriMain.java`

## 📊 Application Flow

1. **Login**: Automatically logs into Naukri (with session caching)
2. **Job Search**: Searches for jobs based on your keywords and location
3. **Smart Application Logic**:
   - First tries direct "Apply" button
   - If not available, attempts "Apply on Company Site"
   - Falls back to "I am interested" or "Save" options
4. **Company Site Handling**:
   - Opens company career pages in new tabs
   - Detects and fills application forms automatically
   - Submits applications when possible
   - Saves failed attempts for manual review
5. **Comprehensive Reporting**: Shows detailed statistics at the end

## 📈 Key Improvements Over Original

| Feature | Original | Enhanced Version |
|---------|----------|------------------|
| Company Site Jobs | ❌ Only saved | ✅ Automatically applies |
| Form Detection | ❌ None | ✅ Smart pattern matching |
| Tab Management | ❌ Single tab only | ✅ Multi-tab support |
| Error Handling | ❌ Basic | ✅ Comprehensive |
| Statistics | ❌ Basic counts | ✅ Detailed breakdown |
| Configuration | ❌ Hardcoded | ✅ Centralized config |
| AI Integration | ❌ None | ✅ Ollama support |

## 🔧 Advanced Configuration

### Company Site Application Settings
```java
public static final boolean ENABLE_COMPANY_SITE_APPLICATIONS = true;
public static final int COMPANY_SITE_TIMEOUT_SECONDS = 30;
public static final boolean KEEP_FAILED_TABS_OPEN = false; // For manual review
```

### Default Responses for Applications
```java
public static final String DEFAULT_SALARY_EXPECTATION = "As per industry standards";
public static final String DEFAULT_NOTICE_PERIOD = "Immediate";
public static final String DEFAULT_SKILLS = "Java, Spring Boot, Microservices, REST API";
```

## 📝 Output and Logs

The application generates:
- **Console Output**: Real-time progress and statistics
- **output.txt**: Detailed logs of failed applications
- **naukri_cache.ser**: Session cache for faster subsequent runs
- **Comprehensive Summary**: Final statistics showing all application types

## ⚠️ Important Notes

1. **Rate Limiting**: Respects Naukri's daily application limits
2. **Session Management**: Caches login sessions to avoid repeated logins
3. **Error Recovery**: Continues processing even if individual applications fail
4. **Manual Review**: Failed company site applications are flagged for manual review
5. **Browser Compatibility**: Currently configured for Microsoft Edge (easily changeable)

## 🤝 Contributing

Feel free to contribute by:
- Adding support for more browsers
- Improving form detection patterns
- Adding new application sites
- Enhancing AI response quality

## 📄 License

This project is for educational purposes. Please use responsibly and in accordance with Naukri.com's terms of service.
