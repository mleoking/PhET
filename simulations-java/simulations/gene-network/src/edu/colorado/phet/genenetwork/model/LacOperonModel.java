/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.genenetwork.model;

import java.util.ArrayList;



/**
 * Model template.
 */
public class LacOperonModel {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GeneNetworkClock clock;
    private ArrayList<SimpleModelElement> simpleModelElements = new ArrayList<SimpleModelElement>();

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public LacOperonModel( GeneNetworkClock clock ) {
        super();
        
        this.clock = clock;        
    }

    //----------------------------------------------------------------------------
    // Accessors
    //----------------------------------------------------------------------------
    
    public GeneNetworkClock getClock() {
        return clock;
    }

    //----------------------------------------------------------------------------
    // Other Methods
    //----------------------------------------------------------------------------
    
    private void addModelElement(SimpleModelElement modelElement){
    	simpleModelElements.add(modelElement);
    }
}
