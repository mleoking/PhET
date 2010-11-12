/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.model.Battery;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.model.Voltmeter;
import edu.colorado.phet.capacitorlab.shapes.BatteryShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.VoltmeterShapeFactory;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the shapes for various model elements.
 * All shapes are in the global view coordinate frame.
 * So when a model object moves, its shape changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ShapesDebugNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 2f );
    private static final Color STROKE_COLOR = Color.YELLOW;
    
    public ShapesDebugNode( final DielectricModel model, final ModelViewTransform mvt ) {
        
        // battery
        {
            final Battery battery = model.getBattery();
            final BatteryShapeFactory shapeFactory = new BatteryShapeFactory( battery, mvt );
            
            PPath bodyNode = new PhetPPath( shapeFactory.createBodyShape(), STROKE, STROKE_COLOR );
            addChild( bodyNode );

            final PPath topTerminalNode = new PhetPPath( shapeFactory.createTopTerminalShape(), STROKE, STROKE_COLOR );
            addChild( topTerminalNode );
            
            // set top terminal shape to match polarity
            battery.addBatteryChangeListener( new BatteryChangeAdapter() {
                @Override
                public void polarityChanged() {
                    topTerminalNode.setPathTo( shapeFactory.createTopTerminalShape() );
                }
            } );
        }
        
        // voltmeter
        {
            final Voltmeter voltmeter = model.getVoltmeter();
            final VoltmeterShapeFactory shapeFactory = new VoltmeterShapeFactory( voltmeter, mvt );
            
            final PPath positiveTipNode = new PhetPPath( shapeFactory.getPositiveProbeTipShape(), STROKE, STROKE_COLOR );
            addChild( positiveTipNode );
            
            final PPath negativeTipNode = new PhetPPath( shapeFactory.getNegativeProbeTipShape(), STROKE, STROKE_COLOR );
            addChild( negativeTipNode );
            
            // set shape to match positive probe location
            voltmeter.addPositiveProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    positiveTipNode.setPathTo( shapeFactory.getPositiveProbeTipShape() );
                }
            } );
            
            // set shape to match negative probe location
            voltmeter.addNegativeProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    negativeTipNode.setPathTo( shapeFactory.getNegativeProbeTipShape() );
                }
            } );
            
            // set visibility to match voltmeter visibility
            voltmeter.addVisibleObserver( new SimpleObserver() {
                public void update() {
                    positiveTipNode.setVisible( voltmeter.isVisible() );
                    negativeTipNode.setVisible( voltmeter.isVisible() );
                }
            } );
        }
    }

}
