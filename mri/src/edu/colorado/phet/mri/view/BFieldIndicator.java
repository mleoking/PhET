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
import edu.colorado.phet.common.math.MathUtil;
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
    private GradientElectromagnet magnet;
    private double xLoc;
    private double minLength;

    /**
     * Constructor
     * @param magnet
     * @param maxLength
     * @param fill
     */
    public BFieldIndicator( GradientElectromagnet magnet, double maxLength, Paint fill ) {
        this( magnet, maxLength, fill, 0 );
    }

    /**
     * Constructor
     *
     * @param magnet
     * @param maxLength
     * @param fill
     * @param xLoc The location along the length of the magnet for which this indicator shows the
     * strength of the field
     */
    public BFieldIndicator( GradientElectromagnet magnet, double maxLength, Paint fill, double xLoc ) {
        this.magnet = magnet;
        this.xLoc = xLoc;
        this.maxLength = maxLength;
        this.arrowColor = fill;
        magnet.addChangeListener( new Electromagnet.ChangeListener() {
            public void stateChanged( Electromagnet.ChangeEvent event ) {
                update();
            }
        } );
        update();
    }

    public void setMaxLength( double maxLength ) {
        this.maxLength = maxLength;
    }

    public void setMinLength( double minLength ) {
        this.minLength = minLength;
        update();
    }

    private void update() {
        if( arrowPPath != null ) {
            removeChild( arrowPPath );
        }
        double field = magnet.getFieldStrengthAt( xLoc);
        // Use sign to determine if arrow points up or down
        int sign = MathUtil.getSign( field );
        double length = Math.abs( field / MriConfig.MAX_FADING_COIL_FIELD ) * maxLength;
        length = Math.max( minLength, Math.min( length, maxLength ));
        Arrow bFieldArrow = new Arrow( new Point2D.Double( 0, ( length * sign) / 2 ),
                                       new Point2D.Double( 0, ( length * -sign )/ 2 ),
                                       length / 2, length / 2, length / 4, .5, true );
        arrowPPath = new PPath( bFieldArrow.getShape() );
        if( arrowColor != null ) {
            arrowPPath.setPaint( arrowColor );
        }
        this.addChild( arrowPPath );
    }
}
