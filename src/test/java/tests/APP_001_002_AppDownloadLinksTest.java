package tests;

import Base.Base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utilities.RetryAnalyzer;
import utilities.TestListener;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Listeners(TestListener.class)
public class APP_001_002_AppDownloadLinksTest extends Base {

    /**
     * Test Purpose: Verify that App Store and Google Play download links work correctly
     *
     * APP-001 Test Steps:
     * 1. Find and click the App Store download link
     * 2. Switch to new tab
     * 3. Verify URL contains apps.apple.com
     * 4. Verify page title contains MultiBank
     *
     * APP-002 Test Steps:
     * 1. Return to main page
     * 2. Find and click the Google Play download link
     * 3. Switch to new tab
     * 4. Verify URL contains play.google.com
     * 5. Verify page title contains MultiBank
     */

    @Test(description = "APP-001: Validate App Store download link navigation", priority = 1)
    public void testAppStoreDownloadLink() throws InterruptedException {

        log.info("Starting APP-001: Validate App Store download link");

        // Step 1: Scroll to app download section
        log.info("Step 1: Scrolling to app download section");
        homePage.scrollToAppDownloadSection();
        Thread.sleep(2000);


        // Verify app download section is visible
        Assert.assertTrue(homePage.isAppDownloadSectionVisible(),
                "App download section should be visible");

        // Step 2: Find and verify App Store link
        log.info("Step 2: Verifying App Store download link");
        WebElement appStoreLink = homePage.appStoreLink;
        Assert.assertNotNull(appStoreLink, "App Store download link should be present");
        Assert.assertTrue(appStoreLink.isDisplayed(), "App Store link should be visible");
        log.info("✓ App Store link found and visible");

        // Get expected values from test data
        String expectedAppStoreUrlPattern = testData.getAppStoreExpectations().get("urlPattern");
        String expectedTitleContains = testData.getAppStoreExpectations().get("titleContains");
        String expectedExactTitle = testData.getAppStoreExpectations().get("exactTitle");

        // Step 3: Click the App Store link
        log.info("Step 3: Clicking App Store link");
        appStoreLink.click();
        Thread.sleep(3000); // Wait for new tab to open

        // Step 3: SIMPLE TAB SWITCHING
        String originalWindow = driver.getWindowHandle();

        Set<String> allWindows = driver.getWindowHandles();
        log.info("Total windows after click: " + allWindows.size());

        // Switch to the new tab (the one that's not the original)
        for (String window : allWindows) {
            if (!window.equals(originalWindow)) {
                driver.switchTo().window(window);
                log.info("Switched to new tab: " + window);
                break;
            }
        }

        String currentWindow = driver.getWindowHandle();
        Assert.assertNotEquals(currentWindow, originalWindow, "Should be on new tab");
        log.info("Current window after switch: " + currentWindow);

        // Step 5: Verify App Store URL
        log.info("Step 5: Verifying App Store URL");
        String appStoreUrl = driver.getCurrentUrl();
        log.info("App Store URL: " + appStoreUrl);
        Assert.assertTrue(appStoreUrl.contains(expectedAppStoreUrlPattern),
                "Should navigate to Apple App Store. URL: " + appStoreUrl +
                        " | Expected pattern: " + expectedAppStoreUrlPattern);

        // Step 6: Verify page title matches expected value using HomePage method
        log.info("Step 6: Verifying App Store page title");
        String actualTitle = homePage.getAppStoreTitle();

        log.info("Actual title: " + actualTitle);
        log.info("Expected title contains: " + expectedTitleContains);

        // Validate title contains expected text
        Assert.assertTrue(actualTitle.contains(expectedTitleContains), "Page title should contain '" + expectedTitleContains + "'. Actual title: " + actualTitle);

        // Validate exact title match
        Assert.assertEquals(actualTitle, expectedExactTitle,
                "Page title should match exactly. Expected: " + expectedExactTitle +
                        ", Actual: " + actualTitle);
        log.info("APP-001 test completed successfully");
    }

    @Test(description = "APP-002: Validate Google Play download link navigation", priority = 2)
    public void testGooglePlayDownloadLink() throws InterruptedException {

        log.info("Starting APP-002: Validate Google Play download link");

        // Step 1: Scroll to app download section (if not already done by previous test)
        log.info("Step 1: Scrolling to app download section");
        homePage.scrollToAppDownloadSection();
        Thread.sleep(2000); // Wait for scroll to complete

        // Verify app download section is visible
        Assert.assertTrue(homePage.isAppDownloadSectionVisible(),
                "App download section should be visible");

        // Get expected values from test data
        String expectedGooglePlayUrlPattern = testData.getGooglePlayExpectations().get("urlPattern");
        String expectedTitleContains = testData.getGooglePlayExpectations().get("titleContains");
        String expectedExactTitle = testData.getGooglePlayExpectations().get("exactTitle");

        // Step 2: Find and verify Google Play link
        log.info("Step 2: Verifying Google Play download link");
        WebElement googlePlayLink = homePage.googlePlayLink;
        Assert.assertNotNull(googlePlayLink, "Google Play download link should be present");
        Assert.assertTrue(googlePlayLink.isDisplayed(), "Google Play link should be visible");
        log.info("Google Play link found and visible");

        // Step 3: Click the Google Play link
        log.info("Step 3: Clicking Google Play link");
        googlePlayLink.click();
        Thread.sleep(3000); // Wait for new tab to open

        // Step 4: SIMPLE TAB SWITCHING
        String originalWindow = driver.getWindowHandle();
        Set<String> allWindows = driver.getWindowHandles();
        log.info("Total windows after click: " + allWindows.size());

        // Switch to the new tab (the one that's not the original)
        for (String window : allWindows) {
            if (!window.equals(originalWindow)) {
                driver.switchTo().window(window);
                log.info("Switched to new tab: " + window);
                break;
            }
        }

        // Verify we actually switched to a new tab
        String currentWindow = driver.getWindowHandle();
        Assert.assertNotEquals(currentWindow, originalWindow, "Should be on new tab");
        log.info("Current window after switch: " + currentWindow);

        // Step 5: Verify Google Play URL
        log.info("Step 5: Verifying Google Play URL");
        String googlePlayUrl = driver.getCurrentUrl();
        log.info("Google Play URL: " + googlePlayUrl);
        Assert.assertTrue(googlePlayUrl.contains(expectedGooglePlayUrlPattern),
                "Should navigate to Google Play Store. URL: " + googlePlayUrl +
                        " | Expected pattern: " + expectedGooglePlayUrlPattern);

        // Step 6: Verify page title matches expected value using HomePage method
        log.info("Step 6: Verifying Google Play page title");
        String actualTitle = homePage.getGooglePlayTitle();

        log.info("Actual title: " + actualTitle);
        log.info("Expected title contains: " + expectedTitleContains);

        // Validate title contains expected text
        Assert.assertTrue(actualTitle.contains(expectedTitleContains), "Page title should contain '" + expectedTitleContains + "'. Actual title: " + actualTitle);

        // Validate exact title match
        Assert.assertEquals(actualTitle, expectedExactTitle,
                "Page title should match exactly. Expected: " + expectedExactTitle +
                        ", Actual: " + actualTitle);

        log.info("APP-002 test completed successfully");
    }
    @AfterMethod
    public void close() {
        closeBrowser();
    }
}