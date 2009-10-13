/* Copyright 2009, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.neuron.NeuronConstants;
import edu.colorado.phet.neuron.NeuronStrings;
import edu.colorado.phet.neuron.model.AbstractLeakChannel;
import edu.colorado.phet.neuron.model.AxonModel;
import edu.colorado.phet.neuron.model.MembraneChannelTypes;
import edu.colorado.phet.neuron.model.ParticleType;
import edu.colorado.phet.neuron.model.PotassiumLeakageChannel;
import edu.colorado.phet.neuron.model.SodiumLeakageChannel;
import edu.colorado.phet.neuron.view.MembraneChannelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Slider for controlling the number of leak channels of a given type in the
 * model.
 * 
 * @author John Blanco
 */
public class LeakChannelSlider extends LinearValueControl{
	
    private static final Font LABEL_FONT = new PhetFont(12);
	private static final Dimension2D OVERALL_SIZE_OF_LEAK_CHANNEL_ICON = new PDimension(38, 50);
	private static final Dimension2D CHANNEL_SIZE_OF_LEAK_CHANNEL_ICON = new PDimension(15, 30);
	
	// Model-View transform used to make the nodes shown on the control be an
	// appropriate size.
	private static final ModelViewTransform2D MVT = new ModelViewTransform2D(
			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-10, -10, 20, 20));
	
	/**
	 * Constructor.
	 * 
	 * @param title
	 * @param axonModel
	 * @param atomType
	 */
    public LeakChannelSlider(String title, final AxonModel axonModel, ParticleType atomType) {
    	
        super( 0, NeuronConstants.MAX_CHANNELS_PER_TYPE, title, "0", "");
        setUpDownArrowDelta( 1 );
        setTextFieldVisible(false);
        setMinorTicksVisible(false);
        setBorder( BorderFactory.createEtchedBorder() );
        setSnapToTicks(false);

        // Put in the labels for the left and right bottom portion of the
        // slider.
        Hashtable<Double, JLabel> leakChannelSliderLabelTable = new Hashtable<Double, JLabel>();
        JLabel leftLabel = new JLabel(NeuronStrings.NONE);
        leftLabel.setFont( LABEL_FONT );
        leakChannelSliderLabelTable.put( new Double( 0 ), leftLabel );
        JLabel rightLabel = new JLabel(NeuronStrings.LOTS);
        rightLabel.setFont( LABEL_FONT );
        leakChannelSliderLabelTable.put( new Double( NeuronConstants.MAX_CHANNELS_PER_TYPE ), rightLabel );
        setTickLabels( leakChannelSliderLabelTable );

        // Set up the variables that will differ based on the type.
        AbstractLeakChannel leakChannel;
        final MembraneChannelTypes channelType;
        switch (atomType){
        case SODIUM_ION:
        	leakChannel = new SodiumLeakageChannel();
        	channelType = MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL;
        	break;
        case POTASSIUM_ION:
        	leakChannel = new PotassiumLeakageChannel();
        	channelType = MembraneChannelTypes.POTASSIUM_LEAKAGE_CHANNEL;
        	break;
        	
        default:
        	System.err.println(getClass().getName() + " - Error: Unknown leak channel type.");
        	assert false;
        	leakChannel = new SodiumLeakageChannel();  // Just in case.
        	channelType = MembraneChannelTypes.SODIUM_LEAKAGE_CHANNEL; // Just in case.
        }
        leakChannel.setDimensions(OVERALL_SIZE_OF_LEAK_CHANNEL_ICON, CHANNEL_SIZE_OF_LEAK_CHANNEL_ICON);
        leakChannel.setRotationalAngle(-Math.PI / 2);
        
        // Create and set the icon image.
        PNode iconNode = new MembraneChannelNode(leakChannel, MVT);
        JLabel _valueLabel = getValueLabel();
        _valueLabel.setIcon( new ImageIcon(iconNode.toImage(40, 40, new Color(0,0,0,0))) );
        _valueLabel.setVerticalTextPosition( JLabel.CENTER );
        _valueLabel.setHorizontalTextPosition( JLabel.LEFT );

        // Register a listener to handle changes.
        addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int value = (int)Math.round(getValue());
				if ( value != axonModel.getNumMembraneChannels(channelType) ){
					axonModel.setNumMembraneChannels(channelType, value);
				}
			}
		});
	}
}