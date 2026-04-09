package com.notyourd3.bar.core;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.7.10")
public class BARLoadingPlugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{"com.notyourd3.bar.core.BARTransformer"};
    }

    @Override
    public String getModContainerClass() {
        return "com.notyourd3.bar.core.BARModContainer";
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
