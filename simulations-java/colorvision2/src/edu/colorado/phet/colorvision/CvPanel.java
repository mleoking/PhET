/**
 * Class: CvPanel
 * Package: edu.colorado.phet.colorvision
 * Author: Another Guy
 * Date: Feb 24, 2004
 */
package edu.colorado.phet.colorvision;

import javax.swing.*;
import java.util.ArrayList;
import java.awt.*;

public class CvPanel extends JPanel {
    ArrayList photonBeams = new ArrayList();

    public CvPanel() {
        RepaintClock clock = new RepaintClock( 10 );
        Thread thread = new Thread( clock );
        thread.start();
    }

    public void addPhotonBeam( PhotonBeam pb ) {
        photonBeams.add( pb );
    }

    protected void paintComponent( Graphics g ) {
        super.paintComponent( g );
        for( int i = 0; i < photonBeams.size(); i++ ) {
            PhotonBeam photonBeam = (PhotonBeam)photonBeams.get( i );
            photonBeam.paint( g );
        }
    }

    private class RepaintClock implements Runnable {
        private boolean running;
        private long tickTime;

        public RepaintClock( long tickTime ) {
            this.tickTime = tickTime;
        }

        public void run() {
            running = true;
            while( running ) {
                CvPanel.this.repaint();
                try {
                    Thread.sleep( tickTime );
                }
                catch( InterruptedException e ) {
                    e.printStackTrace();
                }
            }

        }
    }
}
