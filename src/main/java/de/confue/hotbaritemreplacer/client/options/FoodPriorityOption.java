package de.confue.hotbaritemreplacer.client.options;

public enum FoodPriorityOption implements IHotbarReplaceOption
{
	PLANTS("Plants", "Looks for plant (and fish) based food first, before selecting meat based food"),
	MEAT("Meat", "Looks for meat first, before selecting plant based food");

	// Using a bit mask to keep myself open for future extensions to food sorting - probably unnecessary though lol
	public static final int HUNGER_PRIORITY = 0x01;
	public static final int SATURATION_PRIORITY = 0x02;

	static
	{
		// Initialize the sorting priorities
		PLANTS.setSortingPriority(HUNGER_PRIORITY);
		MEAT.setSortingPriority(HUNGER_PRIORITY);
	}

	private final String displayName;
	private final String description;
	private int sortingPriority;

	FoodPriorityOption(String displayName, String description)
	{
		this.displayName = displayName;
		this.description = description;
	}

	public void setSortingPriority(int sortingPriority)
	{
		this.sortingPriority = sortingPriority;
	}

	public int getSortingPriority()
	{
		return sortingPriority;
	}

	@Override
	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public String getDescription()
	{
		return description;
	}
}
