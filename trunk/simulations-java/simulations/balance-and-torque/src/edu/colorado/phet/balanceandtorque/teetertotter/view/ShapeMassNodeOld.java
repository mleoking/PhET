// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.text.DecimalFormat;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ShapeMass;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * A node that represents a mass that is described by a particular shape (as
 * opposed to an image) in the view.
 *
 * @author John Blanco
 */
public class ShapeMassNodeOld extends PNode {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    // Default color, which is meant to look somewhat like a brick.
    private static final Color DEFAULT_COLOR = new Color( 205, 38, 38 );

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private final PText massLabel = new PText( "uninitialized" ) {{
        setFont( new PhetFont( 18 ) );
    }};

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------
    public ShapeMassNodeOld( final ModelViewTransform mvt, final ShapeMass mass, PhetPCanvas canvas ) {
        this( mvt, mass, DEFAULT_COLOR, canvas, new BooleanProperty( false ) );
    }

    public ShapeMassNodeOld( final ModelViewTransform mvt, final ShapeMass mass, Color fillColor, PhetPCanvas canvas, BooleanProperty massLabelVisibleProperty ) {
        // Create and add the mass label.
        DecimalFormat formatter = new DecimalFormat( "##" );
        // TODO: i18n, including order and units!
        massLabel.setText( formatter.format( mass.getMass() ) + " kg" );
        addChild( massLabel );

        // Create the main shape node and set up the observer that handles shape
        // changes.
        addChild( new PhetPPath( fillColor, new BasicStroke( 1 ), Color.BLACK ) {{
            mass.shapeProperty.addObserver( new VoidFunction1<Shape>() {
                public void apply( Shape shape ) {

                    // Set the shape of the node to the scaled shape of the
                    // model element.  Note that this handles changes to
                    // position and rotation as well as what we generally
                    // think of as the "shape".
                    setPathTo( mvt.modelToView( shape ) );

                    // Update the position of the mass label.
                    massLabel.setRotation( 0 );
                    massLabel.setOffset( getFullBoundsReference().getCenterX() - massLabel.getFullBoundsReference().width / 2,
                                         getFullBoundsReference().getMinY() - massLabel.getFullBoundsReference().height );
                    massLabel.rotateAboutPoint( -mass.getRotationAngle(), mass.getPosition() );
                }
            } );
        }} );

        // Add event listeners for mouse activity.
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new MassDragHandler( mass, this, canvas, mvt ) );
    }

}
