package pw.xwy.skyblockutils.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.item.ItemSkull;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinionsCommand extends CommandBase {
	@Override
	public String getCommandName() {
		return "minions";
	}

	@Override
	public String getCommandUsage(ICommandSender iCommandSender) {
		return null;
	}

	@Override
	public void processCommand(ICommandSender iCommandSender, String[] strings) {
		iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "Minions/Fairy Souls:"));
		int amount = 0;
		List<String> strings1 = new ArrayList<>();
		for (Entity entity : iCommandSender.getEntityWorld().loadedEntityList) {
			if (entity instanceof EntityArmorStand) {
				EntityArmorStand armorStand = (EntityArmorStand) entity;
				if (armorStand.getCurrentArmor(3) != null)
					if (armorStand.getCurrentArmor(3).getItem() instanceof ItemSkull) {
						amount++;
						strings1.add(EnumChatFormatting.DARK_AQUA + "X: " + EnumChatFormatting.WHITE + armorStand.getPosition().getX() + EnumChatFormatting.DARK_AQUA + " Y: " + EnumChatFormatting.WHITE + armorStand.getPosition().getY() + EnumChatFormatting.DARK_AQUA + " Z: " + EnumChatFormatting.WHITE + armorStand.getPosition().getZ() /*+ " ~ " + EnumChatFormatting.DARK_AQUA + "Head ID: " + armorStand.getCurrentArmor(3).getTagCompound().getString("SkullOwner")*/);
					}
			}
		}
		iCommandSender.addChatMessage(new ChatComponentText(EnumChatFormatting.DARK_AQUA + "Active: " + EnumChatFormatting.WHITE + amount));
		for (String s : strings1) {
			iCommandSender.addChatMessage(new ChatComponentText(s));
		}

	}

	@Override
	public int getRequiredPermissionLevel() {
		return 0;
	}

	@Override
	public List<String> getCommandAliases() {
		return Collections.singletonList("minion");
	}
}
