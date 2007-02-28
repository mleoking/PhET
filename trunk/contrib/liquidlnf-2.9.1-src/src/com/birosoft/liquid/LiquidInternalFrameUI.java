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

import java.awt.Component;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DesktopManager;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.LookAndFeel;
import javax.swing.border.*;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicInternalFrameUI;


/**
 * This class represents the UI delegate for the JInternalFrame component and
 * its derivates.
 *
 * @author Taoufik Romdhane
 */
public class LiquidInternalFrameUI extends BasicInternalFrameUI {
    protected static String IS_PALETTE = "JInternalFrame.isPalette";
    private static String FRAME_TYPE = "JInternalFrame.frameType";
    private static String NORMAL_FRAME = "normal";
    private static String PALETTE_FRAME = "palette";
    private static String OPTION_DIALOG = "optionDialog";
    private static final PropertyChangeListener liquidPropertyChangeListener = new LiquidPropertyChangeHandler();

    /**
     * If this flag is set, Internal Frames can have rounded vertices.
     * Since a new DesktopManager has to be installed this is quite an
     * involving process that might cause errors. So for compatibility and
     * performance reasons this can be disabled with the property
     * Liquidlookandfeel.roundedWindows=false
     */
    static boolean allowRoundedWindows = false;
    private static DesktopManager sharedDesktopManager;
//    LiquidInternalFrameBorder frameBorder = new LiquidInternalFrameBorder();
    Border frameBorder = null;
    //UIManager.getBorder("RootPane.frameBorder");

    //	static Border disabledBorder=new LiquidInternalFrameBorder(false);

    /**
     * The metouia version of the internal frame title pane.
     */
    private LiquidInternalFrameTitlePane titlePane;

    /**
     * Creates the UI delegate for the given frame.
     *
     * @param frame The frame to create its UI delegate.
     */
    public LiquidInternalFrameUI(JInternalFrame frame) {
        super(frame);
    }

    /**
     * Creates the UI delegate for the given component.
     *
     * @param c The component to create its UI delegate.
     * @return The UI delegate for the given component.
     */
    public static ComponentUI createUI(JComponent c) {
        return new LiquidInternalFrameUI((JInternalFrame) c);
    }

    JDesktopPane getDesktopPane(JComponent frame) {
        JDesktopPane pane = null;
        Component c = frame.getParent();

        // Find the JDesktopPane
        while (pane == null) {
            if (c instanceof JDesktopPane) {
                pane = (JDesktopPane) c;
            } else if (c == null) {
                break;
            } else {
                c = c.getParent();
            }
        }

        return pane;
    }

    protected DesktopManager getDesktopManager() {
        if (!allowRoundedWindows) {
            return super.getDesktopManager();
        }

        if (sharedDesktopManager == null) {
            sharedDesktopManager = createDesktopManager();
        }

        return sharedDesktopManager;
    }

    protected DesktopManager createDesktopManager() {
        if (!allowRoundedWindows) {
            return super.createDesktopManager();
        } else {
            return new LiquidDesktopManager();
        }
    }

    public void installUI(JComponent c) {
        super.installUI(c);

        if (allowRoundedWindows) {
            frame.setOpaque(false);
        }

        if( LiquidLookAndFeel.winDecoPanther ) {
            frameBorder = UIManager.getBorder("RootPane.frameBorder");    
        }
        else {
            frameBorder = new LiquidInternalFrameBorder();
        }
        
        frame.setBorder(frameBorder);
        frame.addPropertyChangeListener(liquidPropertyChangeListener);

        Object paletteProp = c.getClientProperty(IS_PALETTE);

        if (paletteProp != null) {
            setPalette(((Boolean) paletteProp).booleanValue());
        }
    }

    public void uninstallUI(JComponent c) {
        frame.removePropertyChangeListener(liquidPropertyChangeListener);
        super.uninstallUI(c);
    }

    /**
     * Creates the north pane (the internal frame title pane) for the given frame.
     *
     * @param frame The frame to create its north pane.
     */
    protected JComponent createNorthPane(JInternalFrame frame) {
        super.createNorthPane(frame);
        titlePane = new LiquidInternalFrameTitlePane(frame);

        return titlePane;
    }

    protected void activateFrame(JInternalFrame f) {
        super.activateFrame(f);
        if( !LiquidLookAndFeel.winDecoPanther ) {
            ((LiquidInternalFrameBorder) frameBorder).setActive(true);
        }
        titlePane.activate();
    }

    /** This method is called when the frame is no longer selected.
     * This action is delegated to the desktopManager.
     */
    protected void deactivateFrame(JInternalFrame f) {
        super.deactivateFrame(f);
        if( !LiquidLookAndFeel.winDecoPanther ) {
            ((LiquidInternalFrameBorder) frameBorder).setActive(false);
        }
        titlePane.deactivate();
    }

    /**
     * Changes this internal frame mode from / to palette mode.
     * This affect only the title pane.
     *
     * @param isPalette The target palette mode.
     */
    public void setPalette(boolean isPalette) {
        if (isPalette) {
            LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
        } else {
            LookAndFeel.installBorder(frame, "InternalFrame.border");
        }

        titlePane.setPalette(isPalette);
        if( !LiquidLookAndFeel.winDecoPanther ) {
            ((LiquidInternalFrameBorder) frameBorder).isPalette = isPalette;
        }
        frame.setBorder(frameBorder);
    }

    private void stripContentBorder(Object c) {
        if (c instanceof JComponent) {
            JComponent contentComp = (JComponent) c;
            Border contentBorder = contentComp.getBorder();

            if ((contentBorder == null) || contentBorder instanceof UIResource) {
                contentComp.setBorder(new EmptyBorder(0, 0, 0, 0));
            }
        }
    }

    private void setFrameType(String frameType) {
        if (frameType.equals(OPTION_DIALOG)) {
            LookAndFeel.installBorder(frame, "InternalFrame.optionDialogBorder");
            titlePane.setPalette(false);
        } else if (frameType.equals(PALETTE_FRAME)) {
            LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
            titlePane.setPalette(true);
        } else {
            LookAndFeel.installBorder(frame, "InternalFrame.border");
            titlePane.setPalette(false);
        }
    }

    private static class LiquidPropertyChangeHandler
        implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            JInternalFrame jif = (JInternalFrame) e.getSource();

            if (!(jif.getUI() instanceof LiquidInternalFrameUI)) {
                return;
            }

            LiquidInternalFrameUI ui = (LiquidInternalFrameUI) jif.getUI();

            if (name.equals(FRAME_TYPE)) {
                if (e.getNewValue() instanceof String) {
                    ui.setFrameType((String) e.getNewValue());
                }
            } else if (name.equals(IS_PALETTE)) {
                if (e.getNewValue() != null) {
                    ui.setPalette(((Boolean) e.getNewValue()).booleanValue());
                } else {
                    ui.setPalette(false);
                }
            } else if (name.equals(JInternalFrame.CONTENT_PANE_PROPERTY)) {
                ui.stripContentBorder(e.getNewValue());
            }
        }
    }

    // end class LiquidPropertyChangeHandler
}
