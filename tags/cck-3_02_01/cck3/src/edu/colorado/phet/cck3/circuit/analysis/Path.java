package edu.colorado.phet.cck3.circuit.analysis;

import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.Circuit;
import edu.colorado.phet.cck3.circuit.CircuitChangeListener;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.Resistor;
import edu.colorado.phet.cck3.circuit.components.Switch;

import java.util.ArrayList;
import java.util.Collections;

public class Path {
    private ArrayList entries = new ArrayList();

    public Path( Path init, Branch branch, Junction last ) {
        entries.addAll( init.entries );
        PathEntry pe = new PathEntry( branch, last );
        entries.add( pe );
    }

    public Path( Branch branch, Junction junction ) {
        entries.add( new PathEntry( branch, junction ) );
    }

    public Path( PathEntry pathEntry ) {
        entries.add( pathEntry );
    }

    public void add( Branch branch, Junction last ) {
        entries.add( new PathEntry( branch, last ) );
    }

    public Junction lastJunction() {
        return lastPathEntry().getJunction();
    }

    public PathEntry lastPathEntry() {
        return (PathEntry)entries.get( entries.size() - 1 );
    }

    public boolean containsJunction( Junction junction ) {
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry pathEntry = (PathEntry)entries.get( i );
            if( pathEntry.getBranch().hasJunction( junction ) ) {
                return true;
            }
        }
        return false;
    }

    public boolean equals( Object obj ) {
        if( !( obj instanceof Path ) ) {
            return false;
        }
        Path p = (Path)obj;
        if( p.numPathEntries() != this.numPathEntries() ) {
            return false;
        }
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry pathEntry = (PathEntry)entries.get( i );
            PathEntry yours = (PathEntry)p.entries.get( i );
            if( !pathEntry.equals( yours ) ) {
                return false;
            }
        }
        return true;
    }

    public Junction getStartJunction() {
        return entryAt( 0 ).getBranch().opposite( entryAt( 0 ).getJunction() );
    }

    public String toString() {
        String str = "";
        str += getStartJunction().getLabel();
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry pe = (PathEntry)entries.get( i );
            str += " <" + pe.getBranch().getLabel() + "> " + pe.getJunction().getLabel();
        }
        return str;
    }

    public boolean containsBranch( Branch out ) {
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry pathEntry = (PathEntry)entries.get( i );
            if( pathEntry.getBranch() == out ) {
                return true;
            }
        }
        return false;
    }

    public boolean containsOpenSwitch() {
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry pathEntry = (PathEntry)entries.get( i );
            if( pathEntry.getBranch() instanceof Switch ) {
                Switch switchy = (Switch)pathEntry.getBranch();
                if( !switchy.isClosed() ) {
                    return true;
                }
            }
            if( pathEntry.getBranch() instanceof Resistor )
            {//todo workaround since Switch is getting mapped into Resistor somewhere
                Resistor resistor = (Resistor)pathEntry.getBranch();
                if( resistor.getResistance() == Switch.OPEN_RESISTANCE ) {
                    return true;
                }
            }
        }
        return false;
    }

    static class PathEntry {
        Branch branch;
        Junction endJunction;

        public PathEntry( Branch branch, Junction endJunction ) {
            this.branch = branch;
            this.endJunction = endJunction;
            if( !branch.hasJunction( endJunction ) ) {
                throw new RuntimeException( "Branch does not contain junction." );
            }
        }

        public Junction getJunction() {
            return endJunction;
        }

        public Branch getBranch() {
            return branch;
        }

        public boolean equals( Object obj ) {
            if( !( obj instanceof PathEntry ) ) {
                return false;
            }
            PathEntry pe = (PathEntry)obj;
            return branch.equals( pe.branch ) && endJunction.equals( pe.endJunction );
        }
    }

    public static Path[] getLoops( Circuit c ) {
        ArrayList all = new ArrayList();
        long time = System.currentTimeMillis();
        for( int i = 0; i < c.numBranches(); i++ ) {
            Branch b = c.branchAt( i );
            Path init = new Path( b, b.getStartJunction() );
            addLoops( init, c, all );
        }
        long finalTime = System.currentTimeMillis();
        long dt = finalTime - time;
        if( dt > 0 ) {
            System.out.println( "Loop computation time=" + dt + ", num loops=" + all.size() );
        }
        return (Path[])all.toArray( new Path[0] );
    }

    private static void addLoops( Path init, Circuit c, ArrayList all ) {
        Junction lastPathElement = init.lastJunction();
        Branch[] outs = c.getAdjacentBranches( lastPathElement );
        for( int i = 0; i < outs.length; i++ ) {
            Branch out = outs[i];
            Junction opposite = out.opposite( lastPathElement );
            if( !init.containsBranch( out ) ) {//don't cross the same branch twice.
                if( init.containsJunction( opposite ) )
                {//we found a loop, but we're only returning those that start the path.
                    if( init.getStartJunction() == opposite ) {
                        Path newPath = new Path( init, out, opposite );
                        if( isUniqueLoop( all, newPath ) ) {
                            all.add( newPath );
                        }
                    }
                }
                else {
                    addLoops( new Path( init, out, opposite ), c, all );
                }
            }
        }
    }

    private static boolean isUniqueLoop( ArrayList paths, Path newPath ) {
        for( int i = 0; i < paths.size(); i++ ) {
            Path path = (Path)paths.get( i );
            if( path.loopEquals( newPath ) ) {
                return false;
            }
        }
        return true;
    }

    private boolean loopEquals( Path path ) {
//        System.out.println( "In loopEquals, this=" + this + ", path = " + path );
        if( equals( path ) ) {
            return true;
        }
        else {
            if( path.numPathEntries() != numPathEntries() ) {
//                System.out.println( "Wrong number elements, not equal" );
                return false;
            }
            else if( loopEqualsSameDir( path ) ) {
//                System.out.println( "Equal in same direction" );
                return true;
            }
            else if( loopEqualsOppositeDir( path ) ) {
//                System.out.println( "Equal in opposite directions." );
                return true;
            }
            else {
//                System.out.println( "Else not equal" );
                return false;
            }
        }
    }

    public ArrayList getJunctionList() {
        ArrayList list = new ArrayList();
        list.add( getStartJunction() );
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry pathEntry = (PathEntry)entries.get( i );
            list.add( pathEntry.getJunction() );
        }
        return list;
    }

    public ArrayList getBranchList() {
        ArrayList list = new ArrayList();
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry pathEntry = (PathEntry)entries.get( i );
            list.add( pathEntry.getBranch() );
        }
        return list;
    }

    /*
    This addTestCircuit relies on well formed paths.
    */
    private boolean loopEqualsOppositeDir( Path path ) {
        ArrayList yourBranches = path.getBranchList();
        ArrayList myBranches = getBranchList();
        Collections.reverse( myBranches );
        int n = myBranches.size();
        for( int i = 0; i < n; i++ ) {
            myBranches.add( myBranches.get( i ) );
        }
        int myIndex = myBranches.indexOf( yourBranches.get( 0 ) );
        if( myIndex == -1 ) {
            return false;
        }
        int yourIndex = 0;
        while( yourIndex < yourBranches.size() ) {
            if( myBranches.get( myIndex ) != ( yourBranches.get( yourIndex ) ) ) {
                return false;
            }

            myIndex++;
            yourIndex++;
        }
        return true;
    }

    private boolean loopEqualsSameDir( Path path ) {
        int N = path.numPathEntries();
        int dx = indexOf( path.entryAt( 0 ) );
        if( dx == -1 ) {
            return false;
        }
        int myIndex = dx;
        int yourIndex = 0;
        while( yourIndex < N ) {
//            System.out.println( "myindex=" + myIndex + ", yourIndex = " + yourIndex );
            if( !entryAt( myIndex ).equals( path.entryAt( yourIndex ) ) ) {
                return false;
            }
            myIndex = ( myIndex + 1 ) % N;
            yourIndex++;
        }
        return true;
    }

    private int indexOf( PathEntry pathEntry ) {
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry entry = (PathEntry)entries.get( i );
            if( entry.equals( pathEntry ) ) {
                return i;
            }
        }
        return -1;
    }

    private PathEntry entryAt( int i ) {
        return (PathEntry)entries.get( i );
    }

    private int numPathEntries() {
        return entries.size();
    }

    public static void main( String[] args ) {

        Junction j0 = new Junction( 0, 0 );
        Junction j1 = new Junction( 1, 0 );
        CircuitChangeListener kl = new CircuitChangeListener() {
            public void circuitChanged() {
            }
        };
        Branch a = new Branch( kl, j0, j1 );
        Branch b = new Branch( kl, j0, j1 );

        Path p = new Path( a, j1 );
        p.add( b, j0 );

        Path p2 = new Path( b, j1 );
        p2.add( a, j0 );

        System.out.println( "p = " + p );
        System.out.println( "p2 = " + p2 );

        boolean equals = p.loopEquals( p2 );
        System.out.println( "equals = " + equals );

    }

    public DirectedBranch[] getDirectedBranches() {
        DirectedBranch[] out = new DirectedBranch[numPathEntries()];
        for( int i = 0; i < out.length; i++ ) {
            Branch branch = entryAt( i ).getBranch();
            boolean forward = entryAt( i ).getJunction() == branch.getEndJunction();
            out[i] = new DirectedBranch( branch, forward );
        }
        return out;
    }

    public boolean containsVoltageSource() {
        for( int i = 0; i < entries.size(); i++ ) {
            PathEntry pathEntry = (PathEntry)entries.get( i );
            boolean batt = pathEntry.getBranch() instanceof Battery;
            if( batt ) {
                Battery bx = (Battery)pathEntry.getBranch();
                double voltage = bx.getVoltageDrop();
                if( voltage != 0 ) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class DirectedBranch {
        Branch branch;
        boolean isForward;

        public DirectedBranch( Branch branch, boolean forward ) {
            this.branch = branch;
            isForward = forward;
        }

        public Branch getBranch() {
            return branch;
        }

        public boolean isForward() {
            return isForward;
        }

        public String toString() {
            return branch.toString() + ", forward=" + isForward;
        }
    }
}