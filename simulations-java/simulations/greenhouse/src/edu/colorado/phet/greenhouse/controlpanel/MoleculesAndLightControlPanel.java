/* Copyright 2010, University of Colorado */

package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.model.CH4;
import edu.colorado.phet.greenhouse.model.CO;
import edu.colorado.phet.greenhouse.model.CO2;
import edu.colorado.phet.greenhouse.model.H2O;
import edu.colorado.phet.greenhouse.model.Molecule;
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
public class MoleculesAndLightControlPanel extends ControlPanel {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // Font to use on this panel.
    private static final Font LABEL_FONT = new PhetFont( 14 );

    // Model view transform used for creating images of the various molecules.
    // This is basically a null transform except that it flips the Y axis so
    // that molecules are oriented the same as in the play area.
    private static final ModelViewTransform2D MVT =
            new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point( 0, 0 ), 1, true );

    // Image scaling factors, determined empirically.
    private static final double MOLECULE_SCALING_FACTOR = 0.13;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final PhotonAbsorptionModel model;

    private final RadioButtonWithIconPanel coSelector;
    private final RadioButtonWithIconPanel n2Selector;
    private final RadioButtonWithIconPanel o2Selector;
    private final RadioButtonWithIconPanel co2Selector;
    private final RadioButtonWithIconPanel h2oSelector;
    private final RadioButtonWithIconPanel no2Selector;
    private final RadioButtonWithIconPanel o3Selector;

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public MoleculesAndLightControlPanel( PiccoloModule module, final PhotonAbsorptionModel model ) {

        this.model = model;

        // Set the control panel's minimum width.
        int minimumWidth = GreenhouseResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        // Create and add a panel that will contain the buttons for selecting
        // the gas.
        PhetTitledPanel atmosphericGasesPanel = new PhetTitledPanel( GreenhouseResources.getString( "PhotonAbsorptionControlPanel.AtmosphericGasesTitle" ) );
        atmosphericGasesPanel.setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        addControlFullWidth( atmosphericGasesPanel );

        // Add buttons for selecting the molecule.
        coSelector = createAndAttachSelectorPanel(
                // TODO: i18n
                "CO",
                "Carbon Monoxide",
                createImageFromMolecule( new CO() ), PhotonTarget.SINGLE_CO_MOLECULE, MOLECULE_SCALING_FACTOR );
        coSelector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( coSelector, constraints );

        n2Selector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "PhotonAbsorptionControlPanel.N2" ),
                GreenhouseResources.getString( "PhotonAbsorptionControlPanel.Nitrogen" ),
                createImageFromMolecule( new N2() ), PhotonTarget.SINGLE_N2_MOLECULE, MOLECULE_SCALING_FACTOR );
        n2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( n2Selector, constraints );

        o2Selector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "PhotonAbsorptionControlPanel.O2" ),
                GreenhouseResources.getString( "PhotonAbsorptionControlPanel.Oxygen" ),
                createImageFromMolecule( new O2() ), PhotonTarget.SINGLE_O2_MOLECULE, MOLECULE_SCALING_FACTOR );
        o2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( o2Selector, constraints );

        co2Selector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "PhotonAbsorptionControlPanel.CO2" ),
                GreenhouseResources.getString( "PhotonAbsorptionControlPanel.CarbonDioxide" ),
                createImageFromMolecule( new CO2() ), PhotonTarget.SINGLE_CO2_MOLECULE, MOLECULE_SCALING_FACTOR );
        co2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( co2Selector, constraints );

        h2oSelector = createAndAttachSelectorPanel(
                GreenhouseResources.getString( "PhotonAbsorptionControlPanel.H2O" ),
                GreenhouseResources.getString( "PhotonAbsorptionControlPanel.Water" ),
                createImageFromMolecule( new H2O() ), PhotonTarget.SINGLE_H2O_MOLECULE, MOLECULE_SCALING_FACTOR );
        h2oSelector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( h2oSelector, constraints );

        no2Selector = createAndAttachSelectorPanel(
                // TODO: i18n
                "NO2",
                "Nitrogen Dioxide",
                createImageFromMolecule( new CH4() ), PhotonTarget.SINGLE_CH4_MOLECULE, MOLECULE_SCALING_FACTOR );
        no2Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( no2Selector, constraints );

        o3Selector = createAndAttachSelectorPanel(
                // TODO: i18n
                "O3",
                "Ozone",
                createImageFromMolecule( new CH4() ), PhotonTarget.SINGLE_CH4_MOLECULE, MOLECULE_SCALING_FACTOR );
        o3Selector.setFont( LABEL_FONT );
        atmosphericGasesPanel.add( o3Selector, constraints );

        // Put all the buttons in a button group.
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( coSelector.getButton() );
        buttonGroup.add( n2Selector.getButton() );
        buttonGroup.add( o2Selector.getButton() );
        buttonGroup.add( co2Selector.getButton() );
        buttonGroup.add( h2oSelector.getButton() );
        buttonGroup.add( no2Selector.getButton() );
        buttonGroup.add( o3Selector.getButton() );

        // Add the reset all button.
        addControlFullWidth( createVerticalSpacingPanel( 5 ) );
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
     * @param toolTipText TODO
     */
    private RadioButtonWithIconPanel createAndAttachSelectorPanel( String text, String toolTipText,
            BufferedImage image, final PhotonTarget photonTarget, double imageScaleFactor ) {

        // Create the panel.
        final RadioButtonWithIconPanel panel = new RadioButtonWithIconPanel( text, toolTipText, image, imageScaleFactor );

        // Listen to the button so that the specified value can be set in the
        // model when the button is pressed.
        panel.getButton().addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                if ( panel.getButton().isSelected() ) {
                    model.setPhotonTarget( photonTarget );
                }
                }
                } );

        // Listen to the model so that the button state can be updated when
        // the model setting changes.
        model.addListener( new PhotonAbsorptionModel.Adapter() {
            @Override
            public void photonTargetChanged() {
                // The logic in these statements is a little hard to follow,
                // but the basic idea is that if the state of the model
                // doesn't match that of the button, update the button,
                // otherwise leave the button alone.  This prevents a bunch
                // of useless notifications from going to the model.
                if ( ( model.getPhotonTarget() == photonTarget ) != panel.getButton().isSelected() ) {
                    panel.getButton().setSelected( model.getPhotonTarget() == photonTarget );
                }
            }
        } );
        return panel;
    }

    private JPanel createVerticalSpacingPanel( int space ) {
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }

    private BufferedImage createImageFromMolecule( Molecule molecule ) {
        return new MoleculeNode( molecule, MVT ).getImage();
    }
}