package mctester.common.util;

import net.minecraft.data.dev.NbtProvider;
import net.minecraft.test.StructureTestUtil;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StructureNBTConverter {
    /**
     * Convenience method to allow the user to put normal structure block nbt files into the gameteststructures folder.
     */
    public static void convertAllNbtToSnbt() {
        String structuresDirectoryName = StructureTestUtil.testStructuresDirectoryName;
        File[] files = new File(structuresDirectoryName).listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file == null) {
                continue;
            }
            String fileName = file.getName();
            if (!file.isFile() || !file.canRead() || !fileName.endsWith(".nbt")) {
                continue;
            }
            String structureName = fileName.substring(0, fileName.length() - ".nbt".length());

            Path path = NbtProvider.convertNbtToSnbt(file.toPath(), structureName, Paths.get(structuresDirectoryName));
            if (path != null) {
                //delete nbt file after successfully converting to snbt
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }
}
