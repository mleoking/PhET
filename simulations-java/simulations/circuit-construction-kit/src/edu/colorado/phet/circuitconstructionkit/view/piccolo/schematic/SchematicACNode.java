// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.ACVoltageSource;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 20, 2004
 * Time: 11:45:11 PM
 */
public class SchematicACNode extends SchematicOscillateNode {
    private PhetPPath shapeGraphic;
    private float SCALE = (float) (1.0 / 60.0);

    public SchematicACNode(CCKModel parent, ACVoltageSource circuitComponent, JComponent jComponent, CCKModule module) {
        super(parent, circuitComponent, jComponent, module, 0.3);
        shapeGraphic = new PhetPPath(new BasicStroke(3.0f * SCALE), Color.black);
        addChild(shapeGraphic);
        changed();
        setVisible(true);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (shapeGraphic != null) {
            shapeGraphic.setVisible(visible);
        }
    }

    protected void changed() {
        super.changed();
        if (shapeGraphic != null) {
            //draw a circle around the resistor part.
            Point2D catPoint = super.getCatPoint();
            Point2D anoPoint = getAnoPoint();
            double dist = catPoint.distance(anoPoint);
            double radius = dist / 2;
            Vector2D vec = new Vector2D(catPoint, anoPoint);
            Point2D ctr = vec.getScaledInstance(.5).getDestination(catPoint);
            Ellipse2D.Double ellipse = new Ellipse2D.Double();
            Point2D corner = new Vector2D(radius, radius).getDestination(ctr);
            ellipse.setFrameFromCenter(ctr, corner);
            shapeGraphic.setPathTo(ellipse);

        }
    }

}
