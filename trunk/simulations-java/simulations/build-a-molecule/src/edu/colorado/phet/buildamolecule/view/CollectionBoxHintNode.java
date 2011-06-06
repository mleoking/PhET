//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.buildamolecule.BuildAMoleculeStrings;
import edu.colorado.phet.buildamolecule.model.Atom2D;
import edu.colorado.phet.buildamolecule.model.Atom2D.Adapter;
import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.model.Molecule;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.buildamolecule.BuildAMoleculeConstants.MODEL_VIEW_TRANSFORM;

public class CollectionBoxHintNode extends PNode {
    public CollectionBoxHintNode( final Molecule molecule, final CollectionBox box ) {

        PBounds moleculeDestinationBounds = molecule.getDestinationBounds();

        // at the end, where our tip and tail should be
        final ImmutableVector2D tipTarget = MODEL_VIEW_TRANSFORM.modelToView( new ImmutableVector2D( box.getDropBounds().getMinX() - 20, box.getDropBounds().getCenterY() ) );

        final Rectangle2D moleculeViewBounds = MODEL_VIEW_TRANSFORM.modelToViewRectangle( moleculeDestinationBounds );
        final Rectangle2D boxViewBounds = MODEL_VIEW_TRANSFORM.modelToView( box.getDropBounds() ).getBounds2D();

        PNode labelNode = new PNode() {{
            PText textNode = new PText( BuildAMoleculeStrings.COLLECTION_HINT ) {{
                setFont( new PhetFont( 16, true ) );
                setTextPaint( Color.BLACK );
            }};
            double verticalPad = 5;
            double horizontalPad = 5;
            PBounds textBounds = textNode.getFullBounds();
            addChild( new PhetPPath( new RoundRectangle2D.Double( textBounds.x - horizontalPad, textBounds.y - verticalPad, textBounds.getWidth() + horizontalPad * 2, textBounds.getHeight() + verticalPad * 2, 15, 15 ) ) {{
                setPaint( Color.WHITE );
                setStrokePaint( Color.BLACK );
            }} );
            addChild( textNode );
            centerFullBoundsOnPoint( moleculeViewBounds.getCenterX(), moleculeViewBounds.getMaxY() + 5 + getFullBounds().getHeight() / 2 );
        }};
        addChild( labelNode );

        ImmutableVector2D textEnd = new ImmutableVector2D( labelNode.getFullBounds().getMaxX() - 5, labelNode.getFullBounds().getCenterY() );
        ImmutableVector2D direction = tipTarget.minus( textEnd ).getNormalizedInstance();

        final ArrowNode blueArrow = new ArrowNode( textEnd.plus( direction.times( 15 ) ).toPoint2D(), tipTarget.toPoint2D(), 30, 40, 20 ) {{
            setPaint( Color.BLUE );
        }};
        addChild( blueArrow );

        // hide this when we move the molecule
        Adapter grabListener = new Adapter() {
            @Override public void grabbedByUser( Atom2D particle ) {
                disperse();
                for ( Atom2D atom : molecule.getAtoms() ) {
                    atom.removeListener( this );
                }
            }
        };
        for ( Atom2D atom : molecule.getAtoms() ) {
            atom.addListener( grabListener );
        }
    }

    /**
     * Figures out a nice point around the rectangle (padding-distance from it) that the tip of an arrow should end at to
     * point from outsidePoint
     *
     * @param bounds  Rectangle bounds
     * @param padding Padding
     * @param tail    Arrow tail
     * @return Arrow tip
     */
    private ImmutableVector2D getInterceptPoint( Rectangle2D bounds, double padding, ImmutableVector2D tail ) {
        ImmutableVector2D center = new ImmutableVector2D( bounds.getCenterX(), bounds.getCenterY() );
        ImmutableVector2D unitDirection = center.minus( tail ).getNormalizedInstance();

        double minX = bounds.getMinX() - padding;
        double maxX = bounds.getMaxX() + padding;

        double minY = bounds.getMinY() - padding;
        double maxY = bounds.getMaxY() + padding;

        ImmutableVector2D hitLocation = null;

        // check for left/right hits
        if ( tail.getX() < minX || tail.getX() > maxX ) {
            double borderX = tail.getX() > bounds.getCenterX() ? maxX : minX;
            double t = ( borderX - tail.getX() ) / unitDirection.getX();
            hitLocation = tail.plus( unitDirection.times( t ) );
        }
        if ( tail.getY() < minY || tail.getY() > maxY ) {
            double borderY = tail.getY() > bounds.getCenterY() ? maxY : minY;
            double t = ( borderY - tail.getY() ) / unitDirection.getY();

            ImmutableVector2D otherHitLocation = tail.plus( unitDirection.times( t ) );
            if ( hitLocation == null || hitLocation.getDistance( center ) > otherHitLocation.getDistance( center ) ) {
                hitLocation = otherHitLocation;
            }
        }
        return hitLocation;
    }

    public void disperse() {
        setVisible( false );
    }
}
