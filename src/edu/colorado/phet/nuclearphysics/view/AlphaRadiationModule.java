/**
 * Class: AlphaRadiationModule
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 11:58:03 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayControlPanel;
import edu.colorado.phet.nuclearphysics.controller.NuclearPhysicsModule;
import edu.colorado.phet.nuclearphysics.model.DecayListener;
import edu.colorado.phet.nuclearphysics.model.DecayProducts;
import edu.colorado.phet.nuclearphysics.model.Uranium235;

import java.awt.geom.Point2D;

public class AlphaRadiationModule extends NuclearPhysicsModule implements DecayListener {

    //
    // Statics
    //
    private static int numAlphaParticles = 4;


    //
    // Instance fields and methods
    //
    private Uranium235 nucleus;

    public AlphaRadiationModule( AbstractClock clock ) {
        super( "Alpha Radiation", clock );
        super.addControlPanelElement( new AlphaDecayControlPanel( this ) );

        nucleus = new Uranium235( new Point2D.Double( 0, 0 ) );
//        nucleus = new Uranium235( new Point2D.Double( 200, 400 ) );
        setUraniumNucleus( nucleus );
        nucleus.addDecayListener( this );

        for( int i = 0; i < nucleus.getAlphaParticles().length; i++ ) {
            addAlphaParticle( nucleus.getAlphaParticles()[i] );
        }
    }

    public void alphaDecay( DecayProducts decayProducts ) {
        getPotentialProfilePanel().addDecayProduct( decayProducts.getN2() );
        super.handleDecay( decayProducts );
    }

    protected void clear() {
        getPhysicalPanel().clear();
        getPotentialProfilePanel().clear();
        this.getModel().removeModelElement( nucleus );
    }

    public void run() {
        clear();

        nucleus = new Uranium235( new Point2D.Double( 0, 0 ) );
//        nucleus = new Uranium235( new Point2D.Double( 200, 400 ) );
        setUraniumNucleus( nucleus );
        nucleus.addDecayListener( this );

        for( int i = 0; i < nucleus.getAlphaParticles().length; i++ ) {
            addAlphaParticle( nucleus.getAlphaParticles()[i] );
        }
    }
}
