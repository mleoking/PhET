package edu.colorado.phet.common.math;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 8:13:02 PM
 * Copyright (c) Dec 12, 2004 by Sam Reid
 */
public interface Function {
    public double evaluate( double x );

    public Function createInverse();

    public static class IdentityFunction implements Function {
        public double evaluate( double x ) {
            return x;
        }

        public Function createInverse() {
            return this;
        }
    }

    public static class PowerFunction implements Function {
        private double power;

        public PowerFunction( double power ) {
            this.power = power;
        }

        public double evaluate( double x ) {
            return Math.pow( x, power );
        }

        public Function createInverse() {
            throw new RuntimeException( "Not yet implemented" );
        }
    }

    public static class LinearFunction implements Function {
        private double xMin;
        private double xMax;
        private double yMin;
        private double yMax;

        private double t1;
        private double scale;
        private double t2;

        public LinearFunction( double xMin, double xMax, double yMin, double yMax ) {
            this.xMin = xMin;
            this.xMax = xMax;
            this.yMin = yMin;
            this.yMax = yMax;
            update();
        }

        protected void update() {
            t1 = ( -xMin );
            scale = ( yMax - yMin ) / ( xMax - xMin );
            t2 = yMin;
        }

        public double evaluate( double x ) {
            double output = t1 + x;
            output = scale * output;
            output = t2 + output;
            return output;
        }

        public Function createInverse() {
            return new LinearFunction( yMin, yMax, xMin, xMax );
        }

        public double getMinInput() {
            return xMin;
        }

        public double getMaxInput() {
            return xMax;
        }

        public double getMinOutput() {
            return yMin;
        }

        public double getMaxOutput() {
            return yMax;
        }

        public double getInputRange() {
            return xMax - xMin;
        }

        public double getOutputRange() {
            return yMax - yMin;
        }
    }
}
