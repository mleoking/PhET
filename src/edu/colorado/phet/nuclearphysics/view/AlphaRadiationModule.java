/**
 * Class: AlphaRadiationModule
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 11:58:03 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.AlphaSetter;
import edu.colorado.phet.nuclearphysics.controller.AlphaDecayControlPanel;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.DecayListener;
import edu.colorado.phet.nuclearphysics.model.DecayProducts;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class AlphaRadiationModule extends OneNucleusModule implements DecayListener {

    //
    // Statics
    //
    private static int numAlphaParticles = 4;


    public AlphaRadiationModule( AbstractClock clock ) {
        super( "Alpha Radiation", clock );
        super.addControlPanelElement( new AlphaDecayControlPanel( this ) );

        getNucleus().addDecayListener( this );

        for( int i = 0; i < getNucleus().getAlphaParticles().length; i++ ) {
            addAlphaParticle( getNucleus().getAlphaParticles()[i] );
        }
        addRingGraphic();
    }

    private void addRingGraphic() {
        // Add a ring around the nucleus to show where its alpha decay radius is
        double radius = Math.abs( getNucleus().getPotentialProfile().getAlphaDecayX() ) + AlphaParticle.RADIUS;
        double x = getNucleus().getLocation().getX() - radius;
        double y = getNucleus().getLocation().getY() - radius;
        final Ellipse2D.Double alphaRing = new Ellipse2D.Double( x, y, radius * 2, radius * 2 );
        Graphic ringGraphic = new Graphic() {
            public void paint( Graphics2D g ) {
                GraphicsUtil.setAntiAliasingOn( g );
                AlphaSetter.set( g, 0.3 );
                g.setColor( Color.blue );
                g.setStroke( new BasicStroke( 2f ) );
                g.draw( alphaRing );
                AlphaSetter.set( g, 1 );
            }
        };
        this.getPotentialProfilePanel().addOriginCenteredGraphic( ringGraphic );
    }


    public void alphaDecay( DecayProducts decayProducts ) {
        getPotentialProfilePanel().addDecayProduct( decayProducts.getN2() );
        getPhysicalPanel().addNucleus( decayProducts.getN2() );
        getModel().addModelElement( decayProducts.getN2() );
//        super.handleDecay( decayProducts );
    }

    public void run() {
        super.run();
        getNucleus().addDecayListener( this );
        for( int i = 0; i < getNucleus().getAlphaParticles().length; i++ ) {
            addAlphaParticle( getNucleus().getAlphaParticles()[i] );
        }
        addRingGraphic();
    }
}
