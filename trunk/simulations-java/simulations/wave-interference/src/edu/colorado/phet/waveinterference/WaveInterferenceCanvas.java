/*  */
package edu.colorado.phet.waveinterference;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.BufferedPhetPCanvas;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:12:40 PM
 */

public abstract class WaveInterferenceCanvas extends BufferedPhetPCanvas implements Maximizable {
    private boolean waveMaximized = false;

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );//todo is this to avoid performance problems in 1.5?
        super.paintComponent( g );
    }

    public Point2D getWaveModelGraphicOffset() {
        return new Point2D.Double( 200, 30 );//has to keep the slits screen & buttons onscreen.
    }

    public int getLayoutHeight() {
        return super.getHeight();
    }

    public void setWaveMaximized( boolean waveMaximized ) {
        this.waveMaximized = waveMaximized;
        updateWaveSize();
    }

    protected abstract void updateWaveSize();

    public boolean isWaveMaximized() {
        return waveMaximized;
    }
}
