/**
 * Class: BaseGreenhouseModule
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Nov 4, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.FlipperAffineTransformFactory;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.instrumentation.Thermometer;
import edu.colorado.phet.instrumentation.ThermometerGraphic;

import javax.swing.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D;
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;

public abstract class BaseGreenhouseModule extends Module {
    HashMap photonToGraphicsMap = new HashMap();
    protected CompositeGraphic drawingCanvas;
    private ThermometerGraphic thermometerGraphic;
    protected EarthGraphic earthGraphic;
    private PhotonEmitterListener earthPhotonEmitterListener;
    private PhotonEmitterListener sunPhotonEmitterListener;
    private boolean thermometerEnabled;
    double exposedEarth = 1;

    // Determines how many photons are put in the model for everyone one
    // that shows up on the screen
    private static int invisiblePhotonCnt = 10;
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

    protected BaseGreenhouseModule( String s ) {
        super( s );

        earthPhotonEmitterListener = new GreenhouseModule.PhotonEmitterListener();

        // Set up the model and apparatus panel
        double modelHeight = EARTH_DIAM + SUN_DIAM + SUN_EARTH_DIST * 2;
        initialModelBounds = new Rectangle2D.Double( -modelHeight * 4 / 3 / 2, -EARTH_DIAM * 20,
//        initialModelBounds = new Rectangle2D.Double( -modelHeight * 4 / 3 / 2, -EARTH_DIAM * 4,
                                                     modelHeight * 4 / 3,
                                                     modelHeight );
        modelHeight = exposedEarth + Atmosphere.troposphereThickness;
        finalModelBounds = new Rectangle2D.Double( -modelHeight * 4 / 3 / 2, -exposedEarth,
                                                   modelHeight * 4 / 3,
                                                   modelHeight );


        GreenhouseModel model = new GreenhouseModel( finalModelBounds );
//        GreenhouseModel model = new GreenhouseModel( initialModelBounds );
        this.setModel( model );

//        ApparatusPanel apparatusPanel = new MultiTxApparatusPanel( new FlipperAffineTransformFactory( initialModelBounds ) );
        ApparatusPanel apparatusPanel = new TestApparatusPanel( 4 / 3, new FlipperAffineTransformFactory( initialModelBounds ) );
        this.setApparatusPanel( apparatusPanel );
        apparatusPanel.setBackground( Color.black );

//        drawingCanvas = new BufferedGraphic2( apparatusPanel );
        drawingCanvas = apparatusPanel.getCompositeGraphic();
//        drawingCanvas = new CompositeGraphic();
//        BufferedGraphic2 bg = new BufferedGraphic2(apparatusPanel );
//        drawingCanvas.addGraphic(bg,0);

        //
        // Create the model elements and their view elements
        //
        earthPhotonEmitterListener = new GreenhouseModule.PhotonEmitterListener();

        // Create the earth
        double gamma = Math.atan( ( finalModelBounds.getWidth() / 2 ) / Earth.radius );
//        double gamma = Math.atan( ( initialModelBounds.getWidth() / 2 ) / Earth.radius );
        earth = new Earth( new Point2D.Double( 0, -Earth.radius + exposedEarth ), Math.PI / 2 - gamma, Math.PI / 2 + gamma );
        model.setEarth( earth );
        earth.getPhotonSource().setProductionRate( 1E-2 );
        earth.addPhotonEmitterListener( earthPhotonEmitterListener );
        earth.addPhotonAbsorberListener( new GreenhouseModule.PhotonAbsorberListener() );
        earth.getPhotonSource().addListener( model );
        earthGraphic = new EarthGraphic( getApparatusPanel(), earth, initialModelBounds );
        earth.setReflectivityAssessor( earthGraphic );

        // Create the atmosphere
        Atmosphere atmosphere = new Atmosphere( earth );
        model.setAtmosphere( atmosphere );
        AtmosphereGraphic atmosphereGraphic = new AtmosphereGraphic( atmosphere );
        atmosphere.addScatterEventListener( new GreenhouseModule.ModuleScatterEventListener() );
//        apparatusPanel.addGraphic( atmosphereGraphic, GreenhouseConfig.ATMOSPHERE_GRAPHIC_LAYER );
        drawingCanvas.addGraphic( atmosphereGraphic, GreenhouseConfig.ATMOSPHERE_GRAPHIC_LAYER );

        // Create the sun
        sun = new Star( SUN_DIAM / 2, new Point2D.Double( 0, EARTH_DIAM + SUN_EARTH_DIST + SUN_DIAM / 2 ),
                        new Rectangle2D.Double( finalModelBounds.getX(),
                                                finalModelBounds.getY() + finalModelBounds.getHeight(),
                                                finalModelBounds.getWidth() / 1,
                                                1 ) );

        sun.setProductionRate( 0 );
        sunPhotonEmitterListener = new GreenhouseModule.PhotonEmitterListener();
        sun.addListener( sunPhotonEmitterListener );
        model.setSun( sun );

        sunGraphic = new StarGraphic( sun, initialModelBounds );
        apparatusPanel.addGraphic( sunGraphic, 10 );

        // Create a black hole to suck away any photons that are beyond the
        // model bounds
        BlackHole blackHole = new BlackHole( model );
        blackHole.addListener( model );
        blackHole.addListener( new GreenhouseModule.PhotonAbsorberListener() );
        model.addModelElement( blackHole );

        // Put a thermometer on the earth
        Thermometer thermometer = new Thermometer( earth );
        thermometer.setLocation( new Point2D.Double( finalModelBounds.getX() + 2, .5 ) );
        model.addModelElement( thermometer );
        thermometerGraphic = new ThermometerGraphic( thermometer );
//        drawingCanvas.addGraphic( thermometerGraphic, ApparatusPanel.LAYER_DEFAULT );
//        apparatusPanel.addGraphic( drawingCanvas, ApparatusPanel.LAYER_DEFAULT );

        // Set initial conditions
//        setVirginEarth();
        thermometerEnabled( false );

        // Kick off the zoom
        if( !s_zoomed ) {
            s_zoomed = true;
            Zoomer zoomer = new Zoomer( initialModelBounds, finalModelBounds );
            zoomer.start();
        }
        else {
            getApparatusPanel().removeGraphic( sunGraphic );
            sunGraphic.stopAnimation();

            ( (TestApparatusPanel)getApparatusPanel() ).setAffineTransformFactory( new FlipperAffineTransformFactory( finalModelBounds ) );
//            ( (TestApparatusPanel)getApparatusPanel() ).setModelBounds( finalModelBounds );
            sun.setProductionRate( GreenhouseConfig.defaultSunPhotonProductionRate );
        }
    }

    public GreenhouseModel getGreenhouseModel() {
        return (GreenhouseModel)getModel();
    }

    public Earth getEarth() {
        return earth;
    }

    protected Rectangle2D.Double getFinalModelBounds() {
        return finalModelBounds;
    }

    public void thermometerEnabled( boolean enabled ) {
        thermometerEnabled = enabled;
        if( enabled ) {
            getApparatusPanel().addGraphic( thermometerGraphic, ApparatusPanel.LAYER_DEFAULT );

        }
        else {
            getApparatusPanel().removeGraphic( thermometerGraphic );
        }
    }

    public void setVisiblePhotonRatio( double ratio ) {
        double cnt = 0;
        for( Iterator iterator = photonToGraphicsMap.keySet().iterator(); iterator.hasNext(); ) {
            PhotonGraphic graphic = (PhotonGraphic)photonToGraphicsMap.get( iterator.next() );
            cnt += ratio;
            drawingCanvas.removeGraphic( graphic );
            graphic.setVisible( false );
            if( cnt >= 1 ) {
                drawingCanvas.addGraphic( graphic, GreenhouseConfig.PHOTON_GRAPHIC_LAYER );
                graphic.setVisible( true );
                cnt = 0;
            }
        }
        BaseGreenhouseModule.invisiblePhotonCnt = (int)( 1 / ratio );
        earthPhotonEmitterListener.setInvisiblePhotonCnt( (int)( 1 / ratio ) );
        sunPhotonEmitterListener.setInvisiblePhotonCnt( (int)( 1 / ratio ) );
    }

    public boolean isThermometerEnabled() {
        return thermometerEnabled;
    }

    public void clear() {
        Collection keys = photonToGraphicsMap.keySet();
        for( Iterator iterator = keys.iterator(); iterator.hasNext(); ) {
            Photon photon = (Photon)iterator.next();
            getGreenhouseModel().removePhoton( photon );
            Graphic g = (Graphic)photonToGraphicsMap.get( photon );
            getApparatusPanel().removeGraphic( g );
        }
        photonToGraphicsMap.clear();
    }

    public void removeScatterEvent( ScatterEvent event ) {
        getModel().removeModelElement( event );
        ScatterEventGraphic seg = (ScatterEventGraphic)scatterToGraphicMap.get( event );
//        drawingCanvas.removeGraphic( seg );
        getApparatusPanel().removeGraphic( seg );
        scatterToGraphicMap.remove( event );
    }

    protected class PhotonEmitterListener implements PhotonEmitter.Listener {
        private int n = 0;

        public void setInvisiblePhotonCnt( int cnt ) {
            BaseGreenhouseModule.invisiblePhotonCnt = cnt;
        }

        public void photonEmitted( Photon photon ) {
            n++;
            PhotonGraphic photonView = new PhotonGraphic( photon );
            photonToGraphicsMap.put( photon, photonView );
            photonView.setVisible( false );
            if( n >= BaseGreenhouseModule.invisiblePhotonCnt ) {
                photonView.setVisible( true );
                if( photon.getWavelength() == GreenhouseConfig.irWavelength
                        && photon.getLocation().getY() > 1 ) {
                    System.out.println();
                }
                drawingCanvas.addGraphic( photonView, GreenhouseConfig.PHOTON_GRAPHIC_LAYER );
//                getApparatusPanel().addGraphic( photonView, GreenhouseConfig.PHOTON_GRAPHIC_LAYER );

                // reset counter
                n = 0;
            }
        }
    }

    class ModelToGraphicMap {
        HashMap map;

        public void put( ModelElement me, Graphic g ) {
            map.put( me, g );
        }

        Graphic getGraphic( ModelElement me ) {
            return (Graphic)map.get( me );
        }
    }

    /**
     * When a photon is absorbed, this removes its graphic from the aparatus panel
     */
    protected class PhotonAbsorberListener implements PhotonAbsorber.Listener {
        public void photonAbsorbed( Photon photon ) {
            PhotonGraphic photonView = (PhotonGraphic)photonToGraphicsMap.get( photon );
            getApparatusPanel().removeGraphic( photonView );
//            drawingCanvas.removeGraphic( photonView );
            photonToGraphicsMap.remove( photon );
        }
    }

    public class ModuleScatterEventListener implements Atmosphere.ScatterEventListener {
        public void photonScatered( Photon photon ) {
            if( photonToGraphicsMap.get( photon ) != null ) {
                ScatterEvent se = new ScatterEvent( photon, BaseGreenhouseModule.this );
                getModel().addModelElement( se );
                if( ( (PhotonGraphic)photonToGraphicsMap.get( photon ) ).isVisible() ) {
                    ScatterEventGraphic seg = new ScatterEventGraphic( se );
                    getApparatusPanel().addGraphic( seg, GreenhouseConfig.PHOTON_GRAPHIC_LAYER - 1 );
//            drawingCanvas.addGraphic( seg, GreenhouseModule.PHOTON_GRAPHIC_LAYER - 1 );
                    scatterToGraphicMap.put( se, seg );
                }
            }
        }
    }

    public void activate( PhetApplication phetApplication ) {
        phetApplication.getApplicationModel().getClock().start();
    }

    public void deactivate( PhetApplication phetApplication ) {
        phetApplication.getApplicationModel().getClock().stop();
    }

    //
    // Inner classes
    //
    private class Zoomer extends Thread {
        private Rectangle2D.Double currModelBounds;
        private Rectangle2D.Double finalModelBounds;

        Zoomer( Rectangle2D.Double currModelBounds,
                Rectangle2D.Double finalModelBounds ) {
            this.currModelBounds = currModelBounds;
            this.finalModelBounds = finalModelBounds;
        }

        public void run() {

            try {
                // Sit here until things outside this thread are initialized
                while( PhetApplication.instance() == null ) {
                    Thread.sleep( 200 );
                }

                // Put up a dialog prompting the user to kick things off
                String[] options = new String[] { "Fly me in", "Beam me down, Scotty!" };
                JOptionPane jop = new JOptionPane( "How would you like to get to Earth?", JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, options, options[0] );
                JDialog zoomDialog = jop.createDialog( PhetApplication.instance().getApplicationView().getPhetFrame(), "" );
                Point p = PhetApplication.instance().getApplicationView().getPhetFrame().getLocation();
                zoomDialog.setLocation( p.x + 50, p.y + 50 );
                zoomDialog.setVisible( true );
                boolean doZoom = false;
                Object val = jop.getValue();
                if( val instanceof String ) {
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
                for( ; n > 0 && doZoom; n-- ) {

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
                    ( (TestApparatusPanel)getApparatusPanel() ).setModelBounds( currModelBounds );
                }

                currModelBounds.setRect( finalModelBounds.getX(),
                                         finalModelBounds.getY(),
                                         finalModelBounds.getWidth(),
                                         finalModelBounds.getHeight() );
                ( (TestApparatusPanel)getApparatusPanel() ).setModelBounds( currModelBounds );

                getApparatusPanel().removeGraphic( sunGraphic );
                sunGraphic.stopAnimation();
                earthGraphic.stopAnimation();
                ( (TestApparatusPanel)getApparatusPanel() ).setModelBounds( finalModelBounds );
                thermometerEnabled( true );
            }
            catch( InterruptedException e ) {
                e.printStackTrace();
            }
            sun.setProductionRate( GreenhouseConfig.defaultSunPhotonProductionRate );
        }
    }
}
