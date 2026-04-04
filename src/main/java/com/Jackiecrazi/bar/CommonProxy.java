package com.Jackiecrazi.bar;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;

import org.apache.logging.log4j.LogManager;

import com.Jackiecrazi.bar.Items.Accessories;
import com.Jackiecrazi.bar.Items.ModItems;
import com.Jackiecrazi.bar.crafting.ModCrafting;
import com.Jackiecrazi.bar.dumbpackets.FirstMessage;
import com.Jackiecrazi.bar.dumbpackets.SecondMessage;
import com.Jackiecrazi.bar.entities.ModEntities;
import com.Jackiecrazi.bar.lenders.QuiverModTickHandler;
import com.Jackiecrazi.bar.quivering.QuiverGuiHandler;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

    public static Minecraft mc;
    public static QuiverModTickHandler overlayHandler;
    public static Item acc = new Accessories();

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {

        ModItems.itemify();
        ModEntities.init();
        int discriminator = 0;
        BAR.net.registerMessage(FirstMessage.Handler.class, FirstMessage.class, discriminator++, Side.SERVER);
        BAR.net.registerMessage(SecondMessage.Handler.class, SecondMessage.class, discriminator++, Side.SERVER);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCrafting.initCrafting();
        NetworkRegistry.INSTANCE.registerGuiHandler(BAR.inst, new QuiverGuiHandler());

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        if (Loader.isModLoaded("Baubles") && ConfigofJustice.baublesIntegration) {
            GameRegistry.registerItem(acc, "quiveraccessories");
            LogManager.getLogger("BAR")
                .debug("Yeap, baubles integration enabled");
        }
    }

    public void register() {}

    public void preInitRegister() {}

    public void onBowUse(EntityPlayer player, float frameTime) {}

    public void resetSavedFOV() {}

    public void spawnDiggingFX(World world, double x, double y, double z, double motionX, double motionY,
        double motionZ, Block block, int side, int metadata) {}

}
