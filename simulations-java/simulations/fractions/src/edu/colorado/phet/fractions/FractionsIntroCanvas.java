// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FractionsIntroCanvas extends PhetPCanvas {

    private static PhetFont NUMBER_FONT = new PhetFont( 40, true );
    private static PhetFont BIG_NUMBER_FONT = new PhetFont( 128, true );
    //Stage where nodes are added and scaled up and down
    private final PNode rootNode;

    //Size for the stage, should have the right aspect ratio since it will always be visible
    //The dimension was determined by running on Windows and inspecting the dimension of the canvas after menubar and tabs are added
    public static final PDimension STAGE_SIZE = new PDimension( 1008, 680 );

    public FractionsIntroCanvas() {
        ControlPanelNode toolbox = new ControlPanelNode( new VBox( 0, new PhetPText( "Toolbox" ),
                                                                   new NumberText( "1" ),
                                                                   new NumberText( "2" ),
                                                                   new NumberText( "3" ),
                                                                   new NumberText( "4" ),
                                                                   new NumberText( "5" ),
                                                                   new NumberText( "6" ),
                                                                   new NumberText( "7" ),
                                                                   new NumberText( "8" ),
                                                                   new NumberText( "9" ),
                                                                   new NumberText( "10" ),
                                                                   new NumberText( "11" ),
                                                                   new NumberText( "12" ) ) ) {{
            setOffset( 20, FractionsIntroCanvas.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );

        addChild( toolbox );

        addChild( new FractionNode() {{
            setOffset( FractionsIntroCanvas.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }} );
    }

    public static class FractionNode extends PNode {
        public FractionNode() {
            PNode numerator = new PhetPText( "3", BIG_NUMBER_FONT );
            PNode denominator = new PhetPText( "4", BIG_NUMBER_FONT );

            addChild( numerator );
            numerator.setVisible( false );
            addChild( denominator );
            denominator.setVisible( false );
            final PhetPPath line = new PhetPPath( new Line2D.Double( -25, numerator.getFullBounds().getHeight(), Math.max( numerator.getFullBounds().getWidth(), denominator.getFullBounds().getWidth() ) + 25, numerator.getFullBounds().getHeight() ),
                                                  new BasicStroke( 10 ), Color.black );
            addChild( line );
            denominator.setOffset( line.getFullBounds().getCenterX() - denominator.getFullBounds().getWidth() / 2, line.getFullBounds().getMaxY() );

            addChild( new PhetPPath( RectangleUtils.compactRectangle2D( numerator.getFullBounds(), 0, 15 ), new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10f, 10f }, 0f ), Color.red ) );
            addChild( new PhetPPath( RectangleUtils.compactRectangle2D( denominator.getFullBounds(), 0, 15 ), new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10f, 10f }, 0f ), Color.red ) );
        }
    }

    public static class NumberText extends PNode {
        public NumberText( String number ) {
            addChild( new PhetPText( number, NUMBER_FONT ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mouseDragged( PInputEvent event ) {

                }
            } );
        }
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}