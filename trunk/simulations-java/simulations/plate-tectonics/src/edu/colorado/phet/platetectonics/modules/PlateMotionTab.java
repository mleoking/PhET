// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLCursorHandler;
import edu.colorado.phet.lwjglphet.math.ImmutableMatrix4F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.nodes.GuiNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.lwjglphet.shapes.UnitMarker;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.control.CrustChooserPanel;
import edu.colorado.phet.platetectonics.control.PlayModePanel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;
import edu.umd.cs.piccolo.PNode;

/**
 * Displays two main plates that the user can direct to move towards, away from, or along each other.
 */
public class PlateMotionTab extends PlateTectonicsTab {

    public final Property<Boolean> isAutoMode = new Property<Boolean>( false );
    private CrustChooserPanel crustChooserPanel;
    private OrthoPiccoloNode crustChooserNode;
    private GuiNode crustPieceLayer;

    public PlateMotionTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.PLATE_MOTION_TAB, 0.5f );
    }

    @Override public void initialize() {
        super.initialize();

        crustPieceLayer = new GuiNode( this );
        rootNode.addChild( crustPieceLayer );

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -700000, 700000,
                                     -400000, 15000,
                                     -250000, 0 ),
                256, 256, 32 );

        // create the model and terrain
//        model = new AnimatedPlateModel( grid );
        setModel( new PlateMotionModel( grid ) );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

        sceneLayer.addChild( new PlateView( getModel(), this, grid ) );

        /*---------------------------------------------------------------------------*
         * manual / automatic switch
         *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode( new ControlPanelNode( new PlayModePanel( isAutoMode ) ), this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D( 10, 10 ) ), mouseEventNotifier ) {{
            updateOnEvent( beforeFrameRender );
        }} );

        /*---------------------------------------------------------------------------*
         * crust chooser
         *----------------------------------------------------------------------------*/
        crustChooserPanel = new CrustChooserPanel();
        crustChooserNode = new OrthoPiccoloNode( new ControlPanelNode( crustChooserPanel ), this, getCanvasTransform(),
                                                 new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            getStageSize().width - getComponentWidth() - 10, // right side
                            getStageSize().height - getComponentHeight() - 10 ) ); // offset from bottom
                }
            } );
            updateOnEvent( beforeFrameRender );
        }};
        addGuiNode( crustChooserNode );

        crustPieceLayer.addChild( new UnitMarker() {{
            // TODO: change to float version
            ImmutableVector2D nodeOffset = crustChooserNode.position.get();
            ImmutableVector2F pieceOffset = crustChooserPanel.getYoungOceanicCenter();
            transform.append( ImmutableMatrix4F.translation( (float) nodeOffset.getX(), (float) nodeOffset.getY(), 0 ) );
            transform.append( ImmutableMatrix4F.translation( pieceOffset.x, pieceOffset.y, 0 ) );
            transform.append( ImmutableMatrix4F.scaling( 10 ) );
        }} );

        // continental piece
        OrthoPiccoloNode continentalPiece = new OrthoPiccoloNode( new CrustPiece( CrustChooserPanel.CRUST_AREA_MAX_HEIGHT, 0.8f ), this, getCanvasTransform(),
                                                                  new Property<ImmutableVector2D>(
                                                                          new ImmutableVector2D() ),
                                                                  mouseEventNotifier ) {{
            position.set( new ImmutableVector2D(
                    getContinentalOffset().x - getNode().getFullBounds().getWidth() / 2,
                    getContinentalOffset().y - getNode().getFullBounds().getHeight() / 2 ) );
        }};
        crustPieceLayer.addChild( continentalPiece );
        guiNodes.add( continentalPiece );

        // young oceanic piece
        OrthoPiccoloNode youngOceanicPiece = new OrthoPiccoloNode( new CrustPiece( 35, 0.5f ), this, getCanvasTransform(),
                                                                   new Property<ImmutableVector2D>(
                                                                           new ImmutableVector2D() ),
                                                                   mouseEventNotifier ) {{
            position.set( new ImmutableVector2D(
                    getYoungOceanicOffset().x - getNode().getFullBounds().getWidth() / 2,
                    getYoungOceanicOffset().y - getNode().getFullBounds().getHeight() / 2 ) );
        }};
        crustPieceLayer.addChild( youngOceanicPiece );
        guiNodes.add( youngOceanicPiece );

        // old oceanic piece
        OrthoPiccoloNode oldOceanicPiece = new OrthoPiccoloNode( new CrustPiece( 35, 0.4f ), this, getCanvasTransform(),
                                                                 new Property<ImmutableVector2D>(
                                                                         new ImmutableVector2D() ),
                                                                 mouseEventNotifier ) {{
            position.set( new ImmutableVector2D(
                    getOldOceanicOffset().x - getNode().getFullBounds().getWidth() / 2,
                    getOldOceanicOffset().y - getNode().getFullBounds().getHeight() / 2 ) );
        }};
        crustPieceLayer.addChild( oldOceanicPiece );
        guiNodes.add( oldOceanicPiece );
    }

    private static class CrustPiece extends PNode {
        public CrustPiece( final float height, float intensity ) {
            final float topHeight = 20;
            final float rightWidth = 30;

            float topIntensity = 1 - ( 1 - intensity ) * 0.5f;
            float rightIntensity = 1 - ( 1 - intensity ) * 0.3f;

            Color mainColor = new Color( intensity, intensity, intensity, 1f );
            Color topColor = new Color( topIntensity, topIntensity, topIntensity, 1f );
            Color rightColor = new Color( rightIntensity, rightIntensity, rightIntensity, 1f );
            Color strokePaint = new Color( 0.2f, 0.2f, 0.2f, 1f );
            BasicStroke stroke = new BasicStroke( 1 );

            // front
            addChild( new PhetPPath(
                    new Rectangle2D.Double( 0, topHeight, CrustChooserPanel.CRUST_AREA_MAX_WIDTH - rightWidth, height - topHeight ),
                    mainColor, stroke, strokePaint ) );

            // top
            addChild( new PhetPPath( new DoubleGeneralPath() {{
                moveTo( 0, topHeight );
                lineTo( rightWidth, 0 );
                lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH, 0 );
                lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH - rightWidth, topHeight );
            }}.getGeneralPath(), topColor, stroke, strokePaint ) );

            // right
            addChild( new PhetPPath( new DoubleGeneralPath() {{
                moveTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH - rightWidth, topHeight );
                lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH, 0 );
                lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH, height - topHeight );
                lineTo( CrustChooserPanel.CRUST_AREA_MAX_WIDTH - rightWidth, height );
            }}.getGeneralPath(), rightColor, stroke, strokePaint ) );

            addInputEventListener( new LWJGLCursorHandler() );
        }
    }

    private ImmutableVector2F getCrustOffset( ImmutableVector2F pieceOffset ) {
        ImmutableVector2D nodeOffset = crustChooserNode.position.get();
        return new ImmutableVector2F( (float) nodeOffset.getX() + pieceOffset.x,
                                      (float) nodeOffset.getY() + pieceOffset.y );
    }

    private ImmutableVector2F getContinentalOffset() {
        return getCrustOffset( crustChooserPanel.getContinentalCenter() );
    }

    private ImmutableVector2F getYoungOceanicOffset() {
        return getCrustOffset( crustChooserPanel.getYoungOceanicCenter() );
    }

    private ImmutableVector2F getOldOceanicOffset() {
        return getCrustOffset( crustChooserPanel.getOldOceanicCenter() );
    }
}
