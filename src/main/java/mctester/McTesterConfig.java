package mctester;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

public class McTesterConfig {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean shouldAutorun;
    //shuffling the test functions leads to the same test not always running in the same position and with the same neighboring tests
    //if a test breaks due to interaction with a neighboring tests, it or the neighboring test should be redesigned
    private static boolean shouldAutorunShuffle;
    private static boolean shouldCrashOnFail;
    private static boolean shouldShutdownAfterTest;
    private static boolean shouldStayUpAfterFail;
    private static long autorunShuffleSeed;
    private static boolean isDevelopment;
    private static boolean includeExampleTests;

    static {
        LOGGER.info("Loading default config...");
        isDevelopment = false;
        shouldAutorun = true;
        shouldAutorunShuffle = true;
        autorunShuffleSeed = new Random().nextLong();
        shouldCrashOnFail = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
        shouldShutdownAfterTest = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
        shouldStayUpAfterFail = false;
        includeExampleTests = true;

        ArrayList<String> optionValues = new ArrayList<>();
        optionValues.add("false");
        optionValues.add("true");
        optionValues.add("serveronly");
        optionValues.add("clientonly");
        boolean[] optionValueLookup = {
                false,
                true,
                FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER,
                FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
        };

        Properties properties = new Properties();
        File file = new File("./config/mctester.properties");
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
                shouldAutorun = optionValueLookup[Math.max(0, optionValues.indexOf(autostartProperty))];
            }
            String autostartShuffleProperty = properties.getProperty("autostart.shuffle");
            if (autostartShuffleProperty != null) {
                shouldAutorunShuffle = optionValueLookup[Math.max(0, optionValues.indexOf(autostartShuffleProperty))];
            }
            String autostartShuffleSeedProperty = properties.getProperty("autostart.shuffle.seed");
            if (autostartShuffleSeedProperty != null) {
                autorunShuffleSeed = Long.parseLong(autostartShuffleSeedProperty);
            }
            String crashOnFailProperty = properties.getProperty("crashOnFail");
            if (crashOnFailProperty != null) {
                shouldCrashOnFail = optionValueLookup[Math.max(0, optionValues.indexOf(crashOnFailProperty))];
            }
            String shutdownAfterTestProperty = properties.getProperty("shutdownAfterTest");
            if (shutdownAfterTestProperty != null) {
                shouldShutdownAfterTest = optionValueLookup[Math.max(0, optionValues.indexOf(shutdownAfterTestProperty))];
            }
            String stayUpAfterFailProperty = properties.getProperty("stayUpAfterFail");
            if (stayUpAfterFailProperty != null) {
                shouldStayUpAfterFail = optionValueLookup[Math.max(0, optionValues.indexOf(stayUpAfterFailProperty))];
            }
            String isDevelopmentProperty = properties.getProperty("isDevelopment");
            if (isDevelopmentProperty != null) {
                isDevelopment = optionValueLookup[Math.max(0, optionValues.indexOf(isDevelopmentProperty))];
            }
            String includeExampleTestsProperty = properties.getProperty("includeExampleTests");
            if (includeExampleTestsProperty != null) {
                includeExampleTests = optionValueLookup[Math.max(0, optionValues.indexOf(includeExampleTestsProperty))];
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

    public static boolean shouldIncludeExampleTests() {
        return includeExampleTests;
    }
}
