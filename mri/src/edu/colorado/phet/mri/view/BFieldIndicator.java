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

/**
 * BFieldIndicator
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldIndicator extends PNode {
    private double maxLength = 200;

    public BFieldIndicator( Electromagnet magnet ) {
        magnet.addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                update( event.getElectromagnet());
            }
        } );
        update( magnet );
    }

    private void update( Electromagnet magnet ) {
        double field = magnet.getFieldStrength();
        double length = ( field / MriConfig.MAX_FADING_COIL_FIELD ) * maxLength;
        length = Math.min( length, maxLength );
        length = Math.max( length, 20 );
        Arrow bFieldArrow = new Arrow( new Point2D.Double( 0, -length / 2 ),
                                       new Point2D.Double( 0, length / 2),
                                       20, 20, 10 );
        PPath arrowPPath = new PPath( bFieldArrow.getShape() );
        this.addChild( arrowPPath );
    }
}
