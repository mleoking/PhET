// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.view.LightRayNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionCanvas extends PhetPCanvas {
    private PNode rootNode;

    public LightReflectionAndRefractionCanvas( final LRRModel model ) {
        // Root of our scene graph
        rootNode = new PNode();
        addWorldChild( rootNode );

        setWorldTransformStrategy( new PhetPCanvas.CenteredStage( this, LRRModel.STAGE_SIZE ) );

        setBackground( Color.black );

        final ModelViewTransform transform = ModelViewTransform.createRectangleInvertedYMapping(
                new Rectangle2D.Double( -model.getWidth() / 2, -model.getHeight() / 2, model.getWidth(), model.getHeight() ),
                new Rectangle2D.Double( 0, 0, LRRModel.STAGE_SIZE.width, LRRModel.STAGE_SIZE.height ) );

        final VoidFunction1<LightRay> addLightRayNode = new VoidFunction1<LightRay>() {
            public void apply( LightRay lightRay ) {
                final LightRayNode node = new LightRayNode( transform, lightRay );
                addChild( node );
                lightRay.addRemovalListener( new VoidFunction0() {
                    public void apply() {
                        removeChild( node );
                    }
                } );
            }
        };

        addChild( new MediumNode( transform, model.topMedium ) );
        addChild( new MediumNode( transform, model.bottomMedium ) );

        addChild( new PSwing( new VerticalLayoutPanel() {{
            add( new PropertyRadioButton<Boolean>( "On", model.laserOn, true ) {{
                setForeground( Color.white );
                setFont( new PhetFont( 18, true ) );
            }} );
            add( new PropertyRadioButton<Boolean>( "Off", model.laserOn, false ) {{
                setForeground( Color.white );
                setFont( new PhetFont( 18, true ) );
            }} );
            SwingUtils.setBackgroundDeep( this, Color.black );
        }} ) );

        for ( LightRay lightRay : model.getRays() ) {
            addLightRayNode.apply( lightRay );
        }
        model.addRayAddedListener( new VoidFunction1<LightRay>() {
            public void apply( final LightRay lightRay ) {
                addLightRayNode.apply( lightRay );
            }
        } );

        addChild( new LaserNode( transform, model.getLaser() ) );

        addChild( new ControlPanel( new VerticalLayoutPanel() {{
            add( new VerticalLayoutPanel() {{
                setBorder( BorderFactory.createTitledBorder( BorderFactory.createBevelBorder( BevelBorder.RAISED ), "Index of Refraction", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.TOP, new PhetFont( 18, true ) ) );
                add( new LinearValueControl( 1, 3, model.topMedium.getValue().getIndexOfRefraction(), "n1=", "0.00", "" ) {{
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            model.topMedium.setValue( new Medium( model.topMedium.getValue().getShape(), getValue(), indexOfRefractionToColor( getValue() ) ) );
                        }
                    } );
                }} );
                add( new LinearValueControl( 1, 3, model.bottomMedium.getValue().getIndexOfRefraction(), "n2=", "0.00", "" ) {{
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            model.bottomMedium.setValue( new Medium( model.bottomMedium.getValue().getShape(), getValue(), indexOfRefractionToColor( getValue() ) ) );
                        }
                    } );
                }} );
            }} );
        }} ) {{
            setOffset( LRRModel.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 0 );
        }} );
    }

    public static Color indexOfRefractionToColor( double value ) {
        Function.LinearFunction linearFunction = new Function.LinearFunction( 1, 3, 0, 1 );
        Color color = new Color( (float) linearFunction.evaluate( value ) / 2, (float) linearFunction.evaluate( value ) / 2, (float) linearFunction.evaluate( value ) );
        return color;
    }

    public static class ControlPanel extends PNode {
        public ControlPanel( JComponent controlPanel ) {
            final PSwing pswing = new PSwing( controlPanel );
            addChild( pswing );
//            addChild( new PhetPPath( new RoundRectangle2D.Double( 0, 0, pswing.getFullBounds().getWidth(), pswing.getFullBounds().getHeight(), 10, 10 ), new BasicStroke( 1 ), Color.blue ) );
        }
    }

    protected void addChild( PNode node ) {
        rootNode.addChild( node );
    }

    protected void removeChild( PNode node ) {
        rootNode.removeChild( node );
    }
}
