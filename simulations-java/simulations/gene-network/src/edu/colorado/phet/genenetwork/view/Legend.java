package edu.colorado.phet.genenetwork.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.genenetwork.GeneNetworkStrings;
import edu.colorado.phet.genenetwork.model.LacIMessengerRna;
import edu.colorado.phet.genenetwork.model.LacZ;
import edu.colorado.phet.genenetwork.model.RnaPolymerase;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class represents the legend that allows the user to see the names of
 * the various items floating around in the cell.
 * 
 * @author John Blanco
 */
public class Legend extends PNode {
	
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

	private static final Stroke OUTLINE_STROKE = new BasicStroke(2f);
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final double SPACE_FROM_TOP_TO_FIRST_ITEM = 20;
	private static final Font LABEL_FONT = new PhetFont(16, true);
	private static final Color LABEL_COLOR = Color.BLACK;
	
	// A local model-view transform that is used to scale the simple model
	// elements that appear on the legend.  The size is empirically
	// determined, tweak as needed for optimal sizing.
	private static final ModelViewTransform2D MVT = new ModelViewTransform2D(new Rectangle2D.Double(-1, -1, 2, 2),
			new Rectangle2D.Double(-5, -5, 10, 10), true );
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private PPath background;
	private SimpleModelElementNode rnaPolymeraseLegendItem;
	private HTMLNode rnaPolymeraseCaption;
	private SimpleModelElementNode lacZLegendItem;
	private HTMLNode lacZCaption;
	private SimpleModelElementNode messengerRnaLegendItem;
	private HTMLNode messengerRnaCaption;
	
	
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
    	
	public Legend(final PhetPCanvas canvas){
		
		// Register for notifications of size change.
		canvas.addComponentListener(new ComponentAdapter() {
		    public void componentResized(ComponentEvent e) {
		    	updateLayout(canvas);
		    }
		});

		background = new PhetPPath(BACKGROUND_COLOR, OUTLINE_STROKE, Color.BLACK);
		addChild(background);
		
		rnaPolymeraseLegendItem = new SimpleModelElementNode(new RnaPolymerase(), MVT, false);
		rnaPolymeraseLegendItem.setPickable(false);
		background.addChild(rnaPolymeraseLegendItem);
		
		rnaPolymeraseCaption = new HTMLNode(GeneNetworkStrings.POLYMERASE_LEGEND_CAPTION, LABEL_FONT, LABEL_COLOR);
		background.addChild(rnaPolymeraseCaption);
		
		lacZLegendItem = new SimpleModelElementNode(new LacZ(), MVT, false);
		lacZLegendItem.setPickable(false);
		background.addChild(lacZLegendItem);
		
		lacZCaption = new HTMLNode(GeneNetworkStrings.LAC_Z_LEGEND_CAPTION, LABEL_FONT, LABEL_COLOR);
		background.addChild(lacZCaption);
		
		messengerRnaLegendItem = new SimpleModelElementNode(new LacIMessengerRna(15), MVT, false);
		messengerRnaLegendItem.setPickable(false);
		background.addChild(messengerRnaLegendItem);
		
		messengerRnaCaption = new HTMLNode(GeneNetworkStrings.MESSENGER_RNA_LEGEND_CAPTION, LABEL_FONT, LABEL_COLOR);
		background.addChild(messengerRnaCaption);
		
		// Do initial layout.
		updateLayout(canvas);
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------
	
	private void updateLayout(JComponent parent){
		// For now (Dec 21, 2009), the box stays at the default size when the
		// canvas is resized.  This may change after review with the
		// designers.

		double width = 0;
		double height = 0;
		double xPos, yPos;
		
		// Position the RNA polymerase.
		xPos = background.getBoundsReference().width / 2;
		yPos = SPACE_FROM_TOP_TO_FIRST_ITEM + (rnaPolymeraseLegendItem.getOffset().getY() - 
				rnaPolymeraseLegendItem.getFullBoundsReference().getMinY());
		rnaPolymeraseLegendItem.setOffset(xPos, yPos);
		width = Math.max(rnaPolymeraseLegendItem.getFullBoundsReference().width, width);
		
		// Position polymerase caption.
		rnaPolymeraseCaption.setOffset(xPos - rnaPolymeraseCaption.getFullBoundsReference().width / 2,
				rnaPolymeraseLegendItem.getFullBoundsReference().getMaxY() + 6);
		width = Math.max(rnaPolymeraseCaption.getFullBoundsReference().width, width);
		
		// Position the LacZ.
		yPos = rnaPolymeraseCaption.getFullBoundsReference().getMaxY() + 35;
		lacZLegendItem.setOffset(xPos, yPos);
		width = Math.max(lacZLegendItem.getFullBoundsReference().width, width);
		
		// Position the LacZ caption.
		lacZCaption.setOffset(xPos - lacZCaption.getFullBoundsReference().width / 2,
				lacZLegendItem.getFullBoundsReference().getMaxY() + 5);
		width = Math.max(lacZCaption.getFullBoundsReference().width, width);
		
		// Position the messenger RNA.  Note the mRNA is a bit unique in that
		// it's offset if from the left side rather than the middle.
		yPos = lacZCaption.getFullBoundsReference().getMaxY() + 35;
		messengerRnaLegendItem.setOffset(xPos - messengerRnaLegendItem.getFullBoundsReference().width / 2, yPos);
		width = Math.max(messengerRnaLegendItem.getFullBoundsReference().width, width);
		
		// Position the messenger RNA caption.
		messengerRnaCaption.setOffset(xPos - messengerRnaCaption.getFullBoundsReference().width / 2,
				messengerRnaLegendItem.getFullBoundsReference().getMaxY() + 5);
		width = Math.max(messengerRnaCaption.getFullBoundsReference().width, width);
		
		// Expand the width by a fixed amount.
		width = width * 1.15;
		height = messengerRnaCaption.getFullBoundsReference().getMaxY() + 20;
		
		// Set the shape of the background.
		background.setPathTo(new RoundRectangle2D.Double(0, 0, width, height, 8, 8));
	}
}
