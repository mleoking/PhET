/**
 * Class: CompositeAffineTransform
 * Package: edu.colorado.phet.coreadditions.graphics
 * Author: Another Guy
 * Date: Oct 23, 2003
 */
package edu.colorado.phet.coreadditions.graphics;

import java.awt.geom.AffineTransform;
import java.util.HashMap;

public class CompositeAffineTransform extends AffineTransform {

    private HashMap txMap = new HashMap();

    public void addTx( Class type, AffineTransform tx ) {
        txMap.put( type, tx );
    }

    public AffineTransform getTx( Class type ) {
        return (AffineTransform)txMap.get( type );
    }
}
