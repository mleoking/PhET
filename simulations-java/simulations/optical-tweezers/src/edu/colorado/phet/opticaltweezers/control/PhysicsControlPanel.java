/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JFrame;

import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.PhysicsDeveloperDialog;
import edu.colorado.phet.opticaltweezers.model.PhysicsModel;
import edu.colorado.phet.opticaltweezers.module.PhysicsModule;
import edu.colorado.phet.opticaltweezers.view.PhysicsCanvas;

/**
 * PhysicsControlPanel is the control panel for PhysicsModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PhysicsControlPanel extends AbstractControlPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhysicsModule _module;
    private PhysicsModel _model;
    private PhysicsCanvas _canvas;
    private PhysicsDeveloperDialog _developerControlsDialog;
    
    private ClockStepControlPanel _clockStepControlPanel;
    private LaserDisplayControlPanel _laserDisplayControlPanel;
    private BeadChargeControlPanel _beadChargeControlPanel;
    private ForcesControlPanel _forcesControlPanel;
    private ChartsControlPanel _chartsControlPanel;
    private AdvancedControlPanel _advancedControlPanel;
    
    private JCheckBox _developerControlsCheckBox;
    private JCheckBox _rulerCheckBox;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param module
     */
    public PhysicsControlPanel( PhysicsModule module) {
        super( module );
        
        _module = module;
        _model = module.getPhysicsModel();
        _canvas = module.getPhysicsCanvas();

        // Set the control panel's minimum width.
        int minimumWidth = OTResources.getInt( "int.minControlPanelWidth", 215 );
        setMinumumWidth( minimumWidth );
        
        // Sub-panels
        _clockStepControlPanel = new ClockStepControlPanel( TITLE_FONT, CONTROL_FONT, _model.getClock() );
        _laserDisplayControlPanel = new LaserDisplayControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getLaserNode() );
        _beadChargeControlPanel = new BeadChargeControlPanel( TITLE_FONT, CONTROL_FONT );
        _forcesControlPanel = new ForcesControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getTrapForceNode(), _canvas.getDragForceNode(), _canvas.getBrownianForceNode() );
        _chartsControlPanel = new ChartsControlPanel( TITLE_FONT, CONTROL_FONT, _canvas.getPositionHistogramChartNode(), _canvas.getPotentialEnergyChartNode() );
        _advancedControlPanel = new AdvancedControlPanel( TITLE_FONT, CONTROL_FONT, _model.getFluid(), _module.getFrame() );
        
        _developerControlsCheckBox = new JCheckBox( "Show Developer Controls" );
        _developerControlsCheckBox.setFont( CONTROL_FONT );
        _developerControlsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleDeveloperControlsCheckBox();
            }
        });
        
        _rulerCheckBox = new JCheckBox( OTResources.getString( "label.showRuler" ) );
        _rulerCheckBox.setFont( CONTROL_FONT );
        _rulerCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRulerCheckBox();
            }
        });
        
        // Layout
        {
            if ( System.getProperty( OTConstants.PROPERTY_PHET_DEVELOPER ) != null ) {
                addControlFullWidth( _developerControlsCheckBox );
                addSeparator();
            }
            addControlFullWidth( _clockStepControlPanel );
            addSeparator();
            addControlFullWidth( _laserDisplayControlPanel );
            addSeparator();
            addControlFullWidth( _beadChargeControlPanel );
            addSeparator();
            addControlFullWidth( _forcesControlPanel );
            addSeparator();
            addControlFullWidth( _chartsControlPanel );
            addSeparator();
            addControlFullWidth( _rulerCheckBox );
            addSeparator();
            addControlFullWidth( _advancedControlPanel );
            addSeparator();
            addResetButton();
        }
        
        // Default state
        _developerControlsCheckBox.setSelected( false );
        _rulerCheckBox.setSelected( false );
        //XXX enable & disable controls based on clock speed
    }
    
    //----------------------------------------------------------------------------
    // Mutators and accessors
    //----------------------------------------------------------------------------
    
    public ClockStepControlPanel getClockStepControlPanel() {
        return _clockStepControlPanel;
    }
    
    public LaserDisplayControlPanel getLaserDisplayControlPanel() {
        return _laserDisplayControlPanel;
    }
    
    public BeadChargeControlPanel getBeadChargeControlPanel() {
        return _beadChargeControlPanel;
    }
    
    public ForcesControlPanel getForcesControlPanel() {
        return _forcesControlPanel;
    }
    
    public ChartsControlPanel getChartsControlPanel() {
        return _chartsControlPanel;
    }
    
    public AdvancedControlPanel getAdvancedControlPanel() {
        return _advancedControlPanel;
    }
    
    public void setDeveloperControlsSelected( boolean b ) {
        _developerControlsCheckBox.setSelected( b );
        handleDeveloperControlsCheckBox();
    }
    
    public boolean isDeveloperControlsSelected() {
        return _developerControlsCheckBox.isSelected();
    }
    
    public void setRulerSelected( boolean b ) {
        _rulerCheckBox.setSelected( b );
        handleRulerCheckBox();
    }
    
    public boolean isRulerSelected() {
        return _rulerCheckBox.isSelected();
    }
    
    public void closeAllDialogs() {
        _advancedControlPanel.setFluidControlSelected( false );
        setDeveloperControlsSelected( false );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleRulerCheckBox() {
        final boolean selected = _rulerCheckBox.isSelected();
        _canvas.getRulerNode().setVisible( selected );
    }
    
    private void handleDeveloperControlsCheckBox() {
        
        final boolean selected = _developerControlsCheckBox.isSelected();
        
        if ( !selected ) {
            if ( _developerControlsDialog != null ) {
                _developerControlsDialog.dispose();
                _developerControlsDialog = null;
            }
        }
        else {
            JFrame parentFrame = _module.getFrame();
            _developerControlsDialog = new PhysicsDeveloperDialog( parentFrame, _module );
            _developerControlsDialog.addWindowListener( new WindowAdapter() {

                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    _developerControlsDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    _developerControlsDialog = null;
                    _developerControlsCheckBox.setSelected( false );
                }
            } );
            _developerControlsDialog.show();
        }
    }
}
