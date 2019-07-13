package pw.xwy.skyblockutils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.item.ItemStack;

public class EnchantmentGUI extends Gui {

	private int width;
	private int height;

	public EnchantmentGUI(Minecraft mc) {
		ScaledResolution res = new ScaledResolution(mc);
		width = res.getScaledWidth();
		height = res.getScaledHeight();
	}

	public void draw(ItemStack stack) {

	}
}
