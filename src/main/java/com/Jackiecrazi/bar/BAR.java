package com.Jackiecrazi.bar;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import com.Jackiecrazi.bar.Items.ModItems;
import com.Jackiecrazi.bar.Items.arrows.ItemQuiverModArrow;
import com.Jackiecrazi.bar.Items.arrows.PotionArrow;
import com.Jackiecrazi.bar.lenders.QuiverModTickHandler;
import com.Jackiecrazi.bar.quivering.Quiver;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = "BetterArcheryReborn", version = BAR.MODVER, name = "Better Archery Reborn")
public class BAR {

    public static final String MODID = "BetterArcheryReborn";
    public static final String MODVER = "0.0";
    @Mod.Instance("BetterArcheryReborn")
    public static BAR inst;
    public static boolean BaublesLoaded;
    public static SimpleNetworkWrapper net;
    @SidedProxy(clientSide = "com.Jackiecrazi.bar.ClientProxy", serverSide = "com.Jackiecrazi.bar.ServerProxy")
    public static CommonProxy proxy;
    public static CreativeTabs BARBow = new CreativeTabs("BABow") {

        @Override
        public Item getTabIconItem() {
            return ModItems.Longbow;
        }
    };
    public static CreativeTabs BARArrow = new CreativeTabs("BAArrow") {

        @Override
        public Item getTabIconItem() {
            return Items.arrow;
        }
    };
    BAREventHandler eh = new BAREventHandler();
    QuiverModTickHandler h = new QuiverModTickHandler();

    public static void log(String string) {
        // TODO Auto-generated method stub
        System.out.println(string);
    }

    public static boolean isArrow(Item item) {
        return item == Items.arrow || item instanceof ItemQuiverModArrow || item instanceof PotionArrow
        // ||item==ModItems.quiver
        ;
    }

    public static boolean isArrow(ItemStack itemStack) {
        if ((itemStack.getItem() == ModItems.quiver && Quiver.getArrowCount(itemStack) > 0)) {
            return true;
        }
        return isArrow(itemStack.getItem());
    }

    @EventHandler
    public void preinit(FMLPreInitializationEvent event) {
        ConfigofJustice.CreatioExNihilo(event.getSuggestedConfigurationFile());
        net = NetworkRegistry.INSTANCE.newSimpleChannel("BARnet");
        this.proxy.preInit(event);
        FMLCommonHandler.instance()
            .bus()
            .register(h);
        MinecraftForge.EVENT_BUS.register(h);
        FMLCommonHandler.instance()
            .bus()
            .register(eh);
        MinecraftForge.EVENT_BUS.register(eh);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        this.proxy.init(event);
        ClientProxy.initLenders();
    }

    @EventHandler
    public void postinit(FMLPostInitializationEvent event) {
        this.proxy.postInit(event);
        BaublesLoaded = Loader.isModLoaded("Baubles");
    }
}
