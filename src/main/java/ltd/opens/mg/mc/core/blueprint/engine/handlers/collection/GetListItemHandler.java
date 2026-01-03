package ltd.opens.mg.mc.core.blueprint.engine.handlers.collection;

import com.google.gson.JsonObject;
import ltd.opens.mg.mc.core.blueprint.engine.NodeContext;
import ltd.opens.mg.mc.core.blueprint.engine.NodeHandler;
import ltd.opens.mg.mc.core.blueprint.engine.NodeLogicRegistry;
import ltd.opens.mg.mc.core.blueprint.engine.TypeConverter;

public class GetListItemHandler implements NodeHandler {
    @Override
    public Object getValue(JsonObject node, String pinId, NodeContext ctx) {
        if (pinId.equals("value")) {
            try {
                String listStr = TypeConverter.toString(NodeLogicRegistry.evaluateInput(node, "list", ctx));
                int index = TypeConverter.toInt(NodeLogicRegistry.evaluateInput(node, "index", ctx));
                
                if (listStr == null || listStr.isEmpty()) return null;
                
                String[] items = listStr.split("\\|");
                
                if (index >= 0 && index < items.length) {
                    return items[index];
                }
            } catch (Exception e) {}
        }
        return null;
    }
}




