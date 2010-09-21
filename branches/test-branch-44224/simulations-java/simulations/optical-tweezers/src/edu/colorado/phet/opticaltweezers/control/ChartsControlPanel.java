/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

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
    private PNode _potentialEnergyChartNode;
    private LaserNode _laserNode;
    
    private PositionHistogramDialog _positionHistogramDialog;
    private Point _positionHistogramDialogOffset;
    private Point _positionHistogramDialogLocation;
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
     * @param positionHistogramDialogOffset
     * @param clock
     * @param bead
     * @param laser
     * @param potentialEnergyChartNode
     */
    public ChartsControlPanel( Font titleFont, Font controlFont, 
            Frame parentFrame, Point positionHistogramDialogOffset,
            IClock clock, Bead bead, Laser laser, 
            PNode potentialEnergyChartNode, LaserNode laserNode ) {
        super();
        
        _parentFrame = parentFrame;
        _clock = clock;
        _bead = bead;
        _laser = laser;
        _potentialEnergyChartNode = potentialEnergyChartNode;
        _laserNode = laserNode;
        
        _positionHistogramDialog = null;
        _positionHistogramDialogOffset = new Point( positionHistogramDialogOffset );
        _positionHistogramDialogLocation = null;
        _positionHistogramZoomIndex = -1; // force an update
        _positionHistogramRulerVisible = false;

        JLabel titleLabel = new JLabel( OTResources.getString( "title.chartsControlPanel" ) );
        titleLabel.setFont( titleFont );
        
        // Position Histogram checkbox and icon
        JPanel positionHistogramPanel = null;
        {
            _positionHistogramCheckBox = new JCheckBox( OTResources.getString( "label.positionHistogram" ) );
            _positionHistogramCheckBox.setFont( controlFont );
            _positionHistogramCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handlePositionHistogramCheckBox();
                }
            } );
            
            Icon positionHistogramIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_HISTOGRAM_ICON ) );
            JLabel positionHistogramLabel = new JLabel( positionHistogramIcon );
            positionHistogramLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    setPositionHistogramSelected( !isPositionHistogramSelected() );
                }
            } );
            
            positionHistogramPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            positionHistogramPanel.add( _positionHistogramCheckBox );
            positionHistogramPanel.add( positionHistogramLabel );
        }

        // Potential Energy Chart checkbox and icon
        JPanel potentialEnergyPanel = null;
        {
            _potentialEnergyChartCheckBox = new JCheckBox( OTResources.getString( "label.potentialEnergyChart" ) );
            _potentialEnergyChartCheckBox.setFont( controlFont );
            _potentialEnergyChartCheckBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handlePotentialEnergyCheckBox();
                }
            } );
            _potentialEnergyChartNode.addPropertyChangeListener( new PropertyChangeListener() {
                public void propertyChange( PropertyChangeEvent evt ) {
                    _potentialEnergyChartCheckBox.setSelected( _potentialEnergyChartNode.getVisible() );
                }
            } );
            
            Icon potentialEnergyChartIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_POTENTIAL_ENERGY_CHART_ICON ) );
            JLabel potentialEnergyChartLabel = new JLabel( potentialEnergyChartIcon );
            potentialEnergyChartLabel.addMouseListener( new MouseAdapter() {
                public void mouseReleased( MouseEvent event ) {
                    setPotentialEnergySelected( !isPotentialChartSelected() );
                }
            } );
            
            potentialEnergyPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            potentialEnergyPanel.add( _potentialEnergyChartCheckBox );
            potentialEnergyPanel.add( potentialEnergyChartLabel );
        }

        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( titleLabel, row++, 0 );
        layout.addComponent( positionHistogramPanel, row++, 0 );
        layout.addComponent( potentialEnergyPanel, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );

        // Default state
        _positionHistogramCheckBox.setSelected( _positionHistogramDialog != null );
        _potentialEnergyChartCheckBox.setSelected( _potentialEnergyChartNode.getVisible() );
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
            if ( _parentFrame.isVisible() ) {
                openPositionHistogramDialog();
            }
        }
        else {
            closePositionHistogramDialog();
        }
    }
    
    private void handlePotentialEnergyCheckBox() {
        _potentialEnergyChartNode.setVisible( _potentialEnergyChartCheckBox.isSelected() );
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
        
        if ( _positionHistogramDialogLocation == null ) {
            // initial placement is at the upper-left corner of the parent frame
            int x = (int)( _parentFrame.getLocation().getX() + _positionHistogramDialogOffset.getX() );
            int y = (int)( _parentFrame.getLocation().getY() + _positionHistogramDialogOffset.getY() );
            _positionHistogramDialog.setLocation( x, y );
        }
        else {
            _positionHistogramDialog.setLocation( _positionHistogramDialogLocation );
        }
        _positionHistogramDialog.show();
    }
    
    private void closePositionHistogramDialog() {
        if ( _positionHistogramDialog != null ) {
            _positionHistogramDialogLocation = _positionHistogramDialog.getLocation();
            _positionHistogramZoomIndex = _positionHistogramDialog.getPanel().getZoomIndex();
            _positionHistogramRulerVisible = _positionHistogramDialog.getPanel().isRulerVisible();
            _positionHistogramDialog.dispose();
            _positionHistogramDialog = null;
        }
    }
}
