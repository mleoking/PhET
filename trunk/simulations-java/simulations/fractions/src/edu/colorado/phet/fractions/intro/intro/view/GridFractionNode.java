// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;

/**
 * @author Sam Reid
 */
public class GridFractionNode extends ChosenRepresentationNode {
    public GridFractionNode( Property<ChosenRepresentation> chosenRepresentation, final Property<Integer> numerator, final Property<Integer> denominator ) {
        super( chosenRepresentation, ChosenRepresentation.SQUARE );

        new RichSimpleObserver() {
            @Override public void update() {
                removeAllChildren();

                int numGrids = numerator.get() / denominator.get();
                if ( numerator.get() % denominator.get() != 0 ) {
                    numGrids++;
                }
                int numElementsAdded = 0;

                int numRows = (int) Math.sqrt( denominator.get() );
                int numCellsPerGrid = denominator.get();

                int gridIndex = 0;
                int rowIndex = 0;
                int columnIndex = 0;
                int numElementsAddedPerGrid = 0;

                double gridWidth = ( numRows + 1 ) * 50;
                while ( numElementsAdded < numGrids * numCellsPerGrid ) {

                    Color color = numElementsAdded < numerator.get() ? FractionsIntroCanvas.FILL_COLOR : Color.white;
                    addChild( new PhetPPath( new Rectangle2D.Double( gridIndex * ( gridWidth - 20 ) + columnIndex * 50, rowIndex * 50, 50, 50 ), color, new BasicStroke( 2 ), Color.black ) );
                    numElementsAdded++;
                    numElementsAddedPerGrid++;

                    if ( numElementsAddedPerGrid == numCellsPerGrid ) {
                        gridIndex++;
                        rowIndex = 0;
                        columnIndex = 0;
                        numElementsAddedPerGrid = 0;
                    }
                    else if ( numElementsAddedPerGrid % numRows == 0 ) {
                        rowIndex++;
                        columnIndex = 0;
                    }
                    else {
                        columnIndex++;
                    }

                }
            }
        }.observe( numerator, denominator );
    }
}
