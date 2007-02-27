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
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollBarUI;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinSimpleButtonIndexModel;

/**
 * This class represents the UI delegate for the JScrollBar component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidScrollBarUI extends BasicScrollBarUI
{
    private int orientation = -1;
    private int minExtent, minValue;
    private final int MIN_THUMB_SIZE = 14;
    public static final String FREE_STANDING_PROP = "JScrollBar.isFreeStanding";
    
    /**
     * The scrollbar's highlight color.
     */
    private static Color highlightColor;
    
    /**
     * The scrollbar's dark shadow color.
     */
    private static Color darkShadowColor;
    
    /**
     * The thumb's shadow color.
     */
    private static Color thumbShadow;
    
    /**
     * The thumb's highlight color.
     */
    private static Color thumbHighlightColor;
    
    /** true if thumb is in rollover state */
    protected boolean isRollover=false;
    /** true if thumb was in rollover state */
    protected boolean wasRollover=false;
    
    /**
     * The free standing property of this scrollbar UI delegate.
     */
    private boolean freeStanding = false;
    
    int scrollBarWidth;
    
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
    
    /** the thumb skin for this instance */
    private Skin skinThumb;
    private SkinSimpleButtonIndexModel skinThumbIndexModel=new SkinSimpleButtonIndexModel();
    
    /** the gripper skin for a vertical scrollbar */
    //static Skin skinGripperVert;
    /** the gripper skin for a horizontal scrollbar */
    //static Skin skinGripperHoriz;
    
    /** the gripper skin for this instance */
    //private Skin skinGripper;
    
    
    public LiquidScrollBarUI()
    {
    }
    
    /**
     * Installs some default values.
     * Initializes the metouia dots used for the thumb.
     */
    protected void installDefaults()
    {
        scrollBarWidth = LiquidScrollButton.getSkinUp().getHsize();
        super.installDefaults();
        scrollbar.setBorder(null);
    }
    
    
    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c)
    {
        return new LiquidScrollBarUI();
    }
    
    JButton decreaseButton, increaseButton;
    /**
     * Creates the decrease button of the scrollbar.
     *
     * @param orientation The button's orientation.
     * @return The created button.
     */
    protected JButton createDecreaseButton(int orientation)
    {
        decreaseButton = new LiquidScrollButton(orientation,  scrollBarWidth, freeStanding);
        return decreaseButton;
    }
    
    /**
     * Creates the increase button of the scrollbar.
     *
     * @param orientation The button's orientation.
     * @return The created button.
     */
    protected JButton createIncreaseButton(int orientation)
    {
        increaseButton = new LiquidScrollButton(orientation,  scrollBarWidth, freeStanding);
        return increaseButton;
    }
    
    /// From MetalUI
    
    public Dimension getPreferredSize(JComponent c)
    {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
        {
            return new Dimension(scrollBarWidth, scrollBarWidth * 3 + 10);
        } else // Horizontal
        {
            return new Dimension(scrollBarWidth * 3 + 10, scrollBarWidth);
        }
        
    }
    
    protected void layoutVScrollbar(JScrollBar sb)
    {
        Dimension sbSize = sb.getSize();
        Insets sbInsets = sb.getInsets();
        
        /*
         * Width and left edge of the buttons and thumb.
         */
        int itemW = sbSize.width - (sbInsets.left + sbInsets.right);
        int itemX = sbInsets.left;
        
        /* Nominal locations of the buttons, assuming their preferred
         * size will fit.
         */
        int decrButtonH = decrButton.getPreferredSize().height;
        int decrButtonY = sbInsets.top;
        
        int incrButtonH = incrButton.getPreferredSize().height;
        int incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
        
        /* The thumb must fit within the height left over after we
         * subtract the preferredSize of the buttons and the insets.
         */
        int sbInsetsH = sbInsets.top + sbInsets.bottom;
        int sbButtonsH = decrButtonH + incrButtonH;
        float trackH = sbSize.height - (sbInsetsH + sbButtonsH);
        
        /* Compute the height and origin of the thumb.   The case
         * where the thumb is at the bottom edge is handled specially
         * to avoid numerical problems in computing thumbY.  Enforce
         * the thumbs min/max dimensions.  If the thumb doesn't
         * fit in the track (trackH) we'll hide it later.
         */
        float min = sb.getMinimum();
        float extent = sb.getVisibleAmount();
        float range = sb.getMaximum() - min;
        float value = sb.getValue();
        
        int thumbH = (range <= 0)
        ? getMaximumThumbSize().height : (int)(trackH * (extent / range));
        thumbH = Math.max(thumbH, getMinimumThumbSize().height);
        thumbH = Math.min(thumbH, getMaximumThumbSize().height);
        // I need this to lock thumb height not to be smaller then 14
        if (thumbH < MIN_THUMB_SIZE)
            thumbH = MIN_THUMB_SIZE;
        
        int thumbY = incrButtonY - thumbH;
        if (sb.getValue() < (sb.getMaximum() - sb.getVisibleAmount()))
        {
            float thumbRange = trackH - thumbH;
            thumbY = (int)(0.5f + (thumbRange * ((value - min) / (range - extent))));
            thumbY +=  decrButtonY + decrButtonH;
        }
        
        /* If the buttons don't fit, allocate half of the available
         * space to each and move the lower one (incrButton) down.
         */
        int sbAvailButtonH = (sbSize.height - sbInsetsH);
        if (sbAvailButtonH < sbButtonsH)
        {
            incrButtonH = decrButtonH = sbAvailButtonH / 2;
            incrButtonY = sbSize.height - (sbInsets.bottom + incrButtonH);
        }
        decrButton.setBounds(itemX, decrButtonY, itemW, decrButtonH);
        incrButton.setBounds(itemX, incrButtonY, itemW, incrButtonH);
        
        /* Update the trackRect field.
         */
        int itrackY = decrButtonY + decrButtonH;
        int itrackH = incrButtonY - itrackY;
        trackRect.setBounds(itemX, itrackY, itemW, itrackH);
        
        /* If the thumb isn't going to fit, zero it's bounds.  Otherwise
         * make sure it fits between the buttons.  Note that setting the
         * thumbs bounds will cause a repaint.
         */
        if(thumbH >= (int)trackH)
        {
            setThumbBounds(0, 0, 0, 0);
        }
        else
        {
            if ((thumbY + thumbH) > incrButtonY)
            {
                thumbY = incrButtonY - thumbH;
            }
            if (thumbY  < (decrButtonY + decrButtonH))
            {
                thumbY = decrButtonY + decrButtonH + 1;
            }
            setThumbBounds(itemX, thumbY, itemW, thumbH);
        }
    }
    
    protected void layoutHScrollbar(JScrollBar sb)  
    {
        Dimension sbSize = sb.getSize();
        Insets sbInsets = sb.getInsets();
        
	/* Height and top edge of the buttons and thumb.
	 */
	int itemH = sbSize.height - (sbInsets.top + sbInsets.bottom);
	int itemY = sbInsets.top;

        boolean ltr = sb.getComponentOrientation().isLeftToRight();

        /* Nominal locations of the buttons, assuming their preferred
	 * size will fit.
	 */
        int leftButtonW = (ltr ? decrButton : incrButton).getPreferredSize().width; 
        int rightButtonW = (ltr ? incrButton : decrButton).getPreferredSize().width;        
        int leftButtonX = sbInsets.left;        
        int rightButtonX = sbSize.width - (sbInsets.right + rightButtonW);

        /* The thumb must fit within the width left over after we
	 * subtract the preferredSize of the buttons and the insets.
	 */
        int sbInsetsW = sbInsets.left + sbInsets.right;
        int sbButtonsW = leftButtonW + rightButtonW;
        float trackW = sbSize.width - (sbInsetsW + sbButtonsW);
        
        /* Compute the width and origin of the thumb.  Enforce
	 * the thumbs min/max dimensions.  The case where the thumb 
	 * is at the right edge is handled specially to avoid numerical 
	 * problems in computing thumbX.  If the thumb doesn't
	 * fit in the track (trackH) we'll hide it later.
	 */
        float min = sb.getMinimum();
        float max = sb.getMaximum();
        float extent = sb.getVisibleAmount();
        float range = max - min;
        float value = sb.getValue();

        int thumbW = (range <= 0) 
	    ? getMaximumThumbSize().width : (int)(trackW * (extent / range));
        thumbW = Math.max(thumbW, getMinimumThumbSize().width);
        thumbW = Math.min(thumbW, getMaximumThumbSize().width);
        // I need this to lock thumb height not to be smaller then 14
        if (thumbW < MIN_THUMB_SIZE)
            thumbW = MIN_THUMB_SIZE;
        
	int thumbX = ltr ? rightButtonX - thumbW : leftButtonX + leftButtonW;
	if (sb.getValue() < (max - sb.getVisibleAmount())) {
	    float thumbRange = trackW - thumbW;
            if( ltr ) {
                thumbX = (int)(0.5f + (thumbRange * ((value - min) / (range - extent))));
            } else {
                thumbX = (int)(0.5f + (thumbRange * ((max - extent - value) / (range - extent))));
            }
	    thumbX +=  leftButtonX + leftButtonW;
	}

        /* If the buttons don't fit, allocate half of the available 
         * space to each and move the right one over.
         */
        int sbAvailButtonW = (sbSize.width - sbInsetsW);
        if (sbAvailButtonW < sbButtonsW) {
            rightButtonW = leftButtonW = sbAvailButtonW / 2;
            rightButtonX = sbSize.width - (sbInsets.right + rightButtonW);
        }
        
        (ltr ? decrButton : incrButton).setBounds(leftButtonX, itemY, leftButtonW, itemH);
        (ltr ? incrButton : decrButton).setBounds(rightButtonX, itemY, rightButtonW, itemH);

	/* Update the trackRect field.
	 */	
	int itrackX = leftButtonX + leftButtonW;
	int itrackW = rightButtonX - itrackX;
	trackRect.setBounds(itrackX, itemY, itrackW, itemH);

	/* Make sure the thumb fits between the buttons.  Note 
	 * that setting the thumbs bounds causes a repaint.
	 */
	if (thumbW >= (int)trackW) {
	    setThumbBounds(0, 0, 0, 0);
	}
	else {
	    if (thumbX + thumbW > rightButtonX) {
		thumbX = rightButtonX - thumbW;
	    }
	    if (thumbX  < leftButtonX + leftButtonW) {
		thumbX = leftButtonX + leftButtonW + 1;
	    }
	    setThumbBounds(thumbX, itemY, thumbW, itemH);
	}
    }
    
    public void paint(Graphics g, JComponent c)
    {
        if (orientation == -1) orientation = scrollbar.getOrientation();
        Rectangle trackBounds=getTrackBounds();
        getSkinTrack().draw(g, 0,  trackBounds.x, trackBounds.y,  trackBounds.width, trackBounds.height);
        
        Rectangle thumbBounds=getThumbBounds();
        int index=skinThumbIndexModel.getIndexForState(c.isEnabled(),isRollover,isDragging);
        
        int x = orientation == JScrollBar.VERTICAL ? thumbBounds.x + 1 : thumbBounds.x;
        int y = orientation == JScrollBar.VERTICAL ? thumbBounds.y : thumbBounds.y + 1;
        int width = orientation == JScrollBar.VERTICAL ? thumbBounds.width - 2 : thumbBounds.width;
        int height = orientation == JScrollBar.VERTICAL ? thumbBounds.height : thumbBounds.height - 2;
        getSkinThumb().draw(g, index, x, y, width, height);
    }
    
    public boolean isThumbVisible()
    {
        if (scrollbar.getOrientation() == JScrollBar.VERTICAL)
        {
            if (getThumbBounds().height == 0)
                return false;
            else
                return true;
        } else
        {
            if (getThumbBounds().width == 0)
                return false;
            else
                return true;
        }
    }
    
    // From BasicUI
    protected TrackListener createTrackListener()
    {
        return new MyTrackListener();
    }
    
    /**
     * Basically does BasicScrollBarUI.TrackListener the right job, it just needs
     * an additional repaint and rollover management
     */
    protected class MyTrackListener extends BasicScrollBarUI.TrackListener
    {
        public void mouseReleased(MouseEvent e)
        {
            super.mouseReleased(e);
            scrollbar.repaint();
        }
        
        public void mousePressed(MouseEvent e)
        {
            super.mousePressed(e);
            scrollbar.repaint();
        }
        public void mouseEntered(MouseEvent e)
        {
            isRollover=false;
            wasRollover=false;
            if(getThumbBounds().contains(e.getX(), e.getY()))
            {
                isRollover=true;
            }
        }
        public void mouseExited(MouseEvent e)
        {
            isRollover=false;
            if (isRollover!=wasRollover)
            {
                scrollbar.repaint();
                wasRollover=isRollover;
            }
        }
        public void mouseDragged(MouseEvent e)
        {
            if(getThumbBounds().contains(e.getX(), e.getY()))
            {
                isRollover=true;
            }
            super.mouseDragged(e);
        }
        public void mouseMoved(MouseEvent e)
        {
            if(getThumbBounds().contains(e.getX(), e.getY()))
            {
                isRollover=true;
                if (isRollover!=wasRollover)
                {
                    scrollbar.repaint();
                    wasRollover=isRollover;
                }
            } else
            {
                isRollover=false;
                if (isRollover!=wasRollover)
                {
                    scrollbar.repaint();
                    wasRollover=isRollover;
                }
            }
        }
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
     * Returns the skinTrackHoriz.
     * @return Skin
     */
    public static Skin getSkinTrackHoriz()
    {
        if (skinTrackHoriz == null)
        {
            skinTrackHoriz = new Skin("scrollbartrackhoriz.png", 1, 7);
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
            skinTrackVert = new Skin("scrollbartrackvert.png", 1, 7);
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
            skinTrack = (scrollbar.getOrientation() == JScrollBar.VERTICAL) ? getSkinTrackVert() : getSkinTrackHoriz();
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
            skinThumb = (scrollbar.getOrientation() == JScrollBar.VERTICAL) ? getSkinThumbVert() : getSkinThumbHoriz();
        }
        return skinThumb;
    }
    
}
