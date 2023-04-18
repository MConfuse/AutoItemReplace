package de.confue.autoitemreplace.mixin;

import de.confue.autoitemreplace.client.AutoItemSwitchUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({ClientPlayerInteractionManager.class})
public class ClientPlayerInteractionMixin
{
	private static ItemStack previouslyUsedItem = ItemStack.EMPTY;

	@Inject(
			at = {@At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket" +
							"(Lnet/minecraft/client/world/ClientWorld;" +
							"Lnet/minecraft/client/network/SequencedPacketCreator;)V"
			)},
			method = {"interactBlock"}
	)
	private void preBlockInteraction(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult,
									 CallbackInfoReturnable<ActionResult> cir)
	{
		if (player.isSpectator())
			return;

		ItemStack itemStack = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();

		previouslyUsedItem = itemStack.copy();
	}

	@Inject(
			at = {@At(
					value = "TAIL",
					target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket" +
							"(Lnet/minecraft/client/world/ClientWorld;" +
							"Lnet/minecraft/client/network/SequencedPacketCreator;)V"
			)},
			method = {"interactBlock"}
	)
	private void postBlockInteraction(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult,
									  CallbackInfoReturnable<ActionResult> cir)
	{
		if (player.isSpectator())
			return;

		ItemStack itemStack = hand == Hand.MAIN_HAND ? player.getMainHandStack() : player.getOffHandStack();

		if (previouslyUsedItem.isItemEqual(itemStack) || itemStack.isDamageable() || hand == Hand.OFF_HAND)
			return;

		int slot = AutoItemSwitchUtilities.getSlotWithMatchingOrEqualItemFromInventory(previouslyUsedItem);

		if (slot == -1)
			return;

		MinecraftClient.getInstance().interactionManager.pickFromInventory(slot);
	}

}
