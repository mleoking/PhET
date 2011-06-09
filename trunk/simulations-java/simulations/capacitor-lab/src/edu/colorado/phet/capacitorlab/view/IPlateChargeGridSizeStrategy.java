// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.view;

import java.awt.Dimension;

/**
 * Interface for all strategies used to determine the size of the grid used to represent plate charge.
 * <p>
 * Only one of these strategies is used. The others are kept for historical purposes,
 * so that we know what we tried, why it didn't work, and how we arrived at the final solution.
 * <p>
 * See TestPlateChargeLayout for a test application.
 */
public interface IPlateChargeGridSizeStrategy {

    /**
     * Gets the size of the grid.
     * The number of cells in the grid may be more or less than numberOfObjects.
     *
     * @param numberOfObjects number of objects to put on the plate
     * @param width width of the plate
     * @param height height of the plate
     * @return
     */
    public Dimension getGridSize( int numberOfObjects, double width, double height );

    /**
     * This factory determines the strategy used throughout the application.
     */
    public static class GridSizeStrategyFactory {
        public static IPlateChargeGridSizeStrategy createStrategy() {
            return new CCKStrategyWithRounding();
        }
    }

    /**
     * Strategy borrowed from CCK's view.piccolo.lifelike.CapacitorNode (r40140, line 207).
     * Problem:  This strategy works well when the plate is square, but falls apart
     * as the plate becomes narrow. When the plate is narrow, this strategy creates
     * grid sizes where one of the dimensions is zero (eg, 8x0, 0x14).
     */
    public static class CCKStrategy implements IPlateChargeGridSizeStrategy {

        public Dimension getGridSize( int numberOfObjects, double width, double height ) {
            double alpha = Math.sqrt( numberOfObjects / width / height );
            // casting here may result in some charges being thrown out, but that's OK
            int columns = (int)( width * alpha );
            int rows = (int)( height * alpha );
            return new Dimension( columns, rows );
        }
    }

    /**
     * Workaround for one of the known issues with CCKGridSizeStrategy.
     * Ensures that we don't have a grid size where exactly one of the dimensions is zero.
     * Problem: If numberOfCharges is kept constant, a plate with smaller
     * area but larger aspect ratio will display more charges.
     * For example, if charges=7, a 5x200mm plate will display 7 charges,
     * while a 200x200mm plate will only display 4 charges.
     */
    public static class ModifiedCCKStrategy extends CCKStrategy {

        public Dimension getGridSize( int numberOfObjects, double width, double height ) {
            Dimension gridSize = super.getGridSize( numberOfObjects, width, height );
            if ( gridSize.width == 0 && gridSize.height != 0 ) {
                gridSize.setSize( 1, numberOfObjects );
            }
            else if ( gridSize.width != 0 && gridSize.height == 0 ) {
                gridSize.setSize( numberOfObjects, 1 );
            }
            return gridSize;
        }
    }

    /**
     * Strategy developed by Sam Reid, here's how he described it:
     * The main change is to use rounding instead of clamping to get the rows and columns.
     * Also, for one row or column, it should be exact (similar to the intent of the ModifiedCCKGridSizeStrategy subclass).
     * It looks like it exhibits better (though understandably imperfect) behavior in the problem cases.
     * Also, as opposed to the previous versions, the visible number of objects can exceed the specified numberOfObjects.
     * This may be the best we can do if we are showing a rectangular grid of charges.  We could get the count exactly
     * right if we show some (or one) of the columns having different numbers of charges than the others, but then
     * it may look nonuniform (and would require more extensive changes to the sim).
     *
     * @author Sam Reid
     */
    public static class CCKStrategyWithRounding implements IPlateChargeGridSizeStrategy {

        public Dimension getGridSize( int numberOfObjects, double width, double height ) {
            int columns = 0;
            int rows = 0;
            if ( numberOfObjects > 0 ) {

                double alpha = Math.sqrt( numberOfObjects / width / height );
                columns = (int) ( Math.round( width * alpha ) );

                // compute rows 2 ways, choose the best fit
                final int rows1 = (int) ( Math.round( height * alpha ) );
                final int rows2 = (int) Math.round( numberOfObjects / (double) columns );
                if ( rows1 != rows2 ) {
                    int error1 = Math.abs( numberOfObjects - ( rows1 * columns ) );
                    int error2 = Math.abs( numberOfObjects - ( rows2 * columns ) );
                    rows = ( error1 < error2 ) ? rows1 : rows2;
                }
                else {
                    rows = rows1;
                }

                // handle boundary cases
                if ( columns == 0 ) {
                    columns = 1;
                    rows = numberOfObjects;
                }
                else if ( rows == 0 ) {
                    rows = 1;
                    columns = numberOfObjects;
                }
            }
            assert( columns >= 0 && rows >=0 );
            return new Dimension( columns, rows );
        }
    }
}