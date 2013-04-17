package edu.colorado;

import edu.colorado.phet.common.piccolophet.nodes.periodictable.SymbolTable;

// Class that generates a simple HTML periodic table.
public class PeriodicTableGen {
    public static void main( String[] args ) {
        int numRows = 7;
        int numColumns = 18;
        int atomicNumber = 1;

        System.out.println("<table>");

        // First row, just H and He.
        System.out.println( "<tr>" );
        outputCellWithSymbol( SymbolTable.getSymbol( atomicNumber++ ) );
        outputBlankCells( 16 );
        outputCellWithSymbol( SymbolTable.getSymbol( atomicNumber++ ) );
        System.out.println( "</tr>" );

        // 2nd and 3rd rows have the same format.
        for ( int i = 0; i < 2; i++ ) {
            System.out.println( "<tr>" );
            outputCellWithSymbol( SymbolTable.getSymbol( atomicNumber++ ) );
            outputCellWithSymbol( SymbolTable.getSymbol( atomicNumber++ ) );
            outputBlankCells( 10 );
            for ( int j = 0; j < 6; j++ ) {
                outputCellWithSymbol( SymbolTable.getSymbol( atomicNumber++ ) );
            }
            System.out.println( "</tr>" );
        }

        // Output the next several rows as a grid with no gaps.
        for ( int row = 3; row < numRows - 1; row++ ) {
            System.out.println( "<tr>" );
            for ( int column = 0; column < numColumns; column++ ) {
                outputCellWithSymbol( SymbolTable.getSymbol( atomicNumber++ ) );
                if ( atomicNumber == 58 ) {
                    // Use a single entry to represent the lanthanide series.
                    atomicNumber += 14;
                }
            }
            System.out.println( "</tr>" );
        }

        // Last row is partial.
        System.out.println( "<tr>" );
        while ( atomicNumber < SymbolTable.ELEMENT_SYMBOL_TABLE.length ) {
            if ( atomicNumber == 90 ) {
                // Use a single entry to represent the actinide series.
                atomicNumber += 14;
            }
            outputCellWithSymbol( SymbolTable.getSymbol( atomicNumber++ ) );
        }
        System.out.println( "</tr>" );
        System.out.println("</table>");

    }

    private static void outputBlankCells( int numCells ) {
        for ( int i = 0; i < numCells; i++ ) {
            System.out.println( "<td></td>" );
        }
    }

    private static void outputCellWithSymbol( String symbol ) {
        System.out.println( "<td data-symbol=" + "\"" + symbol + "\"" + ">" + symbol + "</td>" );
    }
}
