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
        // Colors from web editor
        int colorExec = 0xFFFFFFFF;
        int colorString = 0xFFDA00DA;
        int colorFloat = 0xFF36CF36;
        int colorBoolean = 0xFF920101;
        int colorObject = 0xFF00AAFF;
        int colorList = 0xFFFFCC00; // Yellow-ish for List
        int colorUUID = 0xFF55FF55; // Light Green for UUID
        int colorEnum = 0xFFFFAA00; // Orange for Enum

        // Register default nodes matching web editor
        // Events
        register(new NodeDefinition.Builder("on_mgrun", "On MGRUN")
            .category("Events")
            .color(0xFF880000)
            .addOutput("exec", "Out", NodeDefinition.PortType.EXEC, colorExec)
            .addOutput("name", "Name", NodeDefinition.PortType.STRING, colorString)
            .addOutput("parameters", "Parameters", NodeDefinition.PortType.LIST, colorList)
            .addOutput("trigger_uuid", "Trigger UUID", NodeDefinition.PortType.UUID, colorUUID)
            .addOutput("trigger_name", "Trigger Name", NodeDefinition.PortType.STRING, colorString)
            .addOutput("trigger_x", "Trigger X", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("trigger_y", "Trigger Y", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("trigger_z", "Trigger Z", NodeDefinition.PortType.FLOAT, colorFloat)
            .build());

        register(new NodeDefinition.Builder("on_player_move", "On Player Move")
            .category("Events")
            .color(0xFF0088FF)
            .addOutput("exec", "Out", NodeDefinition.PortType.EXEC, colorExec)
            .addOutput("x", "X", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("y", "Y", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("z", "Z", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("speed", "Speed", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("uuid", "UUID", NodeDefinition.PortType.UUID, colorUUID)
            .build());

        // Function
        register(new NodeDefinition.Builder("print_chat", "Print to Chat")
            .category("Function")
            .color(0xFF4488FF)
            .addInput("exec", "In", NodeDefinition.PortType.EXEC, colorExec)
            .addInput("message", "Message", NodeDefinition.PortType.STRING, colorString, true, "Hello Chat")
            .addOutput("exec", "Out", NodeDefinition.PortType.EXEC, colorExec)
            .build());

        register(new NodeDefinition.Builder("play_effect", "Play Effect")
            .category("Function")
            .color(0xFF4488FF)
            .addInput("exec", "In", NodeDefinition.PortType.EXEC, colorExec)
            .addInput("effect", "Effect", NodeDefinition.PortType.STRING, colorString, true, "minecraft:happy_villager")
            .addInput("x", "X", NodeDefinition.PortType.FLOAT, colorFloat, true, 0.0)
            .addInput("y", "Y", NodeDefinition.PortType.FLOAT, colorFloat, true, 0.0)
            .addInput("z", "Z", NodeDefinition.PortType.FLOAT, colorFloat, true, 0.0)
            .addOutput("exec", "Out", NodeDefinition.PortType.EXEC, colorExec)
            .build());

        register(new NodeDefinition.Builder("explosion", "Explosion")
            .category("Function")
            .color(0xFFFF4444)
            .addInput("exec", "In", NodeDefinition.PortType.EXEC, colorExec)
            .addInput("x", "X", NodeDefinition.PortType.FLOAT, colorFloat, true, 0.0)
            .addInput("y", "Y", NodeDefinition.PortType.FLOAT, colorFloat, true, 0.0)
            .addInput("z", "Z", NodeDefinition.PortType.FLOAT, colorFloat, true, 0.0)
            .addInput("radius", "Radius", NodeDefinition.PortType.FLOAT, colorFloat, true, 3.0)
            .addOutput("exec", "Out", NodeDefinition.PortType.EXEC, colorExec)
            .build());

        register(new NodeDefinition.Builder("get_list_item", "Get List Item")
            .category("Function")
            .color(0xFF44AA44)
            .addInput("list", "List", NodeDefinition.PortType.LIST, colorList)
            .addInput("index", "Index", NodeDefinition.PortType.FLOAT, colorFloat, true, 0)
            .addOutput("value", "Value", NodeDefinition.PortType.STRING, colorString)
            .build());

        register(new NodeDefinition.Builder("get_entity_info", "Get Entity Info")
            .category("Function")
            .color(0xFF44AA44)
            .addInput("uuid", "UUID", NodeDefinition.PortType.UUID, colorUUID)
            .addOutput("name", "Name", NodeDefinition.PortType.STRING, colorString)
            .addOutput("type", "Type", NodeDefinition.PortType.ENUM, colorEnum)
            .addOutput("registry_name", "Registry Name", NodeDefinition.PortType.STRING, colorString)
            .addOutput("pos_x", "X", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("pos_y", "Y", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("pos_z", "Z", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("health", "Health", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("max_health", "Max Health", NodeDefinition.PortType.FLOAT, colorFloat)
            .addOutput("is_living", "Is Living", NodeDefinition.PortType.BOOLEAN, colorBoolean)
            .addOutput("is_player", "Is Player", NodeDefinition.PortType.BOOLEAN, colorBoolean)
            .build());

        // Logic
        register(new NodeDefinition.Builder("branch", "Branch")
            .category("Logic")
            .color(0xFF888888)
            .addInput("exec", "In", NodeDefinition.PortType.EXEC, colorExec)
            .addInput("condition", "Condition", NodeDefinition.PortType.BOOLEAN, colorBoolean, true, true)
            .addOutput("true", "True", NodeDefinition.PortType.EXEC, colorExec)
            .addOutput("false", "False", NodeDefinition.PortType.EXEC, colorExec)
            .build());

        register(new NodeDefinition.Builder("cast", "Cast (Convert)")
            .category("Logic")
            .color(0xFF888888)
            .addInput("input", "Input", NodeDefinition.PortType.ANY, 0xFFAAAAAA)
            .addInput("to_type", "To Type", NodeDefinition.PortType.STRING, colorString, true, "STRING", 
                new String[]{"STRING", "FLOAT", "BOOLEAN", "UUID", "INT", "LIST"})
            .addOutput("output", "Output", NodeDefinition.PortType.ANY, 0xFFAAAAAA)
            .build());
    }
}
