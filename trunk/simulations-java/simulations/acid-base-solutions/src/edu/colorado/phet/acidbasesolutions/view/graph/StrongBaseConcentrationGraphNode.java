package edu.colorado.phet.acidbasesolutions.view.graph;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;


public class StrongBaseConcentrationGraphNode extends AbstractConcentrationGraphNode{

    private static final int BASE_INDEX = 0;
    private static final int METAL_INDEX = 1;
    
    public StrongBaseConcentrationGraphNode() {
        super();
        setMolecule( BASE_INDEX, ABSImages.MOH_MOLECULE, ABSSymbols.MOH, ABSConstants.MOH_COLOR );
        setMolecule( METAL_INDEX, ABSImages.M_PLUS_MOLECULE, ABSSymbols.M_PLUS, ABSConstants.M_COLOR );
    }
    
    public void setBaseConcentration( double concentration ) {
        setConcentration( BASE_INDEX, concentration );
    }
    
    public void setMetalConcentration( double concentration ) {
        setConcentration( METAL_INDEX, concentration );
    }
    
    public void setBaseLabel( String text ) {
        setLabel( BASE_INDEX, text );
    }
    
    public void setMetalLabel( String text ) {
        setLabel( METAL_INDEX, text );
    }
}
