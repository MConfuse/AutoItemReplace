package de.confue.autoitemreplace.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;

public class AutoItemSwitchUtilities
{

	/**
	 * @param oldItemStack The item stack that should be searched for
	 * @return The slot that item was found in, or -1 if nothing was found
	 */
	public static int getSlotWithMatchingOrEqualItemFromInventory(ItemStack oldItemStack)
	{
		if (oldItemStack == null)
		{
			System.err.println("AutoItemSwitchUtilities.getSlotWithMatchingOrEqualItemFromInventory:");
			System.err.println("The item that was used up / broke is null?");
			return -1;
		}

		ClientPlayerEntity player = MinecraftClient.getInstance().player;
		PlayerInventory inventory = player.getInventory();
		DefaultedList<ItemStack> main = inventory.main;
		Item oldItem = oldItemStack.getItem();
		/* Array Structure:	0: enchantLevel,	1: slotIndex */
		final int[] itemSlots = {-1, -1}; // initializing everything to -1
		final boolean isTool = oldItem instanceof MiningToolItem; // Tools are sorted by efficiency enchants
		final boolean isSilkTouch = EnchantmentHelper.hasSilkTouch(oldItemStack);

		final boolean isMeleeItem = oldItem instanceof SwordItem; // Swords are sorted by sharpness enchants
//		final boolean isRangedItem = oldItemStack instanceof RangedWeaponItem;

		System.out.println("isTool = " + isTool);
		System.out.println("isSilkTouch = " + isSilkTouch); // TODO: Fix silk touch

		// TODO: How do we manage silk touch? Surely not by replacing a silk pickaxe with a non silk one if it breaks

		/*
		Order of search !Excludes any crafting/armor/offhand slot!:
		0 - 8: Hot bar
		9 - 35: Inventory from top left to bottom right, left to right, row for row.
		 */
		for (int i = 0; i < main.size(); i++)
		{
			ItemStack stack = main.get(i);
			/* skip the currently selected sameItemSlot as it might have a broken item in it, weird stuff | only
			needed for
			the item broken check though, never happens anywhere else - probably just the fact my injection point-/s
			SUCK lmao */
			if (i == inventory.selectedSlot)
				continue;
			/*
			Compares the old Item with the item in the sameItemSlot i, if they have the same name, they are at least
			of the
			same type.
			 */
			else if (getItemIdentifier(oldItem).equals(getItemIdentifier(stack.getItem())))
			{
				int enchantLevel;
				// We can assume that if we get here, the item we are looking at is actually a tool too
				if (isTool)
				{
					/* Is the current tool we are replacing a silk touch one? */
					if (isSilkTouch)
					{
						/* If so, is the one we are looking at silk touch too? */
						if (EnchantmentHelper.hasSilkTouch(stack))
						{
							System.out.println("silk");
							enchantLevel = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
						}
						else
						{
							System.out.println("no silk");
							continue; // no? next!
						}
					}
					else if (!EnchantmentHelper.hasSilkTouch(stack))
						enchantLevel = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
					else
						continue;
				}
				else if (isMeleeItem)
				{
					enchantLevel = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack);
				}
//				else if (isRangedItem)
//				{
//					// TODO: What do I even look for in (cross-)bows? Power? Punch?
//				}
				else
				{
					return i;
				}

				if (itemSlots[0] < enchantLevel)
				{
					itemSlots[0] = enchantLevel;
					itemSlots[1] = i;
				}

//				sameItemSlot = i;
				// break
			}

		}

		// if [0] is not -1 the slot shouldn't be either
		if (itemSlots[0] != -1)
		{
			return itemSlots[1];
		}

		/*
		Pickaxe order: Same level -> Netherite -> Diamond -> etc
		 */

		// TODO: Search for other items of the same category


		return -1;
	}

	public static Identifier getItemIdentifier(Item item)
	{
		return Registries.ITEM.getId(item);
	}


}
