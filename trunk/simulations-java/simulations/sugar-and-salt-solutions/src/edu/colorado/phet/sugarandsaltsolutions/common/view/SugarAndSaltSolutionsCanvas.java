// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.sugarandsaltsolutions.common.model.Salt;
import edu.colorado.phet.sugarandsaltsolutions.common.model.SugarAndSaltSolutionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;
import static javax.swing.SwingUtilities.invokeLater;

/**
 * Canvas for the introductory (first) tab in the Sugar and Salt Solutions Sim
 *
 * @author Sam Reid
 */
public class SugarAndSaltSolutionsCanvas extends PhetPCanvas {
    //Root node that shows the nodes in the stage coordinate frame
    private PNode rootNode;

    //Insets to be used for padding between edge of canvas and controls, or between controls
    private final int INSET = 5;

    //Fonts
    public static Font CONTROL_FONT = new PhetFont( 16 );
    public static Font TITLE_FONT = new PhetFont( 16, true );
    private PNode crystalLayer = new PNode();//Layer that holds the sugar and salt crystals

    public SugarAndSaltSolutionsCanvas( final SugarAndSaltSolutionModel model ) {
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBackground( Color.white );

        //Width of the stage
        final int stageWidth = 1008;//Actual size of the canvas coming up on windows from the IDE is java.awt.Dimension[width=1008,height=676]
        final int stageHeight = (int) ( stageWidth / model.width * model.height );

        //Set the stage size according to the model aspect ratio
        final PDimension stageSize = new PDimension( stageWidth, stageHeight );

        //Set the transform from stage coordinates to screen coordinates
        setWorldTransformStrategy( new CenteredStage( this, stageSize ) );

        //Create the transform from model (SI) to view (stage) coordinates
        final double scale = stageWidth / model.width;
        final ModelViewTransform transform = createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                     new Point2D.Double( stageSize.getWidth() * 0.43, stageSize.getHeight() - 50 ),
                                                                                     scale );

        //Allows the user to select a solute
        final ControlPanelNode soluteControlPanelNode = new ControlPanelNode( new VBox() {{
            addChild( new PText( "Solute" ) {{setFont( TITLE_FONT );}} );
            addChild( new PhetPPath( new Rectangle( 0, 0, 0, 0 ), new Color( 0, 0, 0, 0 ) ) );//spacer
            addChild( new PSwing( new VerticalLayoutPanel() {{
                add( new JRadioButton( "Salt" ) {{setFont( CONTROL_FONT );}} );
                add( new JRadioButton( "Sugar" ) {{setFont( CONTROL_FONT );}} );
            }} ) );
        }} ) {{
            setOffset( stageSize.getWidth() - getFullBounds().getWidth() - INSET, 150 );
        }};
        addChild( soluteControlPanelNode );

        //Tools for the user to use
        final ControlPanelNode toolsControlPanelNode = new ControlPanelNode( new VBox() {{
            //Add title and a spacer below it
            addChild( new PText( "Tools" ) {{setFont( TITLE_FONT );}} );
            addChild( new PhetPPath( new Rectangle( 0, 0, 0, 0 ), new Color( 0, 0, 0, 0 ) ) );//spacer

            //Add the controls in the control panel
            addChild( new PSwing( new VerticalLayoutPanel() {{
                add( new CheckBox( "Show concentration" ) );
                add( new JPanel() {{
                    add( Box.createHorizontalStrut( 10 ) );//Indent the show values a bit since it relates to show concentration box
                    add( new CheckBox( "Show values" ) );
                }} );
                add( new CheckBox( "Measure conductivity" ) );
                add( new CheckBox( "Evaporate water" ) );
            }} ) );
        }} ) {{
            //Set the location of the control panel
            setOffset( stageSize.getWidth() - getFullBounds().getWidth(), soluteControlPanelNode.getFullBounds().getMaxY() + INSET );
        }};
        addChild( toolsControlPanelNode );

        //Add the reset all button
        addChild( new ButtonNode( "Reset All", Color.yellow ) {{
            setOffset( toolsControlPanelNode.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, toolsControlPanelNode.getFullBounds().getMaxY() + INSET );
            setFont( CONTROL_FONT );
        }} );

        //Add the faucets
        addChild( new FaucetNode( transform, model.inputFlowRate ) );
        addChild( new FaucetNode( transform, model.outputFlowRate ) {{
            Point2D beakerBottomRight = model.beaker.getOutputFaucetAttachmentPoint();
            Point2D beakerBottomRightView = transform.modelToView( beakerBottomRight );
            //Move it up by the height of the faucet image, otherwise it sticks out underneath the beaker
            setOffset( beakerBottomRightView.getX() - getFullBounds().getWidth() * 0.4, //Hand tuned so it doesn't overlap the reset button in English
                       beakerBottomRightView.getY() - getFullBounds().getHeight() );
        }} );

        //Add salt crystals graphics when salt crystals are added to the model
        model.addSaltAddedListener( new VoidFunction1<Salt>() {
            public void apply( final Salt salt ) {
                //Create the node
                final SaltNode saltNode = new SaltNode( transform, salt );

                //Set up to remove the node and its listener when salt crystal removed from the model
                salt.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        crystalLayer.removeChild( saltNode );

                        //Store a reference to the removalListener instance, for use in the anonymous inner class below
                        final VoidFunction0 removalListener = this;

                        //invoke later to avoid concurrentmodificationexception, since this is called during Crystal.remove()
                        //This code should be read with IntelliJ's closure folding
                        invokeLater( new Runnable() {
                            public void run() {
                                salt.removeRemovalListener( removalListener );
                            }
                        } );
                    }
                } );
                crystalLayer.addChild( saltNode );
            }
        } );
        //add the salt shaker node
        addChild( new SaltShakerNode( transform, new VoidFunction1<ImmutableVector2D>() {
            public void apply( ImmutableVector2D position ) {
                model.addSalt( new Salt( position ) );
            }
        } ) );

        //Show the crystal layer behind the water and beaker so the crystals look like they go into the water instead of in front of it.
        addChild( crystalLayer );

        //Add beaker and water nodes
        addChild( new BeakerNode( transform, model.beaker ) );
        addChild( new WaterNode( transform, model.water ) );

        //Debug for showing stage
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, stageSize.getWidth(), stageSize.getHeight() ), new BasicStroke( 2 ), Color.red ) );
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    private void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}