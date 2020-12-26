package mctester;

import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import mctester.test.TestConfig;

import java.util.function.Consumer;

public class Templates {
    public static final Object2ReferenceOpenHashMap<String, Consumer<TestConfig>> TEST_TEMPLATES = new Object2ReferenceOpenHashMap<>();

    static {
        TEST_TEMPLATES.put("test_redstone", MyTests::simple_redstone_test);
    }
}
