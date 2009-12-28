package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.GeneNetworkModelAdapter;
import edu.colorado.phet.genenetwork.model.IGeneNetworkModelControl;
import edu.colorado.phet.genenetwork.model.LacI;
import edu.colorado.phet.genenetwork.model.LacIMessengerRna;
import edu.colorado.phet.genenetwork.model.LacZ;
import edu.colorado.phet.genenetwork.model.MessengerRna;
import edu.colorado.phet.genenetwork.model.RnaPolymerase;
import edu.colorado.phet.genenetwork.model.TransformationArrow;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class represents the legend that allows the user to see the names of
 * the various items floating around in the cell.
 * 
 * @author John Blanco
 */
public class MacroMoleculeLegend extends PNode {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Stroke OUTLINE_STROKE = new BasicStroke(2f);
	private static final Color BACKGROUND_COLOR = new Color(255, 255, 220);
	private static final Font TITLE_FONT = new PhetFont(20, true);
	private static final double ICON_TO_CAPTION_HORIZONTAL_SPACING = 10;
	private static final double INTER_LEGEND_TIME_VERTICAL_SPACING = 10;
	private static final double SIDE_PADDING = 4;
	
	// A local model-view transform that is used to scale the simple model
	// elements that appear on the legend.  The size is empirically
	// determined, tweak as needed for optimal sizing.
	private static final ModelViewTransform2D MVT = new ModelViewTransform2D(new Rectangle2D.Double(-1, -1, 2, 2),
			new Rectangle2D.Double(-5, -5, 10, 10), true );
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private PPath background;
	private PText title;
	private ArrayList<LegendEntry> legendEntries = new ArrayList<LegendEntry>();
	
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
    	
	public MacroMoleculeLegend(final IGeneNetworkModelControl model, final PhetPCanvas canvas){
		
		// Register for notifications of size change from the canvas.
		canvas.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	updateLayout(canvas);
		    }
		});
		
		// Register for notifications that can affect this node's visibility.
		model.addListener(new GeneNetworkModelAdapter(){
			public void legendVisibilityStateChange(){
				updateVisibility(model);
			}
		});

		background = new PhetPPath(BACKGROUND_COLOR, OUTLINE_STROKE, Color.BLACK);
		addChild(background);
		
		title = new PText(GeneNetworkStrings.MACRO_MOLECULE_LEGEND_TITLE);
		title.setFont(TITLE_FONT);
		addChild(title);
		
		// Create the various legend items and add them to the list of items.
		SimpleModelElementNode icon;
		
		icon = new SimpleModelElementNode(new RnaPolymerase(), MVT, false);
		legendEntries.add(new LegendEntry(icon, GeneNetworkStrings.POLYMERASE_LEGEND_CAPTION));
		
		icon = new SimpleModelElementNode(new LacZ(), MVT, false);
		legendEntries.add(new LegendEntry(icon, GeneNetworkStrings.LAC_Z_LEGEND_CAPTION));
		
		icon = new SimpleModelElementNode(new LacI(), MVT, false);
		legendEntries.add(new LegendEntry(icon, GeneNetworkStrings.LAC_I_LEGEND_CAPTION));
		
		MessengerRna mRna = new LacIMessengerRna(15);
		mRna.setPredictibleShape();
		icon = new SimpleModelElementNode(mRna, MVT, false);
		legendEntries.add(new LegendEntry(icon, GeneNetworkStrings.MESSENGER_RNA_LEGEND_CAPTION));;
		
		icon = new SimpleModelElementNode(new TransformationArrow(null, new Point2D.Double(0, 0), 5, false), MVT,
				false);
		legendEntries.add(new LegendEntry(icon, GeneNetworkStrings.TRANSFORMATION_ARROW_LEGEND_CAPTION));
		
		// Add all items to the node.
		for (LegendEntry le : legendEntries){
			background.addChild(le.getIcon());
			background.addChild(le.getCaption());
		}
		
		// Do initial layout.
		updateLayout(canvas);
		
		// Update visibility.
		updateVisibility(model);
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	private void updateLayout(JComponent parent){
		// For now (Dec 21, 2009), the box stays at the default size when the
		// canvas is resized.  This may change after review with the
		// designers.

		double widestIconWidth = 0;
		double widestCaptionWidth = 0;
		
		for (LegendEntry legendEntry : legendEntries ){
			widestIconWidth = Math.max(legendEntry.getIcon().getFullBoundsReference().getWidth(), widestIconWidth);
			widestCaptionWidth = Math.max(legendEntry.getCaption().getFullBoundsReference().getWidth(), widestCaptionWidth);
		}
		
		double legendWidth = SIDE_PADDING * 2 + widestIconWidth + ICON_TO_CAPTION_HORIZONTAL_SPACING + widestCaptionWidth;
		double centerOfIconColumn = SIDE_PADDING + widestIconWidth / 2;
		double centerOfCaptionColumn = SIDE_PADDING + widestCaptionWidth + ICON_TO_CAPTION_HORIZONTAL_SPACING +
			widestCaptionWidth / 2;
		double yPos;
		
		// Position the title.
		yPos = 5;
		title.setOffset(legendWidth / 2 - title.getFullBoundsReference().width / 2, yPos);
		legendWidth = Math.max(title.getFullBoundsReference().width, legendWidth);
		yPos = title.getFullBoundsReference().getMaxY();
		
		// Position each legend item.
		for (LegendEntry legendEntry : legendEntries ){
			
			yPos += INTER_LEGEND_TIME_VERTICAL_SPACING;
			double centerOfRow = yPos + legendEntry.getHeight();
			
			// Position the icon.  This is a bit tricky since the nodes are
			// generally centered around their position.
			PNode icon = legendEntry.getIcon();
			icon.setOffset(0, 0);
			double minX = icon.getFullBoundsReference().getMinX();
			double iconWidth = icon.getFullBoundsReference().getWidth();
			double minY = icon.getFullBoundsReference().getMinY();
			double iconHeight = icon.getFullBoundsReference().getHeight();
			icon.setOffset(centerOfIconColumn - iconWidth / 2 - minX, centerOfRow - iconHeight / 2 - minY);
			
			// Position the caption.
			PNode caption = legendEntry.getCaption();
			caption.setOffset(centerOfCaptionColumn - caption.getFullBoundsReference().width / 2,
					centerOfRow - caption.getFullBoundsReference().height / 2);
			
			// Update the running vertical position tracker.
			yPos = Math.max(icon.getFullBoundsReference().getMaxY(), caption.getFullBoundsReference().getMaxY());
		}
		
		// Set the shape of the background.
		background.setPathTo(new RoundRectangle2D.Double(0, 0, legendWidth, yPos, 8, 8));
	}
	
	private void updateVisibility(IGeneNetworkModelControl model){
		setVisible(model.isLegendVisible());
	}
	
	/**
	 * A very simple class that bundles together two PNodes, one that is a
	 * graphical representation of an item and the other that is the
	 * associated caption. 
	 */
	private static class LegendEntry {

		private static final Font LABEL_FONT = new PhetFont(16, false);
		private static final Color LABEL_COLOR = Color.BLACK;

		private final PNode icon;
		private final PNode caption;
		
		public LegendEntry(PNode icon, String captionText) {
			this.icon = icon;
			icon.setPickable(false);
			this.caption = new HTMLNode(captionText, LABEL_FONT, LABEL_COLOR);
		}

		public PNode getIcon() {
			return icon;
		}

		public PNode getCaption() {
			return caption;
		}
		
		public double getHeight(){
			return Math.max(icon.getFullBoundsReference().height, caption.getFullBoundsReference().height);
		}
	}
}
