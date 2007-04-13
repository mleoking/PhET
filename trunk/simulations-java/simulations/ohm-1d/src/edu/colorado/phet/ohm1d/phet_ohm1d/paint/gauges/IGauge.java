package edu.colorado.phet.ohm1d.phet_ohm1d.paint.gauges;

import edu.colorado.phet.ohm1d.phet_ohm1d.paint.Painter;

public interface IGauge extends Painter {
    public void setMax( double max );

    public void setMin( double min );

    public void setValue( double value );

}
