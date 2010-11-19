/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.developer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import edu.colorado.phet.capacitorlab.CLPaints;
import edu.colorado.phet.capacitorlab.model.Capacitor;
import edu.colorado.phet.capacitorlab.model.CLModelViewTransform3D;
import edu.colorado.phet.capacitorlab.module.dielectric.DielectricModel;
import edu.colorado.phet.capacitorlab.shapes.CapacitorShapeFactory;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Displays the shapes related to the measurement of E-Field.
 * All shapes are in the global view coordinate frame; when a model object moves, its shape changes.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class EFieldShapesDebugNode extends PComposite {
    
    private static final Stroke STROKE = new BasicStroke( 2f );
    private static final Color STROKE_COLOR = CLPaints.EFIELD_SHAPES;
    
    public EFieldShapesDebugNode( final DielectricModel model, final CLModelViewTransform3D mvt ) {
        
        // nothing interactive here
        setPickable( false );
        setChildrenPickable( false );
        
        // capacitor
        {
            final Capacitor capacitor = model.getCapacitor();
            final CapacitorShapeFactory shapeFactory = new CapacitorShapeFactory( capacitor, mvt );
            
            final PPath dielectricBetweenPlatesNode = new PhetPPath( shapeFactory.createDielectricBetweenPlatesShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( dielectricBetweenPlatesNode );
            
            final PPath airBetweenPlatesNode = new PhetPPath( shapeFactory.createAirBetweenPlatesShapeOccluded(), STROKE, STROKE_COLOR );
            addChild( airBetweenPlatesNode );
            
            // set shapes to match model
            SimpleObserver o = new SimpleObserver() {
                public void update() {
                    dielectricBetweenPlatesNode.setPathTo( shapeFactory.createDielectricBetweenPlatesShapeOccluded() );
                    airBetweenPlatesNode.setPathTo( shapeFactory.createAirBetweenPlatesShapeOccluded() );
                }
            };
            capacitor.addPlateSizeObserver( o );
            capacitor.addPlateSeparationObserver( o );
            capacitor.addDielectricOffsetObserver( o );
        }
    }
}
