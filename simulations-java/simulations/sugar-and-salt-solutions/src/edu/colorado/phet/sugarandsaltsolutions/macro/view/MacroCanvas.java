// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.macro.view;

import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.CrystalMaker;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SaltNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SoluteControlPanelNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas;
import edu.colorado.phet.sugarandsaltsolutions.common.view.SugarNode;
import edu.colorado.phet.sugarandsaltsolutions.common.view.VolumeIndicatorNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.DispenserRadioButtonSet;
import edu.colorado.phet.sugarandsaltsolutions.micro.view.Item;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SUGAR;
import static edu.colorado.phet.sugarandsaltsolutions.common.util.Units.metersCubedToLiters;

/**
 * Canvas for the introductory (macro) tab of sugar and salt solutions
 *
 * @author Sam Reid
 */
public class MacroCanvas extends SugarAndSaltSolutionsCanvas {

    //Separate layer for the conductivity toolbox to make sure the conductivity node shows as submerged in the water, but still goes behind the shaker
    protected final PNode conductivityToolboxLayer = new PNode();

    public final ExpandableConcentrationBarChartNode concentrationBarChart;
    private final PNode soluteControlPanelNode;

    private final PNode crystalLayer = new PNode();//Layer that holds the sugar and salt crystals

    public MacroCanvas( final MacroModel model, final GlobalState globalState ) {
        super( model, globalState, createMacroTransform( model ), false );

        //Show the crystal layer behind the water and beaker so the crystals look like they go into the water instead of in front of it.
        submergedInWaterNode.addChild( crystalLayer );

        //Add salt crystals graphics when salt crystals are added to the model
        model.saltAdded.addListener( new CrystalMaker<MacroSalt>( crystalLayer, new Function1<MacroSalt, PNode>() {
            public PNode apply( MacroSalt salt ) {
                return new SaltNode( transform, salt, globalState.colorScheme.saltColor.color );
            }
        } ) );

        //Add sugar crystals graphics when sugar crystals are added to the model
        model.sugarAdded.addListener( new CrystalMaker<MacroSugar>( crystalLayer, new Function1<MacroSugar, PNode>() {
            public PNode apply( MacroSugar sugar ) {
                return new SugarNode( transform, sugar, globalState.colorScheme.saltColor.color );
            }
        } ) );

//Show the precipitate as the sum of salt and sugar
        submergedInWaterNode.addChild( new PrecipitateNode( transform, model.salt.solidVolume.plus( model.sugar.solidVolume ), model.beaker ) );


        //Readout function for the exact volume readout on the solution when the user selects "show values.
        //Read out more precisely than the fine-grained tick marks on the side
        Function1<Double, String> beakerVolumeReadoutFormat = new Function1<Double, String>() {
            DecimalFormat decimalFormat = new DecimalFormat( "0.00" );

            public String apply( Double volumeInMetersCubed ) {
                return decimalFormat.format( metersCubedToLiters( volumeInMetersCubed ) );
            }
        };

        //Readout the volume of the water in Liters, only visible if the user opted to show values (in the concentration bar chart)
        addChild( new VolumeIndicatorNode( transform, model.solution, model.showConcentrationValues, model.getAnySolutes(), beakerVolumeReadoutFormat ) );

        //This tab uses the conductivity tester
        submergedInWaterNode.addChild( conductivityToolboxLayer );

        //Show the concentration bar chart behind the shaker so the user can drag the shaker in front
        //TODO: why is the scale factor 1 here?
        concentrationBarChart = new ExpandableConcentrationBarChartNode( model.showConcentrationBarChart, model.saltConcentration, model.sugarConcentration, model.showConcentrationValues, 1 ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        behindShakerNode.addChild( concentrationBarChart );

        //Create the control panel for choosing sugar vs salt, use a radio-button-based selector for solutes
        soluteControlPanelNode = new SoluteControlPanelNode( new DispenserRadioButtonSet( model.dispenserType, new Item( Strings.SALT, SALT ), new Item( Strings.SUGAR, SUGAR ) ) );
        soluteControlPanelNode.setOffset( stageSize.getWidth() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, 150 );
        addChild( soluteControlPanelNode );

        soluteControlPanelNode.setOffset( concentrationBarChart.getFullBounds().getX() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getY() );

        //Toolbox from which the conductivity tester can be dragged
        conductivityToolboxLayer.addChild( new ConductivityTesterToolboxNode( model, this ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getMaxY() + INSET );
        }} );

        //Add a control that allows the user to remove solutes
        //Button should be inside the beaker at the bottom right so it doesn't collide with the leftmost tick marks
        addChild( new RemoveSoluteControlNode( model ) {{
            setOffset( transform.modelToViewX( model.beaker.getMaxX() ) - getFullBounds().getWidth() - INSET,
                       transform.modelToViewY( model.beaker.getY() ) - getFullBounds().getHeight() - INSET );
        }} );
    }

    //Create the transform from model (SI) to view (stage) coordinates.  Public and static since it is also used to create the MiniBeakerNode in the Water tab
    public static ModelViewTransform createMacroTransform( SugarAndSaltSolutionModel model ) {
        double modelScale = 0.75;//Scale the model down so there will be room for control panels.
        return createRectangleInvertedYMapping( model.visibleRegion.toRectangle2D(),
                                                //Manually tuned so that the model part shows up in the left side of the canvas,
                                                // leaving enough room for controls, labels, and positioning it so it appears near the bottom
                                                new Rectangle2D.Double( 20,
                                                                        //y-position: increasing this number moves down the beaker
                                                                        135,
                                                                        canvasSize.width * modelScale, canvasSize.height * modelScale ) );
    }
}