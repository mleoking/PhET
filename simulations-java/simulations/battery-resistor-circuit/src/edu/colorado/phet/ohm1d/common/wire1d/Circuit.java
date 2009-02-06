package edu.colorado.phet.ohm1d.common.wire1d;

import java.util.Vector;

/**
 * Represents a single path along wires and tools for creating that path.
 */
public class Circuit {
    Vector patches = new Vector();

    public void addWirePatch( WirePatch wp ) {
        patches.add( wp );
    }

    public WirePatch getPatch( double position ) {
        double sum = 0;
        for ( int i = 0; i < patches.size(); i++ ) {
            sum += patchAt( i ).totalDistance();
            if ( position <= sum ) {
                return patchAt( i );
            }
        }
        throw new RuntimeException( "Patch not found for position=" + position + ", length=" + getLength() );
    }

    public double getLocalPosition( double global, WirePatch patch ) {
        double sum = 0;
        for ( int i = 0; i < patches.size(); i++ ) {
            if ( patchAt( i ) == patch ) {
                return global - sum;
            }
            sum += patchAt( i ).totalDistance();
        }
        throw new RuntimeException( "Patch not found." );
    }

    public double getLength() {
        double sum = 0;
        for ( int i = 0; i < patches.size(); i++ ) {
            sum += patchAt( i ).totalDistance();
        }
        return sum;
    }

    public WirePatch patchAt( int i ) {
        return (WirePatch) patches.get( i );
    }
}

