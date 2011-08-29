// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.ToolboxCanvas;
import edu.colorado.phet.sugarandsaltsolutions.GlobalState;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Dispenser;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.colorado.phet.sugarandsaltsolutions.common.view.faucet.FaucetNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;
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

    //Store a reference to the EvaporationSlider for layout purposes
    protected final EvaporationSlider evaporationSlider;

    public SugarAndSaltSolutionsCanvas( final SugarAndSaltSolutionModel model, final GlobalState globalState, final ModelViewTransform transform,

                                        //This flag indicates whether it is the micro or macro tab since different images are used depending on the tab
                                        boolean micro ) {

        //Set the stage size according to the same aspect ratio as used in the model
        stageSize = new PDimension( canvasSize.width, (int) ( canvasSize.width / model.visibleRegion.width * model.visibleRegion.height ) );

        //Gets the ModelViewTransform used to go between model coordinates (SI) and stage coordinates (roughly pixels)
        this.transform = transform;

        // Root of the scene graph in stage coordinates (scaled with the window size)
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

        //Add the reset all button
        addChild( new ResetAllButtonNode( stageSize.getWidth(), stageSize.getHeight(), new VoidFunction0() {
            public void apply() {
                model.reset();
            }
        } ) );

        //Show the water flowing out of the top and bottom faucets
        addChild( new WaterNode( transform, model.inputWater ) );
        addChild( new WaterNode( transform, model.outputWater ) );

        //Add the faucets, the first faucet should have the water stop at the base of the beaker.  This faucet should extend very far in case the user makes the sim short and fat, so the faucet pipe will always be visible
        FaucetNode inputFaucetNode = new FaucetNode( model.inputFlowRate, not( model.beakerFull ), 10000 ) {{
            setOffset( 50, 10 );
        }};
        addChild( inputFaucetNode );
        model.setInputFaucetMetrics( new FaucetMetrics( transform, model, rootNode, inputFaucetNode ) );

        //Add a faucet that drains the beaker; there is no input pipe for this since it attaches directly to the beaker
        //Move it far enough from the beaker that the slider isn't touching it, but not so far that the flowing water would overlap the reset all button
        final double distanceFromBeaker = 12;
        drainFaucetNode = new FaucetNode( model.outputFlowRate, model.lowerFaucetCanDrain, distanceFromBeaker ) {{

            //Move it up by the height of the faucet image, otherwise it sticks out underneath the beaker
            //x-value hand tuned so it doesn't overlap the reset button in English
            //y-value hand tuned so the bottom of the faucet input pipe lines up with the bottom of the water when at the minimum fluid level
            Point2D beakerBottomRight = model.beaker.getOutputFaucetAttachmentPoint();
            Point2D beakerBottomRightView = transform.modelToView( beakerBottomRight );
            setOffset( beakerBottomRightView.getX() + 7 + distanceFromBeaker, beakerBottomRightView.getY() - getFullBounds().getHeight() * 0.8 );
        }};
        addChild( drainFaucetNode );

        //Use the view coordinates to set the model coordinates for how particle should flow toward and flow out the drain pipe
        //But make sure the output drain input point is within the fluid so particles can reach it
        Rectangle2D.Double fullShape = model.beaker.getWaterShape( 0, model.beaker.getMaxFluidVolume() );
        model.setDrainFaucetMetrics( new FaucetMetrics( transform, model, rootNode, drainFaucetNode ).clampInputWithinFluid( fullShape.getMaxX() - fullShape.getWidth() * 0.02 ) );

        //Add a node for children that should be behind the shakers
        behindShakerNode = new PNode();
        addChild( behindShakerNode );

        //add the salt and sugar dispenser nodes, which should always be in front of everything
        for ( Dispenser dispenser : model.dispensers ) {
            submergedInWaterNode.addChild( dispenser.createNode( transform, model.beaker.getHeight(), micro ) );
        }

        //Add beaker node that shows border of the beaker and tick marks
        final BeakerNode node = new BeakerNodeWithTicks( transform, model.beaker );
        addChild( node );

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

        //Add an evaporation rate slider below the beaker
        evaporationSlider = new EvaporationSlider( model.evaporationRate ) {{
            Point2D point = SugarAndSaltSolutionsCanvas.this.transform.modelToView( 0, -model.beaker.getWallThickness() / 2 );
            setOffset( point.getX() - getFullBounds().getWidth() / 2, point.getY() + INSET );
        }};
        addChild( evaporationSlider );

        //Add a graphic to show where particles will flow out the drain
        addChild( new DrainFaucetNodeLocationDebugger( transform, model ) );
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