package de.confue.hotbaritemreplacer.client.options;

import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class ModOptions
{
	private static final Path configFile;
	private static final Properties modProperties;

	static
	{
		configFile = FabricLoader.getInstance().getConfigDir().resolve("hotbarItemReplace.properties");
		modProperties = new Properties();
	}

	public static void loadModProperties()
	{
		if (!Files.exists(configFile))
		{
			try
			{
				Files.createFile(configFile);
				System.out.println("HotbarItemReplacerClient.loadModProperties");
				System.out.println("Created HotbarItemReplacer properties file!");
			}
			catch (IOException e)
			{
				System.err.println("HotbarItemReplacerClient.loadModProperties");
				System.err.println("Could not create HotbarItemReplacer properties file!");
				e.printStackTrace();
			}
		}

		try
		{
			modProperties.load(new FileReader(new File(configFile.toUri())));
		}
		catch (IOException e)
		{
			System.err.println("HotbarItemReplacerClient.loadModProperties");
			System.err.println("Failed to load properties file!");
			e.printStackTrace();
		}

		FoodPriorityOption.setCurrentFoodPriority(getPropertyAsEnum((String) modProperties.computeIfAbsent("food" +
				"-priority", func -> "ANYTHING"), FoodPriorityOption.ANYTHING));
		FoodPriorityOption.setCurrentSortingPriority(getPropertyAsInt((String) modProperties.computeIfAbsent("food" +
				"-sorting-priority", func -> "1"), FoodPriorityOption.HUNGER_PRIORITY));
		saveModProperties();
	}

	public static void saveModProperties()
	{
		try
		{
			try (OutputStream stream = Files.newOutputStream(configFile))
			{
				modProperties.store(stream, "HotbarItemReplacer properties file");

				if (stream != null)
					stream.close();
			}

		}
		catch (Exception e)
		{
			System.err.println("ModOptions.saveModProperties");
			System.err.println("Could not save the HotbarItemReplacer properties file!");
			e.printStackTrace();
		}
	}

	/**
	 * Uses the {@link Properties#replace(Object, Object)} operation to update the value assigned to {@code key}.
	 *
	 * @param key   The key of the property
	 * @param value The new value of the property
	 * @return true if value was updated, false if otherwise
	 */
	public static boolean updatePropertyValue(String key, String value)
	{
		return modProperties.replace(key, value) != null;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Enum<T>> T getPropertyAsEnum(String property, T defaultValue)
	{
		if (property == null || property.isEmpty())
			return defaultValue;

		for (Enum<T> obj : defaultValue.getClass().getEnumConstants())
		{
			if (property.equalsIgnoreCase(obj.name()))
				return (T) obj;
		}

		return defaultValue;
	}

	private static int getPropertyAsInt(String property, int defaultValue)
	{
		if (property == null || property.isEmpty())
			return defaultValue;

		try
		{
			return Integer.parseInt(property);
		}
		catch (NumberFormatException e)
		{
			return defaultValue;
		}

	}
}
