/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.junction;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.selection.CompositeSelectionListener;
import edu.colorado.phet.cck.selection.SelectionListener;
import edu.colorado.phet.common.math.PhetVector;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 28, 2003
 * Time: 2:04:13 AM
 * Copyright (c) Aug 28, 2003 by Sam Reid
 */
public class Junction {
    Branch branch;
    double x;
    double y;
    ArrayList connections = new ArrayList();//connected to other junctions.
    ArrayList observers = new ArrayList();
    CompositeSelectionListener selectionListener = new CompositeSelectionListener();
    boolean selected;
    static int counter = 0;
    private int index;
    private boolean immobile = false;

    public Junction( Branch branch, double x, double y ) {
        this.branch = branch;
        this.x = x;
        this.y = y;
        index = counter;
        counter++;
    }

    public void setImmobile( boolean immobile ) {
        this.immobile = immobile;
    }

    public int getIndex() {
        return index;
    }

    public String toString() {
        return "Junction[" + index + "]";
    }

    public void addSelectionListener( SelectionListener sel ) {
        selectionListener.addSelectionListener( sel );
    }

    public boolean hasConnection( Junction conn ) {
        return connections.contains( conn ) || conn == this;
    }

    private void protectedAdd( Junction other ) {
        if( !hasConnection( other ) && other != this ) {
            connections.add( other );
        }
    }

    //Fully connects the other junction to this junction.
    public void addConnection( Junction other ) {
        //Compile a list of all junctions to participate.
        ArrayList allJunctions = new ArrayList();
        allJunctions.add( other );
        allJunctions.add( this );
        for( int i = 0; i < connections.size(); i++ ) {
            Junction junction2 = (Junction)connections.get( i );
            if( !allJunctions.contains( junction2 ) ) {
                allJunctions.add( junction2 );
            }
        }
        for( int i = 0; i < other.connections.size(); i++ ) {
            Junction junction2 = (Junction)other.connections.get( i );
            if( !allJunctions.contains( junction2 ) ) {
                allJunctions.add( junction2 );
            }
        }
        Junction[] all = (Junction[])allJunctions.toArray( new Junction[0] );

        for( int i = 0; i < allJunctions.size(); i++ ) {
            Junction junction2 = (Junction)allJunctions.get( i );
            junction2.addAllConnections( all );
        }
        for( int i = 0; i < all.length; i++ ) {
            Junction junction2 = all[i];
            junction2.fireConnectivityChanged();
        }
        branch.getCircuit().fireConnectivityChanged();
    }

    private void addAllConnections( Junction[] all ) {
        for( int i = 0; i < all.length; i++ ) {
            Junction junction2 = all[i];
            protectedAdd( junction2 );
        }
    }

    private void fireConnectivityChanged() {
        for( int i = 0; i < observers.size(); i++ ) {
            JunctionObserver junctionObserver = (JunctionObserver)observers.get( i );
            junctionObserver.connectivityChanged();
        }
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double distance( PhetVector start ) {
        return Point2D.Double.distance( start.getX(), start.getY(), x, y );
    }

    public void setLocation( double x, double y ) {
        if( isImmobile() ) {
            return;
        }
        this.x = x;
        this.y = y;
        for( int i = 0; i < connections.size(); i++ ) {
            Junction junction2 = (Junction)connections.get( i );
            junction2.x = x;
            junction2.y = y;
            junction2.fireLocationChanged();
        }
        fireLocationChanged();
    }

    private boolean isImmobile() {
        if( immobile ) {
            return true;
        }
        for( int i = 0; i < connections.size(); i++ ) {
            Junction junction2 = (Junction)connections.get( i );
            if( junction2.immobile ) {
                return true;
            }
        }
        return false;
    }

    public void translate( double dx, double dy ) {
        setLocation( x + dx, y + dy );
//        System.out.println("branch = " + branch);
    }

    public void addObserver( JunctionObserver obs ) {
        observers.add( obs );
    }

    public void fireLocationChanged() {
        fireLocationChangedLocal();
        for( int i = 0; i < connections.size(); i++ ) {
            Junction junction2 = (Junction)connections.get( i );
            junction2.fireLocationChangedLocal();
        }
    }

    private void fireLocationChangedLocal() {
        for( int i = 0; i < observers.size(); i++ ) {
            JunctionObserver junctionObserver = (JunctionObserver)observers.get( i );
            junctionObserver.locationChanged( this );
        }
    }

    public PhetVector getVector() {
        return new PhetVector( x, y );
    }

    public Point2D.Double getLocation() {
        return new Point2D.Double( x, y );
    }

    public Branch getBranch() {
        return branch;
    }

    public int numConnections() {
        return connections.size();
    }

    public void removeJunction( Junction j ) {
        connections.remove( j );
        fireConnectivityChanged();
    }

    public void splitJunction() {
        for( int i = 0; i < connections.size(); i++ ) {
            Junction junction2 = (Junction)connections.get( i );
            junction2.connections.clear();
            junction2.fireConnectivityChanged();
        }
        connections.clear();
        fireConnectivityChanged();
    }

    public Junction[] getConnections() {
        return (Junction[])connections.toArray( new Junction[0] );
    }

    public boolean isStartJunction() {
        if( getBranch().getStartJunction() == this ) {
            return true;
        }
        else if( getBranch().getEndJunction() == this ) {
            return false;
        }
        else {
            throw new RuntimeException( "Parent didn't contain junction." );
        }
    }

    public void setLocationWithoutUpdate( double x1, double y1 ) {
        this.x = x1;
        this.y = y1;
        for( int i = 0; i < connections.size(); i++ ) {
            Junction j2 = (Junction)connections.get( i );
            j2.x = x;
            j2.y = y;
        }
//        for (int i = 0; i < connections.size(); i++) {
//            Junction junction2 = (Junction) connections.get(i);
//            junction2.x = x;
//            junction2.y = y;
//            junction2.fireLocationChanged();
//        }
//        fireLocationChanged();
//
    }

    public void setSelected( boolean selected ) {
        setSelectedLocal( selected );
        for( int i = 0; i < connections.size(); i++ ) {
            Junction junction2 = (Junction)connections.get( i );
            junction2.setSelectedLocal( selected );
        }

    }

    public void setSelectedLocal( boolean selected ) {
        this.selected = selected;
        selectionListener.selectionChanged( selected );
    }

    public boolean isSelected() {
        return selected;
    }
}
