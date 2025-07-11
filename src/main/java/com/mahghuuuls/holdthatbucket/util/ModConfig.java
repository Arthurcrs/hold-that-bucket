package com.mahghuuuls.holdthatbucket.util;

import net.minecraftforge.common.config.Config;

@Config(modid = ModReference.MOD_ID)
public final class ModConfig {

	@Config.Comment("Allow the items to be held on the main hand")
	@Config.RequiresMcRestart
	public static boolean allowMainHand = true;

	@Config.Comment("Allow the items to be held in the off hand")
	@Config.RequiresMcRestart
	public static boolean allowOffHand = true;

	@Config.Comment("Display a message when the item is dropped from the player inventory")
	@Config.RequiresMcRestart
	public static boolean displayMessageOnDrop = true;

	@Config.Comment("Display a message when the item is picked up")
	@Config.RequiresMcRestart
	public static boolean displayMessageOnPickup = false;

	@Config.Comment("List of registry names of items that this mod affects")
	@Config.RequiresMcRestart
	public static String[] restrictedItems = new String[] { "minecraft:water_bucket", "minecraft:lava_bucket" };
}