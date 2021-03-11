package mctester;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

public class McTesterConfig {
    public static final String crashOnFailOptionName = "crashOnFail";
    private static final Logger LOGGER = LogManager.getLogger("McTesterConfig");
    private static boolean shouldAutorun;
    //shuffling the test functions leads to the same test not always running in the same position and with the same neighboring tests
    //if a test breaks due to interaction with a neighboring tests, it or the neighboring test should be redesigned
    private static boolean shouldAutorunShuffle;
    private static boolean shouldCrashOnFail;
    private static boolean shouldShutdownAfterTest;
    private static boolean shouldStayUpAfterFail;
    private static long autorunShuffleSeed;
    private static boolean isDevelopment;

    static {
        LOGGER.info("Loading default config...");
        isDevelopment = false;
        shouldAutorun = true;
        shouldAutorunShuffle = true;
        autorunShuffleSeed = new Random().nextLong();
        shouldCrashOnFail = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
        shouldShutdownAfterTest = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
        shouldStayUpAfterFail = false;

        Properties properties = new Properties();
        File file = new File("mctester.properties");
        if (file.exists()) {
            try (FileInputStream in = new FileInputStream(file)) {
                LOGGER.info("Found configuration file, loading properties!");
                properties.load(in);
            } catch (IOException e) {
                e.printStackTrace();
                LOGGER.error("Error closing config file!");
            }

            String autostartProperty = properties.getProperty("autostart");
            if (autostartProperty != null) {
                shouldAutorun = Boolean.parseBoolean(autostartProperty);
            }
            String autostartShuffleProperty = properties.getProperty("autostart.shuffle");
            if (autostartShuffleProperty != null) {
                shouldAutorunShuffle = Boolean.parseBoolean(autostartShuffleProperty);
            }
            String autostartShuffleSeedProperty = properties.getProperty("autostart.shuffle.seed");
            if (autostartShuffleSeedProperty != null) {
                autorunShuffleSeed = Long.parseLong(autostartShuffleSeedProperty);
            }
            String crashOnFailProperty = properties.getProperty(crashOnFailOptionName);
            if (crashOnFailProperty != null) {
                shouldCrashOnFail = Boolean.parseBoolean(crashOnFailProperty);
            }
            String shutdownAfterTestProperty = properties.getProperty("shutdownAfterTest");
            if (shutdownAfterTestProperty != null) {
                shouldShutdownAfterTest = Boolean.parseBoolean(shutdownAfterTestProperty);
            }
            String stayUpAfterFailProperty = properties.getProperty("stayUpAfterFail");
            if (stayUpAfterFailProperty != null) {
                shouldStayUpAfterFail = Boolean.parseBoolean(stayUpAfterFailProperty);
            }
            String isDevelopmentProperty = properties.getProperty("isDevelopment");
            if (isDevelopmentProperty != null) {
                isDevelopment = Boolean.parseBoolean(isDevelopmentProperty);
            }
        }
    }

    public static boolean shouldAutorun() {
        return shouldAutorun;
    }

    public static boolean shouldShuffleBeforeAutorun() {
        return shouldAutorunShuffle;
    }

    public static long shuffleSeed() {
        return autorunShuffleSeed;
    }

    public static boolean shouldCrashOnFail() {
        return shouldCrashOnFail;
    }

    public static boolean shouldShutdownAfterTest() {
        return shouldShutdownAfterTest;
    }

    public static boolean shouldStayUpAfterFail() {
        return shouldStayUpAfterFail;
    }

    public static boolean isDevelopment() {
        return isDevelopment;
    }
}
