package com.zeiro.attributeswap;

import com.zeiro.attributeswap.mixin.MultiPlayerGameModeAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

/**
 * Logika swap.
 *
 * Alur saat kamu klik kiri ke entity sambil pegang item trigger:
 *
 *  Langkah 0 (di dalam event serang, sebelum paket serang dikirim):
 *      pindah ke PRIMARY slot  -> serangan vanilla kamu keluar memakai item di slot ini
 *  Langkah 1 (setelah delayTicks):
 *      pindah ke SECONDARY slot
 *      kalau mode Stun Slam: serang target yang sama sekali lagi (serangan ke-2 pakai item secondary)
 *  Langkah 2 (setelah delayTicks lagi):
 *      balik ke slot awal
 */
public final class SwapController {
	private SwapController() {}

	/** true saat kita sendiri yang memicu serangan, supaya tidak memicu swap lagi. */
	private static boolean ownAttack = false;

	private static int stage = 0; // 0 = idle, 1 = menunggu langkah 1, 2 = menunggu langkah 2
	private static int wait = 0;
	private static int originalSlot = 0;
	private static int secondarySlot = 0;
	private static boolean stunSlam = false;
	private static Entity target = null;

	/** Dipanggil Fabric API tepat sebelum serangan ke entity dikirim (client). */
	public static InteractionResult onAttack(Player player, Level level, InteractionHand hand,
	                                         Entity entity, EntityHitResult hit) {
		if (ownAttack || stage != 0) return InteractionResult.PASS;
		if (!(player instanceof LocalPlayer)) return InteractionResult.PASS; // abaikan sisi server (singleplayer)

		SwapConfig cfg = SwapConfig.get();
		if (!cfg.enabled) return InteractionResult.PASS;

		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null || mc.gameMode == null) return InteractionResult.PASS;

		String held = BuiltInRegistries.ITEM.getKey(player.getMainHandItem().getItem()).toString();

		for (SwapConfig.Trigger t : cfg.triggers) {
			String id = t.normalizedId();
			if (id.isEmpty() || !id.equals(held)) continue;

			originalSlot = mc.player.getInventory().getSelectedSlot();
			secondarySlot = t.secondarySlot - 1;
			stunSlam = t.stunSlam;
			target = entity;

			selectSlot(mc, t.primarySlot - 1);

			stage = 1;
			wait = cfg.delayTicks;
			break;
		}
		return InteractionResult.PASS;
	}

	/** Dipanggil tiap akhir client tick. */
	public static void tick(Minecraft mc) {
		if (stage == 0) return;
		if (mc.player == null || mc.level == null || mc.gameMode == null) {
			reset();
			return;
		}
		if (wait > 0) {
			wait--;
			return;
		}

		SwapConfig cfg = SwapConfig.get();

		if (stage == 1) {
			selectSlot(mc, secondarySlot);

			Entity t = target;
			if (stunSlam && t != null && t.isAlive() && mc.player.distanceTo(t) <= 6.0F) {
				ownAttack = true;
				try {
					mc.gameMode.attack(mc.player, t);
					mc.player.swing(InteractionHand.MAIN_HAND);
				} finally {
					ownAttack = false;
				}
			}
			stage = 2;
			wait = cfg.delayTicks;
		} else {
			selectSlot(mc, originalSlot);
			reset();
		}
	}

	private static void selectSlot(Minecraft mc, int slot) {
		if (mc.player == null || mc.gameMode == null) return;
		if (slot < 0 || slot > 8) return;
		Inventory inv = mc.player.getInventory();
		if (inv.getSelectedSlot() == slot) return;
		inv.setSelectedSlot(slot);
		((MultiPlayerGameModeAccessor) mc.gameMode).attributeswap$sendSelectedSlot();
	}

	private static void reset() {
		stage = 0;
		wait = 0;
		target = null;
	}
}
