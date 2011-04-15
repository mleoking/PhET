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
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.view.BodyNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createSinglePointScaleInvertedYMapping;
import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.CONTROL_FONT;
import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.FOREGROUND;
import static edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas.STAGE_SIZE;

/**
 * This control allows the user to view and change the mass of certain Body instances, which also changes the radius.
 *
 * @author Sam Reid
 */
public class BodyMassControl extends VerticalLayoutPanel {
    public static final int VIEW_MIN = 0;
    public static final int VIEW_MAX = 100000;//max value that the slider can take internally (i.e. the resolution of the slider)
    private static double SNAP_TOLERANCE = 0.08;//Percentage of the range the slider must be in to snap to a named label tick

    private boolean updatingSlider = false;

    public BodyMassControl( final Body body, double min, double max, final String minLabel, final String maxLabel,
                            final double labelValue, final String valueLabel ) {//for showing a label for a specific body such as "earth"
        final Function.LinearFunction modelToView = new Function.LinearFunction( min, max, VIEW_MIN, VIEW_MAX );
        setInsets( new Insets( 5, 5, 5, 5 ) );

        //Top component that shows the body's name and icon
        add( new JPanel() {{
            add( new JLabel( body.getName() ) {{
                setFont( CONTROL_FONT );
                setForeground( FOREGROUND );
            }} );
            //using a scale of 1 instead of 1E-9 fixes a problem that caused transparent pixels to appear around an image, making the rendered part smaller than it should have been
            //However, that caused out of memory errors when we needed to buffer the round gradient paint graphics, so we reverted back to the 1E-9 scaling
            final BodyNode bodyNode = new BodyNode( body, new Property<ModelViewTransform>( createSinglePointScaleInvertedYMapping( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.30, STAGE_SIZE.height * 0.5 ), 1E-9 ) ),
                                                    new Property<ImmutableVector2D>( new ImmutableVector2D( 0, 0 ) ), null, -Math.PI / 4 );
            final Image image = bodyNode.renderImage( 22 );
            add( new JLabel( new ImageIcon( image ), SwingConstants.LEFT ) );
        }} );
        setForeground( FOREGROUND );

        class SmallLabel extends JLabel {
            SmallLabel( String text ) {
                super( text );
                setForeground( FOREGROUND );
                setFont( new PhetFont( 14 ) );
            }
        }
        //Add the slider component.
        add( new JSlider( VIEW_MIN, VIEW_MAX ) {{
            setMajorTickSpacing( (int) ( modelToView.evaluate( labelValue ) - VIEW_MIN ) );
            setPaintLabels( true );
            setPaintTicks( true );
            setLabelTable( new Hashtable<Object, Object>() {{
                put( VIEW_MIN, new SmallLabel( minLabel ) );
                put( (int) modelToView.evaluate( labelValue ), new SmallLabel( valueLabel ) );//show the custom tick mark and label
                put( VIEW_MAX, new SmallLabel( maxLabel ) );
            }} );
            setForeground( FOREGROUND );
            body.getMassProperty().addObserver( new SimpleObserver() {
                public void update() {
                    updatingSlider = true;
                    setValue( (int) modelToView.evaluate( body.getMass() ) );
                    updatingSlider = false;
                }
            } );

            //TODO: Identify more clearly why this is problematic and come up with a better solution.
            // From the review: After reading this comment, I still don't understand why updatingSlider is necessary.  This is not a general issue with sliders. What is special about this slider than makes this necessary?
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
                                //TODO: why is this necessary? Is this a workaround? If so, I would be concerned that you have an undiscovered bug.
                                body.getMassProperty().notifyObservers();//For unknown reasons, without this call, updates won't be sent properly and the thumb won't snap to the tick
                            }
                        }
                    } );
                }
            } );
        }} );
    }
}
