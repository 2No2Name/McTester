package mctester.common.copy;

import mctester.common.util.GameTestUtil;
import net.minecraft.structure.Structure;
import net.minecraft.test.GameTestException;
import net.minecraft.test.GameTestState;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

/**
 * This class is reimplementing {@link net.minecraft.test.PositionedException} because its constructor doesn't exist.
 */
public class PositionedException2 extends GameTestException {
    private final BlockPos pos;
    private final BlockPos relativePos;
    private final long testTick;

    public PositionedException2(String message, BlockPos absolutePos, GameTestState gameTest, long testTick) {
        super(message);
        this.pos = absolutePos;
        BlockPos blockPos = gameTest.getPos();
        this.relativePos = Structure.transformAround(absolutePos, BlockMirror.NONE, GameTestUtil.getInverse(gameTest.getRotation()), blockPos).add(-blockPos.getX(), -blockPos.getY(), -blockPos.getZ());
        this.testTick = testTick;
    }

    public PositionedException2(String message, GameTestState gameTest, BlockPos relativePos, long testTick) {
        super(message);
        BlockPos blockPos = gameTest.getPos();
        this.pos = Structure.transformAround(relativePos.add(blockPos.getX(), blockPos.getY(), blockPos.getZ()), BlockMirror.NONE, gameTest.getRotation(), blockPos);
        this.relativePos = relativePos;
        this.testTick = testTick;
    }

    public String getMessage() {
        String string = "" + this.pos.getX() + "," + this.pos.getY() + "," + this.pos.getZ() + " (relative: " + this.relativePos.getX() + "," + this.relativePos.getY() + "," + this.relativePos.getZ() + ")";
        return super.getMessage() + " at " + string + " (t=" + this.testTick + ")";
    }

    @Nullable
    public String getDebugMessage() {
        return super.getMessage() + " here";
    }

    @Nullable
    public BlockPos getPos() {
        return this.pos;
    }
}
