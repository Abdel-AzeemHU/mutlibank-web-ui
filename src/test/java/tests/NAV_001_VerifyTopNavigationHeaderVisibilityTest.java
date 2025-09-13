package tests;

import Base.Base;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utilities.TestListener;

import java.util.List;
@Listeners(TestListener.class)
public class NAV_001_VerifyTopNavigationHeaderVisibilityTest extends Base {


    /* Test case
    1. Open the browser and navigate to the Power Planet home page.
    2. Check if the map is displaying
     */

    @Test(description = "NAV-001: Validate top navigation bar with all menu items")
    public void testTopNavigationBarDisplay() {
        // Verify navigation container is visible
        Assert.assertTrue(homePage.isNavigationBarVisible(), "Navigation bar should be visible");

        // Get actual navigation items
        List<String> actualMenuItems = homePage.getNavigationMenuItems();

        // Get expected navigation items from external data file
        List<String> expectedMenuItems = testData.getExpectedNavigationItems();

        log.info("Actual menu items: " + actualMenuItems);
        log.info("Expected menu items: " + expectedMenuItems);

        // Verify all expected items are present
        for (String expectedItem : expectedMenuItems) {
            Assert.assertTrue(actualMenuItems.contains(expectedItem), "Navigation should contain: " + expectedItem);
        }

        // Verify count matches
        Assert.assertEquals(actualMenuItems.size(), expectedMenuItems.size(), "Number of navigation items should match expected");
    }

    @AfterMethod
    public void close() {
        closeBrowser();
    }

}
