package ltd.opens.mg.mc;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import ltd.opens.mg.mc.core.blueprint.engine.BlueprintEngine;
import net.minecraft.commands.Commands;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.nio.file.Files;
import java.nio.file.Path;

import net.minecraft.commands.CommandSourceStack;

@Mod(MaingraphforMC.MODID)
public class MaingraphforMC {
    public static final String MODID = "mgmc";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MaingraphforMC(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(new BlueprintServerHandler());

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        LOGGER.info("Maingraph for MC initialized.");
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("mgrun")
            .then(Commands.argument("name", StringArgumentType.word())
                .then(Commands.argument("args", StringArgumentType.greedyString())
                    .executes(context -> {
                        String name = StringArgumentType.getString(context, "name");
                        String argsStr = StringArgumentType.getString(context, "args");
                        String[] args = argsStr.split("\\s+");
                        
                        CommandSourceStack source = context.getSource();
                        ServerLevel level = source.getLevel();
                        String triggerUuid = source.getEntity() != null ? source.getEntity().getUUID().toString() : "";
                        String triggerName = source.getTextName();
                        var pos = source.getPosition();
                        
                        try {
                            Path dataFile = level.getServer().getWorldPath(LevelResource.ROOT).resolve("blueprint_data.json");
                            if (Files.exists(dataFile)) {
                                String json = Files.readString(dataFile);
                                BlueprintEngine.execute(level, json, "on_mgrun", name, args, triggerUuid, triggerName, pos.x, pos.y, pos.z);
                            } else {
                                context.getSource().sendFailure(Component.literal("Blueprint data file not found on server: " + dataFile.toAbsolutePath()));
                            }
                        } catch (Exception e) {
                            context.getSource().sendFailure(Component.literal("Failed to execute blueprint on server: " + e.getMessage()));
                        }
                        return 1;
                    })
                )
                .executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    CommandSourceStack source = context.getSource();
                    ServerLevel level = source.getLevel();
                    String triggerUuid = source.getEntity() != null ? source.getEntity().getUUID().toString() : "";
                    String triggerName = source.getTextName();
                    var pos = source.getPosition();
                    try {
                        Path dataFile = level.getServer().getWorldPath(LevelResource.ROOT).resolve("blueprint_data.json");
                        if (Files.exists(dataFile)) {
                            String json = Files.readString(dataFile);
                            BlueprintEngine.execute(level, json, "on_mgrun", name, new String[0], triggerUuid, triggerName, pos.x, pos.y, pos.z);
                        } else {
                            context.getSource().sendFailure(Component.literal("Blueprint data file not found on server: " + dataFile.toAbsolutePath()));
                        }
                    } catch (Exception e) {
                        context.getSource().sendFailure(Component.literal("Failed to execute blueprint on server: " + e.getMessage()));
                    }
                    return 1;
                })
            )
        );
    }

    public static class BlueprintServerHandler {
        @SubscribeEvent
        public void onServerTick(ServerTickEvent.Post event) {
            // Player movement check on server
            var server = net.neoforged.neoforge.server.ServerLifecycleHooks.getCurrentServer();
            
            if (server == null) return;

            for (var player : server.getPlayerList().getPlayers()) {
                double x = player.getX();
                double y = player.getY();
                double z = player.getZ();

                try {
                    ServerLevel level = (ServerLevel) player.level();
                    Path dataFile = level.getServer().getWorldPath(LevelResource.ROOT).resolve("blueprint_data.json");
                    if (Files.exists(dataFile)) {
                        String json = Files.readString(dataFile);
                        BlueprintEngine.execute(level, json, "on_player_move", "", new String[0], 
                            player.getUUID().toString(), player.getName().getString(), x, y, z);
                    }
                } catch (Exception e) {
                    // Ignore
                }
            }
        }
    }
}
