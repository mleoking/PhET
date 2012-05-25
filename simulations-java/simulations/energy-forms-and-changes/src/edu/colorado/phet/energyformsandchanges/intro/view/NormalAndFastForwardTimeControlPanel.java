// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing.UserComponents.fastForwardRadioButton;

/**
 * Clock control panel that shows "Normal Speed" and "Fast Forward" as radio
 * buttons with a play/pause and step button.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class NormalAndFastForwardTimeControlPanel extends RichPNode {

    private final Color TRANSPARENT = new Color( 0, 0, 0, 0 );
    private final PhetFont RADIO_BUTTON_FONT = new PhetFont( 16 );

    /**
     * Constructor.
     *
     * @param normalSpeed
     * @param clock
     */
    public NormalAndFastForwardTimeControlPanel( SettableProperty<Boolean> normalSpeed, IClock clock ) {
        PNode clockControlPanel = new PSwing( new PiccoloClockControlPanel( clock ) {{
            setBackground( TRANSPARENT );
            getButtonCanvas().setBackground( TRANSPARENT );
            getBackgroundNode().setVisible( false );
        }} );
        // TODO: i18n
        PNode normalSpeedButton = new PSwing( new PropertyRadioButton<Boolean>( fastForwardRadioButton, "Normal Speed", normalSpeed, true ) {{
            setBackground( TRANSPARENT );
            setFont( RADIO_BUTTON_FONT );
        }} );
        // TODO: i18n
        PNode fastForwardButton = new PSwing( new PropertyRadioButton<Boolean>( fastForwardRadioButton, "Fast Forward", normalSpeed, false ) {{
            setBackground( TRANSPARENT );
            setFont( RADIO_BUTTON_FONT );
        }} );

        addChild( new HBox( normalSpeedButton, fastForwardButton, clockControlPanel ) );
    }
}