/* Copyright 2002-2011, University of Colorado */

package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyCheckBox;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.common.phetcommon.view.util.SwingUtils.setBackgroundDeep;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.CONCENTRATION;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.Strings.SHOW_VALUES;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.CONTROL_FONT;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.TITLE_FONT;

/**
 * Optional bar chart that shows bar charts for concentrations in macro and micro tab
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class ConcentrationBarChart extends PNode {

    protected final double abscissaY;
    protected final PNode background;

    public ConcentrationBarChart( final SettableProperty<Boolean> showValues, final SettableProperty<Boolean> visible, double scaleFactor ) {
        final int INSET = 5;

        background = new PhetPPath( new Rectangle2D.Double( 0, 0, 220, 234 ), WATER_COLOR, new BasicStroke( 1f ), Color.BLACK );
        addChild( background );

        abscissaY = background.getFullBounds().getHeight() - 60;
        addChild( new PhetPPath( new Line2D.Double( INSET, abscissaY, background.getFullBounds().getWidth() - INSET, abscissaY ), new BasicStroke( 2 ), Color.black ) );

        //Show the title
        addChild( new PText( CONCENTRATION ) {{
            setFont( TITLE_FONT );
            setOffset( ConcentrationBarChart.this.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, INSET );
        }} );

        //Add a minimize button that hides the bar chart (replaced with a "+" button which can be used to restore it
        addChild( new PImage( PhetCommonResources.getMinimizeButtonImage() ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    visible.set( false );
                }
            } );
            setOffset( background.getFullBounds().getWidth() - getFullBounds().getWidth() - INSET, INSET );
        }} );

        //Only show this bar chart if the user has opted to do so
        visible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean visible ) {
                setVisible( visible );
            }
        } );

        //Add a checkbox that lets the user toggle on and off whether actual values are shown
        //This is in a VBox in case we need to add other controls later
        addChild( new VBox() {{
            addChild( new PSwing( new PropertyCheckBox( SHOW_VALUES, showValues ) {{
                setFont( CONTROL_FONT );
                setBackgroundDeep( this, WATER_COLOR );
            }} ) );
            setOffset( background.getFullBounds().getWidth() / 2 - getFullBoundsReference().width / 2, background.getHeight() - getFullBounds().getHeight() - INSET );
        }} );
    }
}