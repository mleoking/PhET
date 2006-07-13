/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;


public class BSLauncher extends JFrame {
    
    public static void main( String args[] ) {
        SimStrings.init( args, BSConstants.LOCALIZATION_BUNDLE_BASENAME );
        BSLauncher launcher = new BSLauncher( args );
        SwingUtils.centerWindowOnScreen( launcher );
        launcher.show();
    }

    private String[] _args;
    private JRadioButton _boundStatesRadioButton;
    private JRadioButton _covalentBoundsRadioButton;
    private JRadioButton _bandStructureRadioButton;
    
    public BSLauncher( String[] args ) {
        super();
        _args = args;
        createUI();
        setResizable( false );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
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
        
        BorderLayout layout = new BorderLayout( 20, 20 );
        JPanel mainPanel = new JPanel( layout );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
        pack();
    }
    
    private JPanel createInputPanel() {
        
        JLabel instructions = new JLabel( SimStrings.get( "BSLauncher.instructions") );
        
        _boundStatesRadioButton = new JRadioButton( SimStrings.get( "BSBoundStatesApplication.title" ) );
        _covalentBoundsRadioButton = new JRadioButton( SimStrings.get( "BSCovalentBondsApplication.title" ) );
        _bandStructureRadioButton = new JRadioButton( SimStrings.get( "BSBandStructureApplication.title" ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _boundStatesRadioButton );
        buttonGroup.add( _covalentBoundsRadioButton );
        buttonGroup.add( _bandStructureRadioButton );
        _boundStatesRadioButton.setSelected( true );
        
        JPanel inputPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( instructions, row++, column );
        layout.addComponent( _boundStatesRadioButton, row++, column );
        layout.addComponent( _covalentBoundsRadioButton, row++, column );
        layout.addComponent( _bandStructureRadioButton, row++, column );
        
        return inputPanel;
    }
    
    /*
     * Creates the dialog's actions panel, consisting of a Close button.
     * 
     * @return the actions panel
     */
    protected JPanel createActionsPanel() {

        JButton startButton = new JButton( SimStrings.get( "button.start" ) );
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                run();
            }
        });
        
        JButton cancelButton = new JButton( SimStrings.get( "button.cancel" ) );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                cancel();
            }
        });

        final int rows = 1;
        final int columns = 2; /* same as number of buttons! */
        final int hgap = 5;
        final int vgap = 0;
        JPanel buttonPanel = new JPanel( new GridLayout( rows, columns, hgap, vgap ) );
        buttonPanel.add( startButton );
        buttonPanel.add( cancelButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }
    
    private void cancel() {
        dispose();
        System.exit( 0 );
    }
    
    private void run() {
        if ( _boundStatesRadioButton.isSelected() ) {
            BSBoundStatesApplication.main( _args );
        }
        else if ( _covalentBoundsRadioButton.isSelected() ) {
            BSCovalentBondsApplication.main( _args );
        }
        else if ( _bandStructureRadioButton.isSelected() ) {
            BSBandStructureApplication.main( _args );
        }
        dispose();
    }
}
