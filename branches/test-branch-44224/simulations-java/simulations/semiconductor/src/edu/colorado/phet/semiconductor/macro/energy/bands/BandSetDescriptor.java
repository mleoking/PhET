package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 26, 2004
 * Time: 1:12:29 AM
 */
public class BandSetDescriptor {
    ArrayList bandDescriptors = new ArrayList();

    public void addBandDescriptor( BandDescriptor bd ) {
        bandDescriptors.add( bd );
    }

    public int numBands() {
        return bandDescriptors.size();
    }

    public BandDescriptor bandDescriptorAt( int i ) {
        return (BandDescriptor) bandDescriptors.get( i );
    }
}
