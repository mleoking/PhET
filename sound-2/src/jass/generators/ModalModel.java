package jass.generators;

import java.io.*;
import java.net.URL;

/** Modal model, which is loaded from an .sy format text file.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ModalModel {

    /** Mode frequencies in Hertz. */
    public float[] f;

    /**  Angular decay rates in Hertz. */
    public float[] d;

    /** Gains. a[p][k] is gain at point p for mode k. */
    public float[][] a;

    /** Number of modes available. */
    public int nf;

    /** Number of modes used. */
    public int nfUsed;

    /** Number of points. */
    public int np;

    /** Multiplies all frequencies. */
    public float fscale = 1f;

    /** Multiplies all dampings. */
    public float dscale = 1f;

    /** Multiplies all gains. */
    public float ascale = 1f;

    /** Constructor.
     @param fn File name with modal data in .sy format.
     */
    public ModalModel(String fn) throws FileNotFoundException {
        readModes(fn);
    }

    /** Constructor.
     @param url Url name with modal data in .sy format.
     */
    public ModalModel(URL url) throws IOException {
        readModes(url);
    }

    /** Constructor. Just allocates data.
     @param nf number of modes.
     @param np numpber of locations.
     */
    public ModalModel(int nf, int np) {
        this.nf = this.nfUsed = nf;
        this.np = np;
        allocate(nf, np);
    }

    /** Allocated arrays.
     @param nf number of modes.
     @param np number of locations.
     */
    protected void allocate(int nf, int np) {
        f = new float[nf];
        d = new float[nf];
        a = new float[np][nf];
    }

    private boolean isURLComment(String s) {
        if (s.charAt(0) == '<' && s.charAt(1) == '!') {
            return true;
        } else {
            return false;
        }
    }

    private void readModes(BufferedReader br) {
        try {
            float dval;
            int ival;
            String s = "<!";
            // BufferedReader may contain Netscape 7.1 generated JavaScript code due to a bug in Netscape. Get rid of it.
            // It starts with "<!". I guess "any URL string is supposed to be able to contains this kinda
            // comment"s is the idea.
            while (isURLComment(s)) {
                s = br.readLine();
            }
            // s is now: "nactive_freq:"
            s = br.readLine();
            //System.out.println(":"+s+":");
            nfUsed = Integer.parseInt(s);
            s = br.readLine(); // n_freq:
            s = br.readLine();
            nf = new Integer(s).intValue();
            s = br.readLine(); // n_points:
            s = br.readLine();
            np = new Integer(s).intValue();
            s = br.readLine(); // freq_scale:
            s = br.readLine();
            fscale = new Float(s).floatValue();
            s = br.readLine(); // d_scale:
            s = br.readLine();
            dscale = new Float(s).floatValue();
            s = br.readLine(); // a_scale:
            s = br.readLine();
            ascale = new Float(s).floatValue();
            allocate(nf, np);

            s = br.readLine(); // frequencies:
            for (int i = 0; i < nf; i++) {
                s = br.readLine();
                f[i] = new Float(s).floatValue();
            }
            s = br.readLine(); // dampings:
            for (int i = 0; i < nf; i++) {
                s = br.readLine();
                d[i] = new Float(s).floatValue();
            }
            s = br.readLine(); // amplitudes[point][freq]:
            for (int p = 0; p < np; p++) {
                for (int i = 0; i < nf; i++) {
                    s = br.readLine();
                    a[p][i] = new Float(s).floatValue();
                }
            }
            s = br.readLine(); // END
        } catch (IOException e) {
            System.out.println(e + " Error parsing sy data ");
        }
    }

    /** Read the modes file in .sy format.
     @param fn File name with modal data in .sy format.
     */
    public void readModes(String fn) throws FileNotFoundException {
        BufferedReader br = new BufferedReader(new FileReader(fn));
        readModes(br);
    }

    /** Read the modes url in .sy format.
     @param url URL name with modal data in .sy format.
     */
    public void readModes(URL url) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        readModes(br);
    }

}



