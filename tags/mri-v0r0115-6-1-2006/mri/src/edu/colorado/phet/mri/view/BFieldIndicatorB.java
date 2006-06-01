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

import edu.colorado.phet.common.math.MathUtil;
import edu.colorado.phet.common.view.graphics.Arrow;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.MriModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * BFieldIndicatorB
 * <p/>
 * An arrow graphic that grows and shrinks with the magnitude of a specified
 * Electromagnet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class BFieldIndicatorB extends PNode {
    private double maxLength;
    private PPath arrowPPath;
    private Paint arrowColor = new Color( 255, 255, 255, 50 );
    private double minLength;
    private Point2D location;
    private double strokeOpacity = 0.5;
    private Color strokePaint = new Color( 0, 0, 0, (int)( strokeOpacity * 255 ) );

    /**
     * Constructor
     *
     * @param model
     * @param location
     * @param maxLength
     * @param fill      strength of the field
     */
    public BFieldIndicatorB( MriModel model, Point2D location, double maxLength, Paint fill ) {
        this.location = location;
        this.maxLength = maxLength;
        this.arrowColor = fill;
        model.addListener( new MriModel.ChangeAdapter() {
            public void fieldChanged( MriModel model ) {
                update( model );
            }
        } );
        update( model );
    }

    private void update( MriModel model ) {
        if( arrowPPath != null ) {
            removeChild( arrowPPath );
        }
        double field = model.getTotalFieldStrengthAt( location );
        // Use sign to determine if arrow points up or down
        int sign = MathUtil.getSign( field );
        double length = Math.abs( field / ( MriConfig.MAX_FADING_COIL_FIELD ) ) * maxLength;
        length = Math.max( minLength, Math.min( length, maxLength ) );
        Arrow bFieldArrow = new Arrow( new Point2D.Double( 0, ( length * sign ) / 2 ),
                                       new Point2D.Double( 0, ( length * -sign ) / 2 ),
                                       length / 2, length / 2, length / 4, .5, true );
        arrowPPath = new PPath( bFieldArrow.getShape() );
        if( arrowColor != null ) {
            arrowPPath.setPaint( arrowColor );
        }
        arrowPPath.setStrokePaint( strokePaint );
        this.addChild( arrowPPath );
    }
}
