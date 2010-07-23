/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
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
import edu.colorado.phet.greenhouse.model.N2O;
import edu.colorado.phet.greenhouse.model.O2;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel;
import edu.colorado.phet.greenhouse.model.PhotonAbsorptionModel.PhotonTarget;
import edu.colorado.phet.greenhouse.view.MoleculeNode;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;

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
    private static final double MOLECULE_SCALING_FACTOR = 0.14;
    private static final double PLANET_SCALING_FACTOR = 0.22;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    PhotonAbsorptionModel photonAbsorptionModel;

    private RadioButtonWithIconPanel co2Selector;
    private RadioButtonWithIconPanel h2oSelector;
    private RadioButtonWithIconPanel ch4Selector;
    private RadioButtonWithIconPanel n2oSelector;
    private RadioButtonWithIconPanel n2Selector;
    private RadioButtonWithIconPanel o2Selector;
    private RadioButtonWithIconPanel atmospherSelector;
    
    private LinearValueControl n2LevelInAtmosphere;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public PhotonAbsorptionControlPanel (PiccoloModule module, final PhotonAbsorptionModel model){

        this.photonAbsorptionModel = model;
        
        // Set the control panel's minimum width.
        int minimumWidth = GreenhouseResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Put some space at the top of the panel.
        addControlFullWidth(createVerticalSpacingPanel(20));
        
        // Create and add a panel that will contain the buttons for selecting
        // the gas.
        VerticalLayoutPanel atmosphericGasesPanel = new VerticalLayoutPanel();
        // TODO: i18n
        atmosphericGasesPanel.setBorder(createTitledBorder("Atmospheric Gases"));
        addControlFullWidth(atmosphericGasesPanel);
        
        // Add buttons for selecting greenhouse gas.
        ch4Selector = createAndAttachSelectorPanel( "<html>CH<sub>4</sub></html>", createImageFromMolecule( new CH4() ), PhotonTarget.SINGLE_CH4_MOLECULE,
                MOLECULE_SCALING_FACTOR );
        ch4Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(ch4Selector);
        
        co2Selector = createAndAttachSelectorPanel( "<html>CO<sub>2</sub></html>", createImageFromMolecule( new CO2() ), PhotonTarget.SINGLE_CO2_MOLECULE,
                MOLECULE_SCALING_FACTOR );
        co2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(co2Selector);
        
        h2oSelector = createAndAttachSelectorPanel( "<html>H<sub>2</sub>O</html>", createImageFromMolecule( new H2O() ), PhotonTarget.SINGLE_H2O_MOLECULE,
                MOLECULE_SCALING_FACTOR );
        h2oSelector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(h2oSelector);
        
        n2Selector = createAndAttachSelectorPanel( "<html>N<sub>2</sub></html>", createImageFromMolecule( new N2() ), PhotonTarget.SINGLE_N2_MOLECULE,
                MOLECULE_SCALING_FACTOR );
        n2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(n2Selector);
        
        n2oSelector = createAndAttachSelectorPanel( "<html>N<sub>2</sub>O</html>", createImageFromMolecule( new N2O() ), PhotonTarget.SINGLE_N2O_MOLECULE,
                MOLECULE_SCALING_FACTOR );
        n2oSelector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(n2oSelector);
        
        o2Selector = createAndAttachSelectorPanel( "<html>O<sub>2</sub></html>", createImageFromMolecule( new O2() ), PhotonTarget.SINGLE_O2_MOLECULE,
                MOLECULE_SCALING_FACTOR );
        o2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add(o2Selector);

        // Create and add a panel that will contain the buttons for selecting
        // the atmosphere.
        VerticalLayoutPanel atmospherePanel = new VerticalLayoutPanel();
        // TODO: i18n
        atmospherePanel.setBorder(createTitledBorder("Atmosphere"));
        addControlFullWidth( atmospherePanel );

        // TODO: i18n
        atmospherSelector = createAndAttachSelectorPanel("Build Atmosphere", GreenhouseResources.getImage( "earth.png" ),
                PhotonTarget.CONFIGURABLE_ATMOSPHERE, PLANET_SCALING_FACTOR);
        atmospherePanel.add(atmospherSelector);
        
        // TODO: i18n
        n2LevelInAtmosphere = new LinearValueControl( 0, model.getConfigurableAtmosphereMaxLevel( MoleculeID.N2 ),
                "N2 Level", "###", "Molecules");
        n2LevelInAtmosphere.setFont( LABEL_FONT );
        n2LevelInAtmosphere.setUpDownArrowDelta( 1 );
        n2LevelInAtmosphere.setTextFieldEditable( true );
        n2LevelInAtmosphere.setMajorTicksVisible( false );
        n2LevelInAtmosphere.setBorder( BorderFactory.createEtchedBorder() );
        n2LevelInAtmosphere.setValue( model.getConfigurableAtmosphereGasLevel( MoleculeID.N2 ) );
        n2LevelInAtmosphere.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                int currentLevel = model.getConfigurableAtmosphereGasLevel( MoleculeID.N2 );
                int sliderValue = (int)Math.round( n2LevelInAtmosphere.getValue() );
                if ( sliderValue != currentLevel ){
                    model.setConfigurableAtmosphereGasLevel( MoleculeID.N2, sliderValue ); 
                }
            }
        });
        atmospherePanel.add(  n2LevelInAtmosphere );

        addControlFullWidth(atmospherePanel);
        
        // Put all the buttons in a button group.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( co2Selector.getButton() );
        buttonGroup.add( h2oSelector.getButton() );
        buttonGroup.add( ch4Selector.getButton() );
        buttonGroup.add( n2oSelector.getButton() );
        buttonGroup.add( n2Selector.getButton() );
        buttonGroup.add( o2Selector.getButton() );
        buttonGroup.add(atmospherSelector.getButton());

        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(60));
        addResetAllButton( module );
    }
    
    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------
    
    /**
     * Creates a selector panel with a radio button and an icon and "attaches"
     * it to the model in the sense that it hooks it up to set the appropriate
     * value when pressed and updates its state when the model sends
     * notifications of changes.  This is a convenience method that exists in
     * order to avoid duplication of code.
     */
    private RadioButtonWithIconPanel createAndAttachSelectorPanel(String text, BufferedImage image, 
            final PhotonTarget photonTarget, double imageScaleFactor){
        
        // Create the panel.
        final RadioButtonWithIconPanel panel =  new RadioButtonWithIconPanel( text, image, imageScaleFactor );
        
        // Listen to the button so that the specified value can be set in the
        // model when the button is pressed.
        panel.getButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if (panel.getButton().isSelected()){
                    photonAbsorptionModel.setPhotonTarget( photonTarget );
                }
            }
        });
        
        // Listen to the model so that the button state can be updated when
        // the model setting changes.
        photonAbsorptionModel.addListener( new PhotonAbsorptionModel.Adapter(){
            @Override
            public void photonTargetChanged() {
                // The logic in these statements is a little hard to follow,
                // but the basic idea is that if the state of the model
                // doesn't match that of the button, update the button,
                // otherwise leave the button alone.  This prevents a bunch
                // of useless notifications from going to the model.
                if ((photonAbsorptionModel.getPhotonTarget() == photonTarget) != panel.getButton().isSelected()){
                    panel.getButton().setSelected( photonAbsorptionModel.getPhotonTarget() == photonTarget );
                }
            }
        });
        return panel;
    }

	private TitledBorder createTitledBorder(String title) {
		BevelBorder beveledBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( beveledBorder,
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
		return titledBorder;
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
        private static final int PREFERRED_HEIGHT = 46;  // In pixels.
        
        private JRadioButton button;

        /**
         * Constructor.
         * 
         * @param text
         * @param imageName
         */
        public RadioButtonWithIconPanel(String text, BufferedImage image, double imageScalingFactor){
            
            // Create and add the button.
            button = new JRadioButton(text);
            button.setFont(LABEL_FONT);
            add(button);
            
            setPreferredSize( new Dimension(getPreferredSize().width, PREFERRED_HEIGHT ) );
            
            // Create and add the image.
            BufferedImage scaledImage = BufferedImageUtils.multiScale( image, imageScalingFactor );
            ImageIcon imageIcon = new ImageIcon( scaledImage );
            JLabel iconImageLabel = new JLabel( imageIcon );
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