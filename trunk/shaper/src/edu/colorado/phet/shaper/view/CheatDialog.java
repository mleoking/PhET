/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.shaper.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.shaper.model.FourierSeries;


/**
 * CheatDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class CheatDialog extends JDialog implements SimpleObserver {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private DecimalFormat CHEAT_FORMAT = new DecimalFormat( "0.00" );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JButton _closeButton;
    private FourierSeries _outputFourierSeries;
    private ArrayList _values; // array of JLabel
    
    /**
     * Sole constructor.
     * 
     * @param parent
     * @param outputFourierSeries
     */
    public CheatDialog( Frame parent, FourierSeries outputFourierSeries ) {
        super( parent );
        
        _outputFourierSeries = outputFourierSeries;
        _outputFourierSeries.addObserver( this );
        
        super.setTitle( SimStrings.get( "CheatDialog.title" ) );
        super.setModal( false );
        super.setResizable( false );
        
        createUI( parent );
        
        addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                cleanup();
            }
        } );
        
        update();
    }
    
    private void cleanup() {
        _outputFourierSeries.removeObserver( this );
    }
    
    /**
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI( Frame parent ) {
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( inputPanel, BorderLayout.CENTER );
        panel.add( actionsPanel, BorderLayout.SOUTH );

        this.getContentPane().add( panel );
        this.pack();
        this.setLocationRelativeTo( parent );
    }
    
    /**
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        JPanel panel = new JPanel();
        
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.EAST );
        layout.setMinimumWidth( 0, 30 );
        int row = 0;
        
        JLabel heading = new JLabel( SimStrings.get( "CheatDialog.label" ) );
        layout.addComponent( heading, row, 0, 4, 1 );
        row++;
        row++;
        
        _values = new ArrayList();
        int numberOfHarmonics = _outputFourierSeries.getNumberOfHarmonics();
        for ( int i = 0; i < numberOfHarmonics; i++ ) {
            double dAmplitude = _outputFourierSeries.getHarmonic( i ).getAmplitude();
            String sAmplitude = CHEAT_FORMAT.format( dAmplitude );

            JLabel label = new JLabel( "<html>A<sub>" + ( i + 1 ) + "</sub> = </html>" );
            JLabel value = new JLabel( sAmplitude );
            layout.addComponent( label, row, 1 );
            layout.addComponent( value, row, 2 );
            row++;

            _values.add( value );
        }

        return panel;
    }
    
    /** 
     * Creates the dialog's actions panel.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _closeButton = new JButton( SimStrings.get( "CheatDialog.close" ) );
        _closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
                cleanup();
            }
        });

        JPanel innerPanel = new JPanel( new GridLayout( 1, 1, 10, 0 ) );
        innerPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( innerPanel );

        return actionPanel;
    }
    
    public void update() {
        for ( int i = 0; i < _values.size(); i++ ) {
            double amplitude = _outputFourierSeries.getHarmonic( i ).getAmplitude();
            JLabel label = (JLabel) _values.get( i );
            label.setText( String.valueOf( amplitude ) );
        }
        this.invalidate();
        this.repaint();
    }
}
