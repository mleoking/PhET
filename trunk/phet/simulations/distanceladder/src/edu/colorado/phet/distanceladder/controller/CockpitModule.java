/**
 * Class: CockpitModule
 * Class: edu.colorado.phet.distanceladder.controller
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:50:26 AM
 */
package edu.colorado.phet.distanceladder.controller;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.distanceladder.Config;
import edu.colorado.phet.distanceladder.model.*;
import edu.colorado.phet.distanceladder.view.CockpitView;
import edu.colorado.phet.distanceladder.view.ParallaxReticle;
import edu.colorado.phet.distanceladder.view.PhotometerReticle;
import edu.colorado.phet.distanceladder.view.StarViewGraphic;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class CockpitModule extends Module {

    private UniverseModel model;
    private ParallaxReticle parallaxReticle;
    private PhotometerReticle photometerReticle;
    private StarField starField;
    private StarView starView;
    private StarViewGraphic starViewGraphic;
    private CockpitView cockpitGraphic;
    private CockpitControlPanel cockpitControlPanel;

    public CockpitModule( UniverseModel model ) {
        super( "Cockpit" );
        this.model = model;
        final ApparatusPanel apparatusPanel = new ApparatusPanel();
        setApparatusPanel( apparatusPanel );
        setModel( model );

        photometerReticle = new PhotometerReticle( apparatusPanel );
        photometerReticle.setLocation( 0, 0 );
        cockpitGraphic = new CockpitView( this );

        starField = model.getStarField();
        Rectangle2D.Double bounds = new Rectangle2D.Double( apparatusPanel.getBounds().getMinX(),
                                                            apparatusPanel.getBounds().getMinY(),
                                                            apparatusPanel.getBounds().getWidth(),
                                                            apparatusPanel.getBounds().getHeight() );

        starView = new StarView( model.getStarShip(), starField, Config.viewAngle, bounds );
        model.getStarShip().setStarView( starView );
        model.addObserver( starView );

        starViewGraphic = new StarViewGraphic( apparatusPanel, starView );
        starView.addObserver( starViewGraphic );
        apparatusPanel.addGraphic( cockpitGraphic, Config.cockpitLayer );
        apparatusPanel.addGraphic( starViewGraphic, Config.starLayer );

        parallaxReticle = new ParallaxReticle( apparatusPanel, Config.viewAngle );

        cockpitControlPanel = new CockpitControlPanel( this );
        setControlPanel( cockpitControlPanel );
    }

    public StarField getStarField() {
        return starField;
    }

    public StarView getStarView() {
        return starView;
    }

    public void setParallaxReticleOn( boolean isOn ) {
        cockpitControlPanel.setParallaxEnabled( isOn );
        if( isOn ) {
            starViewGraphic.addGraphic( parallaxReticle, Config.measurementInstrumentLayer );
            getApparatusPanel().repaint();
        }
        else {
            starViewGraphic.remove( parallaxReticle );
            getApparatusPanel().repaint();
        }
    }

    public void setPhotometerReticle( boolean isOn ) {
        cockpitControlPanel.setPhotometerEnabled( isOn );
        if( isOn ) {
            starViewGraphic.addGraphic( photometerReticle, Config.measurementInstrumentLayer );
            getApparatusPanel().repaint();
        }
        else {
            starViewGraphic.remove( photometerReticle );
            getApparatusPanel().repaint();
        }
    }

    public void changeCockpitPov( double cockpitDx, double cockpitDy, double gamma ) {
        double dx = -cockpitDx * Math.sin( this.starView.getPovTheta() );
        double dy = cockpitDx * Math.cos( this.starView.getPovTheta() );
        starView.movePov( dx, dy, gamma );
    }

    public void setPovTheta( double theta ) {
        model.getStarShip().setOrientation( theta );
    }

    public Point2D.Double getCockpitPovPt() {
        return starView.getPovPt();
    }

    public void setPov( PointOfView pov ) {
        model.getStarShip().setPov( pov );
    }

    public PointOfView getCockpitPov() {
        return starView.getPov();
    }

    public PhotometerReticle getPhotometerReticle() {
        return photometerReticle;
    }

    public Starship getStarship() {
        return model.getStarShip();
    }

    public void setStarshipCordinateGraphicEnabled( boolean isEnabled ) {
        cockpitControlPanel.setStarshipCordinateGraphicEnabled( isEnabled );
    }
}
