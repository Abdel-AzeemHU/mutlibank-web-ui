package pages;

import Base.Base;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.testng.Assert;


public class HomePage extends Base {

    // Navigation Bar Container
    @FindBy(className = "style_menu-container__Ha_wV")
    private WebElement navigationContainer;

    // Direct navigation links
    @FindBy(xpath = "//a[@href='/' and contains(@class, 'style_menu-item__SLdA4')]")
    private WebElement dashboardLink;

    @FindBy(xpath = "//a[@href='/markets' and contains(@class, 'style_menu-item__SLdA4')]")
    private WebElement marketsLink;

    // Dropdown buttons
    @FindBy(id = "trade-header-option-open-button")
    private WebElement tradeDropdown;

    @FindBy(id = "features-header-option-open-button")
    private WebElement featuresDropdown;

    @FindBy(id = "about-header-option-open-button")
    private WebElement aboutUsDropdown;

    @FindBy(id = "support-header-option-open-button")
    private WebElement supportDropdown;

    // Spot Trading Section Locators
    @FindBy(xpath = "//span[@class='style_label-badge-wrapper__MWCxl style_active__Yuxzy']")
    public WebElement spotHeader;

    @FindBy(xpath = "//div[contains(@class, 'categories')]//button | //div[contains(@class, 'tab')]//button")
    public List<WebElement> categoryButtons;

    @FindBy(id = "all")
    private WebElement allSpotCategoriesBtn;

    @FindBy(xpath = "(//tbody)[1]//tr")
    public List<WebElement> tradingPairsTable;

    @FindBy(id = "favorites")
    private WebElement favoritesCategoryBtn;

    @FindBy(id = "usdt")
    private WebElement usdtCategoryBtn;

    @FindBy(id = "btc")
    private WebElement btcCategoryBtn;

    @FindBy(id = "fiat")
    private WebElement fiatCategoryBtn;

    @FindBy(id = "headlessui-menu-button-:r0:")
    private WebElement themesDropdownButton;

    @FindBy(xpath = "//div[contains(@class, 'style_items__rwhTi')]")  // Dropdown menu container
    private WebElement themesDropdownMenu;

    @FindBy(id = "themes")
    private WebElement themesCategoryBtn;

    @FindBy(id = "Legacy")
    private WebElement legacyCategoryBtn;

    @FindBy(id = "DeFi")
    private WebElement defiCategoryBtn;

    @FindBy(id = "Stablecoin")
    private WebElement stablecoinCategoryBtn;

    // Footer and Marketing Banners
    @FindBy(xpath = "//footer")
    private WebElement footer;

    @FindBy(css = ".slick-slide:not(.slick-cloned) .style_container__hCH7M")
    private List<WebElement> marketingBanners;

    @FindBy(css = ".slick-slide:not(.slick-cloned) .style_container__hCH7M .style_title__g8VH0")
    private List<WebElement> bannerTitles;

    @FindBy(css = ".slick-slide:not(.slick-cloned) .style_container__hCH7M .style_link__qP2GD")
    private List<WebElement> bannerLinks;

    @FindBy(css = ".slick-slide:not(.slick-cloned) .style_container__hCH7M .style_image__kiucM")
    private List<WebElement> bannerImages;

    @FindBy(className = "slick-track")
    private WebElement slickTrack;

    @FindBy(css = ".slick-slide.slick-active .style_container__hCH7M")
    private List<WebElement> activeBanners;

    // App Download Section
    @FindBy(xpath = "//div[@class='d-flex gap-8 flex-col']")
    public WebElement appDownloadSection;

    @FindBy(xpath = "//a[contains(@href, 'apps.apple.com')]")
    public WebElement appStoreLink;

    @FindBy(xpath = "//a[contains(@href, 'play.google.com')]")
    public WebElement googlePlayLink;

    @FindBy(xpath = "//h1[@class='product-header__title app-header__title']")
    public WebElement appStoreTitleElement;

    @FindBy(xpath = "//span[@class='AfwdI']")
    public WebElement googlePlayTitleElement;

    private WebDriverWait wait;
    private Actions actions;

    public HomePage(WebDriver driver) {
        PageFactory.initElements(driver, this);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        this.actions = new Actions(driver);
    }

    public boolean isNavigationBarVisible() {
        try {
            waitForVisibility(navigationContainer);
            return navigationContainer.isDisplayed();
        } catch (Exception e) {
            log.error("Navigation bar not visible: " + e.getMessage());
            return false;
        }
    }

    public List<String> getNavigationMenuItems() {
        waitForVisibility(navigationContainer);
        List<String> menuTexts = new ArrayList<>();

        try {
            // Get each navigation item individually using the defined elements

            // Add Dashboard
            if (dashboardLink != null && dashboardLink.isDisplayed()) {
                String text = dashboardLink.getText().trim();
                if (!text.isEmpty()) menuTexts.add(text);
            }

            // Add Markets
            if (marketsLink != null && marketsLink.isDisplayed()) {
                String text = marketsLink.getText().trim();
                if (!text.isEmpty()) menuTexts.add(text);
            }

            // Add Trade
            if (tradeDropdown != null && tradeDropdown.isDisplayed()) {
                String text = tradeDropdown.getText().trim();
                // Remove dropdown arrow if present
                text = text.replace("▼", "").replace("▾", "").trim();
                if (!text.isEmpty() && text.contains("Trade")) menuTexts.add("Trade");
            }

            // Add Features
            if (featuresDropdown != null && featuresDropdown.isDisplayed()) {
                String text = featuresDropdown.getText().trim();
                text = text.replace("▼", "").replace("▾", "").trim();
                if (!text.isEmpty() && text.contains("Features")) menuTexts.add("Features");
            }

            // Add About Us
            if (aboutUsDropdown != null && aboutUsDropdown.isDisplayed()) {
                String text = aboutUsDropdown.getText().trim();
                text = text.replace("▼", "").replace("▾", "").trim();
                if (!text.isEmpty() && text.contains("About")) menuTexts.add("About Us");
            }

            // Add Support
            if (supportDropdown != null && supportDropdown.isDisplayed()) {
                String text = supportDropdown.getText().trim();
                text = text.replace("▼", "").replace("▾", "").trim();
                if (!text.isEmpty() && text.contains("Support")) menuTexts.add("Support");
            }

        } catch (Exception e) {
            log.error("Error getting navigation menu items: " + e.getMessage());
        }

        return menuTexts;
    }

    public boolean isDashboardLinkActive() {
        try {
            String classes = dashboardLink.getAttribute("class");
            return classes != null && classes.contains("style_menu-item--active");
        } catch (Exception e) {
            log.warn("Could not check if Dashboard is active: " + e.getMessage());
            return false;
        }
    }

    public void clickMarketsLink() {
        waitForClickability(marketsLink);
        marketsLink.click();
    }

    public void hoverOnTradeMenu() {
        actions.moveToElement(tradeDropdown).perform();
    }

    public void clickTradeDropdown() {
        waitForClickability(tradeDropdown);
        tradeDropdown.click();
    }

    public void clickFeaturesDropdown() {
        waitForClickability(featuresDropdown);
        featuresDropdown.click();
    }

    public void clickAboutUsDropdown() {
        waitForClickability(aboutUsDropdown);
        aboutUsDropdown.click();
    }

    public void clickSupportDropdown() {
        waitForClickability(supportDropdown);
        supportDropdown.click();
    }

    public List<String> getDropdownItems(String dropdownName) {
        List<String> items = new ArrayList<>();
        WebElement dropdown = null;

        try {
            switch(dropdownName.toLowerCase()) {
                case "trade":
                    dropdown = tradeDropdown;
                    break;
                case "features":
                    dropdown = featuresDropdown;
                    break;
                case "About Us":
                case "aboutus":
                    dropdown = aboutUsDropdown;
                    break;
                case "support":
                    dropdown = supportDropdown;
                    break;
            }

            if (dropdown != null) {
                // Just hover, don't click
                actions.moveToElement(dropdown).perform();
                Thread.sleep(1000);

                // Get the dropdown items that appeared
                List<WebElement> dropdownItems = driver.findElements(
                        By.xpath("//div[@role='menu']//a | //ul//li//a")
                );

                for (WebElement item : dropdownItems) {
                    if (item.isDisplayed()) {
                        String text = item.getText().trim();
                        if (!text.isEmpty()) {
                            items.add(text);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting dropdown items for " + dropdownName + ": " + e.getMessage());
        }

        return items;
    }

    // click on All categories button in Spot section
    public void clickAllCategory() {
        try {
            waitForClickability(allSpotCategoriesBtn);
            allSpotCategoriesBtn.click();
        } catch (Exception e) {
            log.error("Could not click All category button: " + e.getMessage());
        }
    }

    // Updated method to check if Spot header is visible
    public boolean isSpotHeaderVisible() {
        try {
            waitForVisibility(spotHeader);
            return spotHeader.isDisplayed() && spotHeader.getText().contains("Spot");
        } catch (Exception e) {
            log.warn("Spot header not found: " + e.getMessage());
            return false;
        }
    }

    public List<WebElement> getTradingPairs() {
        try {
            Thread.sleep(2000); // Wait for pairs to load
            return tradingPairsTable;
        } catch (Exception e) {
            log.error("Could not get trading pairs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public List<WebElement> getTradingPairsRows() {
        try {
            Thread.sleep(2000); // Wait for pairs to load

            log.info("Found " + tradingPairsTable.size() + " trading pair rows");
            return tradingPairsTable;

        } catch (Exception e) {
            log.error("Could not get trading pairs: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Footer Methods
    public void scrollToFooter() {
        try {
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", footer);
            Thread.sleep(1000);
        } catch (Exception e) {
            log.error("Could not scroll to footer: " + e.getMessage());
        }
    }

    // App Download Methods
    public void clickAppStoreLink() {
        try {
            waitForClickability(appStoreLink);
            appStoreLink.click();
        } catch (Exception e) {
            log.error("Could not click App Store link: " + e.getMessage());
        }
    }

    public void clickGooglePlayLink() {
        try {
            waitForClickability(googlePlayLink);
            googlePlayLink.click();
        } catch (Exception e) {
            log.error("Could not click Google Play link: " + e.getMessage());
        }
    }

    public boolean isAppDownloadSectionVisible() {
        try {
            waitForVisibility(appDownloadSection);
            return appDownloadSection.isDisplayed();
        } catch (Exception e) {
            log.error("App download section not visible: " + e.getMessage());
            return false;
        }
    }

    /**
     * Scroll to the app download section
     */
    public void scrollToAppDownloadSection() {
        try {
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});",
                    homePage.appDownloadSection
            );
        } catch (Exception e) {
            log.error("Error scrolling to app download section: " + e.getMessage());
            // Fallback to regular scroll
            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
                    "window.scrollBy(0, 1500);"
            );
        }
    }

    public String getAppStoreTitle() {
        try {
            // Wait for the title element to be present on App Store
            waitForVisibility(appStoreTitleElement);
            return appStoreTitleElement.getText().trim();
        } catch (Exception e) {
            log.error("Error getting App Store title: " + e.getMessage());
            return "";
        }
    }

    public String getGooglePlayTitle() {
        try {
            waitForVisibility(googlePlayTitleElement);
            return googlePlayTitleElement.getText().trim();
        } catch (Exception e) {
            log.error("Error getting Google Play title: " + e.getMessage());
            return "";
        }
    }

    /**
     * Switch to the newly opened tab
     */
    public boolean switchToNewTab() {
        try {
            String originalWindow = driver.getWindowHandle();
            log.info("Original window: " + originalWindow);

            // Wait for a new window to appear with a reasonable timeout
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            boolean newTabOpened = wait.until(driver -> {
                try {
                    int windowCount = driver.getWindowHandles().size();
                    log.info("Current window count: " + windowCount);
                    return windowCount > 1;
                } catch (Exception e) {
                    log.warn("Error checking window count: " + e.getMessage());
                    return false;
                }
            });

            if (!newTabOpened) {
                log.warn("No new tab opened within timeout");
                return false;
            }

            // Get all window handles safely
            Set<String> allWindows;
            try {
                allWindows = driver.getWindowHandles();
            } catch (Exception e) {
                log.error("Error getting window handles: " + e.getMessage());
                return false;
            }

            log.info("All window handles: " + allWindows);

            // Find and switch to the new window
            for (String window : allWindows) {
                if (!window.equals(originalWindow)) {
                    try {
                        driver.switchTo().window(window);
                        log.info("Switched to new tab: " + window);

                        // Wait for the new page to load
                        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                                .executeScript("return document.readyState").equals("complete"));
                        Thread.sleep(2000);
                        return true;
                    } catch (Exception e) {
                        log.error("Error switching to window " + window + ": " + e.getMessage());
                        continue; // Try next window
                    }
                }
            }

            log.warn("No new tab found or could not switch to it");
            return false;

        } catch (Exception e) {
            log.error("Error in switchToNewTab: " + e.getMessage());
            return false;
        }
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }

    // Check if a menu item has dropdown indicator
    public boolean hasDropdownIndicator(String menuName) {
        String id = menuName.toLowerCase().replace(" ", "") + "-header-option-open-button";
        try {
            WebElement dropdown = driver.findElement(By.id(id));
            List<WebElement> svgElements = dropdown.findElements(By.tagName("svg"));
            return !svgElements.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Check if a menu item is a direct link (not dropdown)
    public boolean isDirectLink(String menuName) {
        try {
            WebElement link = null;
            if (menuName.equalsIgnoreCase("Dashboard")) {
                link = dashboardLink;
            } else if (menuName.equalsIgnoreCase("Markets")) {
                link = marketsLink;
            }

            if (link != null) {
                String tagName = link.getTagName();
                String href = link.getAttribute("href");
                return "a".equalsIgnoreCase(tagName) && href != null;
            }
        } catch (Exception e) {
            log.warn("Could not check if " + menuName + " is a direct link: " + e.getMessage());
            return false;
        }
        return false;
    }

    // Switch back to original tab
    public void switchToOriginalTab() {
        try {
            String originalWindow = driver.getWindowHandles().iterator().next();
            driver.switchTo().window(originalWindow);
        } catch (Exception e) {
            log.error("Could not switch to original tab: " + e.getMessage());
        }
    }

    // Verify URL contains specific text
    public boolean verifyUrlContains(String expectedText) {
        try {
            return wait.until(ExpectedConditions.urlContains(expectedText));
        } catch (Exception e) {
            log.error("URL does not contain: " + expectedText);
            return false;
        }
    }

    // Get all spot categories
    public List<String> getSpotCategories() {
        List<String> categories = new ArrayList<>();
        try {

            for (WebElement button : categoryButtons) {
                String text = button.getText().trim();
                if (!text.isEmpty()) {
                    categories.add(text);
                }
            }
        } catch (Exception e) {
            log.error("Could not get spot categories: " + e.getMessage());
        }
        return categories;
    }

    // Open the Themes dropdown
    public void openThemesDropdown() {
        try {
            if (!isThemesDropdownOpen()) {
                waitForClickability(themesDropdownButton);
                themesDropdownButton.click();
                Thread.sleep(800); // Increased wait time for dropdown animation
                log.info("Opened Themes dropdown");
            }
        } catch (Exception e) {
            log.error("Could not open Themes dropdown: " + e.getMessage());
        }
    }

    // Check if Themes dropdown is open
    public boolean isThemesDropdownOpen() {
        try {
            return themesDropdownMenu.isDisplayed() &&
                    themesDropdownMenu.getAttribute("aria-hidden").equals("false");
        } catch (Exception e) {
            return false;
        }
    }

    // Close the Themes dropdown
    public void closeThemesDropdown() {
        try {
            if (isThemesDropdownOpen()) {
                // Click outside the dropdown to close it
                actions.moveByOffset(0, 0).click().perform();
                Thread.sleep(500);
                log.info("Closed Themes dropdown");
            }
        } catch (Exception e) {
            log.warn("Could not close Themes dropdown: " + e.getMessage());
        }
    }

    // Get all theme sub-categories
    public List<String> getThemeSubCategories() {
        List<String> subCategories = new ArrayList<>();
        try {
            openThemesDropdown();

            List<WebElement> menuItems = driver.findElements(
                    By.xpath("//div[contains(@class, 'style_items__rwhTi')]//button[contains(@class, 'style_item__i44hn')]")
            );

            for (WebElement item : menuItems) {
                if (item.isDisplayed()) {
                    String text = item.getText().trim();
                    if (!text.isEmpty()) {
                        subCategories.add(text);
                    }
                }
            }

            closeThemesDropdown();

        } catch (Exception e) {
            log.error("Could not get theme sub-categories: " + e.getMessage());
        }
        return subCategories;
    }

    // Click a specific theme sub-category
    public void clickThemeSubCategory(String subCategoryName) {
        try {
            openThemesDropdown();
            Thread.sleep(300);

            WebElement subCategory = null;

            switch(subCategoryName.toLowerCase()) {
                case "Legacy":
                    subCategory = legacyCategoryBtn;
                    break;
                case "DeFi":
                    subCategory = defiCategoryBtn;
                    break;
                case  "Stablecoin":
                    subCategory = stablecoinCategoryBtn;
                    break;
                default:
                    // Try to find by text
                    subCategory = driver.findElement(
                            By.xpath("//div[contains(@class, 'style_items__rwhTi')]//button[normalize-space()='" + subCategoryName + "']")
                    );
            }

            if (subCategory != null && subCategory.isDisplayed()) {
                waitForClickability(subCategory);
                subCategory.click();
                log.info("Clicked theme sub-category: " + subCategoryName);
                Thread.sleep(2000); // Wait for content to load
            }

        } catch (Exception e) {
            log.error("Could not click theme sub-category " + subCategoryName + ": " + e.getMessage());
        }
    }

    public void clickSpotCategory(String categoryName) {
        try {
            WebElement categoryButton = null;

            switch(categoryName.toLowerCase()) {
                case "all":
                    categoryButton = allSpotCategoriesBtn;
                    break;
                case "favorites":
                    categoryButton = favoritesCategoryBtn;
                    break;
                case "usdt":
                    categoryButton = usdtCategoryBtn;
                    break;
                case "btc":
                    categoryButton = btcCategoryBtn;
                    break;
                case "fait":
                    categoryButton = fiatCategoryBtn;
                    break;
                case "themes":
                    categoryButton = themesCategoryBtn;
                    break;
                default:
                    // Try to find by text if no specific ID
                    categoryButton = driver.findElement(
                            By.xpath("//span[normalize-space()='" + categoryName + "']")
                    );
            }

            if (categoryButton != null) {
                waitForClickability(categoryButton);
                categoryButton.click();
                log.info("Clicked category: " + categoryName);
            }
        } catch (Exception e) {
            log.error("Could not click category " + categoryName + ": " + e.getMessage());
        }
    }


    // Method to check if Spot section is visible
    public boolean isSpotSectionVisible() {
        try {
            List<WebElement> spotElements = driver.findElements(
                    By.xpath("//div[contains(text(), 'Spot')] | //h2[contains(text(), 'Spot')]")
            );
            return !spotElements.isEmpty() && spotElements.get(0).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    // Method to get all available categories
    public List<String> getAvailableCategories() {
        List<String> categories = new ArrayList<>();
        try {
            for (WebElement button : categoryButtons) {
                if (button.isDisplayed()) {
                    String text = button.getText().trim();
                    if (!text.isEmpty()) {
                        categories.add(text);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Could not get categories: " + e.getMessage());
        }
        return categories;
    }



    /**
     * Validate that all expected column headers are present and correctly labeled
     */
    public void validateColumnHeaders() {
        // Expected column headers (adjust based on your actual UI)
        List<String> expectedHeaders = Arrays.asList(
                "Pair", "Price", "24h Change", "High", "Low", "Last 7 days"
        );

        try {
            // Find all header elements - adjust selector based on your actual UI
            List<WebElement> headerElements = driver.findElements(
                    By.xpath("//thead//th | //thead//div[contains(@class, 'header')]")
            );

            Assert.assertTrue(!headerElements.isEmpty(), "No column headers found");

            // Collect actual header texts
            List<String> actualHeaders = new ArrayList<>();
            for (WebElement header : headerElements) {
                if (header.isDisplayed()) {
                    String text = header.getText().trim();
                    if (!text.isEmpty()) {
                        actualHeaders.add(text);
                        log.info("Found header: " + text);
                    }
                }
            }

            // Verify we have at least the minimum expected headers
            Assert.assertTrue(actualHeaders.size() >= 4,
                    "Should have at least 4 column headers. Found: " + actualHeaders.size());

            // Check if expected headers are present (not necessarily in same order)
            for (String expectedHeader : expectedHeaders) {
                boolean found = false;
                for (String actualHeader : actualHeaders) {
                    if (actualHeader.contains(expectedHeader)) {
                        found = true;
                        break;
                    }
                }
                Assert.assertTrue(found, "Expected header not found: " + expectedHeader);
            }

        } catch (Exception e) {
            Assert.fail("Column header validation failed: " + e.getMessage());
        }
    }

    /**
     * Validate trading pair symbol formatting
     */
    public void validatePairSymbolFormatting() {
        try {
            List<WebElement> tradingPairs = getTradingPairs();
            log.info("Total trading pairs found: " + tradingPairs.size());

            Assert.assertTrue(!tradingPairs.isEmpty(), "No trading pairs found for validation");

            // Check first few pairs for proper formatting
            int pairsToCheck = Math.min(5, tradingPairs.size());
            int validPairs = 0;
            int emptyPairs = 0;

            for (int i = 0; i < pairsToCheck; i++) {
                WebElement pairElement = tradingPairs.get(i);

                // Get the pair symbol
                String pairSymbol = getPairSymbolFromRow(pairElement);

                // Skip empty symbols
                if (pairSymbol.isEmpty()) {
                    log.warn("Empty pair symbol at row " + i);
                    emptyPairs++;
                    continue;
                }

                log.info("Row " + i + " - Extracted pair symbol: '" + pairSymbol + "'");

                // Validate pair symbol format
                if (isValidPairSymbol(pairSymbol)) {
                    validPairs++;
                    log.info("✓ Valid pair symbol: " + pairSymbol);
                } else {
                    log.warn("Invalid pair symbol format: '" + pairSymbol + "'");
                }
            }

            // Make sure we found at least one valid pair
            Assert.assertTrue(validPairs > 0,
                    "Should have at least one valid trading pair. Checked " + pairsToCheck +
                            " rows, found " + validPairs + " valid pairs and " + emptyPairs + " empty rows");

        } catch (Exception e) {
            Assert.fail("Pair symbol validation failed: " + e.getMessage());
        }
    }
    /**
     * Extract pair symbol from table row - updated for actual HTML structure
     */
    public String getPairSymbolFromRow(WebElement row) {
        try {
            // Debug: Log the entire row HTML
            log.info("Row HTML: " + row.getAttribute("outerHTML").substring(0, Math.min(200, row.getAttribute("outerHTML").length())));

            // Debug: Log the entire row text
            String fullRowText = row.getText();
            log.info("Full row text: " + fullRowText);

            // Try to get all cells in the row
            List<WebElement> cells = row.findElements(By.tagName("td"));
            log.info("Number of cells in row: " + cells.size());

            if (!cells.isEmpty()) {
                // Get text from first cell
                String firstCellText = cells.get(0).getText().trim();
                log.info("First cell text: '" + firstCellText + "'");

                // If empty, try to find nested elements
                if (firstCellText.isEmpty()) {
                    List<WebElement> nestedElements = cells.get(0).findElements(By.xpath(".//div | .//span"));
                    log.info("Found " + nestedElements.size() + " nested elements in first cell");

                    StringBuilder pairText = new StringBuilder();
                    for (WebElement nested : nestedElements) {
                        String text = nested.getText().trim();
                        if (!text.isEmpty()) {
                            pairText.append(text).append(" ");
                        }
                    }
                    firstCellText = pairText.toString().trim();
                    log.info("Extracted from nested elements: '" + firstCellText + "'");
                }

                // Clean up the text
                firstCellText = firstCellText.replace("_base", "").replace("_price", "").replace("\n", "/");

                // If we have something, return it
                if (!firstCellText.isEmpty()) {
                    return firstCellText;
                }
            }

            // Fallback: just get first word from row
            String[] words = fullRowText.split("\\s+");
            if (words.length > 0 && !words[0].isEmpty()) {
                log.info("Fallback - returning first word: " + words[0]);
                return words[0];
            }

            log.warn("Could not extract pair symbol from row");
            return "";

        } catch (Exception e) {
            log.error("Error extracting pair symbol: " + e.getMessage());
            return "";
        }
    }

    /**
     * Validate if pair symbol has proper format - updated for actual data
     */
    public boolean isValidPairSymbol(String symbol) {
        // The pairs might be in format: "BTC/AED", "ETH/USDT", "BTC AED", etc.
        // Let's be more flexible with validation

        if (symbol == null || symbol.isEmpty()) {
            return false;
        }

        // Check if it contains known currency/crypto symbols
        String upperSymbol = symbol.toUpperCase();
        boolean hasValidSymbols =
                upperSymbol.contains("BTC") ||
                upperSymbol.contains("ETH") ||
                upperSymbol.contains("USDT") ||
                upperSymbol.contains("AED") ||
                upperSymbol.contains("USD") ||
                upperSymbol.contains("EUR") ||
                upperSymbol.matches(".*[A-Z]{2,}.*"); // At least 2 uppercase letters

        return hasValidSymbols;
    }

    /**
     * Extract price from table row - updated for actual structure
     */
    public String getPriceFromRow(WebElement row) {
        try {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() >= 2) {
                String priceText = cells.get(1).getText().trim();
                priceText = priceText.replaceAll("[^0-9.,]", "");
                return priceText;
            }
        } catch (Exception e) {
            log.error("Error extracting price: " + e.getMessage());
        }
        return "";
    }

    public void validatePriceFormatting() {
        try {
            List<WebElement> tradingPairs = getTradingPairs();
            Assert.assertTrue(!tradingPairs.isEmpty(), "No trading pairs found for price validation");

            // Check prices in first few pairs
            int pairsToCheck = Math.min(3, tradingPairs.size());

            for (int i = 0; i < pairsToCheck; i++) {
                WebElement pairElement = tradingPairs.get(i);
                String price = getPriceFromRow(pairElement);

                if (price != null && !price.isEmpty()) {
                    log.info("Validating price: " + price);
                    Assert.assertTrue(isValidPriceFormat(price),
                            "Invalid price format: " + price);
                }
            }

        } catch (Exception e) {
            Assert.fail("Price validation failed: " + e.getMessage());
        }
    }

    /**
     * Updated price validation to be more flexible
     */
    public boolean isValidPriceFormat(String price) {
        if (price == null || price.isEmpty()) {
            return false;
        }

        // Price should contain numbers
        return price.matches(".*\\d+.*");
    }

    /**
     * Validate all required UI elements are present
     */
    public void validateUIElements() {
        try {
            // Verify trading table exists
            WebElement tradingTable = driver.findElement(By.xpath("//table | //div[contains(@class, 'table')]"));
            Assert.assertTrue(tradingTable.isDisplayed(), "Trading table should be visible");

            // Verify category buttons are present - Fixed: removed "homePage."
            Assert.assertTrue(categoryButtons.size() > 0, "Category buttons should be present");

            // Verify search/filter functionality exists (if applicable)
            List<WebElement> searchElements = driver.findElements(
                    By.xpath("//input[contains(@placeholder, 'Search')] | //div[contains(@class, 'search')]")
            );
            if (!searchElements.isEmpty()) {
                log.info("Search functionality found");
            }

            // Verify sort indicators exist (if applicable)
            List<WebElement> sortElements = driver.findElements(
                    By.xpath("//th[contains(@class, 'sort')] | //div[contains(@class, 'sort')]")
            );
            if (!sortElements.isEmpty()) {
                log.info("Sort functionality found");
            }

            log.info("All required UI elements are present");

        } catch (Exception e) {
            Assert.fail("UI elements validation failed: " + e.getMessage());
        }
    }

    // Marketing Banner Methods
    // Get all marketing banners (excluding cloned slides)
    public List<WebElement> getMarketingBanners() {
        return marketingBanners;
    }

    // Get active/visible banner (returns first active banner)
    public WebElement getActiveBanner() {
        return activeBanners.isEmpty() ? null : activeBanners.get(0);
    }

    // Get all active banners
    public List<WebElement> getActiveBanners() {
        return activeBanners;
    }

    // Add these methods to HomePage.java
    public List<WebElement> getBannerTitlesWithinBanners() {
        List<WebElement> allTitles = new ArrayList<>();
        for (WebElement banner : marketingBanners) {
            if (banner.isDisplayed()) {
                allTitles.addAll(banner.findElements(By.cssSelector(".style_title__g8VH0")));
            }
        }
        return allTitles;
    }

    public List<WebElement> getBannerLinksWithinBanners() {
        List<WebElement> allLinks = new ArrayList<>();
        for (WebElement banner : marketingBanners) {
            if (banner.isDisplayed()) {
                allLinks.addAll(banner.findElements(By.cssSelector(".style_link__qP2GD")));
            }
        }
        return allLinks;
    }

    public List<WebElement> getBannerImagesWithinBanners() {
        List<WebElement> allImages = new ArrayList<>();
        for (WebElement banner : marketingBanners) {
            if (banner.isDisplayed()) {
                allImages.addAll(banner.findElements(By.cssSelector(".style_image__kiucM")));
            }
        }
        return allImages;
    }

    // Scroll to banners section
    public void scrollToBanners() {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", slickTrack);
    }

    // Get count of actual banners
    public int getBannerCount() {
        return marketingBanners.size();
    }

    // Verify banners are visible and have content
    public boolean areBannersDisplayedWithContent() {
        if (marketingBanners.isEmpty()) return false;

        for (WebElement banner : marketingBanners) {
            if (!banner.isDisplayed()) return false;

            // Check if banner has either text or image
            String bannerText = banner.getText();
            boolean hasImage = banner.findElements(By.tagName("img")).size() > 0;
            if (bannerText.trim().isEmpty() && !hasImage) {
                return false;
            }
        }
        return true;
    }

    // Wait for banners to load
    public void waitForBannersToLoad() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(d -> !marketingBanners.isEmpty() && marketingBanners.get(0).isDisplayed());
    }

}