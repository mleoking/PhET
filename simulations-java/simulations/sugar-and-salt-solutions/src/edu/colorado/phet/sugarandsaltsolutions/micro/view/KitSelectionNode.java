// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.SwingUtilities;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.common.view.StandardizedNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PClip;

import static edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode.DEFAULT_ARC;

/**
 * Node for showing and scrolling between kits, which can be any PNode.
 *
 * @author Sam Reid
 */
public class KitSelectionNode<T extends PNode> extends PNode {

    //The currently selected kit, public because it can be set and observed by other classes
    public final Property<Integer> selectedKit;

    //Animation activity that scrolls between kits
    private PTransformActivity activity;

    //Layer that contains all the kits side by side horizontally
    protected PNode kitLayer;

    //Border and background, used for layout
    protected PhetPPath background;

    //List of the available kits
    private final ArrayList<T> kits;

    /**
     * Create a KitSelectionNode that uses the specified kits
     *
     * @param selectedKit model for which kit has been selected by the user
     * @param kits        the list of kits to show
     */
    public KitSelectionNode( final Property<Integer> selectedKit, T... kits ) {
        this.kits = new ArrayList<T>( Arrays.asList( kits ) );
        this.selectedKit = selectedKit;

        //Standardize the nodes, this centers them to simplify the layout
        final ArrayList<PNode> standardizedNodes = new ArrayList<PNode>();
        for ( PNode kit : kits ) {
            standardizedNodes.add( new StandardizedNode( kit ) );
        }

        //Find the smallest rectangle that holds all the kits
        Rectangle2D kitBounds = standardizedNodes.get( 0 ).getFullBounds();
        for ( PNode kit : standardizedNodes ) {
            kitBounds = kitBounds.createUnion( kit.getFullBounds() );
        }

        //Add insets so the kits sit within the background instead of protruding off the edge
        kitBounds = RectangleUtils.expand( kitBounds, 2, 2 );

        //Construct and add the background.  Make it big enough to hold the largest kit, and set it to look like ControlPanelNode by default
        RoundRectangle2D.Double backgroundPath = new RoundRectangle2D.Double( 0, 0, kitBounds.getWidth(), kitBounds.getHeight(), DEFAULT_ARC, DEFAULT_ARC );

        //Hide the background for embedding in a larger control panel, background is just used for layout purposes
        background = new PhetPPath( backgroundPath, null, null, null );
        addChild( background );

        //Create the layer that contains all the kits, and add the kits side by side spaced by the distance of the background so only 1 kit will be visible at a time
        kitLayer = new PNode();
        double x = 0;
        for ( PNode standardizedNode : standardizedNodes ) {
            standardizedNode.setOffset( x + background.getFullBounds().getWidth() / 2 - standardizedNode.getFullBounds().getWidth() / 2, background.getFullBounds().getHeight() / 2 - standardizedNode.getFullBounds().getHeight() / 2 );
            kitLayer.addChild( standardizedNode );
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

        //Buttons for scrolling previous/next
        //Place the kit "previous" and "next" buttons above the kit to save horizontal space.  In Sugar and Salt Solution, they are moved up next to the title
        final PNode nextButton = new ForwardButton() {{
            setOffset( background.getFullBounds().getMaxX() - getFullBounds().getWidth(), background.getFullBounds().getMinY() - getFullBounds().getHeight() );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selectedKit.set( selectedKit.get() + 1 );
                }
            } );
            KitSelectionNode.this.selectedKit.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setVisible( KitSelectionNode.this.selectedKit.get() < standardizedNodes.size() - 1 );
                }
            } );
        }};
        addChild( nextButton );
        addChild( new BackButton() {{
            setOffset( background.getFullBounds().getMinX(), background.getFullBounds().getMinY() - getFullBounds().getHeight() );

            //Make sure the previous and next buttons don't overlap, useful to handle long i18n strings
            if ( getFullBounds().getMaxX() > nextButton.getFullBounds().getMinX() ) {
                setOffset( nextButton.getFullBounds().getMinX() - 2, getOffset().getY() );
            }
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    selectedKit.set( selectedKit.get() - 1 );
                }
            } );
            KitSelectionNode.this.selectedKit.addObserver( new VoidFunction1<Integer>() {
                public void apply( Integer integer ) {
                    setVisible( KitSelectionNode.this.selectedKit.get() > 0 );
                }
            } );
        }} );
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

    public T getKit( Integer index ) {
        return kits.get( index );
    }

    //Sample main to demonstrate KitSelectionNode
    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                new PFrame() {{
                    getCanvas().getLayer().addChild( new KitSelectionNode<PNode>(
                            new Property<Integer>( 0 ),
                            new PText( "Hello" ),
                            new PText( "There" ),
                            new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 100 ), Color.blue ),
                            new PText( "So" ),
                            new PText( "Many" ),
                            new PText( "Kits" )
                    ) {{
                        setOffset( 100, 100 );
                    }} );
                    setDefaultCloseOperation( EXIT_ON_CLOSE );
                }}.setVisible( true );
            }
        } );
    }
}