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

import com.birosoft.liquid.util.LiquidUtilities;
import com.sun.java.swing.SwingUtilities2;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;


/**
 * This class represents the title pane for the JInternalFrame components.
 *
 * @author Taoufik Romdhane
 */
public class LiquidInternalFrameTitlePane extends BasicInternalFrameTitlePane
    implements LayoutManager {
    static LiquidWindowButtonUI iconButtonUI;
    static LiquidWindowButtonUI maxButtonUI;
    static LiquidWindowButtonUI closeButtonUI;
    protected boolean isPalette = false;
    protected Icon paletteCloseIcon;
    protected int paletteTitleHeight;

    /**
     * Color for the title in a normal sized internal frame
     */
    Color normalTitleColor = Color.white;

    /**
     * Color for the shadow of the title in a normal sized internal frame
     */
    Color shadowColor = new Color(10, 24, 131);

    /**
     * Color for the title in a normal sized internal frame that is not enabled
     */
    Color disabledTitleColor = new Color(64, 63, 63);

    /**
     * The frame's title height, read from the UIDefaults table.
     */
    protected int frameTitleHeight;

    /**
     * The buttons width, calculated at runtime.
     */
    private int buttonsWidth;

    
    /**
     * This constructor creates a title pane for the given internal frame
     * instance.
     *
     * @param frame The internal frame that needs a title pane.
     */
    public LiquidInternalFrameTitlePane(JInternalFrame frame) {
        super(frame);

        //if (LiquidInternalFrameUI.allowRoundedWindows)
        //setOpaque(false);
    }

    /**
     * Installs some default values.
     * Reads the internalframe title height from the ui defaults table.
     */
    protected void installDefaults() {
        super.installDefaults();
        frameTitleHeight = UIManager.getInt("InternalFrame.frameTitleHeight");
        Font font = isPalette ? UIManager.getFont("InternalFrame.paletteTitleFont")//super.getFont()
                         : (LiquidLookAndFeel.winDecoPanther ? UIManager.getFont("InternalFrame.pantherTitleFont")
                                                             : UIManager.getFont("InternalFrame.titleFont"));
//        setFont(UIManager.getFont("InternalFrame.titleFont"));
        setFont(font);
        paletteTitleHeight = UIManager.getInt(
                "InternalFrame.paletteTitleHeight");
        paletteCloseIcon = UIManager.getIcon("InternalFrame.paletteCloseIcon");

        //wasClosable = frame.isClosable();
    }

    protected void uninstallDefaults() {
        super.uninstallDefaults();

        //if (wasClosable != frame.isClosable()) {
        //    frame.setClosable(wasClosable);
        //}
    }

    protected void paintTitleBackground(Graphics g) {
        return;
    }

    /**
     * Paints this component.
     *
     * @param g The graphics context to use.
     */
    public void paintComponent(Graphics g) {
        //    if (isPalette)
        //    {
        //      paintPalette(g);
        //      return;
        //    }
        boolean leftToRight = frame.getComponentOrientation().isLeftToRight();
        boolean isSelected = frame.isSelected();

        Insets insets = frame.getInsets();
        int width = getWidth();
        int height = getHeight();

        Color foreground = LiquidLookAndFeel.getWindowTitleInactiveForeground();

        Graphics2D g2 = (Graphics2D) g;
        Object oldAntiAliasingValue = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (LiquidLookAndFeel.winDecoPanther) {
            drawPantherCaption(g, isSelected, width, height);
        } else {
            drawLiquidCaption(g, isSelected, width, height);
        }

        int xOffset = leftToRight && !LiquidLookAndFeel.winDecoPanther ? 2 : (width - 2);
        int xOvalOffset = 8;
        
// 20060212 MEV - Correct InternalFrame for Mac decorations            
        Icon icon = null;
        if (LiquidLookAndFeel.winDecoPanther) {
            icon = UIManager.getIcon("InternalFrame.pantherFrameIcon");
        } else {
            icon = frame.getFrameIcon();
        }         

        if (icon != null) {
            if (!leftToRight || LiquidLookAndFeel.winDecoPanther) {
                xOffset -= icon.getIconWidth();
            }

            int iconY = ((height / 2) - (icon.getIconHeight() / 2));
            icon.paintIcon(frame, g, xOffset, iconY);
            xOffset += (leftToRight && !LiquidLookAndFeel.winDecoPanther ? (icon.getIconWidth() + 2) : (-2));
        }

// Add in the oval offset 
        xOffset += (leftToRight && !LiquidLookAndFeel.winDecoPanther) ? xOvalOffset : 0;

        String frameTitle = frame.getTitle();
        int titleLength = 0;
        
        if (frameTitle != null) {
            Font f = getFont();
            g.setFont(f);
            int osFontOffset = f.getFamily().equals(LiquidLookAndFeel.fontName) ? 2 : 0;

            FontMetrics fm = g.getFontMetrics();
            
            int titleW = 0;

            Rectangle r = new Rectangle(0, 0, 0, 0);
            if( leftToRight && !LiquidLookAndFeel.winDecoPanther ) {
                if (frame.isIconifiable()) {
                    r = iconButton.getBounds();
                } else if (frame.isMaximizable()) {
                    r = maxButton.getBounds();
                } else if (frame.isClosable()) {
                    r = closeButton.getBounds();
                }

                if( r.x == 0 ) {
                    r.x = frame.getWidth()-frame.getInsets().right;
                }

                xOffset += menuBar != null ? (menuBar.getX() + menuBar.getWidth() + 2) : 2;
                titleW = r.x - xOffset - (!isPalette ? xOvalOffset : 0);
            } else {
                if (frame.isMaximizable()) {
                    r = maxButton.getBounds();
                } else if (frame.isIconifiable()) {
                    r = iconButton.getBounds();
                } else if (frame.isClosable()) {
                    r = closeButton.getBounds();
                }

                Rectangle menu = new Rectangle(0, 0, 0, 0);
                menu.x = frame.getWidth() - insets.right;

                xOffset = !isPalette ? (r.x + r.width + 2) : 10;
                titleW = menu.x - xOffset;
            }


            int startTitleLength = fm.stringWidth(frameTitle);
            frameTitle = LiquidUtilities.clipStringIfNecessary(fm, frameTitle, titleW);
            titleLength = fm.stringWidth(frameTitle);
            if( titleLength == startTitleLength && !isPalette ) {
                xOffset += (titleW - titleLength) / 2;
            }

            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = f.getLineMetrics(frameTitle, frc);
            
//            System.out.println("Internal: Title Height = " + height);
//            
//            System.out.println("Internal: osFontOffset = " + osFontOffset);
//            System.out.println("Internal: Font = " + f);
//            System.out.println("Internal: Font.size = " + f.getSize());
//            System.out.println("Internal: FontMetrics.getAscent = " + fm.getAscent());
//            System.out.println("Internal: FontMetrics.getDescent = " + fm.getDescent());
//            System.out.println("Internal: FontMetrics.getHeight = " + fm.getHeight());
//
//            System.out.println("Internal: LineMetrics.getAscent = " + lm.getAscent());
//            System.out.println("Internal: LineMetrics.getDescent = " + lm.getDescent());
//            System.out.println("Internal: LineMetrics.getHeight = " + lm.getHeight());

//                int yOffset = ((height - f.getSize()) / 2) + fm.getAscent() + osFontOffset - 1;
            int yOffset = ((height - Math.round(lm.getHeight())) / 2) + Math.round(lm.getAscent()) + osFontOffset;
            int endOffset = 19;//yOffset + Math.round(lm.getDescent());//height - (yOffset - 15) - Math.round(lm.getDescent());

//            System.out.println("Internal: yOffset = " + yOffset);
//            System.out.println("Internal: endOffset = " + endOffset);
            
            if (isPalette) {
                if (isSelected) {
                    g.setColor(Color.white);
                } else {
                    if (!LiquidLookAndFeel.winDecoPanther) {
                        g.setColor(Color.black);
                    } else {
                        g.setColor(Color.gray);
                    }
                }

                if (!leftToRight) {
                    xOffset -= titleLength;
                }

// Take out the oval offset since the Palette doesn't get the oval
                xOffset -= xOvalOffset;
                
                g2.drawString(frameTitle, xOffset, yOffset);
                xOffset += (leftToRight ? (titleLength + 2) : (-2));
            } else {
                // a shadow effect for the titles in normal sized internal frames
//                int yOffset = ((height - f.getSize()) / 2) + fm.getAscent() + osFontOffset;

                if (!leftToRight) {
                    xOffset -= titleLength;
                }

                //xOffset = (width / 2) - (titleLength / 2);

                if (isSelected) {
                    // for an active window

                    if (!LiquidLookAndFeel.winDecoPanther) {
                        GradientPaint grad = new GradientPaint(xOffset +
                                (titleLength / 2), yOffset - 15,
                                new Color(60, 144, 233),
                                xOffset + (titleLength / 2),
                                endOffset, new Color(102, 186, 255));
                        g2.setPaint(grad);
                        g2.fillRoundRect(xOffset - 8, yOffset - 15,
                            titleLength + 15, endOffset, 18, 18);
                        g.setColor(new Color(0, 78, 167));
                        g2.drawRoundRect(xOffset - 8, yOffset - 15,
                            titleLength + 15, endOffset, 18, 18);
                    }

                    if (!LiquidLookAndFeel.winDecoPanther) {
                        g.setColor(shadowColor);//Color.black);
                        g.drawString(frameTitle, xOffset + 1, yOffset);
                        g.setColor(normalTitleColor);
                    } else {
                        g.setColor(Color.black);
                    }

                    g.drawString(frameTitle, xOffset, yOffset - 1);

                    xOffset += (leftToRight ? (titleLength + 2) : (-2));
                } else {
                    // for an inactive window

// 20060212 MEV - Correct InternalFrame for Mac decorations            
                    if (!LiquidLookAndFeel.winDecoPanther) {
                        GradientPaint grad = new GradientPaint(xOffset +
                                (titleLength / 2), yOffset - 15,
                                new Color(191, 211, 233),
                                xOffset + (titleLength / 2), endOffset,
                                new Color(233, 253, 255));
                        g2.setPaint(grad);
                        g2.fillRoundRect(xOffset - 8, yOffset - 15,
                            titleLength + 15, endOffset, 18, 18);
                        g.setColor(new Color(125, 145, 167));
                        g2.drawRoundRect(xOffset - 8, yOffset - 15,
                            titleLength + 15, endOffset, 18, 18);
                    }

// 20060212 MEV - Correct InternalFrame for Mac decorations to have inactive title gray           
                    if (!LiquidLookAndFeel.winDecoPanther) {
                        g.setColor(Color.black);
                    } else {
                        g.setColor(Color.GRAY);
                    }

                    g.drawString(frameTitle, xOffset, yOffset - 1);

                    xOffset += (leftToRight ? (titleLength + 2) : (-2));
                }
            }
        }
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAliasingValue);
    }

    private void drawLiquidCaption(Graphics g, boolean isSelected, int w, int h) {
        Color c = isSelected ? new Color(62, 145, 235) : new Color(175, 214, 255);
        g.setColor(c);
        g.fillRect(0, 0, w, h - 1);
        c = isSelected ? new Color(94, 172, 255) : new Color(226, 240, 255);
        g.setColor(c);
        g.drawLine(0, 0, w, 0);
        c = isSelected ? new Color(60, 141, 228) : new Color(170, 207, 247);
        g.setColor(c);
        g.drawLine(0, 1, w, 1);

        for (int i = 4; i < (h - 1); i += 4) {
            c = isSelected ? new Color(59, 138, 223) : new Color(166, 203, 242);
            g.setColor(c);
            g.drawLine(0, i, w, i);
            c = isSelected ? new Color(60, 141, 228) : new Color(170, 207, 247);
            g.setColor(c);
            g.drawLine(0, i + 1, w, i + 1);
        }

        c = isSelected ? new Color(47, 111, 180) : new Color(135, 164, 196);
        g.setColor(c);
        g.drawLine(0, h - 1, w, h - 1);
    }

    private void drawPantherCaption(Graphics g, boolean isSelected, int w, int h) {
        Graphics2D g2 = (Graphics2D) g;
        GradientPaint grad = isSelected
            ? new GradientPaint(0, 0, new Color(238, 238, 238), 0, h - 1,
                new Color(192, 192, 192))
            : new GradientPaint(0, 0, new Color(230, 230, 230), 0, h - 1,
                new Color(202, 202, 202));

        g2.setPaint(grad);
        g2.fillRect(0, 0, w, h - 1);

        g2.setColor(new Color(198, 198, 198));
        g2.drawLine(0, 0, w - 1, 0);
        g2.setColor(Color.WHITE);
        g2.drawLine(0, 1, w - 1, 1);
        g2.setColor(new Color(147, 147, 147));
        g2.drawLine(0, h - 1, w, h - 1);
    }


    /**
     * Creates the layout manager for the title pane.
     *
     * @return The layout manager for the title pane.
     */
    protected LayoutManager createLayout() {
        return this;
    }

    protected void addSubComponents() {
        //super.addSubComponents();
        add(iconButton);
        add(maxButton);
        add(closeButton);

        if (menuBar != null) {
            //menuBar.setOpaque(false);
        }
    }

    protected void setButtonIcons() {
//        System.err.println("setButtonIcons called!");
    }

    /**
     * Creates the buttons of the title pane and initilizes their actions.
     */
    protected void createButtons() {
        //    Boolean paintActive = frame.isSelected() ? Boolean.TRUE:Boolean.FALSE;
        if (iconButtonUI == null) {
            iconButtonUI = LiquidWindowButtonUI.createButtonUIForType(LiquidWindowButtonUI.MINIMIZE);
            maxButtonUI = LiquidWindowButtonUI.createButtonUIForType(LiquidWindowButtonUI.MAXIMIZE);
            closeButtonUI = LiquidWindowButtonUI.createButtonUIForType(LiquidWindowButtonUI.CLOSE);
        }

        iconButton = new SpecialUIButton(iconButtonUI, frame);
        iconButton.addActionListener(iconifyAction);
        iconButton.setRolloverEnabled(true);
        iconButton.addMouseListener(new RolloverListener(iconButton,
                iconifyAction));

        maxButton = new SpecialUIButton(maxButtonUI, frame);
        maxButton.addActionListener(maximizeAction);
        maxButton.setRolloverEnabled(true);
        maxButton.addMouseListener(new RolloverListener(maxButton,
                maximizeAction));

        closeButton = new SpecialUIButton(closeButtonUI, frame);
        closeButton.addActionListener(closeAction);
        closeButton.setRolloverEnabled(true);
        closeButton.addMouseListener(new RolloverListener(closeButton,
                closeAction));

        iconButton.getAccessibleContext().setAccessibleName(UIManager.getString(
                "InternalFrameTitlePane.iconifyButtonAccessibleName"));

        maxButton.getAccessibleContext().setAccessibleName(UIManager.getString(
                "InternalFrameTitlePane.maximizeButtonAccessibleName"));

        closeButton.getAccessibleContext().setAccessibleName(UIManager.getString(
                "InternalFrameTitlePane.closeButtonAccessibleName"));

        if (frame.isSelected()) {
            activate();
        } else {
            deactivate();
        }
    }

    
    /**
     * Paints the title pane for a palette.
     *
     * @param g The graphics context to use.
     */
    public void paintPalette(Graphics g) {
        return;
    }

    public void setPalette(boolean b) {
        isPalette = b;

        if (isPalette) {
            closeButton.setIcon(paletteCloseIcon);

            if (frame.isMaximizable()) {
                remove(maxButton);
            }

            if (frame.isIconifiable()) {
                remove(iconButton);
            }
        } else {
            closeButton.setIcon(closeIcon);

            if (frame.isMaximizable()) {
                add(maxButton);
            }

            if (frame.isIconifiable()) {
                add(iconButton);
            }
        }

        revalidate();
        repaint();
    }

    /**
     * Adds the specified component with the specified name to the layout.
     *
     * @param name the component name
     * @param c the component to be added
     */
    public void addLayoutComponent(String name, Component c) {
    }

    /**
     * Removes the specified component from the layout.
     *
     * @param c the component to be removed
     */
    public void removeLayoutComponent(Component c) {
    }

    /**
     * Calculates the preferred size dimensions for the specified
     * panel given the components in the specified parent container.
     *
     * @param c the component to be laid out
     */
    public Dimension preferredLayoutSize(Container c) {
        return getPreferredSize(c);
    }

    /**
     * Gets the preferred size of the given container.
     *
     * @param c The container to gets its preferred size.
     * @return The preferred size of the given container.
     */
    public Dimension getPreferredSize(Container c) {
        return new Dimension(c.getSize().width,
            (isPalette ? paletteTitleHeight : frameTitleHeight));
    }

    /**
     * The minimum size of the frame.
     * This is used, for example, during resizing to
     * find the minimum allowable size.
     * Providing at least some minimum size fixes a bug
     * which breaks horizontal resizing.
     * <b>Note</b>: the Motif plaf allows for a 0,0 min size,
     * but we provide a reasonable minimum here.
     * <b>Future</b>: calculate min size based upon contents.
     */
    public Dimension getMinimumSize() {
        return new Dimension(70, 25);
    }

    
    /**
     * Calculates the minimum size dimensions for the specified
     * panel given the components in the specified parent container.
     *
     * @param c the component to be laid out
     */
    public Dimension minimumLayoutSize(Container c) {
        return preferredLayoutSize(c);
    }

    /**
     * Lays out the container in the specified panel.
     *
     * @param c the component which needs to be laid out
     */
    public void layoutContainer(Container c) {
        boolean leftToRight = frame.getComponentOrientation().isLeftToRight();

        if( LiquidLookAndFeel.winDecoPanther ) {
            leftToRight = !leftToRight;
        }
        
        int buttonHeight = closeButton.getPreferredSize().height;

        int w = getWidth();
        int x = leftToRight ? w : 0;
        int y = ((getHeight() - buttonHeight) / 2) + 1;
        int spacing;

        // assumes all buttons have the same dimensions
        // these dimensions include the borders
//        int buttonWidth = 18; //closeButton.getIcon().getIconWidth();
        int buttonWidth = closeButton.getPreferredSize().width;

        if (frame.isClosable()) {
            if (isPalette) {
                spacing = LiquidLookAndFeel.winDecoPanther ? 7 : 0;
                x += (leftToRight ? (-spacing - (buttonWidth)) : spacing);
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);

                if (!leftToRight) {
                    x += (buttonWidth);
                }
            } else {
                spacing = LiquidLookAndFeel.winDecoPanther ? 7 : 0;
                x += (leftToRight ? (-spacing - buttonWidth) : spacing);
                closeButton.setBounds(x, y, buttonWidth, buttonHeight);

                if (!leftToRight) {
                    x += buttonWidth;
                }
            }
        }

        if( LiquidLookAndFeel.winDecoPanther ) {
            if (frame.isIconifiable() && !isPalette ) {//&& !frame.isIcon()) {
                //spacing = 0; //frame.isMaximizable() ? 0 : (frame.isClosable() ? 0 : 2);
                spacing = 7;//frame.isMaximizable() ? (LiquidLookAndFeel.winDecoPanther ? 7 : 0) : (frame.isClosable() ? (LiquidLookAndFeel.winDecoPanther ? 8 : 0) : 0);

                x += (leftToRight ? (-spacing - buttonWidth) : spacing);
                iconButton.setBounds(x, y, buttonWidth, buttonHeight);

                if (!leftToRight) {
                    x += buttonWidth;
                }
            }
        }
        
        if (frame.isMaximizable() && !isPalette) {
            if( !LiquidLookAndFeel.winDecoPanther || !frame.isIcon() ) {
                spacing = (LiquidLookAndFeel.winDecoPanther ? 7 : 0);//frame.isClosable() ? (LiquidLookAndFeel.winDecoPanther ? 8 : 0) : 0;

                x += (leftToRight ? (-spacing - buttonWidth) : spacing);
                maxButton.setBounds(x, y, buttonWidth, buttonHeight);

                if (!leftToRight) {
                    x += buttonWidth;
                }
            }
        }

        if( !LiquidLookAndFeel.winDecoPanther ) {
            if (frame.isIconifiable() && !isPalette) {
                //spacing = 0; //frame.isMaximizable() ? 0 : (frame.isClosable() ? 0 : 2);
                spacing = 0;//frame.isMaximizable() ? (LiquidLookAndFeel.winDecoPanther ? 7 : 0) : (frame.isClosable() ? (LiquidLookAndFeel.winDecoPanther ? 8 : 0) : 0);

                x += (leftToRight ? (-spacing - buttonWidth) : spacing);
                iconButton.setBounds(x, y, buttonWidth, buttonHeight);

                if (!leftToRight) {
                    x += buttonWidth;
                }
            }
        }
        
        buttonsWidth = leftToRight ? (w - x) : x;
    }

    public void activate() {
        closeButton.setEnabled(true);
        iconButton.setEnabled(true);
        maxButton.setEnabled(true);
    }

    public void deactivate() {
        closeButton.setEnabled(false);
        iconButton.setEnabled(false);
        maxButton.setEnabled(false);
    }


    /**
     * This listener is added to the maximize, minimize and close button to
     * manage the rollover status of the buttons
     *
     */
    class RolloverListener implements MouseListener {
        JButton button;
        Action action;

        public RolloverListener(JButton b, Action a) {
            button = b;
            action = a;
        }

        public void mouseClicked(MouseEvent e) {
            action.actionPerformed(new ActionEvent(this, Event.ACTION_EVENT,
                    button.getText()));
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
            button.getModel().setRollover(true);

            if (!button.isEnabled()) {
                button.setEnabled(true);
            }

            button.repaint();
        }

        public void mouseExited(MouseEvent e) {
            button.getModel().setRollover(false);

            if (!frame.isSelected()) {
                button.setEnabled(false);
            }

            button.repaint();
        }
    }
}
