package mctester.annotation;

import java.lang.reflect.Method;

public class TestAnnotationHelper {
    public static String getGroupName(GameTest annotation, Method method) {
        String s = annotation.groupName();
        if (s.length() == 0) {
            s = method.getDeclaringClass().getSimpleName().toLowerCase();
        }
        return s;
    }

    public static String getStructureName(GameTest annotation, Method method) {
        String s = annotation.structureName();
        if (s.length() == 0) {
            s = method.getName().toLowerCase();
        }
        return s;
    }
}
