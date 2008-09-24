/*, 2003.*/
package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jan 18, 2004
 * Time: 1:58:25 PM
 */
public class BandSet {
    ArrayList bands = new ArrayList();

    public Rectangle2D.Double getBounds() {
        Rectangle2D r = null;
        for ( int i = 0; i < bands.size(); i++ ) {
            Band band = (Band) bands.get( i );
            Rectangle2D.Double rx = band.getRegion().toRectangle();
            if ( r == null ) {
                r = rx;
            }
            else {
                r.add( rx );
            }
        }
        return new Rectangle2D.Double( r.getX(), r.getY(), r.getWidth(), r.getHeight() );
    }

    public double getX() {
        return bandAt( 0 ).getRegion().getMinX();
    }

    public double getWidth() {
        return bandAt( 0 ).getRegion().getSpatialWidth();
    }

    public int numBands() {
        return bands.size();
    }

    public Band bandAt( int i ) {
        return (Band) bands.get( i );
    }

    public EnergyLevel levelAt( int abslevel ) {
        int rel = abslevel;
        for ( int i = 0; i < numBands(); i++ ) {
            Band b = bandAt( i );

            if ( rel < b.numEnergyLevels() ) {
                return b.energyLevelAt( rel );
            }
            else {
                rel -= b.numEnergyLevels();
            }
        }
        return null;
    }

    public int absoluteIndexOf( EnergyLevel level ) {
        for ( int i = 0; i < numEnergyLevels(); i++ ) {
            if ( levelAt( i ) == level ) {
                return i;
            }
        }
        return -1;
    }

    public int numEnergyLevels() {
        int sum = 0;
        for ( int i = 0; i < numBands(); i++ ) {
            sum += bandAt( i ).numEnergyLevels();
        }
        return sum;
    }

}
