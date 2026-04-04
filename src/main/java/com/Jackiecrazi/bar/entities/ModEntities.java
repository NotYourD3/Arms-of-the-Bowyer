package com.Jackiecrazi.bar.entities;

import com.Jackiecrazi.bar.BAR;

import cpw.mods.fml.common.registry.EntityRegistry;

public class ModEntities {

    public static void init() {
        EntityRegistry
            .registerModEntity(EntityQuiverModArrowNew.class, "itsanarrownothingtoseehere", 1, BAR.inst, 64, 1, true);
    }
}
