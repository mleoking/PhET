/**
 * Class: CockpitModule
 * Class: edu.colorado.games4education.lostinspace.controller
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:50:26 AM
 */
package edu.colorado.games4education.lostinspace.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.games4education.lostinspace.view.*;
import edu.colorado.games4education.lostinspace.model.UniverseModel;
import edu.colorado.games4education.lostinspace.model.StarView;
import edu.colorado.games4education.lostinspace.model.StarField;
import edu.colorado.games4education.lostinspace.Config;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CockpitModule extends Module {

    private UniverseModel model;
    private PhotometerReticle photometerReticle;
    private ParallaxReticle parallaxReticle;

    public CockpitModule( UniverseModel model ) {
        super( "Cockpit" );
        this.model = model;
        ApparatusPanel apparatusPanel = new ApparatusPanel();
        ModuleManager m;
        setApparatusPanel( apparatusPanel );
        setModel( model );

        apparatusPanel.addGraphic( new CockpitGraphic( this ), Config.cockpitLayer );

        parallaxReticle = new ParallaxReticle( apparatusPanel );
        parallaxReticle.setLocation(400, 200 );

        photometerReticle = new PhotometerReticle( apparatusPanel );
        photometerReticle.setLocation( 200, 200 );

        StarField starField = new StarField();
        StarView starView = new StarView( starField, Math.PI / 2 );
        StarViewGraphic starViewGraphic = new StarViewGraphic( apparatusPanel,
                                                               starView,
                                                               new Rectangle2D.Double( 50, 50, 600, 400 ));
        apparatusPanel.addGraphic( new StarGraphic( 10, Color.red, new Point2D.Double( 300, 300 ) ), Config.starLayer );
        setControlPanel( new CockpitControlPanel() );


    }

    public void setParallaxReticleOn( boolean isOn ) {
        if( isOn ) {
            getApparatusPanel().addGraphic( parallaxReticle, Config.measurementInstrumentLayer );
            getApparatusPanel().repaint();
        }
        else {
            getApparatusPanel().removeGraphic( parallaxReticle );
            getApparatusPanel().repaint();
        }
    }

    public void setPhotometerReticle( boolean isOn ) {
        if( isOn ) {
            getApparatusPanel().addGraphic( photometerReticle, Config.measurementInstrumentLayer );
            getApparatusPanel().repaint();
        }
        else {
            getApparatusPanel().removeGraphic( photometerReticle );
            getApparatusPanel().repaint();
        }
    }
}
