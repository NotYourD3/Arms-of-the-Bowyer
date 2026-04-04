package com.Jackiecrazi.bar.Items.arrows;

import net.minecraft.item.ItemStack;

public interface IArrowItem {

    public int getInfinityChance();

    public boolean canAddArrowHead(ItemStack stack);

    public int getArrowHeadID(ItemStack stack);

    public void setArrowHeadID(ItemStack stack, int id);

}
