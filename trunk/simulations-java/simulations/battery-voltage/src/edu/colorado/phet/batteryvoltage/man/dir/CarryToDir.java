package edu.colorado.phet.batteryvoltage.man.dir;

import edu.colorado.phet.batteryvoltage.man.Directional;
import edu.colorado.phet.batteryvoltage.man.ReadyToDrop;

public class CarryToDir implements Directional {
    double xMin;
    double xMax;
    ReadyToDrop rtd;

    public CarryToDir( double xMin, double xMax, boolean goToMin, ReadyToDrop rtd ) {
        this.rtd = rtd;
        this.xMin = xMin;
        this.xMax = xMax;
    }

    public void setCarryRight( boolean b ) {
        if ( b ) {
            rtd.setState( xMax, true );
        }
        else {
            rtd.setState( xMin, false );
        }
    }
}







