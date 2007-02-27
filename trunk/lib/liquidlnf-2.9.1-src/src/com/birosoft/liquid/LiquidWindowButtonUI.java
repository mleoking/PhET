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

import com.birosoft.liquid.LiquidButtonUI;
import com.birosoft.liquid.skin.Skin;
import com.birosoft.liquid.skin.SkinSimpleButtonIndexModel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;


/**
 * A button placed in the title frame of a internal frame to enable
 * closing, iconifying and maximizing of the internal frame.
 */
public class LiquidWindowButtonUI extends LiquidButtonUI {
    /** An icon to indicate that this button closes the windows */
    public final static int CLOSE = 0;

    /** An icon to indicate that this button maximizes the windows */
    public final static int MAXIMIZE = 1;

    /** An icon to indicate that this button minmizes / iconfies the windows */
    public final static int MINIMIZE = 2;

    /** The only instance for this UI */
    /** An icon to indicate that this button minmizes / iconfies the windows */
    public final static int RESTORE = 3;
    public final static int SYSMENU = 4;
    private static final String[] files = {
            "closebutton.png", "maximizebutton.png", "minimizebutton.png",
            "restorebutton.png"
        };
    private static final String[] pantherFiles = {
            "panther-closebutton.png", "panther-maximizebutton.png",
            "panther-minimizebutton.png", "panther-restorebutton.png",
            "menu-button.png"
        };

    /** the index model for the window buttons */
    private static SkinSimpleButtonIndexModel indexModel = new SkinSimpleButtonIndexModel(0,
            1, 2, 4);
    static Skin[] skins = new Skin[5];
    boolean isRestore = true;
    int type;

    LiquidWindowButtonUI(int type) {
        this.type = type;
    }

    public static ComponentUI createUI(JComponent c) {
        throw new IllegalStateException("Must not be used this way.");
    }

    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
        Rectangle textRect, Rectangle iconRect) {
    }

    public void paint(Graphics g, JComponent c) {
        SpecialUIButton button = (SpecialUIButton) c;

        indexModel.setButton(button);

        int index = indexModel.getIndexForState();

        if (button.frame != null) {
            if (LiquidLookAndFeel.winDecoPanther && !button.frame.isSelected() &&
                    (index == 0)) {
                index = 1;
            }

            if ((button.frame.isMaximum() && (type == MAXIMIZE)) ||
               (button.frame.isIcon() && (type == MINIMIZE))) {
                getSkin(RESTORE).draw(g, index, button.getWidth(),
                    button.getHeight());
            } else {
                getSkin(type).draw(g, index, button.getWidth(),
                    button.getHeight());
            }
        } else {
            if (LiquidLookAndFeel.winDecoPanther && !button.window.isActive() &&
                    (index == 0)) {
                index = 4;
            }

            getSkin(type).draw(g, index, button.getWidth(), button.getHeight());
        }
    }

    protected static Skin getSkin(int type) {
        if (skins[type] == null) {
            if (LiquidLookAndFeel.winDecoPanther) {
                skins[type] = new Skin(pantherFiles[type], 5, 0);
            } else {
                skins[type] = new Skin(files[type], 5, 2);
            }
        }

        return skins[type];
    }

    /**
     * Creates a new Window Button UI for the specified type
     * @param type
     * @return LiquidWindowButtonUI
     */
    public static LiquidWindowButtonUI createButtonUIForType(int type) {
        return new LiquidWindowButtonUI(type);
    }

    /**
     * @see javax.swing.plaf.basic.BasicButtonUI#getPreferredSize(javax.swing.JComponent)
     */
    public Dimension getPreferredSize(JComponent c) {
        return new Dimension(getSkin(type).getHsize(), getSkin(type).getVsize());
    }
}
