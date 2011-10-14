// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

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
    private final FractionNode fractionNode;

    public FractionsIntroCanvas() {
        ControlPanelNode toolbox = new ControlPanelNode( new VBox( 0, new PhetPText( "Toolbox" ),
                                                                   new NumberText( 1, this ),
                                                                   new NumberText( 2, this ),
                                                                   new NumberText( 3, this ),
                                                                   new NumberText( 4, this ),
                                                                   new NumberText( 5, this ),
                                                                   new NumberText( 6, this ),
                                                                   new NumberText( 7, this ),
                                                                   new NumberText( 8, this ),
                                                                   new NumberText( 9, this ),
                                                                   new NumberText( 10, this ),
                                                                   new NumberText( 11, this ),
                                                                   new NumberText( 12, this ) ) ) {{
            setOffset( 20, FractionsIntroCanvas.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBorder( null );

        addChild( toolbox );

        fractionNode = new FractionNode() {{
            setOffset( FractionsIntroCanvas.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 );
        }};
        addChild( fractionNode );
    }

    public static class FractionNode extends PNode {
        private PhetPPath numeratorBox;
        private PhetPPath denominatorBox;

        public FractionNode() {
            PNode numerator = new PhetPText( "12", BIG_NUMBER_FONT );
            PNode denominator = new PhetPText( "12", BIG_NUMBER_FONT );

//            addChild( numerator );
            numerator.setVisible( false );
//            addChild( denominator );
            denominator.setVisible( false );
            final PhetPPath line = new PhetPPath( new Line2D.Double( -25, numerator.getFullBounds().getHeight(), Math.max( numerator.getFullBounds().getWidth(), denominator.getFullBounds().getWidth() ) + 25, numerator.getFullBounds().getHeight() ),
                                                  new BasicStroke( 10 ), Color.black );
            addChild( line );
            denominator.setOffset( line.getFullBounds().getCenterX() - denominator.getFullBounds().getWidth() / 2, line.getFullBounds().getMaxY() );

            numeratorBox = new PhetPPath( RectangleUtils.compactRectangle2D( numerator.getFullBounds(), 5, 15 ), new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10f, 10f }, 0f ), Color.red );
            addChild( numeratorBox );
            denominatorBox = new PhetPPath( RectangleUtils.compactRectangle2D( denominator.getFullBounds(), 5, 15 ), new BasicStroke( 5, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1f, new float[] { 10f, 10f }, 0f ), Color.red );
            addChild( denominatorBox );
        }
    }

    public static class NumberText extends PNode {
        public NumberText( final int number, final FractionsIntroCanvas canvas ) {
            addChild( new PhetPText( "" + number, NUMBER_FONT ) );
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {

                private PNode created;

                @Override public void mousePressed( final PInputEvent event ) {
                    created = new PhetPText( "" + number, BIG_NUMBER_FONT ) {{
//                    created = new ZeroOffsetNode( new NumberGraphic( number ) {{
                        final Point2D position = event.getPositionRelativeTo( NumberText.this.getParent() );
                        setOffset( position.getX() - getFullBounds().getWidth() / 2, position.getY() - getFullBounds().getHeight() / 2 );
                        addInputEventListener( new CursorHandler() );
                        addInputEventListener( new PBasicInputEventHandler() {
                            @Override public void mouseDragged( PInputEvent event ) {
                                translate( event.getDeltaRelativeTo( getParent() ).getWidth(), event.getDeltaRelativeTo( getParent() ).getHeight() );
                            }

                            @Override public void mouseReleased( PInputEvent event ) {
                                if ( getGlobalFullBounds().intersects( canvas.fractionNode.numeratorBox.getGlobalFullBounds() ) ) {
                                    centerFullBoundsOnPoint( canvas.fractionNode.numeratorBox.getGlobalFullBounds().getCenterX(), canvas.fractionNode.numeratorBox.getGlobalFullBounds().getCenterY() );
                                    canvas.fractionNode.numeratorBox.setVisible( false );
                                    canvas.repaint();
                                }
                                else if ( getGlobalFullBounds().intersects( canvas.fractionNode.denominatorBox.getGlobalFullBounds() ) ) {
                                    centerFullBoundsOnPoint( canvas.fractionNode.denominatorBox.getGlobalFullBounds().getCenterX(), canvas.fractionNode.denominatorBox.getGlobalFullBounds().getCenterY() - 15 );
                                    canvas.fractionNode.denominatorBox.setVisible( false );
                                    canvas.repaint();
                                }
                            }
                        } );
                    }};
                    canvas.addChild( created );
                }

                @Override public void mouseDragged( PInputEvent event ) {
                    created.translate( event.getDeltaRelativeTo( getParent() ).getWidth(), event.getDeltaRelativeTo( getParent() ).getHeight() );
                }

                @Override public void mouseReleased( PInputEvent event ) {
                    if ( created.getGlobalFullBounds().intersects( canvas.fractionNode.numeratorBox.getGlobalFullBounds() ) ) {
                        created.centerFullBoundsOnPoint( canvas.fractionNode.numeratorBox.getGlobalFullBounds().getCenterX(), canvas.fractionNode.numeratorBox.getGlobalFullBounds().getCenterY() );
                        canvas.fractionNode.numeratorBox.setVisible( false );
                        canvas.repaint();
                    }
                    else if ( created.getGlobalFullBounds().intersects( canvas.fractionNode.denominatorBox.getGlobalFullBounds() ) ) {
                        created.centerFullBoundsOnPoint( canvas.fractionNode.denominatorBox.getGlobalFullBounds().getCenterX(), canvas.fractionNode.denominatorBox.getGlobalFullBounds().getCenterY() - 15 );
                        canvas.fractionNode.denominatorBox.setVisible( false );
                        canvas.repaint();
                    }
                }
            } );
        }
    }

    private void addChild( PNode node ) {
        rootNode.addChild( node );
    }
}