/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.developer;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.application.ModuleEvent;
import edu.colorado.phet.common.phetcommon.application.ModuleObserver;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.ColorControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.statesofmatter.AbstractStatesOfMatterApp;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author John Blanco
 */
public class AtomicInteractionsDeveloperControlsDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------

    private AbstractStatesOfMatterApp m_app;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public AtomicInteractionsDeveloperControlsDialog( Frame owner, AbstractStatesOfMatterApp app ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        setModal( false );

        m_app = app;
        
        // Register with the application for module change events.
        m_app.addModuleObserver( new ModuleObserver(){
            public void moduleAdded( ModuleEvent event ) {
            }

            public void activeModuleChanged( ModuleEvent event ) {
                // Since these developer controls are specific to the selected
                // module, the controls should disappear if the module changes.
                AtomicInteractionsDeveloperControlsDialog.this.dispose();
            }

            public void moduleRemoved( ModuleEvent event ) {
            }

        });
        
        // Create and add the input panel.
        JPanel inputPanel = createInputPanel();

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel() {

        Frame parentFrame = PhetApplication.getInstance().getPhetFrame();

        Color controlPanelBackground = m_app.getControlPanelBackground();
        final ColorControl controlPanelColorControl = new ColorControl( parentFrame, "control panel background color: ", controlPanelBackground );
        controlPanelColorControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                m_app.setControlPanelBackground( controlPanelColorControl.getColor() );
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

        return panel;
    }    
}
