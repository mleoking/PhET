// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.kit;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.Option.None;
import edu.colorado.phet.common.phetcommon.util.Option.Some;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PClip;

import static edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode.DEFAULT_ARC;
import static java.lang.Math.max;

/**
 * Node for showing and scrolling between kits.
 *
 * @author Sam Reid
 */
public class KitSelectionNode<T extends PNode> extends PNode {

    //The currently selected kit, public because it can be set and observed by other classes
    public final Property<Integer> selectedKit;

    //Animation activity that scrolls between kits
    private PTransformActivity activity;

    //Layer that contains all the kits side by side horizontally
    protected final PNode kitLayer;

    //Border and background, used for layout
    protected final PhetPPath background;

    //List of the available kits
    private final ArrayList<Kit<T>> kits;

    /**
     * Creates a KitSelectionNode with no title
     *
     * @param selectedKit model for which kit has been selected by the user
     * @param kits        the list of kits to show
     */
    public KitSelectionNode( final Property<Integer> selectedKit, Kit<T>... kits ) {
        this( selectedKit, new None<PNode>(), kits );
    }

    /**
     * Creates a KitSelectionNode with the specified title node
     *
     * @param selectedKit model for which kit has been selected by the user
     * @param titleNode   node to display for the title
     * @param kits        the list of kits to show
     */
    public KitSelectionNode( final Property<Integer> selectedKit, PNode titleNode, Kit<T>... kits ) {
        this( selectedKit, new Some<PNode>( titleNode ), kits );
    }

    /**
     * Create a KitSelectionNode that uses the specified kits
     *
     * @param selectedKit model for which kit has been selected by the user
     * @param titleNode   optional node to display for the title
     * @param kits        the list of kits to show
     */
    public KitSelectionNode( final Property<Integer> selectedKit, Option<PNode> titleNode, Kit<T>... kits ) {
        this.kits = new ArrayList<Kit<T>>( Arrays.asList( kits ) );
        this.selectedKit = selectedKit;

        //Standardize the nodes, this centers them to simplify the layout
        final ArrayList<PNode> zeroOffsetContentNodes = new ArrayList<PNode>();
        for ( Kit<T> kit : kits ) {
            zeroOffsetContentNodes.add( new ZeroOffsetNode( kit.content ) );
        }

        //Find out the max bounds of all the kit contents for layout purposes
        Rectangle2D contentBounds = getBoundingRectangle( zeroOffsetContentNodes );

        final ArrayList<PNode> zeroOffsetTitleNodes = new ArrayList<PNode>();
        for ( Kit<T> kit : kits ) {
            if ( kit.title.isSome() ) {
                zeroOffsetTitleNodes.add( new ZeroOffsetNode( kit.title.get() ) );
            }
        }

        //Find out the max bounds of all the kit titles for layout purposes
        Rectangle2D titleBounds = getBoundingRectangle( zeroOffsetTitleNodes );

        //The bounds of the smallest possible KitControlNode, used to ensure the rest of the control is at least this big
        //TODO: This value is hard coded, could have a better layout
        double controlWidth = new KitControlNode( getKitCount(), selectedKit, titleNode, 3 ).getFullBounds().getWidth();

        //Construct and add the background.  Make it big enough to hold the largest kit, and set it to look like ControlPanelNode by default
        RoundRectangle2D.Double kitBounds = new RoundRectangle2D.Double( 0, 0, max( max( contentBounds.getWidth(), titleBounds.getWidth() ), controlWidth ), contentBounds.getHeight() + titleBounds.getHeight(), DEFAULT_ARC, DEFAULT_ARC );

        //Hide the background for embedding in a larger control panel, background is just used for layout purposes
        background = new PhetPPath( kitBounds, null, null, null );
        addChild( background );

        //Create the layer that contains all the kits, and add the kits side by side spaced by the distance of the background so only 1 kit will be visible at a time
        kitLayer = new PNode();

        //X location of each kit content nodes within the new parent (kitLayer) that will be scrolled across
        double x = 0;

        double availableSpaceForContentNode = background.getFullBounds().getHeight() - titleBounds.getHeight();
        for ( Kit<T> kit : kits ) {

            //Zero the title and content for easier layout
            ZeroOffsetNode kitTitleNode = new ZeroOffsetNode( kit.title.getOrElse( new PNode() ) );
            ZeroOffsetNode contentNode = new ZeroOffsetNode( kit.content );

            //Put the title centered at the top and the content node centered in the available space beneath
            kitTitleNode.setOffset( x + background.getFullBounds().getWidth() / 2 - kitTitleNode.getFullBounds().getWidth() / 2, 0 );
            contentNode.setOffset( x + background.getFullBounds().getWidth() / 2 - contentNode.getFullBounds().getWidth() / 2,
                                   titleBounds.getHeight() + availableSpaceForContentNode / 2 - contentNode.getFullBounds().getHeight() / 2 );

            //Add the nodes to the kitLayer
            kitLayer.addChild( kitTitleNode );
            kitLayer.addChild( contentNode );

            //Move over to the next kit
            x += background.getFullBounds().getWidth();
        }

        //Show a clip around the kits so that kits to the side can't be seen or interacted with.  Hide its stroke so it doesn't show a black border
        addChild( new PClip() {{
            setPathTo( background.getPathReference() );
            setStroke( null );
            addChild( kitLayer );
        }} );

        selectedKit.addObserver( new VoidFunction1<Integer>() {
            public void apply( Integer kit ) {
                scrollTo( kit );
            }
        } );

        //Add the KitControlNode which has the optional title and forward/back buttons
        //TODO: This heuristic is not exactly right for making sure the KitControlNode is spaced properly
        double width = background.getFullBounds().getWidth();
        double extraSpace = width - controlWidth;
        double extraInsetSpace = extraSpace / 2;

        addChild( new ZeroOffsetNode( new KitControlNode( getKitCount(), selectedKit, titleNode, extraInsetSpace ) ) {{
            setOffset( background.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, background.getFullBounds().getMinY() - getFullBounds().getHeight() );
        }} );
    }

    private Rectangle2D getBoundingRectangle( ArrayList<PNode> nodes ) {
        if ( nodes.size() == 0 ) {
            return new Rectangle();
        }

        //Find the smallest rectangle that holds all the kits
        Rectangle2D kitBounds = nodes.get( 0 ).getFullBounds();
        for ( PNode kit : nodes ) {
            kitBounds = kitBounds.createUnion( kit.getFullBounds() );
        }

        //Add insets so the kits sit within the background instead of protruding off the edge
        kitBounds = RectangleUtils.expand( kitBounds, 2, 2 );
        return kitBounds;
    }

    //When the next or previous button is pressed, scroll to the next kit
    private void scrollTo( int index ) {
        if ( index >= 0 && index < kitLayer.getChildrenCount() ) {
            if ( activity != null ) {

                //Terminate without finishing the previous activity so the motion doesn't stutter
                activity.terminate( PActivity.TERMINATE_WITHOUT_FINISHING );
            }
            selectedKit.set( index );
            double x = background.getFullBounds().getWidth() * index;
            activity = kitLayer.animateToPositionScaleRotation( -x, 0, 1, 0, 500 );
        }
    }

    public Kit<T> getKit( Integer index ) {
        return kits.get( index );
    }

    //Sample main to demonstrate KitSelectionNode
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame() {{
                    getCanvas().getLayer().addChild( new KitSelectionNode<PNode>(
                            new Property<Integer>( 0 ),
                            new Some<PNode>( new PText( "Choose a kit:" ) {{setFont( new PhetFont( 16, true ) );}} ),
                            new Kit<PNode>( new PText( "Title 1" ), new VBox( new PText( "Hello" ), new PText( "This is kit 1" ) ) ),
                            new Kit<PNode>( new PText( "there" ) ),
                            new Kit<PNode>( new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 100 ), Color.blue ) ),
                            new Kit<PNode>( new PText( "so" ) ),
                            new Kit<PNode>( new PText( "many" ) ),
                            new Kit<PNode>( new PText( "kits!" ) )
                    ) {{
                        setOffset( 100, 100 );
                    }} );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }

    public int getKitCount() {
        return kits.size();
    }
}