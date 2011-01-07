// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.common.paint.gauges;

import edu.colorado.phet.batteryresistorcircuit.common.paint.Painter;

public interface IGauge extends Painter {
    public void setMax( double max );

    public void setMin( double min );

    public void setValue( double value );

}
