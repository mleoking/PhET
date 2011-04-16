package edu.colorado.phet.buildamolecule.control;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.buildamolecule.model.CollectionBox;
import edu.colorado.phet.buildamolecule.view.BuildAMoleculeCanvas;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction2;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;

/**
 * Represents a generic collection box node which is decorated by additional header nodes (probably text describing what can be put in, what is in it,
 * etc.)
 */
public class CollectionBoxNode extends SwingLayoutNode {

    private final CollectionBox box;
    private final PNode boxNode = new PNode();
    private final PhetPPath blackBox;
    private final PNode moleculeLayer = new PNode();
    private final List<PNode> moleculeNodes = new LinkedList<PNode>();

    private static final double MOLECULE_PADDING = 5;

    public CollectionBoxNode( final BuildAMoleculeCanvas canvas, final CollectionBox box, PNode... headerNodes ) {
        super( new GridBagLayout() );
        this.box = box;

        // grid bag layout and SwingLayoutNode for easier horizontal and vertical layout
        GridBagConstraints c = new GridBagConstraints() {{
            gridx = 0;
            gridy = 0;
        }};

        // add in our header nodes
        for ( PNode headerNode : headerNodes ) {
            addChild( headerNode, c );
            c.gridy += 1;
        }

        c.insets = new Insets( 3, 0, 0, 0 ); // some padding between the black box

        blackBox = new PhetPPath( new Rectangle2D.Double( 0, 0, 160, 50 ), Color.BLACK ) {{
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
        boxNode.addChild( blackBox );
        boxNode.addChild( moleculeLayer );
        addChild( boxNode, c );

        box.quantity.addObserver( new VoidFunction2<Integer, Integer>() {
            public void apply( Integer newValue, Integer oldValue ) {
                if ( newValue > oldValue ) {
                    assert ( newValue - 1 == oldValue );
                    addMolecule();
                }
                else {
                    assert ( newValue + 1 == oldValue );
                    removeMolecule();
                }
            }
        } );
    }

    public void addMolecule() {
        PNode pseudo3DNode = box.getMoleculeType().createPseudo3DNode();
        pseudo3DNode.setOffset( moleculeNodes.size() * ( pseudo3DNode.getFullBounds().getWidth() + MOLECULE_PADDING ) - pseudo3DNode.getFullBounds().getX(), 0 ); // add it to the right
        moleculeLayer.addChild( pseudo3DNode );
        moleculeNodes.add( pseudo3DNode );

        centerMoleculesInBlackBox();
    }

    public void removeMolecule() {
        PNode lastMoleculeNode = moleculeNodes.get( moleculeNodes.size() - 1 );
        moleculeLayer.removeChild( lastMoleculeNode );
        moleculeNodes.remove( lastMoleculeNode );

        if ( box.quantity.getValue() > 0 ) {
            centerMoleculesInBlackBox();
        }
    }

    public void centerMoleculesInBlackBox() {
        PBounds blackBoxFullBounds = blackBox.getFullBounds();
        moleculeLayer.centerFullBoundsOnPoint(
                blackBoxFullBounds.getCenterX() - blackBoxFullBounds.getX(),
                blackBoxFullBounds.getCenterY() - blackBoxFullBounds.getY() );
    }
}
