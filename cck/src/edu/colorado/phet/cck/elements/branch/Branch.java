/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch;

import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.elements.junction.JunctionObserver;
import edu.colorado.phet.cck.elements.xml.BranchData;
import edu.colorado.phet.cck.selection.CompositeSelectionListener;
import edu.colorado.phet.cck.selection.SelectionListener;
import edu.colorado.phet.common.math.PhetVector;

import java.awt.geom.Point2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 28, 2003
 * Time: 2:02:52 AM
 * Copyright (c) Aug 28, 2003 by Sam Reid
 */
public abstract class Branch {
    Junction startJunction;
    Junction endJunction;
    protected Circuit parent;

    ArrayList observers = new ArrayList();
    CompositeSelectionListener selectionListener = new CompositeSelectionListener();
    boolean selected;
    double current;
    private double voltageDrop;
    public static final int UNKNOWN = 0;
    public static final int SAME_AS_ORIENTATION = 1;
    public static final int OPPOSITE_OF_ORIENTATION = -1;
    public static int ID_COUNTER = 0;
    private int id;
    DecimalFormat df = new DecimalFormat( "##.0##" );
    private boolean onFire;

    public int getId() {
        return id;
    }

    public Branch( Circuit parent, Branch source ) {
        this( parent, source.getX1(), source.getY1(), source.getX2(), source.getY2() );
    }

    public Branch( Circuit parent, double x1, double y1, double x2, double y2 ) {
        this.parent = parent;
        this.startJunction = new Junction( this, x1, y1 );
        this.endJunction = new Junction( this, x2, y2 );
        startJunction.addObserver( new JunctionObserver() {
            public void locationChanged( Junction junction2 ) {
                fireJunctionMoved( junction2 );
            }

            public void connectivityChanged() {
            }
        } );
        endJunction.addObserver( new JunctionObserver() {
            public void locationChanged( Junction junction2 ) {
                fireJunctionMoved( junction2 );
            }

            public void connectivityChanged() {
            }
        } );
        this.id = ID_COUNTER;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected( boolean selected ) {
        this.selected = selected;
        selectionListener.selectionChanged( selected );
    }

    public void addObserver( BranchObserver bo ) {
        observers.add( bo );
    }

    protected void fireJunctionMoved( Junction junction ) {
        for( int i = 0; i < observers.size(); i++ ) {
            BranchObserver branchObserver = (BranchObserver)observers.get( i );
            branchObserver.junctionMoved( this, junction );
        }
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

    public void translate( double dx, double dy ) {
        startJunction.translate( dx, dy );
        endJunction.translate( dx, dy );
    }

    public void translateStartPoint( double x, double y ) {
        startJunction.translate( x, y );
    }

    public void translateEndPoint( double x, double y ) {
        endJunction.translate( x, y );
    }

    public PhetVector getStart() {
        return startJunction.getVector();
    }

    public PhetVector getEnd() {
        return endJunction.getVector();
    }

    public PhetVector getDirection() {
        return new PhetVector( getX2() - getX1(), getY2() - getY1() ).getNormalizedInstance();
    }

    public double getLength() {
        return Point2D.Double.distance( getX1(), getY1(), getX2(), getY2() );
    }

    public double getAngle() {
        return getDirection().getAngle();
    }

    public void setStartPointLocation( double x, double y ) {
        startJunction.setLocation( x, y );
    }

    public void setEndPointLocation( double x, double y ) {
        endJunction.setLocation( x, y );
    }

    public void setLocation( Branch location ) {
        startJunction.setLocation( location.getX1(), location.getY1() );
        endJunction.setLocation( location.getX2(), location.getY2() );
    }

    public Junction getStartJunction() {
        return startJunction;
    }

    public Junction getEndJunction() {
        return endJunction;
    }

    public Circuit getCircuit() {
        return parent;
    }

    public boolean containsJunction( Junction j2 ) {
        return j2 == startJunction || j2 == endJunction;
    }

    public void removeJunction( Junction j ) {
        startJunction.removeJunction( j );
        endJunction.removeJunction( j );
    }

    public boolean connectsTo( Junction junction ) {
        return startJunction.hasConnection( junction ) || endJunction.hasConnection( junction );
    }

    public Junction getOppositeJunction( Junction j1 ) {
        if( j1 == startJunction ) {
            return endJunction;
        }
        else if( j1 == endJunction ) {
            return startJunction;
        }
        else {
            throw new RuntimeException( "No such junction." );
        }
    }


    public void setCurrent( double current ) {
        setCurrentNoUpdate( current );
        fireCurrentChanged();
    }

    public void setCurrentNoUpdate( double current ) {
        this.current = current;
    }

    protected void fireCurrentChanged() {
        for( int i = 0; i < observers.size(); i++ ) {
            BranchObserver branchObserver = (BranchObserver)observers.get( i );
            branchObserver.currentOrVoltageChanged( this );
        }
    }

    public double getCurrent() {
        return current;
    }

    public double getVoltageDrop() {
        return voltageDrop;
    }

    public void setVoltageDrop( double voltageDrop ) {
        this.voltageDrop = voltageDrop;
        fireCurrentChanged();
    }

    public PhetVector getPosition2D( double x ) {
        if( !containsScalarLocation( x ) ) {
//            throw new RuntimeException("Particle not contained in wire.");//TODO fixme
            return new PhetVector();
        }
        return getDirection().getScaledInstance( x ).getAddedInstance( getStart() );
    }

    public boolean containsScalarLocation( double x ) {
        return x >= 0 && x <= getLength();
    }

    public void addSelectionListener( SelectionListener sel ) {
        selectionListener.addSelectionListener( sel );
    }

    public Junction getEquivalentJunction( Junction junction ) {
        if( junction == startJunction ) {
            return startJunction;
        }
        else if( junction == endJunction ) {
            return endJunction;
        }
        else {
            Junction[] j = junction.getConnections();
            for( int i = 0; i < j.length; i++ ) {
                Junction junction2 = j[i];
                if( junction2 == startJunction ) {
                    return startJunction;
                }
                else if( junction2 == endJunction ) {
                    return endJunction;
                }
            }
            return null;
        }
    }
//    static final DecimalFormat df=new DecimalFormat("0.0");
    public String toString() {
        return "x1=" + df.format( getX1() ) + ", y1=" + df.format( getY1() ) + ", x2=" + df.format( getX2() ) + ", y2=" + df.format( getY2() ) + ", voltage=" + df.format( voltageDrop ) + ", current=" + df.format( current ) + ", id=" + id;
    }

    public abstract Branch copy();

    public void setOnFire( boolean onFire ) {
        this.onFire = onFire;
    }

    public boolean isOnFire() {
        return onFire;
    }

    public abstract BranchData toBranchData();

    public void setVoltageDropNoUpdate( double v ) {
        this.voltageDrop = v;
    }

    public void setCurrentAndVoltage( double amps, double volts ) {
        setCurrent( amps );
        setVoltageDrop( volts );
    }

}
