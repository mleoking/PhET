// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.common.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.moleculepolarity.MPStrings;
import edu.colorado.phet.moleculepolarity.common.view.JmolViewerNode.ElementColor;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Table that shows electronegativity for a select of elements.
 * By default, all cells in the table are the same color.
 * Colors for specific cells can be set, so that they match the colors of the molecule displayed by Jmol.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ElectronegativityTableNode extends PComposite {

    private final ArrayList<Cell> cells;

    public ElectronegativityTableNode( final JmolViewerNode viewerNode ) {

        PText titleNode = new PText( MPStrings.ATOM_ELECTRONEGATIVITIES ) {{
            setFont( new PhetFont( Font.BOLD, 14 ) );
        }};
        addChild( titleNode );

        cells = new ArrayList<Cell>() {{
            add( new Cell( "H", 1, 2.1 ) );
            add( new Cell( "B", 5, 2.0 ) );
            add( new Cell( "C", 6, 2.5 ) );
            add( new Cell( "N", 7, 3.0 ) );
            add( new Cell( "O", 8, 3.5 ) );
            add( new Cell( "F", 9, 4.0 ) );
            add( new Cell( "Cl", 17, 3.0 ) );
        }};

        // layout cells, first and last cells are horizontally separated from others
        final double xGap = 12;
        double x = 0;
        final double y = titleNode.getFullBoundsReference().getHeight() + 3;
        Cell firstCell = cells.get( 0 );
        addChild( firstCell );
        firstCell.setOffset( x, y );
        x = x + firstCell.getFullBoundsReference().getWidth() + xGap;
        for ( int i = 1; i < cells.size() - 1; i++ ) {
            Cell cell = cells.get( i );
            addChild( cell );
            cell.setOffset( x, y );
            x = x + cell.getFullBoundsReference().getWidth();
        }
        x += xGap;
        Cell lastCell = cells.get( cells.size() - 1 );
        addChild( lastCell );
        lastCell.setOffset( x, y );

        // center title above cells
        titleNode.setOffset( ( lastCell.getFullBoundsReference().getMaxX() - firstCell.getFullBoundsReference().getMinX() ) / 2 - ( titleNode.getFullBoundsReference().getWidth() / 2 ), 0 );

        // when the current molecule changes, ask Jmol for the molecule's elements and colors
        viewerNode.molecule.addObserver( new SimpleObserver() {
            public void update() {
                reset();
                ArrayList<ElementColor> elementColors = viewerNode.getElementNumbersAndColors();
                for ( ElementColor elementColor : elementColors ) {
                    setColor( elementColor.elementNumber, elementColor.color );
                }
            }
        } );
    }

    private void reset() {
        for ( Cell cell : cells ) {
            cell.disable();
        }
    }

    // Sets the color of a specified element
    private void setColor( int elementNumber, Color color ) {
        for ( Cell cell : cells ) {
            if ( cell.getElementNumber() == elementNumber ) {
                cell.enable( color );
                break;
            }
        }
    }

    // A cell in the table, displays element name and number, color can be set
    private static class Cell extends PComposite {

        private static final Dimension SIZE = new Dimension( 50, 65 );
        private static final Color BACKGROUND_COLOR = new Color( 210, 210, 210 );
        private static final Color NORMAL_TEXT_COLOR = BACKGROUND_COLOR.darker();
        private static final Color HIGHLIGHTED_TEXT_COLOR = Color.BLACK;

        private final int elementNumber;
        private final PPath backgroundNode;
        private final PText elementNameNode, electronegativityNode;

        public Cell( String elementName, int elementNumber, double electronegativity ) {

            this.elementNumber = elementNumber;

            // nodes
            backgroundNode = new PPath( new Rectangle2D.Double( 0, 0, SIZE.width, SIZE.height ) ) {{
                setPaint( BACKGROUND_COLOR );
            }};
            elementNameNode = new PText( elementName ) {{
                setFont( new PhetFont( Font.BOLD, 24 ) );
                setTextPaint( NORMAL_TEXT_COLOR );
            }};
            electronegativityNode = new PText( new DecimalFormat( "0.0" ).format( electronegativity ) ) {{
                setFont( new PhetFont( 18 ) );
                setTextPaint( NORMAL_TEXT_COLOR );
            }};

            // rendering order
            addChild( backgroundNode );
            addChild( elementNameNode );
            addChild( electronegativityNode );

            // layout
            elementNameNode.setOffset( backgroundNode.getFullBoundsReference().getCenterX() - ( elementNameNode.getFullBoundsReference().getWidth() / 2 ), 3 );
            electronegativityNode.setOffset( backgroundNode.getFullBoundsReference().getCenterX() - ( electronegativityNode.getFullBoundsReference().getWidth() / 2 ),
                                             backgroundNode.getFullBoundsReference().getMaxY() - electronegativityNode.getFullBoundsReference().getHeight() - 3 );
        }

        public int getElementNumber() {
            return elementNumber;
        }

        // makes the cell appear enabled
        public void enable( Color color ) {
            backgroundNode.setPaint( color );
            elementNameNode.setTextPaint( HIGHLIGHTED_TEXT_COLOR );
            electronegativityNode.setTextPaint( HIGHLIGHTED_TEXT_COLOR );
        }

        // make the cell appear disabled
        public void disable() {
            backgroundNode.setPaint( BACKGROUND_COLOR );
            elementNameNode.setTextPaint( NORMAL_TEXT_COLOR );
            electronegativityNode.setTextPaint( NORMAL_TEXT_COLOR );
        }
    }
}
