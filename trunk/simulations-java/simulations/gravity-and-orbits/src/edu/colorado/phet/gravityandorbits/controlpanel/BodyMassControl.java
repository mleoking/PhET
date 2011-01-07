// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.view.BodyNode;
import edu.colorado.phet.gravityandorbits.view.Scale;

import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.*;
import static edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas.STAGE_SIZE;

/**
 * This control allows the user to view and change the mass of certain Body instances.
 *
 * @author Sam Reid
 */
public class BodyMassControl extends VerticalLayoutPanel {
    public static final int MIN = 0;
    public static final int MAX = 100000;

    private boolean updatingSlider = false;
    private double SNAP_TOLERANCE = 0.08;//Percentage of the range the slider must be in to snap to a named label tick

    public BodyMassControl( final Body body, double min, double max, final String minLabel, final String maxLabel,
                            final double labelValue, final String valueLabel ) {//for showing a label for a specific body such as "earth"
        final Function.LinearFunction modelToView = new Function.LinearFunction( min, max, MIN, MAX );
        setInsets( new Insets( 5, 5, 5, 5 ) );

        //Top component that shows the body's name and icon
        //TODO: move title label west, probably by not having the horizontal panel expand to take full width
        add( new JPanel() {{
            setBackground( BACKGROUND );
            add( new JLabel( body.getName() ) {{
                setFont( CONTROL_FONT );
                setForeground( FOREGROUND );
                setBackground( BACKGROUND );
            }} );
            final BodyNode bodyNode = new BodyNode( body, new Property<ModelViewTransform>( ModelViewTransform.createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.30, STAGE_SIZE.height * 0.5 ),
                                                                                                                                                       1//using a scale of 1 instead of 1E-9 fixes a problem that caused transparent pixels to appear around an image, making the rendered part smaller than it should have been
            ) ),
                                                    new Property<Scale>( Scale.REAL ), new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) ), null, -Math.PI / 4, null );
            final Image image = bodyNode.renderImage( 22 );
            add( new JLabel( "", new ImageIcon( image ), SwingConstants.LEFT ) {{
                setBackground( BACKGROUND );
            }} );
        }} );

        setForeground( FOREGROUND );
        setBackground( BACKGROUND );

        //Add the slider component.
        add( new JSlider( MIN, MAX ) {{
            setMajorTickSpacing( (int) ( modelToView.evaluate( labelValue ) - MIN ) );
            setPaintLabels( true );
            setPaintTicks( true );
            setLabelTable( new Hashtable() {{
                put( MIN, new JLabel( minLabel ) {{
                    setBackground( BACKGROUND );
                    setForeground( FOREGROUND );
                    setFont( new PhetFont( 14, false ) );
                }} );
                //show the custom tick mark and label
                put( (int) modelToView.evaluate( labelValue ), new JLabel( valueLabel ) {{
                    setBackground( BACKGROUND );
                    setForeground( FOREGROUND );
                    setFont( new PhetFont( 14, false ) );
                }} );
                put( MAX, new JLabel( maxLabel ) {{
                    setBackground( BACKGROUND );
                    setForeground( FOREGROUND );
                    setFont( new PhetFont( 14, false ) );
                }} );
            }} );
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );
            body.getMassProperty().addObserver( new SimpleObserver() {
                public void update() {
                    updatingSlider = true;
                    setValue( (int) modelToView.evaluate( body.getMass() ) );
                    updatingSlider = false;
                }
            } );

            // we don't want to set the body mass if we are updating the slider. otherwise we get a
            // mass change => update slider => mass change bounce and the wrong values are stored for a reset
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    if ( !updatingSlider ) {
                        double sliderValue = modelToView.createInverse().evaluate( getValue() );
                        if ( Math.abs( sliderValue - labelValue ) / labelValue < SNAP_TOLERANCE ) {//if near to tick mark, then use that value
                            body.setMass( labelValue );
                        }
                        else {
                            body.setMass( modelToView.createInverse().evaluate( getValue() ) );
                        }
                    }
                }
            } );
            //if mouse is released near to tick mark, then use that value
            addMouseListener( new MouseAdapter() {
                @Override
                public void mouseReleased( MouseEvent e ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            double sliderValue = modelToView.createInverse().evaluate( getValue() );
                            if ( Math.abs( sliderValue - labelValue ) / labelValue < SNAP_TOLERANCE ) {
                                body.setMass( labelValue );
                                body.getMassProperty().notifyObservers();//Without this call, updates won't be sent properly and the thumb won't snap to the tick
                            }
                        }
                    } );
                }
            } );
        }} );
    }
}
