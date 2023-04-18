package de.confue.autoitemreplace.mixin;

import de.confue.autoitemreplace.client.AutoItemSwitchUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

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

	private static Item brokenItem = null;

	/*
	preItemBrokenCallback: Injected at the head of the method before any code was executed, clears the brokenItem
	field.

	itemBrokenCallback: Only called if the item was actually broken with the received damage stores the item in
	question in the brokenItem field.

	postItemBrokenCallback: Injected at any given return point of this method. If the brokenItem field is not null it
	will go ahead and switch the broken tool/weapon
	 */
	@Inject(method = {"damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"}, at = {@At(value =
			"HEAD", target = "Lnet/minecraft/item/ItemStack;decrement(I)V")}, locals = LocalCapture.CAPTURE_FAILSOFT)
	private <T extends LivingEntity> void preItemBrokenCallback(int amount, T entity, Consumer<T> breakCallback,
																CallbackInfo ci)
	{
		System.out.println("ItemStackMixin.preItemBrokenCallback");
		NbtList nbtElements = ((ItemStack)(Object)this).getEnchantments();
		System.out.println("nbtElements.size() = " + nbtElements.size());

//		brokenItem = null;
	}

	@Inject(method = {"damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"}, at = {@At(value =
			"INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V")}, locals = LocalCapture.CAPTURE_FAILSOFT)
	private <T extends LivingEntity> void itemBrokenCallback(int amount, T entity, Consumer<T> breakCallback,
															 CallbackInfo ci, Item item)
	{
//		brokenItem = item;

		if (item == null || brokenItem instanceof ArmorItem)
			return;

		// Retrieves the player inventory
		int slot = AutoItemSwitchUtilities.getSlotWithMatchingOrEqualItemFromInventory(((ItemStack)(Object)this));
		if (slot == -1)
			return;

		// Swap the found item with the currently selected hot bar slot
		MinecraftClient.getInstance().interactionManager.pickFromInventory(slot);
	}

	@Inject(method = {"damage(ILnet/minecraft/entity/LivingEntity;Ljava/util/function/Consumer;)V"}, at = {@At(value =
			"RETURN", target = "Lnet/minecraft/item/ItemStack;decrement(I)V")}, locals = LocalCapture.CAPTURE_FAILSOFT)
	private <T extends LivingEntity> void postItemBrokenCallback(int amount, T entity, Consumer<T> breakCallback,
																 CallbackInfo ci)
	{
//		if (brokenItem == null || brokenItem instanceof ArmorItem)
//			return;
//
//		// Retrieves the player inventory
//		int slot = AutoItemSwitchUtilities.getSlotWithMatchingOrEqualItemFromInventory(((ItemStack)(Object)this));
//		if (slot == -1)
//			return;
//
//		// Swap the found item with the currently selected hot bar slot
//		MinecraftClient.getInstance().interactionManager.pickFromInventory(slot);
	}

	/*
	Methods "use" and "finishUsing" are used to detect a food item that is being fully eaten.
	 */

	@Inject(method = {"use"}, at = {@At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getItem()" +
			"Lnet/minecraft/item/Item;")})
	private void use(World world, PlayerEntity user, Hand hand,
					 CallbackInfoReturnable<TypedActionResult<ItemStack>> cir)
	{
		if (!(user instanceof ClientPlayerEntity))
			return;

		lastUsedEquipmentSlot = hand == Hand.MAIN_HAND ? EquipmentSlot.MAINHAND :
				EquipmentSlot.OFFHAND;
		ItemStack itemStack = user.getEquippedStack(lastUsedEquipmentSlot);
		lastUsedItemStack = itemStack.copy();
	}

	@Inject(method = {"finishUsing"}, at = {@At(value = "RETURN")})
	private void finishUsing(World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir)
	{
		if (!(user instanceof ClientPlayerEntity))
			return;

		ItemStack itemStack = user.getEquippedStack(lastUsedEquipmentSlot);

		if (lastUsedItemStack.isItemEqual(itemStack))
			return;

		int slot = AutoItemSwitchUtilities.getSlotWithMatchingOrEqualItemFromInventory(lastUsedItemStack);
		if (slot == -1)
			return;

//		inventory.swapSlotWithHotbar(slot);
		MinecraftClient.getInstance().interactionManager.pickFromInventory(slot);
	}



}
