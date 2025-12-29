package ltd.opens.mg.mc.core.blueprint.engine.handlers.impl;

import com.google.gson.JsonObject;
import ltd.opens.mg.mc.core.blueprint.engine.NodeContext;
import ltd.opens.mg.mc.core.blueprint.engine.NodeHandler;
import ltd.opens.mg.mc.core.blueprint.engine.NodeLogicRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;

public class PlayEffectHandler implements NodeHandler {
    @Override
    public void execute(JsonObject node, NodeContext ctx) {
        String effectName = NodeLogicRegistry.evaluateInput(node, "effect", ctx);
        String xStr = NodeLogicRegistry.evaluateInput(node, "x", ctx);
        String yStr = NodeLogicRegistry.evaluateInput(node, "y", ctx);
        String zStr = NodeLogicRegistry.evaluateInput(node, "z", ctx);

        try {
            double x = xStr.isEmpty() ? 0 : Double.parseDouble(xStr);
            double y = yStr.isEmpty() ? 0 : Double.parseDouble(yStr);
            double z = zStr.isEmpty() ? 0 : Double.parseDouble(zStr);

            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                Identifier id = effectName.contains(":") ? 
                    Identifier.fromNamespaceAndPath(effectName.split(":")[0], effectName.split(":")[1]) : 
                    Identifier.fromNamespaceAndPath("minecraft", effectName);
                
                var particleTypeOptional = BuiltInRegistries.PARTICLE_TYPE.get(id);
                if (particleTypeOptional != null && particleTypeOptional.isPresent()) {
                    ParticleType<?> type = particleTypeOptional.get().value();
                    if (type instanceof ParticleOptions options) {
                        for (int i = 0; i < 5; i++) {
                            mc.level.addParticle(options, 
                                x + (mc.level.random.nextDouble() - 0.5) * 0.5, 
                                y + mc.level.random.nextDouble() * 0.5, 
                                z + (mc.level.random.nextDouble() - 0.5) * 0.5, 
                                0, 0, 0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Ignore errors
        }

        NodeLogicRegistry.triggerExec(node, "exec", ctx);
    }
}
