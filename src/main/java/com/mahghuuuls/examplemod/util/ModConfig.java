package com.mahghuuuls.examplemod.util;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Configuration;

@Config(modid = ModReference.MOD_ID)
public final class ModConfig {

	private static Configuration cfg;

	@Config.Comment("List of registry names of items that should only be held in the main or off hand")
	public static String[] restrictedItems = new String[] { "minecraft:diamond_sword" };

	private ModConfig() {
	}

}