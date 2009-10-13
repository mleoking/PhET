package edu.colorado.phet.densityjava.view.jpct;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.densityjava.model.Block;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.RectangularObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

import com.threed.jpct.*;

public class DensityCanvasJPCT extends PhetPCanvas {
    private World world;
    private Object3D box;

    static class BlockNode extends PNode {
        private RectangularObject rectangularObject;
        private ModelViewTransform2D modelViewTransform2D;
        private PhetPPath face;
        private PhetPPath topFace;
        private PhetPPath rightFace;

        public BlockNode( RectangularObject rectangularObject, ModelViewTransform2D modelViewTransform2D ) {
            this.rectangularObject = rectangularObject;
            this.modelViewTransform2D = modelViewTransform2D;
            face = new PhetPPath( rectangularObject.getFaceColor() );
            addChild( face );

            topFace = new PhetPPath( rectangularObject.getTopFaceColor() );
            addChild( topFace );

            rightFace = new PhetPPath( rectangularObject.getRightFaceColor() );
            addChild( rightFace );

            rectangularObject.addListener( new RectangularObject.Adapter() {
                public void modelChanged() {
                    updateShapes();
                }
            } );
            updateShapes();
        }

        private void updateShapes() {
            face.setPathTo( modelViewTransform2D.createTransformedShape( rectangularObject.getFrontFace() ) );
            topFace.setPathTo( modelViewTransform2D.createTransformedShape( rectangularObject.getTopFace() ) );
            rightFace.setPathTo( modelViewTransform2D.createTransformedShape( rectangularObject.getRightFace() ) );
        }
    }

    static class DraggableBlockNode extends BlockNode {

        public DraggableBlockNode( final Block rectangularObject, final ModelViewTransform2D modelViewTransform2D ) {
            super( rectangularObject, modelViewTransform2D );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    Point2D pt = new Point2D.Double( event.getDeltaRelativeTo( getParent() ).getWidth(), event.getDeltaRelativeTo( getParent() ).getHeight() );
                    rectangularObject.setUserDragging( true );
                    rectangularObject.translate( modelViewTransform2D.viewToModelDifferential( pt ) );
                }

                public void mouseReleased( PInputEvent event ) {
                    rectangularObject.setUserDragging( false );
                }
            } );
        }
    }

    public DensityCanvasJPCT( DensityModel model ) {
        setWorldTransformStrategy( new CenteringBoxStrategy( this, new PDimension( 800, 800 ) ) );
        ModelViewTransform2D modelViewTransform2D = new ModelViewTransform2D( new PBounds( -1, -1, 12, 12 ), new PBounds( 0, 0, 800, 800 ), true );

//        for (int i = 0; i < model.getBlockCount(); i++) {
//            addWorldChild(new DraggableBlockNode(model.getBlock(i), modelViewTransform2D));
//        }
//        addWorldChild(new UndraggableBlockNode(model.getWater(), modelViewTransform2D));

        world = new World();
        TextureManager.getInstance().addTexture("box", new Texture(64,64, Color.red));

        box = Primitives.getBox( 1f, 2f );
        box.setTexture( "box" );
        box.setEnvmapped( Object3D.ENVMAP_ENABLED );
        box.setShadingMode( Object3D.SHADING_FAKED_FLAT );
        box.build();

        world.addObject( box );

        world.addLight( new SimpleVector( -20,-5,-25),Color.gray );

        world.getCamera().setPosition( 5, -5,-2 );
        world.getCamera().lookAt( box.getTransformedCenter() );
        setOpaque( false );

        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                box.rotateY( 0.01f );
                repaint();
            }
        } );
        timer.start();
    }

    FrameBuffer buffer = new FrameBuffer( 800, 600, FrameBuffer.SAMPLINGMODE_OGSS );

    public void paintComponent( Graphics g ) {
        buffer.clear( java.awt.Color.BLUE );
        world.renderScene( buffer );
        world.draw( buffer );
        buffer.update();
        buffer.display( g );
        super.paintComponent( g );
    }

    private class UndraggableBlockNode extends BlockNode {
        public UndraggableBlockNode( RectangularObject object, ModelViewTransform2D modelViewTransform2D ) {
            super( object, modelViewTransform2D );
            setPickable( false );
            setChildrenPickable( false );
        }
    }
}