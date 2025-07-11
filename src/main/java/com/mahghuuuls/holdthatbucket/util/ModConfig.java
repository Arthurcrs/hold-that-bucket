package com.mahghuuuls.holdthatbucket.util;

import net.minecraftforge.common.config.Config;

@Config(modid = ModReference.MOD_ID)
public final class ModConfig {

	@Config.Comment("List of registry names of items that should only be held in the main or off hand")
	@Config.RequiresMcRestart
	public static String[] restrictedItems = new String[] { "minecraft:dirt" };

}