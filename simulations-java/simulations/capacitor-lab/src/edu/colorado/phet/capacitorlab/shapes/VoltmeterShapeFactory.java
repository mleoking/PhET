/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.shapes;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.model.Voltmeter;
import edu.colorado.phet.common.phetcommon.math.Point3D;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Creates 2D shapes associated with the voltmeter.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltmeterShapeFactory {
    
    /*
     * Sizes determined by visual inspection of the associated image files.
     * To see the corresponding bounds, run the sim with -dev.
     */
    private final PDimension PROBE_TIP_SIZE = new PDimension( 0.0005, 0.0015 );
    
    private final Voltmeter voltmeter;
    
    public VoltmeterShapeFactory( Voltmeter voltmeter ) {
        this.voltmeter = voltmeter;
    }

    /**
     * This shape will be drawn by the voltmeter probe node, for debugging purposes.
     * Its rotation here is zero because rotation is handled by the node. 
     * @return
     */
    public Shape getPositiveProbeTipShapeLocal() {
        return getProbeTipShape( new Point3D.Double(), 0 );
    }
    
    public Shape getPositiveProbeTipShapeWorld() {
        return getProbeTipShape( voltmeter.getPositiveProbeLocationReference(), -CLConstants.MVT_YAW );
    }
    
    /**
     * This shape will be drawn by the voltmeter probe node, for debugging purposes.
     * Its rotation here is zero because rotation is handled by the node. 
     * @return
     */
    public Shape getNegativeProbeTipShapeLocal() {
        return getProbeTipShape( new Point3D.Double(), 0 );
    }
    
    public Shape getNegativeProbeTipShapeWorld() {
        return getProbeTipShape( voltmeter.getNegativeProbeLocationReference(), -CLConstants.MVT_YAW );
    }
    
    private Shape getProbeTipShape( Point3D origin, double theta ) {
        double x = origin.getX() - ( PROBE_TIP_SIZE.getWidth() / 2 );
        double y = origin.getY();
        Rectangle2D r = new Rectangle2D.Double( x, y, PROBE_TIP_SIZE.getWidth(), PROBE_TIP_SIZE.getHeight() );
        AffineTransform t = AffineTransform.getRotateInstance( theta, origin.getX(), origin.getY() );
        return t.createTransformedShape( r );
    }
}
