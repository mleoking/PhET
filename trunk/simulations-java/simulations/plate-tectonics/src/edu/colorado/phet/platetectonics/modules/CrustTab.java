// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.modules;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.nodes.OrthoPiccoloNode;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing;
import edu.colorado.phet.platetectonics.control.MyCrustPanel;
import edu.colorado.phet.platetectonics.control.OptionsPanel;
import edu.colorado.phet.platetectonics.control.ZoomPanel;
import edu.colorado.phet.platetectonics.model.CrustModel;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.util.Bounds3D;
import edu.colorado.phet.platetectonics.util.Grid3D;
import edu.colorado.phet.platetectonics.view.PlateView;
import edu.colorado.phet.platetectonics.view.RangeLabel;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.CONTINENTAL_CRUST;
import static edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings.OCEANIC_CRUST;

/**
 * Represents the 1st tab, which has a modifiable section of crust surrounded by oceanic and continental crusts, all
 * on top of the mantle.
 */
public class CrustTab extends PlateTectonicsTab {

    private Property<Float> scaleProperty = new Property<Float>( 1f );
    private final Property<Boolean> showLabels = new Property<Boolean>( false );

    public CrustTab( LWJGLCanvas canvas ) {
        super( canvas, Strings.CRUST_TAB, 2 ); // 0.5 km => 1 distance in view

        zoomRatio.addObserver( new SimpleObserver() {
            public void update() {
                scaleProperty.set( getSceneDistanceZoomFactor() );
            }
        } );
    }

    @Override public void initialize() {
        super.initialize();

        getClock().start();

        // grid centered X, with front Z at 0
        Grid3D grid = new Grid3D(
                Bounds3D.fromMinMax( -1500000, 1500000,
                                     -150000, 15000,
                                     -2000000, 0 ),
                512, 512, 64 );

        // create the model and terrain
        setModel( new CrustModel( grid ) );

        guiLayer.addChild( createFPSReadout( Color.BLACK ) );

        // TODO: improve the plate view
        sceneLayer.addChild( new PlateView( getModel(), this ) );

        final Function1<ImmutableVector3F, ImmutableVector3F> flatModelToView = new Function1<ImmutableVector3F, ImmutableVector3F>() {
            public ImmutableVector3F apply( ImmutableVector3F v ) {
                return getModelViewTransform().transformPosition( PlateModel.convertToRadial( v ) );
            }
        };

        GLNode layerLabels = new GLNode() {{
            showLabels.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showLabels.get() );
                }
            } );
        }};
        sceneLayer.addChild( layerLabels );

        // crust label
        layerLabels.addChild( new RangeLabel( new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
            beforeFrameRender.addUpdateListener( new UpdateListener() {
                public void update() {
                    set( flatModelToView.apply( new ImmutableVector3F( -10000, (float) getCrustModel().getCenterCrustElevation(), 0 ) ) );
                }
            }, true );
        }}, new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
            beforeFrameRender.addUpdateListener( new UpdateListener() {
                public void update() {
                    set( flatModelToView.apply( new ImmutableVector3F( -10000, (float) getCrustModel().getCenterCrustBottomY(), 0 ) ) );
                }
            }, true );
        }}, "Crust", scaleProperty
        ) ); // TODO: i18n

        // TODO: refactor so label heights are from the model. AYE!
        final Property<ImmutableVector3F> upperMantleTop = new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
            beforeFrameRender.addUpdateListener( new UpdateListener() {
                public void update() {
                    set( flatModelToView.apply( new ImmutableVector3F( 0, (float) getCrustModel().getCenterCrustBottomY(), 0 ) ) );
                }
            }, true );
        }};
        final Property<ImmutableVector3F> upperMantleBottom = new Property<ImmutableVector3F>( flatModelToView.apply( new ImmutableVector3F( 0, -750000, 0 ) ) );

        // TODO: refactor and cleanup
        Function2<Property<ImmutableVector3F>, Property<ImmutableVector3F>, Property<ImmutableVector3F>> labelPositionFunction
                = new Function2<Property<ImmutableVector3F>, Property<ImmutableVector3F>, Property<ImmutableVector3F>>() {
            public Property<ImmutableVector3F> apply( final Property<ImmutableVector3F> aProp,
                                                      final Property<ImmutableVector3F> bProp ) {
                return new Property<ImmutableVector3F>( new ImmutableVector3F() ) {{
                    final SimpleObserver observer = new SimpleObserver() {
                        public void update() {
                            ImmutableVector2F screenBottom = getBottomCenterPositionOnZPlane();
                            if ( bProp.get().y < screenBottom.y ) {
                                // use the screen bottom, the actual bottom is too low
                                float averageY = ( screenBottom.y + aProp.get().y ) / 2;
                                float ratio = ( averageY - aProp.get().y ) / ( bProp.get().y - aProp.get().y );
                                set( aProp.get().times( 1 - ratio ).plus( bProp.get().times( ratio ) ) );
                            }
                            else {
                                set( aProp.get().plus( bProp.get() ).times( 0.5f ) );
                            }
                        }
                    };

                    // TODO: debug listener ordering issue that is causing jittering when zooming in/out
                    scaleProperty.addObserver( observer );
                    beforeFrameRender.addUpdateListener( new UpdateListener() {
                        public void update() {
                            observer.update();
                        }
                    }, false );
                }};
            }
        };

        layerLabels.addChild( new RangeLabel(
                upperMantleTop,
                upperMantleBottom,
                "Mantle", scaleProperty,   // TODO: i18n
                labelPositionFunction.apply( upperMantleTop, upperMantleBottom )
        ) );

        Property<ImmutableVector3F> lowerMantleTop = new Property<ImmutableVector3F>( flatModelToView.apply( new ImmutableVector3F( 150000, -750000, 0 ) ) );
        Property<ImmutableVector3F> lowerMantleBottom = new Property<ImmutableVector3F>( flatModelToView.apply( new ImmutableVector3F( 150000, -2921000, 0 ) ) );
        layerLabels.addChild( new RangeLabel(
                lowerMantleTop,
                lowerMantleBottom,
                "Lower Mantle", scaleProperty,   // TODO: i18n
                labelPositionFunction.apply( lowerMantleTop, lowerMantleBottom )
        ) );

        Property<ImmutableVector3F> outerCoreTop = new Property<ImmutableVector3F>( flatModelToView.apply( new ImmutableVector3F( -250000, -2921000, 0 ) ) );
        Property<ImmutableVector3F> outerCoreBottom = new Property<ImmutableVector3F>( flatModelToView.apply( new ImmutableVector3F( -250000, -5180000, 0 ) ) );
        layerLabels.addChild( new RangeLabel(
                outerCoreTop,
                outerCoreBottom,
                "Outer Core", scaleProperty,   // TODO: i18n
                labelPositionFunction.apply( outerCoreTop, outerCoreBottom )
        ) );

        Property<ImmutableVector3F> innerCoreTop = new Property<ImmutableVector3F>( flatModelToView.apply( new ImmutableVector3F( 250000, -5180000, 0 ) ) );
        Property<ImmutableVector3F> innerCoreBottom = new Property<ImmutableVector3F>( flatModelToView.apply( new ImmutableVector3F( 250000, -PlateModel.EARTH_RADIUS, 0 ) ) );
        layerLabels.addChild( new RangeLabel(
                innerCoreTop,
                innerCoreBottom,
                "Inner Core", scaleProperty,   // TODO: i18n
                labelPositionFunction.apply( innerCoreTop, innerCoreBottom )
        ) );

        /*---------------------------------------------------------------------------*
        * my crust
        *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode( new ControlPanelNode( new MyCrustPanel( getCrustModel() ) ), CrustTab.this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            // layout the panel if its size changes (and on startup)
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            Math.ceil( ( getStageSize().width - getComponentWidth() ) / 2 ), // center horizontally
                            10 ) ); // offset from top
                }
            } );

            updateOnEvent( beforeFrameRender );

            zoomRatio.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( zoomRatio.get() == 1 );
                }
            } );
        }} );

        /*---------------------------------------------------------------------------*
        * temporary zoom control
        *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode( new ControlPanelNode( new ZoomPanel( zoomRatio ) ), CrustTab.this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            // top right
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D(
                            getStageSize().width - getComponentWidth() - 10,
                            10
                    ) );
                }
            } );

            updateOnEvent( beforeFrameRender );
        }} );

        /*---------------------------------------------------------------------------*
         * options panel
         *----------------------------------------------------------------------------*/
        addGuiNode( new OrthoPiccoloNode(
                new ControlPanelNode( new OptionsPanel( showLabels, new Runnable() {
                    public void run() {
                        resetAll();
                    }
                }, colorMode ) ),
                CrustTab.this, getCanvasTransform(),
                new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            canvasSize.addObserver( new SimpleObserver() {
                public void update() {
                    position.set( new ImmutableVector2D( getStageSize().width - getComponentWidth() - 10,
                                                         getStageSize().height - getComponentHeight() - 10 ) );
                }
            } );
            updateOnEvent( beforeFrameRender );
        }} );

        /*---------------------------------------------------------------------------*
        * labels
        *----------------------------------------------------------------------------*/

        // "oceanic crust" label
        guiLayer.addChild(
                new OrthoPiccoloNode( new PText( OCEANIC_CRUST ) {{
                    setFont( new PhetFont( 16, true ) );
                }},
                                      this,
                                      getCanvasTransform(),
                                      new Property<ImmutableVector2D>( new ImmutableVector2D( 30, getStageSize().getHeight() * 0.38 ) ),
                                      mouseEventNotifier ) {{
                    zoomRatio.addObserver( new SimpleObserver() {
                        public void update() {
                            setVisible( zoomRatio.get() == 1 );
                        }
                    } );
                }} );

        // "continental crust" label
        guiLayer.addChild( new OrthoPiccoloNode( new PText( CONTINENTAL_CRUST ) {{
            setFont( new PhetFont( 16, true ) );
        }}, this, getCanvasTransform(), new Property<ImmutableVector2D>( new ImmutableVector2D() ), mouseEventNotifier ) {{
            // TODO: improve positioning to handle i18n?
            position.set( new ImmutableVector2D( getStageSize().getWidth() - getComponentWidth() - 30,
                                                 getStageSize().getHeight() * 0.38 ) );
            zoomRatio.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( zoomRatio.get() == 1 );
                }
            } );
        }} );
    }

    @Override public void resetAll() {
        super.resetAll();

        showLabels.reset();
    }

    public CrustModel getCrustModel() {
        return (CrustModel) getModel();
    }

    public IUserComponent getUserComponent() {
        return PlateTectonicsSimSharing.UserComponents.crustTab;
    }
}
