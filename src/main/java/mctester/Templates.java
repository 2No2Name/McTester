package mctester;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import mctester.test.TestConfig;

import java.util.function.Consumer;

public class Templates {
    public static final Object2ReferenceOpenHashMap<String, Consumer<TestConfig>> TEST_TEMPLATES = new Object2ReferenceOpenHashMap<>();

    static {
        //replaces red terracotta with redstone block as start and succeeds if noteblock on top of emerald block is powered
        TEST_TEMPLATES.put("test_redstone", ExampleTests::basic_redstone_test);
    }
}
