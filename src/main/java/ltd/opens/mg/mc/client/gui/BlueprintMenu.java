package ltd.opens.mg.mc.client.gui;

import ltd.opens.mg.mc.core.blueprint.NodeDefinition;
import ltd.opens.mg.mc.core.blueprint.NodeRegistry;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlueprintMenu {
    private String hoveredCategory = null;

    public void renderNodeContextMenu(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, double menuX, double menuY) {
        int x = (int) menuX;
        int y = (int) menuY;
        int width = 100;
        int height = 40;
        
        guiGraphics.fill(x, y, x + width, y + height, 0xEE1A1A1A);
        guiGraphics.renderOutline(x, y, width, height, 0xFFFFFFFF);
        
        // Delete Node
        boolean hoverDelete = mouseX >= x && mouseX <= x + width && mouseY >= y + 5 && mouseY <= y + 20;
        guiGraphics.drawString(font, Component.translatable("gui.mgmc.blueprint_editor.context_menu.delete"), x + 5, y + 7, hoverDelete ? 0xFFFFFF00 : 0xFFFFFFFF, false);
        
        // Break Links
        boolean hoverBreak = mouseX >= x && mouseX <= x + width && mouseY >= y + 20 && mouseY <= y + 35;
        guiGraphics.drawString(font, Component.translatable("gui.mgmc.blueprint_editor.context_menu.break_links"), x + 5, y + 22, hoverBreak ? 0xFFFFFF00 : 0xFFFFFFFF, false);
    }

    public void renderNodeMenu(GuiGraphics guiGraphics, Font font, int mouseX, int mouseY, double menuX, double menuY) {
        int x = (int) menuX;
        int y = (int) menuY;
        int width = 100;
        
        Map<String, List<NodeDefinition>> categories = new HashMap<>();
        for (NodeDefinition def : NodeRegistry.getAll()) {
            categories.computeIfAbsent(def.category(), k -> new ArrayList<>()).add(def);
        }
        
        List<String> sortedCategories = new ArrayList<>(categories.keySet());
        sortedCategories.sort((a, b) -> Component.translatable(a).getString().compareTo(Component.translatable(b).getString()));
        
        int height = sortedCategories.size() * 15 + 10;
        guiGraphics.fill(x, y, x + width, y + height, 0xEE1A1A1A);
        guiGraphics.renderOutline(x, y, width, height, 0xFFFFFFFF);
        
        String currentHoveredCat = null;
        for (int i = 0; i < sortedCategories.size(); i++) {
            String catKey = sortedCategories.get(i);
            int itemY = y + 5 + i * 15;
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= itemY && mouseY <= itemY + 15;
            int color = hovered ? 0xFFFFFF00 : 0xFFFFFFFF;
            guiGraphics.drawString(font, Component.translatable(catKey), x + 5, itemY + 2, color, false);
            
            if (hovered) {
                currentHoveredCat = catKey;
            }
        }
        
        if (currentHoveredCat != null) {
            hoveredCategory = currentHoveredCat;
        }
        
        if (hoveredCategory != null) {
            List<NodeDefinition> catNodes = categories.get(hoveredCategory);
            if (catNodes == null) {
                hoveredCategory = null;
                return;
            }
            int subX = x + width;
            int subY = y + sortedCategories.indexOf(hoveredCategory) * 15;
            int subWidth = 120;
            int subHeight = catNodes.size() * 15 + 10;
            
            guiGraphics.fill(subX, subY, subX + subWidth, subY + subHeight, 0xEE1A1A1A);
            guiGraphics.renderOutline(subX, subY, subWidth, subHeight, 0xFFFFFFFF);
            
            for (int i = 0; i < catNodes.size(); i++) {
                NodeDefinition def = catNodes.get(i);
                int itemY = subY + 5 + i * 15;
                boolean hovered = mouseX >= subX && mouseX <= subX + subWidth && mouseY >= itemY && mouseY <= itemY + 15;
                int color = hovered ? 0xFFFFFF00 : 0xFFFFFFFF;
                guiGraphics.drawString(font, Component.translatable(def.name()), subX + 5, itemY + 2, color, false);
            }
        }
    }

    public void reset() {
        hoveredCategory = null;
    }

    public enum ContextMenuResult {
        DELETE, BREAK_LINKS, NONE
    }

    public ContextMenuResult onClickContextMenu(double mouseX, double mouseY, double menuX, double menuY) {
        int x = (int) menuX;
        int y = (int) menuY;
        int width = 100;
        
        if (mouseX >= x && mouseX <= x + width && mouseY >= y + 5 && mouseY <= y + 20) {
            return ContextMenuResult.DELETE;
        }
        if (mouseX >= x && mouseX <= x + width && mouseY >= y + 20 && mouseY <= y + 35) {
            return ContextMenuResult.BREAK_LINKS;
        }
        return ContextMenuResult.NONE;
    }

    public NodeDefinition onClickNodeMenu(double mouseX, double mouseY, double menuX, double menuY) {
        Map<String, List<NodeDefinition>> categories = new HashMap<>();
        for (NodeDefinition def : NodeRegistry.getAll()) {
            categories.computeIfAbsent(def.category(), k -> new ArrayList<>()).add(def);
        }
        List<String> sortedCategories = new ArrayList<>(categories.keySet());
        sortedCategories.sort((a, b) -> Component.translatable(a).getString().compareTo(Component.translatable(b).getString()));

        int x = (int) menuX;
        int y = (int) menuY;
        int width = 100;

        if (hoveredCategory != null) {
            List<NodeDefinition> catNodes = categories.get(hoveredCategory);
            int subX = x + width;
            int subY = y + sortedCategories.indexOf(hoveredCategory) * 15;
            int subWidth = 120;

            for (int i = 0; i < catNodes.size(); i++) {
                int itemY = subY + 5 + i * 15;
                if (mouseX >= subX && mouseX <= subX + subWidth && mouseY >= itemY && mouseY <= itemY + 15) {
                    NodeDefinition def = catNodes.get(i);
                    hoveredCategory = null;
                    return def;
                }
            }
        }

        // Check if clicked a category to keep menu open
        for (int i = 0; i < sortedCategories.size(); i++) {
            int itemY = y + 5 + i * 15;
            if (mouseX >= x && mouseX <= x + width && mouseY >= itemY && mouseY <= itemY + 15) {
                return null; // Stay open
            }
        }
        
        // Clicked outside or on a category that doesn't trigger node creation
        // Return null but we need a way to say "close" if it's not a category
        return null; 
    }

    public boolean isClickInsideNodeMenu(double mouseX, double mouseY, double menuX, double menuY) {
        Map<String, List<NodeDefinition>> categories = new HashMap<>();
        for (NodeDefinition def : NodeRegistry.getAll()) {
            categories.computeIfAbsent(def.category(), k -> new ArrayList<>()).add(def);
        }
        List<String> sortedCategories = new ArrayList<>(categories.keySet());
        sortedCategories.sort((a, b) -> Component.translatable(a).getString().compareTo(Component.translatable(b).getString()));

        int x = (int) menuX;
        int y = (int) menuY;
        int width = 100;
        int height = sortedCategories.size() * 15 + 10;

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            return true;
        }

        if (hoveredCategory != null) {
            List<NodeDefinition> catNodes = categories.get(hoveredCategory);
            int subX = x + width;
            int subY = y + sortedCategories.indexOf(hoveredCategory) * 15;
            int subWidth = 120;
            int subHeight = catNodes.size() * 15 + 10;
            if (mouseX >= subX && mouseX <= subX + subWidth && mouseY >= subY && mouseY <= subY + subHeight) {
                return true;
            }
        }
        return false;
    }
}
