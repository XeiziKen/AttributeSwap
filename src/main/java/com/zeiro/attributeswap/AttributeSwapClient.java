package com.zeiro.attributeswap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeSwapClient implements ClientModInitializer {
	public static final String MOD_ID = "attributeswap";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		SwapConfig.load();

		AttackEntityCallback.EVENT.register(SwapController::onAttack);
		ClientTickEvents.END_CLIENT_TICK.register(SwapController::tick);

		// Buka layar setting lewat chat: /attributeswap
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
			dispatcher.register(ClientCommandManager.literal("attributeswap").executes(context -> {
				Minecraft mc = Minecraft.getInstance();
				mc.execute(() -> mc.setScreen(ConfigScreenFactory.build(null)));
				return 1;
			})));

		LOGGER.info("Auto AttributeSwap & StunSlam loaded. Settings: /attributeswap");
	}
}
