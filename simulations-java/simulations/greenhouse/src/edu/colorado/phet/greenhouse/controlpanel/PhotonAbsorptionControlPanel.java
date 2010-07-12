/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.CarbonDioxide;
import edu.colorado.phet.greenhouse.model.Molecule;
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
    
    // Model view transform used for creating images of the various molecules.
    private static final ModelViewTransform2D MVT = new ModelViewTransform2D();

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------
    
    PhotonAbsorptionModel photonAbsorptionModel;

    private RadioButtonWithIcon co2Button;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public PhotonAbsorptionControlPanel (PiccoloModule module, PhotonAbsorptionModel model){

        this.photonAbsorptionModel = model;
        
        // Set the control panel's minimum width.
        int minimumWidth = GreenhouseResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );
        
        // Put some space at the top of the panel.
        addControlFullWidth(createVerticalSpacingPanel(20));
        
        // Create and add a panel that will contain the buttons for selecting
        // the gas.
        VerticalLayoutPanel greenhouseGasPanel = new VerticalLayoutPanel();
        // TODO: i18n
        greenhouseGasPanel.setBorder(createTitledBorder("Greenhouse Gas"));
        addControlFullWidth(greenhouseGasPanel);
        
        // Add buttons for selecting greenhouse gas.
        // TODO: i18n
        JRadioButton h2oButton = new JRadioButton("H2O");
        greenhouseGasPanel.add(h2oButton);
        co2Button = new RadioButtonWithIcon( "CO2", createImageFromMolecule( new CarbonDioxide() ) );
        greenhouseGasPanel.add(co2Button);
        co2Button.getButton().addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                photonAbsorptionModel.setPhotonTarget( PhotonTarget.CO2 );
            }
        });
        photonAbsorptionModel.addListener( new PhotonAbsorptionModel.Adapter(){
            public void photonTargetChanged() {
                if (photonAbsorptionModel.getPhotonTarget() == PhotonTarget.CO2 && !co2Button.getButton().isSelected()){
                    co2Button.setEnabled( true );
                }
            }
        });
        
        
        // TODO: i18n
        JRadioButton ch4Button = new JRadioButton("CH4");
        greenhouseGasPanel.add(ch4Button);
        // TODO: i18n
        JRadioButton n2oButton = new JRadioButton("N2O");
        greenhouseGasPanel.add(n2oButton);
        
        // Create and add a panel that will contain the buttons for selecting
        // the non-greenhouse gasses.
        VerticalLayoutPanel otherGas = new VerticalLayoutPanel();
        // TODO: i18n
        otherGas.setBorder(createTitledBorder("Other Gas"));
        addControlFullWidth(otherGas);
        
        // Add buttons for other gas selection.
        // TODO: i18n
        JRadioButton n2Button = new JRadioButton("N2");
        otherGas.add(n2Button);
        // TODO: i18n
        JRadioButton o2Button = new JRadioButton("O2");
        otherGas.add(o2Button);

        // Create and add a panel that will contain the buttons for selecting
        // the atmosphere.
        VerticalLayoutPanel atmosphere = new VerticalLayoutPanel();
        // TODO: i18n
        atmosphere.setBorder(createTitledBorder("Atmosphere"));
        addControlFullWidth(atmosphere);
        
        // Add buttons for atmosphere selection.
        // TODO: i18n
        JRadioButton earthAtmosphereButton = new JRadioButton("Earth");
        atmosphere.add(earthAtmosphereButton);
        // TODO: i18n
        JRadioButton venusAtmosphereButton = new JRadioButton("Venus");
        atmosphere.add(venusAtmosphereButton);

        // Add the reset all button.
        addControlFullWidth(createVerticalSpacingPanel(60));
        addResetAllButton( module );
    }
    
    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

	private TitledBorder createTitledBorder(String title) {
		BevelBorder otherGasBaseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( otherGasBaseBorder,
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
    private static class RadioButtonWithIcon extends HorizontalLayoutPanel {
        
        // Font to use for labels.
        private static final Font LABEL_FONT = new PhetFont(14);
        
        // Fixed scale factor for images.
        private static final double IMAGE_SCALING_FACTOR = 0.1;
        
        private JRadioButton _button;

        /**
         * Constructor.
         * 
         * @param text
         * @param imageName
         */
        public RadioButtonWithIcon(String text, BufferedImage image){
            
            // Create and add the button.
            _button = new JRadioButton(text);
            _button.setFont(LABEL_FONT);
            add(_button);
            
            // Create and add the image.
            BufferedImage scaledImage = BufferedImageUtils.rescaleFractional( image, IMAGE_SCALING_FACTOR,
                    IMAGE_SCALING_FACTOR );
            ImageIcon imageIcon = new ImageIcon( scaledImage );
            JLabel iconImageLabel = new JLabel( imageIcon );
            add( iconImageLabel );
            
            // Add a listener to the image that essentially makes it so that
            // clicking on the image is the same as clicking on the button.
            iconImageLabel.addMouseListener( new MouseAdapter(){
                public void mouseReleased(MouseEvent e) {
                    _button.doClick();
                }
            });
        }
        
        public JRadioButton getButton(){
            return _button;
        }
    }
}
