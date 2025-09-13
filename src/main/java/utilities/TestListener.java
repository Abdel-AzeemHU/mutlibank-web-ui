package utilities;

import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TestListener extends CommonActions implements ITestListener {

    private static ExtentReports extent;
    private static ExtentSparkReporter reporter;
    private static final Map<String, ExtentTest> extentTestMap = new HashMap<>();
    private static final Map<String, ITestResult> finalResults = new HashMap<>();
    private static final Map<String, Integer> retryTracker = new HashMap<>();
    private static final Map<String, String> displayNames = new HashMap<>();
    private static final Map<String, Boolean> testCompleted = new HashMap<>();
    private static final Map<String, Boolean> videoRecordingStarted = new HashMap<>();


    @Override
    public void onStart(ITestContext context) {
        if (extent == null) {
            reporter = new ExtentSparkReporter("./reports/UI_Automation_Report.html");
            reporter.config().setDocumentTitle("Multibank Web Execution Results");
            reporter.config().setReportName("Multibank Web Execution Results");
            reporter.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(reporter);
        }

        System.out.println("🔹 Test execution started");
        // Check if video recording is available
        if (isPipelineEnvironment()) {
            System.out.println("📹 Pipeline environment detected - video recording will be enabled for failed tests");
        } else {
            System.out.println("💻 Local environment detected - video recording disabled");
        }

    }

    @Override
    public void onTestStart(ITestResult result) {
        String uniqueName = getUniqueTestName(result);
        String methodOnlyName = result.getMethod().getMethodName();
        displayNames.put(uniqueName, methodOnlyName);

        if (!extentTestMap.containsKey(uniqueName)) {
            ExtentTest test = extent.createTest(methodOnlyName).assignAuthor("Abdelazeem").assignCategory("REGRESSION");
            extentTestMap.put(uniqueName, test);
            test.log(Status.INFO, "Test started: " + methodOnlyName);

            // Start video recording for this test (pipeline only)
            if (isPipelineEnvironment()) {
                VideoRecorder.startRecording(methodOnlyName);
                videoRecordingStarted.put(uniqueName, true);
            }
        } else {
            ExtentTest test = extentTestMap.get(uniqueName);
            test.log(Status.INFO, "Retrying test: " + methodOnlyName);

            // Start recording for retry if not already started
            if (isPipelineEnvironment() && !videoRecordingStarted.getOrDefault(uniqueName, false)) {
                VideoRecorder.startRecording(methodOnlyName);
                videoRecordingStarted.put(uniqueName, true);
            }
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String uniqueName = getUniqueTestName(result);
        String methodName = displayNames.getOrDefault(uniqueName, result.getMethod().getMethodName());
        ExtentTest test = extentTestMap.get(uniqueName);
        int retryCount = getRetryCount(result);

        // Mark test as completed successfully
        testCompleted.put(uniqueName, true);

        if (test != null) {
            test.log(Status.PASS, methodName + " passed" + (retryCount > 0 ? " after " + retryCount + " retry attempt(s)." : " successfully."));
        }

        // Stop recording and delete the video since test passed
        if (videoRecordingStarted.getOrDefault(uniqueName, false)) {
            VideoRecorder.stopRecording();
            VideoRecorder.deleteRecording();
            videoRecordingStarted.put(uniqueName, false);
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

        // Check if this is the final failure (no more retries available)
        Object retryAnalyzer = result.getMethod().getRetryAnalyzer(result);
        boolean isFinalFailure = true;
        if (retryAnalyzer instanceof RetryAnalyzer) {
            RetryAnalyzer analyzer = (RetryAnalyzer) retryAnalyzer;
            isFinalFailure = analyzer.getRetryCount() >= RetryAnalyzer.maxRetryCount;
        }

        if (test != null) {
            if (isFinalFailure) {
                // Stop and save video recording for failed test
                if (videoRecordingStarted.getOrDefault(uniqueName, false)) {
                    VideoRecorder.stopRecording();
                    videoRecordingStarted.put(uniqueName, false);

                    // Get video path and add to report
                    String videoPath = VideoRecorder.getVideoPath();
                    if (videoPath != null) {
                        File videoFile = new File(videoPath);
                        if (videoFile.exists()) {
                            test.log(Status.INFO, "🎥 Video recording saved: " + videoFile.getName());
                        }
                    }
                }

                // Capture screenshot for final failures
                String base64Screenshot = captureScreenshotAsBase64(result, methodName);

                if (base64Screenshot != null) {
                    test.fail(methodName + " failed after " + retryCount + " retry attempt(s).",
                            MediaEntityBuilder.createScreenCaptureFromBase64String(base64Screenshot).build());
                } else {
                    test.log(Status.FAIL, methodName + " failed after " + retryCount + " retry attempt(s).");
                }

                // Log the exception if available
                if (result.getThrowable() != null) {
                    test.fail(result.getThrowable());
                }
            } else {
                // Just log the retry attempt without stopping the recording
                test.log(Status.INFO, methodName + " failed (attempt " + (retryCount + 1) + "), will retry...");
            }
        }

        // Only store as final result if this is the final failure
        if (isFinalFailure) {
            finalResults.put(uniqueName, result);
            retryTracker.put(uniqueName, retryCount);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String uniqueName = getUniqueTestName(result);
        String methodName = displayNames.getOrDefault(uniqueName, result.getMethod().getMethodName());

        // Check if this test was already completed successfully
        if (testCompleted.getOrDefault(uniqueName, false)) {
            // This is a phantom skip after successful completion, ignore it
            return;
        }

        Object retryAnalyzer = result.getMethod().getRetryAnalyzer(result);

        if (retryAnalyzer instanceof RetryAnalyzer) {
            RetryAnalyzer analyzer = (RetryAnalyzer) retryAnalyzer;
            if (analyzer.getRetryCount() < RetryAnalyzer.maxRetryCount) {
                retryTracker.put(uniqueName, analyzer.getRetryCount());
                System.out.println("🔄 Test will be retried: " + methodName + " (attempt " + (analyzer.getRetryCount() + 1) + ")");

                ExtentTest test = extentTestMap.get(uniqueName);
                if (test != null) {
                    test.log(Status.INFO, methodName + " skipped (attempt " + (analyzer.getRetryCount() + 1) + "), will retry...");
                }
                return; // Not a final skip, due to retry logic
            }
        }

        // This is a genuine skip (no retry or max retries reached)
        ExtentTest test = extentTestMap.get(uniqueName);
        if (test != null) {
            test.log(Status.SKIP, methodName + " was skipped and not retried.");
        }

        // Stop and delete video for skipped tests
        if (videoRecordingStarted.getOrDefault(uniqueName, false)) {
            VideoRecorder.stopRecording();
            VideoRecorder.deleteRecording();
            videoRecordingStarted.put(uniqueName, false);
        }

        finalResults.put(uniqueName, result);
        retryTracker.put(uniqueName, 0);
    }

    @Override
    public void onFinish(ITestContext context) {
        // Ensure any remaining recordings are stopped
        if (VideoRecorder.isRecording()) {
            System.out.println("⚠️ Cleaning up remaining video recording...");
            VideoRecorder.stopRecording();
        }

        // Force cleanup just in case
        VideoRecorder.forceCleanup();

        if (extent != null) {
            extent.flush();
            System.out.println("✅ Extent Report generated successfully.");
        }

        // Count video files created
        File videoDir = new File("./reports/videos");
        if (videoDir.exists()) {
            int videoCount = countVideoFiles(videoDir);
            if (videoCount > 0) {
                System.out.println("📹 Total video recordings saved: " + videoCount);
            }
        }

        try {
            exportTestSummary("./reports/test_summary.txt");
        } catch (IOException e) {
            System.err.println("Error writing test summary: " + e.getMessage());
        }
    }

    /**
     * Check if running in pipeline environment
     */
    private boolean isPipelineEnvironment() {
        return System.getenv("CI") != null ||
                System.getenv("AZURE_PIPELINES") != null ||
                System.getenv("BUILD_ID") != null ||
                System.getenv("AGENT_ID") != null;
    }

    /**
     * Count video files in directory
     */
    private int countVideoFiles(File dir) {
        int count = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    count += countVideoFiles(file);
                } else if (file.getName().endsWith(".mp4")) {
                    count++;
                }
            }
        }
        return count;
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

    /**
     * Captures screenshot as Base64 using pure Java (no extra dependencies)
     */
    private String captureScreenshotAsBase64(ITestResult result, String testName) {
        try {
            if (getDriver() == null) {
                System.err.println("Driver is null - cannot capture screenshot for: " + testName);
                return null;
            }

            // Capture screenshot as Base64 (for Extent Report)
            TakesScreenshot screenshot = (TakesScreenshot) getDriver();
            String base64Screenshot = screenshot.getScreenshotAs(OutputType.BASE64);

            // Also save to file for pipeline artifacts
            saveScreenshotToFileSimple(screenshot, testName);

            return base64Screenshot;
        } catch (Exception e) {
            System.err.println("Failed to capture screenshot for " + testName + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * Save screenshot to file using pipeline-friendly directory structure
     */
    private void saveScreenshotToFileSimple(TakesScreenshot screenshot, String testName) {
        try {
            // Use pipeline-friendly directory structure
            String screenshotBaseDir = System.getProperty("screenshot.dir", "./reports/screenshots");
            File screenshotDir = new File(screenshotBaseDir);

            // Don't create timestamp subdirectories - pipeline expects flat structure
            if (!screenshotDir.exists()) {
                boolean created = screenshotDir.mkdirs();
                System.out.println("📂 Created screenshot directory: " + screenshotDir.getAbsolutePath() + " (success: " + created + ")");
            }

            File screenshotFile = screenshot.getScreenshotAs(OutputType.FILE);

            // Include timestamp in filename instead of directory
            String timestamp = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(LocalDateTime.now());
            File destFile = new File(screenshotDir, testName + "_" + timestamp + ".png");

            java.nio.file.Files.copy(screenshotFile.toPath(), destFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);

            System.out.println("📸 Screenshot saved: " + destFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to save screenshot file for " + testName + ": " + e.getMessage());
        }
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