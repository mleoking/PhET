/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;

import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jul 1, 2006
 * Time: 1:07:31 AM
 * Copyright (c) Jul 1, 2006 by Sam Reid
 */

public class JadeElectronNode extends PNode {
    private JadeElectron electron;
    private LocationMap locationMap = new DefaultLocationMap( getRadius() );

    public static double getViewRadius() {
        try {
            return ImageLoader.loadBufferedImage( "images/Electron3.GIF" ).getWidth() / 2;
        }
        catch( IOException e ) {
            e.printStackTrace();
            return Double.NaN;
        }
    }

    public JadeElectronNode( JadeElectron electron ) {
        this.electron = electron;
        addChild( PImageFactory.create( "images/Electron3.GIF" ) );
        setPickable( false );
        setChildrenPickable( false );
        update();
        electron.addListener( new JadeElectron.Listener() {
            public void electronMoved() {
                update();
            }
        } );
    }

    public void update() {
        Point2D loc = locationMap.getLocation( electron.getPosition() );
        setOffset( loc.getX(), loc.getY() );
    }

    public double getRadius() {
        return getFullBounds().getWidth() / 2.0;
    }

    public JadeElectron getCircleParticleForElectron() {
        return electron;
    }

    public void setLocationMap( LocationMap locationMap ) {
        this.locationMap = locationMap;
        update();
    }
}
