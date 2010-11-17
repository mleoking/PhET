package edu.colorado.phet.fluidpressureandflow.modules.fluidflow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.util.Function0;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.fluidpressureandflow.model.Pool;
import edu.colorado.phet.fluidpressureandflow.model.PressureSensor;
import edu.colorado.phet.fluidpressureandflow.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class FluidFlowCanvas extends FluidPressureAndFlowCanvas {
    private PNode particleLayer;
    private PNode foodColoringLayer;

    private static final double modelHeight = Pool.DEFAULT_HEIGHT * 2.2;
    private static final double pipeCenterY = -2;
    private static final double modelWidth = modelHeight / STAGE_SIZE.getHeight() * STAGE_SIZE.getWidth();

    public FluidFlowCanvas( final FluidFlowModule module ) {
        super( module, new ModelViewTransform2D( new Rectangle2D.Double( -modelWidth / 2, -modelHeight / 2+pipeCenterY, modelWidth, modelHeight ), new Rectangle2D.Double( 0, 0, STAGE_SIZE.width, STAGE_SIZE.height ), true ) );

        addChild( new GroundNode( transform ) );
        addChild( new SkyNode( transform ) );
//        addChild( new PhetPPath( transform.createTransformedShape( module.getFluidPressureAndFlowModel().getPool().getShape() ), Color.white ) );//so earth doesn't bleed through transparent pool

        addChild( new PipeNode( transform, module.getFluidFlowModel().getPipe() ) );
        particleLayer = new PNode();
        foodColoringLayer = new PNode();
        addChild( foodColoringLayer );
        addChild( particleLayer );
        for ( final Particle p : module.getFluidFlowModel().getParticles() ) {
            addParticleNode( p );
        }
        module.getFluidFlowModel().addParticleAddedObserver( new Function1<Particle, Void>() {
            public Void apply( Particle particle ) {
                addParticleNode( particle );
                return null;//TODO: better support for void
            }
        } );
        for ( PressureSensor pressureSensor : module.getFluidPressureAndFlowModel().getPressureSensors() ) {
            addChild( new PressureSensorNode( transform, pressureSensor, null, module.getFluidPressureAndFlowModel().getPressureUnitProperty() ) );
        }
        addChild( new VelocitySensorNode( transform, module.getFluidFlowModel().getVelocitySensor() ) );

        final DropperNode dropperNode = new DropperNode( transform, module.getFluidFlowModel().getPipe(), module.getFluidFlowModel().getDropperOnProperty() );
        addChild( dropperNode );
        addChild( new PSwing( new JButton( "Pour Food Coloring" ) {{
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.getFluidFlowModel().pourFoodColoring();
                }
            } );
        }} ) {{
            setOffset( dropperNode.getFullBounds().getMaxX(), dropperNode.getFullBounds().getMaxY() - getFullBounds().getHeight() );
        }} );

        module.getFluidFlowModel().addFoodColoringObserver( new Function1<FoodColoring, Void>() {
            public Void apply( FoodColoring foodColoring ) {
                addFoodColoringNode( foodColoring );
                return null;
            }
        } );

        addControls( new Point2D.Double( STAGE_SIZE.getWidth() / 2, STAGE_SIZE.getHeight() ) );
    }

    private void addFoodColoringNode( final FoodColoring p ) {
        final FoodColoringNode node = new FoodColoringNode( transform, p );
        foodColoringLayer.addChild( node );
        p.addRemovalListener( new SimpleObserver() {
            public void update() {
                particleLayer.removeChild( node );
                p.removeRemovalListener( this );
            }
        } );
    }

    private void addParticleNode( final Particle p ) {
        final ParticleNode node = new ParticleNode( transform, p );
        particleLayer.addChild( node );
        p.addRemovalListener( new Function0() {
            public Object apply() {
                particleLayer.removeChild( node );
                p.removeRemovalListener( this );
                return null;//TODO: better interface so we don't have to do this
            }
        } );
    }

}
