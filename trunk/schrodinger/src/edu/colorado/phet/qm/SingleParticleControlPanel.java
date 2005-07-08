/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm;

import edu.colorado.phet.common.view.components.VerticalLayoutPanel;

/**
 * User: Sam Reid
 * Date: Jul 7, 2005
 * Time: 10:20:42 AM
 * Copyright (c) Jul 7, 2005 by Sam Reid
 */

public class SingleParticleControlPanel extends SchrodingerControlPanel {
    public SingleParticleControlPanel( SingleParticleModule singleParticleModule ) {
        super( singleParticleModule );
        VerticalLayoutPanel detectorPanel = new DetectorPanel( singleParticleModule );
        addControlFullWidth( detectorPanel );
    }
}
