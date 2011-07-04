// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.ToolboxCanvas;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSalt;
import edu.colorado.phet.sugarandsaltsolutions.macro.model.MacroSugar;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.ExpandableConcentrationBarChartNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.PrecipitateNode;
import edu.colorado.phet.sugarandsaltsolutions.macro.view.RemoveSoluteControlNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;
import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.STRING_RESET_ALL;
import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.getInstance;
import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;

/**
 * Canvas for the first tab in the Sugar and Salt Solutions Sim
 *
 * @author Sam Reid
 */
public abstract class SugarAndSaltSolutionsCanvas extends PhetPCanvas implements ToolboxCanvas {
    //Root node that shows the nodes in the stage coordinate frame
    private final PNode rootNode;

    //Insets to be used for padding between edge of canvas and controls, or between controls
    public static final int INSET = 5;

    //Fonts
    public static Font CONTROL_FONT = new PhetFont( 16, true );
    public static Font TITLE_FONT = new PhetFont( 18, true );
    private final PNode crystalLayer = new PNode();//Layer that holds the sugar and salt crystals

    protected final WhiteControlPanelNode soluteControlPanelNode;

    protected final PDimension stageSize;
    protected final ModelViewTransform transform;

    //Actual size of the canvas coming up on windows from the IDE (with tabs) is java.awt.Dimension[width=1008,height=676].
    //This field is public so the model can use the same aspect ratio (to simplify layout and minimize blank regions)
    public static final Dimension canvasSize = new Dimension( 1008, 676 );

    //Other content that should go behind the shakers
    protected PNode behindShakerNode;
    private boolean debug = false;

    //For nodes that should look like they go into the water, such as the conductivity tester probes
    public final PNode submergedInWaterNode = new PNode();

    //Color for reset and remove buttons
    public static final Color BUTTON_COLOR = new Color( 255, 153, 0 );

    //Node that shows the faucet, we need a reference so subclasses can listen to the water flowing out bounds for collision hit testing for the conductivity tester
    protected final FaucetNode drainFaucetNode;

    public SugarAndSaltSolutionsCanvas( final SugarAndSaltSolutionModel model, final GlobalState globalState ) {

        //Set the stage size according to the same aspect ratio as used in the model
        stageSize = new PDimension( canvasSize.width,
                                    (int) ( canvasSize.width / model.visibleRegion.width * model.visibleRegion.height ) );

        //Gets the ModelViewTransform used to go between model coordinates (SI) and stage coordinates (roughly pixels)
        transform = createTransform( model );

//        System.out.println( "transform.getTransform().getScaleX() = " + transform.getTransform().getScaleX() );
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        //Use the background color specified in the backgroundColor, since it is changeable in the developer menu
        globalState.colorScheme.backgroundColorSet.color.addObserver( new VoidFunction1<Color>() {
            public void apply( Color color ) {
                setBackground( color );
            }
        } );

        //Set the transform from stage coordinates to screen coordinates
        setWorldTransformStrategy( new CenteredStage( this, stageSize ) );

        //Create the control panel for choosing sugar vs salt
        soluteControlPanelNode = createSoluteControlPanelNode( model, this, stageSize );
        soluteControlPanelNode.setOffset( stageSize.getWidth() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, 150 );
        addChild( soluteControlPanelNode );

        //Add the reset all button
        addChild( new HTMLImageButtonNode( getInstance().getLocalizedString( STRING_RESET_ALL ), BUTTON_COLOR ) {{
            setFont( CONTROL_FONT );

            //Have to set the offset after changing the font since it changes the size of the node
            setOffset( stageSize.width - getFullBounds().getWidth() - INSET, stageSize.height - getFullBounds().getHeight() - INSET );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.reset();
                }
            } );
        }} );

        //Add the faucets, the first faucet should have the water stop at the base of the beaker
        addChild( new FaucetNode( transform, model.inputFlowRate, new Some<Double>( transform.modelToViewY( model.beaker.getY() ) ), not( model.beakerFull ),
                                  //move the top faucet down a little bit, so the slider doesn't go offscreen
                                  new Point2D.Double( 0, 10 ) ) );

        //Add a faucet that drains the beaker, note that the value is assigned in the creation
        addChild( drainFaucetNode = new FaucetNode( transform, model.outputFlowRate, new None<Double>(), model.lowerFaucetCanDrain, new Point2D.Double( 0, 0 ) ) {{
            Point2D beakerBottomRight = model.beaker.getOutputFaucetAttachmentPoint();
            Point2D beakerBottomRightView = transform.modelToView( beakerBottomRight );
            //Move it up by the height of the faucet image, otherwise it sticks out underneath the beaker
            setOffset( beakerBottomRightView.getX() - getFullBounds().getWidth() * 0.4, //Hand tuned so it doesn't overlap the reset button in English
                       beakerBottomRightView.getY() - getFullBounds().getHeight() );
        }} );

        //Add salt crystals graphics when salt crystals are added to the model
        model.saltAdded.addListener( new CrystalMaker<MacroSalt>( transform, crystalLayer, new Function1<MacroSalt, PNode>() {
            public PNode apply( MacroSalt salt ) {
                return new SaltNode( transform, salt, globalState.colorScheme.saltColor.color );
            }
        } ) );

        //Add sugar crystals graphics when sugar crystals are added to the model
        model.sugarAdded.addListener( new CrystalMaker<MacroSugar>( transform, crystalLayer, new Function1<MacroSugar, PNode>() {
            public PNode apply( MacroSugar sugar ) {
                return new SugarNode( transform, sugar, globalState.colorScheme.saltColor.color );
            }
        } ) );

        //Add a node for children that should be behind the shakers
        behindShakerNode = new PNode();
        addChild( behindShakerNode );

        //add the salt and sugar dispenser nodes, which should always be in front of everything
        submergedInWaterNode.addChild( new SaltShakerNode( transform, model.saltShaker, model.beaker.getHeight() ) );
        submergedInWaterNode.addChild( new SugarDispenserNode( transform, model.sugarDispenser, model.beaker.getHeight() ) );

        //Show the crystal layer behind the water and beaker so the crystals look like they go into the water instead of in front of it.
        submergedInWaterNode.addChild( crystalLayer );

        //Add beaker node that shows border of the beaker and tick marks
        addChild( new BeakerNode( transform, model.beaker ) );

        //Debug for showing stage
        if ( debug ) {
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, stageSize.getWidth(), stageSize.getHeight() ), new BasicStroke( 2 ), Color.red ) );

            //Show the model coordinates for clicked points, this can help us come up with good model coordinates for graphics that are mainly positioned in the view, such as faucet connector points
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    System.out.println( "modelPoint = " + transform.viewToModel( event.getPositionRelativeTo( rootNode ) ) );
                }
            } );
        }

        //Show the full water node at the correct color, then overlay a partially transparent one on top, so that some objects (such as the conductivity tester) will look submerged
        addChild( new SolutionNode( transform, model.solution, WATER_COLOR ) );

        //Node that shows things that get submerged such as the conductivity tester
        addChild( submergedInWaterNode );

        //Overlay node that renders as partially transparent in front of submerged objects, such as the conductivity tester.
        //When changing the transparency here make sure it looks good for precipitate as well as submerged probes
        addChild( new SolutionNode( transform, model.solution, new Color( WATER_COLOR.getRed(), WATER_COLOR.getGreen(), WATER_COLOR.getBlue(), 128 ) ) );

        //Readout the volume of the water in Liters, only visible if the user opted to show values (in the concentration bar chart)
        addChild( new VolumeIndicatorNode( transform, model.solution, model.showConcentrationValues, model.solidVolume, model.anySolutes ) );

        //Add a control that allows the user to remove solutes
        final PNode removeSolutesButton = new RemoveSoluteControlNode( model );

        //Button should be inside the beaker at the bottom right so it doesn't collide with the leftmost tick marks
        removeSolutesButton.setOffset( transform.modelToViewX( model.beaker.getMaxX() ) - INSET - removeSolutesButton.getFullBounds().getWidth(),
                                       transform.modelToViewY( model.beaker.getY() ) - removeSolutesButton.getFullBounds().getHeight() - INSET );
        addChild( removeSolutesButton );

        ExpandableConcentrationBarChartNode concentrationBarChart = new ExpandableConcentrationBarChartNode( model.showConcentrationBarChart, model.saltConcentration, model.sugarConcentration, model.showConcentrationValues, 1 ) {{
            setOffset( stageSize.getWidth() - getFullBoundsReference().width - INSET, INSET );
        }};
        behindShakerNode.addChild( concentrationBarChart );

        soluteControlPanelNode.setOffset( concentrationBarChart.getFullBounds().getX() - soluteControlPanelNode.getFullBounds().getWidth() - INSET, concentrationBarChart.getFullBounds().getY() );

        //Add an evaporation rate slider below the beaker
        addChild( new EvaporationSlider( model.evaporationRate ) {{
            Point2D point = SugarAndSaltSolutionsCanvas.this.transform.modelToView( 0, -model.beaker.getWallWidth() / 2 );
            setOffset( point.getX() - getFullBounds().getWidth() / 2, point.getY() + INSET );
        }} );

        //Show the precipitate as the sum of salt and sugar
        submergedInWaterNode.addChild( new PrecipitateNode( transform, model.salt.solidVolume.plus( model.sugar.solidVolume ), model.beaker ) );
    }

    //Create the component used to choose between different solutes.
    //Called from the constructor, should only use arguments passed in
    protected abstract SoluteControlPanelNode createSoluteControlPanelNode( SugarAndSaltSolutionModel model, PSwingCanvas canvas, PDimension stageSize );

    //Create the transform from model (SI) to view (stage) coordinates.  Public and static since it is also used to create the MiniBeakerNode in the Water tab
    public static ModelViewTransform createTransform( SugarAndSaltSolutionModel model ) {
        double modelScale = 0.75;//Scale the model down so there will be room for control panels.
        return createRectangleInvertedYMapping( model.visibleRegion.toRectangle2D(),
                                                //Manually tuned so that the model part shows up in the left side of the canvas,
                                                // leaving enough room for controls, labels, and positioning it so it appears near the bottom
                                                new Rectangle2D.Double( 20,
                                                                        135,//increasing this number moves down the beaker
                                                                        canvasSize.width * modelScale, canvasSize.height * modelScale ) );
    }

    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    public PNode getRootNode() {
        return rootNode;
    }

    public ModelViewTransform getModelViewTransform() {
        return transform;
    }
}