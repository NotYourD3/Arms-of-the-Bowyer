package com.Jackiecrazi.bar.Items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class MiscItems extends Item {

    public String[] names;
    public IIcon[] icons;

    public MiscItems() {
        super();
        names = new String[] { "sinew" };
        setHasSubtypes(true);
        setMaxDamage(0);
        setCreativeTab(CreativeTabs.tabMaterials);
    }

    @Override
    public int getItemStackLimit() {
        return 64;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        if (stack.getItemDamage() >= names.length) {
            return "";
        }

        return "item." + names[stack.getItemDamage()];
    }

    @Override
    public void registerIcons(IIconRegister iconRegister) {
        icons = new IIcon[names.length];

        for (int i = 0; i < icons.length; i++) {
            icons[i] = iconRegister.registerIcon("quivermod:" + names[i].toLowerCase());
        }
    }

    @Override
    public IIcon getIconFromDamage(int damage) {
        if (damage >= icons.length) {
            return icons[0];
        }

        return icons[damage];
    }

    public void getSubItems(Item itemID, CreativeTabs tabs, List list) {
        for (int i = 0; i < names.length; i++) {
            list.add(new ItemStack(itemID, 1, i));
        }
    }

}
