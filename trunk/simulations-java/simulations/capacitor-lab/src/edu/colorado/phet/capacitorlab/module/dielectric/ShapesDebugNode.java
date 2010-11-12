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
            final BatteryShapeFactory shapeFactory = new BatteryShapeFactory( battery );
            
            PPath bodyNode = new PhetPPath( mvt.modelToView( shapeFactory.createBodyShape() ), STROKE, STROKE_COLOR );
            addChild( bodyNode );

            final PPath topTerminalNode = new PhetPPath( mvt.modelToView( shapeFactory.createTopTerminalShape() ), STROKE, STROKE_COLOR );
            addChild( topTerminalNode );
            battery.addBatteryChangeListener( new BatteryChangeAdapter() {
                @Override
                public void polarityChanged() {
                    topTerminalNode.setPathTo( mvt.modelToView( shapeFactory.createTopTerminalShape() ) );
                }
            } );
        }
        
        // voltmeter
        {
            final Voltmeter voltmeter = model.getVoltmeter();
            final VoltmeterShapeFactory shapeFactory = new VoltmeterShapeFactory( voltmeter );
            
            final PPath positiveTipNode = new PhetPPath( mvt.modelToView( shapeFactory.getPositiveProbeTipShape() ), STROKE, STROKE_COLOR );
            addChild( positiveTipNode );
            voltmeter.addPositiveProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    positiveTipNode.setPathTo( mvt.modelToView( shapeFactory.getPositiveProbeTipShape() ) );
                }
            } );

            final PPath negativeTipNode = new PhetPPath( mvt.modelToView( shapeFactory.getNegativeProbeTipShape() ), STROKE, STROKE_COLOR );
            addChild( negativeTipNode );
            voltmeter.addNegativeProbeLocationObserver( new SimpleObserver() {
                public void update() {
                    negativeTipNode.setPathTo( mvt.modelToView( shapeFactory.getNegativeProbeTipShape() ) );
                }
            } );
            
            voltmeter.addVisibleObserver( new SimpleObserver() {
                public void update() {
                    positiveTipNode.setVisible( voltmeter.isVisible() );
                    negativeTipNode.setVisible( voltmeter.isVisible() );
                }
            } );
        }
    }

}
