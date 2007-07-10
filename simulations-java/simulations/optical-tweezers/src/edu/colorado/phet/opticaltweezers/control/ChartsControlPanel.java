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
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.PositionHistogramDialog;
import edu.colorado.phet.opticaltweezers.model.Bead;
import edu.colorado.phet.opticaltweezers.model.Laser;
import edu.colorado.phet.opticaltweezers.view.LaserNode;
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
    private PNode _potentialEnergyNode;
    private LaserNode _laserNode;
    
    private PositionHistogramDialog _positionHistogramDialog;
    private int _positionHistogramZoomIndex;
    private boolean _positionHistogramRulerVisible;

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
            PNode potentialEnergyNode, LaserNode laserNode ) {
        super();
        
        _parentFrame = parentFrame;
        _clock = clock;
        _bead = bead;
        _laser = laser;
        _potentialEnergyNode = potentialEnergyNode;
        _laserNode = laserNode;
        
        _positionHistogramDialog = null;
        _positionHistogramZoomIndex = -1; // force an update
        _positionHistogramRulerVisible = false;

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
                closePositionHistogramDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                _positionHistogramDialog = null;
                _positionHistogramCheckBox.setSelected( false );
                _laserNode.setOriginMarkerVisible( false );
            }
        } );
        
        _laserNode.setOriginMarkerVisible( true );
        
        // Restore the zoom index
        if ( _positionHistogramZoomIndex >= 0 ) {
            _positionHistogramDialog.getPanel().setZoomIndex( _positionHistogramZoomIndex );
        }
        
        // Restore the ruler visibility
        _positionHistogramDialog.getPanel().setRulerVisible( _positionHistogramRulerVisible );
        
        // Place the dialog at the upper-left corner of the parent frame
        _positionHistogramDialog.setLocation( (int) _parentFrame.getLocation().getX() + 15, (int) _parentFrame.getLocation().getY() + 80 );
        _positionHistogramDialog.show();
    }
    
    private void closePositionHistogramDialog() {
        if ( _positionHistogramDialog != null ) {
            _positionHistogramZoomIndex = _positionHistogramDialog.getPanel().getZoomIndex();
            _positionHistogramRulerVisible = _positionHistogramDialog.getPanel().isRulerVisible();
            _positionHistogramDialog.dispose();
            _positionHistogramDialog = null;
        }
    }
}
