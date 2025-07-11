package com.mahghuuuls.examplemod;

import com.mahghuuuls.examplemod.util.ModConfig;
import com.mahghuuuls.examplemod.util.ModReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = ModReference.MOD_ID, name = ModReference.NAME, version = ModReference.VERSION)
public class Main {

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) {
			return;
		}

		EntityPlayer player = event.player;
		int mainHandSlot = player.inventory.currentItem;

		for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
			if (i == mainHandSlot || i == 40) {
				continue; // allow main hand and off hand
			}
			ItemStack stack = player.inventory.getStackInSlot(i);
			if (stack.isEmpty()) {
				continue;
			}
			ResourceLocation name = stack.getItem().getRegistryName();
			if (name == null) {
				continue;
			}
			for (String id : ModConfig.restrictedItems) { // TODO: THIS COULD BE A HASHMAP SEARCH
				if (name.toString().equals(id)) {
					player.entityDropItem(stack.copy(), 0);
					player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
					break;
				}
			}
		}
	}

}