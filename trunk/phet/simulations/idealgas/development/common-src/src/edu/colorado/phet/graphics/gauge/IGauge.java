package edu.colorado.phet.graphics.gauge;

import edu.colorado.phet.graphics.PhetGraphic;

public abstract class IGauge extends PhetGraphic {

    public abstract void setMax( float  max );

    public abstract void setMin( float  min );

    public abstract void setValue( float  value );

    public abstract void setNumMaj( int num );

    public abstract void setNumMin( int num );
}
