package net.greenjab.nekomasfixed.render.tab;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.PlainTextButton;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ModWorldGenOptionTab implements Tab {
    public static final Component TITLE = Component.translatable("config.worldgen.title");
    public static final Component EXTRA_NARRATION = Component.translatable("config.worldgen.narration");
    private final List<AbstractWidget> widgets = new ArrayList<>();

    @Override
    public Component getTabTitle() {
        return TITLE;
    }

    @Override
    public Component getTabExtraNarration() {
        return EXTRA_NARRATION;
    }

    @Override
    public void visitChildren(Consumer<AbstractWidget> consumer) {
        this.widgets.forEach(consumer);
    }

    @Override
    public void doLayout(ScreenRectangle screenRectangle) {
        this.widgets.clear();
        int startX = screenRectangle.left() + 20;
        int startY = screenRectangle.top() + 20;

        widgets.add(new PlainTextButton(20, 30, 30, 10, Component.literal("BUTTON"), new Button.OnPress() {
            @Override
            public void onPress(Button button) {
                System.out.println("Clicked");
            }
        } , Minecraft.getInstance().font));
    }

    @Override
    public Layout getLayout() {
        return new LinearLayout(100, 100, LinearLayout.Orientation.VERTICAL);
    }
}
