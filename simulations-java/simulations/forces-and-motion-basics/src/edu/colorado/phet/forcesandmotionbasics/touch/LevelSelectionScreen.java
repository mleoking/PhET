package edu.colorado.phet.forcesandmotionbasics.touch;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
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

public class LevelSelectionScreen extends AbstractForcesAndMotionBasicsCanvas {
    private final ForcesAndMotionBasicsApplication application;

    public LevelSelectionScreen( ForcesAndMotionBasicsApplication application ) {
        this.application = application;
        setBackground( Color.black );
        addWorldChild( new PNode() {{
            addChild( new VBox( new PhetPText( "Forces and Motion: Basics", new PhetFont( 48, true ), Color.white ),
                                new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 50 ), null, null ),//spacing
                                new HBox( new LevelNode( new PhetPPath( new Rectangle2D.Double( 0, 0, 300, 300 ), Color.green ), "Tug of War", listener( 0 ) ),
                                          new LevelNode( new PhetPPath( new Rectangle2D.Double( 0, 0, 200, 200 ), Color.green ), "Motion", listener( 1 ) ),
                                          new LevelNode( new PhetPPath( new Rectangle2D.Double( 0, 0, 200, 200 ), Color.green ), "Friction", listener( 2 ) ),
                                          new LevelNode( new PhetPPath( new Rectangle2D.Double( 0, 0, 200, 200 ), Color.green ), "Acceleration Lab", listener( 3 ) ) ) ) {{
                setOffset( 0, 50 );
            }} );
        }} );
        addScreenChild( new HBox( new PhetPText( "PhET", new PhetFont( 20, true ), Color.yellow ), new HTMLImageButtonNode( BufferedImageUtils.multiScaleToHeight( PhetCommonResources.getInstance().getImage( "menu-icon.png" ), 20 ) ) ) {{
            scale( 2 );
            setOffset( 10, 10 );
        }} );
    }

    private PInputEventListener listener( final int i ) {
        return new PBasicInputEventHandler() {
            @Override public void mousePressed( PInputEvent event ) {
                application.showModule( i );
            }
        };
    }

    private static class LevelNode extends PNode {
        private LevelNode( PNode icon, String text, PInputEventListener listener ) {
            addChild( new VBox( icon,
                                new PhetPText( text, new PhetFont( 28 ), Color.white ) ) );
            addInputEventListener( listener );
        }
    }
}
