// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.basics;

import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.energyskatepark.view.EnergySkateParkLookAndFeel;

/**
 * @author Sam Reid
 */
public class IntroControlPanel extends ControlPanelNode {
    public IntroControlPanel( EnergySkateParkBasicsModule module ) {
        super( new VBox( new ViewControlPanel.ContentPane( module ),
                         new MassControl( module ) ), EnergySkateParkLookAndFeel.backgroundColor );
    }
}
