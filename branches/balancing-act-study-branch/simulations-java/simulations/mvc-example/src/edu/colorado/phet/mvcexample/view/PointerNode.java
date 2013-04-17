// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.GeneralPath;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * PointerNode is a node that looks like this:
 * 
 *     --------\
 *     |        \
 *     |        /
 *     --------/
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class PointerNode extends PPath {

    public PointerNode( Dimension size, Color fillColor ) {
        super();
        
        // pointer with origin at geometric center
        final float w = (float) size.getWidth();
        final float h = (float) size.getHeight();
        GeneralPath path = new GeneralPath();
        path.moveTo( w / 2, 0 );
        path.lineTo( w / 4, h / 2 );
        path.lineTo( -w / 2, h / 2 );
        path.lineTo( -w / 2, -h / 2 );
        path.lineTo( w / 4, -h / 2 );
        path.closePath();
        setPathTo( path );
        
        setStroke( new BasicStroke( 1f ) );
        setStrokePaint( Color.BLACK );
        setPaint( fillColor );
        
        addInputEventListener( new CursorHandler() );
    }
}
