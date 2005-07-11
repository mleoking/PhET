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

import java.awt.*;

public class NucleusGraphicFactory {

    public static NucleusGraphic create( Component component, Nucleus nucleus ) {

        if( nucleus instanceof Uranium235 ) {
            return new Uranium235Graphic( component, nucleus );
        }
        if( nucleus instanceof Uranium238 ) {
            return new Uranium238Graphic( component, nucleus );
        }
        if( nucleus instanceof Uranium239 ) {
            return new Uranium239Graphic( component, nucleus );
        }
        else {
            return new NucleusGraphic( component, nucleus );
        }
    }
}
