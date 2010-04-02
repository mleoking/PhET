/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.Color;
import java.awt.geom.Ellipse2D;

import edu.umd.cs.piccolo.nodes.PPath;


/* Base class for all dot nodes */
abstract class DotNode extends PPath {

    private Ellipse2D circlePath;

    public DotNode( double diameter, Color color ) {
        super();
        circlePath = new Ellipse2D.Double();
        setPaint( color );
        setStroke( null );
        setDiameter( diameter );
    }

    public void setDiameter( double diameter ) {
        circlePath.setFrame( -diameter / 2, -diameter / 2, diameter, diameter );
        setPathTo( circlePath );
    }
    
    /* HA dot node */
    static class HADotNode extends DotNode {
        public HADotNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }
    
    /* A dot node */
    static class ADotNode extends DotNode {
        public ADotNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }
    
    /* H30 dot node */
    static class H3ODotNode extends DotNode {
        public H3ODotNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }

    /* OH dot node */
    static class POHDotNode extends DotNode {
        public POHDotNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }
    
    /* H20 dot node */
    static class H2ODotNode extends DotNode {
        public H2ODotNode( double diameter, Color color ) {
            super( diameter, color );
        }
    }
}
