package it.mycraft.powerlib.common.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfigManagerTest {

    @Test
    void createCopiesDefaultFromJarThenLoadsIt(@TempDir Path tmp) throws Exception {
        File dataFolder = new File(tmp.toFile(), "data");
        File jar = new File(tmp.toFile(), "plugin.jar");
        writeJar(jar, "config.yml", "name: PowerLib\nversion: 2\n");

        ConfigManager cm = new ConfigManager(dataFolder, jar);
        Configuration cfg = cm.create("config.yml");

        assertNotNull(cfg, "create() must return the loaded configuration");
        assertEquals("PowerLib", cfg.getString("name"));
        assertEquals(2, cfg.getInt("version"));
        assertTrue(new File(dataFolder, "config.yml").exists(),
                "the default must be copied from the jar into the data folder");
        assertSame(cfg, cm.get("config.yml"), "get() must return the cached config");
    }

    private static void writeJar(File jar, String entry, String content) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(jar))) {
            zos.putNextEntry(new ZipEntry(entry));
            zos.write(content.getBytes());
            zos.closeEntry();
        }
    }
}
