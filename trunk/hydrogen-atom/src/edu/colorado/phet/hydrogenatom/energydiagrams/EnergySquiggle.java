/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.energydiagrams;

import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import edu.umd.cs.piccolo.nodes.PPath;


public class EnergySquiggle extends PPath {

    public EnergySquiggle( Color color, Stroke stroke, Point2D p1, Point2D p2 ) {
        this( color, stroke, p1.getX(), p1.getY(), p2.getX(), p2.getY() );
    }
    
    public EnergySquiggle( Color color, Stroke stroke, double x1, double y1, double x2, double y2 ) {
        super();
        setPathTo( new Line2D.Double( x1, y1, x2, y2 ) );
        setStroke( stroke );
        setStrokePaint( color );
        setPaint( color );
    }
}
