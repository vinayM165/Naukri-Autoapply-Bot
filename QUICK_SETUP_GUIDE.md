# Quick Setup Guide - Enhanced Naukri Job Application Bot

## 🚀 Quick Start (5 Minutes)

### Step 1: Download WebDriver
1. Download Microsoft Edge WebDriver from: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
2. Extract to a folder (e.g., `C:\webdrivers\msedgedriver.exe`)

### Step 2: Configure Your Details
Open `src/main/java/naukriBot/ApplicationConfig.java` and update:

```java
// Your Personal Information
public static final String FIRST_NAME = "John";           // ← Change this
public static final String LAST_NAME = "Doe";             // ← Change this  
public static final String EMAIL = "john@example.com";    // ← Change this
public static final String PHONE = "9876543210";          // ← Change this
public static final String RESUME_PATH = "C:\\Users\\YourName\\Documents\\resume.pdf"; // ← Optional

// Your Naukri Account
public static final String NAUKRI_EMAIL = "your_naukri_email@example.com";    // ← Change this
public static final String NAUKRI_PASSWORD = "your_naukri_password";          // ← Change this

// WebDriver Path
public static final String EDGE_DRIVER_PATH = "C:\\webdrivers\\msedgedriver.exe"; // ← Change this

// Job Search Keywords (customize as needed)
public static final List<String> JOB_KEYWORDS = List.of(
    "Java Developer", "Python Developer", "Software Engineer" // ← Customize this
);
```

### Step 3: Compile and Run
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="naukriBot.NaukriMain"
```

## 📊 What You'll See

### Console Output Example:
```
Profile updated successfully.
Processing page 1...
Found 'Apply on company site' button for: https://company.com/jobs/123
Successfully applied on company site for page 1 Count 1
Applied directly for page 1 Count 2
Clicked 'I am interested' - Count 3

=== APPLICATION SUMMARY ===
Direct Applications: 45
Company Site Applications: 23
Failed Applications: 2
Total Processed: 70
```

## 🎯 Key Features Now Available

### ✅ Direct Applications
- Traditional Naukri "Apply" button jobs
- Automatic chatbot interaction
- Form filling with your details

### ✅ Company Site Applications (NEW!)
- Automatically handles "Apply on company site" jobs
- Opens external company websites
- Fills application forms automatically
- Uploads resume when possible

### ✅ Smart Fallbacks
- "I am interested" button clicks
- Job saving for manual review
- Comprehensive error handling

## ⚙️ Advanced Configuration

### Customize Job Search:
```java
public static final List<String> JOB_KEYWORDS = List.of(
    "Your Skill 1", "Your Skill 2", "Your Role"
);
public static final String LOCATION = "Bangalore"; // Or leave empty for all
public static final int MAX_DAILY_APPLICATIONS = 200;
```

### Company Site Settings:
```java
public static final boolean ENABLE_COMPANY_SITE_APPLICATIONS = true;
public static final int COMPANY_SITE_TIMEOUT_SECONDS = 30;
public static final boolean KEEP_FAILED_TABS_OPEN = false; // Set true for manual review
```

## 🛟 Troubleshooting

### Common Issues:

**1. WebDriver Not Found**
- Ensure the path in `EDGE_DRIVER_PATH` is correct
- Download the correct version for your Edge browser

**2. Login Issues**
- Verify your Naukri credentials in `ApplicationConfig.java`
- Check if 2FA is enabled (disable temporarily)

**3. No Jobs Found**
- Adjust your `JOB_KEYWORDS` to match available jobs
- Try broader search terms

**4. Company Site Applications Failing**
- Some complex company sites may not be auto-fillable
- Check the logs for specific error messages
- Failed applications are saved for manual review

## 📈 Monitoring Results

### Files Created:
- `output.txt` - Detailed logs of all activities
- `naukriapplied.csv` - List of all applied jobs
- `naukri_cache.ser` - Login session cache

### Statistics Tracking:
- **Direct Applications**: Traditional Naukri applications
- **Company Site Applications**: External website applications
- **Failed Applications**: Jobs that couldn't be processed automatically

## 🔄 Daily Usage

1. **Morning**: Run the bot for fresh job postings
2. **Review**: Check `output.txt` for any manual actions needed
3. **Adjust**: Update keywords based on job market trends
4. **Monitor**: Track success rates and adjust settings

## 🎯 Pro Tips

1. **Resume Path**: Add your resume path for automatic uploads
2. **Keywords**: Use specific job titles for better targeting
3. **Timing**: Run during business hours for better response rates
4. **Review**: Manually check complex company applications
5. **Backup**: Keep your original settings backed up

---

**Ready to apply to 2x more jobs with the enhanced company site functionality!** 🚀