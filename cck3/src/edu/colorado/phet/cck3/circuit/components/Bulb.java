/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
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
    private KirkhoffListener kl;
    private double distBetweenJunctions;
    boolean isSchematic = false;

    public Bulb( Point2D start, AbstractVector2D dir, double distBetweenJunctions, double width, double height, KirkhoffListener kl ) {
        super( kl, start, dir, distBetweenJunctions, height );
        this.distBetweenJunctions = distBetweenJunctions;
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
        this.kl = kl;
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
        if( isSchematic ) {
            return super.getPosition( x );
        }
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
        if( isSchematic ) {
            return getStartJunction().getPosition().distance( getEndJunction().getPosition() );
        }
        return filament.getLength();
    }

    public double getComponentLength() {
        if( isSchematic ) {
            return getLength();
        }
        else {
            return super.getLength();
        }
    }

    public void setSchematic( boolean schematic, Circuit circuit ) {
        if( this.isSchematic == schematic ) {
            return;
        }
        this.isSchematic = schematic;
        //move junctions, if necessary.
        Vector2D delta = null;
        if( schematic ) {
            delta = expandToSchematic( this );
        }
        else {
            delta = collaspeToLifelike( this );
        }
//        Branch[] stronglyConnected = circuit.getStrongConnections( getEndJunction() );
//        BranchSet bs = new BranchSet( circuit, stronglyConnected );
//        bs.removeBranch( this );
//        bs.translate( delta );
    }

    private Vector2D collaspeToLifelike( Bulb bulb ) {
        double distBetweenJ = CCK3Module.BULB_DIMENSION.getDistBetweenJunctions();
        AbstractVector2D vector = bulb.getDirectionVector().getInstanceOfMagnitude( distBetweenJ );
        Point2D dst = vector.getDestination( bulb.getStartJunction().getPosition() );
        Vector2D delta = new Vector2D.Double( bulb.getEndJunction().getPosition(), dst );
        bulb.getEndJunction().setPosition( dst.getX(), dst.getY() );
        return delta;
    }

    private Vector2D expandToSchematic( Bulb bulb ) {
        Vector2D vec = new Vector2D.Double( bulb.getStartJunction().getPosition(), bulb.getEndJunction().getPosition() );
        Point2D dst = vec.getInstanceOfMagnitude( CCK3Module.SCH_BULB_DIST ).getDestination( bulb.getStartJunction().getPosition() );
        Vector2D delta = new Vector2D.Double( bulb.getEndJunction().getPosition(), dst );
        bulb.getEndJunction().setPosition( dst.getX(), dst.getY() );
        return delta;
    }

}
