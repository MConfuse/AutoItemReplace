package de.confue.hotbaritemreplacer.client.gui;

import de.confue.hotbaritemreplacer.client.HotbarReplaceUtilities;
import de.confue.hotbaritemreplacer.client.options.FoodPriorityOption;
import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.awt.*;

public class GuiHirSettings extends Screen
{
	public GuiHirSettings(Text title)
	{
		super(title);
	}

	@Override
	protected void init()
	{
		// TODO: Figure out a good way to do stuff, this is horrible lmao
		int buttonWidth = 125;
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(2);

		SimpleOption<Boolean> foodSortingCategoryOption = SimpleOption.ofBoolean("gui.options.hotbar_item_replacer" +
						".option.food_sorting" +
						".category.name", SimpleOption.constantTooltip(Text.translatable("gui.options" +
						".hotbar_item_replacer.option.food_sorting.category.tooltip")), (optionText, value) ->
						value ? Text.translatable("gui.options.hotbar_item_replacer.option.food_sorting.category" +
								".meat") :
								Text.translatable("gui.options.hotbar_item_replacer.option.food_sorting.category" +
										".plant"),
				false,
				aBoolean ->
				{
					if (aBoolean)
						HotbarReplaceUtilities.foodPriorityOption = FoodPriorityOption.MEAT;
					else
						HotbarReplaceUtilities.foodPriorityOption = FoodPriorityOption.PLANTS;
				});
		adder.add(foodSortingCategoryOption.createWidget(MinecraftClient.getInstance().options, 10, 10, buttonWidth));
		SimpleOption<Boolean> foodSortingTypeOption = SimpleOption.ofBoolean("gui.options.hotbar_item_replacer" +
						".option" +
						".food_sorting.type.name", SimpleOption.constantTooltip(Text.translatable("gui.options" +
						".hotbar_item_replacer.option.food_sorting.type.tooltip")), (optionText, value) ->
						value ? Text.translatable("gui.options.hotbar_item_replacer.option.food_sorting.type.hunger") :
								Text.translatable("gui.options.hotbar_item_replacer.option.food_sorting.type" +
										".saturation"),
				false,
				aBoolean ->
				{
					if (aBoolean)
					{
						for (FoodPriorityOption option : FoodPriorityOption.values())
							option.setSortingPriority(FoodPriorityOption.HUNGER_PRIORITY);
					}
					else
					{
						for (FoodPriorityOption option : FoodPriorityOption.values())
							option.setSortingPriority(FoodPriorityOption.SATURATION_PRIORITY);
					}
				});
		adder.add(foodSortingTypeOption.createWidget(MinecraftClient.getInstance().options, 10, 10, buttonWidth));

		gridWidget.refreshPositions();
		SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
		gridWidget.forEachChild(this::addDrawableChild);

		super.init();
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta)
	{
		Renderer2d.renderQuad(matrices, new Color(25, 25, 25, 150), 0, 0, width, height);

		super.render(matrices, mouseX, mouseY, delta);
	}

	@Override
	public void onDisplayed()
	{
		super.onDisplayed();
	}

	@Override
	public void removed()
	{
		super.removed();
	}


}
