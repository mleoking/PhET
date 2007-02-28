// edu.colorado.phet.idealgas.controller.IdealGasMainPanel
/*
 * User: Administrator
 * Date: Oct 25, 2002
 * Time: 9:04:52 PM
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.idealgas.controller.IdealGasControlPanel;
import edu.colorado.phet.idealgas.graphics.*;
import edu.colorado.phet.idealgas.physics.IdealGasSystem;
import edu.colorado.phet.controller.PhetMainPanel;
import edu.colorado.phet.controller.PhetApplication;
import edu.colorado.phet.controller.TabbedMainPanel;
import edu.colorado.phet.controller.ApplicationControlPanel;
import edu.colorado.phet.graphics.ApparatusPanel;
import edu.colorado.phet.graphics.GraphicFactory;
import edu.colorado.phet.graphics.PhetGraphic;
import edu.colorado.phet.physics.PhysicalSystem;
import edu.colorado.phet.physics.body.Particle;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;
import java.awt.*;

/**
 *
 */
public class IdealGasMainPanel extends TabbedMainPanel {

    /**
     *
     */
    public IdealGasMainPanel( PhetApplication application ) {
        super( application );

/*
        // Set up the apparatus panels
        BaseIdealGasApparatusPanel apparatusPanel1 = new IdealGasApparatusPanel( application );
        addApparatusPanel( apparatusPanel1 );
        addApparatusPanel( new HollowSphereApparatusPanel( application ) );
        addApparatusPanel( new HollowSphereApparatusPanel2( application ) );
        addApparatusPanel( new HeliumBalloonApparatusPanel( application ) );
        addApparatusPanel( new HotAirBalloonApparatusPanel( application ) );
        addApparatusPanel( new HotAirBalloonApparatusPanel2( application ) );
        super.initTabs();

        // Set the other panels for the application
        setCurrentApparatusPanel( apparatusPanel1 );
        setControlPanel( new IdealGasControlPanel( application ) );
        setMonitorPanel( new IdealGasMonitorPanel( this ) );
*/
    }

    public void init() {
//        throw new RuntimeException( "Not implemented" );
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
        addApparatusPanel( new HotAirBalloonApparatusPanel2( application ) );
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
        if( body instanceof edu.colorado.phet.idealgas.physics.body.Particle ) {
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

