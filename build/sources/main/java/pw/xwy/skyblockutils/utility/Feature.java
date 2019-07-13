package pw.xwy.skyblockutils.utility;

public enum Feature {
	ALARM(0, ButtonType.REGULAR),
	CHEST_COUNT(1, ButtonType.REGULAR),
	MINION_NAME_TAGS(2, ButtonType.REGULAR),
	FAIRY_SOUL_NAME_TAGS(3, ButtonType.REGULAR),
	HIDE_FOUND_FAIRY_SOULS(4, ButtonType.REGULAR),
	FULL_INVENTORY_WARNING(5, ButtonType.REGULAR),
	MANA_BAR(6, ButtonType.REGULAR),

	WARNING_TIME(-1, ButtonType.NEUTRAL),

	FAIRY_SOUL_PROFILE(-1, ButtonType.NEUTRAL),

	ADD(-1, ButtonType.MODIFY),
	SUBTRACT(-1, ButtonType.MODIFY),

	WARNING_COLOR(-1, ButtonType.COLOR),
	CONFIRMATION_COLOR(-1, ButtonType.COLOR),
	MANA_BAR_COLOR(-1, ButtonType.COLOR),
	MANA_TEXT_COLOR(-1, ButtonType.COLOR);

	private int id;
	private ButtonType buttonType;

	Feature(int id, ButtonType buttonType) {
		this.id = id;
		this.buttonType = buttonType;
	}

	public static Feature fromId(int id) {
		for (Feature feature : values()) {
			if (feature.getId() == id) {
				return feature;
			}
		}
		return null;
	}

	public ButtonType getButtonType() {
		return buttonType;
	}

	public int getId() {
		return id;
	}

	public enum ButtonType {
		REGULAR,
		COLOR,
		NEUTRAL,
		MODIFY,
		SOLID
	}

	public enum ManaBarType {
		BAR_TEXT("Bar & Text"),
		TEXT("Text"),
		BAR("Bar"),
		OFF("Off");

		private String displayText;

		ManaBarType(String displayText) {
			this.displayText = displayText;
		}

		public String getDisplayText() {
			return displayText;
		}

		public ManaBarType getNextType() {
			int nextType = ordinal() + 1;
			if (nextType > values().length - 1) {
				nextType = 0;
			}
			return values()[nextType];
		}
	}
}
