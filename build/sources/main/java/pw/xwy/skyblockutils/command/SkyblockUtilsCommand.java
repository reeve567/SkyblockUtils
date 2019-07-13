package pw.xwy.skyblockutils.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import pw.xwy.skyblockutils.listener.PlayerListener;

import java.util.Collections;
import java.util.List;

public class SkyblockUtilsCommand extends CommandBase {
	@Override
	public String getCommandName() {
		return "skyblockutils";
	}

	@Override
	public String getCommandUsage(ICommandSender iCommandSender) {
		return null;
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public void processCommand(ICommandSender iCommandSender, String[] strings) {
		PlayerListener.setGuiOpen(true);
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("sbu");
	}
}
