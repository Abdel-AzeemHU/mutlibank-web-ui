package utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class VideoRecorder {

    private static Process ffmpegProcess;
    private static String currentVideoPath;
    private static final String VIDEO_BASE_PATH = "./reports/videos";
    private static boolean isRecording = false;
    private static String currentTestName;

    /**
     * Start recording using FFmpeg with Xvfb virtual display
     */
    public static void startRecording(String methodName) {
        try {
            // Store the test name
            currentTestName = methodName;

            // Check if FFmpeg is available
            if (!isFFmpegAvailable()) {
                System.out.println("⚠️ FFmpeg not available - video recording disabled");
                return;
            }

            // Check if we're in pipeline environment
            if (!isPipelineEnvironment()) {
                System.out.println("📍 Not in pipeline environment - video recording skipped");
                return;
            }

            // Create video directory with timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            File videoFolder = new File(VIDEO_BASE_PATH + "/" + timestamp);

            if (!videoFolder.exists()) {
                videoFolder.mkdirs();
                System.out.println("📁 Created video directory: " + videoFolder.getAbsolutePath());
            }

            // Generate video file path
            currentVideoPath = videoFolder.getAbsolutePath() + "/" + methodName + "_" + timestamp + ".mp4";

            // Build FFmpeg command for headless recording
            String[] command = buildFFmpegCommand(currentVideoPath);

            // Start FFmpeg process
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            ffmpegProcess = pb.start();

            isRecording = true;
            System.out.println("🎥 Video recording started for: " + methodName);
            System.out.println("📹 Recording to: " + currentVideoPath);

            // Give FFmpeg time to initialize
            Thread.sleep(2000);

        } catch (Exception e) {
            System.err.println("❌ Failed to start video recording: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Stop the FFmpeg recording process
     */
    public static void stopRecording() {
        if (!isRecording || ffmpegProcess == null) {
            return;
        }

        try {
            System.out.println("⏹️ Stopping video recording for: " + currentTestName);

            // Send 'q' to FFmpeg to stop gracefully
            if (ffmpegProcess.isAlive()) {
                ffmpegProcess.getOutputStream().write("q\n".getBytes());
                ffmpegProcess.getOutputStream().flush();

                // Wait for process to terminate
                boolean terminated = ffmpegProcess.waitFor(5, TimeUnit.SECONDS);

                if (!terminated) {
                    // Force kill if not terminated gracefully
                    ffmpegProcess.destroyForcibly();
                    ffmpegProcess.waitFor(2, TimeUnit.SECONDS);
                }
            }

            isRecording = false;
            System.out.println("🛑 Video recording stopped");

            if (currentVideoPath != null) {
                File videoFile = new File(currentVideoPath);
                if (videoFile.exists() && videoFile.length() > 0) {
                    System.out.println("💾 Video saved: " + currentVideoPath);
                    System.out.println("📊 Video size: " + formatFileSize(videoFile.length()));
                } else if (videoFile.exists()) {
                    System.out.println("⚠️ Video file exists but is empty, deleting...");
                    videoFile.delete();
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Failed to stop video recording: " + e.getMessage());
            e.printStackTrace();
        } finally {
            ffmpegProcess = null;
        }
    }

    /**
     * Delete the recording (called when test passes)
     */
    public static void deleteRecording() {
        if (currentVideoPath == null) {
            return;
        }

        try {
            File videoFile = new File(currentVideoPath);
            if (videoFile.exists()) {
                long fileSize = videoFile.length();
                boolean deleted = videoFile.delete();
                if (deleted) {
                    System.out.println("🗑️ Video deleted (test passed): " + videoFile.getName() + " (" + formatFileSize(fileSize) + ")");
                }

                // Clean up empty directories
                File parentDir = videoFile.getParentFile();
                if (parentDir != null && parentDir.list() != null && parentDir.list().length == 0) {
                    boolean dirDeleted = parentDir.delete();
                    if (dirDeleted) {
                        System.out.println("📁 Cleaned up empty directory: " + parentDir.getName());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Failed to delete video recording: " + e.getMessage());
        } finally {
            currentVideoPath = null;
            currentTestName = null;
        }
    }

    /**
     * Build FFmpeg command for Xvfb recording
     */
    private static String[] buildFFmpegCommand(String outputPath) {
        String display = System.getenv("DISPLAY");
        if (display == null || display.isEmpty()) {
            display = ":99"; // Default Xvfb display
        }

        // Optimized FFmpeg command for pipeline recording
        return new String[] {
                "ffmpeg",
                "-f", "x11grab",           // Input format for X11 screen capture
                "-video_size", "1920x1080", // Screen resolution
                "-framerate", "10",         // Lower framerate for smaller files
                "-i", display,              // Input display
                "-codec:v", "libx264",      // H.264 codec
                "-preset", "ultrafast",     // Fastest encoding
                "-pix_fmt", "yuv420p",      // Pixel format
                "-crf", "35",               // Higher CRF for smaller files (0-51, higher = more compression)
                "-y",                       // Overwrite output file
                outputPath
        };
    }

    /**
     * Check if FFmpeg is available in the system
     */
    private static boolean isFFmpegAvailable() {
        try {
            Process process = new ProcessBuilder("ffmpeg", "-version")
                    .redirectErrorStream(true)
                    .start();
            int exitCode = process.waitFor(2, TimeUnit.SECONDS) ? process.exitValue() : -1;
            boolean available = exitCode == 0;
            if (available) {
                System.out.println("✅ FFmpeg is available");
            }
            return available;
        } catch (Exception e) {
            System.out.println("❌ FFmpeg check failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if running in pipeline environment
     */
    private static boolean isPipelineEnvironment() {
        // Check for common CI/CD environment variables
        return System.getenv("CI") != null ||
                System.getenv("AZURE_PIPELINES") != null ||
                System.getenv("BUILD_ID") != null ||
                System.getenv("AGENT_ID") != null ||
                "true".equals(System.getProperty("pipeline.mode"));
    }

    /**
     * Format file size for readable output
     */
    private static String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }

    /**
     * Get the current video file path
     */
    public static String getVideoPath() {
        return currentVideoPath;
    }

    /**
     * Check if currently recording
     */
    public static boolean isRecording() {
        return isRecording;
    }

    /**
     * Force cleanup of any running FFmpeg processes
     */
    public static void forceCleanup() {
        if (ffmpegProcess != null && ffmpegProcess.isAlive()) {
            try {
                ffmpegProcess.destroyForcibly();
                System.out.println("🔧 Force cleaned up FFmpeg process");
            } catch (Exception e) {
                System.err.println("Failed to force cleanup: " + e.getMessage());
            }
        }
        ffmpegProcess = null;
        isRecording = false;
        currentVideoPath = null;
        currentTestName = null;
    }
}