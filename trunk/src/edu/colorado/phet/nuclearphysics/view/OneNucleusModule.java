/**
 * Class: OneNucleusModule
 * Package: edu.colorado.phet.nuclearphysics.view
 * Author: Another Guy
 * Date: Mar 7, 2004
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.controller.NuclearPhysicsModule;
import edu.colorado.phet.nuclearphysics.model.Uranium235;

import java.awt.geom.Point2D;

public class OneNucleusModule extends NuclearPhysicsModule {
    //
    // Instance fields and methods
    //
    private Uranium235 nucleus;

    public OneNucleusModule( String name, AbstractClock clock ) {
        super( name, clock );
        nucleus = new Uranium235( new Point2D.Double( 0, 0 ) );
        setUraniumNucleus( getNucleus() );
    }

    protected Uranium235 getNucleus() {
        return nucleus;
    }

    public void clear() {
        getPhysicalPanel().clear();
        getPotentialProfilePanel().clear();
        this.getModel().removeModelElement( nucleus );
    }

    public void run() {
        clear();
        nucleus = new Uranium235( new Point2D.Double( 0, 0 ) );
        setUraniumNucleus( nucleus );
    }
}
