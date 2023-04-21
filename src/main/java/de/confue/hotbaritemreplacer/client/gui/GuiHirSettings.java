package de.confue.hotbaritemreplacer.client.gui;

import de.confue.hotbaritemreplacer.client.options.FoodPriorityOption;
import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
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
		int buttonWidth = 140, buttonHeight = 20;
		GridWidget gridWidget = new GridWidget();
		gridWidget.getMainPositioner().marginX(5).marginBottom(4).alignHorizontalCenter();
		GridWidget.Adder adder = gridWidget.createAdder(2);

		adder.add(CyclingButtonWidget.builder(FoodPriorityOption::getTranslationText).values(FoodPriorityOption.values()).
				initially(FoodPriorityOption.getCurrentFoodPriority()).
				tooltip(SimpleOption.constantTooltip(Text.translatable("gui.options.hotbar_item_replacer.option" +
						".food_sorting.category.tooltip"))).
				build(0, 0, buttonWidth, buttonHeight, Text.translatable("gui.options.hotbar_item_replacer.option" +
								".food_sorting.category.name"),
						(widget, foodPriority) -> FoodPriorityOption.setCurrentFoodPriority(foodPriority)));
		adder.add(getFoodTypeOptionWidget(buttonWidth));

		gridWidget.refreshPositions();
		SimplePositioningWidget.setPos(gridWidget, 0, this.height / 6 - 12, this.width, this.height, 0.5F, 0.0F);
		gridWidget.forEachChild(this::addDrawableChild);

		super.init();
	}

	private static ClickableWidget getFoodTypeOptionWidget(int buttonWidth)
	{
		return SimpleOption.ofBoolean("gui.options.hotbar_item_replacer.option.food_sorting.type.name",
				SimpleOption.constantTooltip(Text.translatable("gui.options.hotbar_item_replacer.option.food_sorting" +
						".type.tooltip")), (optionText, value) ->
						value ? Text.translatable("gui.options.hotbar_item_replacer.option.food_sorting.type.hunger") :
								Text.translatable("gui.options.hotbar_item_replacer.option.food_sorting.type" +
										".saturation"),
				FoodPriorityOption.getCurrentSortingPriority() == FoodPriorityOption.HUNGER_PRIORITY,
				aBoolean ->
				{
					if (aBoolean)
						FoodPriorityOption.setCurrentSortingPriority(FoodPriorityOption.HUNGER_PRIORITY);
					else
						FoodPriorityOption.setCurrentSortingPriority(FoodPriorityOption.SATURATION_PRIORITY);
				}).createWidget(MinecraftClient.getInstance().options, 10, 10, buttonWidth);
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
