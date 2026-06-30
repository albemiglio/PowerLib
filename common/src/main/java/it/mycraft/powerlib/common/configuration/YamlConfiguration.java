package it.mycraft.powerlib.common.configuration;

import com.google.common.base.Charsets;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.*;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Original fragments from bungeecord
 *
 * @author md_5
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public class YamlConfiguration extends ConfigurationProvider {

    private Yaml newYaml() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        YamlRepresenter representer = new YamlRepresenter(options);
        return new Yaml(new Constructor(new LoaderOptions()), representer, options);
    }

    @Override
    public void save(Configuration config, File file) throws IOException {
        try (Writer writer = new OutputStreamWriter(Files.newOutputStream(file.toPath()), Charsets.UTF_8)) {
            save(config, writer);
        }
    }

    @Override
    public void save(Configuration config, Writer writer) {
        newYaml().dump(config.self, writer);
    }

    @Override
    public Configuration load(File file) throws IOException {
        return load(file, null);
    }

    @Override
    public Configuration load(File file, Configuration defaults) throws IOException {
        try (FileInputStream is = new FileInputStream(file)) {
            return load(is, defaults);
        }
    }

    @Override
    public Configuration load(Reader reader) {
        return load(reader, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(Reader reader, Configuration defaults) {
        Map<String, Object> map = newYaml().loadAs(reader, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    @Override
    public Configuration load(InputStream is) {
        return load(is, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(InputStream is, Configuration defaults) {
        Map<String, Object> map = newYaml().loadAs(is, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }

    @Override
    public Configuration load(String string) {
        return load(string, null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Configuration load(String string, Configuration defaults) {
        Map<String, Object> map = newYaml().loadAs(string, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<>();
        }
        return new Configuration(map, defaults);
    }
}
