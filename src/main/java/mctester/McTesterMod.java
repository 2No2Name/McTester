package mctester;

import mctester.annotation.TestRegistryHelper;
import mctester.common.util.StructureNBTConverter;
import mctester.templates.test_redstone_template;
import mctester.tests.BoxFill;
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

		if (McTesterConfig.shouldIncludeExampleTests()) {
			boolean tmp = TestRegistryHelper.shouldWarnOnMissingStructureFile;
			TestRegistryHelper.shouldWarnOnMissingStructureFile = false;
			TestRegistryHelper.createTestTemplateFromClass(test_redstone_template.class);
			TestRegistryHelper.createTestsFromClass(Example.class);
			TestRegistryHelper.createTestsFromClass(MobAi.class);
			TestRegistryHelper.createTestsFromClass(Minecarts.class);
			TestRegistryHelper.createTestsFromClass(BoxFill.class);
			TestRegistryHelper.shouldWarnOnMissingStructureFile = tmp;
		}

		TestRegistryHelper.createTemplatedTestsFromFiles();
	}
}
