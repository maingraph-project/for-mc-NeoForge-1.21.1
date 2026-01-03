package ltd.opens.mg.mc.core.blueprint.engine.handlers.collection;

import com.google.gson.JsonObject;
import ltd.opens.mg.mc.core.blueprint.engine.NodeContext;
import ltd.opens.mg.mc.core.blueprint.engine.NodeHandler;
import ltd.opens.mg.mc.core.blueprint.engine.NodeLogicRegistry;
import ltd.opens.mg.mc.core.blueprint.engine.TypeConverter;

public class ListAddHandler implements NodeHandler {
    @Override
    public Object getValue(JsonObject node, String pinId, NodeContext ctx) {
        if (pinId.equals("list_out")) {
            String listStr = TypeConverter.toString(NodeLogicRegistry.evaluateInput(node, "list_in", ctx));
            String item = TypeConverter.toString(NodeLogicRegistry.evaluateInput(node, "item", ctx));
            
            if (item == null) item = "";
            if (listStr == null || listStr.isEmpty()) {
                return item;
            }
            
            if (item.contains("|")) {
                item = "\"" + item + "\""; 
            }
            return listStr + "|" + item;
        }
        return null;
    }
}




