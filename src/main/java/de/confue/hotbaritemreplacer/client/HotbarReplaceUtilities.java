package de.confue.hotbaritemreplacer.client;

import de.confue.hotbaritemreplacer.client.options.FoodPriorityOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HotbarReplaceUtilities
{
	/**
	 * @param oldItemStack The {@link ItemStack} that should be searched for
	 * @return The slot that {@link Item} was found in, or -1 if nothing was found
	 */
	public static int getSlotWithMatchingOrEqualItemFromInventory(ItemStack oldItemStack)
	{
		ClientPlayerEntity player = MinecraftClient.getInstance().player;

		if (oldItemStack == null)
		{
			System.err.println("HotbarReplaceUtilities.getSlotWithMatchingOrEqualItemFromInventory:");
			System.err.println("The item that was used up / broke is null?");
			return -1;
		}
		else if (player == null)
		{
			System.err.println("HotbarReplaceUtilities.getSlotWithMatchingOrEqualItemFromInventory");
			System.err.println("ClientPlayerEntity was null! Is this a server?");
			return -1;
		}


		PlayerInventory inventory = player.getInventory();
		Item oldItem = oldItemStack.getItem();
		final boolean isMiningTool = oldItem instanceof MiningToolItem; // Tools are sorted by efficiency enchants
		final boolean isSilkTouch = EnchantmentHelper.hasSilkTouch(oldItemStack);

		final boolean isMeleeItem = oldItem instanceof SwordItem; // Swords are sorted by sharpness enchants
//		final boolean isRangedItem = oldItemStack instanceof RangedWeaponItem;

		final boolean isFoodItem = oldItemStack.isFood();

		int slot = getInventorySlotOfSameItem(inventory, oldItem, isMiningTool, isSilkTouch, isMeleeItem, isFoodItem);

		// found an item of the same type
		if (slot != -1)
		{
			return slot;
		}

		return getInventorySlotOfSimilarItem(inventory, isMiningTool, isSilkTouch, isMeleeItem, isFoodItem);
	}

	/**
	 * @param inventory    The inventory of the {@link net.minecraft.entity.player.PlayerEntity}
	 * @param oldItem      The {@link Item} (type) to look for
	 * @param isMiningTool Is the oldItem a tool?
	 * @param isSilkTouch  Is the item enchanted with silk touch?
	 * @param isMeleeItem  Is the item a melee weapon?
	 * @param isFoodItem   Is the item edible?
	 * @return -1 or the index of the next best item of the same type/level
	 */
	private static int getInventorySlotOfSameItem(@NotNull PlayerInventory inventory, Item oldItem,
												  boolean isMiningTool, boolean isSilkTouch, boolean isMeleeItem,
												  boolean isFoodItem)
	{
		DefaultedList<ItemStack> main = inventory.main;

		/* Array Structure:	0: enchantLevel, 1: slotIndex */
		final int[] itemSlots = {-1, -1};

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
			if (getItemIdentifier(oldItem).equals(getItemIdentifier(stack.getItem())))
			{
				int enchantLevel;
				// We can assume that if we get here, the item we are looking at is actually a tool too
				if (isMiningTool)
				{
					/* Is the current tool we are replacing a silk touch one? */
					if (isSilkTouch)
					{
						/* If so, is the one we are looking at silk touch too? */
						if (EnchantmentHelper.hasSilkTouch(stack))
							enchantLevel = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
						else
							continue; // no? next!
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

			}

		}

		/* Either -1 or actually a slot in the inventory that has the next best alternative to the broken/used item */
		return itemSlots[1];
	}

	/**
	 * @param inventory    The inventory of the {@link net.minecraft.entity.player.PlayerEntity}
	 * @param isMiningTool Is the oldItem a tool?
	 * @param isSilkTouch  Is the item enchanted with silk touch?
	 * @param isMeleeItem  Is the item a melee weapon?
	 * @param isFoodItem   Is the item edible?
	 * @return -1 or the index of the next best item of the same type (being a block/food)/level of that tool/weapon
	 * that is the highest level available, with the highest efficiency
	 */
	private static int getInventorySlotOfSimilarItem(@NotNull PlayerInventory inventory, boolean isMiningTool,
													 boolean isSilkTouch, boolean isMeleeItem, boolean isFoodItem)
	{
		// TODO: Add customization to choose, for ex., blocks that should be replaced for now, only tools
		DefaultedList<ItemStack> main = inventory.main;
		/* Array Structure:	0: enchantLevel, 1: slotIndex, 2: materialIndex, 3: foodSlotIndex */
		final float[] itemSlots = {-1, -1, -1, -1};
		/* Preferred food type 0: hungerLevel, 1: inventory slot, || 2: saturationLevel, 3: inventory slot */
		final float[] prefFoodType = {-1, -1, -1, -1};

		/*
		Order of search !Excludes any crafting/armor/offhand slot!:
		0 - 8: Hot bar
		9 - 35: Inventory from top left to bottom right, left to right, row for row.
		 */
		for (int i = 0; i < main.size(); i++)
		{
			ItemStack stack = main.get(i);
			if (i == inventory.selectedSlot)
				continue;

			if ((isMiningTool || isMeleeItem) && stack.getItem() instanceof ToolItem)
			{
				int enchantLevel = -1, miningLevel = -1;

				if (isMiningTool && stack.getItem() instanceof MiningToolItem)
				{
					miningLevel = ((ToolItem) stack.getItem()).getMaterial().getMiningLevel();

					/* Is the current tool we are replacing a silk touch one? */
					if (isSilkTouch)
					{
						/* If so, is the one we are looking at silk touch too? */
						if (EnchantmentHelper.hasSilkTouch(stack))
							enchantLevel = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
						else
							continue; // no? next!
					}
					else if (!EnchantmentHelper.hasSilkTouch(stack))
						enchantLevel = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
					else
						continue;
				}
				else if (isMeleeItem && stack.getItem() instanceof SwordItem)
				{
					miningLevel = ((ToolItem) stack.getItem()).getMaterial().getMiningLevel();
					enchantLevel = EnchantmentHelper.getLevel(Enchantments.SHARPNESS, stack);
				}

				if (itemSlots[0] < enchantLevel && itemSlots[2] <= miningLevel)
				{
					itemSlots[0] = enchantLevel;
					itemSlots[1] = i;
					itemSlots[2] = miningLevel;
				}

			}
			else if (isFoodItem && stack.isFood())
			{
				/* Uses Item slots a bit different, as to guarantee that we find SOME food:
				0: hungerLevel, 1: inventory slot || 2: saturationLevel, 3: inventory slot */
				FoodComponent foodComponent = Objects.requireNonNull(stack.getItem().getFoodComponent());
				float hungerLevel = foodComponent.getHunger(), saturationLevel = foodComponent.getSaturationModifier();

				if (FoodPriorityOption.getCurrentFoodPriority() == FoodPriorityOption.PLANTS && !foodComponent.isMeat())
				{
					if (prefFoodType[0] < hungerLevel)
					{
						prefFoodType[0] = hungerLevel;
						prefFoodType[1] = i;
					}

					if (prefFoodType[2] < saturationLevel)
					{
						prefFoodType[2] = saturationLevel;
						prefFoodType[3] = i;
					}
				}
				else if (FoodPriorityOption.getCurrentFoodPriority() == FoodPriorityOption.MEAT && foodComponent.isMeat())
				{
					if (prefFoodType[0] < hungerLevel)
					{
						prefFoodType[0] = hungerLevel;
						prefFoodType[1] = i;
					}

					if (prefFoodType[2] < saturationLevel)
					{
						prefFoodType[2] = saturationLevel;
						prefFoodType[3] = i;
					}
				}

				if (itemSlots[0] < hungerLevel)
				{
					itemSlots[0] = hungerLevel;
					itemSlots[1] = i;
				}

				if (itemSlots[2] < saturationLevel)
				{
					itemSlots[2] = saturationLevel;
					itemSlots[3] = i;
				}

			}

		}

		// --- Returning the found Inventory Slot ---

		if (isFoodItem)
		{
			return FoodPriorityOption.getBestSuitedItem(itemSlots, prefFoodType);
		}
		else
		{
			return (int) itemSlots[1];
		}

	}

	/**
	 * @param item The {@link Item} to return the {@link Identifier} of
	 * @return The identifier of the given item
	 */
	public static @NotNull Identifier getItemIdentifier(Item item)
	{
		return Registries.ITEM.getId(item);
	}

}
