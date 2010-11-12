/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.view;

import java.awt.BasicStroke;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.capacitorlab.CLImages;
import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.control.VoltageSliderNode;
import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Polarity;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Visual representation of a DC battery, with a control for setting its voltage.
 * Image flips when the polarity of the voltage changes.
 * Origin is at center of this node's bounding rectangle.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BatteryNode extends PhetPNode {
    
    private final Battery battery;
    private final ModelViewTransform mvt;
    private final PImage imageNode;
    private final VoltageSliderNode sliderNode;
    private final PhetPPath topTerminalNode;
    
    public BatteryNode( final Battery battery, final ModelViewTransform mvt, boolean dev, DoubleRange voltageRange ) {
        
        this.battery = battery;
        battery.addBatteryChangeListener( new BatteryChangeAdapter() {
            @Override
            public void voltageChanged() {
                updateNode();
            }
            @Override
            public void polarityChanged() {
                updateNode();
            }
        });
        
        this.mvt = mvt;
        
        // battery image, scaled to match model dimensions
        imageNode = new PImage( CLImages.BATTERY_UP );
        addChild( imageNode );
        
        // voltage slider
        double trackLength = 0.60 * imageNode.getFullBoundsReference().getHeight();
        sliderNode = new VoltageSliderNode( voltageRange, trackLength );
        addChild( sliderNode );
        sliderNode.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateModel();
            }
        });
        
        // layout
        double x = -imageNode.getFullBoundsReference().getWidth() / 2;
        double y = -imageNode.getFullBoundsReference().getHeight() / 2;
        imageNode.setOffset( x, y );
        x = imageNode.getXOffset() + ( imageNode.getFullBoundsReference().getWidth() - sliderNode.getFullBoundsReference().getWidth() ) / 2; // horizontally centered
        y = imageNode.getYOffset() + 60; // set by visual inspection, depends on images
        sliderNode.setOffset( x, y );
        
        // show model bounds
        PhetPPath bodyNode = new PhetPPath( mvt.modelToView( battery.createBodyShapeLocal() ), new BasicStroke( 1f ), CLPaints.MODEL_BOUNDS );
        topTerminalNode = new PhetPPath( new BasicStroke( 1f ), CLPaints.MODEL_BOUNDS );
        if ( dev ) {
            addChild( bodyNode );
            addChild( topTerminalNode );
        }
        
        updateNode();
    }
    
    private void updateNode() {
        // slider
        sliderNode.setVoltage( battery.getVoltage() );
        // image
        if ( battery.getPolarity() == Polarity.POSITIVE ) {
            imageNode.setImage( CLImages.BATTERY_UP );
        }
        else {
            imageNode.setImage( CLImages.BATTERY_DOWN );
        }
        // top terminal shape
        topTerminalNode.setPathTo( mvt.modelToView( battery.createTopTerminalShapeLocal() ) );
    }
    
    private void updateModel() {
        battery.setVoltage( sliderNode.getVoltage() );
    }
}
