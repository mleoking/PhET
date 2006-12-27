/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.util.SimpleObserver;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:17 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class Bulb extends CircuitComponent {
    private Filament filament;
    private SimpleObserver so;
    private double width;

    public Bulb( Point2D start, AbstractVector2D dir, double distBetweenJunctions, double width, double height, KirkhoffListener kl ) {
        super( kl, start, dir, distBetweenJunctions, height );
        this.width = width;
        filament = new Filament( kl, getStartJunction(), getEndJunction(), 3, height * .25, width * .8, height * .061 );
        so = new SimpleObserver() {
            public void update() {
                filament.recompute();
            }
        };
        addObserver( so );
        getStartJunction().addObserver( so );
        getEndJunction().addObserver( so );
        setResistance( 10 );
    }

    public double getWidth() {
        return width;
    }

    public void setStartJunction( Junction newJunction ) {
        super.setStartJunction( newJunction );
        filament.setStartJunction( newJunction );
        newJunction.addObserver( so );
    }

    public void setEndJunction( Junction newJunction ) {
        super.setEndJunction( newJunction );
        filament.setEndJunction( newJunction );
        newJunction.addObserver( so );
    }

    public Filament getFilament() {
        return filament;
    }

    public Point2D getPosition( double x ) {
        if( containsScalarLocation( x ) ) {
            return filament.getPosition( x );
        }
        else {
            if( Double.isNaN( getLength() ) ) {
                throw new RuntimeException( "Length was NaN." );
            }
            //this occurs when dragging the bulb after splitting.  maybe splitting needs to relayout.
            Toolkit.getDefaultToolkit().beep();
            throw new RuntimeException( "position not within bulb: x=" + x + ", length=" + getLength() );
        }
    }

    public double getLength() {
        return filament.getLength();
    }

    public double getComponentLength() {
        return super.getLength();
    }
}
