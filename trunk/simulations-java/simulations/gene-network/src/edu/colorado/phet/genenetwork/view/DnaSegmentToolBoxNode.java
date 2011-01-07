// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class represents the "toolbox" for DNA segements that can be grabbed
 * by the user and placed into a DNA strand.
 * 
 * @author John Blanco
 */
public abstract class DnaSegmentToolBoxNode extends PNode {
	
    //----------------------------------------------------------------------------
    // Class Data
    //----------------------------------------------------------------------------
	
	// Defines the width of the tool box as a proportion of the parent window's
	// width.
	protected static final double WIDTH_PROPORTION = 0.8;
	
	// Aspect ratio, width divided by height, from which the height will be
	// calculated.  Smaller numbers means a taller box.
	protected static final double ASPECT_RATIO = 8; 
	
	// Offset from the bottom of the window.
	protected static final double OFFSET_FROM_BOTTOM = 10;
	
	// Background color used to fill the box.
	private static final Color BACKGROUND_COLOR = Color.WHITE; 
	
	// Outline stroke.
	protected static final float OUTLINE_STROKE_WIDTH = 2f;
	private static final Stroke OUTLINE_STROKE = new BasicStroke(OUTLINE_STROKE_WIDTH);
	
	// Font for the check boxes.  For consistency with the nodes that are placed
	// in the tool box, we load a serif font if no preferred font is
	// specified.  For more information about why this is done, please look at
	// the code for the nodes that are added to the tool box.
	private static final Font CHECK_BOX_FONT = GeneNetworkFontFactory.getFont(16, Font.PLAIN);
	
	
    //----------------------------------------------------------------------------
    // Instance Data
    //----------------------------------------------------------------------------
	
	// Main outline of the box.
	protected PhetPPath boxNode;

	// The various grabbable things in the box.
	protected LacIPromoterToolBoxNode lacIPromoter;
	protected LacZGeneToolBoxNode lacZGene;
	protected LacIGeneToolBoxNode lacIGene;
	protected LacOperatorToolBoxNode lacIBindingRegion;
	protected LacPromoterToolBoxNode lacPromoter;
	
	// The check box for controlling legend visibility.
	protected PSwing legendControlCheckBoxPSwing;
	
	// The check box for controlling lactose meter visibility.
	protected PSwing lactoseMeterCheckBoxPSwing;
	
	// Divider line that goes between the DNA segments and the check boxes.
	PPath dividerLine;
	
	// Reference to the model.
	protected IGeneNetworkModelControl model;
	
	// Reference to the model-view transform.
	protected ModelViewTransform2D mvt;
	
	// Reference to the canvas upon which we are placed.
	protected PhetPCanvas canvas;

    //----------------------------------------------------------------------------
    // Constructor(s)
    //----------------------------------------------------------------------------
	
	public DnaSegmentToolBoxNode(final PhetPCanvas canvas, final IGeneNetworkModelControl model, ModelViewTransform2D mvt) {
		
		this.canvas = canvas;
		this.model = model;
		this.mvt = mvt;
		
		// Register for events indicating that the parent window was resized.
		canvas.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	updateLayout(canvas);
		    }
		});
		
		// Create the surrounding box.
		boxNode = new PhetPPath(new RoundRectangle2D.Double(0, 0, canvas.getWidth(), 100, 15, 15), BACKGROUND_COLOR,
				OUTLINE_STROKE, Color.BLACK);
		addChild(boxNode);
		
		// Create the grabbable items in the box.
		lacIPromoter = new LacIPromoterToolBoxNode(model, mvt, canvas);
		addChild(lacIPromoter);
		lacZGene = new LacZGeneToolBoxNode(model, mvt, canvas);
		addChild(lacZGene);
		lacIGene = new LacIGeneToolBoxNode(model, mvt, canvas);
		addChild(lacIGene);
		lacIBindingRegion = new LacOperatorToolBoxNode(model, mvt, canvas);
		addChild(lacIBindingRegion);
		lacPromoter = new LacPromoterToolBoxNode(model, mvt, canvas);
		addChild(lacPromoter);
		
		// Add a check box for enabling/disabling the lactose meter.
		final JCheckBox lactoseMeterControlCheckBox = new JCheckBox(GeneNetworkStrings.LACTOSE_METER_VISIBILITY_CONTROL_CAPTION);
		lactoseMeterControlCheckBox.setFont(CHECK_BOX_FONT);
		lactoseMeterControlCheckBox.setBackground(BACKGROUND_COLOR);
		lactoseMeterControlCheckBox.setSelected(model.isLegendVisible());
		lactoseMeterControlCheckBox.setFocusable(false);
		lactoseMeterControlCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				model.setLactoseMeterVisible(lactoseMeterControlCheckBox.isSelected());
			}
		});
		lactoseMeterCheckBoxPSwing = new PSwing(lactoseMeterControlCheckBox);
		addChild(lactoseMeterCheckBoxPSwing);
		
		// Add a check box for enabling/disabling the legend.
		final JCheckBox legendControlCheckBox = new JCheckBox(GeneNetworkStrings.LEGEND_VISIBILITY_CONTROL_CAPTION);
		legendControlCheckBox.setFont(CHECK_BOX_FONT);
		legendControlCheckBox.setBackground(BACKGROUND_COLOR);
		legendControlCheckBox.setSelected(model.isLegendVisible());
		legendControlCheckBox.setFocusable(false);
		legendControlCheckBox.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				model.setLegendVisible(legendControlCheckBox.isSelected());
			}
		});
		legendControlCheckBoxPSwing = new PSwing(legendControlCheckBox);
		addChild(legendControlCheckBoxPSwing);
		
		// Add a divider line between the DNA segments and the controls.
		dividerLine = new PhetPPath(OUTLINE_STROKE, Color.LIGHT_GRAY);
		addChild(dividerLine);
		
		// Listen to the model for changes to the setting for the legend
		// visibility.
		model.addListener(new GeneNetworkModelAdapter(){
			public void legendVisibilityStateChange() { 
				legendControlCheckBox.setSelected(model.isLegendVisible());
			}
			public void lactoseMeterVisibilityStateChange() {
				lactoseMeterControlCheckBox.setSelected(model.isLactoseMeterVisible());
			}
		});
	}
	
    //----------------------------------------------------------------------------
    // Methods
    //----------------------------------------------------------------------------
	
	protected abstract void updateLayout(JComponent parent);
	
}
