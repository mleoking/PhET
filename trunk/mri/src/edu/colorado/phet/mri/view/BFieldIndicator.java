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

import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.Electromagnet;
import edu.colorado.phet.mri.model.GradientElectromagnet;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * BFieldIndicator
 * <p/>
 * An arrow graphic that grows and shrinks with the magnitude of a specified
 * Electromagnet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldIndicator extends PNode {
    private double maxLength;
    private PPath arrowPPath;
    private Paint arrowColor;
    private double xLoc;

    public BFieldIndicator( GradientElectromagnet magnet, double maxLength, Paint fill ) {
        this( magnet, maxLength, fill, 0 );
    }

    public BFieldIndicator( GradientElectromagnet magnet, double maxLength, Paint fill, double xLoc ) {
        this.xLoc = xLoc;
        this.maxLength = maxLength;
        this.arrowColor = fill;
        magnet.addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                update( (GradientElectromagnet)event.getElectromagnet() );
            }
        } );
        update( magnet );
    }

    public void setMaxLength( double maxLength ) {
        this.maxLength = maxLength;
    }

    private void update( GradientElectromagnet magnet ) {
        if( arrowPPath != null ) {
            removeChild( arrowPPath );
        }
        double field = magnet.getFieldStrengthAt( xLoc);
        double length = ( field / MriConfig.MAX_FADING_COIL_FIELD ) * maxLength;
        length = Math.min( length, maxLength );
        Arrow bFieldArrow = new Arrow( new Point2D.Double( 0, length / 2 ),
                                       new Point2D.Double( 0, -length / 2 ),
                                       length / 2, length / 2, length / 4, .5, true );
        arrowPPath = new PPath( bFieldArrow.getShape() );
        if( arrowColor != null ) {
            arrowPPath.setPaint( arrowColor );
        }
        this.addChild( arrowPPath );
    }
}
