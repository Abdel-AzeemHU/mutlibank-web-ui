package Base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.interactions.Actions;
import pages.HomePage;
import utilities.CommonActions;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utilities.TestDataProvider;

public class Base {

    public static WebDriver driver;
    public static Logger log = LogManager.getLogger(Base.class);
    public static Properties prop = null;

    protected CommonActions commonActions;
    protected HomePage homePage;
    protected TestDataProvider testData;

    String randomNamePick;
    String receiverName;

    public static WebDriver getDriver() {
        return driver;
    }

    protected Properties getProp() {
        return prop; // assuming prop is defined and loaded somewhere
    }

    @BeforeMethod
    public void setUp() {

        String url = getProp().getProperty("multibank_url");
        String browserName = getProp().getProperty("browser");

        // Setup driver using WebDriverManager
        setupWebDriverManager(browserName);
        driver = createDriverInstance(browserName);

        // Initialize WebDriver
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
        driver.get(url);

        // Initialize common objects
        commonActions = new CommonActions();
        homePage = new HomePage(driver);
        testData = new TestDataProvider();
    }

    public void startBackofficeBrowser() {
        startBrowserWithUrl(prop.getProperty("stg-backoffice.url"));
    }

    private void startBrowserWithUrl(String url) {
        String browserName = prop.getProperty("browser");
        System.setProperty("hudson.model.DirectoryBrowserSupport.CSP",
                "sandbox allow-scripts; default-src 'self'; script-src * 'unsafe-eval'; img-src *; style-src * 'unsafe-inline'; font-src *");

        try {
            setupWebDriverManager(browserName);
            driver = createDriverInstance(browserName);

            long start = System.currentTimeMillis();
            driver.get(url);
            long finish = System.currentTimeMillis();
            long totalTime = (finish - start) / 1000;

            driver.manage().window().fullscreen();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

            System.out.println("Total time to respond: " + totalTime + " seconds");
            log.info("Total time to respond: " + totalTime + " seconds");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to start " + browserName + " browser");
            log.error("Unable to start " + browserName + " browser", e);
        }
    }

    private void setupWebDriverManager(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }

    private Class<?> getDriverClass(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chrome":
                return ChromeDriver.class;
            case "firefox":
                return FirefoxDriver.class;
            case "edge":
                return EdgeDriver.class;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }

    private WebDriver createDriverInstance(String browserName) {
        switch (browserName.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                // Anti-bot detection measures
                chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36");
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
                chromeOptions.setExperimentalOption("useAutomationExtension", false);

                chromeOptions.addArguments("--enable-geolocation");
                chromeOptions.addArguments("--allow-running-insecure-content");
                chromeOptions.addArguments("--disable-web-security");
                chromeOptions.addArguments("--disable-features=VizDisplayCompositor");
                chromeOptions.addArguments("--enable-javascript");
                chromeOptions.addArguments("--headless=new");
                return new ChromeDriver(chromeOptions);

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();

                // Essential only
//				firefoxOptions.addArguments("--headless");

                // ONLY the 3 most critical CI/CD arguments
                firefoxOptions.addArguments("--no-sandbox");
                firefoxOptions.addArguments("--disable-dev-shm-usage");
                firefoxOptions.addArguments("--width=1920");
                firefoxOptions.addArguments("--height=1080");

                // Keep your original working argument
                firefoxOptions.addArguments("--private");

                // Essential capability
                firefoxOptions.setCapability("moz:firefoxOptions", true);

                return new FirefoxDriver(firefoxOptions);

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.addArguments("--inprivate", "--disable-dev-shm-usage", "--remote-allow-origins=*");
                // edgeOptions.setHeadless(true);
                return new EdgeDriver(edgeOptions);

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browserName);
        }
    }


    public WebElement waitForVisibility(WebElement element) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(15))
                .ignoring(StaleElementReferenceException.class)
                .until(driver -> {
                    try {
                        return ExpectedConditions.visibilityOf(element).apply(driver);
                    } catch (StaleElementReferenceException e) {
                        return null; // Return null to continue waiting
                    }
                });
    }

    public void waitForVisibilityList(List<WebElement> elements) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElements(elements));
    }

    public WebElement waitForClickability(WebElement element) {
        return new WebDriverWait(getDriver(), Duration.ofSeconds(10))
                .ignoring(StaleElementReferenceException.class)
                .until(driver -> {
                    try {
                        return ExpectedConditions.elementToBeClickable(element).apply(driver);
                    } catch (StaleElementReferenceException e) {
                        return null; // Return null to continue waiting
                    }
                });
    }

    public void waitForPageLoad() {
        new WebDriverWait(driver, Duration.ofSeconds(60)).until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    public void waitForUrlContains(String keyword) {
        new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.urlContains(keyword));
    }

    public static void enterText(WebElement element, CharSequence text) {
        if (element != null && text != null && text.length() > 0) {
            element.clear(); // Clear any existing text in the input field
            element.sendKeys(text); // Send the new text
        } else {
            log.warn("Element or text is null or empty");
        }
    }

    public void closeBrowser() {
        if (driver != null) {
            try {
                driver.manage().deleteAllCookies();
                driver.quit();
                log.info("Browser closed and cookies deleted");
            } catch (Exception e) {
                log.warn("Error during browser cleanup: " + e.getMessage());
            } finally {
                driver = null; // CRITICAL: Release the reference
            }
        }
    }

    public void pressEnter() throws InterruptedException {
        try {
            Actions actions = new Actions(driver);
            actions.sendKeys(Keys.ENTER).perform();
            log.info("Pressed ENTER key globally");
            Thread.sleep(500);
        } catch (Exception e) {
            log.error("Failed to press ENTER key: " + e.getMessage());
            throw e;
        }
    }

}