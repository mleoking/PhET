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
import com.birosoft.liquid.skin.SkinToggleButtonIndexModel;

import java.awt.Component;
import java.awt.Graphics;

import java.io.Serializable;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.plaf.UIResource;


/**
 * This class represents a check box icon.
 *
 * @author Taoufik Romdhane
 */
public class LiquidCheckBoxIcon implements Icon, UIResource, Serializable {
    protected static Skin skin;
    private SkinToggleButtonIndexModel indexModel = new SkinToggleButtonIndexModel();

    /**
     * Draws the check box icon at the specified location.
     *
     * @param c The component to draw on.
     * @param g The graphics context.
     * @param x The x coordinate of the top left corner.
     * @param y The y coordinate of the top left corner.
     */
    protected int getControlSize() {
        return getIconWidth();
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        AbstractButton button = (AbstractButton) c;

        indexModel.setButton(button);

        int index = indexModel.getIndexForState();
        getSkin().draw(g, index, x, y, getSkin().getHsize(),
            getSkin().getVsize());
    }

    public int getIconWidth() {
        return getSkin().getHsize();
    }

    public int getIconHeight() {
        return getSkin().getVsize();
    }

    public Skin getSkin() {
        if (skin == null) {
            skin = new Skin("checkbox.png", 8, 0);
        }

        return skin;
    }
}
