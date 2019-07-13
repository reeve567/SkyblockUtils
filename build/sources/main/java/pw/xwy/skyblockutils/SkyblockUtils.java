package pw.xwy.skyblockutils;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import pw.xwy.skyblockutils.command.MinionsCommand;
import pw.xwy.skyblockutils.command.SkyblockUtilsCommand;
import pw.xwy.skyblockutils.utility.ConfigValues;
import pw.xwy.skyblockutils.listener.PlayerListener;

@Mod(modid = Const.MODID, clientSideOnly = true, acceptedMinecraftVersions = "[1.8.9]")
public class SkyblockUtils {

	public static boolean isAlarmEnabled = true;
	public static float alarmPitch = 0.1F;
	public static float alarmVolume = 0.1F;
	public static int alarmFrequency = 5;
	public static boolean sprintToggle;
	public static boolean mobsToggle;
	private PlayerListener playerListener = new PlayerListener(this);
	private ConfigValues configValues;

	@Mod.EventHandler
	public void init(FMLPreInitializationEvent e) {
		//CONFIG
		this.configValues = new ConfigValues(e.getSuggestedConfigurationFile());
	}

	public ConfigValues getConfigValues() {
		return configValues;
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		new KeyBinds();

		MinecraftForge.EVENT_BUS.register(playerListener);
		MinecraftForge.EVENT_BUS.register(this);
		ClientCommandHandler.instance.registerCommand(new MinionsCommand());
		ClientCommandHandler.instance.registerCommand(new SkyblockUtilsCommand());
		configValues.loadConfig();
	}

	@SubscribeEvent
	public void onKeybind(InputEvent.KeyInputEvent e) {
		if (Minecraft.getMinecraft().gameSettings.keyBindForward.isKeyDown()) {
			if (!Minecraft.getMinecraft().thePlayer.isSprinting() && sprintToggle) {
				Minecraft.getMinecraft().thePlayer.setSprinting(true);
			}
		}
	}

}
