/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck;

import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.circuit.CircuitGraphic;
import edu.colorado.phet.cck.elements.circuit.JunctionGroup;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * User: Sam Reid
 * Date: Nov 21, 2003
 * Time: 10:45:47 AM
 * Copyright (c) Nov 21, 2003 by Sam Reid
 */
public class JunctionCoverUpGraphic implements Graphic {
    private CircuitGraphic circuitGraphic;
    private ModelViewTransform2d transform;
    Ellipse2D.Double ella = new Ellipse2D.Double();
    double radius;
//    Color color=DefaultCompositeBranchGraphic.JUNCTION_COLOR;
    Color color = new Color(CCK2Module.COPPER.getRed()+15,CCK2Module.COPPER.getGreen()+35,CCK2Module.COPPER.getBlue()+35);

    public JunctionCoverUpGraphic(CircuitGraphic circuitGraphic, ModelViewTransform2d transform, double radius) {
        this.circuitGraphic = circuitGraphic;
        this.transform = transform;
        this.radius = radius;
    }

    public void paint(Graphics2D g) {
        g.setColor(color);
        Circuit c = circuitGraphic.getCircuit();
        JunctionGroup[] jg = c.getJunctionGroups();
        for (int i = 0; i < jg.length; i++) {
            JunctionGroup junctionGroup = jg[i];
            Point loc = transform.modelToView(junctionGroup.getLocation());
            ella.setFrameFromCenter(loc.x, loc.y, loc.x + radius, loc.y + radius);
            g.fill(ella);
        }
    }
}
