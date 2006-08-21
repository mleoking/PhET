/**
 * To be implemented by any object which has a &quot;time&quot; parameter,
 * which may be increased incrementally
 */
interface TimeDependent {
    /**
     * The implementor of this method should use it to perform one time step
     * of a model
     */
    public void advanceOnce();

    /**
     * Returns the time in the model
     */
    public double getT();
}
