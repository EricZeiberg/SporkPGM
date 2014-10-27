package io.sporkpgm.team.spawns.kits;

public enum KitArmorSlot {

	HELMET(),
	CHESTPLATE(),
	LEGGINGS(),
	BOOTS();

	public static KitArmorSlot getSlot(String string) {
		KitArmorSlot match = null;
		for(KitArmorSlot slot : KitArmorSlot.values()) {
			if(slot.name().equalsIgnoreCase(string)) {
				match = slot;
			}
		}
		return match;
	}

}
