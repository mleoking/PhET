/* Copyright 2007, University of Colorado */

package edu.colorado.phet.naturalselection.developer;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlsDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private NaturalSelectionApplication _app;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DeveloperControlsDialog( Frame owner, NaturalSelectionApplication app ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        setModal( false );

        _app = app;

        JPanel inputPanel = createInputPanel();

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel() {

        Frame parentFrame = PhetApplication.instance().getPhetFrame();

        Color controlPanelBackground = _app.getControlPanelBackground();
        final ColorControl controlPanelColorControl = new ColorControl( parentFrame, "control panel background color: ", controlPanelBackground );
        controlPanelColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                _app.setControlPanelBackground( controlPanelColorControl.getColor() );
            }
        } );

        //XXX add more controls here, and in layout below

        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( controlPanelColorControl, row++, column );

        return panel;
    }
}
