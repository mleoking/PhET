/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman.view;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 7, 2005
 * Time: 12:23:12 AM
 * Copyright (c) Apr 7, 2005 by Sam Reid
 */

public class WalkwayObjectGraphic extends GraphicLayerSet {
    private WalkWayGraphic walkWayGraphic;
    private double modelLocation;
    private BufferedImage image;

    public WalkwayObjectGraphic( WalkWayGraphic walkWayGraphic, double coordinate, String imageURL ) {
        this( walkWayGraphic, coordinate, loadImage( imageURL ) );
    }

    private static BufferedImage loadImage( String imageURL ) {
        try {
            return ImageLoader.loadBufferedImage( imageURL );
        }
        catch( IOException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public WalkwayObjectGraphic( WalkWayGraphic walkWayGraphic, double coordinate, BufferedImage image ) {
        super( walkWayGraphic.getComponent() );
        this.walkWayGraphic = walkWayGraphic;
        this.modelLocation = coordinate;
        PhetImageGraphic phetImageGraphic = new PhetImageGraphic( getComponent(), image );
        addGraphic( phetImageGraphic );
    }

    public void setModelLocation( double modelLocation ) {
        this.modelLocation = modelLocation;
    }

    public void update() {
        int x = walkWayGraphic.getViewCoordinate( modelLocation );
        int y = walkWayGraphic.getFloorY() - getHeight();
        setLocation( x - getWidth() / 2, y );
    }

    protected WalkWayGraphic getWalkWayGraphic() {
        return walkWayGraphic;
    }

    public double getModelLocation() {
        return modelLocation;
    }
}
