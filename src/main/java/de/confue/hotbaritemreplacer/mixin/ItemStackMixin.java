package de.confue.hotbaritemreplacer.mixin;

import de.confue.hotbaritemreplacer.client.HotbarReplaceUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Objects;
import java.util.function.Consumer;

@Mixin({ItemStack.class})
public class ItemStackMixin
{
	/**
	 * Stores the last used Item, for example a Crossbow or any food item. Will be read by the
	 * {@link #finishUsing(World, LivingEntity, CallbackInfoReturnable)} method.
	 */
	private static ItemStack lastUsedItemStack = ItemStack.EMPTY;
	/**
	 * Determines the hand (main/off) that was used for the {@link #lastUsedItemStack} in form of the equipment slot.
	 */
	private static EquipmentSlot lastUsedEquipmentSlot = EquipmentSlot.MAINHAND;

	@Inject(method = {"damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"}, at = {@At(value =
			"INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V")}, locals = LocalCapture.CAPTURE_FAILSOFT)
	private <T extends LivingEntity> void itemBrokenCallback(int amount, T entity, Consumer<T> breakCallback,
															 CallbackInfo ci, Item item)
	{
//		brokenItem = item;

		if (item == null || item instanceof ArmorItem)
			return;

		// Retrieves the player inventory
		int slot = HotbarReplaceUtilities.getSlotWithMatchingOrEqualItemFromInventory(((ItemStack) (Object) this));
		if (slot == -1)
			return;

		// Swap the found item with the currently selected hot bar slot
		Objects.requireNonNull(MinecraftClient.getInstance().interactionManager).pickFromInventory(slot);
	}

	/*
	Methods "use" and "finishUsing" are used to detect a food item that is being fully eaten.
	 */

	@Inject(method = {"use"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()" +
			"Lnet/minecraft/item/Item;")})
	private void use(World world, PlayerEntity user, Hand hand,
					 CallbackInfoReturnable<TypedActionResult<ItemStack>> cir)
	{
		if (!(user instanceof ClientPlayerEntity) || user.isSpectator())
			return;

		lastUsedEquipmentSlot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND :
				EquipmentSlot.OFFHAND;
		ItemStack itemStack = user.getEquippedStack(lastUsedEquipmentSlot);
		lastUsedItemStack = itemStack.copy();
	}

	@Inject(method = {"finishUsing"}, at = {@At(value = "RETURN")})
	private void finishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir)
	{
		if (!(user instanceof ClientPlayerEntity) || user.isSpectator())
			return;

		ItemStack itemStack = user.getEquippedStack(lastUsedEquipmentSlot);

		if (lastUsedItemStack.isItemEqual(itemStack))
			return;

		int slot = HotbarReplaceUtilities.getSlotWithMatchingOrEqualItemFromInventory(lastUsedItemStack);

		if (slot == -1)
			return;

		Objects.requireNonNull(MinecraftClient.getInstance().interactionManager).pickFromInventory(slot);
	}

}
