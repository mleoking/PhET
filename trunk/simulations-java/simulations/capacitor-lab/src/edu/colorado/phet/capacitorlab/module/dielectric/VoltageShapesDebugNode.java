/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.model.*;
import edu.colorado.phet.capacitorlab.model.Battery.BatteryChangeAdapter;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.shapes.BatteryShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.capacitorlab.shapes.VoltmeterShapeFactory;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the shapes related to the measurement of voltage.
 * All shapes are in the global view coordinate frame; when a model object moves, its shape changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VoltageShapesDebugNode extends PComposite {
    
    private static class ShapeNode extends PPath {
        
        private static final Stroke STROKE = new BasicStroke( 2f );
        private static final Color STROKE_COLOR = Color.YELLOW;
        
        public ShapeNode( Shape shape ) {
            this( shape, STROKE_COLOR );
        }
        
        public ShapeNode( Shape shape, Color strokeColor ) {
            super( shape );
            setStroke( STROKE );
            setStrokePaint( strokeColor );
        }
    }
    
    public VoltageShapesDebugNode( final DielectricModel model, final ModelViewTransform mvt ) {
        
        // nothing interactive here
        setPickable( false );
        setChildrenPickable( false );
        
        // battery
        {
            final Battery battery = model.getBattery();
            final BatteryShapeFactory shapeFactory = new BatteryShapeFactory( battery, mvt );
            
            ShapeNode bodyNode = new ShapeNode( shapeFactory.createBodyShape() );
            addChild( bodyNode );

            final ShapeNode topTerminalNode = new ShapeNode( shapeFactory.createTopTerminalShape() );
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
            
            final ShapeNode topPlateNode = new ShapeNode( shapeFactory.createTopPlateShapeOccluded() );
            addChild( topPlateNode );
            
            final ShapeNode bottomPlateNode = new ShapeNode( shapeFactory.createBottomPlateShapeOccluded() );
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
        
        // voltmeter
        {
            final Voltmeter voltmeter = model.getVoltmeter();
            final VoltmeterShapeFactory shapeFactory = new VoltmeterShapeFactory( voltmeter, mvt );
            
            final ShapeNode positiveTipNode = new ShapeNode( shapeFactory.getPositiveProbeTipShape() );
            addChild( positiveTipNode );
            
            final ShapeNode negativeTipNode = new ShapeNode( shapeFactory.getNegativeProbeTipShape() );
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
