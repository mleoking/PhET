/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.elements.branch.components.HasResistance;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Apr 21, 2003
 * Time: 7:57:31 PM
 * Copyright (c) Apr 21, 2003 by Sam Reid
 */
public class CurrentReadout implements Graphic {
    boolean visible = true;
    ImageBranchGraphic resistorGraphic;
    private HasResistance hr;
    private Font font;

    public CurrentReadout(boolean visible, ImageBranchGraphic resistorGraphic) {
        this.visible = visible;
        this.resistorGraphic = resistorGraphic;
        hr = (HasResistance) resistorGraphic.getBranch();
        this.font = new Font("Dialog", 0, 18);
    }

    public boolean containsRelativePoint(int xrel, int yrel) {
        return false;
    }

    public void setVisible(boolean selected) {
        this.visible = selected;
    }

    DecimalFormat df = new DecimalFormat("#0.0#");

    public void paint(Graphics2D g) {
        if (!visible)
            return;
        AffineTransform at = g.getTransform();
        AffineTransform newTransform = resistorGraphic.getImageTransform();
        g.transform(newTransform);
//        int resistance = (int) hr.getResistance();//resistorGraphic.getBranch().getint) ((Resistor) w).getResistance();
        double current = resistorGraphic.getBranch().getCurrent();
        double abs = Math.abs(current);
        String str = df.format(abs) + " Amps";
        g.setFont(font);
        float height = (float) font.getStringBounds(str, g.getFontRenderContext()).getHeight();
        g.setColor(Color.black);
        g.drawString(str, 20, height * 3);

        g.setTransform(at);
    }

}
