package edu.colorado.phet.cck.piccolo_cck.schematic;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.ACVoltageSource;
import edu.colorado.phet.cck.piccolo_cck.PhetPPath;
import edu.colorado.phet.common.math.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 20, 2004
 * Time: 11:45:11 PM
 * Copyright (c) Jun 20, 2004 by Sam Reid
 */
public class SchematicACNode extends SchematicOscillateNode {
    private PhetPPath shapeGraphic;
    private float SCALE = (float)( 1.0 / 60.0 );

    public SchematicACNode( CCKModel parent, ACVoltageSource circuitComponent, JComponent jComponent, ICCKModule module ) {
        super( parent, circuitComponent, jComponent, module, 0.3 );
        shapeGraphic = new PhetPPath( new BasicStroke( 3.0f * SCALE ), Color.black );
        addChild( shapeGraphic );
        changed();
        setVisible( true );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if( shapeGraphic != null ) {
            shapeGraphic.setVisible( visible );
        }
    }

    protected void changed() {
        super.changed();
        if( shapeGraphic != null ) {
            //draw a circle around the resistor part.
            Point2D catPoint = super.getCatPoint();
            Point2D anoPoint = getAnoPoint();
            double dist = catPoint.distance( anoPoint );
            double radius = dist / 2;
            Vector2D vec = new Vector2D.Double( catPoint, anoPoint );
            Point2D ctr = vec.getScaledInstance( .5 ).getDestination( catPoint );
            Ellipse2D.Double ellipse = new Ellipse2D.Double();
            Point2D corner = new Vector2D.Double( radius, radius ).getDestination( ctr );
            ellipse.setFrameFromCenter( ctr, corner );
            shapeGraphic.setPathTo( ellipse );

        }
    }

}
