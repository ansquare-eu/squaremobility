package eu.ansquare.squaremobility.mixin;

import eu.ansquare.squaremobility.Squaremobility;


import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {
	@Inject(method = "<init>", at = @At("TAIL"))
	private void example$init(RunArgs args, CallbackInfo ci) {
		Squaremobility.LOGGER.info("Hello from {}", Squaremobility.NAME);
	}
}
