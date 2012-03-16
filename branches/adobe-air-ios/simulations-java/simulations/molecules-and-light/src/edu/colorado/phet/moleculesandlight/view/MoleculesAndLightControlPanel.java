// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.moleculesandlight.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.photonabsorption.model.Molecule;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel;
import edu.colorado.phet.common.photonabsorption.model.molecules.*;
import edu.colorado.phet.common.photonabsorption.view.MoleculeSelectorPanel;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.photonabsorption.model.molecules.CO2;
import edu.colorado.phet.common.photonabsorption.model.molecules.N2;
import edu.colorado.phet.common.photonabsorption.model.molecules.NO2;
import edu.colorado.phet.common.photonabsorption.model.molecules.O2;
import edu.colorado.phet.common.photonabsorption.model.molecules.O3;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel.PhotonTarget;
import edu.colorado.phet.common.photonabsorption.view.MoleculeNode;
import edu.colorado.phet.moleculesandlight.MoleculesAndLightResources;

/**
 * Control panel for the "Molecules and Light" sim.
 *
 * @author John Blanco
 */
public class MoleculesAndLightControlPanel extends ControlPanel {

    // ------------------------------------------------------------------------
    // Class Data
    // ------------------------------------------------------------------------

    // Model view transform used for creating images of the various molecules.
    // This is basically a null transform except that it flips the Y axis so
    // that molecules on the panel are oriented the same as in the play area.
    private static final ModelViewTransform2D MVT =
            new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point( 0, 0 ), 1, true );

    // Scaling factor for the molecule images, determined empirically.
    private static final double MOLECULE_SCALING_FACTOR = 0.13;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    // The following data structure defines each of the molecule selectors
    // that will exist on this control panel.
    private final ArrayList<MoleculeSelectorPanel> moleculeSelectors = new ArrayList<MoleculeSelectorPanel>();

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public MoleculesAndLightControlPanel( PiccoloModule module, final PhotonAbsorptionModel model ) {

        // Set the control panel's minimum width.
        int minimumWidth = MoleculesAndLightResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        // Create and add the parent molecule selection panel.
        PhetTitledPanel moleculeSelectionPanel = new PhetTitledPanel( MoleculesAndLightResources.getString( "ControlPanel.Molecule" ) );
        moleculeSelectionPanel.setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        addControlFullWidth( moleculeSelectionPanel );

        // Create the selector panels for each molecule and put them on a list.
        moleculeSelectors.add( new MoleculeSelectorPanel( MoleculesAndLightResources.getString("ControlPanel.CarbonMonoxide"),MoleculesAndLightResources.getString("ControlPanel.CO"), createMoleculeImage( new CO(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_CO_MOLECULE ));
        moleculeSelectors.add( new MoleculeSelectorPanel( MoleculesAndLightResources.getString("ControlPanel.Nitrogen"), MoleculesAndLightResources.getString("ControlPanel.N2"), createMoleculeImage( new N2(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_N2_MOLECULE ));
        moleculeSelectors.add( new MoleculeSelectorPanel( MoleculesAndLightResources.getString("ControlPanel.Oxygen"), MoleculesAndLightResources.getString("ControlPanel.O2"), createMoleculeImage( new O2(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_O2_MOLECULE ));
        moleculeSelectors.add( new MoleculeSelectorPanel( MoleculesAndLightResources.getString("ControlPanel.CarbonDioxide"), MoleculesAndLightResources.getString("ControlPanel.CO2"), createMoleculeImage( new CO2(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_CO2_MOLECULE ));
        moleculeSelectors.add( new MoleculeSelectorPanel( MoleculesAndLightResources.getString("ControlPanel.Water"), MoleculesAndLightResources.getString("ControlPanel.H2O"), createMoleculeImage( new H2O(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_H2O_MOLECULE ));
        moleculeSelectors.add( new MoleculeSelectorPanel( MoleculesAndLightResources.getString("ControlPanel.NitrogenDioxide"), MoleculesAndLightResources.getString("ControlPanel.NO2"), createMoleculeImage( new NO2(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_NO2_MOLECULE ));
        moleculeSelectors.add( new MoleculeSelectorPanel( MoleculesAndLightResources.getString("ControlPanel.Ozone"), MoleculesAndLightResources.getString("ControlPanel.O3"), createMoleculeImage( new O3(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_O3_MOLECULE ));

        // Add the molecule selection panels to the main panel.

        int interSelectorSpacing = 15;
        ButtonGroup buttonGroup = new ButtonGroup();

        for ( MoleculeSelectorPanel moleculeSelector : moleculeSelectors ){
            moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );
            moleculeSelectionPanel.add(  moleculeSelector, constraints );
            buttonGroup.add( moleculeSelector.getRadioButton() ); // This prevent toggling when clicking same button twice.
        }

        moleculeSelectionPanel.add(  createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        // Add the Reset All button.
        addControlFullWidth( createVerticalSpacingPanel( 5 ) );
        addResetAllButton( module );
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    private JPanel createVerticalSpacingPanel( int space ) {
        JPanel spacePanel = new JPanel();
        spacePanel.setLayout( new BoxLayout( spacePanel, BoxLayout.Y_AXIS ) );
        spacePanel.add( Box.createVerticalStrut( space ) );
        return spacePanel;
    }

    /**
     * Creates a buffered image of a molecule given an instance of a Molecule
     * object.
     *
     * @param molecule
     * @return
     */
    private BufferedImage createMoleculeImage( Molecule molecule, double scaleFactor ) {
        BufferedImage unscaledMoleculeImage = new MoleculeNode( molecule, MVT ).getImage();
        return BufferedImageUtils.multiScale( unscaledMoleculeImage, scaleFactor );
    }
}