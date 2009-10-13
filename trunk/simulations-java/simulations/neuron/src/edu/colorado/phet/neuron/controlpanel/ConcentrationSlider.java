/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.ParticleType;
import edu.colorado.phet.neuron.model.PotassiumIon;
import edu.colorado.phet.neuron.model.SodiumIon;
import edu.colorado.phet.neuron.view.ParticleNode;

/**
 * Slider that can be used to set the relative concentration (inside the cell
 * vs. outside the cell) of a particle type.
 * 
 * @author John Blanco
 */
public class ConcentrationSlider extends LinearValueControl{
    	
        private static final Font LABEL_FONT = new PhetFont(12);
        
    	// Model-View transform used to make the nodes shown on the control be an
    	// appropriate size.
    	private static final ModelViewTransform2D MVT = new ModelViewTransform2D(
    			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-10, -10, 20, 20));
    	
		public ConcentrationSlider(String title, final AxonModel axonModel, final ParticleType atomType) {
            super( 0, 1, title, "0", "");
            setUpDownArrowDelta( 0.01 );
            setTextFieldVisible(false);
            setMinorTicksVisible(false);
            setBorder( BorderFactory.createEtchedBorder() );
            setSnapToTicks(false);
            
            // Put in the labels for the left and right bottom portion of the
            // slider.
            Hashtable<Double, JLabel> concentrationSliderLabelTable = new Hashtable<Double, JLabel>();
            JLabel leftLabel = new JLabel(NeuronStrings.OUTSIDE);
            leftLabel.setFont( LABEL_FONT );
            concentrationSliderLabelTable.put( new Double( 0 ), leftLabel );
            JLabel rightLabel = new JLabel(NeuronStrings.INSIDE);
            rightLabel.setFont( LABEL_FONT );
            concentrationSliderLabelTable.put( new Double( 1 ), rightLabel );
            setTickLabels( concentrationSliderLabelTable );

            // Set up the variables that will differ based on the atom type.
            ParticleNode atomNode;
            switch (atomType){
            case SODIUM_ION:
            	atomNode = new ParticleNode(new SodiumIon(), MVT);
            	break;
            case POTASSIUM_ION:
            	atomNode = new ParticleNode(new PotassiumIon(), MVT);
            	break;
            	
            default:
            	System.err.println(getClass().getName() + " - Error: Unknown atom type.");
            	assert false;
            	atomNode = new ParticleNode(new SodiumIon(), MVT);  // Just in case.
            }
            
            // TODO: This is a workaround for a problem where the icon was
            // being cut off on the edges - basically the stroke was being
            // removed.  Figure out why this is needed and fix it.
            // UPDATE: This actually doesn't work, but keep for a while for
            // reference anyway.
//            PNode atomNodeComposite = new PNode();
//            double size = atomNode.getFullBoundsReference().width * 1.4;
//            PhetPPath atomNodeBackground = new PhetPPath(new Rectangle2D.Double(-size / 2, -size / 2, size, size),
//            		new Color(200,200,200,255));
//            atomNodeComposite.addChild(atomNodeBackground);
//            atomNodeComposite.addChild(atomNode);
            
            // Create and add the icon.
            JLabel _valueLabel = getValueLabel();
            System.out.println(atomNode.getFullBoundsReference());
            Image atomImage = atomNode.toImage(25, 25, new Color(0,0,0,0));
            BufferedImage bufferedAtomImage = BufferedImageUtils.toBufferedImage(atomImage);
            try {
            	
				ImageIO.write(bufferedAtomImage, "png", new File("c:/temp/atom-image-" + atomType.toString() + ".png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            _valueLabel.setIcon( new ImageIcon(atomImage) );
            _valueLabel.setVerticalTextPosition( JLabel.CENTER );
            _valueLabel.setHorizontalTextPosition( JLabel.LEFT );
            
            // Set up the change listener for this control.
            addChangeListener(new ChangeListener() {
            	public void stateChanged(ChangeEvent e) {
            		double value = getValue();
            		if ( value != axonModel.getProportionOfParticlesInside(atomType) ){
            			axonModel.setConcentration(atomType, value);
            		}
            	}
            });
		}
    }