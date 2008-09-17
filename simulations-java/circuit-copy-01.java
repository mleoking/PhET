package edu.colorado.phet.circuitconstructionkit.model;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import edu.colorado.phet.circuitconstructionkit.model.components.*;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:31:24 AM
 */
public class Circuit {
    private ArrayList branches = new ArrayList();
    private ArrayList junctions = new ArrayList();
    private ArrayList listeners = new ArrayList();
    private CircuitChangeListener circuitChangeListener;
    private boolean fireKirkhoffChanges = true;
    private boolean allowUserEdits = true;

    public Circuit() {
        this( new CompositeCircuitChangeListener() );
    }

    public Circuit( CircuitChangeListener circuitChangeListener ) {
        this.circuitChangeListener = circuitChangeListener;
    }

    public void addCircuitListener( CircuitListener listener ) {
        listeners.add( listener );
    }

    public int numCircuitListeners() {
        return listeners.size();
    }

    public void removeCircuitListener( CircuitListener listener ) {
        listeners.remove( listener );
    }

    public String toString() {
        return "Junctions=" + junctions + ", Branches=" + branches;
    }

    public void addJunction( Junction junction ) {
        if ( !junctions.contains( junction ) ) {
            junctions.add( junction );
            fireJunctionAdded( junction );
        }
        else {
            //            System.out.println( "Already contained junction." );
        }
    }

    private void fireJunctionAdded( Junction junction ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.junctionAdded( junction );
        }
    }

    public Branch[] getAdjacentBranches( Junction junction ) {
        ArrayList out = new ArrayList();
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.hasJunction( junction ) ) {
                out.add( branch );
            }
        }
        return (Branch[]) out.toArray( new Branch[0] );
    }

    public void updateNeighbors( Junction junction ) {
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.hasJunction( junction ) ) {
                branch.notifyObservers();
            }
        }
    }

    public void updateAll() {
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            branch.notifyObservers();
        }
        for ( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction) junctions.get( i );
            junction.notifyObservers();
        }
    }

    public int numJunctions() {
        return junctions.size();
    }

    public Junction junctionAt( int i ) {
        return (Junction) junctions.get( i );
    }

    public boolean hasBranch( Junction a, Junction b ) {
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.hasJunction( a ) && branch.hasJunction( b ) ) {
                return true;
            }
        }
        return false;
    }

    public Junction[] getNeighbors( Junction a ) {
        ArrayList n = new ArrayList();
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.hasJunction( a ) ) {
                n.add( branch.opposite( a ) );
            }
        }
        return (Junction[]) n.toArray( new Junction[0] );
    }

    public void replaceJunction( Junction old, Junction newJunction ) {
        junctions.remove( old );
        old.delete();
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.getStartJunction() == old ) {
                branch.setStartJunction( newJunction );
            }
            if ( branch.getEndJunction() == old ) {
                branch.setEndJunction( newJunction );
            }
        }
        fireJunctionRemoved( old );
    }

    private void fireJunctionRemoved( Junction junction ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.junctionRemoved( junction );
        }
    }

    public void addBranch( Branch component ) {
        if ( component == null ) {
            throw new RuntimeException( "Null component." );
        }

        branches.add( component );
        fireBranchAdded( component );

        addJunction( component.getStartJunction() );
        addJunction( component.getEndJunction() );
        if ( allowUserEdits ) {
            component.addObserver( editingObserver );
        }
    }

    EditingObserver editingObserver = new EditingObserver();

    public int numEditable() {
        int count = 0;
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.isEditable() ) {
                count++;
            }
        }
        return count;
    }

    public void setAllowUserEdits( boolean allowUserEdits ) {
        this.allowUserEdits = allowUserEdits;
    }

    class EditingObserver implements SimpleObserver {
        public void update() {
            notifyEditingChanged();
        }
    }

    private void notifyEditingChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.editingChanged();
        }
    }
//    public void notifyNeighbors( Branch b ) {
//        ArrayList alreadyNotified = new ArrayList();
//        Branch[] br1 = getAdjacentBranches( b.getStartJunction() );
//        Branch[] br2 = getAdjacentBranches( b.getEndJunction() );
//        ArrayList all = new ArrayList();
//        all.addAll( Arrays.asList( br1 ) );
//        all.addAll( Arrays.asList( br2 ) );
//        for( int i = 0; i < all.size(); i++ ) {
//            Branch branch = (Branch)all.get( i );
//            if( !alreadyNotified.contains( branch ) ) {
//                alreadyNotified.add( branch );
//                branch.notifyObservers();
//            }
//        }
//    }

    public Junction[] split( Junction junction ) {
        Branch[] b = getAdjacentBranches( junction );
        Junction[] newJunctions = new Junction[b.length];
        for ( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            Junction opposite = branch.opposite( junction );
            AbstractVector2D vec = new Vector2D.Double( opposite.getPosition(), junction.getPosition() );
            double curLength = vec.getMagnitude();
            double newLength = Math.abs( curLength - CCKModel.JUNCTION_RADIUS * 1.5 );
            vec = vec.getInstanceOfMagnitude( newLength );
            Point2D desiredDst = vec.getDestination( opposite.getPosition() );
            Point2D dst = desiredDst;
            if ( branch instanceof CircuitComponent ) {
                dst = junction.getPosition();
            }

            Junction newJ = new Junction( dst.getX(), dst.getY() );
            branch.replaceJunction( junction, newJ );
            addJunction( newJ );
            newJunctions[i] = newJ;

            if ( branch instanceof CircuitComponent ) {
                AbstractVector2D tx = new ImmutableVector2D.Double( junction.getPosition(), desiredDst );
                Branch[] stronglyConnected = getStrongConnections( newJ );
                BranchSet bs = new BranchSet( this, stronglyConnected );
                bs.translate( tx );
            }
            else {
                updateNeighbors( newJ );
            }
        }
        removeJunction( junction );
        fireJunctionsMoved();
        circuitChangeListener.circuitChanged();
        fireJunctionsSplit( junction, newJunctions );


        return newJunctions;
    }

    private void fireJunctionsSplit( Junction junction, Junction[] newJunctions ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.junctionsSplit( junction, newJunctions );
        }
    }

    public void removeJunction( Junction junction ) {
        junctions.remove( junction );
        junction.delete();
        fireJunctionRemoved( junction );
    }

    public Branch[] getStrongConnections( Junction junction ) {
        ArrayList visited = new ArrayList();
        getStrongConnections( visited, junction );
        return (Branch[]) visited.toArray( new Branch[0] );
    }

    public Branch[] getStrongConnections( Branch wrongDir, Junction junction ) {
        ArrayList visited = new ArrayList();
        if ( wrongDir != null ) {
            visited.add( wrongDir );
        }
        getStrongConnections( visited, junction );
        if ( wrongDir != null ) {
            visited.remove( wrongDir );
        }
        return (Branch[]) visited.toArray( new Branch[0] );
    }

    private void getStrongConnections( ArrayList visited, Junction junction ) {
        Branch[] out = getAdjacentBranches( junction );
        for ( int i = 0; i < out.length; i++ ) {
            Branch branch = out[i];
            Junction opposite = branch.opposite( junction );
            if ( !visited.contains( branch ) ) {
                if ( branch instanceof CircuitComponent ) {
                    visited.add( branch );
                    getStrongConnections( visited, opposite );
                }//Wires end the connectivity.
            }
        }
    }

    public Branch[] getConnectedSubgraph( Junction junction ) {
        ArrayList visited = new ArrayList();
        getConnectedSubgraph( visited, junction );
        return (Branch[]) visited.toArray( new Branch[0] );
    }

    private void getConnectedSubgraph( ArrayList visited, Junction junction ) {
        Branch[] adj = getAdjacentBranches( junction );
        for ( int i = 0; i < adj.length; i++ ) {
            Branch branch = adj[i];
            Junction opposite = branch.opposite( junction );
            if ( !visited.contains( branch ) ) {
                visited.add( branch );
                getConnectedSubgraph( visited, opposite );
            }
        }
    }

    public void fireJunctionsMoved() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
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
        return (Branch) branches.get( i );
    }

    public void removeBranch( Branch branch ) {
        branch.removeObserver( editingObserver );
        branches.remove( branch );
        removeNeighborJunctions( branch );
        branch.delete();
        fireBranchRemoved( branch );
        fireKirkhoffChanged();
    }

    protected void removeNeighborJunctions( Branch branch ) {
        if ( getAdjacentBranches( branch.getStartJunction() ).length == 0 ) {
            removeJunction( branch.getStartJunction() );
        }
        if ( getAdjacentBranches( branch.getEndJunction() ).length == 0 ) {
            removeJunction( branch.getEndJunction() );
        }
    }

    public void setFireKirkhoffChanges( boolean fireKirkhoffChanges ) {
        this.fireKirkhoffChanges = fireKirkhoffChanges;
    }

    private void fireBranchAdded( Branch branch ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.branchAdded( branch );
        }
    }

    private void fireBranchRemoved( Branch branch ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.branchRemoved( branch );
        }
    }

    public void fireKirkhoffChanged() {
        if ( fireKirkhoffChanges ) {
            circuitChangeListener.circuitChanged();
        }
    }

    public Branch[] getBranches() {
        return (Branch[]) branches.toArray( new Branch[0] );
    }

    private void translate( Junction[] j, AbstractVector2D vec ) {
        for ( int i = 0; i < j.length; i++ ) {
            Junction junction = j[i];
            junction.translate( vec.getX(), vec.getY() );
        }
    }

    public void translate( Branch[] branchs, AbstractVector2D vec ) {
        Junction[] j = getJunctions( branchs );
        translate( j, vec );
        for ( int i = 0; i < branchs.length; i++ ) {
            Branch b = branchs[i];
            b.notifyObservers();
        }
    }

    public Junction[] getJunctions() {
        return (Junction[]) junctions.toArray( new Junction[0] );
    }

    public static Junction[] getJunctions( Branch[] branchs ) {
        ArrayList list = new ArrayList();
        for ( int i = 0; i < branchs.length; i++ ) {
            Branch branch = branchs[i];
            if ( !list.contains( branch.getStartJunction() ) ) {
                list.add( branch.getStartJunction() );
            }
            if ( !list.contains( branch.getEndJunction() ) ) {
                list.add( branch.getEndJunction() );
            }
        }
        return (Junction[]) list.toArray( new Junction[0] );
    }

    public void fireBranchesMoved( Branch[] moved ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.branchesMoved( moved );
        }
    }

    public boolean hasJunction( Junction junction ) {
        return junctions.contains( junction );
    }

    public double getVoltage( Connection a, Connection b ) {
        VoltageCalculation vc = new VoltageCalculation( this );
        return vc.getVoltage( a, b );
    }

    public void setSelection( Branch branch ) {
        clearSelection();
        branch.setSelected( true );
        notifySelectionChanged();
    }

    public void setSelection( Junction junction ) {
        clearSelection();
        junction.setSelected( true );
        notifySelectionChanged();
    }

    private void notifySelectionChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.selectionChanged();
        }
    }

    public void clearSelection() {
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            branch.setSelected( false );
        }
        for ( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction) junctions.get( i );
            junction.setSelected( false );
        }
    }

    public Branch[] getSelectedBranches() {
        ArrayList sel = new ArrayList();
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.isSelected() ) {
                sel.add( branch );
            }
        }
        return (Branch[]) sel.toArray( new Branch[0] );
    }

    public Junction[] getSelectedJunctions() {
        ArrayList sel = new ArrayList();
        for ( int i = 0; i < junctions.size(); i++ ) {
            Junction branch = (Junction) junctions.get( i );
            if ( branch.isSelected() ) {
                sel.add( branch );
            }
        }
        return (Junction[]) sel.toArray( new Junction[0] );
    }

    public void selectAll() {
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            branch.setSelected( true );
        }
        for ( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction) junctions.get( i );
            junction.setSelected( true );
        }
    }

    public void fireJunctionsCollapsed( Junction j1, Junction j2, Junction replacement ) {
        for ( int i = 0; i < listeners.size(); i++ ) {
            CircuitListener circuitListener = (CircuitListener) listeners.get( i );
            circuitListener.junctionsConnected( j1, j2, replacement );
        }
    }

    public CircuitListener[] getCircuitListeners() {
        return (CircuitListener[]) listeners.toArray( new CircuitListener[0] );
    }

    public void moveToFirst( Junction junction ) {
        junctions.remove( junction );
        junctions.add( 0, junction );
    }

    public CircuitChangeListener getKirkhoffListener() {
        return circuitChangeListener;
    }

    public boolean isDynamic() {
        for ( int i = 0; i < numBranches(); i++ ) {
            if ( branchAt( i ) instanceof DynamicBranch ) {
                return true;
            }
        }
        return false;
    }

    public void stepInTime( double dt ) {
        for ( int i = 0; i < numBranches(); i++ ) {
            if ( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch) branchAt( i );
                b.stepInTime( dt );
            }
        }
    }

    public void resetDynamics() {
        for ( int i = 0; i < numBranches(); i++ ) {
            if ( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch) branchAt( i );
                b.resetDynamics();
            }
        }
    }

    public void setTime( double time ) {
        for ( int i = 0; i < numBranches(); i++ ) {
            if ( branchAt( i ) instanceof DynamicBranch ) {
                DynamicBranch b = (DynamicBranch) branchAt( i );
                b.setTime( time );
            }
        }
    }

    public int getInductorCount() {
        int sum = 0;
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch instanceof Inductor ) {
                sum++;
            }
        }
        return sum;
    }

    public Inductor getInductor( int index ) {
        ArrayList inductors = new ArrayList();
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch instanceof Inductor ) {
                inductors.add( branch );
            }
        }
        return (Inductor) inductors.get( index );
    }

    public int getCapacitorCount() {
        int sum = 0;
        for ( int i = 0; i < numBranches(); i++ ) {
            if ( branchAt( i ) instanceof Capacitor ) {
                sum++;
            }
        }
        return sum;
    }

    public void setState( Circuit newCircuit ) {
        clear();
        for ( int i = 0; i < newCircuit.numJunctions(); i++ ) {
            addJunction( newCircuit.junctionAt( i ) );
        }
        for ( int i = 0; i < newCircuit.numBranches(); i++ ) {
            addBranch( newCircuit.branchAt( i ) );
        }
    }

    public void clear() {
        while ( numBranches() > 0 ) {
            Branch br = branchAt( 0 );
            removeBranch( br );
        }
        while ( numJunctions() > 0 ) {
            removeJunction( junctionAt( 0 ) );
        }
    }

    public boolean wouldConnectionCauseOverlappingBranches( Junction a, Junction b ) {
        Junction[] neighborsOfA = getNeighbors( a );
        Junction[] neighborsOfB = getNeighbors( b );
        for ( int i = 0; i < neighborsOfA.length; i++ ) {
            Junction na = neighborsOfA[i];
            for ( int j = 0; j < neighborsOfB.length; j++ ) {
                Junction nb = neighborsOfB[j];
                if ( na == nb ) {
                    return true;
                }
            }
        }
        return false;
    }


    public void collapseJunctions( Junction j1, Junction j2 ) {
        if ( !j1.getPosition().equals( j2.getPosition() ) ) {
            throw new RuntimeException( "Juncitons Not at same coordinates." );
        }
        removeJunction( j1 );
        removeJunction( j2 );
        Junction replacement = new Junction( j1.getX(), j1.getY() );
        addJunction( replacement );
        replaceJunction( j1, replacement );
        replaceJunction( j2, replacement );

        fireKirkhoffChanged();
        fireJunctionsCollapsed( j1, j2, replacement );
    }

    public DragMatch getBestDragMatch( Branch[] sc, Vector2D dx ) {
        return getBestDragMatch( Circuit.getJunctions( sc ), dx );
    }

    public void removedUnusedJunctions( Junction st ) {
        Branch[] out = getAdjacentBranches( st );
        if ( out.length == 0 ) {
            removeJunction( st );
        }
    }

    public void deleteSelectedBranches() {
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.isSelected() ) {
                removeBranch( branch );
                i--;
            }
        }
    }

    public void desolderSelectedJunctions() {
        ArrayList alreadySplit = new ArrayList();
        for ( int i = 0; i < junctions.size(); i++ ) {
            Junction junction = (Junction) junctions.get( i );
            if ( !alreadySplit.contains( junction ) && junction.isSelected() && getNeighbors( junction ).length > 1 ) {
                split( junction );
                alreadySplit.add( junction );
                i = -1;
            }
        }
    }

    public void setAllComponentsEditing( boolean editing ) {
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            branch.setEditing( editing );
        }
    }

    public int getNumEditing() {
        int count = 0;
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch.isEditing() ) {
                count++;
            }
        }
        return count;
    }

    public static class DragMatch {
        private Junction source;
        private Junction target;

        public DragMatch( Junction source, Junction target ) {
            this.source = source;
            this.target = target;
        }

        public Junction getSource() {
            return source;
        }

        public Junction getTarget() {
            return target;
        }

        public double getDistance() {
            return source.getDistance( target );
        }

        public String toString() {
            return "match, source=" + source + ", dest=" + target + ", dist=" + getDistance();
        }

        public Vector2D getVector() {
            return new Vector2D.Double( source.getPosition(), target.getPosition() );
        }
    }

    public DragMatch getBestDragMatch( Junction[] draggedJunctions, Vector2D dx ) {
        Junction[] all = getJunctions();
        ArrayList potentialMatches = new ArrayList();
        potentialMatches.addAll( Arrays.asList( all ) );
        potentialMatches.removeAll( Arrays.asList( draggedJunctions ) );
        //now we have all the junctions that are moving,
        //and all the junctions that aren't moving, so we can look for a best match.
        Junction[] remaining = (Junction[]) potentialMatches.toArray( new Junction[0] );
        DragMatch best = null;
        for ( int i = 0; i < draggedJunctions.length; i++ ) {
            Junction draggedJunction = draggedJunctions[i];
            Point2D loc = dx.getDestination( draggedJunction.getPosition() );
            Junction bestForJunction = getBestDragMatch( draggedJunction, loc, remaining );
            if ( bestForJunction != null ) {
                DragMatch dm = new DragMatch( draggedJunction, bestForJunction );
                if ( best == null || dm.getDistance() < best.getDistance() ) {
                    best = dm;
                }
            }
        }
//        System.out.println( "best = " + best );
        return best;
    }

    public Junction getBestDragMatch( Junction dragging, Point2D loc, Junction[] targets ) {
        Branch[] strong = getStrongConnections( dragging );
        Junction closestJunction = null;
        double closestValue = Double.POSITIVE_INFINITY;
        for ( int i = 0; i < targets.length; i++ ) {
            Junction target = targets[i];
            double dist = loc.distance( target.getPosition() );
            if ( target != dragging && !hasBranch( dragging, target ) && !wouldConnectionCauseOverlappingBranches( dragging, target ) ) {
                if ( closestJunction == null || dist < closestValue ) {
                    boolean legal = !contains( strong, target );
                    double STICKY_THRESHOLD = 1;
                    if ( dist <= STICKY_THRESHOLD && legal ) {
                        closestValue = dist;
                        closestJunction = target;
                    }
                }
            }
        }
        return closestJunction;
    }

    private boolean contains( Branch[] strong, Junction j ) {
        for ( int i = 0; i < strong.length; i++ ) {
            Branch branch = strong[i];
            if ( branch.hasJunction( j ) ) {
                return true;
            }
        }
        return false;
    }

    public Branch getBranch( Point2D pt ) {
        return detectBranch( new Rectangle2D.Double( pt.getX(), pt.getY(), 0.001, 0.001 ) );
    }

    public Connection getConnection( Shape tipShape ) {
        Branch branch = detectBranch( tipShape );
        Junction junction = detectJunction( tipShape );
        Connection result = null;
        if ( junction != null ) {
            result = new Connection.JunctionConnection( junction );
        }
        else if ( branch != null ) {
            //could choose the closest junction
            //but we want a potentiometer.
            result = new Connection.JunctionConnection( branch.getStartJunction() );
            Point2D tipCenterModel = new Point2D.Double( tipShape.getBounds2D().getCenterX(), tipShape.getBounds2D().getCenterY() );
            Point2D.Double branchStartModel = branch.getStartJunction().getPosition();
            Vector2D vec = new Vector2D.Double( branchStartModel, tipCenterModel );
            double dist = vec.getMagnitude();
            result = new Connection.BranchConnection( branch, dist );
        }
        return result;
    }

    private Branch detectBranch( Shape tipShape ) {
        Wire[] wires = getWires();
        Wire connection = null;
        for ( int i = 0; i < wires.length; i++ ) {
            Wire wire = wires[i];
            Shape wireShape = wire.getShape();
            Area area = new Area( tipShape );
            area.intersect( new Area( wireShape ) );
            if ( !area.isEmpty() ) {
                connection = wire;
            }
        }
        return connection;
    }

    private Wire[] getWires() {
        ArrayList list = new ArrayList();
        for ( int i = 0; i < branches.size(); i++ ) {
            Branch branch = (Branch) branches.get( i );
            if ( branch instanceof Wire ) {
                list.add( branch );
            }
        }
        return (Wire[]) list.toArray( new Wire[0] );
    }

    private Junction detectJunction( Shape tipShape ) {
        Junction[] junctions = getJunctions();
        Junction detectedJunction = null;
        for ( int i = 0; i < junctions.length; i++ ) {
            Area area = new Area( junctions[i].getShape() );
            area.intersect( new Area( tipShape ) );
            if ( !area.isEmpty() ) {
                detectedJunction = junctions[i];
            }
        }
        return detectedJunction;
    }

    public void bumpAway( Junction junction ) {
        for ( int i = 0; i < 2; i++ ) {
            bumpOnce( junction );
        }
    }

    public void bumpOnce( Junction junction ) {
        Branch[] branches = getBranches();
        Branch[] strongConnections = getStrongConnections( junction );
        for ( int i = 0; i < branches.length; i++ ) {
            Branch branch = branches[i];
//            if( !branch.hasJunction( junction ) &&!contains(strongConnections,branch)) {
            if ( !branch.hasJunction( junction ) ) {
                if ( branch.getShape().intersects( junction.getShape().getBounds2D() ) ) {
                    AbstractVector2D vec = branch.getDirectionVector();
                    vec = vec.getNormalVector();
                    vec = vec.getNormalizedInstance().getScaledInstance( junction.getShape().getBounds2D().getWidth() );
                    BranchSet bs = new BranchSet( this, strongConnections );
                    bs.addJunction( junction );
                    bs.translate( vec );
                    break;
                }
            }
        }
    }

    private boolean contains( Branch[] branches, Branch branch ) {
        for ( int i = 0; i < branches.length; i++ ) {
            Branch b = branches[i];
            if ( b == branch ) {
                return true;
            }
        }
        return false;
    }
}
