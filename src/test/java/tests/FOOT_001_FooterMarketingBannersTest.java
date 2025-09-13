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

import java.util.List;

@Listeners(TestListener.class)
public class FOOT_001_FooterMarketingBannersTest extends Base {

    /**
     * Test Purpose: Verify that marketing banners are displayed at the bottom of the page
     *
     * Test Steps:
     * 1. Scroll to the bottom of the page
     * 2. Wait for footer/banners to be visible
     * 3. Count and validate marketing banners are present
     * 4. Verify banners have content/images
     */

    @Test(description = "FOOT-001: Validate all marketing banners are displayed at footer")
    public void testFooterMarketingBanners() {

        log.info("Starting FOOT-001: Validate footer marketing banners");

        // Step 1: Scroll to banners section
        log.info("Step 1: Scrolling to banners section");
        homePage.scrollToBanners();

        // Step 2: Wait for banners to load
        log.info("Step 2: Waiting for banners to load");
        homePage.waitForBannersToLoad();

        // Step 3: Get marketing banners
        log.info("Step 3: Getting marketing banners");
        List<WebElement> banners = homePage.getMarketingBanners();
        log.info("Found " + banners.size() + " marketing banner elements");

        // Validation 1: Banners should exist
        Assert.assertTrue(banners.size() > 0, "Marketing banners should be present at the footer");

        // Get elements within each banner instead of using the generic lists
        int titleCount = 0;
        int linkCount = 0;
        int imageCount = 0;

        for (WebElement banner : banners) {
            if (banner.isDisplayed()) {
                List<WebElement> titlesInBanner = banner.findElements(By.cssSelector(".style_title__g8VH0"));
                List<WebElement> linksInBanner = banner.findElements(By.cssSelector(".style_link__qP2GD"));
                List<WebElement> imagesInBanner = banner.findElements(By.cssSelector(".style_image__kiucM"));

                titleCount += titlesInBanner.size();
                linkCount += linksInBanner.size();
                imageCount += imagesInBanner.size();

                System.out.println(titleCount + " " + linkCount + " " + imageCount);
            }
        }

        // Validation 2: Banners should be visible with content
        log.info("Step 4: Verifying banner visibility and content");
        Assert.assertTrue(homePage.areBannersDisplayedWithContent(),
                "All marketing banners should be visible and have content");

        // Validation 3: Verify specific banner elements
        log.info("Step 5: Verifying specific banner elements");
        Assert.assertEquals(titleCount, banners.size(), "Should have one title per banner");
        Assert.assertEquals(linkCount, banners.size(), "Should have one link per banner");
        Assert.assertEquals(imageCount, banners.size(), "Should have one image per banner");

        // Rest of your test remains the same...
        // Validation 4: Verify active banner
        log.info("Step 6: Verifying active banner");
        WebElement activeBanner = homePage.getActiveBanner();
        Assert.assertNotNull(activeBanner, "Should have an active banner");
        Assert.assertTrue(activeBanner.isDisplayed(), "Active banner should be visible");

        // Step 7: Log banner details
        log.info("Step 7: Logging banner details");
        for (int i = 0; i < banners.size(); i++) {
            WebElement banner = banners.get(i);
            if (banner.isDisplayed()) {
                String bannerText = banner.getText();
                log.info("Banner " + (i + 1) + ": " +
                        (bannerText.length() > 100 ? bannerText.substring(0, 100) + "..." : bannerText));
            }
        }

        log.info("✓ FOOT-001 test completed successfully - Found " + banners.size() + " banners");
    }

    @AfterMethod
    public void close() {
        closeBrowser();
    }
}