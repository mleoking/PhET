/**
 * Class: CockpitModule
 * Class: edu.colorado.games4education.lostinspace.controller
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:50:26 AM
 */
package edu.colorado.games4education.lostinspace.controller;

import edu.colorado.games4education.lostinspace.Config;
import edu.colorado.games4education.lostinspace.model.StarField;
import edu.colorado.games4education.lostinspace.model.StarView;
import edu.colorado.games4education.lostinspace.model.UniverseModel;
import edu.colorado.games4education.lostinspace.view.CockpitView;
import edu.colorado.games4education.lostinspace.view.ParallaxReticle;
import edu.colorado.games4education.lostinspace.view.PhotometerReticle;
import edu.colorado.games4education.lostinspace.view.StarViewGraphic;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.view.ApparatusPanel;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class CockpitModule extends Module {

    private UniverseModel model;
    private PhotometerReticle photometerReticle;
    private ParallaxReticle parallaxReticle;
    private StarField starField;
    private StarView starView;
    private StarViewGraphic starViewGraphic;
    private AffineTransform starViewOriginTx = new AffineTransform();
    private CockpitView cockpitGraphic;

    public CockpitModule( UniverseModel model ) {
        super( "Cockpit" );
        this.model = model;
        ApparatusPanel apparatusPanel = new ApparatusPanel();
        ModuleManager m;
        setApparatusPanel( apparatusPanel );
        setModel( model );

        cockpitGraphic = new CockpitView( this );
        apparatusPanel.addGraphic( cockpitGraphic, Config.cockpitLayer );

        parallaxReticle = new ParallaxReticle( apparatusPanel );
        parallaxReticle.setLocation( 400, 200 );

        photometerReticle = new PhotometerReticle( apparatusPanel );
        photometerReticle.setLocation( 200, 200 );

        starField = model.getStarField();
        starView = new StarView( starField, Math.PI / 2 );
        starViewGraphic = new StarViewGraphic( apparatusPanel,
                                               starView,
                                               new Rectangle2D.Double( 50, 50, 600, 400 ),
                                               starViewOriginTx );
        apparatusPanel.addGraphic( starViewGraphic, Config.starLayer );
        setControlPanel( new CockpitControlPanel() );


    }

    public void update() {
        starViewOriginTx.setToIdentity();
        starViewOriginTx.setToTranslation( getApparatusPanel().getWidth() / 2, getApparatusPanel().getHeight() / 2 );
        starViewGraphic.update();
        getApparatusPanel().repaint();
    }

    public StarField getStarField() {
        return starField;
    }

    public StarView getStarView() {
        return starView;
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

    public void changeCockpitPov( double cockpitDx, double cockpitDy, double gamma ) {
        double dx = -cockpitDx * Math.sin( this.starView.getPovTheta() );
        double dy = cockpitDx * Math.cos( this.starView.getPovTheta() );
        changeAbsolutePov( dx, dy, gamma );
    }

    public void changeAbsolutePov( double dx, double dy, double gamma ) {
        starView.movePov( dx, dy, gamma );
        this.update();
    }
}
