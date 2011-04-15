// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Allows the collection of a single molecule
 */
public class SingleCollectionBoxNode extends SwingLayoutNode {
    public SingleCollectionBoxNode( final BuildAMoleculeCanvas canvas, final CollectionBox box ) {
        super( new GridBagLayout() );
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;

        // TODO: i18nize
        addChild( new HTMLNode( box.getMoleculeType().getMoleculeStructure().getMolecularFormulaHTMLFragment() + " (" + box.getMoleculeType().getCommonName() + ")" ) {{
            setFont( new PhetFont( 16, true ) );
        }}, c );

        c.gridy = 1;
        c.insets = new Insets( 3, 0, 0, 0 ); // some padding between the two
        final PhetPPath blackBox = new PhetPPath( new Rectangle2D.Double( 0, 0, 160, 50 ), Color.BLACK ) {{
            canvas.addFullyLayedOutObserver( new SimpleObserver() {
                public void update() {
                    // we need to pass the collection box model coordinates, but here we have relative piccolo coordinates
                    // this requires getting local => global => view => model coordinates

                    // our bounds relative to the root Piccolo canvas
                    Rectangle2D globalBounds = getParent().localToGlobal( getFullBounds() );

                    // pull out the upper-left corner and dimension so we can transform them
                    Point2D upperLeftCorner = new Point2D.Double( globalBounds.getX(), globalBounds.getY() );
                    PDimension dimensions = new PDimension( globalBounds.getWidth(), globalBounds.getHeight() );

                    // transform the point and dimensions to world coordinates
                    canvas.getPhetRootNode().globalToWorld( upperLeftCorner );
                    canvas.getPhetRootNode().globalToWorld( dimensions );

                    // our bounds relative to our simulation (BAM) canvas. Will be filled in
                    Rectangle2D viewBounds = new Rectangle2D.Double( upperLeftCorner.getX(), upperLeftCorner.getY(), dimensions.getWidth(), dimensions.getHeight() );

                    // pass it the model bounds
                    box.setDropBounds( canvas.getModelViewTransform().viewToModel( viewBounds ).getBounds2D() );
                }
            } );
        }};
        addChild( blackBox, c );

        box.quantity.addObserver( new VoidFunction2<Integer, Integer>() {
            public void apply( Integer newValue, Integer oldValue ) {
                if ( newValue > oldValue ) {
                    PNode pseudo3DNode = box.getMoleculeType().createPseudo3DNode();
                    PBounds blackBoxFullBounds = blackBox.getFullBounds();
                    pseudo3DNode.centerFullBoundsOnPoint(
                            blackBoxFullBounds.getCenterX() - blackBoxFullBounds.getX(),
                            blackBoxFullBounds.getCenterY() - blackBoxFullBounds.getY() );
                    blackBox.addChild( pseudo3DNode );
                }
            }
        } );
    }
}
