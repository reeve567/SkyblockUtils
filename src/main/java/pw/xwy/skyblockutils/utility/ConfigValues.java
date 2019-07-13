package pw.xwy.skyblockutils.utility;

import com.google.gson.*;

import java.io.*;
import java.util.*;

public class ConfigValues {

	private File configFile;
	private JsonObject loadedConfig = new JsonObject();

	private Set<Feature> disabledFeatures = EnumSet.noneOf(Feature.class);
	private Map<Feature, ConfigColor> featureColors = new EnumMap<>(Feature.class);
	private Feature.ManaBarType manaBarType = Feature.ManaBarType.BAR_TEXT;
	private Map<Integer, List<String>> foundFairies = new HashMap<>();
	private int warningSeconds = 4;
	private int fairySoulProfile = 0;

	public ConfigValues(File configFile) {
		this.configFile = configFile;
	}

	public void loadConfig() {
		if (configFile.exists()) {
			try {
				FileReader reader = new FileReader(configFile);
				BufferedReader bufferedReader = new BufferedReader(reader);
				StringBuilder builder = new StringBuilder();
				String nextLine;
				while ((nextLine = bufferedReader.readLine()) != null) {
					builder.append(nextLine);
				}
				String complete = builder.toString();
				JsonElement fileElement = new JsonParser().parse(complete);
				if (fileElement == null || fileElement.isJsonNull()) {
					addDefaultsAndSave();
					return;
				}
				loadedConfig = fileElement.getAsJsonObject();
			} catch (JsonParseException | IllegalStateException | IOException ex) {
				ex.printStackTrace();
				System.out.println("SkyblockAddons: There was an error loading the config. Resetting to defaults.");
				addDefaultsAndSave();
				return;
			}
			for (JsonElement element : loadedConfig.getAsJsonArray("disabledFeatures")) {
				disabledFeatures.add(Feature.fromId(element.getAsInt()));
			}
			warningSeconds = loadedConfig.get("warningSeconds").getAsInt();
			if (loadedConfig.has("manaBarType")) {
				manaBarType = Feature.ManaBarType.values()[loadedConfig.get("manaBarType").getAsInt()];
			}
			if (loadedConfig.has("fairySoulProfile")) {
				fairySoulProfile = loadedConfig.get("fairySoulProfile").getAsInt();
			} else {
				loadedConfig.add("fairySoulProfile", new JsonPrimitive(fairySoulProfile));
			}
			if (loadedConfig.has("fairySouls")) {
				JsonObject fairySouls = loadedConfig.getAsJsonObject("fairySouls");
				if (fairySouls.has(String.valueOf(fairySoulProfile))) {
					List<String> strings = new ArrayList<>();
					for (JsonElement element : fairySouls.getAsJsonArray(String.valueOf(fairySoulProfile))) {
						strings.add(element.getAsString());
					}
					foundFairies.put(fairySoulProfile, strings);
				} else {
					foundFairies.put(fairySoulProfile, new ArrayList<>());
					fairySouls.add(String.valueOf(fairySoulProfile), new JsonArray());
				}
			} else {
				loadedConfig.add("fairySouls", new JsonObject());
			}
			if (loadedConfig.has("warningColor")) { // migrate from old config
				featureColors.put(Feature.WARNING_COLOR, ConfigColor.values()[loadedConfig.get("warningColor").getAsInt()]);
			} else {
				featureColors.put(Feature.WARNING_COLOR, ConfigColor.RED);
			}
			if (loadedConfig.has("confirmationColor")) { // migrate from old config
				featureColors.put(Feature.CONFIRMATION_COLOR, ConfigColor.values()[loadedConfig.get("confirmationColor").getAsInt()]);
			} else {
				featureColors.put(Feature.CONFIRMATION_COLOR, ConfigColor.RED);
			}
			if (loadedConfig.has("manaBarColor")) { // migrate from old config
				featureColors.put(Feature.MANA_BAR_COLOR, ConfigColor.values()[loadedConfig.get("manaBarColor").getAsInt()]);
			} else {
				featureColors.put(Feature.MANA_BAR_COLOR, ConfigColor.BLUE);
			}
			if (loadedConfig.has("manaBarTextColor")) { // migrate from old config
				featureColors.put(Feature.MANA_TEXT_COLOR, ConfigColor.values()[loadedConfig.get("manaBarTextColor").getAsInt()]);
			} else {
				featureColors.put(Feature.MANA_TEXT_COLOR, ConfigColor.BLUE);
			}
		} else {
			addDefaultsAndSave();
		}
	}

	private void addDefaultsAndSave() {
		featureColors.put(Feature.CONFIRMATION_COLOR, ConfigColor.RED);
		featureColors.put(Feature.WARNING_COLOR, ConfigColor.RED);
		featureColors.put(Feature.MANA_TEXT_COLOR, ConfigColor.BLUE);
		featureColors.put(Feature.MANA_BAR_COLOR, ConfigColor.BLUE);
		saveConfig();
	}

	@SuppressWarnings("ResultOfMethodCallIgnored")
	public void saveConfig() {
		loadedConfig = new JsonObject();
		try {
			configFile.createNewFile();
			FileWriter writer = new FileWriter(configFile);
			BufferedWriter bufferedWriter = new BufferedWriter(writer);

			JsonArray jsonArray = new JsonArray();
			for (Feature element : disabledFeatures) {
				jsonArray.add(new GsonBuilder().create().toJsonTree(element.getId()));
			}
			loadedConfig.add("disabledFeatures", jsonArray);
			loadedConfig.addProperty("warningColor", getColor(Feature.WARNING_COLOR).ordinal());
			loadedConfig.addProperty("confirmationColor", getColor(Feature.CONFIRMATION_COLOR).ordinal());
			loadedConfig.addProperty("manaBarColor", getColor(Feature.MANA_BAR_COLOR).ordinal());
			loadedConfig.addProperty("manaBarTextColor", getColor(Feature.MANA_TEXT_COLOR).ordinal());
			loadedConfig.addProperty("warningSeconds", warningSeconds);

			bufferedWriter.write(loadedConfig.toString());
			bufferedWriter.close();
			writer.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("An error occurred while attempting to save the config!");
		}
	}

	public Feature.ManaBarType getManaBarType() {
		return manaBarType;
	}

	public void setManaBarType(Feature.ManaBarType manaBarType) {
		this.manaBarType = manaBarType;
	}

	public void setColor(Feature feature, ConfigColor color) {
		featureColors.put(feature, color);
	}

	public ConfigColor getColor(Feature feature) {
		return featureColors.getOrDefault(feature, ConfigColor.RED);
	}

	public Set<Feature> getDisabledFeatures() {
		return disabledFeatures;
	}

	public Map<Integer, List<String>> getFoundFairies() {
		return foundFairies;
	}

	public int getWarningSeconds() {
		return warningSeconds;
	}

	public void setWarningSeconds(int warningSeconds) {
		this.warningSeconds = warningSeconds;
	}

}
