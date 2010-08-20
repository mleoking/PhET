/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;
import edu.colorado.phet.membranediffusion.model.ChannelAlwaysOpenStrategy;
import edu.colorado.phet.membranediffusion.model.GenericMembraneChannel;
import edu.colorado.phet.membranediffusion.model.MembraneChannel;
import edu.colorado.phet.membranediffusion.model.MembraneDiffusionModel;
import edu.colorado.phet.membranediffusion.model.ParticleType;
import edu.colorado.phet.membranediffusion.model.PotassiumLeakageChannel;
import edu.colorado.phet.membranediffusion.model.SodiumLeakageChannel;
import edu.umd.cs.piccolo.PNode;

/**
 * Node that goes in the membrane channel tool box and allows users to add
 * potassium leak channels to the membrane.
 * 
 * @author John Blanco
 */
public class PotassiumLeakageChannelToolBoxNode extends ToolBoxItem {

    private static final Color EDGE_COLOR = ColorUtils.interpolateRBGA(MembraneDiffusionConstants.POTASSIUM_COLOR,
            new Color(00, 200, 255), 0.6);

	public PotassiumLeakageChannelToolBoxNode(MembraneDiffusionModel model, ModelViewTransform2D mvt, PhetPCanvas canvas) {
		super(model, mvt, canvas);
	}

	@Override
	protected MembraneChannel createModelElement( Point2D position ) {
        return new GenericMembraneChannel( getModel(), ParticleType.POTASSIUM_ION,
                ColorUtils.darkerColor(EDGE_COLOR, 0.15), EDGE_COLOR, new ChannelAlwaysOpenStrategy() );
	}

	@Override
	protected void initializeSelectionNode() {
        MembraneChannel channel = new GenericMembraneChannel( getModel(), ParticleType.POTASSIUM_ION,
                ColorUtils.darkerColor(EDGE_COLOR, 0.15), EDGE_COLOR, new ChannelAlwaysOpenStrategy() );
        PNode representation = new MembraneChannelNode(channel, SCALING_MVT);
        setSelectionNode(representation);
	}
}
