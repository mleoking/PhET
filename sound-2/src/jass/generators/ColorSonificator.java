package jass.generators;


/** Map HSB color to [pitch reson-width loudness]
 Represent a color (h,s,b) by a noise source of loudness b,
 filtered through a reson bank with Shepard frequencies
 (i.e. octaves apart covering the audible range) and some damping d = 1*freq/freq_lowest.
 The hue h [0 1] will be mapped to an octave range in freq. (Note the dampings
 are also scaled when freq. is scaled to preserve scale invariance of octaves.) The saturation
 s [0 1] will be mapped to the "material" (i.e., the width of the resonances will be
 multiplied by a factor depending on the saturation.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ColorSonificator extends ModalObjectWithOneContact {

    private float lowestFrequency = 10;     // lower limit of hearing
    private float minimumDamping = 1;       // for completely saturated color
    private float maximumDamping = 25;   // for completely unsaturated color
    // envelope unused:
    private float dBMax = 60;              // level difference between black and white
    private double fudgePower = .35; // in theory energy in mode (f,d,a) is a^2/d, so if we scale a by
    // d^.5 enery damping independent. But does not sound so because of critical band effect so fudge power


    /** Create and initialize.
     @param srate sampling rate in Hertz.
     @param bufferSize Buffer size used for real-time rendering.
     */
    public ColorSonificator(float srate, int bufferSize) {
        super(bufferSize);
        this.srate = srate;
        int np = 1; // 1 location
        // f=lowestFrequency*2^n, n = 0,...nf-1
        int nf = (int) (Math.floor(Math.log((srate / 2) / lowestFrequency) / Math.log(2)));
        this.modalModel = new ModalModel(nf, np);
        allocate(nf, np);
        this.tmpBuf = new float[bufferSize];
        createModalModel();
    }

    private final static double minDB = 1.e-10;

    /** Convert from level to decibel
     @param a level
     @return decibels = 20Log_10(a)
     */
    public static double decibel(double a) {
        double y;
        double abs_a = Math.abs(a);
        if (abs_a < minDB) {
            y = minDB;
        } else {
            y = abs_a;
        }
        return 20 * Math.log(abs_a) / Math.log(10);
    }

    /**
     Set power in scaling law for gains a = a*d^fudgePower. .5 for constant energy but lower
     in practice because of critical band effect, to have d-independent loudness
     @param p fudgePower
     */
    public void setFudgePower(float p) {
        fudgePower = (double) p;
    }

    /**
     Get power in scaling law for gains a = a*d^fudgePower. .5 for constant energy but lower
     in practice because of critical band effect, to have d-independent loudness
     @return fudgePower
     */
    public float getFudgePower() {
        return (float) fudgePower;
    }

    protected void createModalModel() {
        for (int i = 0; i < modalModel.nf; i++) {
            modalModel.d[i] = 1;
            modalModel.f[i] = lowestFrequency * (float) Math.pow(2., (double) i);
            modalModel.d[i] = modalModel.f[i] / lowestFrequency;
            modalModel.a[0][i] = 1;
            //System.out.println("f,a="+modalModel.f[i]+","+modalModel.a[0][i]);
        }
    }

    /** Set damping range corresponding to saturation
     @param dmin damping for saturated color
     @param dmax damping for unsaturated color
     */
    public void setSaturationLimits(float dmin, float dmax) {
        this.minimumDamping = dmin;
        this.maximumDamping = dmax;
    }

    /** Set level difference between white and black
     @param dbmax maximum level difference between white and black
     */
    public void setMaximumLevelDifference(float dbmax) {
        this.dBMax = dbmax;
    }

    /** Get level difference between white and black
     @return maximum level difference between white and black
     */
    public float getMaximumLevelDifference() {
        return this.dBMax;
    }

    private float[] satLimits = new float[2];

    /** Get damping range corresponding to saturation
     @return [minimumDamping maximumDamping]
     */
    public float[] getSaturationLimits() {
        satLimits[0] = minimumDamping;
        satLimits[1] = maximumDamping;
        return satLimits;
    }

    /** Set hue, saturation and brightness
     @param h hue in range 0-1
     @param s saturation in range 0-1
     @param b brightness in range 0-1
     */
    public void setHSB(float h, float s, float b) {
        float dscale = (float) (maximumDamping * Math.exp(-s * Math.log(maximumDamping / minimumDamping)));
        float fscale = (float) Math.pow(2., (double) h);
        // to keep level damping independent:

        double bcorrected = b * Math.pow((double) (dscale / maximumDamping), fudgePower);
        //float ascale = (float)(Math.exp(Math.log(10)*dBMax*(bcorrected-1)/20));
        float ascale = (float) Math.pow((double) (dscale * fscale), fudgePower);
        setDamping(fscale * dscale);
        setFrequencyScale(fscale);
        //System.out.println("setHSB a="+ascale);
        //ascale = 1;
        setGain(ascale);

    }

}
