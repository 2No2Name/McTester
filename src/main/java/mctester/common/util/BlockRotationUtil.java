package mctester.common.util;

import net.minecraft.util.BlockRotation;
import org.jetbrains.annotations.NotNull;

public class BlockRotationUtil {
    private static final BlockRotation[] INVERSES = {BlockRotation.NONE, BlockRotation.COUNTERCLOCKWISE_90, BlockRotation.CLOCKWISE_180, BlockRotation.CLOCKWISE_90};

    static {
        BlockRotation[] blockRotations = BlockRotation.values();
        for (BlockRotation blockRotation : blockRotations) {
            BlockRotation inverse = INVERSES[blockRotation.ordinal()];
            if (blockRotation.rotate(inverse) != BlockRotation.NONE) {
                throw new AssertionError();
            }
        }
    }

    public static BlockRotation inverseOf(@NotNull BlockRotation blockRotation) {
        return INVERSES[blockRotation.ordinal()];
    }
}
