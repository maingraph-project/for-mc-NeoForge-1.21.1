package ltd.opens.mg.mc;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import ltd.opens.mg.mc.client.gui.BlueprintScreen;
import ltd.opens.mg.mc.client.gui.BlueprintWebServer;
import net.minecraft.network.chat.Component;
import static net.minecraft.commands.Commands.literal;

import java.awt.Desktop;
import java.net.URI;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.settings.KeyModifier;
import org.lwjgl.glfw.GLFW;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.common.NeoForge;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = MaingraphforMC.MODID, dist = Dist.CLIENT)
public class MaingraphforMCClient {
    public static final KeyMapping.Category MGMC_CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(MaingraphforMC.MODID, "main"));

    public static final KeyMapping BLUEPRINT_KEY = new KeyMapping(
        "key.mgmc.open_blueprint",
        KeyConflictContext.IN_GAME,
        KeyModifier.CONTROL,
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        MGMC_CATEGORY
    );

    public MaingraphforMCClient(ModContainer container, IEventBus modEventBus) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        modEventBus.addListener(this::onRegisterKeyMappings);
        modEventBus.addListener(this::onClientSetup);
        
        // Register for game events (ClientTickEvent, etc.)
        NeoForge.EVENT_BUS.register(this);
    }

    private void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(MGMC_CATEGORY);
        event.register(BLUEPRINT_KEY);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        while (BLUEPRINT_KEY.consumeClick()) {
            Minecraft.getInstance().setScreen(new BlueprintScreen());
        }
    }

    private void onClientSetup(FMLClientSetupEvent event) {
        MaingraphforMC.LOGGER.info("HELLO FROM CLIENT SETUP");
        
        BlueprintWebServer.setPlayerInfo(
            Minecraft.getInstance().getUser().getName(),
            Minecraft.getInstance().getUser().getProfileId().toString()
        );
        
        BlueprintWebServer.start();
    }

    @SubscribeEvent
    public void onRegisterClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(literal("mgmc")
            .then(literal("gui")
                .executes(context -> {
                        try {
                            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                                Desktop.getDesktop().browse(new URI(BlueprintWebServer.getUrl()));
                            } else {
                                context.getSource().sendFailure(Component.literal("Cannot open browser automatically. Please visit: " + BlueprintWebServer.getUrl()));
                            }
                        } catch (Exception e) {
                            context.getSource().sendFailure(Component.literal("Failed to open browser: " + e.getMessage()));
                        }
                        return 1;
                    })
            )
        );
    }
}
