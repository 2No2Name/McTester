package mctester.annotation;

import net.minecraft.util.BlockRotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    boolean required() default true;
    String batchId() default "defaultbatch";
    String structureName();
//    String structurePath() default "defaultpath";
    int cooldown() default 10;
    int timeout() default 400;
    BlockRotation rotation() default BlockRotation.NONE;
}
