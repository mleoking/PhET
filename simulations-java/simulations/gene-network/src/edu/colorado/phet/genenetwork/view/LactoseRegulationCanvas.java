/* Copyright 2010, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.LacOperonModel;

/**
 * Canvas for the Lactose Regulation tab.
 * 
 * @author John Blanco
 */
public class LactoseRegulationCanvas extends GeneNetworkCanvas {

	public LactoseRegulationCanvas(LacOperonModel model) {
		
		super(model);
		
        // Add the DNA strand to the canvas.
        setDnaStrand(new DnaStrandNode(model.getDnaStrand(), getMvt(), getBackground()));

        // Add the tool box.
        setToolBox(new BasicDnaSegmentToolBoxNode(this, model, getMvt()));
        
        // Add the lactose injector.
        LactoseInjectorNode lactoseInjector = 
        	new LactoseInjectorNode(model, getMvt(), -Math.PI/6, GeneNetworkStrings.LACTOSE_INJECTOR_LABEL);
        lactoseInjector.setOffset(50, 120);
        setLactoseInjector(lactoseInjector);
        
        // Add the legend.
        setLegend(new MacroMoleculeLegend(model, this));
        
        // Add the lactose meter.
        LactoseMeter lactoseMeter = new LactoseMeter(model);
        lactoseMeter.setOffset(-200, 250);
        setLactoseMeter(lactoseMeter);
	}

}
