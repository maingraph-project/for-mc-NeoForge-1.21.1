package ltd.opens.mg.mc.core.blueprint.engine.handlers.collection;

import com.google.gson.JsonObject;
import ltd.opens.mg.mc.core.blueprint.engine.NodeContext;
import ltd.opens.mg.mc.core.blueprint.engine.NodeHandler;
import ltd.opens.mg.mc.core.blueprint.engine.NodeLogicRegistry;
import ltd.opens.mg.mc.core.blueprint.engine.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListSetItemHandler implements NodeHandler {
    @Override
    public Object getValue(JsonObject node, String pinId, NodeContext ctx) {
        if (pinId.equals("list_out")) {
            String listStr = TypeConverter.toString(NodeLogicRegistry.evaluateInput(node, "list_in", ctx));
            int index = TypeConverter.toInt(NodeLogicRegistry.evaluateInput(node, "index", ctx));
            String value = TypeConverter.toString(NodeLogicRegistry.evaluateInput(node, "value", ctx));
            
            if (listStr == null || listStr.isEmpty()) return value;
            
            try {
                String[] items = listStr.split("\\|");
                
                List<String> list = new ArrayList<>(Arrays.asList(items));
                if (index >= 0 && index < list.size()) {
                    list.set(index, value);
                } else if (index == list.size()) {
                    list.add(value);
                }
                return String.join("|", list);
            } catch (Exception e) {
                return listStr;
            }
        }
        return null;
    }
}




