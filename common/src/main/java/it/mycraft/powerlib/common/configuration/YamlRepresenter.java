package it.mycraft.powerlib.common.configuration;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.introspector.PropertyUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SnakeYAML representer that serializes a {@link Configuration} by dumping its backing map.
 */
public class YamlRepresenter extends Representer {

    /**
     * @param options the SnakeYAML dumper options
     */
    public YamlRepresenter(DumperOptions options) {
        super(options);
        representers.put(Configuration.class, data -> represent(((Configuration) data).self));
    }

    @Override
    public TypeDescription addTypeDescription(TypeDescription td) {
        return super.addTypeDescription(td);
    }

    @Override
    public void setPropertyUtils(PropertyUtils propertyUtils) {
        super.setPropertyUtils(propertyUtils);
    }

    @Override
    protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
        return super.representJavaBean(properties, javaBean);
    }

    @Override
    protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
        return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
    }

    @Override
    protected void checkGlobalTag(Property property, Node node, Object object) {
        super.checkGlobalTag(property, node, object);
    }

    @Override
    protected Set<Property> getProperties(Class<?> type) {
        return super.getProperties(type);
    }
}
