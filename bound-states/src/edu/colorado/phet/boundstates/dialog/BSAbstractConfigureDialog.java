/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.boundstates.model.BSAbstractPotential;
import edu.colorado.phet.common.view.util.SimStrings;


public abstract class BSAbstractConfigureDialog extends JDialog implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    protected static final Insets SLIDER_INSETS = new Insets( 0, 0, 0, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSAbstractPotential _potential;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public BSAbstractConfigureDialog( Frame frame, String title, BSAbstractPotential potential ) {
        super( frame, title );
        setModal( false );
        setResizable( false );
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent event ) {
                dispose();
            } 
        } );
        _potential = potential;
        _potential.addObserver( this );
        createUI();
    }
    
    //----------------------------------------------------------------------------
    // UI initializers
    //----------------------------------------------------------------------------
    
    /*
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI() {
        
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( actionsPanel, BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
        pack();
    }
    
    /*
     * Creates the dialog's input panel.
     */
    protected abstract JPanel createInputPanel();
    
    /*
     * Creates the dialog's actions panel, consisting of a Close button.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        JButton closeButton = new JButton( SimStrings.get( "button.close" ) );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 1 ) );
        buttonPanel.add( closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }
    
    //----------------------------------------------------------------------------
    // JDialog overrides
    //----------------------------------------------------------------------------
    
    public void dispose() {
        if ( _potential != null ) {
            _potential.deleteObserver( this );
            _potential = null;
        }
        super.dispose();
    }
}
