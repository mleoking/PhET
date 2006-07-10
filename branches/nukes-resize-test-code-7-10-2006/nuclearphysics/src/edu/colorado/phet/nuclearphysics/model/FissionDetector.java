/**
 * Class: FissionDetector
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Mar 18, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;

public class FissionDetector implements ModelElement {
    private BaseModel model;

    public FissionDetector( BaseModel model ) {
        this.model = model;
    }

    public void stepInTime( double dt ) {
        for( int i = 0; i < model.numModelElements(); i++ ) {
            ModelElement me = model.modelElementAt( i );
            if( me instanceof Neutron ) {
                for( int j = 0; j < model.numModelElements(); j++ ) {
                    ModelElement me2 = model.modelElementAt( j );
                    if( me2 instanceof Uranium235 ) {
                        if( ( (Neutron)me ).getPosition().distanceSq( ( (Nucleus)me2 ).getPosition() )
                            < ( (Nucleus)me2 ).getRadius() * ( (Nucleus)me2 ).getRadius() ) {
                            //                            fission( (Nucleus)me2, (Neutron)me );
                        }
                    }
                }
            }
        }
    }
}
