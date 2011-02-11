// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.view.LaserNode;
import edu.colorado.phet.lightreflectionandrefraction.view.LightRayNode;
import edu.colorado.phet.lightreflectionandrefraction.view.MediumNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.lightreflectionandrefraction.modules.intro.ControlPanelNode.labelFont;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionCanvas extends PhetPCanvas {
    private PNode rootNode;
    public final BooleanProperty showNormal = new BooleanProperty( true );
    public final BooleanProperty showProtractor = new BooleanProperty( false );
    public final Property<LaserView> laserView = new Property<LaserView>( LaserView.RAY );

    public static abstract class LaserView {
        public static final LaserView RAY = new LaserView() {
            public PNode createNode( ModelViewTransform transform, LightRay lightRay ) {
                return new LightRayNode( transform, lightRay );
            }
        };
        public static final LaserView WAVE = new LaserView() {
            public PNode createNode( ModelViewTransform transform, LightRay lightRay ) {
                return new LightWaveNode( transform, lightRay );
            }
        };

        public abstract PNode createNode( ModelViewTransform transform, LightRay lightRay );
    }

    public LightReflectionAndRefractionCanvas( final LRRModel model ) {
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        final int stageWidth = 1008;
        final PDimension STAGE_SIZE = new PDimension( stageWidth, stageWidth * model.getHeight() / model.getWidth() );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, STAGE_SIZE ) );

        final double scale = STAGE_SIZE.getHeight() / model.getHeight();
        final ModelViewTransform transform = ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ),
                                                                                                        new Point2D.Double( STAGE_SIZE.getWidth() / 2 - 150, STAGE_SIZE.getHeight() / 2 ),
                                                                                                        scale );
        addChild( new MediumNode( transform, model.topMedium ) );
        addChild( new MediumNode( transform, model.bottomMedium ) );
        //add a line that will show the border between the mediums even when both n's are the same... Just a thin line will be fine.
        addChild( new PhetPPath( transform.modelToView( new Line2D.Double( -1, 0, 1, 0 ) ), new BasicStroke( 0.5f ), Color.gray ) {{
            setPickable( false );
        }} );
        final BooleanProperty showDragHandles = new BooleanProperty( false );
        addChild( new LaserNodeDragHandle( transform, model.getLaser(), 10, showDragHandles, new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble < Math.PI;
            }
        } ) );
        addChild( new LaserNodeDragHandle( transform, model.getLaser(), -10, showDragHandles, new Function1<Double, Boolean>() {
            public Boolean apply( Double aDouble ) {
                return aDouble > Math.PI / 2;
            }
        } ) );
        addChild( new LaserNode( transform, model.getLaser(), showDragHandles ) );

        addChild( new ControlPanelNode( new PNode() {{
            final PText title = new PText( "Laser View" ) {{setFont( labelFont );}};
            addChild( title );
            addChild( new PSwing( new VerticalLayoutPanel() {{
                add( new PropertyRadioButton<LaserView>( "Ray", laserView, LaserView.RAY ) {{setFont( labelFont );}} );
                add( new PropertyRadioButton<LaserView>( "Wave", laserView, LaserView.WAVE ) {{setFont( labelFont );}} );
                SwingUtils.setBackgroundDeep( this, new Color( 0, 0, 0, 0 ) );
            }} ) {{
                setOffset( 0, title.getFullBounds().getMaxY() );
            }} );
        }} ) {{
            setOffset( 5, 5 );
        }} );

        addChild( new ControlPanelNode( new ToolboxNode( this, transform, showProtractor, -model.getWidth() * 0.3, -model.getHeight() * 0.2, showNormal, model.getIntensityMeter() ) ) {{
            setOffset( 10, STAGE_SIZE.height - getFullBounds().getHeight() - 10 );
        }} );

        addChild( new NormalLine( transform, model.getHeight() ) {{
            showNormal.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showNormal.getValue() );
                }
            } );
        }} );

        laserView.addObserver( new SimpleObserver() {
            public void update() {
                model.regenerateRays();//TODO: Maybe it would be better just to regenerate view, but now we just do this by telling the model to recompute and repopulate
            }
        } );
        final VoidFunction1<LightRay> addLightRayNode = new VoidFunction1<LightRay>() {
            public void apply( LightRay lightRay ) {
                final PNode node = laserView.getValue().createNode( transform, lightRay );
                addChild( node );
                lightRay.addMoveToFrontListener( new VoidFunction0() {
                    public void apply() {
                        node.moveToFront();
                    }
                } );//TODO: memory leak
                lightRay.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        removeChild( node );
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

        addChild( new ControlPanelNode( new MediumControlPanel( this, model.topMedium, model.colorMappingFunction ) ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) - 10 - getFullBounds().getHeight() );
        }} );
        addChild( new ControlPanelNode( new MediumControlPanel( this, model.bottomMedium, model.colorMappingFunction ) ) {{
            setOffset( STAGE_SIZE.width - getFullBounds().getWidth() - 10, transform.modelToViewY( 0 ) + 10 );
        }} );

        //Debug for showing stage
//        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, STAGE_SIZE.getWidth(), STAGE_SIZE.getHeight() ), new BasicStroke( 2 ), Color.red ) );
    }

    public static class ControlPanel extends PNode {
        public ControlPanel( JComponent controlPanel ) {
            final PSwing pswing = new PSwing( controlPanel );
            addChild( pswing );
            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, pswing.getFullBounds().getWidth(), pswing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 3 ), Color.white ) );
        }
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}
