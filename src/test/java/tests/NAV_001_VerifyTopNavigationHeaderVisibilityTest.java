package tests;

import Base.Base;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utilities.TestListener;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
@Listeners(TestListener.class)
public class PowerPlanetTests extends Base {


    /* Test case
    1. Open the browser and navigate to the Power Planet home page.
    2. Check if the map is displaying
     */

    @Test(description = "This is to verify the Map Display")
    public void verifyMapDisplay() {

        boolean isMapDisplayed = homePagePowerPlanet.isMapDisplayed();

        // Assert that the map is displayed
        assertTrue(isMapDisplayed, "Map is not displayed on the Power Planet home page.");
    }

    @Test(description = "This is to verify the Map Zoom In")
    public void verifyPowerPlanetText(){
        String powerPlanetText = homePagePowerPlanet.getPowerPlantsFoundText();
        System.out.println("Power Plants found text: " + powerPlanetText);
        assertEquals(powerPlanetText, "200 Power Plants were found!", "Power Plants found text does not match expected value.");
    }

    @Test(description = "Verify slider functionality")
    public void testEnergySlider() {
        homePagePowerPlanet.setSliderValue(500);
        assertEquals(homePagePowerPlanet.getSliderValue(), 500, "Should set to 500");

        homePagePowerPlanet.setSliderValue(-100); // Should clamp to 0
        assertEquals(homePagePowerPlanet.getSliderValue(), 0, "Should clamp to min (0)");

        homePagePowerPlanet.setSliderValue(1500); // Should clamp to 1000
        assertEquals(homePagePowerPlanet.getSliderValue(), 1000, "Should clamp to max (1000)");
    }


    @AfterMethod
    public void close() {
        closeBrowser();
    }

}
