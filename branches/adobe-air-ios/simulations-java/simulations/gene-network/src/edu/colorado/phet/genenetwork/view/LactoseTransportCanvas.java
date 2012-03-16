// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.LacOperonModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Canvas for the Lactose Regulation tab.
 * 
 * @author John Blanco
 */
public class LactoseTransportCanvas extends GeneNetworkCanvas {
	

	public LactoseTransportCanvas(LacOperonModel model) {
		
		super(model);
		
        // Add the cell membrane to the canvas.
        Rectangle2D cellMembraneRect = model.getCellMembraneRect();
    	Rectangle2D transformedCellMembraneRect = getMvt().createTransformedShape(cellMembraneRect).getBounds2D();
    	GradientPaint paint = new GradientPaint(0f, (float)transformedCellMembraneRect.getCenterY(), Color.WHITE,
    			0f, (float)transformedCellMembraneRect.getBounds2D().getMaxY(), new Color(255, 100, 100), true);
    	PNode cellMembrane = new PhetPPath(transformedCellMembraneRect, paint, new BasicStroke(2f), Color.BLACK);
    	cellMembrane.setTransparency(0.7f);
        PText cellMembraneLabel = new PText(GeneNetworkStrings.CELL_MEMBRANE_LABEL);
        cellMembraneLabel.setFont(new PhetFont(18, true));
        cellMembraneLabel.setOffset(getMvt().modelToViewXDouble(model.getInteriorMotionBounds().getMinX()), 
        		transformedCellMembraneRect.getCenterY() - cellMembraneLabel.getFullBoundsReference().height / 2); 
        cellMembrane.addChild(cellMembraneLabel);
        setCellMembraneNode(cellMembrane);
        
        // Add the DNA strand to the canvas.
        setDnaStrand(new DnaStrandNode(model.getDnaStrand(), getMvt(), getBackground()));

        // Add the tool box.
        setToolBox(new DnaSegmentToolboxWithLacYNode(this, model, getMvt()));
        
        // Add the lactose injector.
        LactoseInjectorNode lactoseInjector = new LactoseInjectorNode(model, getMvt(), 0);
        lactoseInjector.setOffset(50,
        		cellMembrane.getFullBoundsReference().getMinY() - lactoseInjector.getFullBoundsReference().height / 2 - 4);
        setLactoseInjector(lactoseInjector);
        
        // Add the legend.
        setLegend(new MacroMoleculeLegend(model, this, true));
        
        // Add the lactose meter.
        LactoseMeter lactoseMeter = new LactoseMeter(model);
        lactoseMeter.setOffset(-200, 250);
        setLactoseMeter(lactoseMeter);
	}
}
