package ltd.opens.mg.mc.core.blueprint.events;

import net.neoforged.bus.api.Event;
import net.neoforged.fml.event.IModBusEvent;

/**
 * 节点注册事件。
 * 模组应当监听此事件来注册蓝图节点。
 * 此事件在 Mod 事件总线 (modEventBus) 上发布，以便所有模组都能接收到。
 */
public class RegisterMGMCNodesEvent extends Event implements IModBusEvent {
    public RegisterMGMCNodesEvent() {
    }
}
