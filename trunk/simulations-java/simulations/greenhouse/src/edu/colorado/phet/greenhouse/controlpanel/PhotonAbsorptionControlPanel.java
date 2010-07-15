/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Color;
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
import edu.colorado.phet.greenhouse.model.CH4;
import edu.colorado.phet.greenhouse.model.CO2;
import edu.colorado.phet.greenhouse.model.H2O;
import edu.colorado.phet.greenhouse.model.Molecule;
import edu.colorado.phet.greenhouse.model.N2;
import edu.colorado.phet.greenhouse.model.N2O;
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
    
    // Model view transform used for creating images of the various molecules.
    // This is basically a null transform except that it flips the Y axis so
    // that molecules are oriented the same as in the play area.
    private static final ModelViewTransform2D MVT =
        new ModelViewTransform2D( new Point2D.Double(0, 0), new Point(0, 0), 1, true);

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
    private RadioButtonWithIconPanel earthAtmospherSelector;
    private RadioButtonWithIconPanel venusAtmosphereSelector;

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
        co2Selector = createAndAttachSelectorPanel( "CO2", createImageFromMolecule( new CO2() ), PhotonTarget.CO2 );
        greenhouseGasPanel.add(co2Selector);
        
        h2oSelector = createAndAttachSelectorPanel( "H2O", createImageFromMolecule( new H2O() ), PhotonTarget.H2O );
        greenhouseGasPanel.add(h2oSelector);
        
        ch4Selector = createAndAttachSelectorPanel( "CH4", createImageFromMolecule( new CH4() ), PhotonTarget.CH4 );
        greenhouseGasPanel.add(ch4Selector);
        
        n2oSelector = createAndAttachSelectorPanel( "N2O", createImageFromMolecule( new N2O() ), PhotonTarget.N2O );
        greenhouseGasPanel.add(n2oSelector);
        
        // Create and add a panel that will contain the buttons for selecting
        // the non-greenhouse gasses.
        VerticalLayoutPanel otherGas = new VerticalLayoutPanel();
        // TODO: i18n
        otherGas.setBorder(createTitledBorder("Other Gas"));
        addControlFullWidth(otherGas);
        
        n2Selector = createAndAttachSelectorPanel( "N2", createImageFromMolecule( new N2() ), PhotonTarget.N2 );
        otherGas.add(n2Selector);
        
        o2Selector = createAndAttachSelectorPanel( "O2", createImageFromMolecule( new O2() ), PhotonTarget.O2 );
        otherGas.add(o2Selector);

        // Create and add a panel that will contain the buttons for selecting
        // the atmosphere.
        VerticalLayoutPanel atmosphere = new VerticalLayoutPanel();
        // TODO: i18n
        atmosphere.setBorder(createTitledBorder("Atmosphere"));
        addControlFullWidth(atmosphere);
        
        // TODO: i18n
        earthAtmospherSelector = createAndAttachSelectorPanel( "Earth", createImageFromMolecule( new CO2() ), PhotonTarget.EARTH_AIR );
        atmosphere.add(earthAtmospherSelector);
        // TODO: i18n
        venusAtmosphereSelector = createAndAttachSelectorPanel( "Venus", createImageFromMolecule( new CO2() ), PhotonTarget.VENUS_AIR );
        atmosphere.add(venusAtmosphereSelector);

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
     * value when pressed and select or deselects when the model sends
     * notifications of changes.  This is a convenience method that exists in
     * order to avoid duplication of code.
     */
    private RadioButtonWithIconPanel createAndAttachSelectorPanel(String text, BufferedImage image, final PhotonTarget photonTarget){
        
        // Create the panel.
        final RadioButtonWithIconPanel panel =  new RadioButtonWithIconPanel( text, image );
        
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
                // otherwise leave the button alone.  The prevents a bunch
                // of useless notifications from going to the model.
                if ((photonAbsorptionModel.getPhotonTarget() == photonTarget) != panel.getButton().isSelected()){
                    panel.getButton().setSelected( photonAbsorptionModel.getPhotonTarget() == photonTarget );
                }
            }
        });
        return panel;
    }

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
    private static class RadioButtonWithIconPanel extends HorizontalLayoutPanel {
        
        // Font to use for labels.
        private static final Font LABEL_FONT = new PhetFont(14);
        
        // Fixed scale factor for images.
        private static final double IMAGE_SCALING_FACTOR = 0.1;
        
        private JRadioButton button;

        /**
         * Constructor.
         * 
         * @param text
         * @param imageName
         */
        public RadioButtonWithIconPanel(String text, BufferedImage image){
            
            // Create and add the button.
            button = new JRadioButton(text);
            button.setFont(LABEL_FONT);
            add(button);
            
            // Create and add the image.
            BufferedImage scaledImage = BufferedImageUtils.multiScale( image, IMAGE_SCALING_FACTOR );
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