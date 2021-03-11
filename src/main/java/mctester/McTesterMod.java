package mctester;

import mctester.annotation.TestRegistryHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Random;

public class McTesterMod implements ModInitializer {
	public static final String crashOnFailOptionName = "mctester.crashOnFail";

	private static boolean shouldAutorun;
	//shuffling the test functions leads to the same test not always running in the same position and with the same neighboring tests
	//if a test breaks due to interaction with a neighboring tests, it or the neighboring test should be redesigned
	private static boolean shouldAutorunShuffle;
	private static boolean shouldCrashOnFail;
	private static boolean shouldShutdownAfterTest;
	private static boolean shouldStayUpAfterFail;
	private static long autorunShuffleSeed;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		TestRegistryHelper.convertAllNbtToSnbt();

		boolean tmp = TestRegistryHelper.shouldWarnOnMissingStructureFile;
		TestRegistryHelper.shouldWarnOnMissingStructureFile = false;
		TestRegistryHelper.createTestsFromClass(ExampleTests.class);
		TestRegistryHelper.shouldWarnOnMissingStructureFile = tmp;

		TestRegistryHelper.createTemplatedTestsFromFiles();

		shouldAutorun = true;
		shouldAutorunShuffle = true;
		autorunShuffleSeed = new Random().nextLong();
		shouldCrashOnFail = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
		shouldShutdownAfterTest = FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER;
		shouldStayUpAfterFail = false;

		String autostartProperty = System.getProperty("mctester.autostart");
		if (autostartProperty != null) {
			shouldAutorun = Boolean.parseBoolean(autostartProperty);
		}
		String autostartShuffleProperty = System.getProperty("mctester.autostart.shuffle");
		if (autostartShuffleProperty != null) {
			shouldAutorunShuffle = Boolean.parseBoolean(autostartShuffleProperty);
		}
		String autostartShuffleSeedProperty = System.getProperty("mctester.autostart.shuffle.seed");
		if (autostartShuffleSeedProperty != null) {
			autorunShuffleSeed = Long.parseLong(autostartShuffleSeedProperty);
		}
		String crashOnFailProperty = System.getProperty(crashOnFailOptionName);
		if (crashOnFailProperty != null) {
			shouldCrashOnFail = Boolean.parseBoolean(crashOnFailProperty);
		}
		String shutdownAfterTestProperty = System.getProperty("mctester.shutdownAfterTest");
		if (shutdownAfterTestProperty != null) {
			shouldShutdownAfterTest = Boolean.parseBoolean(shutdownAfterTestProperty);
		}

		String stayUpAfterFailProperty = System.getProperty("mctester.stayUpAfterFail");
		if (stayUpAfterFailProperty != null) {
			shouldStayUpAfterFail = Boolean.parseBoolean(stayUpAfterFailProperty);
		}

		//Moved to SharedConstantsMixin
//		String isDevelopmentProperty = System.getProperty("mctester.isDevelopment");
//		if (isDevelopmentProperty != null) {
//			isDevelopment = Boolean.parseBoolean(isDevelopmentProperty);
//		}
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
}
