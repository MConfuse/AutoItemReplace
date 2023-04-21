package de.confue.hotbaritemreplacer.client.options;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum FoodPriorityOption
{
	PLANTS("gui.options.hotbar_item_replacer.option.food_sorting.category.plant"),
	MEAT("gui.options.hotbar_item_replacer.option.food_sorting.category.meat"),
	ANYTHING("gui.options.hotbar_item_replacer.option.food_sorting.category.any");

	// Using a bit mask to keep myself open for future extensions to food sorting - probably unnecessary though lol
	public static final int HUNGER_PRIORITY = 0x01;
	public static final int SATURATION_PRIORITY = 0x02;

	private static FoodPriorityOption currentFoodPriority = ANYTHING;
	private static int currentSortingPriority = HUNGER_PRIORITY;

	private final String translatableKey;

	FoodPriorityOption(String translatableKey)
	{
		this.translatableKey = translatableKey;
	}

	/**
	 * Returns the item slot index of the item that best fits the requirements set by {@link #getCurrentFoodPriority()}
	 * and {@link #getCurrentSortingPriority()}.
	 *
	 * @param itemSlots    Array with Structure: 0: hungerLevel, 1: inventory slot || 2: saturationLevel, 3: inventory
	 *                     slot
	 * @param prefFoodType Array with Structure: 0: hungerLevel, 1: inventory slot || 2: saturationLevel, 3: inventory
	 *                     slot
	 * @return -1 or < 0 < 36 if a valid item was parsed within one of the arrays
	 */
	public static int getBestSuitedItem(float[] itemSlots, float[] prefFoodType)
	{
		final int firstCheck = currentSortingPriority == HUNGER_PRIORITY ? 1 : 3, secondCheck =
				currentSortingPriority == HUNGER_PRIORITY ? 3 : 1;

		switch (currentFoodPriority)
		{
			case PLANTS, MEAT ->
			{
				if (prefFoodType[firstCheck] != -1)
					return (int) prefFoodType[firstCheck];
				else if (prefFoodType[secondCheck] != -1)
					return (int) itemSlots[secondCheck];
				else if (itemSlots[firstCheck] != -1)
					return (int) itemSlots[firstCheck];
				else
					return (int) itemSlots[secondCheck];
			}
			case ANYTHING ->
			{
				if (itemSlots[firstCheck] != -1)
					return (int) itemSlots[firstCheck];
				else
					return (int) itemSlots[secondCheck];
			}
			default ->
			{
				System.out.println("FoodPriorityOption.getBestSuitedItem");
				System.err.println("Unknown FoodPriorityOption: '" + currentFoodPriority + "'!");
			}
		}

		return -1;
	}

	public static FoodPriorityOption getCurrentFoodPriority()
	{
		return currentFoodPriority;
	}

	public static void setCurrentFoodPriority(FoodPriorityOption currentFoodPriority)
	{
		FoodPriorityOption.currentFoodPriority = currentFoodPriority;
	}

	public static int getCurrentSortingPriority()
	{
		return currentSortingPriority;
	}

	public static void setCurrentSortingPriority(int currentSortingPriority)
	{
		FoodPriorityOption.currentSortingPriority = currentSortingPriority;
	}

	@Contract(value = " -> new", pure = true)
	public @NotNull MutableText getTranslationText()
	{
		return Text.translatable(translatableKey);
	}
}