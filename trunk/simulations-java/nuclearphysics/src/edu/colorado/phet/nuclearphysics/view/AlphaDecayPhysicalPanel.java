/**
 * Class: PotentialProfilePanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.GraphicsSetup;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.NuclearModelElement;
import edu.colorado.phet.nuclearphysics.model.NuclearPhysicsModel;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AlphaDecayPhysicalPanel extends PhysicalPanel {

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    private static double nucleusLayer = 20;
    private static GraphicsSetup decayProductGraphicsSetup = new GraphicsSetup() {
        public void setup( Graphics2D graphics ) {
            GraphicsUtil.setAlpha( graphics, 0.8 );
        }
    };

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private double alphaParticleLevel = Config.alphaParticleLevel;

    /**
     * Constructor
     *
     * @param clock
     */
    public AlphaDecayPhysicalPanel( IClock clock, NuclearPhysicsModel model ) {
        super( clock, model );
        this.setBackground( backgroundColor );

        addComponentListener( new ComponentAdapter() {
            private boolean init;

            public void componentResized( ComponentEvent e ) {
                if( !init ) {
                    init = true;

                }
            }
        } );
    }

    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        // Set the origin
        getOriginTx().setToTranslation( getOrigin().getX(), getOrigin().getY() );

        // Draw everything that isn't special to this panel
        //        GraphicsUtil.setAlpha( g2, 0.5 );
        g2.setColor( backgroundColor );
        super.paintComponent( g2 );

        GraphicsUtil.setAlpha( g2, 1 );
    }

    public void addAlphaParticle( final AlphaParticle alphaParticle ) {
        final NucleusGraphic graphic = new AlphaParticleGraphic( alphaParticle );
        this.addOriginCenteredGraphic( graphic, alphaParticleLevel );
        alphaParticle.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                AlphaDecayPhysicalPanel.this.removeGraphic( graphic );
                alphaParticle.removeListener( this );
            }
        } );
    }    
}
