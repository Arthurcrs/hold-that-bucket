package com.mahghuuuls.holdthatbucket;

import java.util.HashSet;

import com.mahghuuuls.holdthatbucket.util.ModConfig;
import com.mahghuuuls.holdthatbucket.util.ModReference;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@Mod(modid = ModReference.MOD_ID, name = ModReference.NAME, version = ModReference.VERSION)
@Mod.EventBusSubscriber(modid = ModReference.MOD_ID)
public class Main {
	private static HashSet<String> affectItemsSet = new HashSet<>();
	private static String warningMessage;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		for (String itemId : ModConfig.restrictedItems) {
			affectItemsSet.add(itemId);
		}

		if (ModConfig.displayMessageOnDrop || ModConfig.displayMessageOnPickup) {
			if (ModConfig.allowMainHand && ModConfig.allowOffHand) {
				warningMessage = "You must hold this item in your main or off hand.";
			} else if (ModConfig.allowMainHand) {
				warningMessage = "You must hold this item in your main hand.";
			} else if (ModConfig.allowOffHand) {
				warningMessage = "You must hold this item in your off hand.";
			} else {
				warningMessage = "This item cannot be held or placed anywhere in your inventory.";
			}
		}

	}

	/**
	 * Every player tick scan the player whole inventory for the items in the list,
	 * if found the item, drop the stack
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

			if (affectItemsSet.contains(itemResourceLocation.toString())) {
				player.dropItem(stack.copy(), false);
				player.inventory.setInventorySlotContents(inventorySlot, ItemStack.EMPTY);
				if (ModConfig.displayMessageOnDrop) {
					player.sendStatusMessage(new TextComponentString(warningMessage), true);
				}
			}
		}
	}

	/**
	 * Ensures items can only be picked up when it goes to an allowed item slot
	 * (main hand and/or off hand)
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

		if (affectItemsSet.contains(itemResourceLocation.toString())) {
			if (player.getHeldItemMainhand().isEmpty() && ModConfig.allowMainHand) {
				player.setHeldItem(EnumHand.MAIN_HAND, itemStack);
				playPickupSoundAtPlayer(player);
				if (ModConfig.displayMessageOnPickup) {
					player.sendStatusMessage(new TextComponentString(warningMessage), true);
				}
				event.getItem().setDead();
				event.setCanceled(true);
			} else if (player.getHeldItemOffhand().isEmpty() && ModConfig.allowOffHand) {
				player.setHeldItem(EnumHand.OFF_HAND, itemStack);
				playPickupSoundAtPlayer(player);
				if (ModConfig.displayMessageOnPickup) {
					player.sendStatusMessage(new TextComponentString(warningMessage), true);
				}
				event.getItem().setDead();
				event.setCanceled(true);
			} else {
				event.setCanceled(true);
			}
		}
	}

	/**
	 * Play the pickup sound at the player location with a random variation of pitch
	 */
	private static void playPickupSoundAtPlayer(EntityPlayer player) {
		player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP,
				SoundCategory.PLAYERS, 0.2F,
				((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
	}
}