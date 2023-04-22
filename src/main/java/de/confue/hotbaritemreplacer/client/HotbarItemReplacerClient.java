package de.confue.hotbaritemreplacer.client;

import de.confue.hotbaritemreplacer.client.gui.GuiHirSettings;
import de.confue.hotbaritemreplacer.client.options.ModOptions;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;

public class HotbarItemReplacerClient implements ClientModInitializer
{
	private static KeyBinding settingsScreenKeybinding;
	private static GuiHirSettings guiSettings;

	@Override
	public void onInitializeClient()
	{
		ModOptions.loadModProperties();

		settingsScreenKeybinding = new KeyBinding("key.hotbar_item_replacer.settings", InputUtil.Type.KEYSYM,
				InputUtil.UNKNOWN_KEY.getCode(), "category.hotbar_item_replacer.settings");
		guiSettings = new GuiHirSettings(Text.translatable("gui.options.hotbar_item_replacer.gui.name"));

		KeyBindingHelper.registerKeyBinding(settingsScreenKeybinding);

		ClientTickEvents.END_CLIENT_TICK.register(HotbarItemReplacerClient::handleClientTickEvent);

		Runtime.getRuntime().addShutdownHook(
				new Thread(ModOptions::saveModProperties, "HotbarItemReplace Config Save"));
	}

	private static void handleClientTickEvent(MinecraftClient client)
	{
		if (settingsScreenKeybinding.wasPressed())
		{
			MinecraftClient.getInstance().setScreen(guiSettings);
		}

	}

}
