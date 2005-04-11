/**
 * Class: RetardedFieldElement
 * Package: edu.colorado.phet.emf.model
 * Author: Another Guy
 * Date: May 30, 2003
 */
package edu.colorado.phet.emf.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.emf.EmfApplication;

import java.awt.geom.Point2D;

public class RetardedFieldElement extends FieldElement {

    private Vector2D fieldStrength = new Vector2D();
    private double sourceTime;
    private Electron sourceElectron;
    Point2D location;

    public RetardedFieldElement( Point2D location, Electron sourceElectron ) {
        super( location, new Vector2D( 0, 0 ) );
        this.location = location;
        this.sourceElectron = sourceElectron;
    }

    /**
     * @return
     */
    public Vector2D getFieldStrength() {

        // TODO: Using the current position here is not really accurate. We need to
        // know the position of the source electron at the time when the field element
        // that is now hitting the receiver would have been generated.
        double distanceFromSource = location.distance( sourceElectron.getCurrentPosition() );
        sourceTime = distanceFromSource / EmfApplication.s_speedOfLight;
//        Vector2D sourceField = sourceElectron.getFieldAtLocationOld( location );
        Vector2D sourceField = sourceElectron.getFieldAtLocation( location );
//        Vector2D sourceField = sourceElectron.getFieldAtTimeAgo( sourceTime );
        return sourceField;
    }
}
