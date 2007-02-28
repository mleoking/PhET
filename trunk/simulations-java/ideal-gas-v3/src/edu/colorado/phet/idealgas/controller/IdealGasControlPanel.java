/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.idealgas.controller;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.idealgas.IdealGasConfig;
import edu.colorado.phet.idealgas.model.Gravity;
import edu.colorado.phet.idealgas.model.IdealGasModel;
import edu.colorado.phet.idealgas.model.HeavySpecies;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Hashtable;

/**
 * The base control panel for all modules.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class IdealGasControlPanel extends JPanel implements Gravity.ChangeListener {

    private NumberFormat gravityFormat = NumberFormat.getInstance();
    private JSlider gravitySlider;
    private IdealGasModule module;
    private IdealGasModel idealGasModel;
    private GridBagConstraints gbc;
    // Separate panels for miscellaneous controls, particle controls, and buttons
    private JPanel miscPanel;
    private JPanel particleControlsPanel;
    private GridBagConstraints particleControlsGbc;


    /**
     * @param module
     */
    public IdealGasControlPanel( IdealGasModule module ) {
        super();
        this.module = module;
        this.idealGasModel = (IdealGasModel)module.getModel();
        idealGasModel.getGravity().addListener( this );
        init();
    }

    private void init() {
        // Create the component panels
        makeMiscControls();
        makeParticlesControls();

        // Lay out the panel
        this.setLayout( new GridBagLayout() );
        gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                      1, 1, 1, 1,
                                      GridBagConstraints.NORTH,
                                      GridBagConstraints.HORIZONTAL,
                                      new Insets( 0, 0, 0, 0 ), 0, 0 );

        // Add control for selecting the constant parameter
        add( constantParamControls(), gbc );

        // Add controls for the number and type of molecules to put in the box
        add( particleControlsPanel, gbc );

        // Add miscellaneous controls (gravity, particle interactions, etc.
        this.add( miscPanel, gbc );

        // Add the measurement tools and options panel
        this.add( new ToolPanel( module ), gbc );

        // Reset button
        JButton resetBtn = new JButton( SimStrings.get( "IdealGasControlPanel.Reset" ) );
        resetBtn.setBackground( new Color( 180, 255, 180 ) );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.reset();
            }
        } );
        GridBagConstraints resetGbc = (GridBagConstraints)gbc.clone();
        resetGbc.fill = GridBagConstraints.NONE;
        this.add( resetBtn, resetGbc );

        Border border = BorderFactory.createEtchedBorder();
        this.setBorder( border );
    }

    /**
     * Creates the panel that holds controls for the particles in the box
     */
    private void makeParticlesControls() {
        particleControlsPanel = new JPanel( new GridBagLayout() );

        particleControlsGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                      1, 1, 1, 1,
                                                      GridBagConstraints.CENTER,
                                                      GridBagConstraints.HORIZONTAL,
                                                      new Insets( 0, 0, 0, 0 ), 0, 0 );

        // Add controls for the number and type of molecules to put in the box
        JPanel speciesButtonPanel = new PumpControlPanel( module, module.getPump(), module.getSpeciesNames() );
        speciesButtonPanel.setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Gas_In_Chamber" ) ) );
        particleControlsPanel.add( speciesButtonPanel, particleControlsGbc );
    }

    /**
     * Creates the panel that contains controls for things that don't appear as objects on the screen
     */
    private void makeMiscControls() {
        miscPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints localGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.CENTER,
                                                              GridBagConstraints.HORIZONTAL,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
        JPanel gravityControls = gravityControls();
        miscPanel.add( gravityControls, localGbc );
    }

    /**
     * Returns a panel with selections for whether volume or pressure is to be held constant
     */
    private JPanel constantParamControls() {
        JPanel constantParamButtonPanel = new JPanel( new GridBagLayout() );
        final JRadioButton constantVolumeRB = new JRadioButton( SimStrings.get( "Common.Volume" ) );
        final JRadioButton constantPressureRB = new JRadioButton( SimStrings.get( "Common.Pressure" ) );
        final JRadioButton constantTempRB = new JRadioButton( SimStrings.get( "Common.Temperature" ) );
        final JRadioButton noneRB = new JRadioButton( SimStrings.get( "Common.None" ) );
        final ButtonGroup constantParameterGroup = new ButtonGroup();
        constantParameterGroup.add( constantVolumeRB );
        constantParameterGroup.add( constantPressureRB );
        constantParameterGroup.add( constantTempRB );
        constantParameterGroup.add( noneRB );

        GridBagConstraints localGbc = new GridBagConstraints( 0, 0,
                                                              1, 1, 1, 1,
                                                              GridBagConstraints.WEST, GridBagConstraints.NONE,
                                                              new Insets( 0, 0, 0, 0 ), 0, 0 );
        localGbc.gridx = 0;
        localGbc.gridy = 0;
        constantParamButtonPanel.add( constantVolumeRB, localGbc );
        localGbc.gridx = 1;
        localGbc.gridy = 0;
        constantParamButtonPanel.add( constantPressureRB, localGbc );
        localGbc.gridx = 0;
        localGbc.gridy = 1;
        constantParamButtonPanel.add( constantTempRB, localGbc );
        localGbc.gridx = 1;
        localGbc.gridy = 1;
        constantParamButtonPanel.add( noneRB, localGbc );

        JPanel container = new JPanel( new GridBagLayout() );
        localGbc.anchor = GridBagConstraints.CENTER;
        container.add( constantParamButtonPanel, localGbc );
        container.setBorder( new TitledBorder( SimStrings.get( "IdealGasControlPanel.Constant_Parameter" ) ) );

        constantVolumeRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                idealGasModel.setConstantProperty( IdealGasModel.CONSTANT_VOLUME );
            }
        } );
        constantPressureRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                idealGasModel.setConstantProperty( IdealGasModel.CONSTANT_PRESSURE );
            }
        } );
        constantTempRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                idealGasModel.setConstantProperty( IdealGasModel.CONSTANT_TEMPERATURE );
            }
        } );
        noneRB.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                idealGasModel.setConstantProperty( IdealGasModel.CONSTANT_NONE );
            }
        } );
        noneRB.setSelected( true );
        return container;
    }

    /**
     * Create a panel with controls for gravity and add it to the IdealGasControlPanel
     */
    private JPanel gravityControls() {

        JPanel gravityControlPanel = new JPanel( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( GridBagConstraints.RELATIVE, 0, 1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 0, 0, 0, 0 ), 0, 0 );

        gravitySlider = new JSlider( JSlider.HORIZONTAL, 0, IdealGasConfig.MAX_GRAVITY, 0 );
        gravitySlider.setPreferredSize( new Dimension( 150, 50 ) );
        gravitySlider.setPaintTicks( true );
        gravitySlider.setMajorTickSpacing( 10 );
        gravitySlider.setMinorTickSpacing( 5 );
        Hashtable labelTable = new Hashtable();
        labelTable.put( new Integer( 0 ), new JLabel( SimStrings.get( "Common.0" ) ) );
        labelTable.put( new Integer( IdealGasConfig.MAX_GRAVITY ), new JLabel( SimStrings.get( "Common.Lots" ) ) );
        gravitySlider.setLabelTable( labelTable );
        gravitySlider.setPaintLabels( true );
        gravityControlPanel.add( gravitySlider, gbc );
        gravitySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent event ) {
                module.setGravity( gravitySlider.getValue() );
            }
        } );

        // Add a readout for the value of gravity
        JTextField gravityTF = new JTextField( 2 );
        gravityTF.setEditable( false );
        gravityTF.setHorizontalAlignment( JTextField.RIGHT );
        gravityFormat.setMaximumFractionDigits( 2 );
        gravityFormat.setMinimumFractionDigits( 2 );
        gravityTF.setText( gravityFormat.format( 0 ) );

        Border gravityBorder = new TitledBorder( SimStrings.get( "Common.Gravity" ) );
        gravityControlPanel.setBorder( gravityBorder );
        return gravityControlPanel;
    }

    public void setGravity( double amt ) {
        this.gravitySlider.setValue( (int)amt );
    }

    public void addComponent( Component component ) {
        this.add( component, gbc );
    }

    public void addParticleControl( Component component ) {
        particleControlsPanel.add( component, particleControlsGbc );
    }

    //--------------------------------------------------------------------------
    // Utility methods
    //--------------------------------------------------------------------------

    protected IdealGasModule getModule() {
        return module;
    }

    //-----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------
    public void gravityChanged( Gravity.ChangeEvent event ) {
        gravitySlider.setValue( (int)event.getGravity().getAmt() );
    }
}


