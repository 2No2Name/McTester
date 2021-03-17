package mctester;

import mctester.annotation.TestRegistryHelper;
import mctester.common.util.StructureNBTConverter;
import mctester.tests.Example;
import mctester.tests.Minecarts;
import mctester.tests.MobAi;
import net.fabricmc.api.ModInitializer;

public class McTesterMod implements ModInitializer {
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		StructureNBTConverter.convertAllNbtToSnbt();

		boolean tmp = TestRegistryHelper.shouldWarnOnMissingStructureFile;
		TestRegistryHelper.shouldWarnOnMissingStructureFile = false;
		TestRegistryHelper.createTestsFromClass(Example.class);
		TestRegistryHelper.shouldWarnOnMissingStructureFile = tmp;
		TestRegistryHelper.createTestsFromClass(MobAi.class);
		TestRegistryHelper.createTestsFromClass(Minecarts.class);

		TestRegistryHelper.createTemplatedTestsFromFiles();
	}
}
