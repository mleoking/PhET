package edu.colorado.phet.forcesandmotionbasics.touch;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsApplication;
import edu.colorado.phet.forcesandmotionbasics.common.AbstractForcesAndMotionBasicsCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.forcesandmotionbasics.ForcesAndMotionBasicsResources.Images;

public class HomeScreen extends AbstractForcesAndMotionBasicsCanvas {
    private final ForcesAndMotionBasicsApplication application;

    public HomeScreen( final ForcesAndMotionBasicsApplication application ) {
        this.application = application;
        setBackground( Color.black );
        addWorldChild( new PNode() {{
            int index = application.getActiveModuleIndex();
            if ( index == -1 ) { index = 0; }
            addChild( new VBox( new PhetPText( "Forces and Motion: Basics", plainFont( 54 ), Color.white ),
                                new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 50 ), null, null ),//spacing
                                new HBox( 32, HBox.TOP_ALIGNED, new LevelNode( 0, index == 0, "Tug of War", listener( 0 ) ),//-1 means not inited yet, so use 1st module
                                          new LevelNode( 1, index == 1, "Motion", listener( 1 ) ),
                                          new LevelNode( 2, index == 2, "Friction", listener( 2 ) ),
                                          new LevelNode( 3, index == 3, "Acceleration Lab", listener( 3 ) ) ) ) {{
                setOffset( -50, 100 );
            }} );
        }} );
        addScreenChild( new HBox( -3, new PhetPText( "PhET ", new Font( "Tahoma", Font.PLAIN, 26 ), Color.yellow ), new VBox( 4, new PhetPPath( new Rectangle( 1, 1 ), new Color( 0, 0, 0, 0 ) ), new PImage( BufferedImageUtils.multiScaleToHeight( PhetCommonResources.getInstance().getImage( "menu-icon-blue.png" ), 33 ) ) {{}} ) ) {{
            scale( 2 );
            setOffset( 10, 10 );
        }} );
    }

    static BufferedImage[] icons = new BufferedImage[]{Images.TUG_ICON, Images.MOTION_ICON, Images.FRICTION_ICON, Images.ACCELERATION_ICON};
    static BufferedImage[] screenshots = new BufferedImage[]{Images.TUG_SCREENSHOT, Images.MOTION_SCREENSHOT, Images.FRICTION_SCREENSHOT, Images.ACCELERATION_SCREENSHOT};

    static private PNode createIcon( int index, boolean isSelected ) {
        BufferedImage x = ( screenshots )[index];
        int height = isSelected ? 276 : 276 / 2;
        Image icon = BufferedImageUtils.multiScaleToHeight( x, height );
        PImage node = new PImage( icon );
        if ( isSelected ) {
            PImage node2 = new PImage( BufferedImageUtils.multiScaleToHeight( Images.SCREENSHOT_FRAME, height + 40 ) );
            node.setOffset( 20, 20 );
            node2.addChild( node );
            return node2;
        }
        return node;
    }

    private PInputEventListener listener( final int i ) {
        return new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                application.showModule( i );
            }
        };
    }

    public static Font plainFont( int size ) { return new Font( "Century Gothic", Font.PLAIN, size ); }

    public static Font boldFont( int size ) { return new Font( "Century Gothic", Font.BOLD, size ); }

    private static class LevelNode extends PNode {
        private LevelNode( int index, boolean selected, String text, PInputEventListener listener ) {
            addChild( new VBox( VBox.LEFT_ALIGNED, createIcon( index, selected ),
                                new PhetPText( text, selected ? boldFont( 35 ) : plainFont( 25 ), selected ? new Color( 252, 247, 106 ) : Color.white ) ) );
            addInputEventListener( listener );
            if ( !selected ) {
                setTransparency( 0.6f );
            }
        }
    }
}