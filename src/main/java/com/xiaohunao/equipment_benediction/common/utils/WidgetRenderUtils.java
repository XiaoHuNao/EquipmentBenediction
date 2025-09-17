package com.xiaohunao.equipment_benediction.common.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class WidgetRenderUtils {
    /**
     * 绘制一个边框
     *
     * @param g GuiGraphics实例，用于绘制
     * @param x 左上角X坐标
     * @param y 左上角Y坐标
     * @param width 矩形总宽度
     * @param height 矩形总高度
     * @param texture 边框纹理资源位置
     * @param srcX 纹理中边框起始X坐标
     * @param srcY 纹理中边框起始Y坐标
     * @param cornerSize 纹理中角落部分的大小
     * @param edgeThickness 纹理中边缘厚度
     * @param centerSize 纹理中中心部分的大小
     * @param sep 纹理中各部分之间的分隔线尺寸
     * @param texWidth 纹理总宽度
     * @param texHeight 纹理总高度
     * @param cornerTarget 渲染时角落的目标大小
     * @param edgeTarget 渲染时边缘的目标厚度
     */
    public static void drawNinePatchFrame(GuiGraphics g, int x, int y, int width, int height,
                                        ResourceLocation texture, int srcX, int srcY,
                                        int cornerSize, int edgeThickness, int centerSize, int sep,
                                        int texWidth, int texHeight, int cornerTarget, int edgeTarget) {
        if (width <= 0 || height <= 0) return;

        // 计算纹理坐标
        int u0 = srcX;
        int u1 = u0 + cornerSize;
        int u2 = u1 + sep;
        int u3 = u2 + centerSize;
        int u4 = u3 + sep;

        int v0 = srcY;
        int v1 = v0 + cornerSize;
        int v2 = v1 + sep;
        int v3 = v2 + centerSize;
        int v4 = v3 + sep;

        // 计算屏幕坐标
        int x0 = x;
        int x1 = x0 + cornerTarget;
        int x2 = x + width - cornerTarget;
        int y0 = y;
        int y1 = y0 + cornerTarget;
        int y2 = y + height - cornerTarget;

        // 计算中间部分的尺寸
        int topWidth = Math.max(0, width - cornerTarget * 2);
        int sideHeight = Math.max(0, height - cornerTarget * 2);

        // 绘制四个角落
        g.blit(texture, x0, y0, cornerTarget, cornerTarget, u0, v0, cornerSize, cornerSize, texWidth, texHeight);
        g.blit(texture, x2, y0, cornerTarget, cornerTarget, u4, v0, cornerSize, cornerSize, texWidth, texHeight);
        g.blit(texture, x0, y2, cornerTarget, cornerTarget, u0, v4, cornerSize, cornerSize, texWidth, texHeight);
        g.blit(texture, x2, y2, cornerTarget, cornerTarget, u4, v4, cornerSize, cornerSize, texWidth, texHeight);

        // 绘制上下边缘
        g.blit(texture, x1, y0, topWidth, edgeTarget, u2, v0, centerSize, edgeThickness, texWidth, texHeight);
        g.blit(texture, x1, y2, topWidth, edgeTarget, u2, v4, centerSize, edgeThickness, texWidth, texHeight);
        
        // 绘制左右边缘
        g.blit(texture, x0, y1, edgeTarget, sideHeight, u0, v2, edgeThickness, centerSize, texWidth, texHeight);
        g.blit(texture, x2, y1, edgeTarget, sideHeight, u4, v2, edgeThickness, centerSize, texWidth, texHeight);
    }

    /**
     * 绘制一个边框（简化版本，自动计算边框尺寸）
     *
     * @param g GuiGraphics实例，用于绘制
     * @param x 内部内容左上角X坐标
     * @param y 内部内容左上角Y坐标
     * @param contentWidth 内部内容宽度
     * @param contentHeight 内部内容高度
     * @param texture 边框纹理资源位置
     * @param srcX 纹理中边框起始X坐标
     * @param srcY 纹理中边框起始Y坐标
     * @param cornerSize 纹理中角落部分的大小
     * @param edgeThickness 纹理中边缘厚度
     * @param centerSize 纹理中中心部分的大小
     * @param sep 纹理中各部分之间的分隔线尺寸
     * @param texWidth 纹理总宽度
     * @param texHeight 纹理总高度
     * @param cornerTarget 渲染时角落的目标大小
     * @param edgeTarget 渲染时边缘的目标厚度
     */
    public static void drawNinePatchFrameForContent(GuiGraphics g, int x, int y, int contentWidth, int contentHeight,
                                                  ResourceLocation texture, int srcX, int srcY,
                                                  int cornerSize, int edgeThickness, int centerSize, int sep,
                                                  int texWidth, int texHeight, int cornerTarget, int edgeTarget) {
        if (contentWidth <= 0 || contentHeight <= 0) return;

        // 计算边框的总尺寸（内部内容 + 边框厚度）
        int totalWidth = contentWidth + cornerTarget * 2;
        int totalHeight = contentHeight + cornerTarget * 2;

        // 计算边框的起始坐标（内部内容坐标 - 边框厚度）
        int frameX = x - cornerTarget;
        int frameY = y - cornerTarget;

        // 调用原有的绘制方法
        drawNinePatchFrame(g, frameX, frameY, totalWidth, totalHeight,
                          texture, srcX, srcY,
                          cornerSize, edgeThickness, centerSize, sep,
                          texWidth, texHeight, cornerTarget, edgeTarget);
    }

}
