// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.ConductivityTesterNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsConfig;
import edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Salt;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Sugar;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.model.property.Not.not;
import static edu.colorado.phet.sugarandsaltsolutions.common.model.DispenserType.SALT;

/**
 * Canvas for the introductory (first) tab in the Sugar and Salt Solutions Sim
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsCanvas extends PhetPCanvas {
    //Root node that shows the nodes in the stage coordinate frame
    private final PNode rootNode;

    //Insets to be used for padding between edge of canvas and controls, or between controls
    protected final int INSET = 5;

    //Fonts
    public static Font CONTROL_FONT = new PhetFont( 16 );
    public static Font TITLE_FONT = new PhetFont( 16, true );
    private final PNode crystalLayer = new PNode();//Layer that holds the sugar and salt crystals

    protected final ControlPanelNode soluteControlPanelNode;
    private final ControlPanelNode toolsControlPanelNode;
    private final SugarAndSaltSolutionModel model;
    protected final PDimension stageSize;
    private final ModelViewTransform transform;

    //Actual size of the canvas coming up on windows from the IDE (with tabs) is java.awt.Dimension[width=1008,height=676].
    //This field is public so the model can use the same aspect ratio (to simplify layout and minimize blank regions)
    public static final Dimension canvasSize = new Dimension( 1008, 676 );

    //Other content that should go behind the shakers
    protected PNode behindShakerNode;

    public SugarAndSaltSolutionsCanvas( final SugarAndSaltSolutionModel model, final ObservableProperty<Boolean> removeSaltSugarButtonVisible, final SugarAndSaltSolutionsConfig config ) {
        this.model = model;

        //Set the stage size according to the same aspect ratio as used in the model
        stageSize = new PDimension( canvasSize.width,
                                    (int) ( canvasSize.width / model.visibleRegion.width * model.visibleRegion.height ) );

        //Gets the ModelViewTransform used to go between model coordinates (SI) and stage coordinates (roughly pixels)
        //Create the transform from model (SI) to view (stage) coordinates
        double modelScale = 0.75;//Scale the model down so there will be room for control panels.
        transform = ModelViewTransform.createRectangleInvertedYMapping( model.visibleRegion.toRectangle2D(),
                                                                        //Manually tuned so that the model part shows up in the left side of the canvas,
                                                                        // leaving enough room for controls, labels, and positioning it so it appears near the bottom
                                                                        new Rectangle2D.Double( 20,
                                                                                                135,//increasing this number moves down the beaker
                                                                                                canvasSize.width * modelScale, canvasSize.height * modelScale ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        //Use the background color specified in the backgroundColor, since it is changeable in the developer menu
        config.backgroundColor.addObserver( new VoidFunction1<Color>() {
            public void apply( Color color ) {
                setBackground( config.backgroundColor.get() );
            }
        } );

        //Set the transform from stage coordinates to screen coordinates
        setWorldTransformStrategy( new CenteredStage( this, stageSize ) );

        soluteControlPanelNode = new ControlPanelNode( new VBox() {{
            addChild( new PText( "Solute" ) {{setFont( TITLE_FONT );}} );
            addChild( new PhetPPath( new Rectangle( 0, 0, 0, 0 ), new Color( 0, 0, 0, 0 ) ) );//spacer
            addChild( new PSwing( new VerticalLayoutPanel() {{
                add( new PropertyRadioButton<DispenserType>( "Salt", model.dispenserType, SALT ) {{setFont( CONTROL_FONT );}} );
                add( new PropertyRadioButton<DispenserType>( "Sugar", model.dispenserType, DispenserType.SUGAR ) {{setFont( CONTROL_FONT );}} );
            }} ) );
        }} ) {{
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - INSET, 150 );
        }};
        addChild( soluteControlPanelNode );

        toolsControlPanelNode = new ControlPanelNode( new VBox() {{
            //Add title and a spacer below it
            addChild( new PText( "Tools" ) {{setFont( TITLE_FONT );}} );
            addChild( new PhetPPath( new Rectangle( 0, 0, 0, 0 ), new Color( 0, 0, 0, 0 ) ) );//spacer

            //Add the controls in the control panel
            addChild( new PSwing( new VerticalLayoutPanel() {{
                //Add a button that shows a conductivity meter, with probes that can be submerged
                add( new PropertyCheckBox( "Measure conductivity", model.conductivityTester.visible ) {{setFont( CONTROL_FONT );}} );
            }} ) );
        }} ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth(), soluteControlPanelNode.getFullBounds().getMaxY() + INSET );
        }};
        addChild( toolsControlPanelNode );

        //Add the reset all button
        addChild( new ButtonNode( "Reset All", Color.yellow ) {{
            setOffset( stageSize.width - getFullBounds().getWidth() - INSET, stageSize.height - getFullBounds().getHeight() - INSET );
            setFont( CONTROL_FONT );
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
        addChild( new FaucetNode( transform, model.outputFlowRate, new None<Double>(), not( model.beakerEmpty ), new Point2D.Double( 0, 0 ) ) {{
            Point2D beakerBottomRight = model.beaker.getOutputFaucetAttachmentPoint();
            Point2D beakerBottomRightView = transform.modelToView( beakerBottomRight );
            //Move it up by the height of the faucet image, otherwise it sticks out underneath the beaker
            setOffset( beakerBottomRightView.getX() - getFullBounds().getWidth() * 0.4, //Hand tuned so it doesn't overlap the reset button in English
                       beakerBottomRightView.getY() - getFullBounds().getHeight() );
        }} );

        //Add salt crystals graphics when salt crystals are added to the model
        model.saltAdded.addListener( new CrystalMaker<Salt>( transform, crystalLayer, new Function1<Salt, PNode>() {
            public PNode apply( Salt salt ) {
                return new SaltNode( transform, salt, config.saltColor );
            }
        } ) );

        //Add sugar crystals graphics when sugar crystals are added to the model
        model.sugarAdded.addListener( new CrystalMaker<Sugar>( transform, crystalLayer, new Function1<Sugar, PNode>() {
            public PNode apply( Sugar sugar ) {
                return new SugarNode( transform, sugar, config.saltColor );
            }
        } ) );

        //Add a node for children that should be behind the shakers
        behindShakerNode = new PNode();
        addChild( behindShakerNode );

        //add the salt and sugar dispenser nodes, which should always be in front of everything
        addChild( new SaltShakerNode( transform, model.saltShaker ) );
        addChild( new SugarDispenserNode( transform, model.sugarDispenser ) );

        //Show the crystal layer behind the water and beaker so the crystals look like they go into the water instead of in front of it.
        addChild( crystalLayer );

        //Add beaker and water nodes
        final BeakerNode beakerNode = new BeakerNode( transform, model.beaker );
        addChild( beakerNode );
        addChild( new WaterNode( transform, model.water ) );

        //Add a button that allows the user to remove all solutes
        addChild( new ButtonNode( "Remove salt/sugar" ) {{
            //Button should be inside the beaker
            setOffset( beakerNode.getFullBounds().getMaxX() - getFullBounds().getWidth() - INSET,
                       beakerNode.getFullBounds().getMaxY() - getFullBounds().getHeight() - INSET );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    model.removeSaltAndSugar();
                }
            } );
            removeSaltSugarButtonVisible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );

        //Add the graphic for the conductivity tester--the probes can be submerged to light the bulb
        behindShakerNode.addChild( new ConductivityTesterNode( model.conductivityTester, transform, Color.lightGray, Color.red, Color.green, false ) {{
            model.conductivityTester.visible.addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean visible ) {
                    setVisible( visible );
                }
            } );
        }} );

        //Add an evaporation rate slider below the beaker
        addChild( new EvaporationSlider( model.evaporationRate ) {{
            Point2D point = transform.modelToView( 0, -model.beaker.getWallWidth() / 2 );
            setOffset( point.getX() - getFullBounds().getWidth() / 2, point.getY() + INSET );
        }} );

        //Debug for showing stage
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, stageSize.getWidth(), stageSize.getHeight() ), new BasicStroke( 2 ), Color.red ) );
    }

    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    protected double getControlPanelMinX() {
        return Math.min( soluteControlPanelNode.getFullBoundsReference().getMinX(), toolsControlPanelNode.getFullBoundsReference().getMinX() );
    }
}