/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package com.birosoft.liquid;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinImageCache;

/**
 * This class represents the UI delegate for the JProgressBar component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidProgressBarUI extends BasicProgressBarUI {

    /**
     * The skin that paint the progress bar if it's a horizontal one
     */
    static Skin skinHorizontal;
    /**
     * The skin that paint the progress bar if it's a vertical one
     */
    static Skin skinVertical;

    /** the track for a vertical scrollbar */
    private static Skin skinTrackVert;
    /** the track for a horizontal scrollbar */
    private static Skin skinTrackHoriz;
    
    /** The skin for the track for this instance */
    private Skin skinTrack;
    
    
    /** the thumb for a vertical scrollbar */
    private static Skin skinThumbVert;
    /** the thumb for a horizontal scrollbar */
    private static Skin skinThumbHoriz;

    /** the disabled thumb for a vertical scrollbar */
    private static Skin disabledSkinThumbVert;
    /** the disabled thumb for a horizontal scrollbar */
    private static Skin disabledSkinThumbHoriz;
    
    /** the thumb skin for this instance */
    private Skin skinThumb;
    
        /*
         * The offset of the filled bar. This amount of space will be added on the left and right of the progress bar
         * to its borders.
         */
    int offset=3;
    
    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c) {
        return new LiquidProgressBarUI();
    }
    
    protected void paintDeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }
        int index = 0;//progressBar.isEnabled() ? 0 : 1;
        
        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);

        Graphics2D g2 = (Graphics2D) g;
        
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
            
            getSkinTrack().draw(g, 0, 0, 0, barRectWidth, barRectHeight); // draw border
            
            Paint p=g2.getPaint();
            
            if (amountFull > 10) {
                getSkinThumb().draw(g, index, 0, 0, amountFull, barRectHeight);
            }
            
            // Deal with possible text painting
            if (progressBar.isStringPainted()) {
                g.setColor(Color.black);
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
            }

            g2.setPaint(p);
            
        } else { // VERTICAL
            int amountFull = getAmountFull(b, barRectWidth, barRectHeight);
            
            getSkinTrack().draw(g, 0, 0, 0, barRectWidth, barRectHeight); // draw border
            
            Paint p=g2.getPaint();

            if (amountFull > 10) {
                getSkinThumb().draw(g, index, 0, barRectHeight-amountFull, barRectWidth, amountFull);
            }
            
            // Deal with possible text painting
            if (progressBar.isStringPainted()) {
                g.setColor(Color.black);
                paintString(g, b.left, b.top, barRectWidth, barRectHeight, amountFull, b);
            }
            
            g2.setPaint(p);
        }
    }
    
    protected void paintIndeterminate(Graphics g, JComponent c) {
        if (!(g instanceof Graphics2D)) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;

        int index = 0;//progressBar.isEnabled() ? 0 : 1;

        Insets b = progressBar.getInsets(); // area for border
        int barRectWidth = progressBar.getWidth() - (b.right + b.left);
        int barRectHeight = progressBar.getHeight() - (b.top + b.bottom);
        
        Rectangle boxRect = getBox(null);
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            getSkinTrack().draw(g, 0, 0, 0, barRectWidth, barRectHeight); // draw border

            g.translate(boxRect.x,boxRect.y);
            
            getSkinThumb().draw(g, index, 0, 0, boxRect.width, barRectHeight);
            
            g.translate(-boxRect.x, -boxRect.y);
            
        } else {
            getSkinTrack().draw(g, 0, 0, 0, barRectWidth, barRectHeight); // draw border
            
            g.translate(boxRect.x,boxRect.y);

            getSkinThumb().draw(g, index, barRectWidth, boxRect.height);
            
            g.translate(-boxRect.x, -boxRect.y);
        }
    }
    
    
    /**
     * @see javax.swing.plaf.ComponentUI#update(java.awt.Graphics, javax.swing.JComponent)
     */
    public void update(Graphics g, JComponent c) {
        paint(g, c);
    }
    
    protected void installDefaults() {
	LiquidLookAndFeel.installColorsAndFont(progressBar,
					 "ProgressBar.background",
					 "ProgressBar.foreground",
					 "ProgressBar.font");
    }
    
    
    protected Dimension getPreferredInnerHorizontal() {
        Dimension horizDim = (Dimension) UIManager.get("ProgressBar.horizontalSize");
        if (horizDim == null) {
            horizDim = new Dimension(146, 14);
        }
        return horizDim;
    }
    
    protected Dimension getPreferredInnerVertical() {
        Dimension vertDim = (Dimension) UIManager.get("ProgressBar.vertictalSize");
        if (vertDim == null) {
            vertDim = new Dimension(14, 146);
        }
        return vertDim;
    }
    
    
    /**
     * Returns the skinThumbHoriz.
     * @return SkinInfoButton
     */
    public static Skin getSkinThumbHoriz()
    {
        if (skinThumbHoriz == null)
        {
            skinThumbHoriz = new Skin("scrollbarthumbhoriz.png", 4, 8, 6, 8, 8);
        }
        return skinThumbHoriz;
    }
    
    /**
     * Returns the skinThumbVert.
     * @return SkinInfoButton
     */
    public static Skin getSkinThumbVert()
    {
        if (skinThumbVert == null)
        {
            skinThumbVert = new Skin("scrollbarthumbvert.png", 4, 6, 8, 8, 7);
        }
        return skinThumbVert;
    }

    /**
     * Returns the disabledSkinThumbHoriz.
     * @return SkinInfoButton
     */
    public static Skin getDisabledSkinThumbHoriz()
    {
        if (disabledSkinThumbHoriz == null)
        {
            disabledSkinThumbHoriz = new Skin("scrollbarthumbhoriz.png", 4, 8, 6, 8, 8);
            disabledSkinThumbHoriz.colourImage();
        }
        return disabledSkinThumbHoriz;
    }
    
    /**
     * Returns the disabledSkinThumbVert.
     * @return SkinInfoButton
     */
    public static Skin getDisabledSkinThumbVert()
    {
        if (disabledSkinThumbVert == null)
        {
            disabledSkinThumbVert = new Skin("scrollbarthumbvert.png", 4, 6, 8, 8, 7);
            disabledSkinThumbVert.colourImage();
        }
        return disabledSkinThumbVert;
    }
    
    /**
     * Returns the skinTrackHoriz.
     * @return Skin
     */
    public static Skin getSkinTrackHoriz()
    {
        if (skinTrackHoriz == null)
        {
            skinTrackHoriz = new Skin("progressbartrackhoriz.png", 1, 7);
        }
        return skinTrackHoriz;
    }
    
    /**
     * Returns the skinTrackVert.
     * @return Skin
     */
    public static Skin getSkinTrackVert()
    {
        if (skinTrackVert == null)
        {
            skinTrackVert = new Skin("progressbartrackvert.png", 1, 7);
        }
        return skinTrackVert;
    }
    
    /**
     * Returns the skinTrack.
     * @return Skin
     */
    public Skin getSkinTrack()
    {
        if (skinTrack == null)
        {
            skinTrack = (progressBar.getOrientation() == JProgressBar.VERTICAL) ? getSkinTrackVert() : getSkinTrackHoriz();
        }
        return skinTrack;
    }
    
    /**
     * Returns the skinThumb.
     * @return Skin
     */
    public Skin getSkinThumb()
    {
        if (skinThumb == null)
        {
            if (progressBar.isEnabled()) {
                skinThumb = (progressBar.getOrientation() == JProgressBar.VERTICAL) ? getSkinThumbVert() : getSkinThumbHoriz();
            } else {
                skinThumb = (progressBar.getOrientation() == JProgressBar.VERTICAL) ? getDisabledSkinThumbVert() : getDisabledSkinThumbHoriz();
            }
        }
        return skinThumb;
    }
    
}
