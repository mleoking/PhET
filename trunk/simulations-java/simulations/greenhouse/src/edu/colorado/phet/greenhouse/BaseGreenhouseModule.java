/**
 * Class: BaseGreenhouseModule
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Nov 4, 2003
 */
package edu.colorado.phet.greenhouse;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.greenhouse.filter.BandpassFilter;
import edu.colorado.phet.greenhouse.filter.Filter1D;
import edu.colorado.phet.greenhouse.instrumentation.Thermometer;
import edu.colorado.phet.greenhouse.instrumentation.ThermometerGraphic;
import edu.colorado.phet.greenhouse.phetcommon.application.Module;
import edu.colorado.phet.greenhouse.phetcommon.application.PhetApplication;
import edu.colorado.phet.greenhouse.phetcommon.model.IClock;
import edu.colorado.phet.greenhouse.phetcommon.model.ModelElement;
import edu.colorado.phet.greenhouse.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.greenhouse.phetcommon.view.CompositeGraphic;
import edu.colorado.phet.greenhouse.phetcommon.view.FlipperAffineTransformFactory;


public abstract class BaseGreenhouseModule extends Module {
    private HashMap photonToGraphicsMap = new HashMap();
    protected CompositeGraphic drawingCanvas;
    private ThermometerGraphic thermometerGraphic;
    protected EarthGraphic earthGraphic;
    private PhotonEmitterListener earthPhotonEmitterListener;
    private PhotonEmitterListener sunPhotonEmitterListener;
    private boolean thermometerEnabled;
    double exposedEarth = 1;

    // Determines how many photons are put in the model for everyone one
    // that shows up on the screen
    private int invisiblePhotonCnt = 10;
    private Earth earth;
    private Rectangle2D.Double initialModelBounds;
    HashMap scatterToGraphicMap = new HashMap();

    private static final double EARTH_DIAM = Earth.radius * 2;
    private static final double SUN_DIAM = EARTH_DIAM * 5;
    private static final double SUN_EARTH_DIST = SUN_DIAM * 5;
    private Star sun;
    private StarGraphic sunGraphic;

    //
    private static boolean s_zoomed;
    private Rectangle2D.Double finalModelBounds;
    private AtmosphereGraphic atmosphereGraphic;

    protected BaseGreenhouseModule( String s ) {
        super( s );
        init();
    }

    private void init() {
        earthPhotonEmitterListener = new PhotonEmitterListener();

        // Set up the model and apparatus panel
        double modelHeight = EARTH_DIAM + SUN_DIAM + SUN_EARTH_DIST * 2;
        initialModelBounds = new Rectangle2D.Double( -modelHeight * 4 / 3 / 2, -EARTH_DIAM * 20,
                                                     modelHeight * 4 / 3,
                                                     modelHeight );
        modelHeight = exposedEarth + Atmosphere.troposphereThickness;
        finalModelBounds = new Rectangle2D.Double( -modelHeight * 4 / 3 / 2, -exposedEarth,
                                                   modelHeight * 4 / 3,
                                                   modelHeight );


        GreenhouseModel model = new GreenhouseModel( finalModelBounds );
        this.setModel( model );

        ApparatusPanel apparatusPanel = new TestApparatusPanel( 4 / 3, new FlipperAffineTransformFactory( initialModelBounds ) );
        this.setApparatusPanel( apparatusPanel );
        apparatusPanel.setBackground( Color.black );

        drawingCanvas = apparatusPanel.getCompositeGraphic();

        //
        // Create the model elements and their view elements
        //
        earthPhotonEmitterListener = new PhotonEmitterListener();

        // Create the earth
        double gamma = Math.atan( ( finalModelBounds.getWidth() / 2 ) / Earth.radius );
        earth = new Earth( new Point2D.Double( 0, -Earth.radius + exposedEarth ), Math.PI / 2 - gamma, Math.PI / 2 + gamma );
        model.setEarth( earth );
        earth.getPhotonSource().setProductionRate( 1E-2 );
        earth.addPhotonEmitterListener( earthPhotonEmitterListener );
        earth.addPhotonAbsorberListener( new PhotonAbsorberListener() );
        earth.getPhotonSource().addListener( model );
        // EarthGraphic adds itself to the apparatus panel. Manages its own transform, too
        earthGraphic = new EarthGraphic( getApparatusPanel(), earth, initialModelBounds );
        earth.setReflectivityAssessor( earthGraphic );

        // Create the atmosphere
        Atmosphere atmosphere = new Atmosphere( earth );
        model.setAtmosphere( atmosphere );
        atmosphereGraphic = new AtmosphereGraphic( atmosphere, finalModelBounds, apparatusPanel );
        atmosphere.addScatterEventListener( new ModuleScatterEventListener() );
        atmosphereGraphic.setVisible( false );
        drawingCanvas.addGraphic( atmosphereGraphic, Double.MAX_VALUE );

        model.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                atmosphereGraphic.update( null, null );
            }
        } );

        // Create the sun
        sun = new Star( SUN_DIAM / 2, new Point2D.Double( 0, EARTH_DIAM + SUN_EARTH_DIST + SUN_DIAM / 2 ),
                        new Rectangle2D.Double( finalModelBounds.getX(),
                                                finalModelBounds.getY() + finalModelBounds.getHeight(),
                                                finalModelBounds.getWidth() / 1,
                                                1 ) );

        sun.setProductionRate( 0 );
        sunPhotonEmitterListener = new PhotonEmitterListener();
        sun.addListener( sunPhotonEmitterListener );
        model.setSun( sun );

        sunGraphic = new StarGraphic( sun, initialModelBounds );
        apparatusPanel.addGraphic( sunGraphic, 10 );

        // Create a black hole to suck away any photons that are beyond the
        // model bounds
        BlackHole blackHole = new BlackHole( model );
        blackHole.addListener( model );
        blackHole.addListener( new PhotonAbsorberListener() );
        model.addModelElement( blackHole );

        // Put a thermometer on the earth
        Thermometer thermometer = new Thermometer( earth );
        thermometer.setLocation( new Point2D.Double( finalModelBounds.getX() + 2, .5 ) );
        model.addModelElement( thermometer );
        thermometerGraphic = new ThermometerGraphic( getApparatusPanel(), thermometer );

        // Set initial conditions
        thermometerEnabled( false );

        // Kick off the zoom
        if ( !s_zoomed ) {
            s_zoomed = true;
            Zoomer zoomer = new Zoomer( initialModelBounds, finalModelBounds );
            zoomer.start();
        }
        else {
            getApparatusPanel().removeGraphic( sunGraphic );
            sunGraphic.stopAnimation();
            earthGraphic.stopAnimation();
            atmosphereGraphic.setVisible( true );

            ( (TestApparatusPanel) getApparatusPanel() ).setModelBounds( finalModelBounds );
            thermometerEnabled( true );
            ( (TestApparatusPanel) getApparatusPanel() ).setAffineTransformFactory( new FlipperAffineTransformFactory( finalModelBounds ) );
            sun.setProductionRate( GreenhouseConfig.defaultSunPhotonProductionRate );
        }
    }

    protected HashMap getPhotonToGraphicsMap() {
        return photonToGraphicsMap;
    }

    public void addClock( IClock clock ) {
        clock.addClockTickListener( getModel() );
    }

    public void reset() {
        earth.getPhotonSource().setProductionRate( 1E-2 );
        earth.reset();
        sun.setProductionRate( GreenhouseConfig.defaultSunPhotonProductionRate );

        java.util.List photons = ( (GreenhouseModel) getModel() ).getPhotons();
        for ( int i = 0; i < photons.size(); i++ ) {
            Photon photon = (Photon) photons.get( i );
            ( (GreenhouseModel) getModel() ).photonAbsorbed( photon );
        }

        photons = ( (GreenhouseModel) getModel() ).getPhotons();
        ( (GreenhouseModel) getModel() ).getPhotons().clear();
        sun.setProductionRate( GreenhouseConfig.defaultSunPhotonProductionRate );

        Iterator keyIt = photonToGraphicsMap.keySet().iterator();
        while ( keyIt.hasNext() ) {
            Object o = keyIt.next();
            PhotonGraphic pg = (PhotonGraphic) photonToGraphicsMap.get( o );
            getApparatusPanel().removeGraphic( pg );
        }
        photonToGraphicsMap.clear();
    }

    public GreenhouseModel getGreenhouseModel() {
        return (GreenhouseModel) getModel();
    }

    public Earth getEarth() {
        return earth;
    }

    protected Rectangle2D.Double getFinalModelBounds() {
        return finalModelBounds;
    }

    public void thermometerEnabled( boolean enabled ) {
        thermometerEnabled = enabled;
        if ( enabled ) {
            getApparatusPanel().addGraphic( thermometerGraphic, ApparatusPanel.LAYER_DEFAULT );

        }
        else {
            getApparatusPanel().removeGraphic( thermometerGraphic );
        }
    }

    public void setVisiblePhotonRatio( double ratio ) {
        double cnt = 0;
        for ( Iterator iterator = photonToGraphicsMap.keySet().iterator(); iterator.hasNext(); ) {
            PhotonGraphic graphic = (PhotonGraphic) photonToGraphicsMap.get( iterator.next() );
            cnt += ratio;
            drawingCanvas.removeGraphic( graphic );
            graphic.setVisible( false );
            if ( cnt >= 1 ) {
                drawingCanvas.addGraphic( graphic, GreenhouseConfig.SUNLIGHT_PHOTON_GRAPHIC_LAYER );
                graphic.setVisible( true );
                cnt = 0;
            }
        }
        invisiblePhotonCnt = (int) ( 1 / ratio );
        earthPhotonEmitterListener.setInvisiblePhotonCnt( (int) ( 1 / ratio ) );
        sunPhotonEmitterListener.setInvisiblePhotonCnt( (int) ( 1 / ratio ) );
    }

    public boolean isThermometerEnabled() {
        return thermometerEnabled;
    }

    public void removeScatterEvent( ScatterEvent event ) {
        getModel().removeModelElement( event );
        ScatterEventGraphic seg = (ScatterEventGraphic) scatterToGraphicMap.get( event );
        getApparatusPanel().removeGraphic( seg );
        scatterToGraphicMap.remove( event );
    }

    public void setVirginEarth() {
        earthGraphic.setVirginEarth();
    }

    public void setIceAge() {
        earthGraphic.setIceAge();
    }

    public void setToday() {
        earthGraphic.setToday();
    }

    public void setTomorrow() {
        earthGraphic.setTomorrow();
    }

    public void setVenus() {
        earthGraphic.setVenus();
    }

    public void setPreIndRev() {
        earthGraphic.set1750();
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    protected class PhotonEmitterListener implements PhotonEmitter.Listener {
        // Used to tell if a photon is IR
        private Filter1D irFilter = new IrFilter();
        private int n = 0;

        public void setInvisiblePhotonCnt( int cnt ) {
            invisiblePhotonCnt = cnt;
        }

        public void photonEmitted( Photon photon ) {
            n++;
            PhotonGraphic photonView = new PhotonGraphic( photon );
            photonToGraphicsMap.put( photon, photonView );
            photonView.setVisible( false );
            if ( n >= invisiblePhotonCnt ) {
                photonView.setVisible( true );
                double layer = irFilter.absorbs( photon.getWavelength() )
                               ? GreenhouseConfig.IR_PHOTON_GRAPHIC_LAYER
                               : GreenhouseConfig.SUNLIGHT_PHOTON_GRAPHIC_LAYER;
                drawingCanvas.addGraphic( photonView, layer );

                // reset counter
                n = 0;
            }
        }
    }

    /**
     * When a photon is absorbed, this removes its graphic from the aparatus panel
     */
    protected class PhotonAbsorberListener implements PhotonAbsorber.Listener {
        public void photonAbsorbed( Photon photon ) {
            Filter1D irPassFilter = new BandpassFilter( 800E-9, 1500E-9 );
            PhotonGraphic photonView = (PhotonGraphic) photonToGraphicsMap.get( photon );
//            getApparatusPanel().removeGraphic( photonView );
            drawingCanvas.removeGraphic( photonView );
            photonToGraphicsMap.remove( photon );
        }
    }

    public class ModuleScatterEventListener implements Atmosphere.ScatterEventListener {
        public void photonScatered( Photon photon ) {
            if ( photonToGraphicsMap.get( photon ) != null ) {
                ScatterEvent se = new ScatterEvent( photon, BaseGreenhouseModule.this );
                getModel().addModelElement( se );
                if ( ( (PhotonGraphic) photonToGraphicsMap.get( photon ) ).isVisible() ) {
                    ScatterEventGraphic seg = new ScatterEventGraphic( se );
                    getApparatusPanel().addGraphic( seg, GreenhouseConfig.SUNLIGHT_PHOTON_GRAPHIC_LAYER - 1 );
                    scatterToGraphicMap.put( se, seg );
                }
            }
        }
    }

    /**
     * Handles zooming in from outer space to the earth
     */
    private class Zoomer extends Thread {
        private Rectangle2D.Double currModelBounds;
        private Rectangle2D.Double finalModelBounds;

        Zoomer( Rectangle2D.Double currModelBounds,
                Rectangle2D.Double finalModelBounds ) {
            this.currModelBounds = currModelBounds;
            this.finalModelBounds = finalModelBounds;
            earthGraphic.setNoBackdrop();
        }

        public void run() {

            try {
                // Prevent a backdrop from appearing
                earthGraphic.setNoBackdrop();

                // Sit here until things outside this thread are initialized
                while ( PhetApplication.instance() == null ) {
                    Thread.sleep( 200 );
                }

                // Put up a dialog prompting the user to kick things off
                String[] options = new String[]{SimStrings.get( "BaseGreenhouseModule.FlyMeInText" ),
                        SimStrings.get( "BaseGreenhouseModule.BeamMeDownText" )};
                JOptionPane jop = new JOptionPane( SimStrings.get( "BaseGreenhouseModule.QuestionText" ), JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, options[0] );
                JDialog zoomDialog = jop.createDialog( PhetApplication.instance().getApplicationView().getPhetFrame(), "" );
                Point p = PhetApplication.instance().getApplicationView().getPhetFrame().getLocation();
                zoomDialog.setLocation( p.x + 50, p.y + 50 );
                zoomDialog.setVisible( true );
                boolean doZoom = false;

                Object val = jop.getValue();
                if ( val instanceof String ) {
                    doZoom = val.equals( options[0] );
                }
//                Point2D.Double sunRefPt = new Point2D.Double( sun.getLocation().getX(),
//                                                              sun.getLocation().getY() - SUN_DIAM / 2 );
//
                Point2D.Double zoomCenter = new Point2D.Double( currModelBounds.getMinX() + currModelBounds.getWidth() / 2,
                                                                currModelBounds.getMinY() + currModelBounds.getHeight() / 2 );
                double zoomFactor = .002;
                double dh = currModelBounds.getHeight() * zoomFactor;
                double dw = dh * currModelBounds.getWidth() / currModelBounds.getHeight();
                double n = ( currModelBounds.getHeight() - finalModelBounds.getHeight() ) / dh;
                double c0y = currModelBounds.getMinY() + currModelBounds.getHeight() / 2;
                double cfy = finalModelBounds.getMinY() + finalModelBounds.getHeight() / 2;
                double dcy = ( cfy - c0y ) / n;

                // If we're supposed to zoom, do it
                for ( ; n > 0 && doZoom; n-- ) {

                    Thread.sleep( 20 );

                    // Zoom in and shift the zoom center
                    zoomCenter.setLocation( zoomCenter.getX(), zoomCenter.getY() + dcy );
                    double w = Math.max( currModelBounds.getWidth() - dw, finalModelBounds.getWidth() );
                    double h = Math.max( currModelBounds.getHeight() - dh, finalModelBounds.getHeight() );
                    double newMinX = zoomCenter.getX() - w / 2;
                    double newMaxX = zoomCenter.getX() + w / 2;
                    double newMinY = zoomCenter.getY() - h / 2;
                    double newMaxY = zoomCenter.getY() + h / 2;

                    currModelBounds.setRect( newMinX, newMinY, newMaxX - newMinX, newMaxY - newMinY );
                    ( (TestApparatusPanel) getApparatusPanel() ).setModelBounds( currModelBounds );
                }

                currModelBounds.setRect( finalModelBounds.getX(),
                                         finalModelBounds.getY(),
                                         finalModelBounds.getWidth(),
                                         finalModelBounds.getHeight() );
                ( (TestApparatusPanel) getApparatusPanel() ).setModelBounds( currModelBounds );

                getApparatusPanel().removeGraphic( sunGraphic );
                sunGraphic.stopAnimation();
                earthGraphic.stopAnimation();
                setToday();
                atmosphereGraphic.setVisible( true );
                ( (TestApparatusPanel) getApparatusPanel() ).setModelBounds( finalModelBounds );
                thermometerEnabled( true );

                GreenhouseApplication.paintContentImmediately();
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }

            sun.setProductionRate( GreenhouseConfig.defaultSunPhotonProductionRate );

            // Set the default view. Note that this is a real mess. It works in conjunction with code in
            // GreenhouseControlPanel.AtmoshpereSelectionPane
            setToday();
        }
    }
}
