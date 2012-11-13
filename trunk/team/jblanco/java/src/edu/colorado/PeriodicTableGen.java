package edu.colorado;


import edu.colorado.phet.common.piccolophet.nodes.periodictable.SymbolTable;

public class PeriodicTableGen {
    public static void main( String [] args ){
        for ( int i = 0; i < 10; i++ ){
            System.out.println(SymbolTable.getSymbol( i ));
        }
    }
}
