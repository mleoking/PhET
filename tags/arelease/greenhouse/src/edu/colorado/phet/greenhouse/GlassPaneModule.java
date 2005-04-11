/**
 * Class: GreenhouseModule
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GlassPaneModule extends BaseGreenhouseModule {
    private GlassPane glassPane;
    private GlassPaneGraphic glassPaneGraphic;
    private boolean glassPaneEnabled;
    private HashMap glassPanesToGraphicMap = new HashMap();
    private int numGlassPanes = 3;
    private ArrayList glassPanes = new ArrayList();

    private double[][] jimmyArray = new double [][] {
        new double[] { 0, 0 },
        new double[] { 255, 255 },
        new double[] { 272, 303 },
        new double[] { 283, 335 },
        new double[] { 286, 361 },
        new double[] { 1000, 1400 }
    };


    public GlassPaneModule() {
        super( "Glass Layers" );

        // Create glass panes
        for( int i = 0; i < numGlassPanes; i++ ) {
            glassPane = new GlassPane( getFinalModelBounds().getMinX(),
                                       getFinalModelBounds().getWidth(),
                                       5 + i * 2 );
            glassPaneGraphic = new GlassPaneGraphic( glassPane );
            glassPane.addListener( new BaseGreenhouseModule.PhotonEmitterListener() );
            glassPane.addListener( (PhotonEmitter.Listener)getModel() );
            glassPane.addListener( new BaseGreenhouseModule.PhotonAbsorberListener() );
            glassPane.addListener( (PhotonAbsorber.Listener)getModel() );
            glassPanesToGraphicMap.put( glassPane, glassPaneGraphic );
            glassPanes.add( glassPane );
        }
        glassPaneEnabled = false;

        // Set up the controls
        setControlPanel( new GlassPaneControlPanel( this ) );

        // Jimmy the temperature
        getEarth().setJimmyArray( jimmyArray );
    }

    public GreenhouseModel getGreenhouseModel() {
        return (GreenhouseModel)getModel();
    }

    public void activate( PhetApplication phetApplication ) {
    }

    public void deactivate( PhetApplication phetApplication ) {
    }


    public void setGlassPaneEnabled( boolean isEnabled ) {
        if( isEnabled && !glassPaneEnabled ) {
            getGreenhouseModel().addGlassPane( glassPane );
            drawingCanvas.addGraphic( glassPaneGraphic, ApparatusPanel.LAYER_DEFAULT );
            glassPaneEnabled = true;
        }
        else {
            getGreenhouseModel().removeGlassPane( glassPane );
            drawingCanvas.removeGraphic( glassPaneGraphic );
            glassPaneEnabled = false;
        }
    }

    public void numGlassPanesEnabled( int numClouds ) {
        for( Iterator iterator = glassPanes.iterator(); iterator.hasNext(); ) {
            GlassPane glassPane = (GlassPane)iterator.next();
            getGreenhouseModel().removeGlassPane( glassPane );
//                drawingCanvas.removeGraphic( (Graphic)cloudsToGraphicMap.get( cloud ) );
            getApparatusPanel().removeGraphic( (Graphic)glassPanesToGraphicMap.get( glassPane ) );
        }

        for( int i = 0; i < numClouds; i++ ) {
            GlassPane glassPane = (GlassPane)glassPanes.get( i );
                getGreenhouseModel().addGlassPane( glassPane );
//                drawingCanvas.addGraphic( (Graphic)cloudsToGraphicMap.get( glassPane ), ApparatusPanel.LAYER_DEFAULT );
                getApparatusPanel().addGraphic( (Graphic)glassPanesToGraphicMap.get( glassPane ), ApparatusPanel.LAYER_DEFAULT );
        }
    }


    public int getMaxGlassPanes() {
        return numGlassPanes;
    }


    //
    // Inner classes
    //

    /**
     * Adds a graphic to the apparatus panel when a photon is emitted
     */
    private class PhotonEmitterListener implements PhotonEmitter.Listener {
        private int invisiblePhotonCnt = 10;
        private int n = 0;

        public void setInvisiblePhotonCnt( int cnt ) {
            invisiblePhotonCnt = cnt;
        }

        public void photonEmitted( Photon photon ) {
            n++;
            if( n >= invisiblePhotonCnt ) {
                PhotonGraphic photonView = new PhotonGraphic( photon );
                photonToGraphicsMap.put( photon, photonView );
//            drawingCanvas.addGraphic( photonView, GreenhouseModule.PHOTON_GRAPHIC_LAYER );
                getApparatusPanel().addGraphic( photonView, GreenhouseConfig.PHOTON_GRAPHIC_LAYER );

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
    private class PhotonAbsorberListener implements PhotonAbsorber.Listener {
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
                ScatterEvent se = new ScatterEvent( photon, GlassPaneModule.this );
                ScatterEventGraphic seg = new ScatterEventGraphic( se );
                getModel().addModelElement( se );
                getApparatusPanel().addGraphic( seg, GreenhouseConfig.PHOTON_GRAPHIC_LAYER - 1 );
//            drawingCanvas.addGraphic( seg, GreenhouseModule.PHOTON_GRAPHIC_LAYER - 1 );
                scatterToGraphicMap.put( se, seg );
            }
        }

    }
}
