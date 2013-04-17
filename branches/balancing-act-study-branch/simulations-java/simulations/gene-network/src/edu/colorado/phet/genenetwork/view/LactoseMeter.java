// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.genenetwork.view;

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
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.Galactose;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.Glucose;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.colorado.phet.genenetwork.model.IModelElementListener;
import edu.colorado.phet.genenetwork.model.ModelElementListenerAdapter;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 *
 * @author John Blanco
 */
public class LactoseMeter extends PhetPNode {

	//------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Dimension2D size = new PDimension(80, 245);
	private static final PhetFont LABEL_FONT = new PhetFont(14, true);
	private static final Color MAIN_BACKGROUND_COLOR = new Color(255, 255, 204);

	private static float OUTLINE_STROKE_WIDTH = 1f;
	private static final Stroke OUTLINE_STROKE = new BasicStroke(OUTLINE_STROKE_WIDTH);
	private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
	private static final Color BAR_BACKGROUND_COLOR = Color.BLACK;
	private static final Color BAR_COLOR = Color.ORANGE;
	private static final double BAR_WIDTH_PROPORTION = 0.85;
	private static final double BAR_HEIGHT_PROPORTION = 0.50;
	private static final double MAX_VALUE = 50;

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private final IGeneNetworkModelControl model;
	private final PNode background;
	private final PNode barBackground;
	private final PhetPPath bar;
	private final Rectangle2D barShape = new Rectangle2D.Double();
	private final HTMLNode label;
	private final double barWidth;
	private final double maxBarHeight;
    private final JButton closeButton;
    private final PSwing closePSwing;
    private final HTMLNode overflowText;

	private final IModelElementListener glucoseListener = new ModelElementListenerAdapter(){
		@Override
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
			@Override
            public void lactoseLevelChanged() {
				updateBarSize();
			}
			@Override
            public void lactoseMeterVisibilityStateChange() {
				updateVisibility();
			}
		});

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
				model.setLactoseMeterVisible(false);
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

		// Create a molecule of lactose to show near the label.
		double mvtSizeFactor = size.getHeight() / 40; // Empirically determined, tweak as needed.
		ModelViewTransform2D mvt = new ModelViewTransform2D(new Rectangle2D.Double(-1, -1, 2, 2),
				new Rectangle2D.Double(-mvtSizeFactor, -mvtSizeFactor, mvtSizeFactor * 2, mvtSizeFactor * 2), true );
		PNode lactoseNode = createLactoseNode(mvt);
		lactoseNode.setOffset(size.getWidth() / 2,
				barBackground.getFullBoundsReference().getMaxY() + lactoseNode.getFullBoundsReference().height / 2 + edgeOffset);
		addChild(lactoseNode);

		// Create the label.
		label = new HTMLNode(GeneNetworkStrings.LACTOSE_METER_CAPTION, Color.BLACK, LABEL_FONT);
		addChild(label);

		// Define the rectangle where the label needs to fit, which is just
		// below the bar.
		Rectangle2D labelAreaRect = new Rectangle2D.Double(
				edgeOffset,
				lactoseNode.getFullBoundsReference().getMaxY(),
				size.getWidth() - 2 * edgeOffset,
				size.getHeight() - lactoseNode.getFullBoundsReference().getMaxY() - edgeOffset);

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

		// Add the text that will be shown if a value is set that exceeds the
		// supported range.
		overflowText = new HTMLNode( GeneNetworkStrings.OFF_CHART );
		overflowText.setFont(LABEL_FONT);
		overflowText.setHTMLColor(Color.BLUE);
		overflowText.setScale((barWidth - edgeOffset * 2) / overflowText.getFullBoundsReference().getWidth());
		overflowText.setOffset(size.getWidth() / 2 - overflowText.getFullBoundsReference().getWidth() / 2,
				barBackground.getOffset().getY() + edgeOffset);
		overflowText.setVisible(false);
		addChild(overflowText);

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
		double barHeight;

		if (model.getLactoseLevel() > MAX_VALUE){
			barHeight = maxBarHeight;
			overflowText.setVisible(true);
		}
		else{
			barHeight = model.getLactoseLevel() / MAX_VALUE * maxBarHeight;
			if (overflowText.getVisible()){
				overflowText.setVisible(false);
			}
		}
		barShape.setFrame(0, 0, barWidth, barHeight);
		bar.setPathTo(barShape);
		bar.setOffset(bar.getOffset().getX(),
			barBackground.getBoundsReference().getMaxY() - barHeight + barBackground.getOffset().getY());
	}

	private void updateVisibility(){
		setVisible(model.isLactoseMeterVisible());
	}

    /**
     * Generate an image of Lactose.  This is necessary because Lactose exists
     * as a combination of two simple model elements in the model, so there is
     * no model element that can be created easily as can be done for the
     * simple model elements.
     */
    private PNode createLactoseNode(ModelViewTransform2D mvt){
    	PNode lactoseNode = new PNode();
    	PNode glucoseNode = new SimpleModelElementNode(new Glucose(), mvt, false);
    	glucoseNode.setOffset(-glucoseNode.getFullBoundsReference().width / 2, 0);
    	lactoseNode.addChild(glucoseNode);
    	PNode galactoseNode = new SimpleModelElementNode(new Galactose(), mvt, false);
    	galactoseNode.setOffset(galactoseNode.getFullBoundsReference().width / 2, 0);
    	lactoseNode.addChild(galactoseNode);
    	lactoseNode.setPickable(false);
    	lactoseNode.setChildrenPickable(false);
    	return lactoseNode;
    }
}
