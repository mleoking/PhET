/**
 * Class: MonitorPanel
 * Package: edu.colorado.phet.lasers.view
 * Author: Another Guy
 * Date: Oct 20, 2004
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.photon.Photon;

import javax.swing.*;

public abstract class MonitorPanel extends JPanel implements SimpleObserver {
    protected int numGroundLevel;
    protected int numMiddleLevel;
    protected int numHighLevel;

    public void photonEmitted( Atom atom, Photon photon ) {
    }
}
