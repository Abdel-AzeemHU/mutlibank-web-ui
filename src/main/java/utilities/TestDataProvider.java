package utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TestDataProvider {

    private static final Logger logger = LoggerFactory.getLogger(TestDataProvider.class);
    private static final String DEFAULT_DATA_FILE = "expected_texts.json";
    private JsonNode rootNode;
    private ObjectMapper objectMapper;

    public TestDataProvider() {
        this(DEFAULT_DATA_FILE);
    }

    public TestDataProvider(String dataFileName) {
        this.objectMapper = new ObjectMapper();
        loadTestData(dataFileName);
    }

    private void loadTestData(String fileName) {
        try {
            // Try to load from resources folder first
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);

            if (inputStream != null) {
                rootNode = objectMapper.readTree(inputStream);
                logger.info("Successfully loaded test data from resources: " + fileName);
            } else {
                // Try to load from file system
                File file = new File("src/test/resources/" + fileName);
                if (file.exists()) {
                    rootNode = objectMapper.readTree(file);
                    logger.info("Successfully loaded test data from file: " + file.getPath());
                } else {
                    logger.error("Test data file not found: " + fileName);
                    throw new RuntimeException("Could not find test data file: " + fileName);
                }
            }
        } catch (IOException e) {
            logger.error("Error loading test data file: " + fileName, e);
            throw new RuntimeException("Failed to load test data", e);
        }
    }

    /**
     * Get expected navigation menu items
     */
    public List<String> getExpectedNavigationItems() {
        List<String> items = new ArrayList<>();
        JsonNode navItems = rootNode.path("navigation").path("mainMenuItems");

        if (navItems.isArray()) {
            for (JsonNode item : navItems) {
                items.add(item.asText());
            }
        }

        return items;
    }

    /**
     * Get expected dropdown items for a specific menu
     */
    public List<String> getExpectedDropdownItems(String dropdownName) {
        List<String> items = new ArrayList<>();
        String normalizedName = dropdownName.toLowerCase().replace(" ", "");

        // Map display names to JSON keys
        String jsonKey = switch (normalizedName) {
            case "trade" -> "trade";
            case "features" -> "features";
            case "aboutus" -> "aboutUs";
            case "support" -> "support";
            default -> normalizedName;
        };

        JsonNode dropdownItems = rootNode.path("navigation").path("dropdowns").path(jsonKey);

        if (dropdownItems.isArray()) {
            for (JsonNode item : dropdownItems) {
                items.add(item.asText());
            }
        }

        return items;
    }

    /**
     * Get expected Spot trading categories
     */
    public List<String> getSpotCategories() {
        List<String> categories = new ArrayList<>();
        JsonNode spotCategories = rootNode.path("spot").path("categories");

        if (spotCategories.isArray()) {
            for (JsonNode category : spotCategories) {
                categories.add(category.asText());
            }
        }

        return categories;
    }

    /**
     * Get expected trading pair columns
     */
    public List<String> getTradingPairColumns() {
        List<String> columns = new ArrayList<>();
        JsonNode pairColumns = rootNode.path("spot").path("tradingPairColumns");

        if (pairColumns.isArray()) {
            for (JsonNode column : pairColumns) {
                columns.add(column.asText());
            }
        }

        return columns;
    }

    /**
     * Get expected marketing banner texts
     */
    public List<String> getMarketingBanners() {
        List<String> banners = new ArrayList<>();
        JsonNode bannerTexts = rootNode.path("footer").path("marketingBanners");

        if (bannerTexts.isArray()) {
            for (JsonNode banner : bannerTexts) {
                banners.add(banner.asText());
            }
        }

        return banners;
    }

    /**
     * Get app store expected values
     */
    public Map<String, String> getAppStoreExpectations() {
        JsonNode appStore = rootNode.path("appDownload").path("appStore");
        return Map.of(
                "urlPattern", appStore.path("urlPattern").asText("apps.apple.com"),
                "titleContains", appStore.path("titleContains").asText("MultiBank"),
                "exactTitle", appStore.path("exactTitle").asText("MultiBank Group on the App Store")
        );
    }

    /**
     * Get Google Play expected values
     */
    public Map<String, String> getGooglePlayExpectations() {
        JsonNode googlePlay = rootNode.path("appDownload").path("googlePlay");
        return Map.of(
                "urlPattern", googlePlay.path("urlPattern").asText("play.google.com"),
                "titleContains", googlePlay.path("titleContains").asText("MultiBank"),
                "exactTitle", googlePlay.path("exactTitle").asText("MultiBank Group - Apps on Google Play")
        );
    }

    /**
     * Get Why MultiLink page sections
     */
    public List<String> getWhyMultiLinkSections() {
        List<String> sections = new ArrayList<>();
        JsonNode whySections = rootNode.path("whyMultiLink").path("sections");

        if (whySections.isArray()) {
            for (JsonNode section : whySections) {
                sections.add(section.asText());
            }
        }

        return sections;
    }

    /**
     * Get Why MultiLink page components
     */
    public List<String> getWhyMultiLinkComponents() {
        List<String> components = new ArrayList<>();
        JsonNode whyComponents = rootNode.path("whyMultiLink").path("components");

        if (whyComponents.isArray()) {
            for (JsonNode component : whyComponents) {
                components.add(component.asText());
            }
        }

        return components;
    }

    /**
     * Generic method to get any value from JSON using path
     */
    public String getValue(String path) {
        String[] parts = path.split("\\.");
        JsonNode current = rootNode;

        for (String part : parts) {
            current = current.path(part);
            if (current.isMissingNode()) {
                logger.warn("Path not found in test data: " + path);
                return "";
            }
        }

        return current.asText("");
    }

    /**
     * Generic method to get a list from JSON using path
     */
    public List<String> getList(String path) {
        List<String> result = new ArrayList<>();
        String[] parts = path.split("\\.");
        JsonNode current = rootNode;

        for (String part : parts) {
            current = current.path(part);
            if (current.isMissingNode()) {
                logger.warn("Path not found in test data: " + path);
                return result;
            }
        }

        if (current.isArray()) {
            for (JsonNode item : current) {
                result.add(item.asText());
            }
        }

        return result;
    }

    /**
     * Reload test data from file (useful for data-driven testing)
     */
    public void reloadData() {
        loadTestData(DEFAULT_DATA_FILE);
    }

    /**
     * Load test data from a different file
     */
    public void loadDataFromFile(String fileName) {
        loadTestData(fileName);
    }
}