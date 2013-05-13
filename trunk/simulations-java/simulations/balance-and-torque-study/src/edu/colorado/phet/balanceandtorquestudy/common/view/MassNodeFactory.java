// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.view;

import java.awt.Color;

import edu.colorado.phet.balanceandtorque.balancelab.view.ImageMassNode;
import edu.colorado.phet.balanceandtorque.common.model.ShapeMass;
import edu.colorado.phet.balanceandtorque.common.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorque.common.model.masses.Mass;
import edu.colorado.phet.balanceandtorque.common.model.masses.MysteryMass;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Factory class for creating mass nodes for a given Mass objects.  This
 * basically does the class identification necessary to match the appropriate
 * view representation (i.e. node) with the give model representation.
 *
 * @author John Blanco
 */
public class MassNodeFactory {
    public static PNode createMassNode( Mass mass, BooleanProperty labelVisibilityProperty, ModelViewTransform mvt, PhetPCanvas canvas ) {
        PNode massNode;
        if ( mass instanceof ShapeMass ) {
            massNode = new BrickStackNode( (ShapeMass) mass, mvt, canvas, labelVisibilityProperty );
        }
        else if ( mass instanceof MysteryMass ) {
            massNode = new MysteryMassNode( mvt, (MysteryMass) mass, canvas, labelVisibilityProperty );
        }
        else if ( mass instanceof ImageMass ) {
            massNode = new ImageMassNode( mvt, (ImageMass) mass, canvas, labelVisibilityProperty );
        }
        else {
            System.out.println( "Error: Unrecognized mass type sent to MassNodeFactory." );
            assert false;
            // In case the ever happens out in the wild, create a fake node.
            massNode = new UnimplementedMassNode( mass, mvt );
        }
        return massNode;
    }

    // This exists to allow the factory to return something for unrecognized
    // masses.
    private static class UnimplementedMassNode extends PNode {
        private UnimplementedMassNode( Mass mass, ModelViewTransform mvt ) {
            PText text = new PText( "Unimplemented Mass" ) {{
                setFont( new PhetFont( 20, true ) );
            }};
            addChild( new PhetPPath( text.getFullBoundsReference(), Color.PINK ) );
            addChild( text );
            setOffset( mvt.modelToView( mass.getPosition() ) );
        }
    }
}
