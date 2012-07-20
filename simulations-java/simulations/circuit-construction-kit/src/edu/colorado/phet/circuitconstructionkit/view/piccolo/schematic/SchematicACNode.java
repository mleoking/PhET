// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.ACVoltageSource;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * User: Sam Reid
 * Date: Jun 20, 2004
 * Time: 11:45:11 PM
 */
public class SchematicACNode extends SchematicOscillateNode {
    private PhetPPath shapeGraphic;
    private float SCALE = (float) ( 1.0 / 60.0 );

    public SchematicACNode( CCKModel parent, ACVoltageSource circuitComponent, JComponent jComponent, CCKModule module ) {
        super( parent, circuitComponent, jComponent, module, 0.3 );
        shapeGraphic = new PhetPPath( new BasicStroke( 3.0f * SCALE ), Color.black );
        addChild( shapeGraphic );
        changed();
        setVisible( true );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( shapeGraphic != null ) {
            shapeGraphic.setVisible( visible );
        }
    }

    protected void changed() {
        super.changed();
        if ( shapeGraphic != null ) {
            //draw a circle around the resistor part.
            Point2D catPoint = super.getCatPoint();
            Point2D anoPoint = getAnoPoint();
            double dist = catPoint.distance( anoPoint );
            double radius = dist / 2;
            MutableVector2D vec = new MutableVector2D( catPoint, anoPoint );
            Point2D ctr = vec.times( .5 ).getDestination( catPoint );
            Ellipse2D.Double ellipse = new Ellipse2D.Double();
            Point2D corner = new MutableVector2D( radius, radius ).getDestination( ctr );
            ellipse.setFrameFromCenter( ctr, corner );
            shapeGraphic.setPathTo( ellipse );

        }
    }

}
