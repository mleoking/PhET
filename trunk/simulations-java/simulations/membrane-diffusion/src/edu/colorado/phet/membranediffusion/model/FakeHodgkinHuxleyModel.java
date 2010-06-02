package edu.colorado.phet.membranediffusion.model;


/**
 * This class implements the interface for a Hodgkin-Huxley model that is
 * widely used in this sim, but the underlying model is not HH.  This is
 * because a model was needed that could be hooked up to drive membrane
 * channels, but that enabled individual control of the different classes
 * of gated channels (i.e. gated potassium and gated sodium channels).  This
 * class allows that control, and that is why it is called "fake".
 * 
 * @author John Blanco
 */
public class FakeHodgkinHuxleyModel implements IHodgkinHuxleyModel
{
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------

	// Max durations of the faked-out sodium and potassium channel
	// activations.
	private static final double SODIUM_CHANNEL_ACTIVATION_TIME = 0.004;    // In seconds of sim time, not wall time.
	private static final double POTASSIUM_CHANNEL_ACTIVATION_TIME = 0.004; // In seconds of sim time, not wall time.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
	private double n4, m3h;
	
	private double timeSinceSodiumChannelsActivated = Double.POSITIVE_INFINITY;
	private double timeSincePotassiumChannelsActivated = Double.POSITIVE_INFINITY;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public FakeHodgkinHuxleyModel()
	{
		reset();
    }
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#reset()
	 */
	public void reset()
	{
		timeSinceSodiumChannelsActivated = Double.POSITIVE_INFINITY;
		n4 = 0;
		timeSincePotassiumChannelsActivated = Double.POSITIVE_INFINITY;
		m3h = 0;
	}
	
	/**
	 * Force the sodium channels to be activated, but do it in isolation so
	 * that it doesn't affect any other flows, such as the potassium channels.
	 */
	public void forceActivationOfSodiumChannels(){
		timeSinceSodiumChannelsActivated = 0;
	}

	/**
	 * Force the potassium channels to be activated, but do it in isolation so
	 * that it doesn't affect any other flows, such as the sodium channels.
	 */
	public void forceActivationOfPotassiumChannels(){
		timeSincePotassiumChannelsActivated = 0;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#get_n4()
	 */
	public double get_n4() { return n4; }
	
	public double get_delayed_n4(double delayAmount){
		// Delayed values are not supported from this class, so just provide
		// the current value.
		return n4;
	}
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#get_m3h()
	 */
	public double get_m3h() { return m3h; }
	
	public double get_delayed_m3h(double delayAmount){
		// Delayed values are not supported from this class, so just provide
		// the current value.
		return m3h;
	}
	
	public float getEna()
	{
		// Stubbed in this faked out version.
		return 0;
	}
	
	public float getEk()
	{
		// Stubbed in this faked out version.
		return 0;
	}
	
	public void setEna( float Ena)
	{
		// Stubbed in this faked out version.
	}
	
	public void setEk( float Ek )
	{
		// Stubbed in this faked out version.
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#get_na_current()
	 */
	public double get_na_current() {
		// Stubbed in this faked out version.
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#get_k_current()
	 */
	public double get_k_current() {
		// Stubbed in this faked out version.
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#get_l_current()
	 */
	public double get_l_current(){
		// This is set to a permanent fixed value based on the maximum that
		// occurs in the real Hodgkin-Huxley model.  This is so that the leak
		// channels will always leak at a moderate rate when this "fake" model
		// is used.
		return -3.4;
	}

	// negative values set to zero	
	public void setPerNaChannels( float perNaChannels )
	{
		// Stubbed in this faked out version.
	}
	
	public float getPerNaChannels() 
	{
		// Stubbed in this faked out version.
		return 0;
	}
	
	public void setPerKChannels( float perKChannels )
	{
		// Stubbed in this faked out version.
	}
	
	public float getPerKChannels() 
	{
		// Stubbed in this faked out version.
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#get_gk()
	 */
	public double get_gk() {
		// Stubbed in this faked out version.
		return 0;
	}

	public void set_gk(double gk) {
		// Stubbed in this faked out version.
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#get_gna()
	 */
	public double get_gna() {
		// Stubbed in this faked out version.
		return 0;
	}

	public void set_gna(double gna) {
		// Stubbed in this faked out version.
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#get_gl()
	 */
	public double get_gl() {
		// Stubbed in this faked out version.
		return 0;
	}

	public void set_gl(double gl) {
		// Stubbed in this faked out version.
	}

	// convert between internal use of V and the user's expectations
	// the V will be membrane voltage using present day conventions
	// see p. 505 of Hodgkin & Huxley, J Physiol. 1952, 117:500-544
	public void setV(double inV) { 
		// Stubbed in this faked out version.
	}
	public double getV() { 
		// Stubbed in this faked out version.
		return 0;
	}

	public void setCm(double inCm) {
		// Stubbed in this faked out version.
	}
	
	public double getCm() {
		// Stubbed in this faked out version.
		return 0;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#getElapsedTime()
	 */
	public double getElapsedTime() {
		// Stubbed in this faked out version.
		return 0;
	}
	
	public void resetElapsedTime() {
		// Stubbed in this faked out version.
	} 
	
	public double getN() {
		// Stubbed in this faked out version.
		return 0;
	}
	public double getM() {
		// Stubbed in this faked out version.
		return 0;
	}
	public double getH() {
		// Stubbed in this faked out version.
		return 0;
	} 

	/**
	 * Converts a voltage from the modern convention to the convention used by the program
	 */
	public float convertV(float voltage)
	{
		// Stubbed in this faked out version.
		return 0;
	}

    /* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#stepInTime(double)
	 */
    public void stepInTime(double dt)
    {
    	if (timeSinceSodiumChannelsActivated < Double.POSITIVE_INFINITY){
    		timeSinceSodiumChannelsActivated += dt;
    		if (timeSinceSodiumChannelsActivated < SODIUM_CHANNEL_ACTIVATION_TIME){
    			m3h = 0.278 * Math.exp( -1 / 0.3 * Math.pow(timeSinceSodiumChannelsActivated * 1000 - 1.0, 2));
    		}
    		else{
    			// Activation is complete, reset the timer.
    			timeSinceSodiumChannelsActivated = Double.POSITIVE_INFINITY;
    			m3h = 0;
    		}
    	}
    	
    	if (timeSincePotassiumChannelsActivated < Double.POSITIVE_INFINITY){
    		timeSincePotassiumChannelsActivated += dt;
    		if (timeSincePotassiumChannelsActivated < POTASSIUM_CHANNEL_ACTIVATION_TIME){
    			n4 = 0.35 * Math.exp( -1 / 1.0 * Math.pow(timeSincePotassiumChannelsActivated * 1000 - 2.0, 2));
    		}
    		else{
    			// Activation is complete, reset the timer.
    			timeSincePotassiumChannelsActivated = Double.POSITIVE_INFINITY;
    			n4 = 0;
    		}
    	}
    }
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#getMembraneVoltage()
	 */
    public double getMembraneVoltage(){
		// Stubbed in this faked out version.
		return 0;
    }
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.membranediffusion.model.IHodgkinHuxleyModel#stimulate()
	 */
    public void stimulate(){
    	forceActivationOfSodiumChannels();
    	forceActivationOfPotassiumChannels();
    }
}
