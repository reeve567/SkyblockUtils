package pw.xwy.skyblockutils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PlayerListener {
	//TODO: FIX EVERYTHING FOR MULTIPLAYER
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '\u00A7' + "[0-9A-FK-OR]");
	private final int tickInterval = 150;
	private SkyblockUtils main;
	private long tick = 0;
	private boolean onIsland;
	private boolean onSkyblock;
	private boolean chestOpen = false;
	private boolean hasGrapplingHook = false;
	private HashMap<String, Integer> counts = new HashMap<>();
	private List<Warnings> warnings = new ArrayList<>();
	private Map<Mob, Integer> mobCount = new HashMap<>();
	private Map<Class<? extends EntityLiving>, Integer> mobCount2 = new HashMap<>();

	public PlayerListener(SkyblockUtils main) {
		this.main = main;
	}

	public static void drawStringAtHUDPosition(String string, HUDPositions position, FontRenderer fontRenderer, int xOffset, int yOffset, int lineOffset) {
		Minecraft mc = Minecraft.getMinecraft();
		ScaledResolution res = new ScaledResolution(mc);

		int screenWidth = res.getScaledWidth();
		screenWidth /= 1;
		int screenHeight = res.getScaledHeight();
		screenHeight /= 1;

		switch (position) {
			case TOP_LEFT:
				yOffset += lineOffset * 9;
				drawStringLeft(string, fontRenderer, 2 + xOffset, 2 + yOffset, 0, true);
				break;
			case TOP_CENTER:
				yOffset += lineOffset * 9;
				drawStringCenter(string, fontRenderer, screenWidth / 2 + xOffset, 2 + yOffset, 0, true);
				break;
			case TOP_RIGHT:
				yOffset += lineOffset * 9;
				drawStringRight(string, fontRenderer, screenWidth - 2 + xOffset, 2 + yOffset, 0, true);
				break;
			case LEFT:
				yOffset += lineOffset * 9;
				drawStringLeft(string, fontRenderer, 2 + xOffset, screenHeight / 2 + yOffset, 0, true);
				break;
			case RIGHT:
				yOffset += lineOffset * 9;
				drawStringRight(string, fontRenderer, screenWidth - 2 + xOffset, screenHeight / 2 + yOffset, 0, true);
				break;
			case BOTTOM_LEFT:
				yOffset -= lineOffset * 9;
				drawStringLeft(string, fontRenderer, 2 + xOffset, screenHeight - 9 + yOffset, 0, true);
				break;
			case BOTTOM_RIGHT:
				yOffset -= lineOffset * 9;
				drawStringRight(string, fontRenderer, screenWidth - 2 + xOffset, screenHeight - 9 + yOffset, 0, true);
		}
	}

	public static void drawStringCenter(String string, FontRenderer fontRenderer, int x, int y, int color, boolean shadow) {
		fontRenderer.drawString(string, x - fontRenderer.getStringWidth(string) / 2, y, color, shadow);
	}

	public static void drawStringLeft(String string, FontRenderer fontRenderer, int x, int y, int color, boolean shadow) {
		fontRenderer.drawString(string, x, y, color, shadow);
	}

	public static void drawStringRight(String string, FontRenderer fontRenderer, int x, int y, int color, boolean shadow) {
		fontRenderer.drawString(string, x - fontRenderer.getStringWidth(string), y, color, shadow);
	}

	private static String stripColor(final String input) {
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}

	private void updateWarnings(boolean... values) {
		warnings = new ArrayList<>();

		if (values.length >= 1) {
			if (!values[0]) warnings.add(Warnings.GRAPPLING_HOOK);
		} else {
			warnings.add(Warnings.GRAPPLING_HOOK);
		}
	}

	public void checkIfOnSkyblockAndIsland() { // Most of this is replicated from the scoreboard rendering code so not many comments here xD
		Minecraft mc = Minecraft.getMinecraft();
		if (mc != null && mc.theWorld != null) {
			Scoreboard scoreboard = mc.theWorld.getScoreboard();
			ScoreObjective scoreobjective = null;
			ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(mc.thePlayer.getName());
			if (scoreplayerteam != null) {
				int randomNumber = scoreplayerteam.getChatFormat().getColorIndex();
				if (randomNumber >= 0) {
					scoreobjective = scoreboard.getObjectiveInDisplaySlot(3 + randomNumber);
				}
			}
			ScoreObjective scoreobjective1 = scoreobjective != null ? scoreobjective : scoreboard.getObjectiveInDisplaySlot(1);
			if (scoreobjective1 != null) {
				String scoreboardTitle = scoreobjective1.getDisplayName();
				onSkyblock = stripColor(scoreboardTitle).startsWith("SKYBLOCK");
				Collection<Score> collection = scoreboard.getSortedScores(scoreobjective1);
				List<Score> list = Lists.newArrayList(collection.stream().filter(score -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")).collect(Collectors.toList()));
				if (list.size() > 15) {
					collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
				} else {
					collection = list;
				}
				for (Score score1 : collection) {
					ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
					String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
					if (s1.equals(" \u00A77\u23E3 \u00A7aYour Isla\uD83C\uDFC0\u00A7and")) {
						onIsland = true;
						return;
					}
				}
			}
		}
		onIsland = false;
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Pre e) {
		checkIfOnSkyblockAndIsland();
		if (onSkyblock) {
			GuiIngameForge.renderArmor = false;
			GuiIngameForge.renderFood = false;
		}
	}

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent event) {
		if (!(event.entityPlayer.openContainer instanceof ContainerChest)) {
			chestOpen = false;
		}
		if (!chestOpen)
			if (event.entityPlayer.openContainer instanceof ContainerChest) {
				int amount = 0;
				ContainerChest chest = (ContainerChest) event.entityPlayer.openContainer;
				for (ItemStack itemStack : chest.getInventory()) {
					if (itemStack != null)
						if (itemStack.getDisplayName().equalsIgnoreCase(event.itemStack.getDisplayName()))
							amount += itemStack.stackSize;
				}
				event.toolTip.add("Amount: " + amount);
			} else {
				event.toolTip.add("Amount: " + counts.getOrDefault(event.itemStack.getDisplayName(), 0));
			}
	}

	@SubscribeEvent
	public void onRender(RenderGameOverlayEvent.Text event) {
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fontRenderer = mc.ingameGUI.getFontRenderer();
		int i = 0;

		if (onSkyblock)
			drawStringAtHUDPosition(EnumChatFormatting.AQUA + "Skyblock", HUDPositions.TOP_LEFT, fontRenderer, 0, 0, i++);

		if (main.sprintToggle) {
			drawStringAtHUDPosition(EnumChatFormatting.GREEN + "Sprint Toggle", HUDPositions.TOP_LEFT, fontRenderer, 0, 0, i++);
		}

		if (!onSkyblock) return;

		String debug = "?";
		drawStringAtHUDPosition(EnumChatFormatting.GOLD + "Debug: " + debug, HUDPositions.TOP_LEFT, fontRenderer, 0, 0, i++);

		if (!warnings.isEmpty()) {
			drawStringAtHUDPosition(EnumChatFormatting.RED + "Warnings: ", HUDPositions.TOP_LEFT, fontRenderer, 0, 0, i++);

			for (Warnings w : this.warnings) {
				drawStringAtHUDPosition(EnumChatFormatting.RED + "  " + w.toString(), HUDPositions.TOP_LEFT, fontRenderer, 0, 0, i++);
			}
		}
	}

	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
		if (Minecraft.getMinecraft() == null) return;
		if (Minecraft.getMinecraft().theWorld == null) return;
		Minecraft mc = Minecraft.getMinecraft();

		tick++;
		if (tick % tickInterval == 0) {
			if (mc.thePlayer == null) return;
			checkEntities();
			countInventory(mc.thePlayer);
			updateWarnings(hasGrapplingHook);
		}
	}

	private void checkEntities() {
		mobCount = new HashMap<>();
		for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
			setArmorstandTag(entity);
			mobCount(entity);
		}
	}

	private void mobCount(Entity entity) {
		if (main.mobsToggle) {
			if (entity instanceof EntityLiving) {
				for (Mob m : Mob.values()) {
					Class<? extends EntityLiving> clazz = m.getClazz();
					if (entity.getClass().isAssignableFrom(clazz)) {
						mobCount.put(m, mobCount.getOrDefault(m, 0) + 1);
					}
				}
			}
		}
	}

	private void setArmorstandTag(Entity entity) {
		if (!onSkyblock) return;

		if (entity instanceof EntityArmorStand) {
			EntityArmorStand armorStand = (EntityArmorStand) entity;
			if (armorStand.getCurrentArmor(3) != null && armorStand.getCurrentArmor(3).getItem() instanceof ItemSkull) {
				armorStand.setAlwaysRenderNameTag(true);
				if (armorStand.getCurrentArmor(2) != null)
					armorStand.setCustomNameTag(EnumChatFormatting.GOLD + "Minion");
				else
					armorStand.setCustomNameTag(EnumChatFormatting.LIGHT_PURPLE + "Fairy Soul");
			}
		}
	}

	private void countInventory(EntityPlayer player, ItemStack... items) {
		if (player == null) return;
		if (player.inventory == null) return;
		//if (!onSkyblock) return;
		counts = new HashMap<>();
		hasGrapplingHook = false;
		for (ItemStack itemStack : player.inventory.mainInventory) {
			if (itemStack != null) {
				if (stripColor(itemStack.getDisplayName()).equalsIgnoreCase("Grappling Hook") && itemStack.getItem() instanceof ItemFishingRod) {
					hasGrapplingHook = true;
				}
				counts.put(itemStack.getDisplayName(), counts.getOrDefault(itemStack.getDisplayName(), 0) + itemStack.stackSize);
			}
		}
		for (ItemStack itemStack : items) {
			sendTitle(player, itemStack.getDisplayName());
			if (stripColor(itemStack.getDisplayName()).equalsIgnoreCase("Grappling Hook") && itemStack.getItem() instanceof ItemFishingRod) {
				hasGrapplingHook = true;
			}
			counts.put(itemStack.getDisplayName(), counts.getOrDefault(itemStack.getDisplayName(), 0) + itemStack.stackSize);
		}
	}

	private boolean playerCheck(Entity entity) {
		if (Minecraft.getMinecraft() == null || Minecraft.getMinecraft().theWorld == null || Minecraft.getMinecraft().thePlayer == null)
			return false;
		if (Minecraft.getMinecraft().isSingleplayer())
			return entity.getUniqueID().equals(Minecraft.getMinecraft().thePlayer.getUniqueID());
		else {
			EntityPlayerMP playerMP = getMultiplayer(entity);
			if (playerMP != null) {
				return entity.getUniqueID().equals(playerMP.getUniqueID());
			}
			return false;
		}
	}

	private void sendMessage(String text) {
		Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(text));
	}

	private void sendTitle(EntityPlayer player, String text) {
		if (!isMultiplayer()) return;
		S45PacketTitle packetTitle = new S45PacketTitle(S45PacketTitle.Type.TITLE, new ChatComponentText(text));
		EntityPlayerMP playerMP = getMultiplayer(player);
		if (playerMP != null) {
			playerMP.playerNetServerHandler.sendPacket(packetTitle);
		}
	}

	private EntityPlayerMP getMultiplayer(Entity player) {
		if (player instanceof EntityPlayerMP) {
			return (EntityPlayerMP) player;
		}
		return null;
	}

	private boolean isMultiplayer() {
		return Minecraft.getMinecraft().theWorld.isRemote;
	}

	public enum Warnings {
		GRAPPLING_HOOK("No Grappling Hook");
		private String display;

		Warnings(String s) {
			this.display = s;
		}

		@Override
		public String toString() {
			return display;
		}
	}

	public enum Mob {
		SPIDER(EntitySpider.class, "Spiders"),
		MAGMA_CUBE(EntityMagmaCube.class, "Magma Cubes"),
		SLIME(EntitySlime.class, "Slimes"),
		ZOMBIE(EntityZombie.class, "Zombies"),
		SKELETON(EntitySkeleton.class, "Skeletons"),
		ENDERMAN(EntityEnderman.class, "Endermen"),
		CREEPER(EntityCreeper.class, "Creepers"),
		COW(EntityCow.class, "Cows"),
		SHEEP(EntitySheep.class, "Sheep"),
		CHICKEN(EntityChicken.class, "Chickens"),
		PIG(EntityPig.class, "Pigs"),
		BLAZE(EntityBlaze.class, "Blazes"),
		GHAST(EntityGhast.class, "Ghasts");
		private Class<? extends EntityLiving> clazz;
		private String display;

		Mob(Class<? extends EntityLiving> clazz, String display) {
			this.clazz = clazz;
			this.display = display;
		}

		public Class<? extends EntityLiving> getClazz() {
			return clazz;
		}

		public String getDisplay() {
			return display;
		}
	}

	public enum HUDPositions {
		TOP_LEFT,
		TOP_CENTER,
		TOP_RIGHT,
		LEFT,
		RIGHT,
		BOTTOM_LEFT,
		BOTTOM_RIGHT
	}
}
