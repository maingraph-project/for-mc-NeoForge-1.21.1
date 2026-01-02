package ltd.opens.mg.mc.client.gui;

import ltd.opens.mg.mc.core.blueprint.NodeDefinition;

public class BlueprintMenuHandler {
    private final BlueprintState state;

    public BlueprintMenuHandler(BlueprintState state) {
        this.state = state;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (state.showNodeContextMenu) {
            BlueprintMenu.ContextMenuResult result = state.menu.onClickContextMenu(mouseX, mouseY, state.menuX, state.menuY);
            if (result == BlueprintMenu.ContextMenuResult.DELETE) {
                if (state.contextMenuNode != null) {
                    if (state.focusedNode == state.contextMenuNode) {
                        state.focusedNode = null;
                        state.focusedPort = null;
                    }
                    final GuiNode finalNode = state.contextMenuNode;
                    state.nodes.remove(state.contextMenuNode);
                    state.connections.removeIf(c -> c.from == finalNode || c.to == finalNode);
                    state.markDirty();
                }
                state.showNodeContextMenu = false;
                state.contextMenuNode = null;
                return true;
            } else if (result == BlueprintMenu.ContextMenuResult.BREAK_LINKS) {
                if (state.contextMenuNode != null) {
                    final GuiNode finalNode = state.contextMenuNode;
                    state.connections.removeIf(c -> c.from == finalNode || c.to == finalNode);
                    state.markDirty();
                }
                state.showNodeContextMenu = false;
                state.contextMenuNode = null;
                return true;
            }
            
            state.showNodeContextMenu = false;
            state.contextMenuNode = null;
            return true;
        }

        if (state.showNodeMenu) {
            NodeDefinition def = state.menu.onClickNodeMenu(mouseX, mouseY, state.menuX, state.menuY);
            if (def != null) {
                float worldX = (float) ((state.menuX - state.panX) / state.zoom);
                float worldY = (float) ((state.menuY - state.panY) / state.zoom);
                GuiNode node = new GuiNode(def, worldX, worldY);
                state.nodes.add(node);
                state.markDirty();
                state.showNodeMenu = false;
                state.menu.reset();
                return true;
            }

            if (!state.menu.isClickInsideNodeMenu(mouseX, mouseY, state.menuX, state.menuY)) {
                state.showNodeMenu = false;
                state.menu.reset();
            }
            return true;
        }

        return false;
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 1) { // Right click
            double worldMouseX = (mouseX - state.panX) / state.zoom;
            double worldMouseY = (mouseY - state.panY) / state.zoom;
            
            for (int i = state.nodes.size() - 1; i >= 0; i--) {
                GuiNode node = state.nodes.get(i);
                if (worldMouseX >= node.x && worldMouseX <= node.x + node.width && worldMouseY >= node.y && worldMouseY <= node.y + node.height) {
                    state.showNodeContextMenu = true;
                    state.contextMenuNode = node;
                    state.menuX = mouseX;
                    state.menuY = mouseY;
                    return true;
                }
            }
            
            state.showNodeMenu = true;
            state.menuX = mouseX;
            state.menuY = mouseY;
            return true;
        }
        return false;
    }
}
