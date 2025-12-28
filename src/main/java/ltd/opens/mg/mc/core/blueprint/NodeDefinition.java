package ltd.opens.mg.mc.core.blueprint;

import java.util.ArrayList;
import java.util.List;

public record NodeDefinition(
    String id,
    String name,
    int color,
    List<PortDefinition> inputs,
    List<PortDefinition> outputs
) {
    public static class Builder {
        private final String id;
        private final String name;
        private int color = 0xFF444444;
        private final List<PortDefinition> inputs = new ArrayList<>();
        private final List<PortDefinition> outputs = new ArrayList<>();

        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder color(int color) {
            this.color = color;
            return this;
        }

        public Builder addInput(String name, PortType type, int color) {
            inputs.add(new PortDefinition(name, type, color));
            return this;
        }

        public Builder addOutput(String name, PortType type, int color) {
            outputs.add(new PortDefinition(name, type, color));
            return this;
        }

        public NodeDefinition build() {
            return new NodeDefinition(id, name, color, List.copyOf(inputs), List.copyOf(outputs));
        }
    }

    public record PortDefinition(String name, PortType type, int color) {}

    public enum PortType {
        EXEC, DATA
    }
}
