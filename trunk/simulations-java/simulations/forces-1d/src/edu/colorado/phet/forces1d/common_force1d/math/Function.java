package edu.colorado.phet.forces1d.common_force1d.math;

/**
 * User: Sam Reid
 * Date: Dec 12, 2004
 * Time: 8:13:02 PM
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
        private double minInput;
        private double maxInput;
        private double minOutput;
        private double maxOutput;

        private double t1;
        private double scale;
        private double t2;

        public LinearFunction( double minInput, double maxInput, double minOutput, double maxOutput ) {
            this.minInput = minInput;
            this.maxInput = maxInput;
            this.minOutput = minOutput;
            this.maxOutput = maxOutput;
            update();
        }

        protected void update() {
            t1 = ( -minInput );
            scale = ( maxOutput - minOutput ) / ( maxInput - minInput );
            t2 = minOutput;
        }

        public double evaluate( double x ) {
            double output = t1 + x;
            output = scale * output;
            output = t2 + output;
            return output;
        }

        public Function createInverse() {
            return new LinearFunction( minOutput, maxOutput, minInput, maxInput );
        }

        public double getMinInput() {
            return minInput;
        }

        public double getMaxInput() {
            return maxInput;
        }

        public double getMinOutput() {
            return minOutput;
        }

        public double getMaxOutput() {
            return maxOutput;
        }

        public double getInputRange() {
            return maxInput - minInput;
        }

        public double getOutputRange() {
            return maxOutput - minOutput;
        }

        public void setInput( double minInput, double maxInput ) {
            this.minInput = minInput;
            this.maxInput = maxInput;
            update();
        }

        public void setOutput( double minOutput, double maxOutput ) {
            this.minOutput = minOutput;
            this.maxOutput = maxOutput;
            update();
        }

        public String toString() {
            return "Linear Function, [" + minInput + "," + maxInput + "]->[" + minOutput + "," + maxOutput + "]";
        }
    }
}
