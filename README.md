# MultiBank Web UI Automation Framework

A comprehensive Selenium-based test automation framework for MultiBank web application, featuring cross-browser testing, video recording, detailed reporting, and CI/CD integration with GitHub Actions.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Test Categories](#test-categories)
- [Reports & Artifacts](#reports--artifacts)
- [CI/CD Pipeline](#cicd-pipeline)
- [Email Notifications](#email-notifications)
- [Video Recording](#video-recording)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)

## Overview

This framework automates testing of the MultiBank trading platform, covering:
- Navigation and UI components
- Spot trading functionality
- App download links
- Footer marketing banners
- Responsive design elements

**Technology Stack:**
- Java 17
- Selenium WebDriver 4.8.3
- TestNG 7.5
- ExtentReports 5.0.9
- Maven 3.11.0
- FFmpeg (for video recording)

## Features

- **Cross-Browser Support**: Chrome, Firefox, Edge
- **Headless Execution**: Optimized for CI/CD environments
- **Video Recording**: Automatic recording of failed tests
- **Screenshot Capture**: On test failures
- **Retry Mechanism**: Configurable retry logic for flaky tests
- **Detailed Reporting**: ExtentReports with rich HTML output
- **Email Notifications**: SendGrid integration
- **GitHub Actions**: Complete CI/CD pipeline
- **Data-Driven Testing**: JSON-based test data management

## Project Structure

```
multibank-web-ui/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── Base/
│   │   │   │   └── Base.java              # Base test class with WebDriver setup
│   │   │   ├── pages/
│   │   │   │   └── HomePage.java          # Page Object Model for home page
│   │   │   └── utilities/
│   │   │       ├── CommonActions.java     # Utility methods and actions
│   │   │       ├── RetryAnalyzer.java     # Test retry logic
│   │   │       ├── RetryListener.java     # TestNG retry listener
│   │   │       ├── TestDataProvider.java  # JSON data reader
│   │   │       ├── TestListener.java      # ExtentReports listener
│   │   │       └── VideoRecorder.java     # FFmpeg video recording
│   │   └── resources/
│   │       ├── expected_texts.json        # Test data and expected values
│   │       └── web_config.properties      # Configuration properties
│   └── test/
│       └── java/
│           └── tests/                     # Test classes
│               ├── APP_001_002_AppDownloadLinksTest.java
│               ├── FOOT_001_FooterMarketingBannersTest.java
│               ├── NAV_001_VerifyTopNavigationHeaderVisibilityTest.java
│               ├── NAV_002_TopNavigationDropdownInteractionTest.java
│               ├── SPOT_001_SpotTradingCategoriesTest.java
│               └── SPOT_002_SpotTradingStructuralTest.java
├── reports/
│   ├── screenshots/                       # Screenshot artifacts
│   ├── videos/                           # Video recordings
│   ├── UI_Automation_Report.html         # ExtentReports output
│   └── test_summary.txt                  # Test execution summary
├── .github/
│   └── workflows/
│       └── multibank-qa-automation.yml   # GitHub Actions pipeline
├── generate_report.sh                    # Report generation script
├── pom.xml                              # Maven dependencies
├── testng.xml                           # TestNG test suite configuration
└── README.md
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Git**
- **Browser Drivers** (handled by WebDriverManager)

For CI/CD:
- **FFmpeg** (for video recording)
- **Xvfb** (virtual display for headless environments)

## Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/multibank-web-ui.git
cd multibank-web-ui
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Configure Browser
Edit `src/main/resources/web_config.properties`:
```properties
browser=chrome
multibank_url=https://trade.multibank.io/
```

## Configuration

### Browser Configuration
The framework supports multiple browsers configured in `Base.java`:

```java
// Chrome (default)
browser=chrome

// Firefox
browser=firefox

// Microsoft Edge
browser=edge
```

### Test Data Configuration
Test data is managed in `src/main/resources/expected_texts.json`:

```json
{
  "navigation": {
    "mainMenuItems": ["Dashboard", "Markets", "Trade", "Features", "About Us", "Support"]
  },
  "spot": {
    "categories": ["All", "Favorites", "USDT", "BTC", "ETH"]
  }
}
```

## Running Tests

### Local Execution

#### Run All Tests
```bash
mvn clean test
```

#### Run Specific Test Suite
```bash
mvn clean test -Dsurefire.suiteXmlFiles=testng.xml
```

#### Run with Specific Browser
```bash
mvn clean test -Dbrowser=firefox
```

#### Run Specific Test Class
```bash
mvn clean test -Dtest=NAV_001_VerifyTopNavigationHeaderVisibilityTest
```

### Headless Execution
```bash
mvn clean test -Djava.awt.headless=true
```

## Test Categories

### Navigation Tests (NAV)
- **NAV-001**: Top navigation header visibility and menu items
- **NAV-002**: Dropdown interactions and link functionality

### Spot Trading Tests (SPOT)
- **SPOT-001**: Trading categories and pair display validation
- **SPOT-002**: Structural elements and data formatting

### App Integration Tests (APP)
- **APP-001**: App Store download link validation
- **APP-002**: Google Play download link validation

### Footer Tests (FOOT)
- **FOOT-001**: Marketing banners visibility and content

## Reports & Artifacts

### ExtentReports
- **Location**: `reports/UI_Automation_Report.html`
- **Features**: Rich HTML reports with screenshots, test details, and execution timeline
- **Theme**: Dark theme with comprehensive test metrics

### Test Summary
- **Location**: `reports/test_summary.txt`
- **Content**: Test counts, execution time, environment details

### Screenshots
- **Location**: `reports/screenshots/`
- **Trigger**: Automatic capture on test failures
- **Format**: PNG with timestamp and test name

### Video Recordings
- **Location**: `reports/videos/`
- **Trigger**: Failed tests only (in CI/CD environments)
- **Format**: MP4 with H.264 encoding

## CI/CD Pipeline

The GitHub Actions pipeline (`multibank-qa-automation.yml`) includes:

### Pipeline Stages
1. **Environment Setup**: Java 17, Maven, FFmpeg, Virtual Display
2. **Dependency Caching**: Maven dependencies cached for faster builds
3. **Build**: Compilation with Lombok compatibility fixes
4. **Test Execution**: Full test suite with video recording
5. **Artifact Collection**: Reports, screenshots, videos
6. **Email Notifications**: SendGrid integration for results

### Pipeline Features
- **Cross-platform**: Runs on Ubuntu latest
- **Parallel Execution**: Optimized for GitHub Actions
- **Artifact Management**: Automatic upload of all test outputs
- **Email Integration**: Automated report distribution

### Environment Variables
```yaml
ENVIRONMENT: "Staging"
TESTER: "Abdelazeem Hussein"
SENDGRID_API_KEY: ${{ secrets.SENDGRID_API_KEY }}
```

## Email Notifications

### SendGrid Configuration
1. Create SendGrid account and API key
2. Add `SENDGRID_API_KEY` to GitHub Secrets
3. Configure recipients in the workflow file

### Email Content
- **Subject**: Multibank Web Automation Test Report
- **Body**: HTML formatted test summary
- **Attachments**: ExtentReports HTML file

### Troubleshooting Email Issues
- Verify SendGrid API key and account limits
- Check sender email verification status
- Review recipient email addresses format

## Video Recording

### How It Works
- **Trigger**: Automatically starts for each test in CI/CD
- **Technology**: FFmpeg with X11 screen capture
- **Storage**: Only failed test videos are retained
- **Cleanup**: Passed test videos are automatically deleted

### Local Video Recording
Video recording is disabled in local environments by default. To enable:

```java
// In TestListener.java, modify isPipelineEnvironment()
private boolean isPipelineEnvironment() {
    return true; // Force enable for local testing
}
```

### Video Configuration
- **Resolution**: 1920x1080
- **Frame Rate**: 10 fps (optimized for file size)
- **Codec**: H.264 with ultrafast preset
- **Format**: MP4

## Troubleshooting

### Common Issues

#### Build Failures with Java 17
**Error**: `IllegalAccessError` with Lombok processor

**Solution**: The pipeline includes automatic fallback:
```bash
mvn clean install -DskipTests -Dmaven.compiler.proc=none
# Falls back to:
export MAVEN_OPTS="--add-opens jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED ..."
```

#### Browser Driver Issues
**Error**: Browser driver not found

**Solution**: Framework uses WebDriverManager for automatic driver management:
```java
WebDriverManager.chromedriver().setup();
```

#### Video Recording Failures
**Error**: FFmpeg not available or X11 capture fails

**Solution**: Check virtual display setup:
```bash
export DISPLAY=:99
Xvfb :99 -screen 0 1920x1080x24 &
```

#### Test Data Not Loading
**Error**: `expected_texts.json` not found

**Solution**: Verify file location in `src/main/resources/` and check JSON syntax.

### Debug Mode
Enable detailed logging:
```bash
mvn clean test -X -Dlog4j2.debug=true
```

### Browser Debug
For debugging browser issues, disable headless mode:
```java
// In Base.java, comment out:
// chromeOptions.addArguments("--headless=new");
```

## Contributing

### Code Standards
- Follow Java naming conventions
- Use Page Object Model pattern
- Add comprehensive JavaDoc comments
- Include test descriptions and categories

### Test Development
1. Create test class with proper naming: `CATEGORY_###_DescriptionTest.java`
2. Extend `Base` class for WebDriver access
3. Use `@Test(description = "...")` annotations
4. Add retry analyzer: `@Test(retryAnalyzer = RetryAnalyzer.class)`
5. Include cleanup in `@AfterMethod`

### Adding New Tests
1. Create test class in `src/test/java/tests/`
2. Add test data to `expected_texts.json` if needed
3. Update `testng.xml` to include new test class
4. Add Page Object methods in appropriate page class
5. Update this README with test documentation

### Pull Request Process
1. Create feature branch from `main`
2. Add/update tests with proper documentation
3. Ensure all tests pass locally
4. Update README if adding new features
5. Submit pull request with detailed description

---

**Framework Version**: 1.0  
**Last Updated**: September 2025  
**Maintainer**: Abdelazeem Hussein