# Auto AttributeSwap & StunSlam (Fabric 1.21.11)

Client-side mod. Butuh: Fabric Loader, Fabric API, Cloth Config API.

Buka setting: ketik `/attributeswap` di chat.

## Cara kerja
Klik kiri ke entity sambil pegang item trigger:
1. Pindah ke Primary slot, serangan kamu keluar dari slot itu
2. Setelah delay: pindah ke Secondary slot
   (mode Stun Slam: serang target yang sama sekali lagi)
3. Setelah delay: balik ke slot awal

Config: `.minecraft/config/attributeswap.json`
Default: Trigger 1 = netherite_axe (Stun Slam, 5 -> 1), Trigger 2 = wind_charge (Attribute Swap, 3 -> 2).
