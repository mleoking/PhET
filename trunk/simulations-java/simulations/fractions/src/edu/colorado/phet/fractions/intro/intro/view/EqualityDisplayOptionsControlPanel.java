// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Shows options for whether or not to show the improper or mixed fraction equivalent to the main fraction.
 *
 * @author Sam Reid
 */
public class EqualityDisplayOptionsControlPanel extends ControlPanelNode {
    public EqualityDisplayOptionsControlPanel( SettableProperty<Visualization> visualization ) {
        super( new VBox( new PSwing( new PropertyRadioButton<Visualization>( "None", visualization, Visualization.NONE ) {{setFont( FractionsIntroCanvas.CONTROL_FONT );}} ),
                         new PSwing( new PropertyRadioButton<Visualization>( "Reduced", visualization, Visualization.FRACTION ) {{setFont( FractionsIntroCanvas.CONTROL_FONT );}} ),
                         new PSwing( new PropertyRadioButton<Visualization>( "Mixed", visualization, Visualization.MIXED ) {{setFont( FractionsIntroCanvas.CONTROL_FONT );}} ) ) );
    }

}
