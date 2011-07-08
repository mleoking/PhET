// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * This class defines the box from which the user can drag weights that can
 * then be set on the balance.
 *
 * @author John Blanco
 */
public class WeightBoxNode2 extends PNode {

    private static final PDimension SIZE = new PDimension( 220, 300 );
    private static final PhetFont BUTTON_FONT = new PhetFont( 16 );
    private static final Color BUTTON_COLOR = new Color( 50, 205, 50 );

    private ArrayList<PNode> weightSets = new ArrayList<PNode>();
    private Property<Integer> activeWeightSet = new Property<Integer>( 0 );

    /**
     * Constructor.
     */
    public WeightBoxNode2( final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {

        // Create a node that contains the set of bricks.  This is a "weight set".
        weightSets.add( new SwingLayoutNode( new GridLayout( 2, 2, 20, 20 ) ) {{
            BrickStackInWeightBoxNode oneBrickStack = new BrickStackInWeightBoxNode( 1, model, mvt, canvas ) {{
                setOffset( SIZE.width * 0.25, SIZE.height / 2 );
            }};
            addChild( oneBrickStack );
            BrickStackInWeightBoxNode twoBrickStack = new BrickStackInWeightBoxNode( 2, model, mvt, canvas ) {{
                setOffset( SIZE.width * 0.75, SIZE.height / 2 );
            }};
            addChild( twoBrickStack );
            BrickStackInWeightBoxNode threeBrickStack = new BrickStackInWeightBoxNode( 3, model, mvt, canvas ) {{
                setOffset( SIZE.width * 0.25, SIZE.height * 0.75 );
            }};
            addChild( threeBrickStack );
            BrickStackInWeightBoxNode fourBrickStack = new BrickStackInWeightBoxNode( 4, model, mvt, canvas ) {{
                setOffset( SIZE.width * 0.75, SIZE.height * 0.75 );
            }};
            addChild( fourBrickStack );
        }} );

        // Create a node that contains people.  This is also a "weight set".
        weightSets.add( new SwingLayoutNode( new GridLayout( 2, 1, 20, 20 ) ) {{
            BrickStackInWeightBoxNode oneBrickStack = new BrickStackInWeightBoxNode( 1, model, mvt, canvas ) {{
                setOffset( SIZE.width * 0.25, SIZE.height / 2 );
            }};
            addChild( oneBrickStack );
            BrickStackInWeightBoxNode twoBrickStack = new BrickStackInWeightBoxNode( 2, model, mvt, canvas ) {{
                setOffset( SIZE.width * 0.75, SIZE.height / 2 );
            }};
            addChild( twoBrickStack );
        }} );

        // TODO: i18n
        TextButtonNode nextButton = new TextButtonNode( "Next", BUTTON_FONT, BUTTON_COLOR ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    activeWeightSet.set( ( activeWeightSet.get() + 1 ) % weightSets.size() );
                }
            } );
            // Set up a listener that disables the button if there are no more weight sets.
            activeWeightSet.valueEquals( weightSets.size() - 1 ).addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean atLastWeight ) {
                    setEnabled( !atLastWeight );
                }
            } );
        }};
        // TODO: i18n
        TextButtonNode previousButton = new TextButtonNode( "Previous", BUTTON_FONT, BUTTON_COLOR ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    activeWeightSet.set( Math.abs( ( activeWeightSet.get() - 1 ) % weightSets.size() ) );
                }
            } );
            // Set up a listener that disables the button if they are looking at the first weight set.
            activeWeightSet.valueEquals( 0 ).addObserver( new VoidFunction1<Boolean>() {
                public void apply( Boolean atFirstWeight ) {
                    setEnabled( !atFirstWeight );
                }
            } );
        }};

        // Create the content node that will be placed on to a control panel.
        // This contains a title, buttons for moving between weight sets, and
        // the weight sets.
        final PNode contentNode = new VBox(
                // Title.
                // TODO: i18n
                new PText( "Weights" ) {{
                    setFont( new PhetFont( 20 ) );
                }},
                // Buttons.
                new HBox( previousButton, nextButton ),
                // Weight set.
                weightSets.get( activeWeightSet.get() )
        );

        // Hook up a listener that will switch the weight sets when necessary.
        activeWeightSet.addObserver( new ChangeObserver<Integer>() {
            public void update( Integer newValue, Integer oldValue ) {
                contentNode.removeChild( weightSets.get( oldValue ) );
                contentNode.addChild( weightSets.get( newValue ) );
            }
        } );

        // Last step: Create and add the control panel.
        addChild( new ControlPanelNode( contentNode ) );
    }
}