/**
 * Class: PotentialProfilePanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.RevertableGraphicsSetup;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;

import java.awt.*;
import java.util.HashMap;

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
    private HashMap particleToGraphicMap = new HashMap();

    public AlphaDecayPhysicalPanel() {
        this.setBackground( backgroundColor );
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        // Set the origin
        origin.setLocation( this.getWidth() / 2, this.getHeight() / 2 );
        originTx.setToTranslation( origin.getX(), origin.getY() );

        // Draw everything that isn't special to this panel
//        GraphicsUtil.setAlpha( g2, 0.5 );
        g2.setColor( backgroundColor );
        super.paintComponent( g2 );

        GraphicsUtil.setAlpha( g2, 1 );
    }

    public synchronized void addAlphaParticle( AlphaParticle alphaParticle ) {
        NucleusGraphic graphic = new NucleusGraphic( alphaParticle );
        this.addOriginCenteredGraphic( graphic );
        particleToGraphicMap.put( alphaParticle, graphic );
    }

    public synchronized void removeAlphaParticle( AlphaParticle alphaParticle ) {
        Graphic graphic = (Graphic)particleToGraphicMap.get( alphaParticle );
        this.removeGraphic( graphic );
    }

    public void clear() {
        this.decayGraphic = null;
        this.removeAllGraphics();
    }
}
