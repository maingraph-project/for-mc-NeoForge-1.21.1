package ltd.opens.mg.mc.core.blueprint.engine.handlers.collection;

import com.google.gson.JsonObject;
import ltd.opens.mg.mc.core.blueprint.engine.NodeContext;
import ltd.opens.mg.mc.core.blueprint.engine.NodeHandler;
import ltd.opens.mg.mc.core.blueprint.engine.NodeLogicRegistry;
import ltd.opens.mg.mc.core.blueprint.engine.TypeConverter;

public class ListJoinHandler implements NodeHandler {
    @Override
    public Object getValue(JsonObject node, String pinId, NodeContext ctx) {
        if (pinId.equals("string")) {
            String listStr = TypeConverter.toString(NodeLogicRegistry.evaluateInput(node, "list", ctx));
            String delim = TypeConverter.toString(NodeLogicRegistry.evaluateInput(node, "delimiter", ctx));
            
            if (listStr == null || listStr.isEmpty()) return null;
            if (delim == null) delim = "";
            
            String[] items = listStr.split("\\|");
            return String.join(delim, items);
        }
        return null;
    }
}




