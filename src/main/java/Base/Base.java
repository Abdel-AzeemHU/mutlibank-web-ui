package Base;

import io.github.bonigarcia.wdm.WebDriverManager;
import pages.*;
import utilities.common_actions;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class base {

	public static WebDriver driver;
	public static Logger log = LogManager.getLogger(base.class);
	public static Properties prop = null;
	
	protected WebDriver merchantDriver;
    protected WebDriver backofficeDriver;

	protected HomePagePowerPlanet homePagePowerPlanet;
	
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

		// Initialize properties if not already done
		if (prop == null) {
			prop = new Properties();
			try (InputStream input = getClass().getClassLoader().getResourceAsStream("web_config.properties")) {
				prop.load(input);
			} catch (IOException ex) {
				log.error("Error loading properties file", ex);
				throw new RuntimeException("Failed to load properties file", ex);
			}
		}

		String url = prop.getProperty("power-planet.url");
		String browserName = prop.getProperty("browser");

		// Setup driver using WebDriverManager
		setupWebDriverManager(browserName);
		driver = createDriverInstance(browserName);

		// Initialize WebDriver		
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(40));
		driver.get(url);

		// Initialize common objects
		homePagePowerPlanet = new HomePagePowerPlanet(driver);

	}
	
	

	public void startBrowser() {
		startBrowserWithUrl(prop.getProperty("stg-merchant.url"));
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
			return new ChromeDriver(chromeOptions);

		case "firefox":
			FirefoxOptions firefoxOptions = new FirefoxOptions();
//			firefoxOptions.setHeadless(true);
			firefoxOptions.addArguments("--disable-dev-shm-usage");
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

	public void waitForVisibility(WebElement element) {
		new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.visibilityOf(element));
	}

	public void waitForVisibilityList(List<WebElement> elements) {
		new WebDriverWait(getDriver(), Duration.ofSeconds(10))
				.until(ExpectedConditions.visibilityOfAllElements(elements));
	}

	public void waitForClickability(WebElement element) {
		new WebDriverWait(getDriver(), Duration.ofSeconds(10)).until(ExpectedConditions.elementToBeClickable(element));
	}

	public void waitForPageLoad() {
		new WebDriverWait(driver, Duration.ofSeconds(60)).until(webDriver -> ((JavascriptExecutor) webDriver)
				.executeScript("return document.readyState").equals("complete"));
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
			driver.manage().deleteAllCookies();
			driver.quit();
			log.info("Browser closed and cookies deleted");
		}
		
	}
}
