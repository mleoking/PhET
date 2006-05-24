/**
 * Class: NucleusGraphicFactory
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 19, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.model.Uranium238;
import edu.colorado.phet.nuclearphysics.model.Uranium239;

public class NucleusGraphicFactory {

    public static NucleusGraphic create( Nucleus nucleus ) {

        if( nucleus instanceof Uranium235 ) {
            return new Uranium235Graphic( nucleus );
        }
        if( nucleus instanceof Uranium238 ) {
            return new Uranium238Graphic( nucleus );
        }
        if( nucleus instanceof Uranium239 ) {
            return new Uranium239Graphic( nucleus );
        }
        else {
            return new NucleusGraphic( nucleus );
        }
    }
}
