// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import static edu.colorado.phet.common.phetcommon.resources.PhetCommonResources.getMaximizeButtonImage;
import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsApplication.WATER_COLOR;
import static edu.colorado.phet.sugarandsaltsolutions.common.view.SugarAndSaltSolutionsCanvas.TITLE_FONT;

/**
 * Control bar that shows when the concentration bar chart is minimized and allows the user to maximized the concentration bar chart.
 * This class is necessary so that it will have the same width and layout metrics as the expanded bar chart.
 *
 * @author Sam Reid
 */
public class MinimizedConcentrationBarChart extends PNode {

    protected final int INSET = 5;

    public MinimizedConcentrationBarChart( final Property<Boolean> barChartVisible ) {
        final BufferedImage maximizeButtonImage = getMaximizeButtonImage();
        final double totalWidth = 220;

        //Show the title
        final PText title = new PText( SugarAndSaltSolutionsResources.CONCENTRATION ) {{
            setFont( TITLE_FONT );
        }};

        //Only show this bar chart if the user has opted to do so
        barChartVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean barChartVisible ) {
                setVisible( !barChartVisible );
            }
        } );

        final PNode background = new PhetPPath( new Rectangle2D.Double( 0, 0, totalWidth, Math.max( title.getFullBounds().getHeight(), maximizeButtonImage.getHeight() ) + INSET * 2 ),
                                                WATER_COLOR, new BasicStroke( 1f ), Color.BLACK );

        //Add a maximized button that shows the bar chart
        final PImage maximizeButton = new PImage( maximizeButtonImage ) {{
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    barChartVisible.set( true );
                }
            } );
        }};

        addChild( background );
        addChild( maximizeButton );
        addChild( title );

        title.setOffset( MinimizedConcentrationBarChart.this.getFullBounds().getCenterX() - title.getFullBounds().getWidth() / 2, INSET );
        maximizeButton.setOffset( background.getFullBounds().getWidth() - maximizeButton.getFullBounds().getWidth() - INSET, INSET );
    }
}