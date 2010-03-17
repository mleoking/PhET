/* Copyright 2010, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.model.LacOperonModel;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the Lactose Regulation tab.
 * 
 * @author John Blanco
 */
public class LactoseTransportCanvas extends GeneNetworkCanvas {
	
	private PNode cellMembraneLayer; 

	public LactoseTransportCanvas(LacOperonModel model) {
		
		super(model);
		
        // Add the cell membrane to the canvas.
		cellMembraneLayer = new PNode();
		addWorldChild(cellMembraneLayer);
        Rectangle2D cellMembraneRect = model.getCellMembraneRect();
        if (cellMembraneRect != null){
        	Rectangle2D transformedCellMembraneRect = getMvt().createTransformedShape(cellMembraneRect).getBounds2D();
        	GradientPaint paint = new GradientPaint(0f, (float)transformedCellMembraneRect.getCenterY(), Color.WHITE,
        			0f, (float)transformedCellMembraneRect.getBounds2D().getMaxY(), new Color(255, 100, 100), true);
        	cellMembraneLayer.addChild(new PhetPPath(transformedCellMembraneRect, paint, new BasicStroke(2f),
        			Color.BLACK));
        }
        
        // Add the DNA strand to the canvas.
        setDnaStrand(new DnaStrandNode(model.getDnaStrand(), getMvt(), getBackground()));

        // Add the tool box.
        setToolBox(new DnaSegmentToolboxWithLacYNode(this, model, getMvt()));
        
        // Add the lactose injector.
        LactoseInjectorNode lactoseInjector = new LactoseInjectorNode(model, getMvt());
        lactoseInjector.setOffset(-140, -40);
        setLactoseInjector(lactoseInjector);
        
        // Add the legend.
        setLegend(new MacroMoleculeLegend(model, this));
        
        // Add the lactose meter.
        LactoseMeter lactoseMeter = new LactoseMeter(model);
        lactoseMeter.setOffset(-140, 250);
        setLactoseMeter(lactoseMeter);
	}

}
