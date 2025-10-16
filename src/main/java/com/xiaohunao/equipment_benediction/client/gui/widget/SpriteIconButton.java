package com.xiaohunao.equipment_benediction.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.time.Duration;

public class SpriteIconButton extends Button {

    protected final ResourceLocation sprite;
    protected final int spriteWidth;
    protected final int spriteHeight;
    protected final int textureHeight;
    protected final int textureWidth;
    protected final int iconX;
    protected final int iconY;
    protected final float iconScale;
    protected final boolean centerIcon;
    protected final boolean enableHoverAnimation;
    protected final boolean enableClickAnimation;
    protected final boolean verticalFrames;

    // 动画相关
    private float hoverAnimation = 0.0f;
    private float clickAnimation = 0.0f;
    private long lastClickTime = 0;
    private boolean selected = false;
    private final java.util.Map<ButtonVisualState, Integer> stateToFrameIndex;

    public enum ButtonVisualState {
        NORMAL,
        HOVER,
        SELECTED,
        DISABLED,
        CLICKING
    }

    protected SpriteIconButton(Builder builder) {
        super(builder);
        this.sprite = builder.sprite;
        this.spriteWidth = builder.spriteWidth;
        this.spriteHeight = builder.spriteHeight;
        this.verticalFrames = builder.verticalFrames;
        // 纹理尺寸默认：
        // 竖向：宽等于精灵宽，高等于精灵高的2倍（常见：正常/悬停）
        // 横向：高等于精灵高，宽等于精灵宽的2倍
        int defaultTextureWidth = this.verticalFrames ? this.spriteWidth : this.spriteWidth * 2;
        int defaultTextureHeight = this.verticalFrames ? this.spriteHeight * 2 : this.spriteHeight;
        int computedTextureWidth = builder.textureWidth > 0 ? builder.textureWidth : defaultTextureWidth;
        int computedTextureHeight = builder.textureHeight > 0 ? builder.textureHeight : defaultTextureHeight;
        this.textureWidth = computedTextureWidth;
        this.textureHeight = computedTextureHeight;
        this.iconX = builder.iconX;
        this.iconY = builder.iconY;
        this.iconScale = builder.iconScale;
        this.centerIcon = builder.centerIcon;
        this.enableHoverAnimation = builder.enableHoverAnimation;
        this.enableClickAnimation = builder.enableClickAnimation;
        this.stateToFrameIndex = builder.stateToFrameIndex == null
                ? java.util.Collections.emptyMap()
                : java.util.Collections.unmodifiableMap(new java.util.EnumMap<>(builder.stateToFrameIndex));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.updateAnimations(partialTick);
        this.renderSpriteIcon(guiGraphics, mouseX, mouseY, partialTick);
    }

    protected void updateAnimations(float partialTick) {
        // 更新悬停动画
        if (this.enableHoverAnimation) {
            float targetHover = this.isHoveredOrFocused() ? 1.0f : 0.0f;
            this.hoverAnimation = Mth.lerp(partialTick * 0.1f, this.hoverAnimation, targetHover);
        }

        // 更新点击动画
        if (this.enableClickAnimation) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - this.lastClickTime < 200) { // 200ms 点击动画
                this.clickAnimation = 1.0f - (float) (currentTime - this.lastClickTime) / 200.0f;
            } else {
                this.clickAnimation = 0.0f;
            }
        }
    }

    protected void renderSpriteIcon(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.sprite == null) {
            return;
        }

        // 计算动画缩放
        float animationScale = 1.0f;
        if (this.enableHoverAnimation) {
            animationScale += this.hoverAnimation * 0.1f; // 悬停时放大10%
        }
        if (this.enableClickAnimation) {
            animationScale += this.clickAnimation * 0.2f; // 点击时放大20%
        }

        int iconRenderWidth = (int) (this.spriteWidth * this.iconScale * animationScale);
        int iconRenderHeight = (int) (this.spriteHeight * this.iconScale * animationScale);

        int renderIconX = this.getX() + this.iconX;
        int renderIconY = this.getY() + this.iconY;

        // 如果设置了居中，则计算居中位置
        if (this.centerIcon) {
            renderIconX = this.getX() + (this.width - iconRenderWidth) / 2;
            renderIconY = this.getY() + (this.height - iconRenderHeight) / 2;
        }

        // 根据按钮状态和可用帧数选择精灵图标的UV坐标（支持竖/横两种帧方向）
        int spriteU = 0;
        int spriteV = 0;
        if (this.verticalFrames) {
            int frameCount = Math.max(1, this.textureHeight / this.spriteHeight);
            int frameIndex = computeFrameIndex(frameCount);
            spriteV = frameIndex * this.spriteHeight;
        } else {
            int frameCount = Math.max(1, this.textureWidth / this.spriteWidth);
            int frameIndex = computeFrameIndex(frameCount);
            spriteU = frameIndex * this.spriteWidth;
        }

        // 渲染精灵图标：采样固定区域(spriteWidth x spriteHeight)，按动画尺寸绘制，避免放大时重复取样
        guiGraphics.blit(this.sprite, renderIconX, renderIconY, iconRenderWidth, iconRenderHeight, spriteU, spriteV, this.spriteWidth, this.spriteHeight, this.textureWidth, this.textureHeight);
    }

    private int computeFrameIndex(int frameCount) {
        // 优先使用自由映射
        if (this.stateToFrameIndex != null && !this.stateToFrameIndex.isEmpty()) {
            ButtonVisualState state = resolveVisualState();
            Integer mapped = this.stateToFrameIndex.get(state);
            if (mapped == null) {
                // 对未显式配置的状态做自然降级
                switch (state) {
                    case HOVER ->
                        mapped = this.stateToFrameIndex.get(ButtonVisualState.NORMAL);
                    case CLICKING -> {
                        mapped = this.stateToFrameIndex.get(ButtonVisualState.HOVER);
                        if (mapped == null) {
                            mapped = this.stateToFrameIndex.get(ButtonVisualState.NORMAL);
                        }
                    }
                    case SELECTED, DISABLED ->
                        mapped = this.stateToFrameIndex.get(ButtonVisualState.NORMAL);
                    default -> {
                    }
                }
            }
            if (mapped != null) {
                return Math.max(0, Math.min(frameCount - 1, mapped));
            }
            // 若仍为空则落回旧逻辑
        }
        if (frameCount >= 4) {
            // 0:normal, 1:hover, 2:selected, 3:clicking
            if (!this.active) {
                return 0;
            }
            if (this.selected) {
                return 2;
            }
            if (this.clickAnimation > 0.0f) {
                return 3;
            }
            return this.isHoveredOrFocused() ? 1 : 0;
        }

        if (frameCount == 3) {
            // 0:normal, 1:hover/focus, 2:disabled
            if (!this.active) {
                return 2;
            }
            return this.isHoveredOrFocused() ? 1 : 0;
        }

        if (frameCount == 2) {
            // 默认：0=normal, 1=hover；若需选中帧，请用 stateFrame 自由映射
            if (!this.active) {
                return 0;
            }
            return this.isHoveredOrFocused() ? 1 : 0;
        }

        // 只有1帧：恒为normal
        return 0;
    }

    private ButtonVisualState resolveVisualState() {
        if (!this.active) {
            return ButtonVisualState.DISABLED;
        }
        if (this.selected) {
            return ButtonVisualState.SELECTED;
        }
        if (this.clickAnimation > 0.0f) {
            return ButtonVisualState.CLICKING;
        }
        return this.isHoveredOrFocused() ? ButtonVisualState.HOVER : ButtonVisualState.NORMAL;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        if (this.enableClickAnimation) {
            this.lastClickTime = System.currentTimeMillis();
        }
    }

    @Override
    public void onPress() {
        super.onPress();
        this.setSelected(!this.isSelected());
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public static class Builder extends Button.Builder {

        protected ResourceLocation sprite;
        protected int spriteWidth = 0; // 0 表示未指定，使用按钮尺寸
        protected int spriteHeight = 0; // 0 表示未指定，使用按钮尺寸
        protected int textureWidth = 0;
        protected int textureHeight = 0;
        protected int iconX = 0;
        protected int iconY = 0;
        protected float iconScale = 1.0f;
        protected boolean centerIcon = true;
        protected boolean enableHoverAnimation = false;
        protected boolean enableClickAnimation = true;
        protected Tooltip tooltip;
        protected boolean autoSpriteSizeFromBounds = true;
        private Duration tooltipDelay;
        protected boolean defaultSelected = false;
        protected boolean verticalFrames = true;
        protected java.util.EnumMap<ButtonVisualState, Integer> stateToFrameIndex = new java.util.EnumMap<>(ButtonVisualState.class);

        public Builder(Component message, OnPress onPress) {
            super(message, onPress);
        }

        public Builder(OnPress onPress) {
            super(Component.empty(), onPress);
        }

        public Builder() {
            super(Component.empty(), button -> {
            });
        }

        public Builder sprite(ResourceLocation sprite, int spriteWidth, int spriteHeight) {
            this.sprite = sprite;
            this.spriteWidth = spriteWidth;
            this.spriteHeight = spriteHeight;
            this.autoSpriteSizeFromBounds = false;
            return this;
        }

        public Builder textureSize(int textureWidth, int textureHeight) {
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            return this;
        }

        public Builder sprite(ResourceLocation sprite) {
            this.sprite = sprite;
            return this;
        }

        public Builder spriteSize(int spriteWidth, int spriteHeight) {
            this.spriteWidth = spriteWidth;
            this.spriteHeight = spriteHeight;
            this.autoSpriteSizeFromBounds = false;
            return this;
        }

        /**
         * 设置帧方向：true=竖向帧（默认），false=横向帧。
         */
        public Builder verticalFrames(boolean vertical) {
            this.verticalFrames = vertical;
            return this;
        }

        public Builder iconPosition(int iconX, int iconY) {
            this.iconX = iconX;
            this.iconY = iconY;
            this.centerIcon = false;
            return this;
        }

        public Builder iconScale(float scale) {
            this.iconScale = scale;
            return this;
        }

        public Builder centerIcon(boolean center) {
            this.centerIcon = center;
            return this;
        }

        public Builder enableHoverAnimation(boolean enable) {
            this.enableHoverAnimation = enable;
            return this;
        }

        public Builder enableClickAnimation(boolean enable) {
            this.enableClickAnimation = enable;
            return this;
        }

        public Builder selected(boolean selected) {
            this.defaultSelected = selected;
            return this;
        }

        /**
         * 自由映射按钮可视状态到帧索引（0..frames-1）。未映射状态将回退到合理默认。
         */
        public Builder stateFrame(ButtonVisualState state, int frameIndex) {
            this.stateToFrameIndex.put(state, Math.max(0, frameIndex));
            return this;
        }

        @Override
        public Builder tooltip(Tooltip tooltip) {
            this.tooltip = tooltip;
            return this;
        }

        public Builder tooltip(Tooltip tooltip, Duration tooltipDelay) {
            this.tooltip = tooltip;
            this.tooltipDelay = tooltipDelay;
            return this;
        }

        @Override
        public Builder pos(int x, int y) {
            this.x = x;
            this.y = y;
            return this;
        }

        @Override
        public Builder bounds(int x, int y, int width, int height) {
            return this.pos(x, y).size(width, height);
        }

        @Override
        public Builder size(int width, int height) {
            this.width = width;
            this.height = height;
            return this;
        }

        @Override
        public SpriteIconButton build() {
            // 在构建前解析默认的精灵尺寸与纹理尺寸
            if (this.autoSpriteSizeFromBounds && (this.spriteWidth <= 0 || this.spriteHeight <= 0)) {
                if (this.width > 0 && this.height > 0) {
                    this.spriteWidth = this.width;
                    this.spriteHeight = this.height;
                }
            }

            // 若未显式设置纹理尺寸，且自定义状态映射包含最大帧索引，则据此推导纹理尺寸
            if ((this.textureWidth <= 0 || this.textureHeight <= 0) && this.spriteWidth > 0 && this.spriteHeight > 0 && this.stateToFrameIndex != null && !this.stateToFrameIndex.isEmpty()) {
                int maxIndex = 0;
                for (Integer idx : this.stateToFrameIndex.values()) {
                    if (idx != null && idx > maxIndex) {
                        maxIndex = idx;
                    }
                }
                int frames = maxIndex + 1;
                if (frames < 1) {
                    frames = 1;
                }
                if (this.verticalFrames) {
                    if (this.textureWidth <= 0) {
                        this.textureWidth = this.spriteWidth;
                    }
                    if (this.textureHeight <= 0) {
                        this.textureHeight = this.spriteHeight * frames;
                    }
                } else {
                    if (this.textureHeight <= 0) {
                        this.textureHeight = this.spriteHeight;
                    }
                    if (this.textureWidth <= 0) {
                        this.textureWidth = this.spriteWidth * frames;
                    }
                }
            }

            SpriteIconButton button = new SpriteIconButton(this);
            button.setSelected(this.defaultSelected);
            if (this.tooltip != null) {
                button.setTooltip(this.tooltip);
                if (this.tooltipDelay != null) {
                    button.setTooltipDelay(this.tooltipDelay);
                }
            }
            return button;
        }
    }
}
