package com.mahghuuuls.holdthatbucket;

import java.util.HashSet;

import com.mahghuuuls.holdthatbucket.util.ModConfig;
import com.mahghuuuls.holdthatbucket.util.ModReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = ModReference.MOD_ID, name = ModReference.NAME, version = ModReference.VERSION)
@Mod.EventBusSubscriber(modid = ModReference.MOD_ID)
public class Main {
	private static HashSet<String> restrictedItemsSet = new HashSet<>(); // TODO: Change this name..Also. Check if it is
																			// really necessary, maybe the ModConfig can
																			// initialize as a HashSet
	private static String warningMessage;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		for (String itemId : ModConfig.restrictedItems) {
			restrictedItemsSet.add(itemId);
		}

		if (ModConfig.displayMessage) {
			if (ModConfig.allowMainHand && ModConfig.allowOffHand) {
				warningMessage = "This item must be held in one of the hands";
			} else if (ModConfig.allowMainHand) {
				warningMessage = "This item must be held in the main hand";
			} else if (ModConfig.allowOffHand) {
				warningMessage = "This item must be held in the off hand";
			} else {
				warningMessage = "This item can't cannot be in your inventory";
			}
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

		for (int inventorySlot = 0; inventorySlot < player.inventory.getSizeInventory(); inventorySlot++) {

			if (inventorySlot == mainHandSlot && ModConfig.allowMainHand) {
				continue;
			}

			if (inventorySlot == 40 && ModConfig.allowOffHand) {
				continue;
			}

			ItemStack stack = player.inventory.getStackInSlot(inventorySlot);
			if (stack.isEmpty()) {
				continue;
			}
			ResourceLocation itemResourceLocation = stack.getItem().getRegistryName();
			if (itemResourceLocation == null) {
				continue;
			}

			if (restrictedItemsSet.contains(itemResourceLocation.toString())) {
				player.entityDropItem(stack.copy(), 0);
				player.inventory.setInventorySlotContents(inventorySlot, ItemStack.EMPTY);
				if (ModConfig.displayMessage) {
					player.sendStatusMessage(new TextComponentString(warningMessage), true);
				}
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
			if (player.getHeldItemMainhand().isEmpty() && ModConfig.allowMainHand) {
				player.setHeldItem(EnumHand.MAIN_HAND, itemStack);
				event.getItem().setDead();
				event.setCanceled(true);
			} else if (player.getHeldItemOffhand().isEmpty() && ModConfig.allowOffHand) {
				player.setHeldItem(EnumHand.OFF_HAND, itemStack);
				event.getItem().setDead();
				event.setCanceled(true);
			} else {
				event.setCanceled(true);
			}
		}
	}
}