package ltd.opens.mg.mc.core.blueprint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NodeRegistry {
    private static final Map<String, NodeDefinition> REGISTRY = new HashMap<>();

    public static void register(NodeDefinition definition) {
        REGISTRY.put(definition.id(), definition);
    }

    public static NodeDefinition get(String id) {
        return REGISTRY.get(id);
    }

    public static Collection<NodeDefinition> getAll() {
        return REGISTRY.values();
    }

    static {
        // Register some default nodes
        register(new NodeDefinition.Builder("on_called", "On Called")
            .color(0xFF880000)
            .addOutput("Out", NodeDefinition.PortType.EXEC, 0xFFFFFFFF)
            .build());

        // Print String Node
        register(new NodeDefinition.Builder("print_string", "Print String")
            .color(0xFF4488FF)
            .addInput("exec", NodeDefinition.PortType.EXEC, 0xFFFFFFFF)
            .addInput("in_string", NodeDefinition.PortType.DATA, 0xFF00FF00)
            .addOutput("exec", NodeDefinition.PortType.EXEC, 0xFFFFFFFF)
            .build());

        // Player Health Node
        register(new NodeDefinition.Builder("player_health", "Player Health")
            .color(0xFFFF4444)
            .addOutput("value", NodeDefinition.PortType.DATA, 0xFF00FF00)
            .build());

        // Add (Float) Node
        register(new NodeDefinition.Builder("add_float", "Add (Float)")
            .color(0xFF44AA44)
            .addInput("a", NodeDefinition.PortType.DATA, 0xFF00FF00)
            .addInput("b", NodeDefinition.PortType.DATA, 0xFF00FF00)
            .addOutput("result", NodeDefinition.PortType.DATA, 0xFF00FF00)
            .build());

        // Branch Node
        register(new NodeDefinition.Builder("branch", "Branch")
            .color(0xFF888888)
            .addInput("exec", NodeDefinition.PortType.EXEC, 0xFFFFFFFF)
            .addInput("condition", NodeDefinition.PortType.DATA, 0xFF00FF00)
            .addOutput("true", NodeDefinition.PortType.EXEC, 0xFFFFFFFF)
            .addOutput("false", NodeDefinition.PortType.EXEC, 0xFFFFFFFF)
            .build());

        // Make Color Node
        register(new NodeDefinition.Builder("make_color", "Make Color")
            .color(0xFF4444AA)
            .addInput("base", NodeDefinition.PortType.DATA, 0xFF00FF00)
            .addOutput("color", NodeDefinition.PortType.DATA, 0xFF00FF00)
            .build());
    }
}
