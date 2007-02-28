// edu.colorado.phet.idealgas.controller.IdealGasMainPanel
/*
 * User: Administrator
 * Date: Oct 25, 2002
 * Time: 9:04:52 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.controller.IdealGasControlPanel;
import edu.colorado.phet.idealgas.graphics.*;
import edu.colorado.phet.idealgas.model.IdealGasSystem;
//import edu.colorado.phet.controller.PhetMainPanel;
//import edu.colorado.phet.controller.PhetApplication;
//import edu.colorado.phet.controller.TabbedMainPanel;
//import edu.colorado.phet.controller.ApplicationControlPanel;
//import edu.colorado.phet.graphics.ApparatusPanel;
//import edu.colorado.phet.graphics.GraphicFactory;
//import edu.colorado.phet.graphics.PhetGraphic;
//import edu.colorado.phet.model.PhysicalSystem;
//import edu.colorado.phet.model.body.Particle;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicPhetPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.model.Particle;

import javax.swing.*;
import java.util.Observable;

/**
 *
 */

// Many of the responsibilities of this class are now handled by the framework.
// Each of the apparatus panels needs to be associated with a module, and the
// modules added to the ApplicationModel in the IdealGasApplication.main();

public class IdealGasMainPanel extends TabbedMainPanel {

    /**
     *
     */
    public IdealGasMainPanel( PhetApplication application ) {
        super( application );
    }

    public void init() {
        // Set up the apparatus panels
        PhetApplication application = PhetApplication.instance();
//        BaseIdealGasApparatusPanel apparatusPanel1 = new MeasurementApparatusPanel( application );
        BaseIdealGasApparatusPanel apparatusPanel1 = new IdealGasApparatusPanel( application );
        addApparatusPanel( apparatusPanel1 );
//        addApparatusPanel( new IdealGasApparatusPanel( application ) );
        addApparatusPanel( new MeasurementApparatusPanel( application ) );
        addApparatusPanel( new HollowSphereApparatusPanel( application ) );
        addApparatusPanel( new HollowSphereApparatusPanel2( application ) );
        addApparatusPanel( new HeliumBalloonApparatusPanel( application ) );
        addApparatusPanel( new HotAirBalloonApparatusPanel( application ) );
//        addApparatusPanel( new HotAirBalloonApparatusPanel2( application ) );
        super.initTabs();

        // Set the other panels for the application
        setControlPanel( new IdealGasControlPanel( application ) );
        setMonitorPanel( new IdealGasMonitorPanel( this ) );
        setCurrentApparatusPanel( apparatusPanel1 );
    }

    protected void setCurrentApparatusPanel( ApparatusPanel apparatusPanel ) {
        PhysicalSystem physcialSystem = this.getPhysicalSystem();
        if( physcialSystem instanceof IdealGasSystem ) {
            IdealGasSystem idealGasSystem = (IdealGasSystem)physcialSystem;
            idealGasSystem.setGravity( null );
        }
        super.setCurrentApparatusPanel( apparatusPanel );
    }


    public JPanel getApplicationControlPanel() {
        return new ApplicationControlPanel( PhetApplication.instance() );
    }

    /**
     * Adds a physical body to the apparatus panel at a specified
     * level
     */
    public void addBody( Particle body, int level ) {

        GraphicFactory graphicFactory = PhetApplication.instance().getGraphicFactory();

        // If it's a particle, only add it to the current apparatus panel
        if( body instanceof edu.colorado.phet.idealgas.model.body.IdealGasParticle ) {
            ApparatusPanel apparatusPanel = getApparatusPanel();
            PhetGraphic graphic = graphicFactory.createGraphic( body, apparatusPanel );
            apparatusPanel.addGraphic( graphic, level );
            apparatusPanel.repaint();
        } else {
            for( int i = 0; i < getApparatusPanels().size(); i++ ) {
                ApparatusPanel apparatusPanel = (ApparatusPanel)getApparatusPanels().get( i );
                PhetGraphic graphic = graphicFactory.createGraphic( body, apparatusPanel );
                apparatusPanel.addGraphic( graphic, level );
                apparatusPanel.repaint();
            }
        }
    }

    /**
     *
     */
    public void setStove( int value ) {
        ( (BaseIdealGasApparatusPanel)getApparatusPanel() ).setStove( value );
    }

    public void update( Observable o, Object arg ) {
    }
}

