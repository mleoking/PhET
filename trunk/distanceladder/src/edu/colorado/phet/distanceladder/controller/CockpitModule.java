/**
 * Class: CockpitModule
 * Class: edu.colorado.phet.distanceladder.controller
 * User: Ron LeMaster
 * Date: Mar 16, 2004
 * Time: 7:50:26 AM
 */
package edu.colorado.phet.distanceladder.controller;

import edu.colorado.phet.distanceladder.Config;
import edu.colorado.phet.distanceladder.model.*;
import edu.colorado.phet.distanceladder.view.*;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.Collection;

public class CockpitModule extends Module {

    private UniverseModel model;
    private OrientationReticle orientationReticle;
    private PhotometerReticle photometerReticle;
    private ParallaxReticle parallaxReticle;
    private StarField starField;
    private StarView starView;
    private StarViewGraphic starViewGraphic;
    private CockpitView cockpitGraphic;
    private Cockpit cockpit;

    public CockpitModule( UniverseModel model ) {
        super( "Cockpit" );
        this.model = model;
        ApparatusPanel apparatusPanel = new ApparatusPanel(){
            public void repaint() {
                super.repaint();
            }

            protected void paintComponent( Graphics graphics ) {


                super.paintComponent( graphics );
            }
        };
        setApparatusPanel( apparatusPanel );
        setModel( model );

        setControlPanel( new CockpitControlPanel( this ) );

        photometerReticle = new PhotometerReticle( apparatusPanel );
        photometerReticle.setLocation( 0, 0 );
//        photometerReticle.setLocation( 200, 200 );

        cockpit = new Cockpit( starField );
        cockpitGraphic = new CockpitView( cockpit, this );

        starField = model.getStarField();
        starView = new StarView( model.getStarShip(), starField, Config.viewAngle, cockpitGraphic.getBounds() );
        model.getStarShip().setStarView( starView );
        starViewGraphic = new StarViewGraphic( apparatusPanel,
                                               starView,
                                               cockpitGraphic.getBounds() );
        starView.addObserver( starViewGraphic );
        apparatusPanel.addGraphic( cockpitGraphic, Config.cockpitLayer );
        apparatusPanel.addGraphic( starViewGraphic, Config.starLayer );

        parallaxReticle = new ParallaxReticle( apparatusPanel, cockpitGraphic.getBounds(), Config.viewAngle );
        orientationReticle = new OrientationReticle( cockpitGraphic.getBounds(), Config.viewAngle );
    }

    public StarField getStarField() {
        return starField;
    }

    public StarView getStarView() {
        return starView;
    }

    public void setOrientationReticleOn( boolean isOn ) {
        if( isOn ) {
            starViewGraphic.addGraphic( orientationReticle, Config.measurementInstrumentLayer );
            getApparatusPanel().repaint();
        }
        else {
            starViewGraphic.remove( orientationReticle );
            getApparatusPanel().repaint();
        }
    }

    public void setParallaxReticleOn( boolean isOn ) {
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
        if( isOn ) {
            starViewGraphic.addGraphic( photometerReticle, Config.measurementInstrumentLayer );
//            getApparatusPanel().addGraphic( photometerReticle, Config.measurementInstrumentLayer );
            getApparatusPanel().repaint();
        }
        else {
            starViewGraphic.remove( photometerReticle );
//            getApparatusPanel().removeGraphic( photometerReticle );
            getApparatusPanel().repaint();
        }
    }

    public void changeCockpitPov( double cockpitDx, double cockpitDy, double gamma ) {
        double dx = -cockpitDx * Math.sin( this.starView.getPovTheta() );
        double dy = cockpitDx * Math.cos( this.starView.getPovTheta() );
        starView.movePov( dx, dy, gamma );
        cockpit.moveIncr( dx, dy );
        cockpit.setVelocity( (float)dx, (float)dy );
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
}
