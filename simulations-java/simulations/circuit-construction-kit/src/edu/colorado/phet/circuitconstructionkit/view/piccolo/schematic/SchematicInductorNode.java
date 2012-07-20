// Copyright 2002-2012, University of Colorado

/*  */
package edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import javax.swing.JComponent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.CircuitComponent;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * User: Sam Reid
 * Date: May 25, 2004
 * Time: 8:34:54 PM
 */
public class SchematicInductorNode extends SchematicOscillateNode {
    private PhetPPath leftBranch;
    private PhetPPath rightBranch;
    private float SCALE = (float) ( 1.0 / 60.0 );

    public SchematicInductorNode( CCKModel parent, CircuitComponent component, JComponent jComponent, CCKModule module ) {
        super( parent, component, jComponent, module, 0.3 );
        leftBranch = new PhetPPath( new BasicStroke( 6.0f * SCALE ), Color.black );
        rightBranch = new PhetPPath( new BasicStroke( 6.0f * SCALE ), Color.black );
        addChild( leftBranch );
        addChild( rightBranch );
        changed();
        setVisible( true );
    }

    protected double getY( double x, double dist, double fracDistToStartSine, double omega ) {
        return Math.abs( 15 * Math.sin( ( x - dist * fracDistToStartSine ) * omega * 1.5 ) * SCALE );
    }

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        if ( leftBranch != null ) {
            leftBranch.setVisible( visible );
        }
    }

    protected void changed() {
        super.changed();
        if ( leftBranch != null ) {
            //draw a circle around the resistor part.
            Point2D catPoint = super.getCatPoint();
            Point2D anoPoint = getAnoPoint();
            MutableVector2D v = new MutableVector2D( catPoint, anoPoint );
            Vector2D s = v.getScaledInstance( super.getFracDistToStartSine() );
            Point2D dst = s.getDestination( catPoint );
            leftBranch.setPathTo( new Line2D.Double( catPoint, dst ) );

            Point2D d2 = s.getScaledInstance( -1 ).getDestination( anoPoint );
            rightBranch.setPathTo( new Line2D.Double( anoPoint, d2 ) );
        }
    }

}
