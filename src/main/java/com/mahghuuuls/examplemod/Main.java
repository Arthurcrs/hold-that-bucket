package com.mahghuuuls.examplemod;

import com.mahghuuuls.examplemod.util.ModReference;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ModReference.MOD_ID, name = ModReference.NAME, version = ModReference.VERSION)
public class Main {

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
	}

}