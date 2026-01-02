package ltd.opens.mg.mc.core.blueprint.engine;

import com.google.gson.JsonObject;
import net.minecraft.world.level.Level;
import java.util.Map;

public class NodeContext {
    public final Level level;
    public final String eventName;
    public final String[] args;
    public final String triggerUuid;
    public final String triggerName;
    public final double triggerX;
    public final double triggerY;
    public final double triggerZ;
    public final double triggerSpeed;
    public final Map<String, JsonObject> nodesMap;
    public boolean breakRequested = false;

    public NodeContext(Level level, String eventName, String[] args, String triggerUuid, String triggerName, 
                       double triggerX, double triggerY, double triggerZ, double triggerSpeed, Map<String, JsonObject> nodesMap) {
        this.level = level;
        this.eventName = eventName;
        this.args = args;
        this.triggerUuid = triggerUuid;
        this.triggerName = triggerName;
        this.triggerX = triggerX;
        this.triggerY = triggerY;
        this.triggerZ = triggerZ;
        this.triggerSpeed = triggerSpeed;
        this.nodesMap = nodesMap;
    }
}
