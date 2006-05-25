/**
 * Class: PotentialProfilePanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.GraphicsSetup;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.NuclearModelElement;

import java.awt.*;
import java.awt.geom.Point2D;

public class AlphaDecayPhysicalPanel extends PhysicalPanel {

    private static double nucleusLayer = 20;
    private static GraphicsSetup decayProductGraphicsSetup = new GraphicsSetup() {
        public void setup( Graphics2D graphics ) {
            GraphicsUtil.setAlpha( graphics, 0.8 );
        }
    };


    private double alphaParticleLevel = Config.alphaParticleLevel;

    public AlphaDecayPhysicalPanel( IClock clock ) {
        super( clock );
        this.setBackground( backgroundColor );
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        // Set the origin
        setOrigin( new Point2D.Double( this.getWidth() / 2, this.getHeight() / 2 ) );
        getOriginTx().setToTranslation( getOrigin().getX(), getOrigin().getY() );

        // Draw everything that isn't special to this panel
        //        GraphicsUtil.setAlpha( g2, 0.5 );
        g2.setColor( backgroundColor );
        super.paintComponent( g2 );

        GraphicsUtil.setAlpha( g2, 1 );
    }

    public synchronized void addAlphaParticle( final AlphaParticle alphaParticle ) {
        final NucleusGraphic graphic = new NucleusGraphic( alphaParticle );
        this.addOriginCenteredGraphic( graphic, alphaParticleLevel );
        alphaParticle.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                AlphaDecayPhysicalPanel.this.removeGraphic( graphic );
                alphaParticle.removeListener( this );
            }
        } );
    }
}
