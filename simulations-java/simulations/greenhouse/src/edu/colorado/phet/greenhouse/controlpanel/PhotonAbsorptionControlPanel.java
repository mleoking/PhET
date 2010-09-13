/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.CH4;
import edu.colorado.phet.greenhouse.model.CO2;
import edu.colorado.phet.greenhouse.model.H2O;
import edu.colorado.phet.greenhouse.model.Molecule;
import edu.colorado.phet.greenhouse.model.MoleculeID;
import edu.colorado.phet.greenhouse.model.N2;
import edu.colorado.phet.greenhouse.model.O2;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel.PhotonTarget;
import edu.colorado.phet.greenhouse.view.MoleculeNode;

/**
 * Control panel for the Photon Absorption tab of this application.
 * 
 * @author John Blanco
 */
public class PhotonAbsorptionControlPanel extends ControlPanel {
    
    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------
    
    // Font to use on this panel.
    private static final Font LABEL_FONT = new PhetFont( 14 );
    
    // Model view transform used for creating images of the various molecules.
    // This is basically a null transform except that it flips the Y axis so
    // that molecules are oriented the same as in the play area.
    private static final ModelViewTransform2D MVT =
        new ModelViewTransform2D( new Point2D.Double(0, 0), new Point(0, 0), 1, true);
    
    // Image scaling factors, determined empirically.
    private static final double MOLECULE_SCALING_FACTOR = 0.13;
    private static final double PLANET_SCALING_FACTOR = 0.24;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    private PhotonAbsorptionModel model;

    private RadioButtonWithIconPanel co2Selector;
    private RadioButtonWithIconPanel h2oSelector;
    private RadioButtonWithIconPanel ch4Selector;
    private RadioButtonWithIconPanel n2Selector;
    private RadioButtonWithIconPanel o2Selector;
    private RadioButtonWithIconPanel atmosphereSelector;
    
    private HashMap<MoleculeID, LinearValueControl> moleculeToSliderMap = new HashMap<MoleculeID, LinearValueControl>();
    
    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public PhotonAbsorptionControlPanel (PiccoloModule module, final PhotonAbsorptionModel model){

        this.model = model;
        
        model.addListener( new PhotonAbsorptionModel.Adapter(){
            @Override
            public void photonTargetChanged() {
                updateSliderEnabledState();
            }

            @Override
            public void configurableAtmosphereCompositionChanged() {
                updateSliderPositions();
            }
        });
        
        // Set the control panel's minimum width.
        int minimumWidth = GreenhouseResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Create and add a panel that will contain the buttons for selecting
        // the gas.
        PhetTitledPanel atmosphericGasesPanel = new PhetTitledPanel(GreenhouseResources.getString("PhotonAbsorptionControlPanel.AtmosphericGasesTitle"));
        atmosphericGasesPanel.setLayout(new GridBagLayout());
        GridBagConstraints constraints=new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        addControlFullWidth(atmosphericGasesPanel);
        
        // Add buttons for selecting greenhouse gas.
        ch4Selector = createAndAttachSelectorPanel( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.CH4"),
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.Methane"),
                createImageFromMolecule( new CH4() ), PhotonTarget.SINGLE_CH4_MOLECULE, MOLECULE_SCALING_FACTOR );
        ch4Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(ch4Selector,constraints);
        
        co2Selector = createAndAttachSelectorPanel( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.CO2"), 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.CarbonDioxide"),
                createImageFromMolecule( new CO2() ), PhotonTarget.SINGLE_CO2_MOLECULE, MOLECULE_SCALING_FACTOR );
        co2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(co2Selector,constraints);
        
        h2oSelector = createAndAttachSelectorPanel( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.H2O"), 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.Water"),
                createImageFromMolecule( new H2O() ), PhotonTarget.SINGLE_H2O_MOLECULE, MOLECULE_SCALING_FACTOR );
        h2oSelector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(h2oSelector,constraints);
        
        n2Selector = createAndAttachSelectorPanel( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.N2"),
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.Nitrogen"),
                createImageFromMolecule( new N2() ), PhotonTarget.SINGLE_N2_MOLECULE, MOLECULE_SCALING_FACTOR );
        n2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(n2Selector,constraints);
        
        o2Selector = createAndAttachSelectorPanel( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.O2"),
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.Oxygen"),
                createImageFromMolecule( new O2() ), PhotonTarget.SINGLE_O2_MOLECULE, MOLECULE_SCALING_FACTOR );
        o2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(o2Selector,constraints);

        atmosphereSelector = createAndAttachSelectorPanel(
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.BuildAtmosphere"), null,
                GreenhouseResources.getImage( "earth.png" ), PhotonTarget.CONFIGURABLE_ATMOSPHERE, PLANET_SCALING_FACTOR);
        atmosphericGasesPanel.add(atmosphereSelector,constraints);

        // Create and add a panel that will contain the sliders for
        // configuring the atmosphere.
        VerticalLayoutPanel atmosphereSliderPanel = new VerticalLayoutPanel();
        
        // Add the molecule control sliders.
        addSliderForMolecule( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.CH4"),
                atmosphereSliderPanel, MoleculeID.CH4 );
        addSliderForMolecule( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.CO2"),
                atmosphereSliderPanel, MoleculeID.CO2 );
        addSliderForMolecule( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.H2O"),
                atmosphereSliderPanel, MoleculeID.H2O );
        addSliderForMolecule( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.N2"),
                atmosphereSliderPanel, MoleculeID.N2 );
        addSliderForMolecule( 
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.O2"),
                atmosphereSliderPanel, MoleculeID.O2 );
        
        atmosphericGasesPanel.add( atmosphereSliderPanel ,constraints);

        // Put all the buttons in a button group.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( co2Selector.getButton() );
        buttonGroup.add( h2oSelector.getButton() );
        buttonGroup.add( ch4Selector.getButton() );
        buttonGroup.add( n2Selector.getButton() );
        buttonGroup.add( o2Selector.getButton() );
        buttonGroup.add(atmosphereSelector.getButton());

        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(5));
        addResetAllButton( module );
        
        // Synchronize the controls with the model.
        updateSliderPositions();
        updateSliderEnabledState();
    }
    
    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    private void updateSliderPositions(){
        for ( MoleculeID moleculeID : moleculeToSliderMap.keySet() ){
            moleculeToSliderMap.get( moleculeID ).setValue( model.getConfigurableAtmosphereGasLevel( moleculeID ) );
        }
    }
    
    private void updateSliderEnabledState(){
        boolean slidersEnabled = model.getPhotonTarget() == PhotonTarget.CONFIGURABLE_ATMOSPHERE;
        for ( LinearValueControl linearValueControl : moleculeToSliderMap.values() ){
            linearValueControl.setEnabled( slidersEnabled );
        }
    }
    
    /**
     * Create and add the slider that controls the level of the specified
     * molecule in the configurable atmosphere.  This is primarily a
     * convenience method that prevents duplication of code.
     * 
     * @param slider
     * @param moleculeID
     */
    private void addSliderForMolecule( String labelText, JPanel panel, final MoleculeID moleculeID){
       
        // The overall width of the control that is created by this method
        // needs to be a little less that the total control panel so that it
        // can be enclosed in a border.  Also, it needs to be indented
        // slightly to convey the idea that it is a sub-function of the "Build
        // Atmosphere" selection.
        int overallWidth = GreenhouseResources.getInt( "int.minControlPanelWidth", 215 ) - 8;
        int indent = 20; // Arbitrary value, adjust as needed for optimal appearance.
        JPanel sliderPanel = new JPanel();
        JPanel spacerPanel = new JPanel();
        spacerPanel.setLayout( new BoxLayout( spacerPanel, BoxLayout.X_AXIS ) );
        spacerPanel.add( Box.createHorizontalStrut( indent ) );

        final LinearValueControl slider = new LinearValueControl( 0,
                model.getConfigurableAtmosphereMaxLevel( moleculeID ), labelText, "###",
                GreenhouseResources.getString("PhotonAbsorptionControlPanel.Molecules"));
        slider.setFont( LABEL_FONT );
        slider.setUpDownArrowDelta( 1 );
        slider.setTextFieldEditable( true );
        slider.setMajorTicksVisible( false );
        slider.setValue( model.getConfigurableAtmosphereGasLevel( moleculeID ) );
        slider.setSliderWidth( overallWidth - indent );
        slider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int currentLevel = model.getConfigurableAtmosphereGasLevel( moleculeID );
                int sliderValue = (int)Math.round( slider.getValue() );
                if ( sliderValue != currentLevel ){
                    model.setConfigurableAtmosphereGasLevel( moleculeID, sliderValue ); 
                }
            }
        });
        moleculeToSliderMap.put(moleculeID, slider);
        sliderPanel.add( spacerPanel );
        sliderPanel.add( slider );
        panel.add( sliderPanel );
    }
    
    /**
     * Creates a selector panel with a radio button and an icon and "attaches"
     * it to the model in the sense that it hooks it up to set the appropriate
     * value when pressed and updates its state when the model sends
     * notifications of changes.  This is a convenience method that exists in
     * order to avoid duplication of code.
     * @param toolTipText TODO
     */
    private RadioButtonWithIconPanel createAndAttachSelectorPanel(String text, String toolTipText, 
            BufferedImage image, final PhotonTarget photonTarget, double imageScaleFactor){
        
        // Create the panel.
        final RadioButtonWithIconPanel panel =  new RadioButtonWithIconPanel( text, toolTipText, image, imageScaleFactor );
        
        // Listen to the button so that the specified value can be set in the
        // model when the button is pressed.
        panel.getButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if (panel.getButton().isSelected()){
                    model.setPhotonTarget( photonTarget );
                }
            }
        });
        
        // Listen to the model so that the button state can be updated when
        // the model setting changes.
        model.addListener( new PhotonAbsorptionModel.Adapter(){
            @Override
            public void photonTargetChanged() {
                // The logic in these statements is a little hard to follow,
                // but the basic idea is that if the state of the model
                // doesn't match that of the button, update the button,
                // otherwise leave the button alone.  This prevents a bunch
                // of useless notifications from going to the model.
                if ((model.getPhotonTarget() == photonTarget) != panel.getButton().isSelected()){
                    panel.getButton().setSelected( model.getPhotonTarget() == photonTarget );
                }
            }
        });
        return panel;
    }

    private JPanel createVerticalSpacingPanel(int space){
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }
    
    private BufferedImage createImageFromMolecule(Molecule molecule){
        return new MoleculeNode(molecule, MVT).getImage();
    }
    
    // ------------------------------------------------------------------------
    // Inner Classes and Interfaces
    //------------------------------------------------------------------------

    /**
     * This class combines an icon and a radio button on to a panel in the way
     * that is needed for this simulation.
     */
    private static class RadioButtonWithIconPanel extends HorizontalLayoutPanel {
        
        // Font to use for labels.
        private static final Font LABEL_FONT = new PhetFont(14);
        
        // Fixed height for the panels.
        private static final int PREFERRED_HEIGHT = 42;  // In pixels.
        
        private JRadioButton button;

        /**
         * Constructor.
         * 
         * @param buttonText
         * @param toolTipText TODO
         * @param imageName
         */
        public RadioButtonWithIconPanel(String buttonText, String toolTipText, BufferedImage image,
                double imageScalingFactor){
            
            // Create and add the button.
            button = new JRadioButton(buttonText);
            button.setFont(LABEL_FONT);
            button.setToolTipText( toolTipText );
            add(button);
            
            setPreferredSize( new Dimension(getPreferredSize().width, PREFERRED_HEIGHT ) );
            
            // Create and add the image.
            BufferedImage scaledImage = BufferedImageUtils.multiScale( image, imageScalingFactor );
            ImageIcon imageIcon = new ImageIcon( scaledImage );
            JLabel iconImageLabel = new JLabel( imageIcon );
            iconImageLabel.setToolTipText( toolTipText );
            add( iconImageLabel );
            
            // Add a listener to the image that essentially makes it so that
            // clicking on the image is the same as clicking on the button.
            iconImageLabel.addMouseListener( new MouseAdapter(){
                public void mouseReleased(MouseEvent e) {
                    button.doClick();
                }
            });
        }
        
        public JRadioButton getButton(){
            return button;
        }
    }
}