package jass.generators;

import jass.engine.BufferNotAvailableException;
import jass.engine.InOut;
import jass.engine.SinkIsFullException;
import jass.engine.Source;

import java.util.Hashtable;
import java.util.Vector;

/** Vibration model of object, capable of playing sound.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ModalObject extends InOut {
    /** Sampling rate in Hertz. */
    public float srate;

    /** Modal data. */
    public ModalModel modalModel;

    /** Associate a Source with a ModalObject.Contact. */
    private Hashtable source_contact = new Hashtable();

    /** Represents contact with location in barycentric coordinates. */
    public class Contact {

        /** State of contact */
        public boolean isOn = false;

        /** Current barycentric location points. */
        public int p1 = 0,p2 = 0,p3 = 0;

        /** Current barycentric coordinates of location. */
        public float b1 = 1,b2 = 0,b3 = 0;

        /** Reson filter gain vector. */
        public float[] ampR = null;

        /** Constructor, allocates nmodes.
         @param af audio force of Contact.
         @param nf number of modes.
         */
        public Contact() {
            ampR = new float[modalModel.nf];
        }

        /** Turn on. */
        public void start() {
            isOn = true;
        }

        /** Turn off. */
        public void stop() {
            isOn = false;
        }

        /** Compute the gain coefficients  from the modal model parameters at point p,
         given inside triangle of point p1,p2,p3, with barycentric coordinated b1,b2,b3
         @param p1 location index 1.
         @param p2 location index 2.
         @param p3 location index 3.
         @param b1 barycentric coordinate 1.
         @param b2 barycentric coordinate 2.
         @param b3 barycentric coordinate 3.
         */
        public void setLocation(int p1, int p2, int p3, float b1, float b2, float b3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
            this.b1 = b1;
            this.b2 = b2;
            this.b3 = b3;
            computeLocation();
        }

        /** Compute gains of contact
         */
        public void computeLocation() {
            for (int i = 0; i < modalModel.nf; i++) {
                ampR[i] = modalModel.ascale * c_i[i] *
                        (b1 * modalModel.a[p1][i] + b2 * modalModel.a[p2][i] + b3 * modalModel.a[p3][i]);
            }
        }
    };

    /** Used a a temp buffer in inner loop to store array of gains for all contacts. */
    private float[] contactAmpTemp;

    /** Location vector of Contact objects. */
    Vector contactVector = new Vector();

    /** State of filters. */
    private float[] yt_1, yt_2;

    /** The transfer function of a reson filter is H(z) = 1/(1-twoRCosTheta/z + R2/z*z). */
    private float[] R2;

    /** The transfer function of a reson filter is H(z) = 1/(1-twoRCosTheta/z + R2/z*z). */
    private float[] twoRCosTheta;

    /** Cached values. */
    private float[] c_i;

    /** Add a Source. Implements Sink interface. Must take into account that may add
     source while running, so have to set time.
     @param s Source to add.
     */
    public synchronized Object addSource(Source s) throws SinkIsFullException {
        sourceContainer.addElement(s);
        s.setTime(getTime());
        Contact c = new Contact();
        contactVector.addElement(c);
        c.computeLocation();
        source_contact.put(s, c);
        // allocate temp storage associated with this source
        contactAmpTemp = new float[contactVector.size()];
        return c;
    }

    /** Remove a Source.  Implements Sink interface.
     @param s Source to remove.
     */
    public synchronized void removeSource(Source s) {
        sourceContainer.removeElement(s);
        //System.out.println("ncontacts="+contactVector.size());
        Contact c = (Contact) (source_contact.get(s));
        source_contact.remove(s);
        //System.out.println("removeSource("+s+"), contact="+c);
        contactVector.removeElement(c);
        //System.out.println("ncontacts="+contactVector.size());
    }


    /** Scale dampings.
     @param d damping scale.
     */
    public void setDamping(float dscale) {
        modalModel.dscale = dscale;
        computeFilter();
    }

    /** Scale frequencies.
     @param fscale frequency scale.
     */
    public void setFrequencyScale(float fscale) {
        modalModel.fscale = fscale;
        computeFilter();
    }

    /** Create and initialize, but don't set any modal parameters.
     @param srate sampling rate in Hertz.
     @param nf number of modes.
     @param np number of locations.
     @param bufferSize Buffer size used for real-time rendering.
     */
    public ModalObject(float srate, int nf, int np, int bufferSize) {
        super(bufferSize);
        this.srate = srate;
        modalModel = new ModalModel(nf, np);
        allocate(nf, np);
    }

    /** Create and initialize with provided modal data.
     @param m modal model to load.
     @param srate sampling rate in Hertz.
     @param bufferSize Buffer size used for real-time rendering.
     */
    public ModalObject(ModalModel m, float srate, int bufferSize) {
        super(bufferSize);
        this.srate = srate;
        modalModel = m;
        allocate(modalModel.nf, modalModel.np);
        computeFilter();
    }

    /** Reduce number of modes used.
     @param nf number of modes to use.
     */
    public void setNf(int nf) {
        if (nf < modalModel.nf) {
            modalModel.nfUsed = nf;
        }
    }

    /** Allocate data.
     @param nf number of modes.
     @param np number of locations.
     */
    private void allocate(int nf, int np) {
        R2 = new float[nf];
        twoRCosTheta = new float[nf];
        yt_1 = new float[nf];
        yt_2 = new float[nf];
        c_i = new float[nf];
        clearHistory();
    }

    /** Compute the filter coefficients used for real-time rendering
     from the modal model parameters.
     */
    public void computeFilter() {
        computeResonCoeff();
        for (int i = 0; i < contactVector.size(); i++) {
            ((Contact) contactVector.elementAt(i)).computeLocation();
        }
    }

    /** Compute the reson coefficients from the modal model parameters.
     Cache values for location computation.
     */
    public void computeResonCoeff() {
        for (int i = 0; i < modalModel.nf; i++) {
            float tmp_r = (float) (Math.exp(-modalModel.dscale * modalModel.d[i] / srate));
            R2[i] = tmp_r * tmp_r;
            twoRCosTheta[i] = (float) (2 * Math.cos(2 * Math.PI * modalModel.fscale * modalModel.f[i] / srate) * tmp_r);
            c_i[i] = (float) (Math.sin(2 * Math.PI * modalModel.fscale * modalModel.f[i] / srate) * tmp_r);
        }
    }

    /** Set state to non-vibrating.
     */
    public void clearHistory() {
        for (int i = 0; i < modalModel.nf; i++) {
            yt_1[i] = yt_2[i] = 0;
        }
    }

    /** Compute the next buffer and store in member float[] buf.
     */
    protected synchronized void computeBuffer() {
        try {
            computeModalFilterBank(this.buf);
        } catch (BufferNotAvailableException e) {
            System.out.println(e);
        }
    }

    /** Apply external force[] and compute response through bank of modal filters.
     @param output provided output buffer.
     */
    private void computeModalFilterBank(float[] output) throws BufferNotAvailableException {
        int bufsz = getBufferSize();
        for (int k = 0; k < bufsz; k++) {
            output[k] = 0;
        }
        int nf = modalModel.nfUsed;
        int ncontacts = contactVector.size();

        for (int i = 0; i < nf; i++) {
            float tmp_twoRCosTheta = twoRCosTheta[i];
            float tmp_R2 = R2[i];
            float tmp_yt_1 = yt_1[i];
            float tmp_yt_2 = yt_2[i];
            for (int ic = 0; ic < ncontacts; ic++) {
                // move array access out of inner loop
                contactAmpTemp[ic] = ((Contact) (contactVector.elementAt(ic))).ampR[i];
            }
            for (int k = 0; k < bufsz; k++) {
                float ynew = tmp_twoRCosTheta * tmp_yt_1 - tmp_R2 * tmp_yt_2;
                // Just this loop (empty) brings 668 (one contact) down to 544
                for (int ic = 0; ic < ncontacts; ic++) {
                    // stuff in this loop slows things a factor of 3, to 200, with everything

                    // Just this line by itself gives 266
                    Contact contact = (Contact) (contactVector.elementAt(ic));

                    // Just this stuff gives 210
                    if (contact.isOn) {
                        // BUG: This assumes scrBuffers has Sources in same order as Contacts. Probably OK but check!
                        ynew += contactAmpTemp[ic] * srcBuffers[ic][k];
                    }
                    // try just array mult and comment out the above gives 219 => arrays are very very bad!
                    //ynew += contactAmpTemp[ic] * contactAmpTemp[ic];
                    //ynew += foo[ic] * foo[ic]; // no, this makes no difference with above line!
                }

                tmp_yt_2 = tmp_yt_1;
                tmp_yt_1 = ynew;
                output[k] += ynew;
            }
            yt_1[i] = tmp_yt_1;
            yt_2[i] = tmp_yt_2;
        }
    }

}
