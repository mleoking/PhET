/**
 * This interface should be implemented by all classes that can behave as
 * a function.  A function may be plotted directly using the {@link
 * SolvFunction SolvFunction} class.
 */
interface Function {
    public double evaluate( double x );
}