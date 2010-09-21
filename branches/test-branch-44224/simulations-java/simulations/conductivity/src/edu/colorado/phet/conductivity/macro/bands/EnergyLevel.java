// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.conductivity.macro.bands;

import java.util.ArrayList;

import edu.colorado.phet.conductivity.macro.bands.states.Speed;

// Referenced classes of package edu.colorado.phet.semiconductor.macro.bands:
//            EnergyCell, BandParticle, Band

public class EnergyLevel {

    public EnergyLevel( Band band1, double d, double d1, double d2,
                        int i, double d3 ) {
        cells = new ArrayList();
        canConduct = false;
        band = band1;
        x = d;
        y = d1;
        width = d2;
        double d4 = d2 / 3D;
        EnergyCell energycell = new EnergyCell( this, d + d4, d1 + d3 );
        EnergyCell energycell1 = new EnergyCell( this, ( d + d2 ) - d4, d1 + d3 );
        cells.add( energycell );
        cells.add( energycell1 );
    }

    public int numCells() {
        return cells.size();
    }

    public String toString() {
        return "index=" + band.indexOf( this );
    }

    public java.awt.geom.Line2D.Double getLine() {
        return new java.awt.geom.Line2D.Double( x, y, x + width, y );
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public EnergyCell cellAt( int i ) {
        return (EnergyCell) cells.get( i );
    }

    public void setCanConduct( boolean flag ) {
        canConduct = flag;
    }

    public int numParticles() {
        int i = 0;
        for ( int j = 0; j < cells.size(); j++ ) {
            EnergyCell energycell = (EnergyCell) cells.get( j );
            if ( energycell.hasOwner() ) {
                i++;
            }
        }

        return i;
    }

    public int indexOf( EnergyCell energycell ) {
        return cells.indexOf( energycell );
    }

    public int indexOf( BandParticle bandparticle ) {
        for ( int i = 0; i < cells.size(); i++ ) {
            EnergyCell energycell = (EnergyCell) cells.get( i );
            if ( energycell.getOwner() == bandparticle ) {
                return i;
            }
        }

        return -1;
    }

    public Band getBand() {
        return band;
    }

    public boolean isFull() {
        return numParticles() == 2;
    }

    public boolean hasAnEmptyCell() {
        return numParticles() <= 1;
    }

    public BandParticle[] fillLevel() {
        BandParticle bandparticle = new BandParticle( 0.0D, 0.0D, cellAt( 0 ) );
        BandParticle bandparticle1 = new BandParticle( 0.0D, 0.0D, cellAt( 1 ) );
        bandparticle.setPosition( cellAt( 0 ).getPosition() );
        bandparticle1.setPosition( cellAt( 1 ).getPosition() );
        return ( new BandParticle[]{
                bandparticle, bandparticle1
        } );
    }

    public boolean canConduct() {
        return canConduct;
    }

    public void propagateBoth( Speed speed ) {
        BandParticle bandparticle = cellAt( 0 ).getOwner();
        BandParticle bandparticle1 = cellAt( 1 ).getOwner();
        bandparticle.pairPropagate( bandparticle1, speed );
    }

    private Band band;
    double x;
    double y;
    double width;
    ArrayList cells;
    private boolean canConduct;
}
