/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *        Liquid Look and Feel                                                   *
 *                                                                             *
 *  Author, Miroslav Lazarevic                                                 *
 *                                                                             *
 *   For licensing information and credits, please refer to the                *
 *   comment in file com.birosoft.liquid.LiquidLookAndFeel                     *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package com.birosoft.liquid;

import com.birosoft.liquid.util.LiquidUtilities;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;

import java.beans.*;

import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;


/*
 * @(#)LiquidTitlePane.java        1.10 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
/*
 * Class that manages a JLF awt.Window-descendant class's title bar.
 * <p>
 * This class assumes it will be created with a particular window
 * decoration style, and that if the style changes, a new one will
 * be created.
 *
 * @version 1.10 12/03/01
 * @author Terry Kellerman
 * @since 1.4
 */

/**
 * LiquidTitlePane
 *
 * @version 0.1
 * @author Miroslav Lazarević
 */
class LiquidTitlePane extends JComponent {
    private static final Border handyEmptyBorder = new EmptyBorder(0, 0, 0, 0);
    private static final int IMAGE_HEIGHT = 16;
    private static final int IMAGE_WIDTH = 16;
    private static LiquidWindowButtonUI iconButtonUI;
    private static LiquidWindowButtonUI maxButtonUI;
    private static LiquidWindowButtonUI closeButtonUI;
    private static LiquidWindowButtonUI menuButtonUI;
    private boolean prevState = false;
    private boolean isMenuShowed = false;

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
    Color disabledTitleColor = new Color(216, 228, 244);

    /**
     * PropertyChangeListener added to the JRootPane.
     */
    private PropertyChangeListener propertyChangeListener;

    /**
     * JMenuBar, typically renders the system menu items.
     */
    private JMenuBar menuBar;

    /**
     * Action used to close the Window.
     */
    private Action closeAction;

    /**
     * Action used to iconify the Frame.
     */
    private Action iconifyAction;

    /**
     * Action to restore the Frame size.
     */
    private Action restoreAction;

    /**
     * Action to restore the Frame size.
     */
    private Action maximizeAction;
    private Action menuAction;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton toggleButton;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton iconifyButton;

    /**
     * Button used to maximize or restore the Frame.
     */
    private JButton closeButton;
    private JButton menuButton;

    /**
     * Listens for changes in the state of the Window listener to update
     * the state of the widgets.
     */
    private WindowListener windowListener;
    private ComponentListener windowMoveListener;

    /**
     * Window we're currently in.
     */
    private Window window;

    /**
     * JRootPane rendering for.
     */
    private JRootPane rootPane;

    /**
     * Room remaining in title for bumps.
     */
    private int buttonsWidth;

    /**
     * Buffered Frame.state property. As state isn't bound, this is kept
     * to determine when to avoid updating widgets.
     */
    private int state;

    /**
     * RootPaneUI that created us.
     */
    private LiquidRootPaneUI rootPaneUI;

    public LiquidTitlePane(JRootPane root, LiquidRootPaneUI ui) {
        rootPane = root;
        rootPaneUI = ui;

        state = -1;

        installSubcomponents();
        installDefaults();

        setLayout(createLayout());
    }

    /**
     * Uninstalls the necessary state.
     */
    private void uninstall() {
        uninstallListeners();
        window = null;
        removeAll();
    }

    /**
     * Installs the necessary listeners.
     */
    private void installListeners() {
        if (window != null) {
            windowListener = createWindowListener();
            window.addWindowListener(windowListener);
            propertyChangeListener = createWindowPropertyChangeListener();
            window.addPropertyChangeListener(propertyChangeListener);
            windowMoveListener = new WindowMoveListener();
            window.addComponentListener(windowMoveListener);
        }
    }

    /**
     * Uninstalls the necessary listeners.
     */
    private void uninstallListeners() {
        if (window != null) {
            window.removeWindowListener(windowListener);
            window.removePropertyChangeListener(propertyChangeListener);
            window.removeComponentListener(windowMoveListener);
        }
    }

    /**
     * Returns the <code>WindowListener</code> to add to the
     * <code>Window</code>.
     */
    private WindowListener createWindowListener() {
        return new WindowHandler();
    }

    /**
     * Returns the <code>PropertyChangeListener</code> to install on
     * the <code>Window</code>.
     */
    private PropertyChangeListener createWindowPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    /**
     * Returns the <code>JRootPane</code> this was created for.
     */
    public JRootPane getRootPane() {
        return rootPane;
    }

    public static void resetCachedSkins() {
        iconButtonUI = null;
        maxButtonUI = null;
        closeButtonUI = null;
        menuButtonUI = null;
        
    }
    
    
    /**
     * Returns the decoration style of the <code>JRootPane</code>.
     */
    private int getWindowDecorationStyle() {
        return getRootPane().getWindowDecorationStyle();
    }

    public void addNotify() {
        super.addNotify();

        uninstallListeners();

        window = SwingUtilities.getWindowAncestor(this);

        if (window != null) {
            if (window instanceof Frame) {
                setState(((Frame) window).getExtendedState());
            } else {
                setState(0);
            }

            setActive(window.isActive());
            installListeners();
        }
    }

    public void removeNotify() {
        super.removeNotify();

        uninstallListeners();
        window = null;
    }

    /**
     * Adds any sub-Components contained in the <code>LiquidTitlePane</code>.
     */
    private void installSubcomponents() {
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            createActions();

            if (!LiquidLookAndFeel.winDecoPanther) {
                menuBar = createMenuBar();
                add(menuBar);
            }

            createButtons();
            add(iconifyButton);
            add(toggleButton);
            add(closeButton);

            if (LiquidLookAndFeel.winDecoPanther) {
                add(menuButton);
                menuButton.putClientProperty("externalFrameButton", Boolean.TRUE);
            }

            iconifyButton.putClientProperty("externalFrameButton", Boolean.TRUE);
            toggleButton.putClientProperty("externalFrameButton", Boolean.TRUE);
            closeButton.putClientProperty("externalFrameButton", Boolean.TRUE);

            /*System.out.println(rootPane.getIgnoreRepaint());
            rootPane.setIgnoreRepaint(true);
            System.out.println(rootPane.getContentPane().getIgnoreRepaint());
            rootPane.getContentPane().setIgnoreRepaint(true);*/
        } else if (getWindowDecorationStyle() != JRootPane.NONE) {
            createActions();
            createButtons();
            add(closeButton);
            closeButton.putClientProperty("externalFrameButton", Boolean.FALSE);

            if (LiquidLookAndFeel.winDecoPanther) {
                add(menuButton);
                menuButton.putClientProperty("externalFrameButton",
                    Boolean.FALSE);
            }
        }
    }

    /**
     * Installs the fonts and necessary properties on the LiquidTitlePane.
     */
    private void installDefaults() {
//        setFont(UIManager.getFont("InternalFrame.titleFont", getLocale()));
        Font font = LiquidLookAndFeel.winDecoPanther ? UIManager.getFont("InternalFrame.pantherTitleFont")
                                                             : UIManager.getFont("InternalFrame.titleFont");
        setFont(font);
    }

    /**
     * Uninstalls any previously installed UI values.
     */
    private void uninstallDefaults() {
    }

    /**
     * Returns the <code>JMenuBar</code> displaying the appropriate
     * system menu items.
     */
    protected JMenuBar createMenuBar() {
        menuBar = new SystemMenuBar(createMenu());
        menuBar.setFocusable(false);
        menuBar.setBorderPainted(true);

        //menuBar.add(createMenu());
        return menuBar;
    }

    /**
     * Closes the Window.
     */
    private void close() {
        isMenuShowed = false;

        Window window = getWindow();

        if (window != null) {
            window.dispatchEvent(new WindowEvent(window,
                    WindowEvent.WINDOW_CLOSING));
        }
    }

    /**
     * Iconifies the Frame.
     */
    private void iconify() {
        isMenuShowed = false;

        Frame frame = getFrame();

        if (frame != null) {
            frame.setExtendedState(state | Frame.ICONIFIED);
        }
    }

    /**
     * Maximizes the Frame.
     */
    private void maximize() {
        isMenuShowed = false;

        Frame frame = getFrame();

        if (frame != null) {
            setMaximizeBounds(frame);
            frame.setExtendedState(state | Frame.MAXIMIZED_BOTH);
        }
    }

    private void setMaximizeBounds(Frame frame) {
        if (frame.getMaximizedBounds() != null) {
            return;
        }

        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // spare any Systemmenus or Taskbars or ??...
        int x = screenInsets.left;
        int y = screenInsets.top;
        int w = screenSize.width - x - screenInsets.right;
        int h = screenSize.height - y - screenInsets.bottom;
        Rectangle maxBounds = new Rectangle(x, y, w, h);
        frame.setMaximizedBounds(maxBounds);
    }

    /**
     * Restores the Frame size.
     */
    private void restore() {
        isMenuShowed = false;

        Frame frame = getFrame();

        if (frame == null) {
            return;
        }

        if ((state & Frame.ICONIFIED) != 0) {
            frame.setExtendedState(state & ~Frame.ICONIFIED);
        } else {
            frame.setExtendedState(state & ~Frame.MAXIMIZED_BOTH);
        }
    }

    private void showMenu(JPopupMenu systemMenu) {
        if (!isMenuShowed) {
            systemMenu.show(this, LiquidLookAndFeel.winDecoPanther ? (getWidth() - systemMenu.getPreferredSize().width) : 0, 21);
            isMenuShowed = true;
        } else {
            isMenuShowed = false;
            systemMenu.setVisible(isMenuShowed);
        }
    }

    /**
     * Create the <code>Action</code>s that get associated with the
     * buttons and menu items.
     */
    private void createActions() {
        closeAction = new CloseAction();
        iconifyAction = new IconifyAction();
        restoreAction = new RestoreAction();
        maximizeAction = new MaximizeAction();
        menuAction = new MenuAction();
    }

    /**
     * Returns the <code>JMenu</code> displaying the appropriate menu items
     * for manipulating the Frame.
     */
    private JPopupMenu createMenu() {
        JPopupMenu menu = new JPopupMenu();

        if ((getWindowDecorationStyle() == JRootPane.FRAME) ||
                (getWindowDecorationStyle() == JRootPane.PLAIN_DIALOG)) {
            addMenuItems(menu);

            // we use this property to prevent the Menu from drawing rollovers
            menu.putClientProperty("isSystemMenu", Boolean.TRUE);
        }

        return menu;
    }

    /**
     * Adds the necessary <code>JMenuItem</code>s to the passed in menu.
     */
    private void addMenuItems(JPopupMenu menu) {
        Locale locale = getRootPane().getLocale();
        JMenuItem mi = menu.add(restoreAction);
        mi.setMnemonic('r');

        mi = menu.add(iconifyAction);
        mi.setMnemonic('e');

        if (Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
            mi = menu.add(maximizeAction);
            mi.setMnemonic('x');
        }

        menu.addSeparator();

        mi = menu.add(closeAction);
        mi.setMnemonic('c');
    }

    /**
     * Creates the buttons of the title pane and initializes their actions.
     */
    protected void createButtons() {
        if (iconButtonUI == null) {
            iconButtonUI = LiquidWindowButtonUI.createButtonUIForType(LiquidWindowButtonUI.MINIMIZE);
            maxButtonUI = LiquidWindowButtonUI.createButtonUIForType(LiquidWindowButtonUI.MAXIMIZE);
            closeButtonUI = LiquidWindowButtonUI.createButtonUIForType(LiquidWindowButtonUI.CLOSE);

            if (LiquidLookAndFeel.winDecoPanther) {
                menuButtonUI = LiquidWindowButtonUI.createButtonUIForType(LiquidWindowButtonUI.SYSMENU);
            }
        }

        iconifyButton = new SpecialUIButton(iconButtonUI,
                (Window) getRootPane().getParent());
        iconifyButton.setAction(iconifyAction);
        iconifyButton.setRolloverEnabled(true);

        toggleButton = new SpecialUIButton(maxButtonUI,
                (Window) getRootPane().getParent());
        toggleButton.setAction(maximizeAction);
        toggleButton.setRolloverEnabled(true);

        closeButton = new SpecialUIButton(closeButtonUI,
                (Window) getRootPane().getParent());
        closeButton.setAction(closeAction);
        closeButton.setRolloverEnabled(true);

        if (LiquidLookAndFeel.winDecoPanther) {
            menuButton = new SpecialUIButton(menuButtonUI,
                    (Window) getRootPane().getParent());
            menuButton.setAction(menuAction);
            menuButton.setRolloverEnabled(true);
            menuButton.getAccessibleContext().setAccessibleName("Menu");
        }

        closeButton.getAccessibleContext().setAccessibleName("Close");
        iconifyButton.getAccessibleContext().setAccessibleName("Iconify");
        toggleButton.getAccessibleContext().setAccessibleName("Maximize");
    }

    /**
     * Returns the <code>LayoutManager</code> that should be installed on
     * the <code>LiquidTitlePane</code>.
     */
    private LayoutManager createLayout() {
        return new TitlePaneLayout();
    }

    /**
     * Updates state dependant upon the Window's active state.
     */
    private void setActive(boolean isActive) {
        if (getWindowDecorationStyle() == JRootPane.FRAME) {
            Boolean activeB = isActive ? Boolean.TRUE : Boolean.FALSE;

            iconifyButton.putClientProperty("paintActive", activeB);
            closeButton.putClientProperty("paintActive", activeB);
            toggleButton.putClientProperty("paintActive", activeB);

            iconifyButton.setEnabled(isActive);
            closeButton.setEnabled(isActive);
            toggleButton.setEnabled(isActive);

            if (LiquidLookAndFeel.winDecoPanther) {
                menuButton.putClientProperty("paintActive", activeB);
                menuButton.setEnabled(isActive);
            }
        }

        // Repaint the whole thing as the Borders that are used have
        // different colors for active vs inactive
        getRootPane().repaint();
    }

    /**
     * Sets the state of the Window.
     */
    private void setState(int state) {
        setState(state, false);
    }

    /**
     * Sets the state of the window. If <code>updateRegardless</code> is
     * true and the state has not changed, this will update anyway.
     */
    private void setState(int state, boolean updateRegardless) {
        Window w = getWindow();

        if ((w != null) &&
                ((getWindowDecorationStyle() == JRootPane.FRAME) ||
                (getWindowDecorationStyle() == JRootPane.PLAIN_DIALOG))) {
            if ((this.state == state) && !updateRegardless) {
                return;
            }

            Frame frame = getFrame();

            if (frame != null) {
                JRootPane rootPane = getRootPane();

                if (((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) &&
                        ((rootPane.getBorder() == null) ||
                        (rootPane.getBorder() instanceof UIResource)) &&
                        frame.isShowing()) {
                    //rootPane.setBorder(null);
                } else if ((state & Frame.MAXIMIZED_BOTH) != Frame.MAXIMIZED_BOTH) {
                    // This is a croak, if state becomes bound, this can
                    // be nuked.
                    //rootPaneUI.installBorder(rootPane);
                }

                if (frame.isResizable()) {
                    if (((state & Frame.MAXIMIZED_VERT) == Frame.MAXIMIZED_VERT) ||
                            ((state & Frame.MAXIMIZED_HORIZ) == Frame.MAXIMIZED_HORIZ)) {
                        updateToggleButton(restoreAction);
                        maximizeAction.setEnabled(false);
                        restoreAction.setEnabled(true);
                    } else {
                        updateToggleButton(maximizeAction);
                        maximizeAction.setEnabled(true);
                        restoreAction.setEnabled(false);
                    }

                    if ((toggleButton.getParent() == null) ||
                            (iconifyButton.getParent() == null)) {
                        add(toggleButton);
                        add(iconifyButton);
                        revalidate();
                        repaint();
                    }

                    toggleButton.setText(null);
                } else {
                    maximizeAction.setEnabled(false);
                    restoreAction.setEnabled(false);

                    if (toggleButton.getParent() != null) {
                        remove(toggleButton);
                        revalidate();
                        repaint();
                    }
                }
            } else {
                // Not contained in a Frame
                maximizeAction.setEnabled(false);
                restoreAction.setEnabled(false);
                iconifyAction.setEnabled(false);
                remove(toggleButton);
                remove(iconifyButton);
                revalidate();
                repaint();
            }

            closeAction.setEnabled(true);
            this.state = state;
        }
    }

    /**
     * Updates the toggle button to contain the Icon <code>icon</code>, and
     * Action <code>action</code>.
     */
    private void updateToggleButton(Action action) {
        toggleButton.setAction(action);
        toggleButton.setText(null);
    }

    /**
     * Returns the Frame rendering in. This will return null if the
     * <code>JRootPane</code> is not contained in a <code>Frame</code>.
     */
    private Frame getFrame() {
        Window window = getWindow();

        if (window instanceof Frame) {
            return (Frame) window;
        }

        return null;
    }

    /**
     * Returns the <code>Window</code> the <code>JRootPane</code> is
     * contained in. This will return null if there is no parent ancestor
     * of the <code>JRootPane</code>.
     */
    private Window getWindow() {
        return window;
    }

    /**
     * Returns the String to display as the title.
     */
    private String getTitle() {
        Window w = getWindow();

        if (w instanceof Frame) {
            return ((Frame) w).getTitle();
        } else if (w instanceof Dialog) {
            return ((Dialog) w).getTitle();
        }

        return null;
    }

    public boolean isSelected() {
        Window window = getWindow();

        return (window == null) ? true : window.isActive();
    }

    public boolean isFrameMaximized() {
        Frame frame = getFrame();

        if (frame != null) {
            return ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH);
        }

        return false;
    }

    /**
     * Renders the TitlePane.
     */
    public void paintComponent(Graphics g) {
        if (getFrame() != null) {
            setState(getFrame().getExtendedState());
        }

        Window frame = getWindow();

        boolean leftToRight = frame.getComponentOrientation().isLeftToRight();
        boolean isSelected = isSelected();

        if (isSelected) {
            prevState = true;
        }

        if (!prevState && !isSelected) {
            isSelected = true;
        }

        int width = getWidth();
        int height = getHeight();

        Color foreground = LiquidLookAndFeel.getWindowTitleInactiveForeground();
        Graphics2D g2 = (Graphics2D) g;

        Object oldAntiAliasingValue = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
//            RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        
        if (LiquidLookAndFeel.winDecoPanther) {
            drawPantherCaption(g, isSelected, width, height);
        } else {
            drawLiquidCaption(g, isSelected, width, height);
        }

        int titleLength = 0;
        int xOffset = leftToRight ? 2 : (width - 2);
        String frameTitle = getTitle();
        int xOvalOffset = 8;

// Add in the oval offset 
        xOffset += (leftToRight && !LiquidLookAndFeel.winDecoPanther) ? xOvalOffset : 0;
        
        if (frameTitle != null) {
            Font f = getFont();
            g.setFont(f);
            int osFontOffset = f.getFamily().equals(LiquidLookAndFeel.fontName) ? 2 : 0;

            
            FontMetrics fm = g.getFontMetrics();
            titleLength = fm.stringWidth(frameTitle);

            int titleW = 0;

            Rectangle r = new Rectangle(0, 0, 0, 0);
            if( leftToRight && !LiquidLookAndFeel.winDecoPanther ) {
                if (iconifyAction.isEnabled()) {
                    r = iconifyButton.getBounds();
                } else if (maximizeAction.isEnabled()) {
                    r = toggleButton.getBounds();
                } else if (closeAction.isEnabled()) {
                    r = closeButton.getBounds();
                }

                if( r.x == 0 ) {
                    r.x = frame.getWidth()-frame.getInsets().right;
                }

                xOffset += menuBar != null ? (menuBar.getX() + menuBar.getWidth() + 2) : 2;
                titleW = r.x - xOffset - xOvalOffset -2;
            } else {
                if (maximizeAction.isEnabled()) {
                    r = toggleButton.getBounds();
                } else if (iconifyAction.isEnabled()) {
                    r = iconifyButton.getBounds();
                } else if (closeAction.isEnabled()) {
                    r = closeButton.getBounds();
                }

                Rectangle menu = new Rectangle(0, 0, 0, 0);
                if( menuButton != null ) {
                    menu = menuButton.getBounds();
                }
                
                if( menu.x == 0 ) {
                    menu.x = frame.getWidth()-frame.getInsets().right;
                }
                xOffset = r.x + r.width + 2;
                titleW = menu.x - xOffset;
            }

            int startTitleLength = fm.stringWidth(frameTitle);
            frameTitle = LiquidUtilities.clipStringIfNecessary(fm, frameTitle, titleW);
            titleLength = fm.stringWidth(frameTitle);
            if( titleLength == startTitleLength ) {
                xOffset += (titleW - titleLength) / 2;
            }

//            System.out.println("Title Height = " + height);
//            
//            System.out.println("osFontOffset = " + osFontOffset);
//            System.out.println("Font = " + f);
//            System.out.println("Font.size = " + f.getSize());
//            System.out.println("FontMetrics.getAscent = " + fm.getAscent());
//            System.out.println("FontMetrics.getDescent = " + fm.getDescent());
//            System.out.println("FontMetrics.getHeight = " + fm.getHeight());
            
            FontRenderContext frc = g2.getFontRenderContext();
            LineMetrics lm = f.getLineMetrics(frameTitle, frc);
            
//            System.out.println("LineMetrics.getAscent = " + lm.getAscent());
//            System.out.println("LineMetrics.getDescent = " + lm.getDescent());
//            System.out.println("LineMetrics.getHeight = " + lm.getHeight());
            
            // a shadow effect for the titles in normal sized internal frames
//            int yOffset = ((height - f.getSize()) / 2) + fm.getAscent() + osFontOffset;
            int yOffset = ((height - Math.round(lm.getHeight())) / 2) + Math.round(lm.getAscent()) + osFontOffset;
            int endOffset = 19;//height - (yOffset - 15) - Math.round(lm.getDescent());
            
//            System.out.println("yOffset = " + yOffset);
//            System.out.println("endOffset = " + endOffset);
            
            if (!leftToRight) {
                xOffset -= titleLength;
            }

            if (isSelected) {
                // for an active window
                if (!LiquidLookAndFeel.winDecoPanther && titleLength > 0) {
                    GradientPaint grad = new GradientPaint(xOffset +
                            (titleLength / 2), yOffset - 15,
                            new Color(60, 144, 233),
                            xOffset + (titleLength / 2), endOffset,
                            new Color(102, 186, 255));
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

                    Frame _frame = null;
                    Dialog _dialog = null;
                    Image image = null;
                    Window w = getWindow();

                    if (w instanceof Frame) {
                        _frame = (Frame) w;
                        image = _frame.getIconImage();
                    } else {
                        _dialog = (Dialog) w;

                        if (_dialog.getParent() != null) {
                            if (_dialog.getParent() instanceof Frame) {
                                image = ((Frame) _dialog.getParent()).getIconImage();
                            }
                        }
                    }

                    if (image != null) {
                        xOffset += 10;
                        g.drawImage(image, xOffset - 20, 3, IMAGE_WIDTH,
                            IMAGE_HEIGHT, null);
                    } else {
                        Icon icon = UIManager.getIcon(
                                "InternalFrame.pantherIcon");

                        if (icon != null) {
                            xOffset += 10;
                            icon.paintIcon(this, g, xOffset - 20, 3);
                        }
                    }
                }

                g.drawString(frameTitle, xOffset, yOffset - 1);
                xOffset += (leftToRight ? (titleLength + 2) : (-2));
            } else {
                // for an inactive window

                if (!LiquidLookAndFeel.winDecoPanther && titleLength > 0) {
                    GradientPaint grad = new GradientPaint(xOffset +
                            (titleLength / 2), yOffset - 15,
                            new Color(191, 211, 233),
                            xOffset + (titleLength / 2), endOffset,
                            new Color(233, 253, 255));
                    g2.setPaint(grad);

                    //g2.fillRoundRect(xOffset-8, yOffset-15, titleLength+15, fm.getHeight()+1, 18, 18);
                    g2.fillRoundRect(xOffset - 8, yOffset - 15,
                        titleLength + 15, endOffset, 18, 18);
                    g.setColor(new Color(125, 145, 167));

                    //g2.drawRoundRect(xOffset-8, yOffset-15, titleLength+15, fm.getHeight()+1, 18, 18);
                    g2.drawRoundRect(xOffset - 8, yOffset - 15,
                        titleLength + 15, endOffset, 18, 18);
                }

                if (LiquidLookAndFeel.winDecoPanther) {
                    Frame _frame = null;
                    Dialog _dialog = null;
                    Image image = null;
                    Window w = getWindow();

                    if (w instanceof Frame) {
                        _frame = (Frame) w;
                        image = _frame.getIconImage();
                    } else {
                        _dialog = (Dialog) w;

                        if (_dialog.getParent() != null) {
                            if (_dialog.getParent() instanceof Frame) {
                                image = ((Frame) _dialog.getParent()).getIconImage();
                            }
                        }
                    }

                    if (image != null) {
                        xOffset += 10;
                        g.drawImage(image, xOffset - 20, 3, IMAGE_WIDTH,
                            IMAGE_HEIGHT, null);
                    } else {
                        Icon icon = UIManager.getIcon(
                                "InternalFrame.pantherIconInactive");

                        if (icon != null) {
                            xOffset += 10;
                            icon.paintIcon(this, g, xOffset - 20, 3);
                        }
                    }
                }

                g.setColor(LiquidLookAndFeel.winDecoPanther
                    ? new Color(115, 115, 115) : Color.black);
                g.drawString(frameTitle, xOffset, yOffset - 1);

                xOffset += (leftToRight ? (titleLength + 2) : (-2));
            }
        }
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldAntiAliasingValue);
        
//        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
//            RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT);
        
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
     * Convenience method to clip the passed in text to the specified
     * size.
     */
    private String clippedText(String text, FontMetrics fm, int availTextWidth) {
        if ((text == null) || (text.equals(""))) {
            return "";
        }

        int textWidth = SwingUtilities.computeStringWidth(fm, text);
        String clipString = "...";

        if (textWidth > availTextWidth) {
            int totalWidth = SwingUtilities.computeStringWidth(fm, clipString);
            int nChars;

            for (nChars = 0; nChars < text.length(); nChars++) {
                totalWidth += fm.charWidth(text.charAt(nChars));

                if (totalWidth > availTextWidth) {
                    break;
                }
            }

            text = text.substring(0, nChars) + clipString;
        }

        return text;
    }

    private int getInt(Object key, int defaultValue) {
        Object value = UIManager.get(key);

        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }

        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException nfe) {
            }
        }

        return defaultValue;
    }

    /**
     * Actions used to <code>close</code> the <code>Window</code>.
     */
    private class CloseAction extends AbstractAction {
        public CloseAction() {
            super("Close");
        }

        public void actionPerformed(ActionEvent e) {
            close();
        }
    }

    /**
     * Actions used to <code>iconfiy</code> the <code>Frame</code>.
     */
    private class IconifyAction extends AbstractAction {
        public IconifyAction() {
            super("Minimize");
        }

        public void actionPerformed(ActionEvent e) {
            iconify();
        }
    }

    /**
     * Actions used to <code>restore</code> the <code>Frame</code>.
     */
    private class RestoreAction extends AbstractAction {
        public RestoreAction() {
            super("Restore");
        }

        public void actionPerformed(ActionEvent e) {
            restore();
        }
    }

    /**
     * Actions used to <code>restore</code> the <code>Frame</code>.
     */
    private class MaximizeAction extends AbstractAction {
        public MaximizeAction() {
            super("Maximize");
        }

        public void actionPerformed(ActionEvent e) {
            maximize();
        }
    }

    private class MenuAction extends AbstractAction {
        public MenuAction() {
            super("Menu");
        }

        public void actionPerformed(ActionEvent e) {
            showMenu(createMenu());
        }
    }

    /**
     * Class responsible for drawing the system menu. Looks up the
     * image to draw from the Frame associated with the
     * <code>JRootPane</code>.
     */
    private class SystemMenuBar extends JMenuBar implements MouseListener {
        private JPopupMenu systemMenu;
        private boolean isShowed = false;

        public SystemMenuBar(JPopupMenu menu) {
            super();
            systemMenu = menu;
            addMouseListener(this);
        }

        protected void setSystemMenuVisible(boolean b) {
            isShowed = b;
        }

        public void paint(Graphics g) {
            if (LiquidLookAndFeel.winDecoPanther) {
                return;
            }

            Frame frame = getFrame();
            Image image = (frame != null) ? frame.getIconImage() : null;

            if (image != null) {
                g.drawImage(image, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
            } else {
                Icon icon = UIManager.getIcon("InternalFrame.icon");

                if (icon != null) {
                    icon.paintIcon(this, g, 0, 0);
                }
            }
        }

        public Dimension getMinimumSize() {
            return getPreferredSize();
        }

        public Dimension getPreferredSize() {
            Icon icon = UIManager.getIcon("InternalFrame.icon");

            if (icon != null) {
                return new Dimension(icon.getIconWidth(), icon.getIconHeight());
            }

            Dimension size = super.getPreferredSize();

            return new Dimension(Math.max(IMAGE_WIDTH, size.width),
                Math.max(size.height, IMAGE_HEIGHT));
        }

        public void mouseClicked(MouseEvent e) {
            if (!isShowed) {
                systemMenu.show(this, 0, 18);
                isShowed = true;
            } else {
                isShowed = false;
                systemMenu.setVisible(isShowed);
            }
        }

        public void mouseEntered(MouseEvent e) {
            if (!systemMenu.isVisible()) {
                isShowed = false;
            }
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mousePressed(MouseEvent e) {
        }

        public void mouseReleased(MouseEvent e) {
        }
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of <Foo>.
     */
    private class TitlePaneLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component c) {
        }

        public void removeLayoutComponent(Component c) {
        }

        public Dimension preferredLayoutSize(Container c) {
            int height = computeHeight();

            return new Dimension(height, height);
        }

        public Dimension minimumLayoutSize(Container c) {
            return preferredLayoutSize(c);
        }

        private int computeHeight() {
            if (getFrame() instanceof JFrame) {
                setMaximizeBounds(getFrame());

                if (LiquidLookAndFeel.winDecoPanther) {
                    return 22;
                }

                return 24;
            } else {
                if (LiquidLookAndFeel.winDecoPanther) {
                    return 22;
                }

                return 24;
            }
        }

        public void layoutContainer(Container c) {
            if (getWindowDecorationStyle() == JRootPane.NONE) {
                buttonsWidth = 0;

                return;
            }

            boolean leftToRight = (window == null)
                ? getRootPane().getComponentOrientation().isLeftToRight()
                : window.getComponentOrientation().isLeftToRight();

            if( LiquidLookAndFeel.winDecoPanther ) {
                leftToRight = !leftToRight;
            }
            
            int w = getWidth();
            int x;
            int spacing;
            int buttonHeight;
            int buttonWidth;

            if (closeButton != null) {
                buttonHeight = closeButton.getPreferredSize().height;
                buttonWidth = closeButton.getPreferredSize().width;
            } else {
                buttonHeight = IMAGE_HEIGHT;
                buttonWidth = IMAGE_WIDTH;
            }

            int y = ((getHeight() - buttonHeight) / 2) + 1;

            //if (Theme.derivedStyle[Theme.style] == Theme.WIN_STYLE) {
            //    y += 1;
            //}
            // assumes all buttons have the same dimensions
            // these dimensions include the borders
            //x = leftToRight ? w : 0;

            spacing = LiquidLookAndFeel.winDecoPanther ? 7 : 0;
            x = leftToRight ? spacing : (w - buttonWidth - spacing);

            if (menuBar != null) {
                // this is a JFrame
                menuBar.setBounds(x, y, buttonWidth, buttonHeight);
            }

            // bound for buttons
            x = leftToRight ? w : 0;
            x += (LiquidLookAndFeel.winDecoPanther
            ? (leftToRight ? (-spacing - 3 - buttonWidth) : spacing)
            : (leftToRight ? (-spacing - buttonWidth) : spacing));

            int yOffset = LiquidLookAndFeel.winDecoPanther ? 1 : 0;

            if (closeButton != null) {
                closeButton.setBounds(x, y - yOffset, buttonWidth, buttonHeight);
            }

            if (menuButton != null) {
                menuButton.setBounds(leftToRight ? 3 : (w - 21 - spacing), y - yOffset + 1, 21, 12);
                //menuButton.setBounds(3, y - yOffset + 1, 21, 12);
            }

            if (!leftToRight) {
                x += buttonWidth;
            }
            
            if( LiquidLookAndFeel.winDecoPanther ) {
                if ((iconifyButton != null) && (iconifyButton.getParent() != null)) {
                    x += (leftToRight ? (-spacing - buttonWidth) : spacing);
                    iconifyButton.setBounds(x, y - yOffset, buttonWidth,
                        buttonHeight);

                    if (!leftToRight) {
                        x += buttonWidth;
                    }
                }
            }
            
            if (Toolkit.getDefaultToolkit().isFrameStateSupported(Frame.MAXIMIZED_BOTH)) {
                if (toggleButton.getParent() != null) {
                    x += (leftToRight ? (-spacing - buttonWidth) : spacing);
                    toggleButton.setBounds(x, y - yOffset, buttonWidth,
                        buttonHeight);

                    if (!leftToRight) {
                        x += buttonWidth;
                    }
                }
            }

            if( !LiquidLookAndFeel.winDecoPanther ) {
                if ((iconifyButton != null) && (iconifyButton.getParent() != null)) {
                    x += (leftToRight ? (-spacing - buttonWidth) : spacing);
                    iconifyButton.setBounds(x, y - yOffset, buttonWidth,
                        buttonHeight);

                    if (!leftToRight) {
                        x += buttonWidth;
                    }
                }
            }
            
            buttonsWidth = leftToRight ? (w - x) : x;
        }
    }

    /**
     * PropertyChangeListener installed on the Window. Updates the necessary
     * state as the state of the Window changes.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent pce) {
            String name = pce.getPropertyName();

            // Frame.state isn't currently bound.
            if ("resizable".equals(name) || "state".equals(name)) {
                Frame frame = getFrame();

                if (frame != null) {
                    setState(frame.getExtendedState(), true);
                }

                if ("resizable".equals(name)) {
                    getRootPane().repaint();
                }
            } else if ("title".equals(name)) {
                repaint();
            } else if ("componentOrientation".equals(name)) {
                revalidate();
                repaint();
            }
        }
    }

    /**
     * WindowListener installed on the Window, updates the state as necessary.
     */
    private class WindowHandler extends WindowAdapter {
        public void windowActivated(WindowEvent ev) {
            setActive(true);
        }

        public void windowDeactivated(WindowEvent ev) {
            setActive(false);
        }
    }

    class WindowMoveListener extends ComponentAdapter {
        /**
         * @see java.awt.event.ComponentListener#componentMoved(ComponentEvent)
         */
        public void componentMoved(ComponentEvent e) {
            if (getWindowDecorationStyle() == JRootPane.NONE) {
                return;
            }

            Window w = getWindow();
            w.repaint(0, 0, w.getWidth(), 5);
        }

        /**
         * @see java.awt.event.ComponentAdapter#componentResized(ComponentEvent)
         */
        public void componentResized(ComponentEvent e) {
            if (getWindowDecorationStyle() == JRootPane.NONE) {
                return;
            }

            Window w = getWindow();
            w.repaint(0, 0, w.getWidth(), 5);
        }
    }
}
