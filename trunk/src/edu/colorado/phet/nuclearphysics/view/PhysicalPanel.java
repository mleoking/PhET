/**
 * Class: PhysicalPanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 9:11:05 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.ApparatusPanel;

import java.awt.*;

public class PhysicalPanel extends ApparatusPanel {

    public PhysicalPanel() {
        this.setBackground( backgroundColor );
    }

    //
    // Statics
    //
    private static Color backgroundColor = new Color( 255, 255, 230 );
}
