/**
 * Class: FisionModule
 * Package: edu.colorado.phet.nuclearphysics.modules
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.modules;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.nuclearphysics.view.ProtonGraphic;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.PotentialProfileGraphic;
import edu.colorado.phet.nuclearphysics.model.Particle;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;
import edu.colorado.phet.nuclearphysics.controller.FisionControlPanel;

import javax.swing.*;
import java.awt.geom.Point2D;

public class FisionModule extends Module {
    private ApparatusPanel apparatusPanel;
    private PotentialProfile potentialProfile;

    public FisionModule() {
        super( "Fision" );

        apparatusPanel = new ApparatusPanel();
        super.setApparatusPanel( apparatusPanel );

        addProton( 200, 200 );
        addNeutron( 300, 300 );

        potentialProfile = new PotentialProfile( 200, 200, 75 );
        PotentialProfileGraphic ppg = new PotentialProfileGraphic( potentialProfile,
                                                                   new Point2D.Double(500, 300 ));
        apparatusPanel.addGraphic( ppg );

        JPanel controlPanel = new FisionControlPanel( this );
        super.setControlPanel( controlPanel );
    }

    private void addProton( double x, double y ) {
        Particle p = new Particle( new Point2D.Double(x,y));
        ProtonGraphic pg = new ProtonGraphic( p );
        apparatusPanel.addGraphic( pg );
    }

    private void addNeutron( double x, double y ) {
        Particle p = new Particle( new Point2D.Double(x,y));
        NeutronGraphic ng = new NeutronGraphic( p );
        apparatusPanel.addGraphic( ng );
    }

    public void activate( PhetApplication app ) {
    }

    public void deactivate( PhetApplication app ) {
    }

    public void setProfileMaxHeight( double modelValue ) {
        potentialProfile.setMaxPotential( modelValue );
        apparatusPanel.repaint();
    }

    public void setProfileWellDepth( double wellDepth ) {
        potentialProfile.setWellDepth( wellDepth );
        apparatusPanel.repaint();
    }

    public void setProfileWidth( double profileWidth ) {
        potentialProfile.setWidth( profileWidth );
        apparatusPanel.repaint();
    }
}
