package mctester.common.util;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.block.entity.StructureBlockBlockEntity;
import net.minecraft.test.TestFunction;
import net.minecraft.util.BlockRotation;
import org.slf4j.Logger;

import java.util.Optional;

public class TestFunctionIdentification {
    public static final Reference2IntOpenHashMap<TestFunction> testFunction2VariantIndex = new Reference2IntOpenHashMap<>();
    public static final Object2ReferenceOpenHashMap<String, TestFunction> identifier2TestFunction = new Object2ReferenceOpenHashMap<>();
    public static final Object2ReferenceOpenHashMap<TestFunction, String> testFunction2Identifier = new Object2ReferenceOpenHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String IDENTIFIER_PREFIX = "McTesterTestFunctionIdentifier[";
    private static final String IDENTIFIER_SUFFIX = "]";

    private static String getIdentifier(TestFunction testFunction, int variantIndex) {
        return IDENTIFIER_PREFIX + "structurePath=\"" + testFunction.getStructurePath() + "\",mcTesterVariant=\"" + variantIndex + "\"" + ",mcTesterRotation=\"" + testFunction.getRotation().ordinal() + "\"" + IDENTIFIER_SUFFIX;
    }

    public static void registerTestFunctionIdentifier(TestFunction testFunction, int variantIndex) {
        TestFunctionIdentification.testFunction2VariantIndex.put(testFunction, variantIndex);
        String identifier = getIdentifier(testFunction, variantIndex);
        TestFunctionIdentification.identifier2TestFunction.put(identifier, testFunction);
        TestFunctionIdentification.testFunction2Identifier.put(testFunction, identifier);
    }

    public static Optional<Integer> parseVariantIndex(String metadata) {
        String variantNumberPrefix = "?mcTesterVariant=\"";
        int i = metadata.indexOf(variantNumberPrefix);
        if (i == -1) {
            return Optional.empty();
        }
        i += variantNumberPrefix.length();
        metadata = metadata.substring(i);
        int end = metadata.indexOf("\"");
        metadata = metadata.substring(0, end);
        return Optional.of(Integer.parseInt(metadata));
    }

    public static Optional<BlockRotation> parseRotation(String metadata) {
        String variantNumberPrefix = "?mcTesterRotation=\"";
        int i = metadata.indexOf(variantNumberPrefix);
        if (i == -1) {
            return Optional.empty();
        }
        i += variantNumberPrefix.length();
        metadata = metadata.substring(i);
        int end = metadata.indexOf("\"");
        metadata = metadata.substring(0, end);
        int rotationOrdinal = Integer.parseInt(metadata);
        BlockRotation[] rotations = BlockRotation.values();
        return Optional.of(rotations[rotationOrdinal % rotations.length]);
    }

    public static void setMetaData(StructureBlockBlockEntity blockEntity, TestFunction testFunction) {
        String s = testFunction2Identifier.get(testFunction);
        if (s == null) {
            LOGGER.warn("Could not find identifier for test function of " + blockEntity.getStructurePath());
            return;
        }
        int variantIndex = testFunction2VariantIndex.getOrDefault(testFunction, 0);
        String identifier = getIdentifier(testFunction, variantIndex);
        String metadata = blockEntity.getMetadata();
        if (metadata.contains(IDENTIFIER_PREFIX)) {
            if (!metadata.contains(identifier)) {
                LOGGER.warn("GameTest test function identifier already present with a different value! Keeping old value!");
            }
        } else {
            metadata += identifier;
            blockEntity.setMetadata(metadata);
        }
    }

    public static String getTestFunctionIdentifierFromMetaData(StructureBlockBlockEntity blockEntity) {
        String metadata = blockEntity.getMetadata();
        int prefixPos = metadata.indexOf(IDENTIFIER_PREFIX);
        if (prefixPos == -1) {
            return null;
        }
        metadata = metadata.substring(prefixPos);
        int suffixPos = metadata.indexOf(IDENTIFIER_SUFFIX);
        if (suffixPos == -1) {
            return null;
        }
        metadata = metadata.substring(0, suffixPos + 1);
        return metadata;
    }
}
