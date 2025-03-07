package com.xiaohunao.equipment_benediction.client.gui.screen.switcher;

import com.xiaohunao.equipment_benediction.EquipmentBenediction;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScrollUI {
    public static final ResourceLocation SCROLL_BAR = EquipmentBenediction.asResource("textures/gui/set_switcher/scroll_bar.png");
    public static final ResourceLocation SCROLL_BAR_SELECTED = EquipmentBenediction.asResource("textures/gui/set_switcher/scroll_bar_selected.png");

    private static final int SCROLL_BAR_WIDTH = 12;
    private static final int SCROLL_BAR_HEIGHT = 15;
    private static final int SCROLL_AREA_HEIGHT = 70;
    
    private float scrollOffset = 0.0F;
    private boolean scrolling = false;
    private final int leftPos;
    private final int topPos;

    public ScrollUI(int leftPos, int topPos) {
        this.leftPos = leftPos;
        this.topPos = topPos;
    }

    public void renderScrollBar(GuiGraphics guiGraphics) {
        int scrollBarX = leftPos + 103;
        int scrollBarY = topPos + 8;
        
        int scrollBarPosition = (int) (scrollBarY + scrollOffset * (SCROLL_AREA_HEIGHT - SCROLL_BAR_HEIGHT));
        guiGraphics.blit(
            scrolling ? SCROLL_BAR_SELECTED : SCROLL_BAR,
            scrollBarX, scrollBarPosition,
            0, 0,
            SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT,
            SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT
        );
    }

    public float getScrollOffset() {
        return scrollOffset;
    }

    public void setScrollOffset(float offset) {
        this.scrollOffset = Mth.clamp(offset, 0.0F, 1.0F);
    }

    public boolean isScrolling() {
        return scrolling;
    }

    public void setScrolling(boolean scrolling) {
        this.scrolling = scrolling;
    }

    public int getScrollBarWidth() {
        return SCROLL_BAR_WIDTH;
    }

    public int getScrollBarHeight() {
        return SCROLL_BAR_HEIGHT;
    }

    public int getScrollAreaHeight() {
        return SCROLL_AREA_HEIGHT;
    }
} 