/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.neuron.model.MembraneDiffusionModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * This class defines a graph that shows the amount of sodium and potassium
 * in the upper and lower chambers of the membrane diffusion model.
 *  
 * @author John Blanco
 */
public class ConcentrationGraph extends PhetPNode {

	//------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Dimension2D size = new PDimension(80, 230);
	private static final PhetFont LABEL_FONT = new PhetFont(14, true);
	private static final Color MAIN_BACKGROUND_COLOR = new Color(255, 255, 204);

	private static float OUTLINE_STROKE_WIDTH = 1f;
	private static final Stroke OUTLINE_STROKE = new BasicStroke(OUTLINE_STROKE_WIDTH);
	private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
	private static final Color BAR_BACKGROUND_COLOR = Color.BLACK;
	private static final Color BAR_COLOR = Color.ORANGE;
	private static final double BAR_WIDTH_PROPORTION = 0.85;
	private static final double BAR_HEIGHT_PROPORTION = 0.55;
	private static final double MAX_VALUE = 50;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private MembraneDiffusionModel model;
	private PNode background;
	private PNode barBackground;
	private PhetPPath bar;
	private Rectangle2D barShape = new Rectangle2D.Double();
	private HTMLNode label;
	private double barWidth;
	private double maxBarHeight;
    private JButton closeButton;
    private PSwing closePSwing;
    private HTMLNode overflowText;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public ConcentrationGraph(final MembraneDiffusionModel model) {
		
		this.model = model;
		
		// Listen to the model for events that may matter to us.
//		model.addListener(new GeneNetworkModelAdapter(){
//			public void lactoseLevelChanged() {
//				updateBarSize();
//			}
//			public void lactoseMeterVisibilityStateChange() {
//				updateVisibility();
//			}
//		});
		
		// Create the overall background.
		background = new PhetPPath(new RoundRectangle2D.Double(0, 0, size.getWidth(), size.getHeight(), 8, 8),
				MAIN_BACKGROUND_COLOR, OUTLINE_STROKE, OUTLINE_STROKE_COLOR);
		addChild(background);

		// Calculate some values that will be used when layout out the
		// components that make up this node.
		barWidth = size.getWidth() * BAR_WIDTH_PROPORTION;
		maxBarHeight = size.getHeight() * BAR_HEIGHT_PROPORTION;
		double edgeOffset = (size.getWidth() - barWidth) / 2 + OUTLINE_STROKE_WIDTH / 2;
		
		// Add the button that will allow the user to close (actually hide) the meter.
		ImageIcon imageIcon = new ImageIcon( 
				PhetCommonResources.getInstance().getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON) );
		closeButton = new JButton( imageIcon );
		closeButton.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
//				model.setLactoseMeterVisible(false);
			}
		} );
		
		closePSwing = new PSwing( closeButton );
		closePSwing.setOffset(size.getWidth() - closeButton.getBounds().width - edgeOffset, edgeOffset / 2);
		closePSwing.addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
		addChild(closePSwing);
		
		// Create the background for the bar.
		barBackground = new PhetPPath(new Rectangle2D.Double(0, 0, barWidth, maxBarHeight), BAR_BACKGROUND_COLOR);
		barBackground.setOffset(edgeOffset, closePSwing.getFullBoundsReference().getMaxY() + edgeOffset / 2);
		addChild(barBackground);
		
		// Add the bar itself.  The shape will be set when updates occur.
		bar = new PhetPPath( BAR_COLOR );
		bar.setOffset((size.getWidth() - barWidth) / 2 + OUTLINE_STROKE_WIDTH / 2, 0);
		addChild(bar);
		
		// Add the text that will be shown if a value is set that exceeds the
		// supported range.
		overflowText = new HTMLNode("<html><center>Off<br>Chart</center></html>");
		overflowText.setFont(LABEL_FONT);
		overflowText.setHTMLColor(Color.BLUE);
		overflowText.setScale((barWidth - edgeOffset * 2) / overflowText.getFullBoundsReference().getWidth());
		overflowText.setOffset(size.getWidth() / 2 - overflowText.getFullBoundsReference().getWidth() / 2, 
				barBackground.getOffset().getY() + edgeOffset);
		overflowText.setVisible(false);
		addChild(overflowText);
		
		// Do the initial updates.
		updateBarSize();
		updateVisibility();
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

	private void updateBarSize(){
		double barHeight;

//		if (model.getLactoseLevel() > MAX_VALUE){
//			barHeight = maxBarHeight;
//			overflowText.setVisible(true);
//		}
//		else{
//			barHeight = (double)model.getLactoseLevel() / MAX_VALUE * maxBarHeight;
//			if (overflowText.getVisible()){
//				overflowText.setVisible(false);
//			}
//		}
//		barShape.setFrame(0, 0, barWidth, barHeight);
//		bar.setPathTo(barShape);
//		bar.setOffset(bar.getOffset().getX(),
//			barBackground.getBoundsReference().getMaxY() - barHeight + barBackground.getOffset().getY());
	}
	
	private void updateVisibility(){
//		setVisible(model.isLactoseMeterVisible());
	}
}
