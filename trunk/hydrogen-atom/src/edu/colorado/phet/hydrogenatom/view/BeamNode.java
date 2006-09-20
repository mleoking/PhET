/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * BeamNode is the beam the comes out of the gun.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BeamNode extends PhetPNode {

    private PPath _pathNode;
    private int _r, _g, _b;
    private int _intensity;
    
    public BeamNode( Dimension size ) {
        super();
        
        setPickable( false );
        setChildrenPickable( false );
        
        _pathNode = new PPath( new Rectangle2D.Double( 0, 0, size.width, size.height ) );
        _pathNode.setStroke( null );
        addChild( _pathNode );

        _r = 255;
        _g = 255;
        _b = 255;
        _intensity = 100;
        updateColor();
    }
    
    public void setColor( Color hue, int intensity ) {
        if ( intensity < 0 || intensity > 100 ) {
            throw new IllegalArgumentException( "intensity out of range: " + intensity );
        }
        _r = hue.getRed();
        _g = hue.getGreen();
        _b = hue.getBlue();
        _intensity = intensity;
        updateColor();
    }
    
    private void updateColor() {
        int a = (int)( ( _intensity / 100d ) * 255 );
        _pathNode.setPaint( new Color( _r, _g, _b, a ) );
    }
}
