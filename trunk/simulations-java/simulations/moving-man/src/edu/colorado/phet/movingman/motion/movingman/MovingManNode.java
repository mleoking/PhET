package edu.colorado.phet.movingman.motion.movingman;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.motion.model.SingleBodyMotionModel;
import edu.colorado.phet.common.phetcommon.view.util.PhetDefaultFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Author: Sam Reid
 * May 22, 2007, 2:37:54 PM
 */
public class MovingManNode extends PNode {
    public MovingManNode( final SingleBodyMotionModel motionModel ) {
        Rectangle2D.Float skyRect = new Rectangle2D.Float( -20, 0, 40, 2 );
        GradientPaint skyPaint = new GradientPaint( skyRect.x, skyRect.y, new Color( 150, 120, 255 ), skyRect.x, skyRect.y + skyRect.height, Color.white );
        PhetPPath skyNode = new PhetPPath( skyRect, skyPaint );
        addChild( skyNode );

        PhetPPath yellowBackground = new PhetPPath( new Rectangle2D.Double( -20, 2, 40, 0.6 ), Color.yellow );
        addChild( yellowBackground );

        Rectangle2D.Float floorRect = new Rectangle2D.Float( -20, 2, 40, 0.1f );
        Paint floorPaint = new GradientPaint( floorRect.x, floorRect.y, new Color( 100, 100, 255 ), floorRect.x, floorRect.y + floorRect.height, Color.white );
        PhetPPath floorNode = new PhetPPath( floorRect, floorPaint );
        addChild( floorNode );

        for ( int i = -10; i <= 10; i += 2 ) {
            PText tickText = new PText( "" + i + ( i == 0 ? " meters" : "" ) );
            tickText.setFont( new Font( PhetDefaultFont.LUCIDA_SANS, Font.PLAIN, 14 ) );
            tickText.scale( 0.025 );
            tickText.setOffset( i - tickText.getFullBounds().getWidth() / 2, 2 );

            PPath tickNode = new PhetPPath( new Line2D.Double( 0, 0, 0, -0.2 ), new BasicStroke( 0.1f / 2 ), Color.black );
            tickNode.setOffset( i, 2 );

            addChild( tickText );
            addChild( tickNode );
        }
        PImage tree = PImageFactory.create( "moving-man/images/tree.gif" );
        tree.translate( -8, 2 - 2.1 );
        tree.scale( 2.1 / tree.getHeight() );
        tree.translate( -tree.getFullBounds().getWidth() / 2.0 / tree.getScale(), 0 );
        addChild( tree );

        PImage house = PImageFactory.create( "moving-man/images/cottage.gif" );
        house.translate( 8, 2 - 1.8 );
        house.scale( 1.8 / house.getHeight() );
        house.translate( -house.getFullBounds().getWidth() / 2.0 / house.getScale(), 0 );
        addChild( house );


        PImage leftWall = PImageFactory.create( "moving-man/images/barrier.jpg" );
        leftWall.translate( -10, 0 );
        leftWall.scale( 2.0 / leftWall.getHeight() );
        leftWall.translate( -leftWall.getFullBounds().getWidth() / leftWall.getScale(), 0 );
        addChild( leftWall );

        PImage rightWall = PImageFactory.create( "moving-man/images/barrier.jpg" );
        rightWall.translate( 10, 0 );
        rightWall.scale( 2.0 / rightWall.getHeight() );
        addChild( rightWall );

        final PImage manImage = PImageFactory.create( "moving-man/images/stand-ii.gif" );
        manImage.scale( 2.0 / manImage.getHeight() );
        addChild( manImage );

        manImage.addInputEventListener( new CursorHandler() );
        manImage.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
//                super.mouseDragged( event );
//                manImage.setOffset( event.getPositionRelativeTo( manImage.getParent() ) );
                motionModel.setPositionDriven();
                motionModel.getMotionBody().setPosition( event.getPositionRelativeTo( manImage.getParent() ).getX() );
            }
        } );

        motionModel.getXVariable().addListener( new IVariable.Listener() {
            public void valueChanged() {
                updateObject( manImage, motionModel );
            }
        } );
        updateObject( manImage, motionModel );

    }

    private void updateObject( PNode object, SingleBodyMotionModel model ) {
//        object.setOffset( rotationModel.getPosition() - object.getFullBounds().getWidth() / 2/object.getScale(), 2.0 - object.getFullBounds().getHeight()/object.getScale() );
        object.setOffset( model.getMotionBody().getPosition() - object.getFullBounds().getWidth() / 2, 2.0 - object.getFullBounds().getHeight() );
    }

}
