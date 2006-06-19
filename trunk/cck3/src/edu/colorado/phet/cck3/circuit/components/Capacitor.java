/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.components;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.CircuitChangeListener;
import edu.colorado.phet.cck3.circuit.DynamicBranch;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import net.n3.nanoxml.IXMLElement;

import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:17 PM
 * Copyright (c) May 28, 2004 by Sam Reid
 */
public class Capacitor extends CircuitComponent implements DynamicBranch {
    public static final double DEFAULT_CAPACITANCE = 0.01;
    double capacitance = DEFAULT_CAPACITANCE;

    public Capacitor( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl ) {
        super( kl, start, dir, length, height );
        setKirkhoffEnabled( false );
        setResistance( CCK3Module.MIN_RESISTANCE );
        setKirkhoffEnabled( true );
    }

    public Capacitor( CircuitChangeListener kl, Junction startJunction, Junction endjJunction, double length, double height ) {
        super( kl, startJunction, endjJunction, length, height );
    }

    public Capacitor( double resistance ) {
        this( new Point2D.Double(), new Vector2D.Double(), 1, 1, new CircuitChangeListener() {
            public void circuitChanged() {
            }
        } );
        setKirkhoffEnabled( false );
        setResistance( resistance );
        setKirkhoffEnabled( true );
    }

    public void addAttributes( IXMLElement xml ) {
        xml.setAttribute( "resistance", getResistance() + "" );
    }

    public double getCapacitance() {
        return capacitance;
    }

    public void setCapacitance( double capacitance ) {
        this.capacitance = capacitance;
        notifyObservers();
        fireKirkhoffChange();
    }

    public void stepInTime( double dt ) {
    }

    public void resetDynamics() {
        setKirkhoffEnabled( false );
        setVoltageDrop( 0.0 );
        setKirkhoffEnabled( true );
    }

    public void setTime( double time ) {
    }
}
