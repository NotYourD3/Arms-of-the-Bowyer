package com.Jackiecrazi.bar;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;

import com.Jackiecrazi.bar.Items.ModItems;
import com.Jackiecrazi.bar.lenders.QuiverModOverlayRenderer;
import com.Jackiecrazi.bar.lenders.QuiverRenderPlayerBase;
import com.Jackiecrazi.bar.lenders.WeirdRenderBowThing;

import api.player.render.RenderPlayerAPI;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    public static void initLenders() {
        MinecraftForgeClient.registerItemRenderer(ModItems.Longbow, new WeirdRenderBowThing());
        MinecraftForgeClient.registerItemRenderer(ModItems.Recurve, new WeirdRenderBowThing());
        MinecraftForgeClient.registerItemRenderer(ModItems.Yumi, new WeirdRenderBowThing());
        MinecraftForgeClient.registerItemRenderer(ModItems.Comp, new WeirdRenderBowThing());

    }

    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);

        try {
            RenderPlayerAPI.register("quivermod", QuiverRenderPlayerBase.class);
        } catch (NoClassDefFoundError a) {
            BAR.log("RenderPlayer API not found. Quivers will not be rendered on players.");
        }

        makeSettingsGui();

        MinecraftForge.EVENT_BUS.register(new QuiverModOverlayRenderer());
        new BARBinds();
        mc = Minecraft.getMinecraft();
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }

    @Override
    public void postInit(FMLPostInitializationEvent e) {
        super.postInit(e);
    }

    public void makeSettingsGui() {
        /*
         * try
         * {
         * ModSettingScreen modSettingScreen = new ModSettingScreen("Better Bows Settings (R: restart, C: client)",
         * "Better Bows");
         * QuiverModSettings modSettings = new QuiverModSettings(QuiverMod.ID, modSettingScreen);
         * modSettingsContainer = new ContainModSettings(modSettings, modSettingScreen);
         * }
         * catch (NoClassDefFoundError e)
         * {
         * }
         */
    }
}
