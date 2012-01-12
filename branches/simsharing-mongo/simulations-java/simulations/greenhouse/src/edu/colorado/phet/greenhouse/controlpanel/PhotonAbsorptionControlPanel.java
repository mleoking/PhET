// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.greenhouse.controlpanel;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.PhetTitledPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.photonabsorption.model.Molecule;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel;
import edu.colorado.phet.common.photonabsorption.model.PhotonAbsorptionModel.PhotonTarget;
import edu.colorado.phet.common.photonabsorption.model.molecules.CH4;
import edu.colorado.phet.common.photonabsorption.model.molecules.CO2;
import edu.colorado.phet.common.photonabsorption.model.molecules.H2O;
import edu.colorado.phet.common.photonabsorption.model.molecules.N2;
import edu.colorado.phet.common.photonabsorption.model.molecules.O2;
import edu.colorado.phet.common.photonabsorption.view.MoleculeNode;
import edu.colorado.phet.common.photonabsorption.view.MoleculeSelectorPanelWithToolTip;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.greenhouse.GreenhouseResources;

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
            new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point( 0, 0 ), 1, true );

    // Image scaling factors, determined empirically.
    private static final double MOLECULE_SCALING_FACTOR = 0.13;
    private static final double PLANET_SCALING_FACTOR = 0.24;

    // ------------------------------------------------------------------------
    // Instance Data
    // ------------------------------------------------------------------------

    private final PhotonAbsorptionModel model;

    private final Map<Class<? extends Molecule>, LinearValueControl> moleculeToSliderMap = new HashMap<Class<? extends Molecule>, LinearValueControl>();

    // The following data structure defines each of the gas selectors
    // that will exist on this control panel.
    private final ArrayList<MoleculeSelectorPanelWithToolTip> gasSelectors =
            new ArrayList<MoleculeSelectorPanelWithToolTip>();

    // ------------------------------------------------------------------------
    // Constructor(s)
    // ------------------------------------------------------------------------

    public PhotonAbsorptionControlPanel( PiccoloModule module, final PhotonAbsorptionModel model ) {

        this.model = model;

        model.addListener( new PhotonAbsorptionModel.Adapter() {
            @Override
            public void photonTargetChanged() {
                updateSliderEnabledState();
            }

            @Override
            public void configurableAtmosphereCompositionChanged() {
                updateSliderPositions();
            }
        } );

        // Set the control panel's minimum width.
        int minimumWidth = GreenhouseResources.getInt( "int.minControlPanelWidth", 215 );
        setMinimumWidth( minimumWidth );

        // Create and add a panel that will contain the buttons for selecting
        // the gas.
        PhetTitledPanel atmosphericGasesPanel = new PhetTitledPanel( GreenhouseResources.getString( "ControlPanel.AtmosphericGasesTitle" ) );
        atmosphericGasesPanel.setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets( 0, 0, 0, 0 ), 0, 0 );
        addControlFullWidth( atmosphericGasesPanel );

        gasSelectors.add( new MoleculeSelectorPanelWithToolTip( GreenhouseResources.getString( "ControlPanel.CH4" ), GreenhouseResources.getString( "ControlPanel.Methane" ), createMoleculeImage( new CH4(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_CH4_MOLECULE ) );
        gasSelectors.add( new MoleculeSelectorPanelWithToolTip( GreenhouseResources.getString( "ControlPanel.CO2" ), GreenhouseResources.getString( "ControlPanel.CarbonDioxide" ), createMoleculeImage( new CO2(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_CO2_MOLECULE ) );
        gasSelectors.add( new MoleculeSelectorPanelWithToolTip( GreenhouseResources.getString( "ControlPanel.H2O" ), GreenhouseResources.getString( "ControlPanel.Water" ), createMoleculeImage( new H2O(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_H2O_MOLECULE ) );
        gasSelectors.add( new MoleculeSelectorPanelWithToolTip( GreenhouseResources.getString( "ControlPanel.N2" ), GreenhouseResources.getString( "ControlPanel.Nitrogen" ), createMoleculeImage( new N2(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_N2_MOLECULE ) );
        gasSelectors.add( new MoleculeSelectorPanelWithToolTip( GreenhouseResources.getString( "ControlPanel.O2" ), GreenhouseResources.getString( "ControlPanel.Oxygen" ), createMoleculeImage( new O2(), MOLECULE_SCALING_FACTOR ), model, PhotonTarget.SINGLE_O2_MOLECULE ) );
        gasSelectors.add( new MoleculeSelectorPanelWithToolTip( GreenhouseResources.getString( "ControlPanel.BuildAtmosphere" ), null, BufferedImageUtils.multiScale( GreenhouseResources.getImage( "earth.png" ), PLANET_SCALING_FACTOR ), model, PhotonTarget.CONFIGURABLE_ATMOSPHERE ) );

        // Add the molecule selection panels to the main panel.
        int interSelectorSpacing = 2;
        ButtonGroup buttonGroup = new ButtonGroup();
        for ( MoleculeSelectorPanelWithToolTip moleculeSelector : gasSelectors ) {
            atmosphericGasesPanel.add( createVerticalSpacingPanel( interSelectorSpacing ), constraints );
            atmosphericGasesPanel.add( moleculeSelector, constraints );
            buttonGroup.add( moleculeSelector.getRadioButton() ); // This prevent toggling when clicking same button twice.
        }

        atmosphericGasesPanel.add( createVerticalSpacingPanel( interSelectorSpacing ), constraints );

        // Create and add a panel that will contain the sliders for
        // configuring the atmosphere.
        JPanel atmosphereSliderPanel = new JPanel( new GridLayout( 5, 1, 0, 2 ) );

        // Add the molecule control sliders.
        atmosphereSliderPanel.add( new MoleculeQuantitySlider( "ControlPanel.CH4", CH4.class, model, moleculeToSliderMap ) );
        atmosphereSliderPanel.add( new MoleculeQuantitySlider( "ControlPanel.CO2", CO2.class, model, moleculeToSliderMap ) );
        atmosphereSliderPanel.add( new MoleculeQuantitySlider( "ControlPanel.H2O", H2O.class, model, moleculeToSliderMap ) );
        atmosphereSliderPanel.add( new MoleculeQuantitySlider( "ControlPanel.N2", N2.class, model, moleculeToSliderMap ) );
        atmosphereSliderPanel.add( new MoleculeQuantitySlider( "ControlPanel.O2", O2.class, model, moleculeToSliderMap ) );

        // Add the panel that now contains all of the sliders.
        atmosphericGasesPanel.add( atmosphereSliderPanel, constraints );

        // Add the reset all button.
        addControlFullWidth( createVerticalSpacingPanel( 5 ) );
        addResetAllButton( module );

        // Synchronize the controls with the model.
        updateSliderPositions();
        updateSliderEnabledState();
    }

    // ------------------------------------------------------------------------
    // Methods
    // ------------------------------------------------------------------------

    private void updateSliderPositions() {
        for ( Class<? extends Molecule> moleculeClass : moleculeToSliderMap.keySet() ) {
            moleculeToSliderMap.get( moleculeClass ).setValue( model.getConfigurableAtmosphereGasLevel( moleculeClass ) );
        }
    }

    private void updateSliderEnabledState() {
        boolean slidersEnabled = model.getPhotonTarget() == PhotonTarget.CONFIGURABLE_ATMOSPHERE;
        for ( LinearValueControl linearValueControl : moleculeToSliderMap.values() ) {
            linearValueControl.setEnabled( slidersEnabled );
        }
    }

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

    /**
     * Convenience class that assembles the things that are needed for the
     * sliders that control the amount of a given gas in the atmosphere.
     */
    private static class MoleculeQuantitySlider extends JPanel {

        private MoleculeQuantitySlider( String labelID, final Class<? extends Molecule> moleculeClass,
                                        final PhotonAbsorptionModel model, Map<Class<? extends Molecule>, LinearValueControl> moleculeToSliderMap ) {
            setLayout( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            // The overall width of this panel needs to be a little less that the
            // total control panel so that it can be enclosed in a border.  Also,
            // it needs to be indented slightly to convey the idea that it is a
            // sub-function of the "Build Atmosphere" selection.
            int overallWidth = GreenhouseResources.getInt( "int.minControlPanelWidth", 215 ) - 8;
            int indent = 20; // Arbitrary value, adjust as needed for optimal appearance.
            JPanel sliderPanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 0, 0 ) );
            JPanel spacerPanel = new JPanel();
            spacerPanel.setLayout( new BoxLayout( spacerPanel, BoxLayout.X_AXIS ) );
            spacerPanel.add( Box.createHorizontalStrut( indent ) );

            final LinearValueControl slider = new LinearValueControl( 0,
                                                                      model.getConfigurableAtmosphereMaxLevel( moleculeClass ),
                                                                      GreenhouseResources.getString( labelID ),
                                                                      "###",
                                                                      GreenhouseResources.getString( "ControlPanel.Molecules" ) );
            slider.setFont( LABEL_FONT );
            slider.setUpDownArrowDelta( 1 );
            slider.setTextFieldEditable( true );
            slider.setMajorTicksVisible( false );
            slider.setValue( model.getConfigurableAtmosphereGasLevel( moleculeClass ) );
            slider.setSliderWidth( overallWidth - indent );
            slider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    int currentLevel = model.getConfigurableAtmosphereGasLevel( moleculeClass );
                    int sliderValue = (int) Math.round( slider.getValue() );
                    if ( sliderValue != currentLevel ) {
                        model.setConfigurableAtmosphereGasLevel( moleculeClass, sliderValue );
                    }
                }
            } );
            moleculeToSliderMap.put( moleculeClass, slider );
            sliderPanel.add( spacerPanel );
            sliderPanel.add( slider );
            add( sliderPanel );
        }
    }
}