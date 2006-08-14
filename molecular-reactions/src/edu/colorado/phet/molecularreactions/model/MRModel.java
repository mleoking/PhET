/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.collision.Box2D;
import edu.colorado.phet.collision.SphereBoxExpert;
import edu.colorado.phet.collision.SphereSphereExpert;
import edu.colorado.phet.collision.SolidSphere;

import java.util.List;
import java.util.Iterator;
import java.awt.geom.Point2D;

/**
 * MRModel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class MRModel extends PublishingModel {
    private Box2D box;

    public MRModel( IClock clock ) {
        super( clock );

        // Add a box
        box = new Box2D( new Point2D.Double( 50, 50 ),
                         new Point2D.Double( 250, 250 ) );
        addModelElement( box );

        // Create collisions agents that will detect and handle collisions between molecules,
        // and between molecules and the box
        addModelElement( new CollisionAgent() );
    }


    private class CollisionAgent implements ModelElement {
        SphereBoxExpert sphereBoxExpert = new SphereBoxExpert();
        SphereSphereExpert sphereSphereExpert = new SphereSphereExpert();

        public void stepInTime( double dt ) {
            List modelElements = getModelElements();
            for( int i = modelElements.size() - 1; i >= 0; i-- ) {
                Object o = modelElements.get( i );
                if( o instanceof SolidSphere ) {
                    sphereBoxExpert.detectAndDoCollision( (SolidSphere)o,
                                                          box );
                    for( int j = modelElements.size() - 1; j >= 0; j-- ) {
                        Object o2 = modelElements.get( j );
                        if( o2 instanceof SolidSphere && o2 != o ) {
                            sphereSphereExpert.detectAndDoCollision( (SolidSphere)o,
                                                                     (SolidSphere)o2 );
                        }
                    }
                }
            }
        }
    }
}
