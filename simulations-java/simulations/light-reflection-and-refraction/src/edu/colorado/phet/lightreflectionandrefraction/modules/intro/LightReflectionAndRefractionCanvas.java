// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.intro;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.util.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetTitledBorder;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.lightreflectionandrefraction.LightReflectionAndRefractionApplication;
import edu.colorado.phet.lightreflectionandrefraction.model.LRRModel;
import edu.colorado.phet.lightreflectionandrefraction.model.LightRay;
import edu.colorado.phet.lightreflectionandrefraction.view.LightRayNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class LightReflectionAndRefractionCanvas extends PhetPCanvas {
    private PNode rootNode;
    public final BooleanProperty showNormal = new BooleanProperty( true );
    public final BooleanProperty showProtractor = new BooleanProperty( false );

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
                setBorder( new PhetTitledBorder( "Index of Refraction" ) );
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
            add( new VerticalLayoutPanel() {{
                setBorder( new PhetTitledBorder( "View" ) );
                final Property<Boolean> ray = new Property<Boolean>( true );
                add( new PropertyRadioButton<Boolean>( "Ray", ray, true ) );
                add( new PropertyRadioButton<Boolean>( "Wave", ray, false ) );
            }} );
            add( new VerticalLayoutPanel() {{
                setBorder( new PhetTitledBorder( "Tools" ) );
                add( new PropertyCheckBox( "Show Normal", showNormal ) );
                add( new PropertyCheckBox( "Protractor", showProtractor ) );
                add( new PropertyCheckBox( "Intensity Meter", new BooleanProperty( false ) ) );
            }} );
        }} ) {{
            setOffset( LRRModel.STAGE_SIZE.getWidth() - getFullBounds().getWidth(), 0 );
        }} );

        //Normal Line
        double x = transform.modelToViewX( 0 );
        double y1 = transform.modelToViewY( 0 - model.getHeight() / 3 );
        double y2 = transform.modelToViewY( 0 + model.getHeight() / 3 );
        addChild( new PhetPPath( new Line2D.Double( x, y1, x, y2 ), new BasicStroke( 1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] { 10, 10 }, 0 ), Color.yellow ) {{
            showNormal.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showNormal.getValue() );
                }
            } );
        }} );

        //Protractor
        addChild( new PImage( BufferedImageUtils.multiScaleToHeight( LightReflectionAndRefractionApplication.RESOURCES.getImage( "protractor.png" ), 250 ) ) {{
            setOffset( transform.modelToViewX( 0 ) - getFullBounds().getWidth() / 2, transform.modelToViewY( 0 ) - getFullBounds().getHeight() / 2 );
            showProtractor.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( showProtractor.getValue() );
                }
            } );
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
