/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph2;

import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 8, 2005
 * Time: 3:57:55 AM
 * Copyright (c) Jun 8, 2005 by Sam Reid
 */

public class DirtyRegionSet {
    private boolean dirty;
    private AffineTransform transform = new AffineTransform();
    private ArrayList rectangles = new ArrayList();

    public void concatenate( AffineTransform transform ) {
        this.transform.concatenate( transform );
    }

    public void addLocalBounds( Rectangle2D r ) {
        Rectangle2D bounds2D = transform.createTransformedShape( r ).getBounds2D();
//        System.out.println( "tx'ed= " + bounds2D );
        rectangles.add( bounds2D );
    }

    public void setRegionDirty() {//todo restore state.
        dirty = true;
    }

    public ArrayList getRegions() {
        return rectangles;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void preConcatenate( AffineTransform transform ) {
        transform.preConcatenate( transform );
    }

    public void addScreenBounds( Rectangle2D renderRegion ) {
        rectangles.add( renderRegion );
    }
}
