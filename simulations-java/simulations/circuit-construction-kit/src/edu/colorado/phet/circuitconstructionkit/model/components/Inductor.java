// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import java.awt.geom.Point2D;

import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.DynamicBranch;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * Inductor model for CCK.
 *
 * @author Sam Reid
 */
public class Inductor extends CircuitComponent implements DynamicBranch {
    //only used in the ComponentMenu for inductor editor, but consolidated here
    public static final double MIN_INDUCTANCE = 10;
    public static final double MAX_INDUCTANCE = 100;

    private static final double DEFAULT_INDUCTANCE = 50;//50 henries makes tau=L/R = 5 sec for default resistor; this saturates in about 5 * tau = 25 sec
    private double inductance = DEFAULT_INDUCTANCE;

    public Inductor( Point2D start, Vector2D dir, double length, double height, CircuitChangeListener kl ) {
        super( kl, start, dir, length, height );
        setKirkhoffEnabled( false );
        setResistance( CCKModel.MIN_RESISTANCE );
        setKirkhoffEnabled( true );
    }

    public Inductor( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
    }

    public Inductor( double resistance ) {
        this( new Point2D.Double(), new Vector2D(), 1, 1, new CircuitChangeListener() {
            public void circuitChanged() {
            }
        } );
        setKirkhoffEnabled( false );
        setResistance( resistance );
        setKirkhoffEnabled( true );
    }

    public void setInductance( double inductance ) {
        this.inductance = inductance;
        notifyObservers();
        fireKirkhoffChange();
    }

    public void stepInTime( double dt ) {
    }

    public void resetDynamics() {//Todo: this is duplicated in Capacitor
        setKirkhoffEnabled( false );
        setVoltageDrop( 0.0 );
        setCurrent( 0.0 );
        setMNACurrent( 0.0 );
        setMNAVoltageDrop( 0.0 );
        setKirkhoffEnabled( true );
    }

    public void setTime( double time ) {
    }

    public double getInductance() {
        return inductance;
    }

    public void discharge() {
        resetDynamics();
    }

}