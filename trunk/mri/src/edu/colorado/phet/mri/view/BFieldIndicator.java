/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.piccolo.PhetPCanvas;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.MriConfig;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.geom.Point2D;
import java.awt.*;

/**
 * BFieldIndicator
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldIndicator extends PNode {
    private double maxLength;
    private PPath arrowPPath;

    public BFieldIndicator( Electromagnet magnet, double maxLength ) {
        this.maxLength = maxLength;
        magnet.addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                update( event.getElectromagnet());
            }
        } );
        update( magnet );
    }

    public void setMaxLength( double maxLength ) {
        this.maxLength = maxLength;
    }

    private void update( Electromagnet magnet ) {
        if( arrowPPath != null ) {
            removeChild( arrowPPath );
        }
        double field = magnet.getFieldStrength();
        double length = ( field / MriConfig.MAX_FADING_COIL_FIELD ) * maxLength;
        length = Math.min( length, maxLength );
//        length = Math.max( length, 0 );
        Arrow bFieldArrow = new Arrow( new Point2D.Double( 0, length / 2 ),
                                       new Point2D.Double( 0, -length / 2),
                                       length / 4, length / 4, length / 8, .5, true );
//                                       20, 20, 10, .5, true );
        arrowPPath = new PPath( bFieldArrow.getShape() );
        arrowPPath.setPaint( new Color( 80, 80, 180 ));
        this.addChild( arrowPPath );
    }
}
