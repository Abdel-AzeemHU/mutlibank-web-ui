package utilities;

import Base.Base;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import java.security.SecureRandom;

public class CommonActions extends Base {

    WebDriverWait wait;
    public String company_name = getSaltString();
    JavascriptExecutor js = (JavascriptExecutor) driver;

    // generate the current date and time
    public String generateDateTimeString() {
        Date dateNow = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_hh-mm-ss");
        return dateFormat.format(dateNow).toString();
    }

    // Load web_config.properties file
    public CommonActions() {

        prop = new Properties();

        try {
            FileInputStream propFile = new FileInputStream(
                    System.getProperty("user.dir") + "/src/main/resources/web_config.properties");
            prop.load(propFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProp() {
        return prop;
    }

    // takes screenshot and saves the screenshot in the reports folder
    public void takeScreenshot(String screenshotName) {
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File file = screenshot.getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(file, new File("./reports/" + screenshotName + ".png"));
            log.info("Screenshot taken");
        } catch (IOException e) {
            e.printStackTrace();
            log.info("Could not take the screenshot");
        }
    }

    // generate random letters which are less than 10 characters
    public String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        long nanoTime = System.nanoTime();
        while (salt.length() < 9) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return nanoTime + "0DDFF" + salt.toString();

    }

    public String getSaltStringSmall() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        SecureRandom rnd = new SecureRandom();
        long nanoTime = System.nanoTime();
        while (salt.length() < 5) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return nanoTime + "06F" + salt.toString();

    }

    // generate random letters which are less than 10 characters
    public String getSaltString50() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() == 50) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();

    }

    public CharSequence generatePhoneNumberForCountry(String country) {
        Random rand = new Random();
        DecimalFormat df3 = new DecimalFormat("000");
        DecimalFormat df4 = new DecimalFormat("0000");

        country = country.toLowerCase();

        switch (country) {
            case "saudi":
            case "saudi arabia":
                // Format: 012-XXX-XXXX
                return df3.format(12) + "-" + df3.format(rand.nextInt(900)) + "-" + df4.format(rand.nextInt(10000));

            case "united arab":
            case "uae":
            case "dubai":
                // Format: 050-XXX-XXXX
                String firstPart = "050";
                String secondPart = df3.format(rand.nextInt(900));
                String thirdPart = df4.format(rand.nextInt(10000));
                return firstPart + "-" + secondPart + "-" + thirdPart;

            case "uk":
            case "united kingdom":
                // Format: 117-XXX-XXXX
                return df3.format(117) + "-" + df3.format(rand.nextInt(900)) + "-" + df4.format(rand.nextInt(10000));

            case "india":
                // Format: 022-XXXX-XXXX
                return df3.format(22) + "-" + df4.format(rand.nextInt(10000)) + "-" + df4.format(rand.nextInt(10000));

            case "usa":
            case "united states":
                // Format: +1-XXX-XXX-XXXX
                return "+1-" + df3.format(rand.nextInt(900)) + "-" + df3.format(rand.nextInt(900)) + "-"
                        + df4.format(rand.nextInt(10000));

            default:
                // Generic fallback: +000-XXX-XXX-XXXX
                return "+000-" + df3.format(rand.nextInt(900)) + "-" + df3.format(rand.nextInt(900)) + "-"
                        + df4.format(rand.nextInt(10000));
        }
    }

    public void actionMethodToDeleteText(WebElement element, WebDriver driver, String newText) {

        try {

            Actions actions = new Actions(driver);
            // element.click();
            actions.sendKeys(Keys.CONTROL, "a").sendKeys(Keys.DELETE).sendKeys(newText).build().perform();
        } catch (Exception e) {
            e.printStackTrace();
            log.info("Not able to delete the prefilled text and enter a new text");
        }
    }


    public void excelreader() throws Exception {

    }

    public void excelwriter() throws Exception {

    }
}