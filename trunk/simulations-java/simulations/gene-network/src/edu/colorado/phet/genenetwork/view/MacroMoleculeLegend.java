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
	private static final Font TITLE_FONT = new PhetFont(18, true);
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
	private PText title;
	private SimpleModelElementNode rnaPolymeraseLegendItem;
	private HTMLNode rnaPolymeraseCaption;
	private SimpleModelElementNode lacZLegendItem;
	private HTMLNode lacZCaption;
	private SimpleModelElementNode lacILegendItem;
	private HTMLNode lacICaption;
	private SimpleModelElementNode messengerRnaLegendItem;
	private HTMLNode messengerRnaCaption;
	private SimpleModelElementNode transformationArrowLegendItem;
	private HTMLNode transformationArrowCaption;
	
    //------------------------------------------------------------------------
    // Constructors
    //------------------------------------------------------------------------
    	
	public MacroMoleculeLegend(final IGeneNetworkModelControl model, final PhetPCanvas canvas){
		
		// Register for notifications of size change.
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
		
		lacILegendItem = new SimpleModelElementNode(new LacI(), MVT, false);
		lacILegendItem.setPickable(false);
		background.addChild(lacILegendItem);
		
		lacICaption = new HTMLNode(GeneNetworkStrings.LAC_I_LEGEND_CAPTION, LABEL_FONT, LABEL_COLOR);
		background.addChild(lacICaption);
		
		MessengerRna mRna = new LacIMessengerRna(15);
		mRna.setPredictibleShape();
		messengerRnaLegendItem = new SimpleModelElementNode(mRna, MVT, false);
		messengerRnaLegendItem.setPickable(false);
		background.addChild(messengerRnaLegendItem);
		
		messengerRnaCaption = new HTMLNode(GeneNetworkStrings.MESSENGER_RNA_LEGEND_CAPTION, LABEL_FONT, LABEL_COLOR);
		background.addChild(messengerRnaCaption);
		
		transformationArrowLegendItem = new SimpleModelElementNode(new TransformationArrow(null, 
				new Point2D.Double(0, 0), 5, false), MVT, false);
		transformationArrowLegendItem.setPickable(false);
		background.addChild(transformationArrowLegendItem);

		transformationArrowCaption = new HTMLNode(GeneNetworkStrings.TRANSFORMATION_ARROW_LEGEND_CAPTION, 
				LABEL_FONT, LABEL_COLOR);
		background.addChild(transformationArrowCaption);
		
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

		double width = 0;
		double height = 0;
		double xPos, yPos;
		
		// Position the title.
		xPos = background.getBoundsReference().width / 2;
		yPos = 5;
		title.setOffset(xPos - title.getFullBoundsReference().width / 2, yPos);
		width = Math.max(title.getFullBoundsReference().width, width);
		
		// Position the RNA polymerase.
		yPos = title.getFullBoundsReference().getMaxY() + 30;
		rnaPolymeraseLegendItem.setOffset(xPos, yPos);
		width = Math.max(rnaPolymeraseLegendItem.getFullBoundsReference().width, width);
		
		// Position polymerase caption.
		rnaPolymeraseCaption.setOffset(xPos - rnaPolymeraseCaption.getFullBoundsReference().width / 2,
				rnaPolymeraseLegendItem.getFullBoundsReference().getMaxY() + 3);
		width = Math.max(rnaPolymeraseCaption.getFullBoundsReference().width, width);
		
		// Position the LacZ.
		yPos = rnaPolymeraseCaption.getFullBoundsReference().getMaxY() + 40;
		lacZLegendItem.setOffset(xPos, yPos);
		width = Math.max(lacZLegendItem.getFullBoundsReference().width, width);
		
		// Position the LacZ caption.
		lacZCaption.setOffset(xPos - lacZCaption.getFullBoundsReference().width / 2,
				lacZLegendItem.getFullBoundsReference().getMaxY() + 5);
		width = Math.max(lacZCaption.getFullBoundsReference().width, width);
		
		// Position the LacI.
		yPos = lacZCaption.getFullBoundsReference().getMaxY() + 30;
		lacILegendItem.setOffset(xPos, yPos);
		width = Math.max(lacILegendItem.getFullBoundsReference().width, width);
		
		// Position the LacI caption.
		lacICaption.setOffset(xPos - lacICaption.getFullBoundsReference().width / 2,
				lacILegendItem.getFullBoundsReference().getMaxY() + 5);
		width = Math.max(lacICaption.getFullBoundsReference().width, width);
		
		// Position the messenger RNA.  Note the mRNA is a bit unique in that
		// it's offset if from the left side rather than the middle.
		yPos = lacICaption.getFullBoundsReference().getMaxY() + 35;
		messengerRnaLegendItem.setOffset(xPos - messengerRnaLegendItem.getFullBoundsReference().width / 2, yPos);
		width = Math.max(messengerRnaLegendItem.getFullBoundsReference().width, width);
		
		// Position the messenger RNA caption.
		messengerRnaCaption.setOffset(xPos - messengerRnaCaption.getFullBoundsReference().width / 2,
				messengerRnaLegendItem.getFullBoundsReference().getMaxY() + 5);
		width = Math.max(messengerRnaCaption.getFullBoundsReference().width, width);
		
		// Position the transformation arrow.
		yPos = messengerRnaCaption.getFullBoundsReference().getMaxY() + 35;
		transformationArrowLegendItem.setOffset(xPos, yPos);
		width = Math.max(transformationArrowLegendItem.getFullBoundsReference().width, width);
		
		// Position the transformation arrow caption.
		transformationArrowCaption.setOffset(xPos - transformationArrowCaption.getFullBoundsReference().width / 2,
				transformationArrowLegendItem.getFullBoundsReference().getMaxY() + 5);
		width = Math.max(transformationArrowCaption.getFullBoundsReference().width, width);
		
		// Expand the width by a fixed amount and set the height.
		width = width * 1.15;
		height = transformationArrowCaption.getFullBoundsReference().getMaxY() + 20;
		
		// Set the shape of the background.
		background.setPathTo(new RoundRectangle2D.Double(0, 0, width, height, 8, 8));
	}
	
	private void updateVisibility(IGeneNetworkModelControl model){
		setVisible(model.isLegendVisible());
	}
}
