package pw.xwy.skyblockutils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@Mod(modid = "skyblockutils")
public class SkyblockUtils {

	public boolean sprintToggle;
	public boolean mobsToggle;
	private PlayerListener playerListener = new PlayerListener(this);
	private KeyBinding[] keyBindings;

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		System.out.println("SkyblockUtils loaded!");
		MinecraftForge.EVENT_BUS.register(playerListener);
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new MinionsCommand());

		keyBindings = new KeyBinding[1];
		keyBindings[0] = new KeyBinding("Sprint Toggle", Keyboard.KEY_F, "SkyblockUtils");
		keyBindings[1] = new KeyBinding("Mob Counts", Keyboard.KEY_J, "SkyblockUtils");
		ClientRegistry.registerKeyBinding(keyBindings[0]);
		ClientRegistry.registerKeyBinding(keyBindings[1]);
	}

	@SubscribeEvent
	public void onKeybind(InputEvent.KeyInputEvent e) {

		if (keyBindings[0].isPressed()) {
			sprintToggle = !sprintToggle;
		}

		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
			if (!Minecraft.getMinecraft().thePlayer.isSprinting() && sprintToggle) {
				Minecraft.getMinecraft().thePlayer.setSprinting(true);
			}
		}
	}

}
