// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import java.awt.geom.Point2D;

import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.vector.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:39 PM
 */
public class Battery extends CircuitComponent {
    private double internalResistance;
    private boolean internalResistanceOn;
    public static final double DEFAULT_INTERNAL_RESISTANCE = 0.001;

    public Battery( double voltage, double internalResistance ) {
        this( new Point2D.Double(), new Vector2D(), 1, 1, new CircuitChangeListener() {
            public void circuitChanged() {
            }
        }, true );
        setKirkhoffEnabled( false );
        this.internalResistance = internalResistance;
        setVoltageDrop( voltage );
        setKirkhoffEnabled( true );
    }

    public Battery( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl, boolean internalResistanceOn ) {
        this( start, dir, length, height, kl, CCKModel.MIN_RESISTANCE, internalResistanceOn );
    }

    public Battery( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl, double internalResistance, boolean internalResistanceOn ) {
        super( kl, start, dir, length, height );
        setKirkhoffEnabled( false );
        setVoltageDrop( 9.0 );
        setInternalResistance( internalResistance );
        setResistance( internalResistance );
        setInternalResistanceOn( internalResistanceOn );
        setKirkhoffEnabled( true );
        userComponent = CCKSimSharing.UserComponents.battery;
    }

    public Battery( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height, double internalResistance, boolean internalResistanceOn ) {
        super( kl, startJunction, endjJunction, length, height );
        setKirkhoffEnabled( false );
        setVoltageDrop( 9.0 );
        setResistance( internalResistance );
        setInternalResistance( internalResistance );
        setInternalResistanceOn( internalResistanceOn );
        setKirkhoffEnabled( true );
        userComponent = CCKSimSharing.UserComponents.battery;
    }

    public void setVoltageDrop( double voltageDrop ) {
        super.setVoltageDrop( voltageDrop );
        super.fireKirkhoffChange();
    }

    public double getEffectiveVoltageDrop() {
        return getVoltageDrop() - getCurrent() * getResistance();
    }

    public void setResistance( double resistance ) {
        if ( resistance < CCKModel.MIN_RESISTANCE ) {
            throw new IllegalArgumentException( "Resistance was les than the min, value=" + resistance + ", min=" + CCKModel.MIN_RESISTANCE );
        }
        super.setResistance( resistance );
    }

    public void setInternalResistance( double resistance ) {
        this.internalResistance = resistance;
        if ( internalResistanceOn ) {
            setResistance( resistance );
        }
    }

    public double getInteralResistance() {
        return internalResistance;
    }

    public void setInternalResistanceOn( boolean internalResistanceOn ) {
        this.internalResistanceOn = internalResistanceOn;
        if ( internalResistanceOn ) {
            setResistance( internalResistance );
        }
        else {
            setResistance( CCKModel.MIN_RESISTANCE );
        }
    }

    public boolean isInternalResistanceOn() {
        return internalResistanceOn;
    }
}
