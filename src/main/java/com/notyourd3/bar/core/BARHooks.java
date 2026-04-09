package com.notyourd3.bar.core;

import com.Jackiecrazi.bar.Items.arrows.PotionArrow;
import com.Jackiecrazi.bar.entities.EntityQuiverModArrowNew;
import com.Jackiecrazi.bar.helpful.RepetitiveSnippets;
import com.notyourd3.bar.BowData;
import com.notyourd3.bar.BowDataManager;
import com.notyourd3.bar.helper.ArrowLookup;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BARHooks {
    private static final Random RANDOM = new Random();
    public static boolean onItemStackStopUsing(ItemStack stack, World world, EntityPlayer player, int timeLeft) {
        if (world.isRemote) return true; // 客户端不做逻辑

        if (stack == null || stack.getItem() == null) return false;

        String itemKey = Item.itemRegistry.getNameForObject(stack.getItem());
        if (!BowDataManager.getInstance().containsKey(itemKey)) return false;

        BowData data = BowDataManager.getInstance().getBowData(itemKey);

        // 1. 获取玩家当前使用的箭矢
        ArrowLookup.Result arrowResult = ArrowLookup.getArrow(player);
        if (arrowResult == null) {
            // 没有箭矢，无法射击
            return true; // 拦截原方法，避免原mod报错
        }

        // 2. 计算拉弓力度 (0.0 ~ 1.0)
        float power = getPower(stack, player, timeLeft, data.getPullbackMultiplier());
        if (power < 0.1f) {
            return true; // 拉弓不足，不射出
        }

        // 3. 获取附魔效果（从弓本身）
        int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
        int flameLevel = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
        boolean infinity = EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;

        // 4. 分裂箭数量（包含本体）
        int totalArrows = arrowResult.splitCount + 1;

        // 5. 循环射出每一支箭
        for (int rep = 0; rep < totalArrows; rep++) {
            // 基础速度 = power * 3.0f * 弓的速度倍率（如果有）
            float arrowSpeedMult = 1.0f; // 可从BowData扩展，目前没有，暂用1.0
            float velocity = power * 3.0f * arrowSpeedMult;

            EntityQuiverModArrowNew entity = new EntityQuiverModArrowNew(world, player, velocity);
            entity.setType(arrowResult.typeIdentifier);
            entity.setThrower(player);

            // 分裂箭的随机偏移（模仿原mod，非第一支箭增加随机散布）
            if (rep > 0) {
                applyRandomSpread(entity);
            }

            // 设置伤害基础值（原mod默认3.0，加上力量附魔）
            double damage = 3.0;
            if (powerLevel > 0) {
                damage += powerLevel * 0.5 + 0.5;
            }
            damage *= data.getDamageMultiplier(); // 弓自身的伤害倍率
            entity.setDamage(damage);

            // 击退
            if (punchLevel > 0) {
                entity.setKnockbackStrength(punchLevel);
            }

            // 火焰（弓自带火矢附魔，或者箭矢本身是火焰箭）
            if (flameLevel > 0) {
                entity.setFire(100);
            }
            if ("firearrow".equals(arrowResult.typeIdentifier)) {
                entity.setFire(10000);
            }

            // 暴击：拉满弓且不是分裂出来的箭（原mod逻辑）
            if (power >= 0.99f && rep == 0) {
                entity.setIsCritical(true);
            }

            // 药水箭特殊处理
            if (arrowResult.typeIdentifier.contains("potionarrow")) {
                applyPotionEffects(entity, arrowResult.arrowStack);
            }

            // 无限附魔：普通箭矢且非分裂箭时，标记不可拾取
            if (infinity && arrowResult.arrowStack.getItem() == Items.arrow && rep == 0) {
                entity.canBePickedUp = 2; // 2 = 只有创造模式可拾取
            }

            // 生成实体
            if (!world.isRemote) {
                world.spawnEntityInWorld(entity);
            }
        }

        // 6. 消耗箭矢（仅消耗一支，分裂箭也只消耗一支）
        if (!player.capabilities.isCreativeMode && !(infinity && arrowResult.arrowStack.getItem() == Items.arrow)) {
            ArrowLookup.consumeArrow(player, arrowResult);
        }

        // 7. 弓耐久损耗（每次射击减少1点）
        stack.damageItem(1, player);

        // 8. 播放音效
        float pitch = 1.0f / (RANDOM.nextFloat() * 0.4f + 1.2f) + power * 0.5f;
        world.playSoundAtEntity(player, "random.bow", 1.0f, pitch);

        player.addExhaustion(arrowResult.splitCount+1);

        return true; // 拦截原方法，完全替换
    }

    /**
     * 计算拉弓力度（0.0 ~ 1.0）
     * @param stack 弓
     * @param player 玩家
     * @param timeLeft 剩余使用刻
     * @param pullbackMult 弓的拉弓倍率（值越小拉得越快）
     */
    private static float getPower(ItemStack stack, EntityPlayer player, int timeLeft, float pullbackMult) {
        int maxUse = stack.getMaxItemUseDuration(); // 通常为72000
        int used = maxUse - timeLeft;
        float effectiveUsed = used / pullbackMult;
        float power = effectiveUsed / 20.0f; // 20刻 = 1秒拉满
        power = (power * power + power * 2.0f) / 3.0f;
        return Math.min(1.0f, Math.max(0.0f, power));
    }

    /**
     * 给分裂箭增加随机位置偏移
     */
    private static void applyRandomSpread(EntityQuiverModArrowNew entity) {
        double dx = (RANDOM.nextDouble() - 0.5) * 4;
        double dy = (RANDOM.nextDouble() - 0.5) * 2;
        double dz = (RANDOM.nextDouble() - 0.5) * 4;
        entity.setPosition(entity.posX + dx, entity.posY + dy, entity.posZ + dz);
    }

    /**
     * 应用药水箭的效果（从原mod的PotionArrow中读取药水效果）
     */
    private static void applyPotionEffects(EntityQuiverModArrowNew entity, ItemStack arrowStack) {
        if (arrowStack.getItem() instanceof PotionArrow) {
            List<PotionEffect> effects = Items.potionitem.getEffects(arrowStack);
            if (effects != null && !effects.isEmpty()) {
                PotionEffect first = effects.get(0);
                entity.setDuration(first.getDuration());
                entity.setPotency(first.getAmplifier());
                entity.setSpecialStuff(first.getPotionID());
            } else {
                // 如果没有效果（比如普通药水箭可能用metadata）
                entity.setDuration(1);
                entity.setPotency(0);
                entity.setSpecialStuff(arrowStack.getItemDamage());
            }
            entity.setDamage(3.0);
        }
    }
    public static boolean onItemRightClickHook(ItemStack stack, World world, EntityPlayer player) {
        if (stack != null && stack.getItem() != null) {
            String type = Item.itemRegistry.getNameForObject(stack.getItem());
            if (BowDataManager.getInstance().containsKey(type)) {
                ArrowLookup.Result result = ArrowLookup.getArrow(player);
                if(result != null) {
                    player.setItemInUse(stack,(int) (72000 * BowDataManager.getInstance().getBowData(type).getPullbackMultiplier()) + 5);
                    player.getEntityData().setString("BARusingArrow",result.typeIdentifier);
                    return true;
                }
            }
        }

        return false;
    }

}
