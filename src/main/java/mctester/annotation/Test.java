package mctester.annotation;

import net.minecraft.util.BlockRotation;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Tests.class)
public @interface Test {
    boolean required() default true;
    String batchId() default "defaultbatch";
    String structureName();
    String groupName() default "";
    int cooldown() default 0;
    int timeout() default 400; //default 20 second timeout, test is counted as failed after this time
    BlockRotation rotation() default BlockRotation.NONE;
}
