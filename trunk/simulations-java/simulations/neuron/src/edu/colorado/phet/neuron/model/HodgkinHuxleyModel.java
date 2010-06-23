package edu.colorado.phet.neuron.model;


/**
 * This class is an implementation of the Hodgkin-Huxley model of neuron
 * membrane voltage.  This was originally taken from an example that was on
 * the web that was written by Anthony Fodor.  We obtained permission to use
 * the example code in our implementation (see Unfuddle #2121).
 * 
 * @author Anthony Fodor, John Blanco
 */
public class HodgkinHuxleyModel implements IHodgkinHuxleyModel
{
	//----------------------------------------------------------------------------
	// Class Data
	//----------------------------------------------------------------------------

	// Amount of time used for each iteration of the model.  This is fixed,
	// and when the model is stepped it breaks whether time step is presented
	// into units of this duration.  This is needed because below a certain
	// time value the model doesn't work - it becomes unstable.
	private static final double INTERNAL_TIME_STEP = 0.005; // In milliseconds, not seconds.
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
    
	private double elapsedTime  = 0;
	private double v;  // membrane voltage
	private double dv;
	private double cm;  // membrane Capacitance
	private double gk, gna, gl;  //constant leak permeabilties
	private double n, m, h;  // voltage-dependent gating paramaters
	private double dn, dm, dh;  //corresponding deltas
	private double an, bn, am, bm, ah, bh; // rate constants
	private double vk, vna, vl;  // Ek-Er, Ena - Er, Eleak - Er
	
	private double n4, m3h, na_current, k_current, l_current;
	
	private double timeRemainder;
	
	public float perNaChannels = 100f;
	public float perKChannels = 100f;
	
    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------

	public HodgkinHuxleyModel()
	{
		reset();
    }
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#reset()
	 */
	public void reset()
	{
		cm = 1;
		v = 0;
		vna = -115;
		vk = 12;
		vl = -10.613;
		gna = perNaChannels * 120 / 100;
		gk = perKChannels * 36 / 100;
		gl = 0.3;
		
		bh = 1 / (Math.exp((v + 30)/10) + 1) ;
		ah = 0.07 * Math.exp( v / 20);
		bm = 4 * Math.exp( v / 18);
		am = 0.1 * (v + 25) / (Math.exp( (v+25)/10  ) -1);
		bn = 0.125 * Math.exp(v/80);
		an = 0.01 * (v + 10) / (Math.exp( (v+10)/10 ) -1);

		// start these parameters in steady state
		n = an / (an + bn);
		m = am / (am + bm);
		h = ah / (ah + bh);
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#get_n4()
	 */
	public double get_n4() { return n4; }
	
	public double get_delayed_n4(double delayAmount){
		System.out.println(getClass().getName() + " Warning - Delay not implemented for this class.");
		return n4;
	}
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#get_m3h()
	 */
	public double get_m3h() { return m3h; }
	
	public double get_delayed_m3h(double delayAmount){
		System.out.println(getClass().getName() + " Warning - Delay not implemented for this class.");
		return m3h;
	}
	
	public float getEna()
	{
		return (float) (-1 * (vna + resting_v));
	}
	
	public float getEk()
	{
		return (float) (-1 * (vk + resting_v));
	}
	
	public void setEna( float Ena)
	{
		vna = -1*Ena - resting_v;
	}
	
	public void setEk( float Ek )
	{
		vk = -1*Ek - resting_v;
	}

	//The -1 is to correct for the fact that in the H & H paper, the currents are reversed.	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#get_na_current()
	 */
	public double get_na_current() { return -1 * na_current; }
	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#get_k_current()
	 */
	public double get_k_current() { return -1 * k_current; }
	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#get_l_current()
	 */
	public double get_l_current() { return -1 * l_current; }

	// negative values set to zero	
	public void setPerNaChannels( float perNaChannels )
	{
		if (perNaChannels < 0 )
		{
			perNaChannels = 0;
		}
		this.perNaChannels = perNaChannels;
		gna = 120 * perNaChannels / 100;
	}
	
	public float getPerNaChannels() 
	{
		return perNaChannels;
	}
	
	public void setPerKChannels( float perKChannels )
	{
		if (perKChannels < 0 )
		{
			perKChannels = 0;
		}
		this.perKChannels = perKChannels;
		gk = 36 * perKChannels / 100;
	}
	
	public float getPerKChannels() 
	{
		return perKChannels;
	}
	
	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#get_gk()
	 */
	public double get_gk() {
		return gk;
	}

	public void set_gk(double gk) {
		this.gk = gk;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#get_gna()
	 */
	public double get_gna() {
		return gna;
	}

	public void set_gna(double gna) {
		this.gna = gna;
	}

	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#get_gl()
	 */
	public double get_gl() {
		return gl;
	}

	public void set_gl(double gl) {
		this.gl = gl;
	}

	final private double resting_v = 65;  
	// remember that H&H voltages are -1 * present convention
	// TODO: should eventually calculate this instead of setting it
    
	// convert between internal use of V and the user's expectations
	// the V will be membrane voltage using present day conventions
	// see p. 505 of Hodgkin & Huxley, J Physiol. 1952, 117:500-544
	public void setV(double inV) { v = -1*inV - resting_v; }
	public double getV() { return -1*(v + resting_v); }
	public double getRestingV() { return -1 * resting_v; }

	public void setCm(double inCm) { cm = inCm; }
	public double getCm() {return cm; }

	/* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#getElapsedTime()
	 */
	public double getElapsedTime() { return elapsedTime; }
	public void resetElapsedTime() { elapsedTime = 0.0; } 
	
	public double getN() {return n; }
	public double getM() {return m; }
	public double getH() {return h; } 

	/**
	 * Converts a voltage from the modern convention to the convention used by the program
	 */
	public float convertV(float voltage)
	{
		return (float) (-1 * voltage - resting_v);
	}

	private boolean vClampOn = false;
	public boolean getVClampOn() { return vClampOn; }
	public void setVClampOn(boolean vClampOn) { this.vClampOn = vClampOn; }
	
	float vClampValue = convertV( 0F );
	
	float get_vClampValue() { return(float) (-1 * (vClampValue + resting_v)); }
	void set_vClampValue( float vClampValue ) { this.vClampValue = convertV( vClampValue ); }
	
	private double peak_m3h = 0;
	private double peak_n4 = 0;
    /* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#stepInTime(double)
	 */
    public void stepInTime(double dt)
    {
    	int modelIterationsToRun = (int)Math.floor((dt * 1000)/ INTERNAL_TIME_STEP);
    	timeRemainder += (dt * 1000) % INTERNAL_TIME_STEP;
    	if (timeRemainder >= INTERNAL_TIME_STEP){
    		// Add an additional iteration and reset the time remainder
    		// accumulation.  This is kind of like a leap year.
    		modelIterationsToRun += 1;
    		timeRemainder -= INTERNAL_TIME_STEP;
    	}
    	
    	// Step the model the appropriate number of times.
    	for (int i = 0; i < modelIterationsToRun; i++){
    		
    		dh = (ah * (1-h) - bh * h) * INTERNAL_TIME_STEP;
    		dm = (am * (1-m) - bm* m) * INTERNAL_TIME_STEP;
    		dn = (an * (1-n) - bn * n) * INTERNAL_TIME_STEP;
    		
    		bh = 1 / (Math.exp((v + 30)/10) + 1) ;
    		ah = 0.07 * Math.exp( v / 20);
    		dh = (ah * (1-h) - bh * h) * INTERNAL_TIME_STEP;
    		bm = 4 * Math.exp( v / 18);
    		am = 0.1 * (v + 25) / (Math.exp( (v+25)/10  ) -1);
    		bn = 0.125 * Math.exp(v/80);
    		an = 0.01 * (v + 10) / (Math.exp( (v+10)/10 ) -1);
    		dm = (am * (1-m) - bm* m) * INTERNAL_TIME_STEP;
    		dn = (an * (1-n) - bn * n) * INTERNAL_TIME_STEP;
    		
    		n4 = n*n*n*n;
    		if (n4 > peak_n4){
    			peak_n4 = n4;
    			System.out.println("peak n4 = " + peak_n4);
    		}
    		m3h = m*m*m*h;
    		if (m3h > peak_m3h){
    			peak_m3h = m3h;
    			System.out.println("peak m3h = " + peak_m3h);
    		}
    		
    		na_current = gna * m3h * (v-vna);
    		k_current = gk * n4 * (v-vk);
    		l_current = gl * (v-vl);
    		
    		dv = -1 * INTERNAL_TIME_STEP * ( k_current + na_current + l_current ) / cm;
    		
    		v += dv;
    		h += dh;
    		m += dm;
    		n += dn;
    		
    		elapsedTime+= INTERNAL_TIME_STEP;
    	}
		
		if ( vClampOn ) v = vClampValue;
    }
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#getMembraneVoltage()
	 */
    public double getMembraneVoltage(){
        // getV() converts the model's v to present day convention
        return getV() / 1000;
    }
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#getRestingMembraneVoltage()
	 */
    public double getRestingMembraneVoltage(){
        // getRestingV() converts the model's resting v to present day convention
        return getRestingV() / 1000;
    }
    
    /* (non-Javadoc)
	 * @see edu.colorado.phet.neuron.model.IMembranePotentialModel#stimulate()
	 */
    public void stimulate(){
    	// Add a fixed amount to the voltage across the membrane.
    	setV(getV() + 15);
    }
}
