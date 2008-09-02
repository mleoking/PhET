package edu.colorado.phet.idealgas.instrumentation;


public abstract class AbstractGauge {
//public abstract class AbstractGauge implements Graphic {

    private double min;
    private double max;
    double value;
    private double numMaj;
    private double numMin;

    public void setMin( double min ) {
        this.min = min;
    }

    public void setMax( double max ) {
        this.max = max;
    }

    public void setValue( double value ) {
        this.value = value;
    }

    public void setNumMaj( double numMaj ) {
        this.numMaj = numMaj;
    }

    public void setNumMin( double numMin ) {
        this.numMin = numMin;
    }
}
