/**
 * Created by IntelliJ IDEA.
 * User: Another Guy
 * Date: Feb 19, 2003
 * Time: 1:26:55 PM
 * To change this template use Options | File Templates.
 */
package javasound;

import srr.tone.ToneDelegator;

public class SrrOscillatorPlayer extends MyOscillatorPlayer {

    private ToneDelegator tone = new ToneDelegator( 1, 1 );
    private float freq;
    private float amp;

    public SrrOscillatorPlayer() {
    }

    public void run() {
//        super.run();
        tone.start();
    }

    public synchronized float getFrequency() {
        return freq;
    }

    public synchronized void setFrequency( float frequency ) {
        tone.setPitch( frequency );
        freq = frequency;
    }

    public synchronized float getAmplitude() {
        return amp;
    }

    public synchronized void setAmplitude( float amplitude ) {
        tone.setVolume( amplitude );
        amp = amplitude;
    }
}
