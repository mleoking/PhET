/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.faraday.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.ModuleManager;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.faraday.FaradayConfig;
import edu.colorado.phet.faraday.module.ICompassGridModule;


/**
 * GridControlDialog is a non-model dialog that contains controls for 
 * the "compass grid" (CompassGridGraphic).  The controls affect all
 * grids in the entire application.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GridControlsDialog extends JDialog implements ActionListener, ChangeListener {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PhetApplication _app;
    private JSlider _gridSpacingSlider, _needleSizeSlider;
    private JLabel _gridSpacingValue, _needleSizeValue;
    private JButton _okButton, _cancelButton;
    private int _xSpacing, _ySpacing;
    private Dimension _needleSize;
    
    //----------------------------------------------------------------------------
    // Constructors & initializers
    //----------------------------------------------------------------------------
    
    /**
     * Sole constructor.
     * 
     * @param app the application
     * @param title the dialog title
     */
    public GridControlsDialog( PhetApplication app, String title ) {
        super( app.getPhetFrame(), title, false /* non-modal */ );
        _app = app;
        initValues();
        createUI( app.getPhetFrame() );
    }
    
    /**
     * Reads the current values for the grid controls.
     */
    private void initValues() {
        // Find the first module that has a grid and use its values.
        ModuleManager moduleManager = _app.getModuleManager();
        Module module = moduleManager.getActiveModule();
        if ( ! ( module instanceof ICompassGridModule ) ) {
            int numberOfModules = moduleManager.numModules();
            for ( int i = 0; i < numberOfModules; i++ ) {
                module = moduleManager.moduleAt( i );
                if ( module instanceof ICompassGridModule ) {
                    break;
                }
                else {
                    module = null;
                }
            }
        }
        assert ( module != null ); // Why are you using this dialog in you app?
        ICompassGridModule gm = (ICompassGridModule) module;
        _xSpacing = gm.getGridXSpacing();
        _ySpacing = gm.getGridYSpacing();
        _needleSize = gm.getGridNeedleSize();
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
        this.setResizable( false );
        this.setLocationRelativeTo( parent );
    }
    
    /**
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        // Warning panel
        JLabel warningMessage = new JLabel( SimStrings.get( "gridWarning.text" ) );
        JPanel warningPanel = new JPanel();
        warningPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
        warningPanel.add( warningMessage );
        
        // Grid spacing
        JLabel gridSpacingLabel = new JLabel( SimStrings.get( "gridSpacing.label" ) );
        {
            // Slider
            _gridSpacingSlider = new JSlider();
            _gridSpacingSlider.setMinimum( FaradayConfig.GRID_SPACING_MIN );
            _gridSpacingSlider.setMaximum( FaradayConfig.GRID_SPACING_MAX );
            _gridSpacingSlider.setValue( _xSpacing );
            FaradayControlPanel.setSliderSize( _gridSpacingSlider, FaradayControlPanel.SLIDER_SIZE );

            // Value
            _gridSpacingValue = new JLabel( String.valueOf( _xSpacing) );
        }
        
        // Needle size
        JLabel needleSizeLabel = new JLabel( SimStrings.get( "needleSize.label" ) );
        {
            // Slider
            _needleSizeSlider = new JSlider();
            _needleSizeSlider.setMinimum( FaradayConfig.GRID_NEEDLE_WIDTH_MIN );
            _needleSizeSlider.setMaximum( FaradayConfig.GRID_NEEDLE_WIDTH_MAX );
            _needleSizeSlider.setValue( _needleSize.width );
            FaradayControlPanel.setSliderSize( _needleSizeSlider, FaradayControlPanel.SLIDER_SIZE );

            // Value
            String value = String.valueOf( _needleSize.width ) + "x" + String.valueOf( _needleSize.height );
            _needleSizeValue = new JLabel( value );
        }
 
        // Listeners
        _gridSpacingSlider.addChangeListener( this );
        _needleSizeSlider.addChangeListener( this );
        
        // Layout
        JPanel controlPanel = new JPanel();
        {     
            EasyGridBagLayout layout = new EasyGridBagLayout( controlPanel );
            controlPanel.setLayout( layout );
            controlPanel.setBorder( new EmptyBorder( 10, 10, 10, 10 ) );
            
            // Grid spacing
            int row = 0;
            layout.addAnchoredComponent( gridSpacingLabel, row, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _gridSpacingSlider, row, 1, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _gridSpacingValue, row, 2, GridBagConstraints.WEST );
            
            // Needle size
            row++;
            layout.addAnchoredComponent( needleSizeLabel, row, 0, GridBagConstraints.EAST );
            layout.addAnchoredComponent( _needleSizeSlider, row, 1, GridBagConstraints.WEST );
            layout.addAnchoredComponent( _needleSizeValue, row, 2, GridBagConstraints.WEST );
        }
        
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout( new BorderLayout() );
        inputPanel.add( warningPanel, BorderLayout.NORTH );
        inputPanel.add( controlPanel, BorderLayout.CENTER );
        return inputPanel;
    }
    
    /** 
     * Creates the dialog's actions panel, consisting of OK and Cancel buttons.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel()
    {   
      _okButton = new JButton( SimStrings.get( "okButton.label" ) );
      _okButton.addActionListener( this );
      
      _cancelButton = new JButton( SimStrings.get( "cancelButton.label" ) ); 
      _cancelButton.addActionListener( this );
      
      JPanel innerPanel = new JPanel( new GridLayout(1,2,10,0) );
      innerPanel.add( _okButton );
      innerPanel.add( _cancelButton );
        
      JPanel actionPanel = new JPanel( new FlowLayout() );
      actionPanel.add( innerPanel );

      return actionPanel;
    }

    /**
     * Handles OK and Cancel button presses.
     */
    public void actionPerformed( ActionEvent e ) {
        if ( e.getSource() == _okButton ) {
            this.dispose();
        }
        else if ( e.getSource() == _cancelButton ) {
            setAllGrids( _xSpacing, _ySpacing, _needleSize );
            this.dispose();
        }
    }

    /**
     * Handles changes to the sliders.  Since the parameters are closely
     * related, we simply read and update all values.
     * 
     * @param e the event
     */
    public void stateChanged( ChangeEvent e ) {
        int spacing = _gridSpacingSlider.getValue();
        int needleWidth = _needleSizeSlider.getValue();
        int needleHeight = (int) ( needleWidth / FaradayConfig.GRID_NEEDLE_ASPECT_RATIO );
        Dimension needleSize = new Dimension( needleWidth, needleHeight );
        
        _gridSpacingValue.setText( String.valueOf( spacing ) );
        _needleSizeValue.setText( String.valueOf( needleWidth ) + "x" + String.valueOf( needleHeight ) );
        
        setAllGrids( spacing, spacing, needleSize );
    }
    
    /**
     * Sets grid parameters for all Modules that have a grid.
     * 
     * @param xSpacing horizontal spacing, in pixels
     * @param ySpacing vertical spacing, in pixels
     * @param needleSize needle size, in pixels
     */
    private void setAllGrids( int xSpacing, int ySpacing, Dimension needleSize ) {
        ModuleManager moduleManager = _app.getModuleManager();
        int numberOfModules = moduleManager.numModules();
        for ( int i = 0; i < numberOfModules; i++ ) {
            Module module = moduleManager.moduleAt( i );
            if ( module instanceof ICompassGridModule ) {
                ( (ICompassGridModule) module ).setGridSpacing( xSpacing, ySpacing );
                ( (ICompassGridModule) module ).setGridNeedleSize( needleSize );
            }
        }
    }
}