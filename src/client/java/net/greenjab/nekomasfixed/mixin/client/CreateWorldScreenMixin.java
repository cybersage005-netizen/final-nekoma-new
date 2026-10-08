package net.greenjab.nekomasfixed.mixin.client;

import net.greenjab.nekomasfixed.render.tab.ModWorldGenOptionTab;
import net.minecraft.client.gui.components.tabs.MenuTabBar;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateWorldScreen.class)
public class CreateWorldScreenMixin {


    @Inject(method = "init", at = @At("TAIL"))
    private void injectCustomWorldGenTab(CallbackInfo ci) {
        CreateWorldScreen screen = (CreateWorldScreen)(Object)this;

        ModWorldGenOptionTab myTab = new ModWorldGenOptionTab();
        if (screen.tabNavigationBar != null) {
            screen.tabNavigationBar = MenuTabBar.builder(screen.tabManager, 100).addTab(myTab).build();
        }
    }
}
