// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;

/**
 * User: Sam Reid
 * Date: Jun 14, 2004
 * Time: 7:18:33 PM
 */
public class SeriesAmmeter extends CircuitComponent {
    public SeriesAmmeter( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
        setUserComponentID( CCKSimSharing.UserComponents.seriesAmmeter );
    }
}
