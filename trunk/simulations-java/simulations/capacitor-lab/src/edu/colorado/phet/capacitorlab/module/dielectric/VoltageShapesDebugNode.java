/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.shapes.BatteryShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.VoltmeterShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.WireShapeFactory;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the shapes related to the measurement of voltage.
 * All shapes are in the global view coordinate frame; when a model object moves, its shape changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltageShapesDebugNode extends PComposite {
    
    private static final boolean SHOW_BATTERY_BODY = false;
    
    private static final Stroke STROKE = new BasicStroke( 2f );
    private static final Color STROKE_COLOR = Color.RED;
    
    public VoltageShapesDebugNode( final DielectricModel model, final ModelViewTransform mvt ) {
        
        // nothing interactive here
        setPickable( false );
        setChildrenPickable( false );
        
        // battery
        {
            final Battery battery = model.getBattery();
            final BatteryShapeFactory shapeFactory = new BatteryShapeFactory( battery, mvt );
            
            if ( SHOW_BATTERY_BODY ) {
                PPath bodyNode = new PhetPPath( shapeFactory.createBodyShape(), STROKE, STROKE_COLOR );
                addChild( bodyNode );
            }

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
        
        // capacitor
        {
            final Capacitor capacitor = model.getCapacitor();
            final CapacitorShapeFactory shapeFactory = new CapacitorShapeFactory( capacitor, mvt );
            
            final PPath topPlateNode = new PhetPPath( shapeFactory.createTopPlateShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( topPlateNode );
            
            final PPath bottomPlateNode = new PhetPPath( shapeFactory.createBottomPlateShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( bottomPlateNode );
            
            capacitor.addCapacitorChangeListener( new CapacitorChangeAdapter() {

                @Override
                public void plateSizeChanged() {
                     updateShapes();
                }

                @Override
                public void plateSeparationChanged() {
                    updateShapes();
                }
                
                @Override
                public void dielectricOffsetChanged() {
                    updateShapes();
                }
                
                private void updateShapes() {
                    topPlateNode.setPathTo( shapeFactory.createTopPlateShapeOccluded() );
                    bottomPlateNode.setPathTo( shapeFactory.createBottomPlateShapeOccluded() );
                }
            } );
        }
        
        // wires
        {
            final Wire topWire = model.getTopWire();
            final WireShapeFactory topShapeFactory = new WireShapeFactory( topWire, mvt );
            
            final Wire bottomWire = model.getBottomWire();
            final WireShapeFactory bottomShapeFactory = new WireShapeFactory( bottomWire, mvt );
            
            final PPath topWireNode = new PhetPPath( topShapeFactory.createShape(), STROKE, STROKE_COLOR );
            addChild( topWireNode );
            
            final PPath bottomWireNode = new PhetPPath( bottomShapeFactory.createShape(), STROKE, STROKE_COLOR );
            addChild( bottomWireNode );
            
            topWire.addShapeObserver( new SimpleObserver() {
                public void update() {
                    topWireNode.setPathTo( topShapeFactory.createShape() );
                }
            } );
            
            bottomWire.addShapeObserver( new SimpleObserver() {
                public void update() {
                    bottomWireNode.setPathTo( bottomShapeFactory.createShape() );
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
