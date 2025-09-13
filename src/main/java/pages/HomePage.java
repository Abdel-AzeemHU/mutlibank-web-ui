package pages;

import Base.base;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.time.Duration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class HomePagePowerPlanet extends base {

    WebDriverWait wait;

    // Locators for elements on the Home Page
    @FindBy(xpath = "//div[@class='leaflet-container leaflet-touch leaflet-retina leaflet-fade-anim leaflet-grab leaflet-touch-drag leaflet-touch-zoom']")
    private WebElement mapElement;

    @FindBy(css = "div[class='App'] div h3")
    private WebElement powerPlantsFoundText;

    @FindBy(xpath = "//input[@type='range']")
    private WebElement slider;

    @FindBy(xpath = "//select[@style='width: 150px;']")
    private WebElement stateFilterDropdown;


    public HomePagePowerPlanet(WebDriver driver) {
        PageFactory.initElements(driver, this);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    @BeforeMethod
    public void beforeMethod(Method method) {
        System.out.println("Starting test: " + method.getName());
    }



    public boolean isMapDisplayed() {
        waitForVisibility(mapElement);
        try {
            return mapElement.isDisplayed();
        } catch (Exception e) {
            log.error("Map element not found: " + e.getMessage());
            return false;
        }
    }

    public String getPowerPlantsFoundText() {
        waitForVisibility(powerPlantsFoundText);
        try {
            return powerPlantsFoundText.getText();
        } catch (Exception e) {
            log.error("Power Plants found text not found: " + e.getMessage());
            return null;
        }
    }

    // Method to set slider value
    public void setSliderValue(int value) {
        // JavaScript is most reliable for exact values
        ((JavascriptExecutor)driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'));",
                slider, value);
    }

    // Method to get current slider value
    public int getSliderValue() {
        return Integer.parseInt(slider.getAttribute("value"));
    }

    // Method to verify slider range
    public void verifySliderRange(int expectedMin, int expectedMax) {
        assertEquals(Integer.parseInt(slider.getAttribute("min")), expectedMin);
        assertEquals(Integer.parseInt(slider.getAttribute("max")), expectedMax);
    }

    // Method to test slider functionality
    public void testSliderFunctionality() {
        // Verify initial state
        verifySliderRange(0, 1000);
        assertEquals(getSliderValue(), 200, "Initial value should be 200");

        // Test min boundary
        setSliderValue(0);
        assertEquals(getSliderValue(), 0, "Should reach minimum (0)");

        // Test max boundary
        setSliderValue(1000);
        assertEquals(getSliderValue(), 1000, "Should reach maximum (1000)");

        // Return to initial
        setSliderValue(200);
        assertEquals(getSliderValue(), 200, "Should return to initial (200)");
    }


    public String getSelectedState() {
        waitForVisibility(stateFilterDropdown);
        Select dropdown = new Select(stateFilterDropdown);
        return dropdown.getFirstSelectedOption().getAttribute("value");
    }

    public void selectState(String stateAbbreviation) {
        try {
            waitForVisibility(stateFilterDropdown);
            Select dropdown = new Select(stateFilterDropdown);
            dropdown.selectByValue(stateAbbreviation);

            // Wait for any loading indicators first if they exist
            waitForAjaxComplete();

            // Then wait for count to be updated
            waitForCountToUpdate();
        } catch (Exception e) {
            log.error("Failed to select state " + stateAbbreviation, e);
            throw e;
        }
    }

    private void waitForAjaxComplete() {
        try {
            ((JavascriptExecutor)driver).executeScript("return jQuery.active == 0");
        } catch (Exception e) {
            // jQuery not available, continue
        }
    }

    private void waitForCountToUpdate() {
        // Wait for count to be visible and contain numbers
        wait.until(d -> {
            try {
                String text = powerPlantsFoundText.getText();
                return text.matches(".*\\d+.*") && !text.contains("Loading");
            } catch (Exception e) {
                return false;
            }
        });
    }

    // More reliable text extraction
    public int extractPowerPlantCount() {
        try {
            String text = getPowerPlantsFoundText().trim();
            return Integer.parseInt(text.replaceAll("[^0-9]", "").trim().split(" ")[0]);
        } catch (Exception e) {
            log.error("Failed to extract count from: " + getPowerPlantsFoundText(), e);
            throw e;
        }
    }



}
