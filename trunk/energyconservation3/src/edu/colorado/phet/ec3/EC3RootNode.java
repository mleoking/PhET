/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.ec3.model.EnergyConservationModel;
import edu.colorado.phet.ec3.model.Floor;
import edu.colorado.phet.ec3.view.BodyGraphic;
import edu.colorado.phet.ec3.view.FloorGraphic;
import edu.colorado.phet.ec3.view.SplineGraphic;
import edu.colorado.phet.piccolo.PhetRootPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Oct 21, 2005
 * Time: 2:16:21 PM
 * Copyright (c) Oct 21, 2005 by Sam Reid
 */

public class EC3RootNode extends PhetRootPNode {
    private PNode bodyGraphics = new PNode();
    private PNode splineGraphics = new PNode();
    private PNode buses;
    private EC3Module ec3Module;
    private EC3Canvas ec3Canvas;

    public EC3RootNode( EC3Module ec3Module, EC3Canvas ec3Canvas ) {
        this.ec3Module = ec3Module;
        this.ec3Canvas = ec3Canvas;
        EnergyConservationModel ec3Model = getModel();
        Floor floor = ec3Model.floorAt( 0 );
        PhetRootPNode.Layer layer = new PhetRootPNode.Layer();
        addLayer( layer, 0 );

        addWorldChild( new SkyGraphic( floor.getY() ) );
        addWorldChild( new FloorGraphic( floor ) );
        addWorldChild( splineGraphics );
        addWorldChild( bodyGraphics );

        PhetRootPNode.Layer topLayer = new Layer();
        addLayer( topLayer );

        SplineToolbox splineToolbox = new SplineToolbox( ec3Canvas, 50, 50 );
        topLayer.addChild( splineToolbox );
    }

    private EnergyConservationModel getModel() {
        EnergyConservationModel ec3Model = ec3Module.getEnergyConservationModel();
        return ec3Model;
    }

    public void clearBuses() {
        if( buses != null ) {
            buses.removeAllChildren();
            removeWorldChild( buses );
            buses = null;
        }
    }

    public void addBuses() {
        if( buses == null ) {
            try {
                buses = new PNode();
                Floor floor = getModel().floorAt( 0 );
                BufferedImage newImage = ImageLoader.loadBufferedImage( "images/schoolbus200.gif" );
                PImage schoolBus = new PImage( newImage );
                double y = floor.getY() - schoolBus.getFullBounds().getHeight() + 10;
                schoolBus.setOffset( 0, y );
                double busStart = 500;
                for( int i = 0; i < 10; i++ ) {
                    PImage bus = new PImage( newImage );
                    double dbus = 2;
                    bus.setOffset( busStart + i * ( bus.getFullBounds().getWidth() + dbus ), y );
                    buses.addChild( bus );
                }
                addWorldChild( buses );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void addSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.addChild( splineGraphic );
    }

    public void reset() {
        bodyGraphics.removeAllChildren();
        splineGraphics.removeAllChildren();
        clearBuses();
    }

    public void addBodyGraphic( BodyGraphic bodyGraphic ) {
        bodyGraphics.addChild( bodyGraphic );
    }

    public void toggleBox() {
        if( bodyGraphics.getChildrenReference().size() > 0 ) {
            boolean state = ( (BodyGraphic)bodyGraphics.getChildrenReference().get( 0 ) ).isBoxVisible();
            for( int i = 0; i < bodyGraphics.getChildrenReference().size(); i++ ) {
                BodyGraphic bodyGraphic = (BodyGraphic)bodyGraphics.getChildrenReference().get( i );
                bodyGraphic.setBoxVisible( !state );
            }
        }
    }

    public SplineGraphic splineGraphicAt( int i ) {
        return (SplineGraphic)splineGraphics.getChildrenReference().get( i );
    }

    public int numSplineGraphics() {
        return splineGraphics.getChildrenReference().size();
    }

    public void removeSplineGraphic( SplineGraphic splineGraphic ) {
        splineGraphics.removeChild( splineGraphic );
    }

    public void updateGraphics() {
        updateSplines();
        updateBodies();
    }

    private void updateBodies() {
    }

    private void updateSplines() {
        if( !splinesCorrect() ) {
            correctSplines();
        }
    }

    private void correctSplines() {
        splineGraphics.removeAllChildren();
        for( int i = 0; i < getModel().numSplineSurfaces(); i++ ) {
            SplineGraphic splineGraphic = new SplineGraphic( ec3Canvas, getModel().splineSurfaceAt( i ) );
            addSplineGraphic( splineGraphic );
        }
    }

    private boolean splinesCorrect() {
        if( getModel().numSplineSurfaces() != splineGraphics.getChildrenCount() ) {
            return false;
        }
        else {
            for( int i = 0; i < getModel().numSplineSurfaces(); i++ ) {
                if( splineGraphicAt( i ).getSplineSurface() != getModel().splineSurfaceAt( i ) ) {
//                if( splineGraphicAt( i ).getSpline() != getModel().splineSurfaceAt( i ) ) {
                    return false;
                }
            }
        }
        return true;
    }

}
