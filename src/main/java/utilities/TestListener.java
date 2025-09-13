package utilities;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.testng.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class testlistener extends common_actions implements ITestListener {

    private static ExtentReports extent;
    private static ExtentSparkReporter reporter;
    private static final Map<String, ExtentTest> extentTestMap = new HashMap<>();
    private static final Map<String, ITestResult> finalResults = new HashMap<>();
    private static final Map<String, Integer> retryTracker = new HashMap<>();
    private static final Map<String, String> displayNames = new HashMap<>();

    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            reporter = new ExtentSparkReporter("./reports/Waslah_Execution_Report.html");
            reporter.config().setDocumentTitle("Waslah Execution Results");
            reporter.config().setReportName("Waslah Execution Results");
            reporter.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(reporter);
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        String uniqueName = getUniqueTestName(result);
        String methodOnlyName = result.getMethod().getMethodName();
        displayNames.put(uniqueName, methodOnlyName);

        if (!extentTestMap.containsKey(uniqueName)) {
            ExtentTest test = extent.createTest(methodOnlyName).assignAuthor("Deepita_Sharma").assignCategory("REGRESSION");
            extentTestMap.put(uniqueName, test);
            test.log(Status.INFO, "Test started: " + methodOnlyName);
        } else {
            ExtentTest test = extentTestMap.get(uniqueName);
            test.log(Status.INFO, "Retrying test: " + methodOnlyName);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String uniqueName = getUniqueTestName(result);
        String methodName = displayNames.getOrDefault(uniqueName, result.getMethod().getMethodName());
        ExtentTest test = extentTestMap.get(uniqueName);
        int retryCount = getRetryCount(result);

        if (test != null) {
            test.log(Status.PASS, methodName + " passed" + (retryCount > 0 ? " after " + retryCount + " retry attempt(s)." : " successfully."));
        }
        finalResults.put(uniqueName, result);
        retryTracker.put(uniqueName, retryCount);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String uniqueName = getUniqueTestName(result);
        String methodName = displayNames.getOrDefault(uniqueName, result.getMethod().getMethodName());
        ExtentTest test = extentTestMap.get(uniqueName);
        int retryCount = getRetryCount(result);

        if (test != null) {
            test.log(Status.FAIL, methodName + " failed after " + retryCount + " retry attempt(s).");
            takeScreenshot(methodName);
        }
        finalResults.put(uniqueName, result);
        retryTracker.put(uniqueName, retryCount);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String uniqueName = getUniqueTestName(result);
        String methodName = displayNames.getOrDefault(uniqueName, result.getMethod().getMethodName());
        Object retryAnalyzer = result.getMethod().getRetryAnalyzer(result);

        if (retryAnalyzer instanceof RetryAnalyzer) {
            RetryAnalyzer analyzer = (RetryAnalyzer) retryAnalyzer;
            if (analyzer.getRetryCount() < RetryAnalyzer.maxRetryCount) {
                retryTracker.put(uniqueName, analyzer.getRetryCount());
                return; // Not a final skip, due to retry logic
            }
        }

        ExtentTest test = extentTestMap.get(uniqueName);
        if (test != null) {
            test.log(Status.SKIP, methodName + " was skipped and not retried.");
            takeScreenshot(methodName);
        }
        finalResults.put(uniqueName, result);
        retryTracker.put(uniqueName, 0);
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
            System.out.println("✅ Extent Report generated successfully.");
        }
        try {
            exportTestSummary("./reports/test_summary.txt");
        } catch (IOException e) {
            System.err.println("Error writing test summary: " + e.getMessage());
        }
    }

    private String getUniqueTestName(ITestResult result) {
        return result.getTestClass().getName() + "." + result.getMethod().getMethodName();
    }

    private int getRetryCount(ITestResult result) {
        Object retryAnalyzer = result.getMethod().getRetryAnalyzer(result);
        if (retryAnalyzer instanceof RetryAnalyzer) {
            return ((RetryAnalyzer) retryAnalyzer).getRetryCount();
        }
        return 0;
    }

    public void takeScreenshot(String testName) {
        // Add screenshot capture logic here
    }

    public static void exportTestSummary(String filePath) throws IOException {
        int passed = 0, failed = 0, total = 0, retryCount = 0, skipDueToRetry = 0;

        for (Map.Entry<String, ITestResult> entry : finalResults.entrySet()) {
            String testName = entry.getKey();
            ITestResult result = entry.getValue();
            total++;

            int retries = retryTracker.getOrDefault(testName, 0);
            retryCount += retries;
            if (retries > 0) skipDueToRetry += retries;

            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    passed++;
                    break;
                case ITestResult.FAILURE:
                    failed++;
                    break;
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a");
        String date = LocalDateTime.now().format(formatter);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("Execution_date=" + date + "\n");
            writer.write("Total_tests=" + total + "\n");
            writer.write("Passed=" + passed + "\n");
            writer.write("Failed=" + failed + "\n");
            writer.write("Retries=" + retryCount + "\n");
            writer.write("Skipped=" + "0" + "\n");
        }

        System.out.println("✅ Test summary exported successfully to: " + filePath);
    }
}
