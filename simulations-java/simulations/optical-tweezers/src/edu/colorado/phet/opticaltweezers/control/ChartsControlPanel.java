/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.PositionHistogramDialog;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.umd.cs.piccolo.PNode;

/**
 * ChartsControlPanel is the panel used to control the visibility of charts.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ChartsControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Frame _parentFrame;
    private IClock _clock;
    private Bead _bead;
    private Laser _laser;
    
    private JDialog _positionHistogramDialog;
    private PNode _potentialEnergyNode;

    private JCheckBox _positionHistogramCheckBox;
    private JCheckBox _potentialEnergyChartCheckBox;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont font used for the control panel's title
     * @param controlFont font used for the controls 
     * @param parentFrame
     * @param clock
     * @param bead
     * @param laser
     * @param potentialEnergyNode
     */
    public ChartsControlPanel( Font titleFont, Font controlFont, Frame parentFrame,
            IClock clock, Bead bead, Laser laser, 
            PNode potentialEnergyNode ) {
        super();
        
        _parentFrame = parentFrame;
        _clock = clock;
        _bead = bead;
        _laser = laser;

        _positionHistogramDialog = null;
        _potentialEnergyNode = potentialEnergyNode;

        JLabel titleLabel = new JLabel( OTResources.getString( "title.chartsControlPanel" ) );
        titleLabel.setFont( titleFont );
        
        // Position Histogram
        _positionHistogramCheckBox = new JCheckBox( OTResources.getString( "label.positionHistogram" ) );
        _positionHistogramCheckBox.setFont( controlFont );
        _positionHistogramCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handlePositionHistogramCheckBox();
            }
        });

        // Potential Energy chart
        _potentialEnergyChartCheckBox = new JCheckBox( OTResources.getString( "label.potentialEnergyChart" ) );
        _potentialEnergyChartCheckBox.setFont( controlFont );
        _potentialEnergyChartCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handlePotentialEnergyCheckBox();
            }
        });
        _potentialEnergyNode.addPropertyChangeListener( new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                _potentialEnergyChartCheckBox.setSelected( _potentialEnergyNode.getVisible() );
            }
        } );

        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0 );
        layout.addComponent( _positionHistogramCheckBox, row++, 0 );
        layout.addComponent( _potentialEnergyChartCheckBox, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // Default state
        _positionHistogramCheckBox.setSelected( _positionHistogramDialog != null );
        _potentialEnergyChartCheckBox.setSelected( _potentialEnergyNode.getVisible() );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setPositionHistogramSelected( boolean b ) {
        _positionHistogramCheckBox.setSelected( b );
        handlePositionHistogramCheckBox();
    }
    
    public boolean isPositionHistogramSelected() {
        return _positionHistogramCheckBox.isSelected();
    }
    
    public void setPotentialEnergySelected( boolean b ) {
        _potentialEnergyChartCheckBox.setSelected( b );
        handlePotentialEnergyCheckBox();
    }
    
    public boolean isPotentialChartSelected() {
        return _potentialEnergyChartCheckBox.isSelected();
    }

    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handlePositionHistogramCheckBox() {
        if ( _positionHistogramCheckBox.isSelected() ) {
            openPositionHistogramDialog();
        }
        else {
            closePositionHistogramDialog();
        }
    }
    
    private void handlePotentialEnergyCheckBox() {
        _potentialEnergyNode.setVisible( _potentialEnergyChartCheckBox.isSelected() );
    }
    
    //----------------------------------------------------------------------------
    // Position Histogram dialog
    //----------------------------------------------------------------------------
    
    private void openPositionHistogramDialog() {
        
        closePositionHistogramDialog();
        
        _positionHistogramDialog = new PositionHistogramDialog( _parentFrame, OTConstants.CONTROL_PANEL_CONTROL_FONT, _clock, _bead, _laser );
        _positionHistogramDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                _positionHistogramDialog.dispose();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                _positionHistogramDialog = null;
                _positionHistogramCheckBox.setSelected( false );
            }
        } );
        
        // Position at the lower-left of the main frame
        _positionHistogramDialog.show();
    }
    
    private void closePositionHistogramDialog() {
        if ( _positionHistogramDialog != null ) {
            _positionHistogramDialog.dispose();
            _positionHistogramDialog = null;
        }
    }
}
