package edu.colorado.phet.graphics.gauge;

import edu.colorado.phet.graphics.Paintable;

import java.util.Observer;

public abstract class AbstractGauge implements Paintable, Observer {

    private float  min;
    private float  max;
    private float  value;
    private float  numMaj;
    private float  numMin;

    public void setMin( float  min ) {
        this.min = min;
    }

    public void setMax( float  max ) {
        this.max = max;
    }

    public void setValue( float  value ) {
        this.value = value;
    }

    public void setNumMaj( float  numMaj ) {
        this.numMaj = numMaj;
    }

    public void setNumMin( float  numMin ) {
        this.numMin = numMin;
    }
}
