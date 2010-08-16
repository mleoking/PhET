/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Dimension2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.colorado.phet.membranediffusion.model.ParticleType;
import edu.colorado.phet.membranediffusion.model.PotassiumIon;
import edu.colorado.phet.membranediffusion.model.SodiumIon;
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

	private static final Dimension2D size = new PDimension(150, 200);
	private static final Color MAIN_BACKGROUND_COLOR = Color.WHITE;

	private static float OUTLINE_STROKE_WIDTH = 1f;
	private static final Stroke OUTLINE_STROKE = new BasicStroke(OUTLINE_STROKE_WIDTH);
	private static final Color OUTLINE_STROKE_COLOR = Color.BLACK;
	private static final Color SODIUM_BAR_COLOR = new SodiumIon().getRepresentationColor();
	private static final Color POTASSIUM_BAR_COLOR = new PotassiumIon().getRepresentationColor();
	private static final double BAR_WIDTH_PROPORTION = 0.20;
	private static final double BAR_HEIGHT_PROPORTION = 0.75;
	private static final double MAX_CONCENTRATION_COUNT = 50;
	
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

	private MembraneDiffusionModel model;
	private PNode background;
	private PNode graphBaseLine;
	private PhetPPath sodiumBar;
	private PhetPPath potassiumBar;
	private Rectangle2D sodiumBarShape = new Rectangle2D.Double();
	private Rectangle2D potassiumBarShape = new Rectangle2D.Double();
	private double barWidth;
	private double maxBarHeight;
    private JButton closeButton;
    private PSwing closePSwing;
	private double distanceFromBottomToBars;
	private boolean upperChamber;
	
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

	public ConcentrationGraph(final MembraneDiffusionModel model, boolean upperChamber) {
		
		this.model = model;
		this.upperChamber = upperChamber;
		
		// Listen to the model for events that may matter to us.
		model.addListener(new MembraneDiffusionModel.Adapter(){
			public void concentrationGraphVisibilityChanged() {
				updateVisibility();
			}
			public void concentrationsChanged(){
				updateBarSizes();
			}
		});
		
		// Create the overall background.
		background = new PhetPPath(new RoundRectangle2D.Double(0, 0, size.getWidth(), size.getHeight(), 12, 12),
				MAIN_BACKGROUND_COLOR, OUTLINE_STROKE, OUTLINE_STROKE_COLOR);
		addChild(background);

		// Calculate some values that will be used when layout out the
		// components that make up this node.
		barWidth = size.getWidth() * BAR_WIDTH_PROPORTION;
		maxBarHeight = size.getHeight() * BAR_HEIGHT_PROPORTION;
		
		// Add the button that will allow the user to close (actually hide) the meter.
		ImageIcon imageIcon = new ImageIcon( 
				PhetCommonResources.getInstance().getImage(PhetCommonResources.IMAGE_CLOSE_BUTTON) );
		closeButton = new JButton( imageIcon );
		closeButton.setPreferredSize(new Dimension(imageIcon.getIconWidth(), imageIcon.getIconHeight()));
		closeButton.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				model.setConcentrationGraphsVisible(false);
			}
		} );
		
		closePSwing = new PSwing( closeButton );
		closePSwing.setOffset(size.getWidth() - closeButton.getBounds().width - 7, 5);
		closePSwing.addInputEventListener( new CursorHandler(Cursor.HAND_CURSOR) );
		addChild(closePSwing);
		
		distanceFromBottomToBars = size.getHeight() * 0.2;
		double distanceFromSideToBarBase = size.getWidth() * 0.1;
		Shape baseLineShape = new Line2D.Double(distanceFromSideToBarBase,
				size.getHeight() - distanceFromBottomToBars,
				size.getWidth() - distanceFromSideToBarBase,
				size.getHeight() - distanceFromBottomToBars);
		graphBaseLine = new PhetPPath(baseLineShape, new BasicStroke(2f), Color.BLACK);
		addChild(graphBaseLine);
		
		// Add the labels for the particle types.
		ParticleTypeLabelNode sodiumLabel = new ParticleTypeLabelNode(distanceFromBottomToBars * 0.6,
				ParticleType.SODIUM_ION);
		sodiumLabel.setOffset(size.getWidth() * 0.25 - sodiumLabel.getFullBoundsReference().width / 2,
				size.getHeight() - sodiumLabel.getFullBoundsReference().height - 5);
		addChild(sodiumLabel);

		ParticleTypeLabelNode potassiumLabel = new ParticleTypeLabelNode(distanceFromBottomToBars * 0.6,
				ParticleType.POTASSIUM_ION);
		potassiumLabel.setOffset(size.getWidth() * 0.75 - potassiumLabel.getFullBoundsReference().width / 2,
				size.getHeight() - potassiumLabel.getFullBoundsReference().height - 5);
		addChild(potassiumLabel);
		
		// Add the bars.  The shapes will be set when updates occur.
		sodiumBar = new PhetPPath( SODIUM_BAR_COLOR );
		sodiumBar.setOffset(size.getWidth() * 0.25 - barWidth / 2, 0);
		addChild(sodiumBar);
		
		potassiumBar = new PhetPPath( POTASSIUM_BAR_COLOR );
		potassiumBar.setOffset(size.getWidth() * 0.75 - barWidth / 2, 0);
		addChild(potassiumBar);
		
		// Do the initial updates.
		updateBarSizes();
		updateVisibility();
	}
	
    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

	private void updateBarSizes(){

		double sodiumBarHeight;
		double potassiumBarHeight;
		
		if (upperChamber){
			sodiumBarHeight = model.getCountInUpperSubChamber(ParticleType.SODIUM_ION) / (double)MAX_CONCENTRATION_COUNT * maxBarHeight;
			potassiumBarHeight = model.getCountInUpperSubChamber(ParticleType.POTASSIUM_ION) / (double)MAX_CONCENTRATION_COUNT * maxBarHeight;
		}
		else{
			sodiumBarHeight = model.getCountInLowerSubChamber(ParticleType.SODIUM_ION) / (double)MAX_CONCENTRATION_COUNT * maxBarHeight;
			potassiumBarHeight = model.getCountInLowerSubChamber(ParticleType.POTASSIUM_ION) / (double)MAX_CONCENTRATION_COUNT * maxBarHeight;
		}

		sodiumBarShape.setFrame(0, 0, barWidth, sodiumBarHeight);
		sodiumBar.setPathTo(sodiumBarShape);
		sodiumBar.setOffset(sodiumBar.getOffset().getX(),
				size.getHeight() - distanceFromBottomToBars - sodiumBarShape.getHeight());

		potassiumBarShape.setFrame(0, 0, barWidth, potassiumBarHeight);
		potassiumBar.setPathTo(potassiumBarShape);
		potassiumBar.setOffset(potassiumBar.getOffset().getX(),
				size.getHeight() - distanceFromBottomToBars - potassiumBarShape.getHeight());
	}
	
	private void updateVisibility(){
		setVisible(model.isConcentrationGraphsVisible());
	}
	
	private static class ParticleTypeLabelNode extends PNode {
		
		private static final Font LABEL_FONT = new PhetFont(14);
		private static final ModelViewTransform2D LABEL_MVT = new ModelViewTransform2D(
				new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-10, -10, 20, 20));
		
		/**
		 * Constructor.  The height is specified, and the width is then
		 * determined as a function of the height and the label widths.
		 * 
		 * @param model
		 * @param height
		 */
		public ParticleTypeLabelNode(double height, ParticleType particleType){
			
			ParticleNode particleNode;
			
			switch (particleType){
			case SODIUM_ION:
				particleNode = new ParticleNode(new SodiumIon(), LABEL_MVT);
				break;
				
			case POTASSIUM_ION:
				particleNode = new ParticleNode(new PotassiumIon(), LABEL_MVT);
				break;
				
			default:
				// Unhandled case.
				System.out.println(getClass().getName() + " - Error: Unhandled particle type.");
				assert false;
				// Use an arbitrary default.
				particleNode = new ParticleNode(new PotassiumIon(), LABEL_MVT);
				break;
			}

			particleNode.setScale(height / particleNode.getFullBoundsReference().height);
			particleNode.setOffset(particleNode.getFullBoundsReference().width / 2,
					particleNode.getFullBoundsReference().height / 2);
			addChild(particleNode);
		}
	}
}
