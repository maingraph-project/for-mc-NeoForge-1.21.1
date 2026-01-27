package ltd.opens.mg.mc.core.blueprint;

import ltd.opens.mg.mc.core.blueprint.events.RegisterMGMCNodesEvent;
import ltd.opens.mg.mc.core.blueprint.nodes.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;

/**
 * 蓝图节点统一初始化入口
 */
public class NodeInitializer {
    /**
     * 初始化所有节点（通过发布事件实现解耦）
     */
    public static void init(IEventBus modEventBus) {
        ltd.opens.mg.mc.MaingraphforMC.LOGGER.info("NodeInitializer: Initializing nodes on Mod Bus...");
        
        // 注册所有内置节点类到 Mod 事件总线
        modEventBus.register(MathNodes.class);
        ltd.opens.mg.mc.MaingraphforMC.LOGGER.info("NodeInitializer: Registered MathNodes to Mod Bus");
        modEventBus.register(LogicNodes.class);
        modEventBus.register(VariableNodes.class);
        modEventBus.register(ConversionNodes.class);
        modEventBus.register(ControlFlowNodes.class);
        modEventBus.register(StringNodes.class);
        modEventBus.register(ListNodes.class);
        modEventBus.register(ActionNodes.class);
        modEventBus.register(EventNodes.class);
        modEventBus.register(GetEntityInfoNode.class);
        modEventBus.register(SpecialNodes.class);

        // 发布注册事件，通知外部模块
        ltd.opens.mg.mc.MaingraphforMC.LOGGER.info("Posting RegisterMGMCNodesEvent to Mod Bus...");
        modEventBus.post(new RegisterMGMCNodesEvent());
        ltd.opens.mg.mc.MaingraphforMC.LOGGER.info("RegisterMGMCNodesEvent posted to Mod Bus.");
        
        // 冻结注册表，防止运行时动态修改
        NodeRegistry.freeze();
    }
}
