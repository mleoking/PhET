/**
 * Class: GreenhouseModule
 * Package: edu.colorado.phet.greenhouse
 * Author: Another Guy
 * Date: Oct 9, 2003
 */
package edu.colorado.phet.greenhouse;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.graphics.ImageGraphic;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
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

    private double[][] jimmyArray = new double [][]{
            new double[]{0, 0},
            new double[]{255, 255},
            new double[]{272, 303},
            new double[]{283, 335},
            new double[]{286, 361},
            new double[]{1000, 1400}
    };
    private ImageGraphic backgroundGraphic;


    public GlassPaneModule() {
        super( SimStrings.get( "ModuleTitle.GlassLayerModule" ) );

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

        backgroundGraphic = new ImageGraphic( "images/glass-pane-background.gif",
                                              new Point2D.Double(
                                                      ( (GreenhouseModel)getModel() ).getBounds().getX(),
                                                      ( (GreenhouseModel)getModel() ).getBounds().getY() ) );
        drawingCanvas.addGraphic( backgroundGraphic, ApparatusPanel.LAYER_DEFAULT - 1 );

        // If the apparatus panelis resized, resize the backdrop graphic
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                Component component = e.getComponent();
                Rectangle2D newBounds = component.getBounds();
                if( backgroundGraphic != null ) {
                    BufferedImage bi = new ImageLoader().loadBufferedImage( "images/glass-pane-background.gif" );
                    double scale = newBounds.getWidth() / bi.getWidth();
                    AffineTransform atx = AffineTransform.getScaleInstance( scale, scale );
                    AffineTransformOp atxOp = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
                    bi = atxOp.filter( bi, null );
                    Rectangle2D modelBounds = ( (GreenhouseModel)getModel() ).getBounds();
                    getApparatusPanel().removeGraphic( backgroundGraphic );
                    backgroundGraphic = new ImageGraphic( bi, new Point2D.Double( -modelBounds.getWidth() / 2, -.50 ) );
                    getApparatusPanel().addGraphic( backgroundGraphic, ApparatusPanel.LAYER_DEFAULT - 1 );
                }
            }
        } );

        // Set up the controls
        setControlPanel( new GlassPaneControlPanel( this ) );

        // Jimmy the temperature
        getEarth().setJimmyArray( jimmyArray );
    }

    public GreenhouseModel getGreenhouseModel() {
        return (GreenhouseModel)getModel();
    }

    public void reset() {
        super.reset();
    }

//    public void activate( PhetApplication phetApplication ) {
//    }
//
//    public void deactivate( PhetApplication phetApplication ) {
//    }

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
            getApparatusPanel().removeGraphic( (Graphic)glassPanesToGraphicMap.get( glassPane ) );
        }

        for( int i = 0; i < numClouds; i++ ) {
            GlassPane glassPane = (GlassPane)glassPanes.get( i );
            getGreenhouseModel().addGlassPane( glassPane );
            getApparatusPanel().addGraphic( (Graphic)glassPanesToGraphicMap.get( glassPane ), ApparatusPanel.LAYER_DEFAULT );
        }
    }


    public int getMaxGlassPanes() {
        return numGlassPanes;
    }

    //
    // Inner classes
    //
    class ModelToGraphicMap {
        HashMap map;

        public void put( ModelElement me, Graphic g ) {
            map.put( me, g );
        }

        Graphic getGraphic( ModelElement me ) {
            return (Graphic)map.get( me );
        }
    }

    public class ModuleScatterEventListener implements Atmosphere.ScatterEventListener {
        public void photonScatered( Photon photon ) {
            if( photonToGraphicsMap.get( photon ) != null ) {
                ScatterEvent se = new ScatterEvent( photon, GlassPaneModule.this );
                ScatterEventGraphic seg = new ScatterEventGraphic( se );
                getModel().addModelElement( se );
                getApparatusPanel().addGraphic( seg, GreenhouseConfig.PHOTON_GRAPHIC_LAYER - 1 );
                scatterToGraphicMap.put( se, seg );
            }
        }
    }
}
