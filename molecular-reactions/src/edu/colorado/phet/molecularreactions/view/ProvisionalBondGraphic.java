/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.model.ProvisionalBond;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.PublishingModel;
import edu.colorado.phet.molecularreactions.util.ModelElementGraphicManager;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.model.ModelElement;

import java.awt.geom.Point2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.*;

/**
 * ProvisionalBondGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBondGraphic extends PNode implements SimpleObserver {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static int numColors = 50;
    private static Color[] colors = new Color[numColors];

    static {
        for( int i = 0; i < colors.length; i++ ) {
            colors[i] = new Color( 255, 0, 0, (int)( 255 * ( (double)(colors.length - i ) / numColors ) ) );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------
    
    private Stroke stroke = new BasicStroke( 3 );
    private Line2D line;
    private ProvisionalBond bond;
    private PPath linePath;

    public ProvisionalBondGraphic( ProvisionalBond bond ) {
        bond.addObserver( this );
        this.bond = bond;
        line = new Line2D.Double();
        linePath = new PPath( line, stroke );
        linePath.setStrokePaint( Color.red );
        addChild( linePath );
        update();
    }

    public void update() {
        Point2D p1 = bond.getMolecules()[0].getPosition();
        Point2D p2 = bond.getMolecules()[1].getPosition();
        line.setLine( p1, p2 );
        linePath.setPathTo( line );
        double d = p2.distance( p1 ) - bond.getMolecules()[0].getRadius() - bond.getMolecules()[1].getRadius();
        int colorIdx = (int)( numColors * ( d / bond.getMaxBondLength() )) - 1;
        colorIdx = Math.max( 0, Math.min( colorIdx, colors.length - 1 ));
        linePath.setStrokePaint( colors[ colorIdx ]);

        if( p1.distance( p2 ) > 100 ) {
            System.out.println( "ProvisionalBondGraphic.update" );
        }
    }
}
