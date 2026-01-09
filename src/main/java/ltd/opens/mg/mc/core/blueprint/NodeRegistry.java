package ltd.opens.mg.mc.core.blueprint;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NodeRegistry {
    private static final Map<String, NodeDefinition> REGISTRY = new ConcurrentHashMap<>();

    public static void register(NodeDefinition definition) {
        REGISTRY.put(definition.id(), definition);
    }

    public static NodeDefinition get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<NodeDefinition> getAllDefinitions() {
        return REGISTRY.values();
    }

    static {
        NodeInitializer.init();
    }
}
