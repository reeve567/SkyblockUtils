package pw.xwy.skyblockutils;

import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = "skyblockutils")
public class SkyblockUtils {

	private PlayerListener playerListener = new PlayerListener(this);

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		System.out.println("SkyblockUtils loaded!");
		MinecraftForge.EVENT_BUS.register(playerListener);
		ClientCommandHandler.instance.registerCommand(new MinionsCommand());
	}

}
