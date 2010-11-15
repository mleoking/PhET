/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.module.dielectric;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.Capacitor.CapacitorChangeAdapter;
import edu.colorado.phet.capacitorlab.model.ModelViewTransform;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the shapes related to the measurement of E-Field.
 * All shapes are in the global view coordinate frame; when a model object moves, its shape changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldShapesDebugNode extends PComposite {
    
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
    
    public EFieldShapesDebugNode( final DielectricModel model, final ModelViewTransform mvt ) {
        
        // nothing interactive here
        setPickable( false );
        setChildrenPickable( false );
        
        // capacitor
        {
            final Capacitor capacitor = model.getCapacitor();
            final CapacitorShapeFactory shapeFactory = new CapacitorShapeFactory( capacitor, mvt );
            
            final ShapeNode dielectricBetweenPlatesNode = new ShapeNode( shapeFactory.createDielectricBetweenPlatesShapeOccluded(), Color.RED );
            addChild( dielectricBetweenPlatesNode );
            
            final ShapeNode airBetweenPlatesNode = new ShapeNode( shapeFactory.createAirBetweenPlatesShapeOccluded(), Color.RED );
            addChild( airBetweenPlatesNode );
            
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
                    dielectricBetweenPlatesNode.setPathTo( shapeFactory.createDielectricBetweenPlatesShapeOccluded() );
                    airBetweenPlatesNode.setPathTo( shapeFactory.createAirBetweenPlatesShapeOccluded() );
                }
            } );
        }
    }

}
