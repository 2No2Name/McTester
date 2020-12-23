package mctester.test;

import mctester.annotation.Test;
import mctester.annotation.TestRegistryHelper;
import mctester.util.GameTestUtil;
import net.minecraft.block.Blocks;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.fluid.Fluids;

public class MyTests {


    @Test(structureName = "cow_on_cactus")
    public static void cowOnCactus(TestConfig testConfig) {
        testConfig.addSuccessCondition(
                gameTest -> GameTestUtil.getEntitiesInTestArea(gameTest).stream().anyMatch(entity -> entity instanceof CowEntity && !entity.isAlive())
        );
    }

    public static void registerTests() {
        TestRegistryHelper.createTestsFromClass(MyTests.class);


        TestConfig testConfig = new TestConfig();
        //assign our structure to the test
        testConfig.structureName("waterflow1");
        //set a cooldown to allow firing observers etc. to stop before starting the test
        testConfig.structurePlaceCooldown(0);
        //fail after 5 seconds of no success
        testConfig.timeout(100);

        testConfig.addAction(new TestActions.SetBlockState(4, 3, 2, Blocks.WATER.getDefaultState()), 0);
        testConfig.addRepeatedAction(new TestActions.GetBlockState(2, 2, 2, (gameTest, blockState) -> {
            if (blockState.getFluidState().getFluid() == Fluids.FLOWING_WATER) {
                //fail(null) means success
                gameTest.fail(null);
            }
        }));

        TestHelper.registerTest(testConfig.build());

//
//        testBuilder = new TestBuilder();
//        testBuilder.structureName("wolfattackskeleton");
//        testBuilder.structurePath("mobai");
//        testBuilder.structurePlaceCooldown(0);
//        testBuilder.timeout(400);
//
//        testBuilder.addAction(new TestActions.GameTestAction() {
//            @Override
//            public void run(GameTest e) {
//
//            }
//        }, 0);


    }
}
