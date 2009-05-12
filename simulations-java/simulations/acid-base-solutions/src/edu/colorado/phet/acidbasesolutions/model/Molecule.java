package edu.colorado.phet.acidbasesolutions.model;

import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;


public abstract class Molecule {

    private final String name;
    private final String symbol;
    
    protected Molecule( String name, String symbol ) {
        this.name = name;
        this.symbol = symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public String toString() {
        return HTMLUtils.toHTMLString( name + " (" + symbol + ")" );
    }
}
