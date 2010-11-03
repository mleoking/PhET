package edu.colorado.phet.gravityandorbits.controlpanel;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.view.BodyNode;

import static edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel.*;
import static edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas.STAGE_SIZE;

/**
 * @author Sam Reid
 */
public class BodyMassControl extends VerticalLayoutPanel {
    public static final int MIN = 0;
    public static final int MAX = 100000;

    public BodyMassControl( final Body body, double min, double max, final String minLabel, final String maxLabel ) {
        final Function.LinearFunction modelToView = new Function.LinearFunction( min, max, MIN, MAX );
        setInsets( new Insets( 5, 5, 5, 5 ) );

        //TODO: move title label west, probably by not having the horizontal panel expand to take full width
        add( new JPanel() {{
            setBackground( BACKGROUND );
            add( new JLabel( body.getName() ) {{
                setFont( CONTROL_FONT );
                setForeground( FOREGROUND );
                setBackground( BACKGROUND );
            }} );
            final BodyNode bodyNode = new BodyNode( body, new ModelViewTransform2D( new Point2D.Double( 0, 0 ), new Point2D.Double( STAGE_SIZE.width * 0.30, STAGE_SIZE.height * 0.5 ), 1.5E-9, true ), new Property<Boolean>( false ) );
            add( new JLabel( "", new ImageIcon( bodyNode.toImage() ), SwingConstants.LEFT ) {{
                setBackground( BACKGROUND );
            }} );
        }} );

        setForeground( FOREGROUND );
        setBackground( BACKGROUND );

        add( new JSlider( MIN, MAX ) {{
            setMinorTickSpacing( ( MAX - MIN ) / 10 );
            setMajorTickSpacing( ( MAX - MIN ) / 2 );
            setPaintLabels( true );
            setPaintTicks( true );
            setLabelTable( new Hashtable() {{
                put( MIN, new JLabel( minLabel ) {{
                    setBackground( BACKGROUND );
                    setForeground( FOREGROUND );
                    setFont( new PhetFont( 14, true ) );
                }} );
                put( MAX, new JLabel( maxLabel ) {{
                    setBackground( BACKGROUND );
                    setForeground( FOREGROUND );
                    setFont( new PhetFont( 14, true ) );
                }} );
            }} );
            setBackground( BACKGROUND );
            setForeground( FOREGROUND );
            body.getDiameterProperty().addObserver( new SimpleObserver() {
                public void update() {
                    setValue( (int) modelToView.evaluate( body.getMass() ) );//todo: will this clamp create problems?
                }
            } );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    body.setMass( modelToView.createInverse().evaluate( getValue() ) );
                }
            } );
        }} );
    }
}
