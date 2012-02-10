// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.view;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas.CONTROL_FONT;

/**
 * Shows options for whether or not to show the improper or mixed fraction equivalent to the main fraction.
 *
 * @author Sam Reid
 */
public class EqualityDisplayOptionsControlPanel extends ControlPanelNode {
    public EqualityDisplayOptionsControlPanel( Property<Boolean> reducedShowing, Property<Boolean> mixedShowing ) {
        super( new VBox( new PSwing( new PropertyCheckBox( "Reduced", reducedShowing ) {{setFont( CONTROL_FONT );}} ),
                         new PSwing( new PropertyCheckBox( "Mixed", mixedShowing ) {{setFont( CONTROL_FONT );}} ) ) );
    }
}