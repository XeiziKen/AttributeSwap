package com.zeiro.attributeswap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/** Config disimpan di .minecraft/config/attributeswap.json */
public class SwapConfig {
	public static final int TRIGGER_COUNT = 3;

	public static class Trigger {
		/** Item ID pemicu, mis. "minecraft:netherite_axe". Kosong = trigger mati. */
		public String itemId = "";
		/** true = Stun Slam (serang 2x: primary lalu secondary). false = Attribute Swap. */
		public boolean stunSlam = false;
		/** Slot hotbar 1-9 */
		public int primarySlot = 1;
		/** Slot hotbar 1-9 */
		public int secondarySlot = 2;

		public Trigger() {}

		public Trigger(String itemId, boolean stunSlam, int primarySlot, int secondarySlot) {
			this.itemId = itemId;
			this.stunSlam = stunSlam;
			this.primarySlot = primarySlot;
			this.secondarySlot = secondarySlot;
		}

		public String normalizedId() {
			String id = itemId == null ? "" : itemId.trim().toLowerCase();
			if (id.isEmpty()) return "";
			return id.contains(":") ? id : "minecraft:" + id;
		}
	}

	public boolean enabled = true;
	/** Jeda (tick) antar langkah swap. 0 = secepat mungkin. */
	public int delayTicks = 1;
	public Trigger[] triggers = new Trigger[] {
		// Sama seperti contoh di video
		new Trigger("minecraft:netherite_axe", true, 5, 1),
		new Trigger("minecraft:wind_charge", false, 3, 2),
		new Trigger("", false, 1, 2)
	};

	// ---------------------------------------------------------------

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static SwapConfig instance;

	private static Path path() {
		return FabricLoader.getInstance().getConfigDir().resolve("attributeswap.json");
	}

	public static SwapConfig get() {
		if (instance == null) load();
		return instance;
	}

	public static void load() {
		SwapConfig loaded = null;
		Path p = path();
		if (Files.exists(p)) {
			try (Reader r = Files.newBufferedReader(p)) {
				loaded = GSON.fromJson(r, SwapConfig.class);
			} catch (Exception e) {
				AttributeSwapClient.LOGGER.warn("Gagal baca config, pakai default", e);
			}
		}
		instance = loaded != null ? loaded : new SwapConfig();
		instance.sanitize();
		if (loaded == null) save();
	}

	public static void save() {
		SwapConfig cfg = get();
		cfg.sanitize();
		try (Writer w = Files.newBufferedWriter(path())) {
			GSON.toJson(cfg, w);
		} catch (IOException e) {
			AttributeSwapClient.LOGGER.warn("Gagal simpan config", e);
		}
	}

	private void sanitize() {
		if (triggers == null) triggers = new Trigger[0];
		Trigger[] fixed = new Trigger[TRIGGER_COUNT];
		for (int i = 0; i < TRIGGER_COUNT; i++) {
			Trigger t = i < triggers.length && triggers[i] != null ? triggers[i] : new Trigger();
			if (t.itemId == null) t.itemId = "";
			t.primarySlot = Math.max(1, Math.min(9, t.primarySlot));
			t.secondarySlot = Math.max(1, Math.min(9, t.secondarySlot));
			fixed[i] = t;
		}
		triggers = fixed;
		delayTicks = Math.max(0, Math.min(10, delayTicks));
	}
}
