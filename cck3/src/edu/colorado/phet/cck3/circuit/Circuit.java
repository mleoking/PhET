/** Sam Reid*/
package edu.colorado.phet.cck3.circuit;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.components.*;
import edu.colorado.phet.cck3.circuit.tools.VoltmeterGraphic;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import net.n3.nanoxml.IXMLElement;
import net.n3.nanoxml.XMLElement;
import net.n3.nanoxml.XMLWriter;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:31:24 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class Circuit {
    ArrayList branches = new ArrayList();
    ArrayList junctions = new ArrayList();
    ArrayList listeners = new ArrayList();
    private KirkhoffListener kirkhoffListener;

//    public Object clone() {
//        try {
//            Circuit clone = (Circuit)super.clone();
//            clone.branches.addAll( branches );
//            clone.junctions.addAll( junctions );
//            clone.listeners.addAll( listeners );
//            clone.kirkhoffListener = kirkhoffListener;
//            return clone;
//        }
//        catch( CloneNotSupportedException e ) {
//            e.printStackTrace();
//            throw new RuntimeException( e );
//        }
//    }

    public Circuit( KirkhoffListener kirkhoffListener ) {
        this.kirkhoffListener = kirkhoffListener;
    }

    public void addCircuitListener( CircuitListener listener ) {
        listeners.add( listener );
    }

    public String toString() {
        return "Junctions=" + junctions + ", Branches=" + branches;
    }

    public void addJunction( Junction junction ) {
        if( !junctions.contains( junction ) ) {
            junctions.add( junction );
        }
        else {
            System.out.println( "Already contained junction." );
        }
    }

    public Branch[] getAdjacentBranches( Junction junction ) {
        ArrayList out = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( junction ) ) {
                out.add( branch );
            }
        }
        return (Branch[])out.toArray( new Branch[0] );
    }

    public void updateNeighbors( Junction junction ) {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( junction ) ) {
                branch.notifyObservers();
            }
        }
    }

    public void updateAll() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            branch.notifyObservers();
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction)junctions.get( i );
            junction.notifyObservers();
        }
    }

    public int numJunctions() {
        return junctions.size();
    }

    public Junction junctionAt( int i ) {
        return (Junction)junctions.get( i );
    }

    public boolean hasBranch( Junction a, Junction b ) {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( a ) && branch.hasJunction( b ) ) {
                return true;
            }
        }
        return false;
    }

    public Junction[] getNeighbors( Junction a ) {
        ArrayList n = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.hasJunction( a ) ) {
                n.add( branch.opposite( a ) );
            }
        }
        return (Junction[])n.toArray( new Junction[0] );
    }

    public void replaceJunction( Junction old, Junction newJunction ) {
        junctions.remove( old );
        old.delete();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.getStartJunction() == old ) {
                branch.setStartJunction( newJunction );
            }
            if( branch.getEndJunction() == old ) {
                branch.setEndJunction( newJunction );
            }
        }
        fireJunctionRemoved( old );
    }

    private void fireJunctionRemoved( Junction junction ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionRemoved( junction );
        }
    }

    public boolean areNeighbors( Junction a, Junction b ) {
        if( a == b ) {
            return false;
        }
        Junction[] na = getNeighbors( a );
        return Arrays.asList( na ).contains( b );
    }

    public void addBranch( Branch component ) {
        addJunction( component.getStartJunction() );
        addJunction( component.getEndJunction() );
        branches.add( component );
    }

    public void notifyNeighbors( Branch b ) {
        ArrayList alreadyNotified = new ArrayList();
        Branch[] br1 = getAdjacentBranches( b.getStartJunction() );
        Branch[] br2 = getAdjacentBranches( b.getEndJunction() );
        ArrayList all = new ArrayList();
        all.addAll( Arrays.asList( br1 ) );
        all.addAll( Arrays.asList( br2 ) );
        for( int i = 0; i < all.size(); i++ ) {
            Branch branch = (Branch)all.get( i );
            if( !alreadyNotified.contains( branch ) ) {
                alreadyNotified.add( branch );
                branch.notifyObservers();
            }
        }
    }

    public Junction[] split( Junction junction ) {
        Branch[] b = getAdjacentBranches( junction );
        Junction[] newJunctions = new Junction[b.length];
        for( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            Junction opposite = branch.opposite( junction );
            AbstractVector2D vec = new Vector2D.Double( opposite.getPosition(), junction.getPosition() );
            double curLength = vec.getMagnitude();
            double newLength = Math.abs( curLength - CircuitGraphic.junctionRadius * 1.5 );
            vec = vec.getInstanceOfMagnitude( newLength );
            Point2D desiredDst = vec.getDestination( opposite.getPosition() );
            Point2D dst = desiredDst;
            if( branch instanceof CircuitComponent ) {
                dst = junction.getPosition();
            }

            Junction newJ = new Junction( dst.getX(), dst.getY() );
            branch.replaceJunction( junction, newJ );
            addJunction( newJ );
            newJunctions[i] = newJ;

            if( branch instanceof CircuitComponent ) {
                AbstractVector2D tx = new ImmutableVector2D.Double( junction.getPosition(), desiredDst );
                Branch[] stronglyConnected = getStrongConnections( newJ );
                BranchSet bs = new BranchSet( this, stronglyConnected );
                bs.translate( tx );
            }
            else {
                updateNeighbors( newJ );
            }
        }
        remove( junction );
        fireJunctionsMoved();
        kirkhoffListener.circuitChanged();
        return newJunctions;
    }

    public void remove( Junction junction ) {
        junctions.remove( junction );
        junction.delete();
        fireJunctionRemoved( junction );
    }

    public Branch[] getStrongConnections( Junction junction ) {
        ArrayList visited = new ArrayList();
        getStrongConnections( visited, junction );
        Branch[] b = (Branch[])visited.toArray( new Branch[0] );
        return b;
    }

    public Branch[] getStrongConnections( Branch wrongDir, Junction junction ) {
        ArrayList visited = new ArrayList();
        if( wrongDir != null ) {
            visited.add( wrongDir );
        }
        getStrongConnections( visited, junction );
        if( wrongDir != null ) {
            visited.remove( wrongDir );
        }
        Branch[] b = (Branch[])visited.toArray( new Branch[0] );
        return b;
    }

    private void getStrongConnections( ArrayList visited, Junction junction ) {
        Branch[] out = getAdjacentBranches( junction );
        for( int i = 0; i < out.length; i++ ) {
            Branch branch = out[i];
            Junction opposite = branch.opposite( junction );
            if( !visited.contains( branch ) ) {
                if( branch instanceof CircuitComponent ) {
                    visited.add( branch );
                    getStrongConnections( visited, opposite );
                }//Wires end the connectivity.
            }
        }
    }

    public void fireJunctionsMoved() {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.junctionsMoved();
        }
    }

    public int numBranches() {
        return branches.size();
    }

    public int indexOf( Branch branch ) {
        return branches.indexOf( branch );
    }

    public int indexOf( Junction junction ) {
        return junctions.indexOf( junction );
    }

    public Branch branchAt( int i ) {
        return (Branch)branches.get( i );
    }

    public void remove( Branch branch ) {
        branches.remove( branch );
        branch.delete();
        fireBranchRemoved( branch );
        fireKirkhoffChanged();
    }

    private void fireBranchRemoved( Branch branch ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.branchRemoved( branch );
        }
    }

    public void fireKirkhoffChanged() {
        kirkhoffListener.circuitChanged();
    }

    public Branch[] getBranches() {
        return (Branch[])branches.toArray( new Branch[0] );
    }

    private void translate( Junction[] j, AbstractVector2D vec ) {
        for( int i = 0; i < j.length; i++ ) {
            Junction junction = j[i];
            junction.translate( vec.getX(), vec.getY() );
        }
    }

    public void translate( Branch[] branchs, AbstractVector2D vec ) {
        Junction[] j = getJunctions( branchs );
        translate( j, vec );
        for( int i = 0; i < branchs.length; i++ ) {
            Branch b = branchs[i];
            b.notifyObservers();
        }
    }

    public Junction[] getJunctions() {
        return (Junction[])junctions.toArray( new Junction[0] );
    }

    public static Junction[] getJunctions( Branch[] branchs ) {
        ArrayList list = new ArrayList();
        for( int i = 0; i < branchs.length; i++ ) {
            Branch branch = branchs[i];
            if( !list.contains( branch.getStartJunction() ) ) {
                list.add( branch.getStartJunction() );
            }
            if( !list.contains( branch.getEndJunction() ) ) {
                list.add( branch.getEndJunction() );
            }
        }
        return (Junction[])list.toArray( new Junction[0] );
    }

    public void fireBranchesMoved( Branch[] moved ) {
        for( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener)listeners.get( i );
            circuitListener.branchesMoved( moved );
        }
    }

    public boolean hasJunction( Junction junction ) {
        return junctions.contains( junction );
    }

    public double getVoltage( VoltmeterGraphic.Connection a, VoltmeterGraphic.Connection b ) {
        if( a.equals( b ) ) {
            return 0;
        }
        else {
            Junction startJ = a.getJunction();
            Junction endJ = b.getJunction();
            Branch[] ignore1 = a.getBranchesToIgnore();
            Branch[] ignore2 = b.getBranchesToIgnore();
            ArrayList ignore = new ArrayList();
            ignore.addAll( Arrays.asList( ignore1 ) );
            ignore.addAll( Arrays.asList( ignore2 ) );
            double va = a.getVoltageAddon();
            double vb = b.getVoltageAddon();
            double voltInit = ( -va + vb );

            double junctionAnswer = getVoltage( ignore, startJ, endJ, 0 );
            double junctionAnswer2 = getVoltage( ignore, endJ, startJ, 0 );
            double result = Double.POSITIVE_INFINITY;
            if( !Double.isInfinite( junctionAnswer ) ) {
                result = junctionAnswer + voltInit;
            }
            else if( !Double.isInfinite( junctionAnswer2 ) ) {
                result = junctionAnswer2 + voltInit;
            }

//            System.out.println( "ignore.length=" + ignore.size() + ", j1=" + junctionAnswer + ", j2= " + junctionAnswer2 + ", voltInit=" + voltInit + ", va=" + va + ", vb=" + vb );
//            double min = Math.max( junctionAnswer, junctionAnswer2 );
//            return junctionAnswer + voltInit;
//            return junctionAnswer;
            return result;
        }
    }

//    public double getVoltage( Branch branch1, Branch branch2 ) {
//        if( branch1 == branch2 ) {
//            return 0;
//        }
//        else {
//            double v1 = getVoltage( branch1, branch1.getStartJunction(), branch2.getStartJunction(), 0 );
//            double v2 = getVoltage( branch1, branch1.getEndJunction(), branch2.getStartJunction(), 0 );
//            double v3 = getVoltage( branch1, branch1.getStartJunction(), branch2.getEndJunction(), 0 );
//            double v4 = getVoltage( branch1, branch1.getEndJunction(), branch2.getEndJunction(), 0 );
//            ArrayList list = new ArrayList();
//            if( !Double.isInfinite( v1 ) ) {
//                list.add( new Double( v1 ) );
//            }
//            if( !Double.isInfinite( v2 ) ) {
//                list.add( new Double( v2 ) );
//            }
//            if( !Double.isInfinite( v3 ) ) {
//                list.add( new Double( v3 ) );
//            }
//            if( !Double.isInfinite( v4 ) ) {
//                list.add( new Double( v4 ) );
//            }
//            Collections.sort( list, new Comparator() {
//                public int compare( Object o1, Object o2 ) {
//                    Double a = (Double)o1;
//                    Double b = (Double)o2;
//                    double diff = ( -Math.abs( a.doubleValue() ) + Math.abs( b.doubleValue() ) );
//                    if( diff == 0 ) {
//                        return 0;
//                    }
//                    else if( diff > 0 ) {
//                        return -1;
//                    }
//                    else if( diff < 0 ) {
//                        return 1;
//                    }
////                    return diff;
//                    else {
//                        return -(int)diff;
//                    }
//                }
//            } );
//            System.out.println( "list = " + list );
//            Double lowest = (Double)list.get( 0 );
//            return lowest.doubleValue();
//        }
//    }

//    private double getVoltage( Branch branch1, Junction at, Junction target, double volts ) {
//        ArrayList visited = new ArrayList();
//        visited.add( branch1 );
//        return getVoltage( visited, at, target, volts );
//    }

    private double getVoltage( ArrayList visited, Junction at, Junction target, double volts ) {
        if( at == target ) {
            return volts;
        }
        Branch[] out = getAdjacentBranches( at );
        for( int i = 0; i < out.length; i++ ) {
            Branch branch = out[i];
            Junction opposite = branch.opposite( at );
            if( !visited.contains( branch ) ) {  //don't cross the same bridge twice.
                visited.add( branch );
                double dv = branch.getVoltageDrop();
                if( branch instanceof Battery ) {
                    Battery batt = (Battery)branch;
                    dv = batt.getEffectiveVoltageDrop();
                }
                if( branch.getEndJunction() == opposite ) {
                    dv *= -1;
                }
                return getVoltage( visited, opposite, target, volts + dv );
            }
        }
        return Double.POSITIVE_INFINITY;
    }

    public void removeCircuitListener( CircuitListener circuitListener ) {
        listeners.remove( circuitListener );
    }

    public static Circuit parseXML( IXMLElement xml, KirkhoffListener kl, CCK3Module module ) {
        Circuit cir = new Circuit( kl );
        for( int i = 0; i < xml.getChildrenCount(); i++ ) {
            IXMLElement child = xml.getChildAtIndex( i );
            int index = child.getAttribute( "index", -1 );
            if( child.getName().equals( "junction" ) ) {
                String xStr = child.getAttribute( "x", "0.0" );
                String yStr = child.getAttribute( "y", "0.0" );
                double x = Double.parseDouble( xStr );
                double y = Double.parseDouble( yStr );
                Junction j = new Junction( x, y );
                cir.addJunction( j );
            }
            else if( child.getName().equals( "branch" ) ) {
                int startIndex = child.getAttribute( "startJunction", -1 );
                int endIndex = child.getAttribute( "endJunction", -1 );
                Junction startJunction = cir.junctionAt( startIndex ); //this only works if everything stays in order.
                Junction endJunction = cir.junctionAt( endIndex );
                Branch branch = toBranch( module, kl, startJunction, endJunction, child );
                cir.addBranch( branch );
            }
        }
        return cir;
    }

    public static Branch toBranch( CCK3Module module, KirkhoffListener kl, Junction startJunction, Junction endJunction, IXMLElement xml ) {
        String type = xml.getAttribute( "type", "null" );
        if( type.equals( Branch.class.getName() ) ) {
            Branch branch = new Branch( kl, startJunction, endJunction );
            return branch;
        }
        double length = Double.parseDouble( xml.getAttribute( "length", "-1" ) );
        double height = Double.parseDouble( xml.getAttribute( "height", "-1" ) );

        if( type.equals( Resistor.class.getName() ) ) {
            Resistor res = new Resistor( kl, startJunction, endJunction, length, height );
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double val = Double.parseDouble( resVal );
            res.setResistance( val );
            return res;
        }
        else if( type.equals( Battery.class.getName() ) ) {
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double internalResistance = Double.parseDouble( resVal );
            Battery batt = new Battery( kl, startJunction, endJunction, length, height, internalResistance );
            String voltVal = xml.getAttribute( "voltage", Double.NaN + "" );
            double val = Double.parseDouble( voltVal );
            batt.setVoltageDrop( val );
            return batt;
        }
        else if( type.equals( Switch.class.getName() ) ) {
            String closedVal = xml.getAttribute( "closed", "false" );
            boolean closed = closedVal != null && closedVal.equals( new Boolean( true ).toString() );
//            boolean closed = Boolean.getBoolean( closedVal );
            Switch swit = new Switch( kl, startJunction, endJunction, closed, length, height );
            return swit;
        }
        else if( type.equals( Bulb.class.getName() ) ) {
            String widthStr = xml.getAttribute( "width", Double.NaN + "" );
            double width = Double.parseDouble( widthStr );
            boolean schematic = !module.getCircuitGraphic().isLifelike();
            Bulb bulb = new Bulb( kl, startJunction, endJunction, width, length, height, schematic );
            String resVal = xml.getAttribute( "resistance", Double.NaN + "" );
            double val = Double.parseDouble( resVal );
            bulb.setResistance( val );
            return bulb;
        }
        else if( type.equals( SeriesAmmeter.class.getName() ) ) {
            Branch amm = new SeriesAmmeter( kl, startJunction, endJunction, length, height );
            return amm;
        }
        return null;
    }

    public XMLElement toXML() {
        XMLElement xe = new XMLElement( "circuit" );
        for( int i = 0; i < numJunctions(); i++ ) {
            Junction j = junctionAt( i );
            XMLElement junctionElement = new XMLElement( "junction" );
            junctionElement.setAttribute( "index", i + "" );
            junctionElement.setAttribute( "x", j.getPosition().getX() + "" );
            junctionElement.setAttribute( "y", j.getPosition().getY() + "" );
            xe.addChild( junctionElement );
        }
        for( int i = 0; i < numBranches(); i++ ) {
            Branch branch = branchAt( i );
            XMLElement branchElement = new XMLElement( "branch" );
            Junction startJ = branch.getStartJunction();
            Junction endJ = branch.getEndJunction();
            int startIndex = indexOf( startJ );
            int endIndex = indexOf( endJ );
            branchElement.setAttribute( "index", "" + i );
            branchElement.setAttribute( "type", branch.getClass().getName() );
            branchElement.setAttribute( "startJunction", startIndex + "" );
            branchElement.setAttribute( "endJunction", endIndex + "" );
            if( branch instanceof CircuitComponent ) {
                CircuitComponent cc = (CircuitComponent)branch;
                branchElement.setAttribute( "length", cc.getLength() + "" );
                branchElement.setAttribute( "height", cc.getHeight() + "" );
            }
            if( branch instanceof Battery ) {
                branchElement.setAttribute( "voltage", branch.getVoltageDrop() + "" );
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
            }
            else if( branch instanceof Resistor ) {
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
            }
            else if( branch instanceof Bulb ) {
                branchElement.setAttribute( "resistance", branch.getResistance() + "" );
                Bulb bulb = (Bulb)branch;
                branchElement.setAttribute( "width", bulb.getWidth() + "" );
                branchElement.setAttribute( "length", branch.getStartJunction().getDistance( branch.getEndJunction() ) + "" );
                branchElement.setAttribute( "schematic", bulb.isSchematic() + "" );
            }
            else if( branch instanceof Switch ) {
                Switch sw = (Switch)branch;
                branchElement.setAttribute( "closed", sw.isClosed() + "" );
            }
            else if( branch instanceof SeriesAmmeter ) {
            }
            xe.addChild( branchElement );
        }
        try {
            new XMLWriter( System.out ).write( xe );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return xe;
    }

    public void setSelection( Branch branch ) {
        clearSelection();
        branch.setSelected( true );
    }

    public void setSelection( Junction junction ) {
        clearSelection();
        junction.setSelected( true );
    }

    public void clearSelection() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch1 = (Branch)branches.get( i );
            branch1.setSelected( false );
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction1 = (Junction)junctions.get( i );
            junction1.setSelected( false );
        }
    }

    public Branch[] getSelectedBranches() {
        ArrayList sel = new ArrayList();
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            if( branch.isSelected() ) {
                sel.add( branch );
            }
        }
        return (Branch[])sel.toArray( new Branch[0] );
    }

    public Junction[] getSelectedJunctions() {
        ArrayList sel = new ArrayList();
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction branch = (Junction)junctions.get( i );
            if( branch.isSelected() ) {
                sel.add( branch );
            }
        }
        return (Junction[])sel.toArray( new Junction[0] );
    }

    public void selectAll() {
        for( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch)branches.get( i );
            branch.setSelected( true );
        }
        for( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction)junctions.get( i );
            junction.setSelected( true );
        }
    }
}
