// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.*;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.LightRay;
import edu.colorado.phet.bendinglight.modules.prisms.WhiteLightNode;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class BendingLightCanvas<T extends BendingLightModel> extends PhetPCanvas {
    public static final PhetFont labelFont = new PhetFont( 16 );
    private PNode rootNode;
    public final BooleanProperty showNormal;
    public final BooleanProperty showProtractor = new BooleanProperty( false );
    protected final PNode mediumNode;
    protected final T model;
    protected final ModelViewTransform transform;
    protected final PDimension stageSize;
    protected final PNode lightRayLayer = new PNode();
    protected final PNode lightWaveLayer = new PNode();
    protected final PNode beforeLightLayer = new PNode();
    protected final PNode afterLightLayer = new PNode();
    public BooleanProperty clockRunningPressed;

    public BendingLightCanvas( final T model,
                               BooleanProperty moduleActive,
                               final Function1<Double, Double> clampDragAngle,
                               final Function1<Double, Boolean> clockwiseArrowNotAtMax,
                               final Function1<Double, Boolean> ccwArrowNotAtMax,
                               boolean showNormal,
                               Resettable resetAll,
                               final Function2<Shape, Shape, Shape> laserTranslationRegion,
                               final Function2<Shape, Shape, Shape> laserRotationRegion,
                               String laserImageName, final double centerOffsetLeft ) {
        this.showNormal = new BooleanProperty( showNormal );
        this.model = model;
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setBackground( Color.black );
        final int stageWidth = 1008;
        stageSize = new PDimension( stageWidth, stageWidth * model.getHeight() / model.getWidth() );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, stageSize ) );

        final double scale = stageSize.getHeight() / model.getHeight();
        transform = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                               new Point2D.Double( stageSize.getWidth() / 2 - centerOffsetLeft, stageSize.getHeight() / 2 ),
                                                                               scale );
        mediumNode = new PNode();
        addChild( mediumNode );

        final BooleanProperty showRotationDragHandles = new BooleanProperty( false );
        final BooleanProperty showTranslationDragHandles = new BooleanProperty( false );
        addChild( new RotationDragHandle( transform, model.getLaser(), 10, showRotationDragHandles, clockwiseArrowNotAtMax ) );
        addChild( new RotationDragHandle( transform, model.getLaser(), -10, showRotationDragHandles, ccwArrowNotAtMax ) );
        double arrowLength = 100;
        addChild( new TranslationDragHandle( transform, model.getLaser(), -arrowLength, 0, showTranslationDragHandles ) );
        addChild( new TranslationDragHandle( transform, model.getLaser(), 0, -arrowLength, showTranslationDragHandles ) );
        addChild( new TranslationDragHandle( transform, model.getLaser(), arrowLength, 0, showTranslationDragHandles ) );
        addChild( new TranslationDragHandle( transform, model.getLaser(), 0, arrowLength, showTranslationDragHandles ) );
        addChild( new LaserNode( transform, model.getLaser(), showRotationDragHandles, showTranslationDragHandles, clampDragAngle, laserTranslationRegion, laserRotationRegion, laserImageName ) );

        final VoidFunction1<LightRay> addLightRayNode = new VoidFunction1<LightRay>() {
            public void apply( LightRay lightRay ) {
                final PNode node = model.laserView.getValue().createNode( transform, lightRay );
                final PNode layer = model.laserView.getValue().getLayer( lightRayLayer, lightWaveLayer );
                layer.addChild( node );
                lightRay.addMoveToFrontListener( new VoidFunction0() {
                    public void apply() {
                        node.moveToFront();
                    }
                } );//TODO: memory leak
                lightRay.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        layer.removeChild( node );
                    }
                } );
            }
        };
        for ( LightRay lightRay : model.getRays() ) {
            addLightRayNode.apply( lightRay );
        }
        model.addRayAddedListener( new VoidFunction1<LightRay>() {
            public void apply( final LightRay lightRay ) {
                addLightRayNode.apply( lightRay );
            }
        } );

        //No time readout
        clockRunningPressed = new BooleanProperty( true );
        final And clockRunning = clockRunningPressed.and( moduleActive );
        clockRunning.addObserver( new SimpleObserver() {
            public void update() {
                if ( clockRunning.getValue() ) {
                    model.getClock().start();
                }
                else {
                    model.getClock().pause();
                }
            }
        } );
        addChild( beforeLightLayer );
        addChild( lightRayLayer );
        addChild( lightWaveLayer );

        final WhiteLightNode whiteLightNode = new WhiteLightNode( lightRayLayer );
        addChild( whiteLightNode );

        addChild( afterLightLayer );

        //Switch between light renderers for white vs nonwhite light
        model.getLaser().color.addObserver( new SimpleObserver() {
            public void update() {
                boolean white = model.getLaser().color.getValue() == LaserColor.WHITE_LIGHT;
                whiteLightNode.setVisible( white );
                lightRayLayer.setVisible( !white );
                lightWaveLayer.setVisible( !white );
            }
        } );
//
        //Coalesce repeat updates
        final boolean[] dirty = new boolean[] { true };
        model.addModelUpdateListener( new VoidFunction0() {
            public void apply() {
                dirty[0] = true;
            }
        } );
        //Update coalesced events every 30 ms
        //TODO: put this in the module clock so it's not running when this tab isn't active
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dirty[0] ) {
                    whiteLightNode.updateImage();
                    dirty[0] = false;
                }
            }
        } );
        timer.start();
        //Debug for showing stage
//        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.getWidth(), STAGE_SIZE.getHeight() ), new BasicStroke( 2 ), Color.red ) );
    }


    public PNode getRootNode() {
        return rootNode;
    }

    public void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    public void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }

    public void removeChildBehindLight( PNode node ) {
        beforeLightLayer.removeChild( node );
    }

    public void addChildBehindLight( PNode node ) {
        beforeLightLayer.addChild( node );
    }

    public void resetAll() {
        showNormal.reset();
        showProtractor.reset();
        clockRunningPressed.reset();
    }

    public T getModel() {
        return model;
    }
}
