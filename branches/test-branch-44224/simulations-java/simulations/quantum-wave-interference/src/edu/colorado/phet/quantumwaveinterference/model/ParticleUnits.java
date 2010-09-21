/*  */
package edu.colorado.phet.quantumwaveinterference.model;

import edu.colorado.phet.quantumwaveinterference.QWIResources;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * User: Sam Reid
 * Date: Jan 5, 2006
 * Time: 2:33:36 PM
 */

public class ParticleUnits {
    private Value hbar;
    private Value mass;
    private Value dx;
    private Value dt;
    private Value minVelocity;
    private Value maxVelocity;
    private int numRulerReadings;
    private double rulerScale;
    private DecimalFormat rulerFormat;
    private double rulerWidth;
    private double latticeWidth;
    private double timeScaleFactor = 1.0;

    protected ParticleUnits() {
    }

    public ParticleUnits( Value hbar, Value mass, Value dx, Value dt, Value minVelocity, Value maxVelocity,
                          int numRulerReadings, double rulerScale, DecimalFormat rulerFormat ) {
        this.hbar = hbar;
        this.mass = mass;
        this.dx = dx;
        this.dt = dt;
        this.minVelocity = minVelocity;
        this.maxVelocity = maxVelocity;
    }

    public String toString() {
        return "hbar=" + hbar + ", mass=" + mass + ", dx=" + dx + ", dt=" + dt + ", minVelocity=" + minVelocity + ", maxVelocity=" + maxVelocity;
    }

    public Value getHbar() {
        return hbar;
    }

    public void setHbar( Value hbar ) {
        this.hbar = hbar;
    }

    public Value getMass() {
        return mass;
    }

    public void setMass( Value mass ) {
        this.mass = mass;
    }

    public Value getDx() {
        return dx;
    }

    public void setDx( Value dx ) {
        this.dx = dx;
    }

    public Value getDt() {
        return dt;
    }

    public void setDt( Value dt ) {
        this.dt = dt;
    }

    public Value getMinVelocity() {
        return minVelocity;
    }

    public double getAverageVelocity() {
        return ( minVelocity.getValue() + maxVelocity.getValue() ) / 2.0;
    }

    public void setMinVelocity( Value minVelocity ) {
        this.minVelocity = minVelocity;
    }

    public Value getMaxVelocity() {
        return maxVelocity;
    }

    public void setMaxVelocity( Value maxVelocity ) {
        this.maxVelocity = maxVelocity;
    }

    private static double scaleDownMaxVel = 0.5;
    //    private static double scaleMinVel = 0.5;
    //    private static double scaleMinVel = 1.0;
    private static double scaleMinVel = 0.5;

    public int getNumRulerReadings() {
        return numRulerReadings;
    }

    public double getRulerScale() {
        return rulerScale;
    }

    public DecimalFormat getRulerFormat() {
        return rulerFormat;
    }

    public void setNumRulerReadings( int numRulerReadings ) {
        this.numRulerReadings = numRulerReadings;
    }

    public void setRulerFormat( DecimalFormat rulerFormat ) {
        this.rulerFormat = rulerFormat;
    }

    public double getRulerWidth() {
        return rulerWidth;
    }

    public double getLatticeWidth() {
        return latticeWidth;
    }

    protected void setRulerWidth( double v ) {
        this.rulerWidth = v;
    }

    protected void setLatticeWidth( double v ) {
        this.latticeWidth = v;
    }

    public void setTimeScaleFactor( double timeScaleFactor ) {
        this.timeScaleFactor = timeScaleFactor;
    }

    protected void setVelocityFormat( DecimalFormat defaultFormat ) {
        getMinVelocity().setDefaultFormat( defaultFormat );
        getMaxVelocity().setDefaultFormat( defaultFormat );
    }

    public double getTimeScaleFactor() {
        return timeScaleFactor;
    }

    public static class PhotonUnits extends ParticleUnits {
        public PhotonUnits() {
            setDx( new Value( 1.0, 0.06 * 20000, QWIResources.getString( "nm" ) ) );
            setDt( new Value( 1, 0.1, QWIResources.getString( "fs" ) ) );

            setLatticeWidth( 2700 );
            setRulerWidth( 3000 );

            setRulerFormat( new DecimalFormat( "0" ) );
            setNumRulerReadings( 4 );
        }
    }

    protected void setupLatticeAndRuler() {
        double scale = 10;
        setLatticeWidth( 0.45 * scale );
        setRulerWidth( 0.50 * scale );
        setRulerFormat( new DecimalFormat( "0.0" ) );
        setNumRulerReadings( 6 );
    }

    public static class ElectronUnits extends ParticleUnits {
        public ElectronUnits() {
            setHbar( new Value( 0.658, 1, QWIResources.getString( "ev.fs" ) ) );
            setMass( new Value( 0.057, 100, QWIResources.getString( "ev.fs.2.nm.2" ) ) );
            setDx( new Value( 1.0, 0.1, QWIResources.getString( "nm" ) ) );
            setDt( new Value( 0.05, 0.10, QWIResources.getString( "fs" ) ) );

            double s = 100.0;
            setMinVelocity( new Value( 700 / s, s, QWIResources.getString( "gun.km-s" ) ) );
            setMaxVelocity( new Value( 1500 / s, s, QWIResources.getString( "gun.km-s" ) ) );

            DecimalFormat defaultFormat = new DecimalFormat( "0" );
            setVelocityFormat( defaultFormat );

            super.setupLatticeAndRuler();
            setTimeScaleFactor( 10.0 );
        }
    }

    public static class NeutronUnits extends ParticleUnits {
        public NeutronUnits() {
            setHbar( new Value( 0.000658, 1, QWIResources.getString( "ev.ps" ) ) );
            setDx( new Value( 1.0, 0.1, QWIResources.getString( "nm" ) ) );
            setDt( new Value( 0.1, 0.1, QWIResources.getString( "ps" ) ) );
            setMass( new Value( 0.000104539, 1.0 / 10000.0, QWIResources.getString( "ev.fs.2.nm.2" ) ) );

            double s = 0.1;
            setMinVelocity( new Value( 0.4 / s, s, QWIResources.getString( "gun.km-s" ) ) );
            setMaxVelocity( new Value( 0.8 / s, s, QWIResources.getString( "gun.km-s" ) ) );

            DecimalFormat defaultFormat = new DecimalFormat( "0.0" );
            setVelocityFormat( defaultFormat );
            setupLatticeAndRuler();
//            setTimeScaleFactor( 10.0 );
            setTimeScaleFactor( 10.0 );
        }
    }

    public static class HeliumUnits extends ParticleUnits {
        public HeliumUnits() {
            setHbar( new Value( 0.000658, 1, QWIResources.getString( "ev.ps" ) ) );
            setDx( new Value( 1.0, 0.1, QWIResources.getString( "nm" ) ) );
            setDt( new Value( 0.5, 0.1, QWIResources.getString( "ps" ) ) );
            setMass( new Value( 0.000414741, 1.0 / 10000.0, QWIResources.getString( "ev.fs.2.nm.2" ) ) );

            double s = 0.1;
            setMinVelocity( new Value( 0.1 / s, s, QWIResources.getString( "gun.km-s" ) ) );
            setMaxVelocity( new Value( 0.2 / s, s, QWIResources.getString( "gun.km-s" ) ) );

            DecimalFormat defaultFormat = new DecimalFormat( "0.0" );
            setVelocityFormat( defaultFormat );
            setupLatticeAndRuler();
            setTimeScaleFactor( 10.0 );
        }
    }

    public static class Value {
        double value;
        double displayScaleFactor;
        String units;
        private NumberFormat defaultFormat;

        public Value( double modelValue, double displayScaleFactor, String units ) {
            this.value = modelValue;
            this.displayScaleFactor = displayScaleFactor;
            this.units = units;
        }

        public void setDefaultFormat( NumberFormat defaultFormat ) {
            this.defaultFormat = defaultFormat;
        }

        public double getValue() {
            return value;
        }

        public double getDisplayValue() {
            return value * displayScaleFactor;
        }

        public double getDisplayScaleFactor() {
            return displayScaleFactor;
        }

        public String getUnits() {
            return units;
        }

        public String toString() {
            return "" + getDisplayValue() + " " + units + " [@" + value + "]";
        }

        public String toPrettyString( NumberFormat numberFormat ) {
            return numberFormat.format( getDisplayValue() ) + " " + units;
        }

        public String toPrettyString() {
            return toPrettyString( defaultFormat );
        }
    }
}

