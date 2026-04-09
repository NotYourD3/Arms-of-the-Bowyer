package com.notyourd3.bar.helper;

import baubles.common.container.InventoryBaubles;
import baubles.common.lib.PlayerHandler;
import com.Jackiecrazi.bar.BAR;
import com.Jackiecrazi.bar.Items.ModItems;
import com.Jackiecrazi.bar.Items.arrows.ItemQuiverModArrow;
import com.Jackiecrazi.bar.Items.arrows.PotionArrow;
import com.Jackiecrazi.bar.helpful.InventorySlot;
import com.Jackiecrazi.bar.helpful.InventorySlots;
import com.Jackiecrazi.bar.helpful.RepetitiveSnippets;
import com.Jackiecrazi.bar.quivering.Quiver;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;


/**
 * 干净的箭矢查找工具（复用原mod的箭袋API）
 */
public class ArrowLookup {

    public static class Result {
        public final ItemStack arrowStack;      // 箭矢ItemStack（注意：箭袋内返回的是副本或引用）
        public final ArrowSource source;        // 来源类型
        public final int slotIndex;             // 槽位索引（背包/baubles槽位，箭袋内则为箭袋内部索引）
        public final int splitCount;            // 分裂数量（射出总箭数，含本体）
        public final String typeIdentifier;     // 箭矢类型标识
        public int quiverSlotIndex = -1;  // 箭袋物品所在的背包槽位，仅当 source=QUIVER 时有效

        public Result(ItemStack arrowStack, ArrowSource source, int slotIndex, int splitCount, String typeIdentifier) {
            this.arrowStack = arrowStack;
            this.source = source;
            this.slotIndex = slotIndex;
            this.splitCount = splitCount;
            this.typeIdentifier = typeIdentifier;
        }
    }

    public enum ArrowSource {
        MAIN_INVENTORY,
        BAUBLES,
        QUIVER          // 箭袋（原mod的Quiver物品）
    }

    /**
     * 获取玩家当前可用的箭矢
     * 优先级：箭袋 > 主背包 > Baubles
     */
    @Nullable
    public static Result getArrow(EntityPlayer player) {
        // 1. 尝试从箭袋获取
        Result quiverResult = getArrowFromQuiver(player);
        if (quiverResult != null) return quiverResult;

        // 2. 主背包

        return getArrowFromMainInventory(player);


    }

    /**
     * 使用原mod的 RepetitiveSnippets 获取箭袋中的箭矢
     */
    @Nullable
    private static Result getArrowFromQuiver(EntityPlayer player) {
        // 使用原mod的方法，它会返回当前激活的箭袋信息
        InventorySlots slots = RepetitiveSnippets.getArrowSlot(player);
        if (slots == null) return null;

        InventorySlot quiverSlot = slots.get("quiver");
        InventorySlot arrowSlot = slots.get("arrow");

        if (quiverSlot == null || arrowSlot == null) return null;

        ItemStack arrow = arrowSlot.stack;
        if (arrow == null || !isArrowItem(arrow)) return null;

        int split = getSplitCount(arrow);
        String typeId = getArrowTypeIdentifier(arrow);

        Result result = new Result(arrow, ArrowSource.QUIVER, arrowSlot.index, split, typeId);
        result.quiverSlotIndex = quiverSlot.index;  // 记录箭袋所在的背包槽位
        return result;
    }

    /**
     * 从主背包查找箭矢
     */
    @Nullable
    private static Result getArrowFromMainInventory(EntityPlayer player) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && isArrowItem(stack)) {
                int split = getSplitCount(stack);
                String typeId = getArrowTypeIdentifier(stack);
                return new Result(stack, ArrowSource.MAIN_INVENTORY, i, split, typeId);
            }
        }
        return null;
    }


    /**
     * 判断是否为箭矢（复用原mod的逻辑，但独立实现避免依赖）
     */
    private static boolean isArrowItem(ItemStack stack) {
        if (stack == null) return false;
        Item item = stack.getItem();
        return item == Items.arrow ||
                item == ModItems.DerpArrow ||
                item == ModItems.FireArrow ||
                item == ModItems.PotArrow ||
                item == ModItems.DrillArrow ||
                item == ModItems.EnderArrow ||
                item == ModItems.TimedArrow ||
                item == ModItems.ImpactArrow ||
                item == ModItems.TorchArrow;
    }

    /**
     * 获取分裂数量
     */
    private static int getSplitCount(ItemStack arrowStack) {
        if (arrowStack.getItem() instanceof PotionArrow) {
            return ((PotionArrow) arrowStack.getItem()).getSplittingArrowCount(arrowStack.getItemDamage());
        } else if (arrowStack.getItem() instanceof ItemQuiverModArrow) {
            return ((ItemQuiverModArrow) arrowStack.getItem()).getSplittingArrowCount(arrowStack.getItemDamage());
        }
        return 1;
    }

    /**
     * 获取类型标识字符串
     */
    private static String getArrowTypeIdentifier(ItemStack arrowStack) {
        Item item = arrowStack.getItem();
        if (item == Items.arrow) {
            return "arrow";
        } else if (item == ModItems.FireArrow) {
            return "firearrow";
        } else if (item == ModItems.PotArrow) {
            boolean isSplash = PotionArrow.isSplash(arrowStack.getItemDamage());
            return (isSplash ? "splash" : "") + ((PotionArrow) item).getName();
        } else if (item instanceof ItemQuiverModArrow) {
            return ((ItemQuiverModArrow) item).getName();
        }
        return item.getUnlocalizedName();
    }

    /**
     * 消耗一根箭矢
     * @param player 玩家
     * @param result 查找结果
     */
    public static void consumeArrow(EntityPlayer player, Result result) {
        if (result == null) return;
        if (player.capabilities.isCreativeMode) return;

        switch (result.source) {
            case MAIN_INVENTORY:
                ItemStack stack = player.inventory.mainInventory[result.slotIndex];
                if (stack != null) {
                    stack.stackSize--;
                    if (stack.stackSize <= 0) {
                        player.inventory.mainInventory[result.slotIndex] = null;
                    }
                }
                break;
            case QUIVER:
                // result.slotIndex 是箭袋内部类型索引
                // result.quiverInventorySlot 是箭袋物品所在的背包槽位
                ItemStack quiverStack = player.inventory.mainInventory[result.quiverSlotIndex];
                if (quiverStack != null) {
                    Quiver.removeArrow(player, quiverStack, result.quiverSlotIndex, result.slotIndex);
                }
                break;
        }
    }

    /**
     * 找到背包中第一个箭袋物品的槽位索引
     */
    private static int findQuiverInventorySlot(EntityPlayer player) {
        for (int i = 0; i < player.inventory.mainInventory.length; i++) {
            ItemStack stack = player.inventory.mainInventory[i];
            if (stack != null && stack.getItem() instanceof com.Jackiecrazi.bar.quivering.Quiver) {
                return i;
            }
        }
        return -1;
    }
}