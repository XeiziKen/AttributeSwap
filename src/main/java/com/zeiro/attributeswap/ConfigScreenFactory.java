package com.zeiro.attributeswap;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Layar setting (Cloth Config): tab General + Trigger 1..3, mirip di video. */
public final class ConfigScreenFactory {
	private ConfigScreenFactory() {}

	public static Screen build(Screen parent) {
		SwapConfig cfg = SwapConfig.get();

		ConfigBuilder builder = ConfigBuilder.create()
			.setParentScreen(parent)
			.setTitle(Component.literal("Auto AttributeSwap & StunSlam"))
			.setSavingRunnable(SwapConfig::save);

		ConfigEntryBuilder eb = builder.entryBuilder();

		ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
		general.addEntry(eb.startBooleanToggle(Component.literal("Enabled"), cfg.enabled)
			.setDefaultValue(true)
			.setSaveConsumer(v -> cfg.enabled = v)
			.build());
		general.addEntry(eb.startIntSlider(Component.literal("Step delay (ticks)"), cfg.delayTicks, 0, 10)
			.setDefaultValue(1)
			.setTooltip(Component.literal("Jeda antar langkah swap. 0 = secepat mungkin."))
			.setSaveConsumer(v -> cfg.delayTicks = v)
			.build());

		for (int i = 0; i < SwapConfig.TRIGGER_COUNT; i++) {
			SwapConfig.Trigger t = cfg.triggers[i];
			ConfigCategory cat = builder.getOrCreateCategory(Component.literal("Trigger " + (i + 1)));

			cat.addEntry(eb.startStrField(Component.literal("Item ID"), t.itemId)
				.setDefaultValue("")
				.setTooltip(Component.literal("Contoh: minecraft:netherite_axe atau minecraft:wind_charge. Kosong = trigger mati."))
				.setSaveConsumer(v -> t.itemId = v)
				.build());
			cat.addEntry(eb.startBooleanToggle(Component.literal("Stun Slam mode"), t.stunSlam)
				.setDefaultValue(false)
				.setTooltip(Component.literal("Yes: serang 2x (primary lalu secondary). No: Attribute Swap."))
				.setSaveConsumer(v -> t.stunSlam = v)
				.build());
			cat.addEntry(eb.startIntSlider(Component.literal("Primary slot"), t.primarySlot, 1, 9)
				.setDefaultValue(1)
				.setSaveConsumer(v -> t.primarySlot = v)
				.build());
			cat.addEntry(eb.startIntSlider(Component.literal("Secondary slot"), t.secondarySlot, 1, 9)
				.setDefaultValue(2)
				.setSaveConsumer(v -> t.secondarySlot = v)
				.build());
		}

		return builder.build();
	}
}
