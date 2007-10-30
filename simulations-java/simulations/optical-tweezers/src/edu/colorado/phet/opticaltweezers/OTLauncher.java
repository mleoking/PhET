/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * OTLauncher allows you to choose which simulation flavor to run 
 * in the Optical Tweezers "family". Since all of the sims share
 * the same JAR file, you should put this line in your JAR manifest:
 * <code>
 * Main-Class: edu.colorado.phet.opticaltweezers.OTLauncher
 * </code> 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class OTLauncher extends JFrame {
    
    public static void main( String args[] ) {
        OTLauncher launcher = new OTLauncher( args );
        SwingUtils.centerWindowOnScreen( launcher );
        launcher.show();
    }

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private String[] _args;
    private JRadioButton _opticalTweezersRadioButton;
    private JRadioButton _stretchingDNARadioButton;
    private JRadioButton _molecularMotorsRadioButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param args command line arguments
     */
    public OTLauncher( String[] args ) {
        super();
        _args = args;
        createUI();
        setResizable( false );
        setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
    }
    
    //----------------------------------------------------------------------------
    // User interface construction
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
        
        BorderLayout layout = new BorderLayout( 20, 20 );
        JPanel mainPanel = new JPanel( layout );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
        pack();
    }
    
    /*
     * Creates dialog's input panel, which contains user controls.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        JLabel instructions = new JLabel( OTResources.getString( "OTLauncher.instructions") );
        
        _opticalTweezersRadioButton = new JRadioButton( OTResources.getString( OTConstants.FLAVOR_OPTICAL_TWEEZERS + ".name" ) );
        _stretchingDNARadioButton = new JRadioButton( OTResources.getString( OTConstants.FLAVOR_STRETCHING_DNA + ".name" ) );
        _molecularMotorsRadioButton = new JRadioButton( OTResources.getString( OTConstants.FLAVOR_MOLECULAR_MOTORS + ".name" ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _opticalTweezersRadioButton );
        buttonGroup.add( _stretchingDNARadioButton );
        buttonGroup.add( _molecularMotorsRadioButton );
        _opticalTweezersRadioButton.setSelected( true );
        
        JPanel inputPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( instructions, row++, column );
        layout.addComponent( _opticalTweezersRadioButton, row++, column );
        layout.addComponent( _stretchingDNARadioButton, row++, column );
        layout.addComponent( _molecularMotorsRadioButton, row++, column );
        
        return inputPanel;
    }
    
    /*
     * Creates the dialog's actions panel, consisting of a Close button.
     * 
     * @return the actions panel
     */
    protected JPanel createActionsPanel() {

        JButton startButton = new JButton( OTResources.getString( "button.start" ) );
        startButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleStart();
            }
        });
        
        JButton cancelButton = new JButton( OTResources.getString( "button.cancel" ) );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleCancel();
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
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    /*
     * Handles the "Cancel" button.
     * Closes the dialog and exits.
     */
    private void handleCancel() {
        dispose();
        System.exit( 0 );
    }
    
    /*
     * Handles the "Start" button.
     * Runs the selected simulation.
     */
    private void handleStart() {
        if ( _opticalTweezersRadioButton.isSelected() ) {
            OpticalTweezersApplication.main( _args );
        }
        else if ( _stretchingDNARadioButton.isSelected() ) {
            StretchingDNAApplication.main( _args );
        }
        else if ( _molecularMotorsRadioButton.isSelected() ) {
            MolecularMotorsApplication.main( _args );
        }
        dispose();
    }
}
