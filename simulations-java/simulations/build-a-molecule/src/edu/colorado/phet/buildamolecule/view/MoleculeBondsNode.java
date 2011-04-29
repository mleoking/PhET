//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.view;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.buildamolecule.model.AtomModel;
import edu.colorado.phet.buildamolecule.model.Kit;
import edu.colorado.phet.buildamolecule.model.MoleculeStructure;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPaintContext;

import static edu.colorado.phet.buildamolecule.model.LewisDotModel.Direction;

/**
 * Handles "bond breaking" nodes for a single molecule
 */
public class MoleculeBondsNode extends PNode {

    /**
     * "Radius" of the bond target that will break the bond
     */
    public static final double BOND_RADIUS = 5;

    public MoleculeBondsNode( final Kit kit, MoleculeStructure moleculeStructure, final ModelViewTransform mvt ) {

        for ( MoleculeStructure.Bond bond : moleculeStructure.getBonds() ) {
            final AtomModel a = kit.getAtomModel( bond.a );
            final AtomModel b = kit.getAtomModel( bond.b );

            Direction bondDirection = kit.getBondDirection( a.getAtomInfo(), b.getAtomInfo() );
            final boolean isHorizontal = bondDirection == Direction.West || bondDirection == Direction.East;

            /*---------------------------------------------------------------------------*
            * bond node
            * TODO: separate class? consolidate molecule parts?
            *----------------------------------------------------------------------------*/
            addChild( new PNode() {
                {
                    // hit target
                    addChild( new PhetPPath( new Ellipse2D.Double( -BOND_RADIUS, -BOND_RADIUS, 2 * BOND_RADIUS, 2 * BOND_RADIUS ) ) {{
                        setPaint( Color.BLUE );
                        setStrokePaint( Color.RED );
                        setTransparency( 0.0f );
                        addInputEventListener( new CursorHandler( createCursor( isHorizontal ) ) {
                            @Override public void mouseClicked( PInputEvent event ) {
                                kit.breakBond( a, b );
                            }
                        } );
                    }} );

                    // listeners and setting position
                    a.addPositionListener( new SimpleObserver() {
                        public void update() {
                            updatePosition();
                        }
                    } );
                    b.addPositionListener( new SimpleObserver() {
                        public void update() {
                            updatePosition();
                        }
                    } );
                    updatePosition();
                }

                public void updatePosition() {
                    ImmutableVector2D location = b.getPosition().getSubtractedInstance( a.getPosition() ).getNormalizedInstance().getScaledInstance( a.getRadius() ).getAddedInstance( a.getPosition() );
                    setOffset( mvt.modelToView( location.toPoint2D() ) );
                }
            } );
        }
    }

    private static void paintArrow( Point2D tail, Point2D tip, PPaintContext context ) {

        double headHeight = 5;
        double headWidth = 10;
        double tailWidth = 3;

        // shadow
        new ArrowNode( new Point2D.Double( tail.getX() + 3, tail.getY() + 2 ), new Point2D.Double( tip.getX() + 3, tip.getY() + 2 ), headHeight, headWidth, tailWidth ) {{
            setPaint( Color.BLACK );
            setStrokePaint( null );
            setTransparency( 0.5f );
        }}.fullPaint( context );

        // arrow
        new ArrowNode( tail, tip, headHeight, headWidth, tailWidth ) {{
            setPaint( Color.WHITE );
            setStrokePaint( Color.BLACK );
        }}.fullPaint( context );
    }

    private static Cursor createCursor( final boolean isHorizontal ) {
        BufferedImage image = new BufferedImage( 32, 32, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        PPaintContext context = new PPaintContext( g2 );
        if ( isHorizontal ) {
            paintArrow( new Point2D.Double( 15, 16 ), new Point2D.Double( 3, 16 ), context );
            paintArrow( new Point2D.Double( 17, 16 ), new Point2D.Double( 29, 16 ), context );
        }
        else {
            paintArrow( new Point2D.Double( 16, 15 ), new Point2D.Double( 16, 3 ), context );
            paintArrow( new Point2D.Double( 16, 17 ), new Point2D.Double( 16, 29 ), context );
        }

        // Use the image to create a cursor
        Point hotSpot = new Point( image.getWidth() / 2, image.getHeight() / 2 );
        String name = "DragHandleCursor";
        Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor( image, hotSpot, name );
        return cursor;
    }
}
