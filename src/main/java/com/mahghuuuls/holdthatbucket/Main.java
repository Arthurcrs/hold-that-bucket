package com.mahghuuuls.holdthatbucket;

import java.util.HashSet;

import com.mahghuuuls.holdthatbucket.util.ModConfig;
import com.mahghuuuls.holdthatbucket.util.ModReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = ModReference.MOD_ID, name = ModReference.NAME, version = ModReference.VERSION)
@Mod.EventBusSubscriber(modid = ModReference.MOD_ID)
public class Main {
	private static HashSet<String> restrictedItemsSet = new HashSet<>(); // TODO: Change this name..

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		for (String itemId : ModConfig.restrictedItems) {
			restrictedItemsSet.add(itemId);
		}
	}

	/**
	 * Every player tick scan the player whole inventory for items in the restricted
	 * items list, if found the item, drop the stack
	 */
	@SubscribeEvent
	public static void checkInventoryForItems(TickEvent.PlayerTickEvent event) {
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
			ResourceLocation itemResourceLocation = stack.getItem().getRegistryName();
			if (itemResourceLocation == null) {
				continue;
			}

			if (restrictedItemsSet.contains(itemResourceLocation.toString())) {
				player.entityDropItem(stack.copy(), 0);
				player.inventory.setInventorySlotContents(i, ItemStack.EMPTY);
				// TODO: Add message to the player informing that the item must be held in one
				// hand
			}
		}
	}

	/**
	 * Ensures items in the list goes to the selected inventory slot or the off hand
	 * slot
	 */
	@SubscribeEvent
	public static void ensureHandSlotPickup(EntityItemPickupEvent event) {
		EntityPlayer player = event.getEntityPlayer();
		if (player.world.isRemote) {
			return;
		}

		ItemStack itemStack = event.getItem().getItem();
		ResourceLocation itemResourceLocation = itemStack.getItem().getRegistryName();
		if (itemResourceLocation == null) {
			return;
		}
		// TODO: When the player is picking up the item in that way, for some reason the
		// item pickup sound does not play
		if (restrictedItemsSet.contains(itemResourceLocation.toString())) {
			if (player.getHeldItemMainhand().isEmpty()) {
				player.setHeldItem(EnumHand.MAIN_HAND, itemStack);
				event.getItem().setDead();
				event.setCanceled(true);
			} else if (player.getHeldItemOffhand().isEmpty()) {
				player.setHeldItem(EnumHand.OFF_HAND, itemStack);
				event.getItem().setDead();
				event.setCanceled(true);
			} else {
				event.setCanceled(true);
			}
		}
	}
}