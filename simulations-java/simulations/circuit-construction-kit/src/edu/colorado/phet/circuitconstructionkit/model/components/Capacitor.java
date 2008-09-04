package edu.colorado.phet.circuitconstructionkit.model.components;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.DynamicBranch;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

/**
 * User: Sam Reid
 * Date: May 28, 2004
 * Time: 1:11:17 PM
 */
public class Capacitor extends CircuitComponent implements DynamicBranch {
    public static final double DEFAULT_CAPACITANCE = 0.02;
    double capacitance = DEFAULT_CAPACITANCE;
    private ArrayList listeners = new ArrayList();

    public Capacitor( Point2D start, AbstractVector2D dir, double length, double height, CircuitChangeListener kl ) {
        super( kl, start, dir, length, height );
        setKirkhoffEnabled( false );
        setResistance( CCKModel.MIN_RESISTANCE );
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

    public double getCapacitance() {
        return capacitance;
    }

    public void setCapacitance( double capacitance ) {
        this.capacitance = capacitance;
        notifyObservers();
        fireKirkhoffChange();
        notifyChargeChanged();
    }

    /**
     * Set the capacitance and keep the charge constant.  That means that
     * the voltage will need to be changed accordingly.
     * 
     * @param capacitance
     */
    public void setCapacitanceConstantCharge( double capacitance ) {
        double q = getCharge();
        setCapacitance( capacitance );
        setVoltageDrop( q / capacitance );
        notifyObservers();
        fireKirkhoffChange();
        notifyChargeChanged();
    }

    public void setVoltageDrop( double voltageDrop ) {
        super.setVoltageDrop( voltageDrop );
        notifyChargeChanged();
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

    public double getCharge() {
        return getCapacitance() * getVoltageDrop();
    }

    public static interface Listener {
        public void chargeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    public void notifyChargeChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.chargeChanged();
        }
    }
}
