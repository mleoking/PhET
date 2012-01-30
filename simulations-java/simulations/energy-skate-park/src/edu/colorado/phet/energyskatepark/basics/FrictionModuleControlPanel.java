// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;

/**
 * Adds more controls to the friction tab
 *
 * @author Sam Reid
 */
public class FrictionModuleControlPanel extends ControlPanelNode {
    public FrictionModuleControlPanel( EnergySkateParkBasicsModule module ) {
        super( new VBox( new ViewControlPanel.ContentPane( module ),
                         new TrackControlContentPane( module ) ), EnergySkateParkLookAndFeel.backgroundColor );
    }
}
