package phet.paint.gauges;

import phet.paint.Painter;

public interface IGauge extends Painter {
    public void setMax( double max );

    public void setMin( double min );

    public void setValue( double value );

}
