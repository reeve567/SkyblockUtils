package pw.xwy.skyblockutils;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class KeyBinds {

	public KeyBinds() {
		for (Bind bind : Bind.values()) {
			ClientRegistry.registerKeyBinding(bind.keyBinding);
		}
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPress(InputEvent.KeyInputEvent e) {
		for (Bind bind : Bind.values()) {
			if (bind.keyBinding.isPressed()) {
				bind.handle();
			}
		}
	}

	private enum Bind implements KeyBind {
		SPRINT_TOGGLE(new KeyBinding("Sprint Toggle", Keyboard.KEY_F, Const.NAME)) {
			@Override
			public void handle() {
				SkyblockUtils.sprintToggle = !SkyblockUtils.sprintToggle;
			}
		},
		MOB_COUNTS_TOGGLE(new KeyBinding("Mob Count Toggle", Keyboard.KEY_J, Const.NAME)) {
			@Override
			public void handle() {
				SkyblockUtils.mobsToggle = !SkyblockUtils.mobsToggle;
			}
		};
		private KeyBinding keyBinding;

		Bind(KeyBinding keyBinding) {
			this.keyBinding = keyBinding;
		}
	}

	private interface KeyBind {
		void handle();
	}
}
