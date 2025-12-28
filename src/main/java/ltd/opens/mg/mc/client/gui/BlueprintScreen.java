package ltd.opens.mg.mc.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import ltd.opens.mg.mc.core.blueprint.NodeDefinition;
import ltd.opens.mg.mc.core.blueprint.NodeRegistry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

public class BlueprintScreen extends Screen {
    private float panX = 0;
    private float panY = 0;
    private float zoom = 1.0f;

    private final List<GuiNode> nodes = new ArrayList<>();
    private GuiNode draggingNode = null;
    private float dragOffsetX, dragOffsetY;

    private boolean isPanning = false;
    private double lastMouseX, lastMouseY;

    public BlueprintScreen() {
        super(Component.literal("Blueprint Editor"));
        
        // Initialize with some nodes from registry
        NodeDefinition onCalled = NodeRegistry.get("on_called");
        if (onCalled != null) {
            nodes.add(new GuiNode(onCalled, 100, 100));
        }

        NodeDefinition printString = NodeRegistry.get("print_string");
        if (printString != null) {
            nodes.add(new GuiNode(printString, 300, 150));
        }
        
        NodeDefinition addFloat = NodeRegistry.get("add_float");
        if (addFloat != null) {
            nodes.add(new GuiNode(addFloat, 200, 250));
        }
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // In 1.21.1, renderBackground might be called by the game or should be handled carefully
        // to avoid "Can only blur once per frame" exception.
        // We'll draw our grid and nodes first.
        
        // Draw grid (acts as background)
        drawGrid(guiGraphics);

        // Apply transform for nodes and connections
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(panX, panY);
        guiGraphics.pose().scale(zoom, zoom);

        // Draw connections (placeholder)
        drawConnections(guiGraphics);

        // Draw nodes
        for (GuiNode node : nodes) {
            node.render(guiGraphics, this.font, mouseX, mouseY, panX, panY, zoom);
        }

        guiGraphics.pose().popMatrix();

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawGrid(GuiGraphics guiGraphics) {
        int width = this.width;
        int height = this.height;
        float scaledGridSize = 20 * zoom;
        int color = 0xFF262626;
        int secondaryColor = 0xFF1A1A1A;

        // Fill background first
        guiGraphics.fill(0, 0, width, height, 0xFF121212);

        float startX = panX % scaledGridSize;
        if (startX > 0) startX -= scaledGridSize;
        float startY = panY % scaledGridSize;
        if (startY > 0) startY -= scaledGridSize;

        for (float x = startX; x < width; x += scaledGridSize) {
            guiGraphics.fill((int) x, 0, (int) x + 1, height, color);
        }
        for (float y = startY; y < height; y += scaledGridSize) {
            guiGraphics.fill(0, (int) y, width, (int) y + 1, color);
        }
        
        // Draw larger grid lines every 5 small grid squares
        float largeGridSize = scaledGridSize * 5;
        float largeStartX = panX % largeGridSize;
        if (largeStartX > 0) largeStartX -= largeGridSize;
        float largeStartY = panY % largeGridSize;
        if (largeStartY > 0) largeStartY -= largeGridSize;
        
        int largeColor = 0xFF333333;
        for (float x = largeStartX; x < width; x += largeGridSize) {
            guiGraphics.fill((int) x, 0, (int) x + 1, height, largeColor);
        }
        for (float y = largeStartY; y < height; y += largeGridSize) {
            guiGraphics.fill(0, (int) y, width, (int) y + 1, largeColor);
        }
    }

    private void drawConnections(GuiGraphics guiGraphics) {
        // We should ideally have a list of actual connections. 
        // For now, let's just connect the first output of the first node 
        // to the first input of the second node if they exist.
        if (nodes.size() >= 2) {
            GuiNode nodeA = nodes.get(0);
            GuiNode nodeB = nodes.get(1);

            if (!nodeA.outputs.isEmpty() && !nodeB.inputs.isEmpty()) {
                float[] outPos = nodeA.getPortPosition(0, false);
                float[] inPos = nodeB.getPortPosition(0, true);

                drawBezier(guiGraphics, outPos[0], outPos[1], inPos[0], inPos[1], 0xFFFFFFFF);
            }
        }
    }

    private void drawBezier(GuiGraphics guiGraphics, float x1, float y1, float x2, float y2, int color) {
        float dist = Math.max(Math.abs(x2 - x1) * 0.5f, 30);
        float cp1x = x1 + dist;
        float cp1y = y1;
        float cp2x = x2 - dist;
        float cp2y = y2;

        int segments = 30;
        float lastX = x1;
        float lastY = y1;

        for (int i = 1; i <= segments; i++) {
            float t = i / (float) segments;
            float invT = 1.0f - t;

            float x = invT * invT * invT * x1 + 3 * invT * invT * t * cp1x + 3 * invT * t * t * cp2x + t * t * t * x2;
            float y = invT * invT * invT * y1 + 3 * invT * invT * t * cp1y + 3 * invT * t * t * cp2y + t * t * t * y2;

            drawLine(guiGraphics, lastX, lastY, x, y, color);
            lastX = x;
            lastY = y;
        }
    }

    private void drawLine(GuiGraphics guiGraphics, float x1, float y1, float x2, float y2, int color) {
        // In 1.21.1, GuiGraphics doesn't have a direct line method that is easy to use.
        // We'll use small fill rectangles. Since we have many segments in drawBezier, 
        // this will look smooth enough.
        int ix1 = (int) x1;
        int iy1 = (int) y1;
        int ix2 = (int) x2;
        int iy2 = (int) y2;
        
        int minX = Math.min(ix1, ix2);
        int minY = Math.min(iy1, iy2);
        int maxX = Math.max(ix1, ix2);
        int maxY = Math.max(iy1, iy2);
        
        // Ensure at least 1 pixel size
        if (maxX == minX) maxX++;
        if (maxY == minY) maxY++;
        
        guiGraphics.fill(minX, minY, maxX, maxY, color);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean p_434187_) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();

        if (button == 2) { // Middle click for panning
            isPanning = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        
        // Transform mouse coordinates to world space
        double worldMouseX = (mouseX - panX) / zoom;
        double worldMouseY = (mouseY - panY) / zoom;

        if (button == 0) { // Left click
            // Check for node header click
            for (int i = nodes.size() - 1; i >= 0; i--) {
                GuiNode node = nodes.get(i);
                if (node.isMouseOverHeader(worldMouseX, worldMouseY)) {
                    draggingNode = node;
                    dragOffsetX = (float) (worldMouseX - node.x);
                    dragOffsetY = (float) (worldMouseY - node.y);
                    
                    // Bring to front
                    nodes.remove(i);
                    nodes.add(node);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, p_434187_);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        draggingNode = null;
        isPanning = false;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        double mouseX = event.x();
        double mouseY = event.y();

        if (draggingNode != null) {
            double worldMouseX = (mouseX - panX) / zoom;
            double worldMouseY = (mouseY - panY) / zoom;
            draggingNode.x = (float) (worldMouseX - dragOffsetX);
            draggingNode.y = (float) (worldMouseY - dragOffsetY);
            return true;
        } else if (isPanning) {
            panX += (float) dragX;
            panY += (float) dragY;
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        float oldZoom = zoom;
        float zoomSensitivity = 0.1f;
        if (scrollY > 0) {
            zoom *= (1 + zoomSensitivity);
        } else {
            zoom /= (1 + zoomSensitivity);
        }
        zoom = Math.max(0.1f, Math.min(3.0f, zoom));

        // Zoom towards mouse
        float worldMouseX = (float) ((mouseX - panX) / oldZoom);
        float worldMouseY = (float) ((mouseY - panY) / oldZoom);

        panX = (float) (mouseX - worldMouseX * zoom);
        panY = (float) (mouseY - worldMouseY * zoom);

        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    // Inner class for simple node representation
    private static class GuiNode {
        String title;
        float x, y;
        int color;
        float width = 120;
        float height = 60;
        float headerHeight = 15;

        List<NodePort> inputs = new ArrayList<>();
        List<NodePort> outputs = new ArrayList<>();

        public GuiNode(NodeDefinition def, float x, float y) {
            this.title = def.name();
            this.x = x;
            this.y = y;
            this.color = def.color();
            
            for (NodeDefinition.PortDefinition p : def.inputs()) {
                addInput(p.name(), p.type(), p.color());
            }
            for (NodeDefinition.PortDefinition p : def.outputs()) {
                addOutput(p.name(), p.type(), p.color());
            }
        }

        public void addInput(String name, NodeDefinition.PortType type, int color) {
            inputs.add(new NodePort(name, type, color, true));
            updateHeight();
        }

        public void addOutput(String name, NodeDefinition.PortType type, int color) {
            outputs.add(new NodePort(name, type, color, false));
            updateHeight();
        }

        private void updateHeight() {
            int maxPorts = Math.max(inputs.size(), outputs.size());
            this.height = Math.max(40, headerHeight + 10 + maxPorts * 15);
            // Auto-width based on text
            // For now keep it fixed or simple
        }

        public float[] getPortPosition(int index, boolean isInput) {
            float py = y + headerHeight + 10 + index * 15 + 3f; // Center of port
            float px = isInput ? x : x + width;
            return new float[]{px, py};
        }

        public void render(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, int mouseX, int mouseY, float panX, float panY, float zoom) {
            // Shadow
            guiGraphics.fill((int) x + 2, (int) y + 2, (int) (x + width + 2), (int) (y + height + 2), 0x88000000);
            
            // Background
            guiGraphics.fill((int) x, (int) y, (int) (x + width), (int) (y + height), 0xEE1A1A1A);
            
            // Border (highlighted if mouse is over)
            double worldMouseX = (mouseX - panX) / zoom;
            double worldMouseY = (mouseY - panY) / zoom;
            boolean isHovered = worldMouseX >= x && worldMouseX <= x + width && worldMouseY >= y && worldMouseY <= y + height;
            int borderColor = isHovered ? 0xFFFFFFFF : 0xFF333333;
            guiGraphics.renderOutline((int) x, (int) y, (int) width, (int) height, borderColor);
            
            // Header
            guiGraphics.fill((int) x + 1, (int) y + 1, (int) (x + width - 1), (int) (y + headerHeight), color);
            
            // Title
            guiGraphics.drawString(font, title, (int) x + 5, (int) y + 4, 0xFFFFFFFF, true);

            // Render Inputs
            for (int i = 0; i < inputs.size(); i++) {
                renderPort(guiGraphics, font, inputs.get(i), (int) x, (int) (y + headerHeight + 10 + i * 15), true);
            }

            // Render Outputs
            for (int i = 0; i < outputs.size(); i++) {
                renderPort(guiGraphics, font, outputs.get(i), (int) (x + width), (int) (y + headerHeight + 10 + i * 15), false);
            }
        }

        private void renderPort(GuiGraphics guiGraphics, net.minecraft.client.gui.Font font, NodePort port, int px, int py, boolean isInput) {
            int color = port.color;

            if (port.type == NodeDefinition.PortType.EXEC) {
                // Draw a more "arrow-like" shape for EXEC ports as in design
                if (isInput) {
                    // Triangle pointing right
                    guiGraphics.fill(px - 1, py, px + 1, py + 6, color);
                    guiGraphics.fill(px + 1, py + 1, px + 3, py + 5, color);
                    guiGraphics.fill(px + 3, py + 2, px + 5, py + 4, color);
                } else {
                    // Triangle pointing right
                    guiGraphics.fill(px - 5, py, px - 3, py + 6, color);
                    guiGraphics.fill(px - 3, py + 1, px - 1, py + 5, color);
                    guiGraphics.fill(px - 1, py + 2, px + 1, py + 4, color);
                }
            } else {
                // Circle/Square for DATA ports
                guiGraphics.fill(px - 3, py + 1, px + 3, py + 5, color);
                guiGraphics.fill(px - 2, py, px + 2, py + 6, color);
            }

            // Port Name
            if (isInput) {
                guiGraphics.drawString(font, port.name, px + 8, py - 1, 0xFFAAAAAA, false);
            } else {
                int textWidth = font.width(port.name);
                guiGraphics.drawString(font, port.name, px - 8 - textWidth, py - 1, 0xFFAAAAAA, false);
            }
        }

        public boolean isMouseOverHeader(double worldMouseX, double worldMouseY) {
            return worldMouseX >= x && worldMouseX <= x + width && worldMouseY >= y && worldMouseY <= y + headerHeight;
        }

        static class NodePort {
            String name;
            NodeDefinition.PortType type;
            int color;
            boolean isInput;

            public NodePort(String name, NodeDefinition.PortType type, int color, boolean isInput) {
                this.name = name;
                this.type = type;
                this.color = color;
                this.isInput = isInput;
            }
        }
    }
}
