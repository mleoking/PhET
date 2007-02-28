/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                   *
 *                                                                              *
 *  Author, Miroslav Lazarevic                                                  *
 *                                                                              *
 *   For licensing information and credits, please refer to the                 *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                      *
 *                                                                              *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinSimpleButtonIndexModel;
import com.birosoft.liquid.util.Colors;

import java.awt.*;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.View;


/**
 * This class represents the UI delegate for the JTabbedPane component.
 *
 * @author Taoufik Romdhane
 */
public class LiquidTabbedPaneUI extends BasicTabbedPaneUI {
    static Skin skinTop;
    static Skin skinLeft;
    static Skin skinRight;
    static Skin skinBottom;
    static Skin skinBorder;
//    static Skin skinBorderRight;
//    static Skin skinBorderLeft;
//    static Skin skinBorderTop;
    static Skin skinBorderBottom;
    static Skin skinBorderVertical;
    SkinSimpleButtonIndexModel indexModel = new SkinSimpleButtonIndexModel();
    static BasicStroke focusStroke = new BasicStroke(1.0f,
            BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f,
            new float[] { 1.0f / 1.0f }, 1.0f);
    
    /**
     * The outer highlight color of the border.
     */

    //private Color outerHighlight = LiquidDefaultTheme.tabbedPaneBorderColor;

    /**
     * The inner highlight color of the border.
     */

    //private Color innerHighlight = Color.green;

    /**
     * The outer shadow color of the border.
     */

    //private Color outerShadow = Color.blue;

    /**
     * The inner shadow color of the border.
     */
    int rollover = -1;

    MouseMotionListener mouseMotionHandler = null;
    
    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c) {
        return new LiquidTabbedPaneUI();
    }
    
    protected void installListeners() {
        super.installListeners();
        mouseMotionHandler = new MyMouseHandler();
        tabPane.addMouseMotionListener(mouseMotionHandler);
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        tabPane.removeMouseMotionListener(mouseMotionHandler);
    }
    
// 20060804 MEV Applying Mihai's changes    
//    protected MouseListener createMouseListener() {
//        return new MyMouseHandler();
//    }

    /* We must implement a check ourselves since this method is private to BasicTabbedPaneUI.
     */
    protected boolean scrollableTabLayoutEnabled() {
        return (tabPane.getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT);
    }
    
    private void ensureCurrentLayout() {
        if (!tabPane.isValid()) {
            tabPane.validate();
        }

        /* If tabPane doesn't have a peer yet, the validate() call will
         * silently fail.  We handle that by forcing a layout if tabPane
         * is still invalid.  See bug 4237677.
         */
        if (!tabPane.isValid()) {
            TabbedPaneLayout layout = (TabbedPaneLayout) tabPane.getLayout();
            layout.calculateLayoutInfo();
        }
    }

    private int getTabAtLocation(int x, int y) {
        ensureCurrentLayout();

// 20060628 MEV - Removed this code and just delegate the call to tabForCoordinate
//                because of inconsistencies for SCROLLABLE LAYOUT.  Should be using
//                using this method anyway for Java 5  
        if( LiquidLookAndFeel.isPreTiger() ) {
            int tabCount = tabPane.getTabCount();

            for (int i = 0; i < tabCount; i++) {
                if (rects[i].contains(x, y)) {
                    return i;
                }
            }

            return -1;
        }
        return tabForCoordinate(tabPane, x, y);
    }

    protected JButton createScrollButton(int direction) {
        if (direction != SOUTH && direction != NORTH && direction != EAST &&
                                  direction != WEST) {
            throw new IllegalArgumentException("Direction must be one of: " +
                                               "SOUTH, NORTH, EAST or WEST");
        }
        return new LiquidScrollableTabButton(direction);
    }
    
    /**
     * Paints the backround of a given tab.
     *
     * @param g The graphics context.
     * @param tabPlacement The placement of the tab to paint.
     * @param tabIndex The index of the tab to paint.
     * @param x The x coordinate of the top left corner.
     * @param y The y coordinate of the top left corner.
     * @param w The width.
     * @param h The height.
     * @param isSelected True if the tab to paint is selected otherwise false.
     */
    protected void paintTabBackground(Graphics g, int tabPlacement,
        int tabIndex, int x, int y, int w, int h, boolean isSelected) {
    }
/*
    protected void paintFocusIndicator(Graphics g, int tabPlacement,
        Rectangle[] rects, int tabIndex, Rectangle iconRect,
        Rectangle textRect, boolean isSelected) {

        if( tabPane.hasFocus() && isSelected ) {
//            Rectangle r = iconRect;
//            r.add(textRect);//rects[tabIndex];

            BufferedImage focusImg;
            ImageIcon icon = LiquidLookAndFeel.loadIcon("comboboxfocus.png",this);
            focusImg = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
            Graphics g3 = focusImg.getGraphics();
            icon.paintIcon(tabPane, g3, 0, 0);

//            g.setColor(Color.black);
            Graphics2D g2d = (Graphics2D) g;
//            BasicStroke focusStroke=new BasicStroke(1.0f,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,1.0f, new float[] {1.0f, 1.0f}, 1.0f);
//            Rectangle r = new Rectangle(textRect);
//            r.x -= 1;
            //r.y -= 1;
//            r.width += 2;
            //r.height += 2;
            Skin skin = getSkinTop();
            skin.getImage()
            TexturePaint tp = new TexturePaint(focusImg, r);

            g2d.setPaint(tp);
        g2d.setStroke(focusStroke);
            
            g2d.draw(r);
        }
        
    }
*/
    /**
     * Paints the border of a given tab.
     *
     * @param g The graphics context.
     * @param tabPlacement The placement of the tab to paint.
     * @param selectedIndex The index of the selected tab.
     */
    protected void paintContentBorder(Graphics g, int tabPlacement,
        int selectedIndex) {

        int width = tabPane.getWidth();
        int height = tabPane.getHeight();
        Insets insets = tabPane.getInsets();
        Insets contentInsets = getContentBorderInsets(tabPlacement);
        
        int x = insets.left;
        int y = insets.top;
        int w = width - insets.right - insets.left;
        int h = height - insets.top - insets.bottom;

        switch (tabPlacement) {
            case LEFT:
                x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                w -= (x - insets.left);
                break;

            case RIGHT:
                w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                break;

            case BOTTOM:
                h -= (y - insets.top) + calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                break;
                
            case TOP:default:
                y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                h -= (y - insets.top);
        }

        Color oldColor = g.getColor();
        //102,186,255  //198, 214, 232 //177, 193, 211  //128,144,162
        Color inBorderClr = new Color(198, 214, 252); //blue inner border
        Color grayBorderClr = new Color(145, 155, 156); //darker gray border
        Color darkShadowClr = new Color(208, 206, 191); //darker shadow
        Color lightShadowClr = new Color(227, 224, 208); //lighter shadow

//        g.setColor(inBorderClr);

        if (tabPlacement == BOTTOM) {
            getSkinBorder().draw(g, 0, x, h - contentInsets.bottom, w, contentInsets.bottom);

// RIGHT            
            g.setColor(inBorderClr);
            g.drawLine((w-contentInsets.right), y + contentInsets.top - 2, (w-contentInsets.right), (y + h) - contentInsets.bottom-1);
            g.drawLine((w-contentInsets.right)+1, y + contentInsets.top - 2, (w-contentInsets.right)+1, (y + h) - contentInsets.bottom-1);
            g.setColor(grayBorderClr);
            g.drawLine((w-contentInsets.right)+2, y + contentInsets.top - 2, (w-contentInsets.right)+2, (y + h) - contentInsets.bottom-1);

// LEFT            
            g.setColor(grayBorderClr);
            g.drawLine(x+1, y + contentInsets.top - 2, x+1, (y + h) - contentInsets.bottom-1);
            g.setColor(inBorderClr);
            g.drawLine(x+2, y + contentInsets.top - 2, x+2, (y + h) - contentInsets.bottom-1);
            g.drawLine(x+3, y + contentInsets.top - 2, x+3, (y + h) - contentInsets.bottom-1);

// TOP      
            g.setColor(grayBorderClr);
            g.drawLine(x+1, y+1, w - contentInsets.right+2, y+1);
            g.setColor(inBorderClr);
            g.drawLine(x+2, y+2, w - contentInsets.right, y+2);
            g.drawLine(x+2, y+3, w - contentInsets.right, y+3);
        }

        if (tabPlacement == TOP) {
            getSkinBorder().draw(g, 0, x, y, w, 5);

// RIGHT    
            g.setColor(inBorderClr);
            g.drawLine((w-contentInsets.right), y + contentInsets.top, (w-contentInsets.right), (y + h) - contentInsets.bottom+1);
            g.drawLine((w-contentInsets.right)+1, y + contentInsets.top, (w-contentInsets.right)+1, (y + h) - contentInsets.bottom+1);
            g.setColor(grayBorderClr);
            g.drawLine((w-contentInsets.right)+2, y + contentInsets.top, (w-contentInsets.right)+2, (y + h) - contentInsets.bottom+1);

// LEFT
            g.setColor(grayBorderClr);
            g.drawLine(x+1, y + contentInsets.top, x+1, (y + h) - contentInsets.bottom+1);
            g.setColor(inBorderClr);
            g.drawLine(x+2, y + contentInsets.top, x+2, (y + h) - contentInsets.bottom+1);
            g.drawLine(x+3, y + contentInsets.top, x+3, (y + h) - contentInsets.bottom+1);

// BOTTOM            
            g.setColor(inBorderClr);
            g.drawLine(x + contentInsets.left, height - contentInsets.bottom, w - contentInsets.right - 1, height - contentInsets.bottom);
            g.drawLine(x + contentInsets.left, height - contentInsets.bottom+1, w - contentInsets.right - 1, height - contentInsets.bottom+1);

            g.setColor(grayBorderClr);
            g.drawLine(x+1, height - contentInsets.bottom+2, w - contentInsets.right+2, height - contentInsets.bottom+2);

            g.setColor(darkShadowClr);
            g.drawLine(x+1, height - contentInsets.bottom+3, w - contentInsets.right+2, height - contentInsets.bottom+3);
            g.drawLine(x+1, height - contentInsets.bottom+4, w - contentInsets.right+2, height - contentInsets.bottom+4);

            g.setColor(lightShadowClr);
            g.drawLine(x+1, height - contentInsets.bottom+5, w - contentInsets.right+2, height - contentInsets.bottom+5);
        }

        if (tabPlacement == RIGHT) {
            getSkinBorderVertical().draw(g, 0, (w - 5), y, 5, h);

// LEFT            
            g.setColor(grayBorderClr);
            g.drawLine(x+1, y + contentInsets.top - 2, x+1, (y + h) - contentInsets.bottom+1);
            g.setColor(inBorderClr);
            g.drawLine(x+2, y + contentInsets.top, x+2, (y + h) - contentInsets.bottom);
            g.drawLine(x+3, y + contentInsets.top, x+3, (y + h) - contentInsets.bottom);

// TOP      
            g.setColor(grayBorderClr);
            g.drawLine(x+1, y+1, w - contentInsets.right - 1, y+1);
            g.setColor(inBorderClr);
            g.drawLine(x+2, y+2, w - contentInsets.right - 1, y+2);
            g.drawLine(x+2, y+3, w - contentInsets.right - 1, y+3);

// BOTTOM      
            g.setColor(inBorderClr);
            g.drawLine(x + 2, height - contentInsets.bottom, w - contentInsets.right - 1, height - contentInsets.bottom);
            g.drawLine(x + 2, height - contentInsets.bottom+1, w - contentInsets.right - 1, height - contentInsets.bottom+1);

            g.setColor(grayBorderClr);
            g.drawLine(x + 1, height - contentInsets.bottom+2, w - contentInsets.right - 1, height - contentInsets.bottom+2);

            g.setColor(darkShadowClr);
            g.drawLine(x + 1, height - contentInsets.bottom+3, w - contentInsets.right - 1, height - contentInsets.bottom+3);
            g.drawLine(x + 1, height - contentInsets.bottom+4, w - contentInsets.right - 1, height - contentInsets.bottom+4);

            g.setColor(lightShadowClr);
            g.drawLine(x + 1, height - contentInsets.bottom+5, w - contentInsets.right - 1, height - contentInsets.bottom+5);
        }

        if (tabPlacement == LEFT) {
            getSkinBorderVertical().draw(g, 0, x, y, 5, h);

// RIGHT            
            g.setColor(inBorderClr);
            g.drawLine((width - contentInsets.right), y + contentInsets.top - 1, (width - contentInsets.right), (y + h) - contentInsets.bottom);
            g.drawLine((width - contentInsets.right)+1, y + contentInsets.top - 2, (width - contentInsets.right)+1, (y + h) - contentInsets.bottom+1);
            g.setColor(grayBorderClr);
            g.drawLine((width - contentInsets.right)+2, y + contentInsets.top - 3, (width - contentInsets.right)+2, (y + h) - contentInsets.bottom+2);

// TOP      
            g.setColor(grayBorderClr);
            g.drawLine(x + contentInsets.left, y+1, width - contentInsets.right+1, y+1);
            g.setColor(inBorderClr);
            g.drawLine(x + contentInsets.left, y+2, width - contentInsets.right, y+2);
            g.drawLine(x + contentInsets.left, y+3, width - contentInsets.right, y+3);

// BOTTOM      
            g.setColor(inBorderClr);
            g.drawLine(x + contentInsets.left, height - contentInsets.bottom, width - contentInsets.right, height - contentInsets.bottom);
            g.drawLine(x + contentInsets.left, height - contentInsets.bottom+1, width - contentInsets.right, height - contentInsets.bottom+1);

            g.setColor(grayBorderClr);
            g.drawLine(x + contentInsets.left, height - contentInsets.bottom+2, width - contentInsets.right+2, height - contentInsets.bottom+2);

            g.setColor(darkShadowClr);
            g.drawLine(x + contentInsets.left, height - contentInsets.bottom+3, width - contentInsets.right+1, height - contentInsets.bottom+3);
            g.drawLine(x + contentInsets.left, height - contentInsets.bottom+4, width - contentInsets.right+1, height - contentInsets.bottom+4);

            g.setColor(lightShadowClr);
            g.drawLine(x + contentInsets.left, height - contentInsets.bottom+5, width - contentInsets.right+1, height - contentInsets.bottom+5);
            
//            getSkinBorderBottom().draw(g, 0, x+5, height - contentInsets.bottom + 1, w + 1, 4);// - contentInsets.right, 5);
        }
        
        g.setColor(oldColor);
        
/*
        int width = tabPane.getWidth();
        int height = tabPane.getHeight();
        Insets insets = tabPane.getInsets();

        int x = insets.left;
        int y = insets.top;
        int w = width - insets.right - insets.left;
        int h = height - insets.top - insets.bottom;

        switch (tabPlacement) {
            case LEFT:
                x += calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);
                w -= (x - insets.left);

                break;

            case RIGHT:
                w -= calculateTabAreaWidth(tabPlacement, runCount, maxTabWidth);

                break;

            case BOTTOM:
            case TOP:default:
                y += calculateTabAreaHeight(tabPlacement, runCount, maxTabHeight);
                h -= (y - insets.top);
        }


        if (tabPlacement == BOTTOM) {
            getSkinBorder().draw(g, 0, x, h - 5, w, 5);
        }

        if (tabPlacement == TOP) {
            getSkinBorder().draw(g, 0, x, y, w, 5);
            getSkinBorderRight().draw(g, 0, (w - 5), y, 5, h);
            getSkinBorderBottom().draw(g, 0, x, h - 5, w, 5);
        }

        if (tabPlacement == RIGHT) {
            getSkinBorderVertical().draw(g, 0, (w - 5), y, 5, h);
        }

        if (tabPlacement == LEFT) {
            getSkinBorderVertical().draw(g, 0, x, y, 5, h);
        }
*/        
    }

    /**
     * Draws the border around each tab.
     *
     * @param g The graphics context.
     * @param tabPlacement The placement of the tabs.
     * @param tabIndex The index of the tab to paint.
     * @param x The x coordinate of the top left corner.
     * @param y The y coordinate of the top left corner.
     * @param w The width.
     * @param h The height.
     * @param isSelected True if the tab to paint is selected otherwise false.
     */
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
        int x, int y, int w, int h, boolean isSelected) {

// 20060505 MEV This shouldn't be done here as it would require the tabPane's 
// layout to be invalidated, which causes BUG #9         
/*        Insets contentBorderInsets = getContentBorderInsets(tabPlacement);

        if ((tabPane.getTabPlacement() == BOTTOM) &&
                (contentBorderInsets.top == 5)) {
            contentBorderInsets.top = 0;
            contentBorderInsets.bottom = 5;
            tabPane.revalidate();
        } else if ((tabPane.getTabPlacement() == TOP) &&
                (contentBorderInsets.top == 0)) {
            contentBorderInsets.top = 5;
            contentBorderInsets.bottom = 0;
            tabPane.revalidate();
        } else if ((tabPane.getTabPlacement() == LEFT) &&
                (contentBorderInsets.left == 0)) {
            contentBorderInsets.left = 5;
            contentBorderInsets.right = 0;
            tabPane.revalidate();
        } else if ((tabPane.getTabPlacement() == RIGHT) &&
                (contentBorderInsets.right == 0)) {
            contentBorderInsets.left = 0;
            contentBorderInsets.right = 5;
            tabPane.revalidate();
        }
*/
        //g.setColor(outerHighlight);
        int index = indexModel.getIndexForState(tabPane.isEnabledAt(tabIndex),
                rollover == tabIndex, isSelected);

        switch (tabPlacement) {
        case LEFT:
            getSkinLeft().draw(g, index, x, y, w, h - 1);

            break;

        case RIGHT:
            getSkinRight().draw(g, index, x, y, w, h - 1);

            break;

        case BOTTOM:
            getSkinBottom().draw(g, index, x, y, w, h);

            break;

        case TOP:default:
            getSkinTop().draw(g, index, x, y, w, h);
        }
    }

    protected void paintText(Graphics g, int tabPlacement, Font font,
        FontMetrics metrics, int tabIndex, String title, Rectangle textRect,
        boolean isSelected) {
        int yOffset = 0;

        if ((tabPlacement == TOP) && isSelected) {
            yOffset = 1;
        }

        if (tabPlacement == BOTTOM) {
            yOffset = isSelected ? (-2) : (-1);
        }

        g.setFont(font);

        View v = getTextViewForTab(tabIndex);

        if (v != null) {
            // html
            textRect.y += yOffset;
            v.paint(g, textRect);
        } else {
            // plain text
            int mnemIndex = tabPane.getDisplayedMnemonicIndexAt(tabIndex);

            if (tabPane.isEnabled() && tabPane.isEnabledAt(tabIndex)) {
                g.setColor(tabPane.getForegroundAt(tabIndex));
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, title,
                    mnemIndex, textRect.x,
                    textRect.y + metrics.getAscent() + yOffset);
            } else { // tab disabled
                g.setColor(tabPane.getBackgroundAt(tabIndex).brighter());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, title,
                    mnemIndex, textRect.x, textRect.y + metrics.getAscent());
                g.setColor(tabPane.getBackgroundAt(tabIndex).darker());
                BasicGraphicsUtils.drawStringUnderlineCharAt(g, title,
                    mnemIndex, textRect.x - 1,
                    (textRect.y + metrics.getAscent()) - 1);
            }
        }
    }

    public void paint(Graphics g, JComponent c) {
        int width = tabPane.getWidth();
        int height = tabPane.getHeight();
        Insets insets = tabPane.getInsets();

        int x = insets.left;
        int y = insets.top;
        int w = width - insets.right - insets.left;
        int h = height - insets.top - insets.bottom;

        int tabPlacement = tabPane.getTabPlacement();
        Insets contentInsets = getContentBorderInsets(tabPlacement);

        if (tabPlacement == BOTTOM) {
            Color oldColor = g.getColor();
            Color bg = LiquidLookAndFeel.getBackgroundColor();

            if (c.isOpaque()) {
                g.setColor(bg);
                g.fillRect(0, 0, c.getWidth(), c.getHeight());
            }

            if (LiquidLookAndFeel.areStipplesUsed()) {
                Colors.drawStipples(g, c, bg);
            }
            g.setColor(oldColor);
        }

        if (tabPlacement == TOP) {
            if (LiquidLookAndFeel.areStipplesUsed()) {
                c.setOpaque(false);
            }
        }
        super.paint(g, c);
    }

    public void update(Graphics g, JComponent c) {
        paint(g, c);
    }

    protected int getTabLabelShiftX(int tabPlacement, int tabIndex,
        boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        int nudge = 0;

        switch (tabPlacement) {
        case LEFT:
            nudge = isSelected ? (-1) : 1;

            break;

        case RIGHT:
            nudge = isSelected ? 1 : (-1);

            break;

        case BOTTOM:
        case TOP:default:
            nudge = 0;
        }

        return nudge;
    }

    protected int getTabLabelShiftY(int tabPlacement, int tabIndex,
        boolean isSelected) {
        Rectangle tabRect = rects[tabIndex];
        int nudge = 0;

        switch (tabPlacement) {
        case BOTTOM:
            nudge = isSelected ? 1 : (-1);

            break;

        case LEFT:
        case RIGHT:
            nudge = tabRect.height % 2;

            break;

        case TOP:default:
            nudge = isSelected ? (-1) : 1;
        }

        return nudge;
    }

    protected Insets getContentBorderInsets(int tabPlacement) {
        Insets contentBorderInsets = new Insets(4,4,4,4);

        switch(tabPlacement)
        {
            case TOP :
                contentBorderInsets.top = 5;
                contentBorderInsets.bottom = 6;
                break;
            case BOTTOM :
                contentBorderInsets.bottom = 6;
                break;
            case LEFT :
                contentBorderInsets.left = 5;
                contentBorderInsets.bottom = 6;
                break;
            case RIGHT :
                contentBorderInsets.right = 5;
                contentBorderInsets.bottom = 6;
                break;
        }
        
        return contentBorderInsets;
    }
    
    public Skin getSkinTop() {
        if (skinTop == null) {
            skinTop = new Skin("tabtop.png", 4, 7, 6, 7, 2);
        }

        return skinTop;
    }

    public Skin getSkinLeft() {
        if (skinLeft == null) {
            skinLeft = new Skin("tableft.png", 4, 6, 7, 2, 7);
        }

        return skinLeft;
    }

    public Skin getSkinRight() {
        if (skinRight == null) {
            skinRight = new Skin("tabright.png", 4, 2, 7, 6, 7);
        }

        return skinRight;
    }

    public Skin getSkinBottom() {
        if (skinBottom == null) {
            //skinBottom = new Skin("tabbottom.png", 4, 6, 7, 6, 7);
            skinBottom = new Skin("tabbottom.png", 4, 6, 7, 6, 2);
        }

        return skinBottom;
    }

    public Skin getSkinBorder() {
        if (skinBorder == null) {
            skinBorder = new Skin("tabborderh.png", 1, 5, 2, 5, 2);
        }

        return skinBorder;
    }

    public Skin getSkinBorderVertical() {
        if (skinBorderVertical == null) {
            skinBorderVertical = new Skin("tabborderv.png", 1, 2, 5, 2, 5);
        }

        return skinBorderVertical;
    }
/*
    public Skin getSkinBorderLeft() {
        if (skinBorderLeft == null) {
            skinBorderLeft = new Skin("tabborderleft.png", 1, 0, 5, 0, 5);
        }

        return skinBorderLeft;
    }

    public Skin getSkinBorderRight() {
        if (skinBorderRight == null) {
            skinBorderRight = new Skin("tabborderright.png", 1, 0, 5, 0, 5);
        }

        return skinBorderRight;
    }

    public Skin getSkinBorderTop() {
        if (skinBorderTop == null) {
            skinBorderTop = new Skin("tabbordertop.png", 1, 5, 0, 5, 0);
        }

        return skinBorderTop;
    }
*/    
    public Skin getSkinBorderBottom() {
        if (skinBorderBottom == null) {
            skinBorderBottom = new Skin("tabborderbottom.png", 1, 5, 0, 5, 0);
        }

        return skinBorderBottom;
    }

    private class LiquidScrollableTabButton extends LiquidScrollButton implements UIResource {
        public LiquidScrollableTabButton(int direction) {
            super(direction, 10, true);
            this.setEnabled(false);
        }
    }

    public class MyMouseHandler implements MouseListener, MouseMotionListener {
        public void mousePressed(MouseEvent e) {
            if (!tabPane.isEnabled()) {
                return;
            }

            int tabIndex = getTabAtLocation(e.getX(), e.getY());

            if ((tabIndex >= 0) && tabPane.isEnabledAt(tabIndex)) {
                if (tabIndex == tabPane.getSelectedIndex()) {
                    if (tabPane.isRequestFocusEnabled()) {
                        tabPane.requestFocus();
                        tabPane.repaint(getTabBounds(tabPane, tabIndex));
                    }
                } else {
                    tabPane.setSelectedIndex(tabIndex);
                }
            }
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
            if ((rollover != -1) && (rollover < tabPane.getTabCount())) {
                int oldRollover = rollover;
                rollover = -1;
                tabPane.repaint(getTabBounds(tabPane, oldRollover));
            }
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
        }

        public void mouseMoved(MouseEvent e) {
            if (tabPane == null) {
                return;
            }

            if (!tabPane.isEnabled()) {
                return;
            }

            int tabIndex = getTabAtLocation(e.getX(), e.getY());
            if ((tabIndex >= 0) && (tabIndex != rollover) && (rollover != -1)) { // Update old rollover

                if ((rollover >= 0) && (rollover < tabPane.getTabCount())) {
                    tabPane.repaint(getTabBounds(tabPane, rollover));
                }
            }

            if ((tabIndex >= 0) && tabPane.isEnabledAt(tabIndex) &&
                    (tabIndex < tabPane.getTabCount())) {
                if (tabIndex == rollover) { // Paint new rollover
                } else {
                    rollover = tabIndex;
                    tabPane.repaint(getTabBounds(tabPane, tabIndex));
                }
            } else if (tabIndex == -1 && ((rollover != -1) && (rollover < tabPane.getTabCount())) ) {
                int oldRollover = rollover;
                rollover = -1;
                tabPane.repaint(getTabBounds(tabPane, oldRollover));
            }
            
        }
    }
}
