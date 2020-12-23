package mctester.util;

import mctester.mixin.GameTestAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.test.GameTest;
import net.minecraft.test.StructureTestUtil;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.Box;

import java.util.List;

public class GameTestUtil {
    public static BlockBox getTestBlockBox(GameTest gameTest) {
        return StructureTestUtil.method_29410(((GameTestAccessor)gameTest).getStructureBlockBlockEntity());
    }

    public static Box getTestBox(GameTest gameTest) {
        return StructureTestUtil.getStructureBoundingBox(((GameTestAccessor)gameTest).getStructureBlockBlockEntity());
    }


    public static List<Entity> getEntitiesInTestArea(GameTest e) {
        return e.getWorld().getOtherEntities(null, GameTestUtil.getTestBox(e));
    }
}
