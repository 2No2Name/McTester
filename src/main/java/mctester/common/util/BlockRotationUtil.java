package mctester.common.util;

import net.minecraft.util.BlockRotation;
import org.jetbrains.annotations.NotNull;

public class BlockRotationUtil {
    private static final BlockRotation[] INVERSES = {BlockRotation.NONE, BlockRotation.COUNTERCLOCKWISE_90, BlockRotation.CLOCKWISE_180, BlockRotation.CLOCKWISE_90};

    static {
        for (BlockRotation blockRotation : BlockRotation.values()) {
            BlockRotation inverse = INVERSES[blockRotation.ordinal()];
            if (blockRotation.rotate(inverse) != BlockRotation.NONE) {
                throw new AssertionError();
            }
        }
    }

    public static BlockRotation inverseOf(@NotNull BlockRotation blockRotation) {
        return INVERSES[blockRotation.ordinal()];
    }

    public static boolean isCustom(int rotation) {
        return rotation < 0 || rotation >= BlockRotation.values().length;
    }

    public static BlockRotation[] getRotations(int rotations) {
        if (isCustom(rotations)) {
            //Only implemented all rotations at the same time for now.
            return BlockRotation.values();
        }
        return new BlockRotation[] {BlockRotation.values()[rotations]};
    }
}
