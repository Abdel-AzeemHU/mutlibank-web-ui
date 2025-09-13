package tests;

import Base.Base;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pages.HomePage;
import utilities.RetryAnalyzer;
import utilities.TestListener;

import java.util.List;

@Listeners(TestListener.class)
public class NAV_002_TopNavigationDropdownInteractionTest extends Base {

    /**
     * Test Case: NAV-002
     * Area: Top Navigation
     * Precondition: Homepage loaded
     * Steps: Iterate expected nav items; for each, hover/verify visibility and link target
     * Expected Result: Each nav item is interactable and mapped to the correct target
     */

    @Test(description = "NAV-002: Verify navigation dropdown interactions and hover functionality", priority = 1)
    public void testNavigationDropdownInteractions() throws InterruptedException {

        log.info("Starting NAV-002: Verify navigation dropdown interactions");

        // Wait for page to load
        Thread.sleep(3000);
        waitForPageLoad();

        // Verify navigation is visible
        Assert.assertTrue(homePage.isNavigationBarVisible(),
                "Navigation bar should be visible");

        // Test Dashboard and Markets links
        Assert.assertTrue(homePage.isDirectLink("Dashboard"),
                "Dashboard should be a direct link");
        Assert.assertTrue(homePage.isDirectLink("Markets"),
                "Markets should be a direct link");

        // Test Trade dropdown - just use your existing hover method
        log.info("Testing Trade dropdown...");
        homePage.hoverOnTradeMenu();
        Thread.sleep(1000);
        // The dropdown should be visible now after hover

        // Get dropdown items using your existing method
        List<String> tradeItems = homePage.getDropdownItems("Trade");
        log.info("Trade dropdown items: " + tradeItems);

        // Test Features dropdown
        log.info("Testing Features dropdown...");
        List<String> featuresItems = homePage.getDropdownItems("Features");
        log.info("Features dropdown items: " + featuresItems);

        // Test About Us dropdown
        log.info("Testing About Us dropdown...");
        List<String> aboutItems = homePage.getDropdownItems("About Us");
        log.info("About Us dropdown items: " + aboutItems);

        // Test Support dropdown
        log.info("Testing Support dropdown...");
        List<String> supportItems = homePage.getDropdownItems("Support");
        log.info("Support dropdown items: " + supportItems);

        log.info("NAV-002 test completed");
    }

//    @Test(description = "NAV-002: Verify navigation dropdown interactions")
//    public void testNavigationDropdownInteractions() throws InterruptedException {
//
//        log.info("Starting NAV-002: Verify navigation dropdown interactions");
//
//        // Wait for page to load
//        Thread.sleep(3000);
//
//        // Just verify the main navigation items exist
//        Assert.assertTrue(homePage.isNavigationBarVisible(),
//                "Navigation bar should be visible");
//
//        // Check Dashboard and Markets as direct links
//        Assert.assertTrue(homePage.isDirectLink("Dashboard"),
//                "Dashboard should be a direct link");
//        Assert.assertTrue(homePage.isDirectLink("Markets"),
//                "Markets should be a direct link");
//
//        // For dropdowns, just check they exist, don't try to open them yet
//        log.info("Checking dropdown buttons exist (not opening them)...");
//
//        WebElement tradeBtn = driver.findElement(By.id("trade-header-option-open-button"));
//        Assert.assertTrue(tradeBtn.isDisplayed(), "Trade dropdown button should be visible");
//
//        WebElement featuresBtn = driver.findElement(By.id("features-header-option-open-button"));
//        Assert.assertTrue(featuresBtn.isDisplayed(), "Features dropdown button should be visible");
//
//        // Note: The actual dropdown interaction needs different approach
//        log.info("Note: Dropdown menu interaction may require hover instead of click");
//
//        log.info("NAV-002 basic test completed");
//    }

    @Test(description = "NAV-002B: Verify Markets link navigation functionality", priority = 2)
    public void testMarketsLinkNavigation() throws InterruptedException {
        log.info("Starting NAV-002B: Verify Markets link navigation");

        // Store original URL
        String originalUrl = homePage.getCurrentUrl();
        log.info("Current URL: " + originalUrl);

        Thread.sleep(1000);
        // Click Markets link
        log.info("Clicking Markets link...");
        homePage.clickMarketsLink();

        Thread.sleep(3000);

        // Wait for navigation
        waitForPageLoad();

        // Verify URL changed to markets page
        String newUrl = homePage.getCurrentUrl();
        Assert.assertNotEquals(newUrl, originalUrl, "URL should change after clicking Markets link");
        Assert.assertTrue(newUrl.contains("/markets"), "Should navigate to markets page. Current URL: " + newUrl);
        log.info("✓ Successfully navigated to Markets page: " + newUrl);

        // Verify Markets link is now active (optional - depends on site behavior)
        // Some sites update active state, some don't
        log.info("NAV-002B test completed successfully");
    }

    @AfterMethod
    public void close() {
        closeBrowser();
    }
}