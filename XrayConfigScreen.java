package com.example.xray;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

public class XrayConfigScreen extends Screen {
    private final Screen parent;
    private int page = 0;

    public XrayConfigScreen(Screen parent) {
        super(Component.literal("Xray Helper"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int left = cx - 155;
        int right = cx + 5;
        int w = 150;

        addRenderableWidget(Button.builder(
                Component.literal(page == 0 ? "> Geral <" : "Geral"),
                b -> { page = 0; rebuildWidgets(); }).bounds(left, 28, w, 20).build());
        addRenderableWidget(Button.builder(
                Component.literal(page == 1 ? "> Minérios <" : "Minérios"),
                b -> { page = 1; rebuildWidgets(); }).bounds(right, 28, w, 20).build());

        if (page == 0) {
            IntSlider range = new IntSlider(left, 56, w, 20, "Alcance", 16, 256, 16, XrayConfig.range,
                    v -> v + " blocos", v -> XrayConfig.range = v);
            range.active = !XrayConfig.unlimited;
            addRenderableWidget(range);

            addRenderableWidget(toggle(right, 56, w, "Ilimitado",
                    () -> XrayConfig.unlimited,
                    v -> XrayConfig.unlimited = v,
                    this::rebuildWidgets));

            addRenderableWidget(new IntSlider(left, 80, 310, 20, "Blocos escondidos", 0, 100, 5,
                    XrayConfig.hiddenVisibility, v -> v + "%", v -> XrayConfig.hiddenVisibility = v));

            addRenderableWidget(toggle(left, 104, w, "Água",
                    () -> XrayConfig.showWater, v -> XrayConfig.showWater = v, null));
            addRenderableWidget(toggle(right, 104, w, "Lava",
                    () -> XrayConfig.showLava, v -> XrayConfig.showLava = v, null));
            addRenderableWidget(toggle(left, 128, w, "Visão noturna",
                    () -> XrayConfig.nightVision, v -> XrayConfig.nightVision = v, null));
        } else {
            OreType[] types = OreType.values();
            for (int i = 0; i < types.length; i++) {
                OreType t = types[i];
                int x = (i % 2 == 0) ? left : right;
                int y = 56 + (i / 2) * 24;
                addRenderableWidget(toggle(x, y, w, t.label, () -> t.enabled, v -> t.enabled = v, null));
            }
        }

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(cx - 100, this.height - 28, 200, 20).build());
    }

    private static Component toggleText(String label, boolean on) {
        return Component.literal(label + ": ").append(
                Component.literal(on ? "ON" : "OFF").withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED));
    }

    private static Button toggle(int x, int y, int w, String label, BooleanSupplier get,
                                 Consumer<Boolean> set, Runnable after) {
        return Button.builder(toggleText(label, get.getAsBoolean()), b -> {
            set.accept(!get.getAsBoolean());
            b.setMessage(toggleText(label, get.getAsBoolean()));
            if (after != null) after.run();
        }).bounds(x, y, w, 20).build();
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        super.render(g, mouseX, mouseY, partialTick);
        g.drawCenteredString(this.font, this.title, this.width / 2, 12, 0xFFFFFF);
        if (page == 0) {
            g.drawCenteredString(this.font,
                    Component.literal("Blocos escondidos: 0% = invisíveis, 100% = normais (efeito pontilhado)"),
                    this.width / 2, 156, 0xAAAAAA);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    @Override
    public void removed() {
        XrayConfig.save();
        if (XrayState.enabled) XrayMod.refresh();
    }

    private static class IntSlider extends AbstractSliderButton {
        private final String label;
        private final int min, max, step;
        private final IntFunction<String> fmt;
        private final IntConsumer onChange;

        IntSlider(int x, int y, int w, int h, String label, int min, int max, int step, int value,
                  IntFunction<String> fmt, IntConsumer onChange) {
            super(x, y, w, h, Component.empty(), (double) (value - min) / (double) (max - min));
            this.label = label;
            this.min = min;
            this.max = max;
            this.step = step;
            this.fmt = fmt;
            this.onChange = onChange;
            updateMessage();
        }

        private int current() {
            int v = min + (int) Math.round(this.value * (max - min) / (double) step) * step;
            return Math.max(min, Math.min(max, v));
        }

        @Override
        protected void updateMessage() {
            if (fmt == null) return;
            setMessage(Component.literal(label + ": " + fmt.apply(current())));
        }

        @Override
        protected void applyValue() {
            onChange.accept(current());
        }
    }
}
