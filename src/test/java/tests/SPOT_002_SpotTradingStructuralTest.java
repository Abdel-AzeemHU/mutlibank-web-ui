package tests;

import Base.Base;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utilities.RetryAnalyzer;
import utilities.TestListener;

@Listeners(TestListener.class)
public class SPOT_002_SpotTradingStructuralTest extends Base {

    /**
     * Test Purpose: Validate structural elements, column headers, pair symbol formatting, and price display
     * in the Spot trading section.
     *
     * Test Steps:
     * 1. Verify Spot section is visible
     * 2. Click "All" category to see all pairs
     * 3. Validate column headers exist and are correct
     * 4. Verify trading pair symbol formatting
     * 5. Validate price display formatting
     * 6. Verify all required UI elements are present
     */

    @Test(description = "SPOT-002: Validate Spot trading section structural elements and formatting")
    public void testSpotTradingStructuralElements() throws InterruptedException {

        log.info("Starting SPOT-002: Validate Spot trading structural elements");

        // Wait for page to fully load
        Thread.sleep(5000);
        waitForPageLoad();

        // Step 1: Verify Spot section is visible
        log.info("Step 1: Verifying Spot section is visible");
        Assert.assertTrue(homePage.isSpotHeaderVisible(), "Spot header should be visible");
        log.info("Spot section is visible");

        // Step 2: Click 'All' category to ensure we see all trading pairs
        log.info("Step 2: Clicking 'All' category to see all pairs");
        homePage.clickSpotCategory("All");
        Thread.sleep(3000); // Wait for pairs to load

        // Step 3: Validate column headers
        log.info("Step 3: Validating column headers");
        try {
            homePage.validateColumnHeaders();
            log.info("Column headers validated successfully");
        } catch (AssertionError e) {
            log.error("Column header validation failed: " + e.getMessage());
            throw e;
        }

        // Step 4: Verify trading pair symbol formatting
        log.info("Step 4: Validating trading pair symbol formatting");
        try {
            homePage.validatePairSymbolFormatting();
            log.info("Trading pair formatting validated successfully");
        } catch (AssertionError e) {
            log.error("Pair symbol validation failed: " + e.getMessage());
            throw e;
        }

        // Step 5: Validate price display formatting
        log.info("Step 5: Validating price display formatting");
        try {
            homePage.validatePriceFormatting();
            log.info("Price formatting validated successfully");
        } catch (AssertionError e) {
            log.error("Price formatting validation failed: " + e.getMessage());
            throw e;
        }

        // Step 6: Verify all required UI elements are present
        log.info("Step 6: Validating required UI elements");
        try {
            homePage.validateUIElements();
            log.info("UI elements validated successfully");
        } catch (AssertionError e) {
            log.error("UI elements validation failed: " + e.getMessage());
            throw e;
        }

        log.info("SPOT-002 test completed successfully - All structural elements validated");
    }

    @AfterMethod
    public void close() {
        closeBrowser();
    }
}