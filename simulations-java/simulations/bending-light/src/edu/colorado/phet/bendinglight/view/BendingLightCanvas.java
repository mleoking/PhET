// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.bendinglight.model.LightRay;
import edu.colorado.phet.bendinglight.modules.prisms.WhiteLightNode;
import edu.colorado.phet.common.phetcommon.math.ImmutableRectangle2D;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.PhetUtilities;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * Base class for Bending Light canvases.
 *
 * @author Sam Reid
 */
public class BendingLightCanvas<T extends BendingLightModel> extends PhetPCanvas {
    public static final PhetFont labelFont = new PhetFont( 16 );//Font for labels in controls
    private PNode rootNode;
    public final BooleanProperty showNormal;
    public final BooleanProperty showProtractor = new BooleanProperty( false );
    protected final PNode mediumNode;//Node for Medium that the laser is in
    protected final T model;//BendingLightModel class
    protected final ModelViewTransform transform;
    protected final PDimension stageSize;
    protected final PNode lightRayLayer = new PNode();
    protected final PNode lightWaveLayer = new PNode();
    private final boolean debug = false;

    //In order to make controls (including the laser itself) accessible (not obscured by the large protractor), KP suggested this layering order:
    //laser on top
    //Control boxes next
    //Protractor
    //Laser beam
    //To implement this, we specify before light layer and 2 after light layers
    protected final PNode beforeLightLayer = new PNode();
    protected final PNode afterLightLayer = new PNode();//in back of afterlightlayer2
    protected final PNode afterLightLayer2 = new PNode();
    public final BooleanProperty playing;//True if the play/pause buttons are in "play" mode
    private BufferedImage bufferedImage;

    public BendingLightCanvas( final T model,
                               final BooleanProperty moduleActive,
                               final Function1<Double, Double> clampDragAngle,
                               final Function1<Double, Boolean> clockwiseArrowNotAtMax,
                               final Function1<Double, Boolean> ccwArrowNotAtMax,
                               boolean showNormal,
                               final Function2<Shape, Shape, Shape> laserTranslationRegion,
                               final Function2<Shape, Shape, Shape> laserRotationRegion,
                               String laserImageName, final double centerOffsetLeft ) {
        this.showNormal = new BooleanProperty( showNormal );
        this.model = model;
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );
        final int stageWidth = 1008;

        //Use the model aspect ratio and specified stage width to create the stage dimension
        stageSize = new PDimension( stageWidth, stageWidth * model.getHeight() / model.getWidth() );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, stageSize ) );//Center the stage in the canvas, specifies how things scale up and down with window size, maps stage to pixels

        //Create the transform from model (SI) to view (stage) coordinates
        final double scale = stageSize.getHeight() / model.getHeight();
        transform = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                               new Point2D.Double( stageSize.getWidth() / 2 - centerOffsetLeft, stageSize.getHeight() / 2 ),
                                                                               scale );

        //Create and add the graphic for the environment medium
        mediumNode = new PNode();
        addChild( mediumNode );

        //Inner function for adding light rays
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

        //Add ray graphics for pre-existing rays
        for ( LightRay lightRay : model.getRays() ) {
            addLightRayNode.apply( lightRay );
        }

        //Add rays graphics for new rays that get created by the model
        model.addRayAddedListener( new VoidFunction1<LightRay>() {
            public void apply( final LightRay lightRay ) {
                addLightRayNode.apply( lightRay );
            }
        } );

        //No time readout
        playing = new BooleanProperty( true );
        final And clockRunning = playing.and( moduleActive );
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

        final WhiteLightNode whiteLightNode = new WhiteLightNode( lightRayLayer );

        //layering
        addChild( beforeLightLayer );
        addChild( lightRayLayer );
        addChild( lightWaveLayer );
        addChild( whiteLightNode );
        addChild( afterLightLayer );
        addChild( afterLightLayer2 );

        //Switch between light renderers for white vs nonwhite light
        model.getLaser().color.addObserver( new SimpleObserver() {
            public void update() {
                boolean white = model.getLaser().color.getValue() == LaserColor.WHITE_LIGHT;
                whiteLightNode.setVisible( white );
                lightRayLayer.setVisible( !white );
                lightWaveLayer.setVisible( !white );
            }
        } );

        //Add rotation for the laser that show if/when the laser can be rotated about its pivot
        final BooleanProperty showRotationDragHandles = new BooleanProperty( false );
        final BooleanProperty showTranslationDragHandles = new BooleanProperty( false );
        addChild( new RotationDragHandle( transform, model.getLaser(), 10, showRotationDragHandles, clockwiseArrowNotAtMax ) );
        addChild( new RotationDragHandle( transform, model.getLaser(), -10, showRotationDragHandles, ccwArrowNotAtMax ) );

        //Add translation indicators that show if/when the laser can be moved by dragging
        double arrowLength = 100;
        addChild( new TranslationDragHandle( transform, model.getLaser(), -arrowLength, 0, showTranslationDragHandles ) );
        addChild( new TranslationDragHandle( transform, model.getLaser(), 0, -arrowLength, showTranslationDragHandles ) );
        addChild( new TranslationDragHandle( transform, model.getLaser(), arrowLength, 0, showTranslationDragHandles ) );
        addChild( new TranslationDragHandle( transform, model.getLaser(), 0, arrowLength, showTranslationDragHandles ) );

        //Add the laser itself
        addChild( new LaserNode( transform, model.getLaser(), showRotationDragHandles, showTranslationDragHandles, clampDragAngle, laserTranslationRegion, laserRotationRegion, laserImageName, model.visibleModelBounds ) );

        //Coalesce repeat updates so work is not duplicated
        final boolean[] dirty = new boolean[] { true };
        model.addModelUpdateListener( new VoidFunction0() {
            public void apply() {
                dirty[0] = true;
            }
        } );
        //Update coalesced events every 30 ms
        //TODO: put this in the module clock so it's not running when this tab isn't active, but it is basically no-op when this tab isn't active, so no big deal
        Timer timer = new Timer( 30, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dirty[0] ) {
                    whiteLightNode.updateImage();
                    dirty[0] = false;
                }
            }
        } );
        timer.start();

        if ( debug ) {
            //Debug for showing stage
            addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, stageSize.getWidth(), stageSize.getHeight() ), new BasicStroke( 2 ), Color.red ) );

            //Debug for showing model bounds
            addChild( new PhetPPath( new BasicStroke( 3 ), Color.green ) {{
                model.visibleModelBounds.addObserver( new VoidFunction1<Option<ImmutableRectangle2D>>() {
                    public void apply( Option<ImmutableRectangle2D> immutableRectangle2DOption ) {
                        setPathTo( immutableRectangle2DOption.isSome() ? transform.modelToView( immutableRectangle2DOption.get().toShape() ) : null );
                    }
                } );
            }} );
        }
    }

    //Notify model elements about the canvas area so they can't be dragged outside it.
    @Override protected void updateLayout() {
        super.updateLayout();

        //identify the bounds that objects will be constrained to be dragged within
        int insetPixels = 10;
        final Rectangle2D.Double viewBounds = new Rectangle2D.Double( insetPixels, insetPixels, getWidth() - insetPixels * 2, getHeight() - insetPixels * 2 );

        //Convert to model bounds and store in the model
        final ImmutableRectangle2D modelBounds = new ImmutableRectangle2D( transform.viewToModel( getRootNode().globalToLocal( viewBounds ) ) );
        model.visibleModelBounds.setValue( new Option.Some<ImmutableRectangle2D>( modelBounds ) );
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

    public void addChildAfterLight( PNode node ) {
        afterLightLayer.addChild( node );
    }

    public void removeChildAfterLight( PNode node ) {
        afterLightLayer.removeChild( node );
    }

    public void resetAll() {
        showNormal.reset();
        showProtractor.reset();
        playing.reset();
    }

    public T getModel() {
        return model;
    }

    //Using BufferedPhetPCanvas prevents a jittering problem on the 2nd tab, see #2786 -- but only apply this solution on Windows since it causes problem on Mac and mac has no jitter problem
    //This code is copied from BufferedPhetPCanvas
    public void paintComponent( Graphics g ) {
        if ( PhetUtilities.isMacintosh() ) {
            super.paintComponent( g );
        }
        //Apply the workaround on windows and linux since they have similar behavior
        else {
            //Create a new buffer if the old one is wrong size or doesn't exist
            if ( ( bufferedImage == null || bufferedImage.getWidth() != getWidth() || bufferedImage.getHeight() != getHeight() ) ) {
                bufferedImage = new BufferedImage( getWidth(), getHeight(), TYPE_INT_RGB );
            }
            //Draw into the buffer
            Graphics2D bufferedGraphics = bufferedImage.createGraphics();
            bufferedGraphics.setClip( g.getClipBounds() );
            super.paintComponent( bufferedGraphics );

            //Draw the buffer into this canvas
            ( (Graphics2D) g ).drawRenderedImage( bufferedImage, new AffineTransform() );

            //Dispose for memory
            bufferedGraphics.dispose();
        }
    }

    //Returns a function that maps from proposed model point to allowed model point (i.e. within visible bounds), for use with RelativeDragHandler
    public Function1<Point2D, Point2D> getBoundedConstraint() {
        return new Function1<Point2D, Point2D>() {
            public Point2D apply( Point2D proposedModelPoint ) {
                return model.visibleModelBounds.getClosestPoint( proposedModelPoint );
            }
        };
    }
}