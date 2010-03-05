/* Copyright 2010, University of Colorado */

package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.Glucose;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.colorado.phet.genenetwork.model.IModelElementListener;
import edu.colorado.phet.genenetwork.model.ModelElementListenerAdapter;
import edu.colorado.phet.genenetwork.model.SimpleModelElement;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * 
 * @author John Blanco
 */
public class LactoseMeter extends PNode {

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
	private static final Color BAR_COLOR = Color.BLUE;
	private static final double BAR_WIDTH_PROPORTION = 0.9;
	private static final double BAR_HEIGHT_PROPORTION = 0.6;
	private static final double MAX_VALUE = 50;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private IGeneNetworkModelControl model;
	private PNode background;
	private PNode barBackground;
	private PhetPPath bar;
	private Rectangle2D barShape = new Rectangle2D.Double();
	private HTMLNode label;
	private double barWidth;
	private double maxBarHeight;
    private JButton closeButton;
    private PSwing closePSwing;
	
	private IModelElementListener glucoseListener = new ModelElementListenerAdapter(){
		public void removedFromModel(){
			updateBarSize();
		}
	};
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public LactoseMeter(final IGeneNetworkModelControl model) {
		
		this.model = model;
		
		// Listen to the model for events that may matter to us.
		model.addListener(new GeneNetworkModelAdapter(){
			public void modelElementAdded(SimpleModelElement modelElement) {
				if (modelElement instanceof Glucose){
					// We assume that the level of glucose is the same as the
					// level of lactose.
					((Glucose)modelElement).addListener(glucoseListener);
					updateBarSize();
				}
			}
			public void lactoseMeterVisibilityStateChange() {
				updateVisibility();
			}
		});
		
		// Create the overall background.
		background = new PhetPPath(new Rectangle2D.Double(0, 0, size.getWidth(), size.getHeight()),
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
				model.setLactoseMeterVisible(false);
			}
		} );
		
		closePSwing = new PSwing( closeButton );
		closePSwing.setOffset(size.getWidth() - closeButton.getBounds().width - edgeOffset, edgeOffset / 2);
		addChild(closePSwing);
		
		// Create the background for the bar.
		barBackground = new PhetPPath(new Rectangle2D.Double(0, 0, barWidth, maxBarHeight), BAR_BACKGROUND_COLOR);
		barBackground.setOffset(edgeOffset, closePSwing.getFullBoundsReference().getMaxY() + edgeOffset / 2);
		addChild(barBackground);
		
		// Create the label.
		// TODO: i18n
		label = new HTMLNode("<html><center>Lactose<br>Level<center></html>", Color.BLACK, LABEL_FONT);
		addChild(label);

		// Define the rectangle where the label needs to fit, which is just
		// below the bar.
		Rectangle2D labelAreaRect = new Rectangle2D.Double(
				edgeOffset,
				barBackground.getFullBoundsReference().getMaxY() + edgeOffset,
				size.getWidth() - 2 * edgeOffset,
				size.getHeight() - barBackground.getFullBoundsReference().getMaxY() - 2 * edgeOffset);
		
		// Scale the label to fit.
		double scale = Math.min(labelAreaRect.getWidth() / label.getWidth(),
				labelAreaRect.getHeight() / label.getHeight());
		label.setScale(scale);
		label.setOffset(labelAreaRect.getCenterX() - label.getFullBoundsReference().width / 2, 
			labelAreaRect.getCenterY() - label.getFullBoundsReference().height / 2);
		
		// Add the bar itself.  The shape will be set when updates occur.
		bar = new PhetPPath( BAR_COLOR );
		bar.setOffset((size.getWidth() - barWidth) / 2 + OUTLINE_STROKE_WIDTH / 2, 0);
		addChild(bar);
		
		// Register to listen for updates from each existing glucose.  We are
		// generally only interested in knowing when it gets removed from the
		// model.
		for (Glucose glucose : model.getGlucoseList()){
			glucose.addListener(glucoseListener);
		}
		
		// Do the initial updates.
		updateBarSize();
		updateVisibility();
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

	private void updateBarSize(){
		double barHeight = Math.min((double)model.getGlucoseList().size() / MAX_VALUE, 1) * maxBarHeight; 
		barShape.setFrame(0, 0, barWidth, barHeight);
		bar.setPathTo(barShape);
		bar.setOffset(bar.getOffset().getX(),
			barBackground.getBoundsReference().getMaxY() - barHeight + barBackground.getOffset().getY());
	}
	
	private void updateVisibility(){
		setVisible(model.isLactoseMeterVisible());
	}
}
