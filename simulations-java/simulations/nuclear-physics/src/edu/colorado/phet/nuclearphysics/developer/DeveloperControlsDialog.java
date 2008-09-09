/* Copyright 2007, University of Colorado */

package edu.colorado.phet.nuclearphysics.developer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.nuclearphysics.AbstractNuclearPhysicsApplication;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsApplication;

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

    private AbstractNuclearPhysicsApplication _app;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DeveloperControlsDialog( Frame owner, AbstractNuclearPhysicsApplication app ) {
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

        Color selectedTabColor = _app.getSelectedTabColor();
        final ColorControl selectedTabColorControl = new ColorControl( parentFrame, "selected module tab color: ", selectedTabColor );
        selectedTabColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                _app.setSelectedTabColor( selectedTabColorControl.getColor() );
            }
        } );

        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( controlPanelColorControl, row++, column );
        layout.addComponent( selectedTabColorControl, row++, column );

        return panel;
    }
}
