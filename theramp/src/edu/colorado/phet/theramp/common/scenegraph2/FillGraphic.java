/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph2;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:14:50 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class FillGraphic extends AbstractGraphic {
    private Shape shape;
    private boolean ensureNoBadPathException = true;
    private Rectangle2D renderRegion;

    public FillGraphic( Shape shape ) {
        this( shape, null );
    }

    public FillGraphic( Shape shape, Color color ) {
        super();
        this.shape = shape;
        setColor( color );
    }

    public void render( RenderEvent renderEvent ) {
        super.setup( renderEvent );
        if( ensureNoBadPathException ) {
            Area myClip = new Area( renderEvent.getClip() );
            myClip.intersect( new Area( shape ) );
            renderEvent.fill( myClip );
        }
        else {
            renderEvent.fill( shape );
        }
        renderRegion = renderEvent.createTransformedShape( getLocalBounds() ).getBounds2D();
    }

    public boolean containsMousePointLocal( double x, double y ) {
        return isVisible() && shape.contains( x, y );
    }

    public Rectangle2D getLocalBounds() {
        return shape.getBounds2D();
    }

    public void collectDirtyRegions( DirtyRegionSet dirty ) {
        super.collectDirtyRegions( dirty );
        if( dirty.isDirty() ) {
            System.out.println( "renderRegion = " + renderRegion );
            System.out.println( "getLocalBounds() = " + getLocalBounds() );
            if( renderRegion != null ) {
//                dirty.addLocalBounds( renderRegion );
                dirty.addScreenBounds( renderRegion );
            }
            dirty.addLocalBounds( getLocalBounds() );
        }
    }

}
