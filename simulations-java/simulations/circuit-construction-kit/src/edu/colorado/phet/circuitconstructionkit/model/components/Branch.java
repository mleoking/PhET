// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.CompositeCircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.CurrentVoltListener;
import edu.colorado.phet.circuitconstructionkit.model.Electron;
import edu.colorado.phet.circuitconstructionkit.model.Electron.Observer;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.SimpleObservableDebug;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:32:58 AM
 */
public abstract class Branch extends SimpleObservableDebug {
    private double resistance = CCKModel.MIN_RESISTANCE;
    private Junction startJunction;
    private Junction endJunction;
    private CompositeCircuitChangeListener circuitChangeListeners = new CompositeCircuitChangeListener();
    private ArrayList<CurrentVoltListener> ivListeners = new ArrayList<CurrentVoltListener>();
    private boolean isSelected = false;
    private boolean kirkhoffEnabled = true;
    private ArrayList<FlameListener> flameListeners = new ArrayList<FlameListener>();
    private boolean isOnFire = false;
    private boolean editing = false;
    private String name;/*For purposes of debugging.*/

    private double current; //average current (averaged over timestep subdivisions for one subdivided stepInTime) for display in an ammeter or chart
    private double mnaCurrent;//instantaneous current for the MNA model (i.e. may differ from aggregate current which is displayed on screen), see #2270

    private double voltageDrop;//average voltage drop (averaged over timestep subdivisions for one subdivided stepInTime) for display in voltmeter or chart
    private double mnaVoltageDrop;//see notes above for mnaCurrent

    private static int indexCounter = 0;

    protected Branch( CircuitChangeListener listener ) {
        name = toLabel( indexCounter++ );
        setResistance( CCKModel.MIN_RESISTANCE );
        addKirkhoffListener( listener );
    }

    public Branch( CircuitChangeListener listener, Junction startJunction, Junction endJunction ) {
        this( listener );
        this.startJunction = startJunction;
        this.endJunction = endJunction;
        startJunction.addObserver( so );
        endJunction.addObserver( so );
    }

    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Sets a name which could be used for debugging.
     *
     * @param label the name to assign to this Branch.
     */
    public void setName( String label ) {
        this.name = label;
    }

    //todo this could be awkward
    //if a junction changes, this branch has also changed.
    SimpleObserver so = new SimpleObserver() {
        public void update() {
            notifyObservers();
        }
    };

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected( boolean selected ) {
        isSelected = selected;
        notifyObservers();
    }

    public void removeFlameListener( FlameListener flameListener ) {
        flameListeners.remove( flameListener );
    }

    public static interface FlameListener {
        void flameStarted();

        void flameFinished();
    }

    public void addFlameListener( FlameListener flameListener ) {
        flameListeners.add( flameListener );
    }

    public void addCurrentVoltListener( CurrentVoltListener currentListener ) {
        ivListeners.add( currentListener );
    }

    public void removeCurrentVoltListener( CurrentVoltListener currentListener ) {
        ivListeners.remove( currentListener );
    }

    public void addKirkhoffListener( CircuitChangeListener circuitChangeListener ) {
        circuitChangeListeners.addCircuitChangeListener( circuitChangeListener );
    }

    public void addObserver( SimpleObserver so ) {
        super.addObserver( so );
    }

    public String toString() {
        return "Branch_" + name + "[" + startJunction.getLabel() + "," + endJunction.getLabel() + "] <" + getClass() + ">";
    }

    public Vector2D getDirectionVector() {
        return new Vector2D( startJunction.getPosition(), endJunction.getPosition() );
    }

    public double getResistance() {
        return resistance;
    }

    public double getCurrent() {
        return current;
    }

    public double getVoltageDrop() {
        return voltageDrop;
    }

    public void setResistance( double resistance ) {
        if ( resistance < 0 ) {
            throw new RuntimeException( "Resistance was < 0, value=" + resistance );
        }
        if ( resistance < CCKModel.MIN_RESISTANCE ) {
            throw new RuntimeException( "Resistance was less than MIN, res=" + resistance + ", min=" + CCKModel.MIN_RESISTANCE );
        }
        if ( resistance != this.resistance ) {
            this.resistance = resistance;
            notifyObservers();
            fireKirkhoffChange();
        }
    }

    public void notifyObservers() {
        //Find out which electrons should not be updated since they have been removed from the model
        ArrayList<Electron.Observer> toRemove = new ArrayList<Observer>();
        for ( int i = 0; i < getObserverList().size(); i++ ) {
            SimpleObserver observer = getObserverList().get( i );
            if ( observer instanceof Electron.Observer ) {
                Electron.Observer e = (Electron.Observer) observer;
                if ( e.isDeleted() ) {
                    toRemove.add( e );
                }
            }
        }

        //Remove any deleted electrons from our list
        for ( Observer observer : toRemove ) {
            removeObserver( observer );
        }

        //Notify any remaining listeners that the Branch state has changed.
        super.notifyObservers();
    }

    public void setCurrent( double current ) {
        if ( this.current != current ) {
            this.current = current;
            notifyObservers();
            for ( CurrentVoltListener ivListener : ivListeners ) {
                ivListener.currentOrVoltageChanged( this );
            }
        }
        boolean shouldBeOnFire = Math.abs( current ) > 10.0;
        if ( shouldBeOnFire != isOnFire ) {
            this.isOnFire = shouldBeOnFire;
            if ( isOnFire ) {
                for ( FlameListener flameListener : flameListeners ) {
                    flameListener.flameFinished();
                }
            }
            else {
                for ( FlameListener flameListener : flameListeners ) {
                    flameListener.flameStarted();
                }
            }
        }
    }


    public void setVoltageDrop( double voltageDrop ) {
        if ( this.voltageDrop != voltageDrop ) {
            this.voltageDrop = voltageDrop;
            notifyObservers();
            for ( int i = 0; i < ivListeners.size(); i++ ) {
                ivListeners.get( i ).currentOrVoltageChanged( this );
            }
        }
    }

    public void fireKirkhoffChange() {
        if ( kirkhoffEnabled ) {
            circuitChangeListeners.circuitChanged();
        }
    }

    public Point2D getStartPoint() {
        return startJunction.getPosition();
    }

    public Point2D getEndPoint() {
        return endJunction.getPosition();
    }

    public double getX1() {
        return startJunction.getX();
    }

    public double getY1() {
        return startJunction.getY();
    }

    public double getX2() {
        return endJunction.getX();
    }

    public double getY2() {
        return endJunction.getY();
    }

    public Junction getStartJunction() {
        return startJunction;
    }

    public Junction getEndJunction() {
        return endJunction;
    }

    public boolean hasJunction( Junction junction ) {
        return endJunction == junction || startJunction == junction;
    }

    public Junction opposite( Junction a ) {
        if ( startJunction == a ) {
            return endJunction;
        }
        else if ( endJunction == a ) {
            return startJunction;
        }
        else {
            throw new RuntimeException( "No such junction: " + a );
        }
    }

    public void setStartJunction( Junction newJunction ) {
        if ( startJunction != null ) {
            startJunction.removeObserver( so );
        }
        this.startJunction = newJunction;
        startJunction.addObserver( so );
    }

    public void translate( double dx, double dy ) {
        getStartJunction().translate( dx, dy );
        getEndJunction().translate( dx, dy );
    }

    public void setEndJunction( Junction newJunction ) {
        if ( endJunction != null ) {
            endJunction.removeObserver( so );
        }
        this.endJunction = newJunction;
        endJunction.addObserver( so );
    }

    public void replaceJunction( Junction junction, Junction newJ ) {
        if ( junction == startJunction ) {
            setStartJunction( newJ );
        }
        else if ( junction == endJunction ) {
            setEndJunction( newJ );
        }
        else {
            throw new RuntimeException( "No such junction." );
        }
    }

    public double getLength() {
        return getStartJunction().getDistance( getEndJunction() );
    }

    public Point2D getPosition( double x ) {
        if ( getLength() == 0 ) {
            return getStartJunction().getPosition();
        }
        Vector2D vec = new MutableVector2D( getStartJunction().getPosition(), getEndJunction().getPosition() ).getInstanceOfMagnitude( x );
        return vec.getDestination( getStartJunction().getPosition() );
    }

    private static String toLabel( int label ) {
        char ch = 'a';
        ch += label % 26;
        int val = label / 26;
        String out = "";
        if ( val == 0 ) {
            out = "" + ch;
        }
        else {
            out = "" + ch + "_" + val;
        }
        return out;
    }

    public String getName() {
        return name;
    }

    public boolean containsScalarLocation( double x ) {
        return x >= 0 && x <= getLength();
    }

    public double getAngle() {
        return new Vector2D( getStartJunction().getPosition(), getEndJunction().getPosition() ).getAngle();
    }

    public Point2D getCenter() {
        return new MutableVector2D( getStartJunction().getPosition(), getEndJunction().getPosition() ).times( .5 ).getDestination( getStartJunction().getPosition() );
    }

    public void delete() {
        removeAllObservers();
        endJunction.removeObserver( so );
        startJunction.removeObserver( so );
    }

    public void setKirkhoffEnabled( boolean kirkhoffEnabled ) {
        this.kirkhoffEnabled = kirkhoffEnabled;
    }

    public abstract Shape getShape();

    public boolean isOnFire() {
        return isOnFire;
    }

    public boolean isEditing() {
        return editing;
    }

    public void setEditing( boolean editing ) {
        if ( this.editing != editing ) {
            this.editing = editing;
            notifyObservers();
        }
    }

    public boolean isEditable() {
        return true;
    }

    public void setMNACurrent( double mnaCurrent ) {
        this.mnaCurrent = mnaCurrent;
    }

    public double getMNACurrent() {
        return mnaCurrent;
    }

    public void setMNAVoltageDrop( double mnaVoltageDrop ) {
        this.mnaVoltageDrop = mnaVoltageDrop;
    }

    public double getMNAVoltageDrop() {
        return mnaVoltageDrop;
    }
}