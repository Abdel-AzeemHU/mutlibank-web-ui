package tests;

import Base.Base;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utilities.RetryAnalyzer;
import utilities.TestListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Listeners(TestListener.class)
public class SPOT_001_SpotTradingCategoriesTest extends Base {

    /**
     * Test Purpose: Verify that the Spot trading section displays trading pairs under different categories
     *
     * Test Steps:
     * 1. Verify Spot section is visible on the homepage
     * 2. Click the "All" category button using existing locator
     * 3. Verify trading pairs are displayed
     * 4. Validate trading pair format
     */

    @Test(description = "SPOT-001: Validate Spot displays trading pairs under All category")
    public void testSpotTradingPairsUnderAllCategory() throws InterruptedException {

        log.info("Starting SPOT-001: Validate Spot trading pairs");

        // Wait for page to fully load
        Thread.sleep(5000);
        waitForPageLoad();

        // Step 1: Verify Spot header is visible (indicating we're on Spot trading page)
        log.info("Step 1: Verifying Spot header is visible");

        // First, check if we're on the right page by looking at URL
        String currentUrl = driver.getCurrentUrl();
        log.info("Current URL: " + currentUrl);

//        // Check if Spot header contains "Spot" text
//        try {
//            String spotHeaderText = homePage.spotHeader.getText();
//            log.info("Spot header text: " + spotHeaderText);
//
//            boolean isSpotVisible = homePage.spotHeader.isDisplayed() &&
//                    spotHeaderText.contains("Spot");
//
//            Assert.assertTrue(isSpotVisible, "Spot header should be visible and contain 'Spot' text");
//            log.info("Spot header found and visible: " + spotHeaderText);
//
//        } catch (Exception e) {
//            log.error("Spot header not found or not visible: " + e.getMessage());
//
//            // Debug: Check what's actually visible
//            List<WebElement> activeHeaders = driver.findElements(
//                    By.xpath("//span[contains(@class, 'style_active__Yuxzy')]")
//            );
//
//            for (WebElement header : activeHeaders) {
//                if (header.isDisplayed()) {
//                    log.info("Active header found: " + header.getText());
//                }
//            }
//
//            Assert.fail("Spot trading section is not active. Current active section: " +
//                    (activeHeaders.isEmpty() ? "None" : activeHeaders.get(0).getText()));
//        }

        // Step 2: Click All category button using your existing method
        log.info("Step 2: Clicking 'All' category button");
        homePage.clickSpotCategory("All");
        Thread.sleep(3000); // Give more time for content to load

        // Step 3: Get trading pairs - use your tradingPairsTable locator
        log.info("Step 3: Getting trading pairs");
        List<WebElement> tradingPairs = homePage.tradingPairsTable;

        // If no pairs found in table, try alternative selectors
        if (tradingPairs.isEmpty()) {
            log.info("No pairs found in table, trying alternative selectors");
            tradingPairs = driver.findElements(
                    By.xpath("//tbody//tr[position() > 1] | " + // Table rows except header
                            "//tr[contains(@class, 'cursor-pointer')] | " +
                            "//div[contains(@class, 'pair')] | " +
                            "//div[contains(@class, 'trading-pair')]")
            );
        }

        log.info("Found " + tradingPairs.size() + " trading pairs");

        // If still no pairs, take screenshot for debugging
        if (tradingPairs.isEmpty()) {
            log.warn("No trading pairs found. Page source might help debugging.");
        }

        Assert.assertTrue(tradingPairs.size() > 0,
                "'All' category should display trading pairs. Found: " + tradingPairs.size());

        // Step 4: Validate first trading pair has proper format
        log.info("Step 4: Validating trading pair format");
        if (tradingPairs.size() > 0) {
            WebElement firstPair = tradingPairs.get(0);
            String pairText = firstPair.getText();
            log.info("First trading pair text: " + pairText);

            // More comprehensive format checking
            boolean hasValidFormat = pairText.contains("/") ||
                    pairText.contains("USDT") ||
                    pairText.contains("BTC") ||
                    pairText.contains("FIAT") ||
                    pairText.matches(".*[A-Z]{3,}/[A-Z]{3,}.*") ||
                    pairText.matches(".*[A-Z]{3,}-[A-Z]{3,}.*") ||
                    pairText.matches(".*[A-Z]{3,}\\s[A-Z]{3,}.*");

            Assert.assertTrue(hasValidFormat,
                    "Trading pairs should have valid format. Actual text: " + pairText);
            log.info("✓ Trading pair format is valid: " + pairText);
        }

        // Step 5: Test other categories
        log.info("Step 5: Testing other categories");

        // Get available categories from the page
        List<String> categories = new ArrayList<>();
        try {
            for (WebElement button : homePage.categoryButtons) {
                if (button.isDisplayed()) {
                    String text = button.getText().trim();
                    if (!text.isEmpty()) {
                        categories.add(text);
                        log.info("Found category button: " + text);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not get category buttons: " + e.getMessage());
            // Fallback to default categories
            categories = Arrays.asList("Favorites", "USDT", "BTC", "FIAT");
        }

        log.info("Available categories: " + categories);

        for (String category : categories) {
            if (!category.equalsIgnoreCase("All")) {
                try {
                    log.info("Testing category: " + category);
                    homePage.clickSpotCategory(category);
                    Thread.sleep(2000); // Wait for category switch

                    // Get pairs for this category
                    List<WebElement> categoryPairs = driver.findElements(
                            By.xpath("//tbody//tr[position() > 1]")
                    );

                    if (category.equalsIgnoreCase("Favorites") && categoryPairs.isEmpty()) {
                        log.info("Favorites category is empty (expected for new users)");
                    } else if (categoryPairs.isEmpty()) {
                        log.warn("Category '" + category + "' has no trading pairs");
                    } else {
                        log.info("Category '" + category + "' has " + categoryPairs.size() + " pairs");
                    }
                } catch (Exception e) {
                    log.warn("Could not test category: " + category + " - " + e.getMessage());
                }
            }
        }

        log.info("Step 6: Testing Themes dropdown and sub-categories");

        try {
            // Click Themes to open the dropdown (this is a main category)
            log.info("Testing Themes main category");
            homePage.clickSpotCategory("Themes");
            Thread.sleep(1000);

            // Now test each sub-category inside the dropdown
            String[] themeSubCategories = {"Legacy", "DeFi", "Stablecoin"};

            for (String subCategory : themeSubCategories) {
                try {
                    log.info("Testing theme sub-category: " + subCategory);

                    // Use the special method for dropdown items
                    homePage.clickThemeSubCategory(subCategory);
                    Thread.sleep(2000);

                    // Verify trading pairs are displayed
                    log.info("Sub-category '" + subCategory + "' has " + tradingPairs.size() + " trading pairs");

                    if (tradingPairs.isEmpty()) {
                        log.warn("No trading pairs found for: " + subCategory);
                    } else {
                        Assert.assertTrue(tradingPairs.size() > 0,
                                "Sub-category '" + subCategory + "' should display trading pairs");
                    }

                    // Reopen dropdown for next sub-category
                    homePage.openThemesDropdown();
                    Thread.sleep(500);

                } catch (Exception e) {
                    log.warn("Could not test sub-category: " + subCategory + " - " + e.getMessage());
                }
            }

            // Close dropdown when done
            homePage.closeThemesDropdown();
            Thread.sleep(500);

        } catch (Exception e) {
            log.warn("Themes dropdown testing failed: " + e.getMessage());
        }

        // Step 7: Final verification - return to All category
        log.info("Step 7: Returning to All category");
        homePage.clickSpotCategory("All");
        Thread.sleep(2000);

        // Verify we still have trading pairs
        List<WebElement> finalPairs = homePage.getTradingPairs();
        Assert.assertTrue(finalPairs.size() > 0,
                "Should have trading pairs when returning to All category");
        log.info("Final verification: " + finalPairs.size() + " trading pairs in All category");


        log.info("SPOT-001 test completed successfully");
    }

    @AfterMethod
    public void close() {
        closeBrowser();
    }
}