// todo: Apply a model transform to this panel, so we don't have to
// work in view coordinates all the time.

/**
 * Class: PotentialProfilePanelOld
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
// todo: Apply a model transform to this panel, so we don't have to
// work in view coordinates all the time.

/**
 * Class: PotentialProfilePanelOld
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.RevertableGraphicsSetup;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.DecayNucleus;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class AlphaDecayPhysicalPanel extends PhysicalPanel {

    private static double nucleusLayer = 20;
    private static RevertableGraphicsSetup nucleusGraphicsSetup = new RevertableGraphicsSetup() {
        private Composite orgComposite;

        public void setup( Graphics2D graphics ) {
            orgComposite = graphics.getComposite();
            GraphicsUtil.setAlpha( graphics, 0.5 );
        }

        public void revert( Graphics2D graphics ) {
            graphics.setComposite( orgComposite );
        }
    };
    private static GraphicsSetup decayProductGraphicsSetup = new GraphicsSetup() {
        public void setup( Graphics2D graphics ) {
            GraphicsUtil.setAlpha( graphics, 0.8 );
        }
    };

    //
    // Instance fields and methods
    //
    private NucleusGraphic decayGraphic;

    public AlphaDecayPhysicalPanel() {
        this.setBackground( backgroundColor );
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        // Set the origin
        origin.setLocation( this.getWidth() / 2, this.getHeight() / 2 );
        originTx.setToTranslation( origin.getX(), origin.getY() );

        // Draw everything that isn't special to this panel
        GraphicsUtil.setAlpha( g2, 0.5 );
        g2.setColor( backgroundColor );
        super.paintComponent( g2 );

        if( decayGraphic != null ) {
            AffineTransform orgTx = g2.getTransform();
            DecayNucleus nucleus = (DecayNucleus)decayGraphic.getNucleus();
            g2.transform( originTx );
            decayGraphic.paint( g2, (int)nucleus.getLocation().getX(), (int)nucleus.getLocation().getY() );
            g2.setTransform( orgTx );
        }

        GraphicsUtil.setAlpha( g2, 1 );
    }

    public synchronized void addDecayProduct( Nucleus decayNucleus ) {
        this.decayGraphic = new NucleusGraphic( decayNucleus );
    }

    public synchronized void addAlphaParticle( AlphaParticle alphaParticle ) {
        NucleusGraphic graphic = new NucleusGraphic( alphaParticle );
        this.addOriginCenteredGraphic( graphic );
    }

    public void clear() {
        this.decayGraphic = null;
        this.removeAllGraphics();
    }
}
