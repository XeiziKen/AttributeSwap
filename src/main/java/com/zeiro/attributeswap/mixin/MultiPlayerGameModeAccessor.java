package com.zeiro.attributeswap.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * Membuka method private yang mengirim paket "ganti slot hotbar" ke server,
 * supaya paket slot terkirim tepat saat kita ganti slot (bukan menunggu tick berikutnya).
 */
@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {
	@Invoker("ensureHasSentCarriedItem")
	void attributeswap$sendSelectedSlot();
  }
