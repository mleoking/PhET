package edu.colorado.phet.eatingandexercise.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ColorChooserFactory;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseStrings;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PClip;

/**
 * Created by: Sam
 * Aug 18, 2008 at 10:09:21 AM
 */
public class BarChartElementNode extends PNode {
    private BarChartElement barChartElement;
    private PClip clip;
    private PhetPPath barNode;
    private PhetPPath barThumb;
    private StackedBarNode.Thumb thumbLocation;

    private PNode imageNode;
    private HTMLNode textNode;
    private HTMLNode valueNode;
    private StackedBarNode stackedBarNode;

    public BarChartElementNode( final StackedBarNode stackedBarNode, final BarChartElement barChartElement, StackedBarNode.Thumb thumbLocation ) {
        this.stackedBarNode = stackedBarNode;
        this.thumbLocation = thumbLocation;
        this.barChartElement = barChartElement;
        barNode = new PhetPPath( createShape(), barChartElement.getPaint() );
        addChild( barNode );

        if ( stackedBarNode.showColorChooser ) {
            showColorChooser();
        }

        barChartElement.addListener( new BarChartElement.Listener() {
            public void valueChanged() {
                updateShape();
            }

            public void paintChanged() {
                barNode.setPaint( barChartElement.getPaint() );
                System.out.println( barChartElement.getName() + " = " + barChartElement.getPaint() );
            }
        } );
        clip = new PClip();

        if ( barChartElement.getImage() != null ) {
            imageNode = new PImage( BufferedImageUtils.multiScaleToWidth( barChartElement.getImage(), 35 ) );
        }
        else {
            imageNode = new PNode();
        }

        PhetFont font = new PhetFont( 14, true );
        textNode = new HTMLNode( barChartElement.getName(), font, barChartElement.getTextColor() );
        valueNode = new HTMLNode( "", font, barChartElement.getTextColor() );
        clip.addChild( imageNode );
        clip.addChild( textNode );
        clip.addChild( valueNode );

        addChild( clip );

        //todo: delegate to subclass
        barThumb = new PhetPPath( thumbLocation.getThumbShape( stackedBarNode.getBarWidth() ), barChartElement.getPaint(), new BasicStroke( 1 ), Color.black );
        addChild( barThumb );
        barThumb.addInputEventListener( new CursorHandler() );
        barThumb.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                double modelDX = stackedBarNode.viewToModelDelta( event.getCanvasDelta().getHeight() );
                barChartElement.setValue( Math.max( 0, barChartElement.getValue() - modelDX ) );
            }
        } );
        updateShape();
    }

    private void showColorChooser() {
        barNode.addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                ColorChooserFactory.showDialog( "Color Picker", null, (Color) barChartElement.getPaint(), new ColorChooserFactory.Listener() {
                    public void colorChanged( Color color ) {
                        barChartElement.setPaint( color );
                    }

                    public void ok( Color color ) {
                    }

                    public void cancelled( Color originalColor ) {
                    }
                }, true );
            }
        } );
    }

    public void updateShape() {
        double value = barChartElement.getValue();
        valueNode.setHTML( EatingAndExerciseStrings.KCAL_PER_DAY_FORMAT.format( value ) );
        barNode.setPathTo( createShape() );
        clip.setPathTo( createShape() );
        double availHeight = clip.getFullBounds().getHeight();
        textNode.setOffset( 0, 0 );
        imageNode.setOffset( clip.getFullBounds().getWidth() / 2 - imageNode.getFullBounds().getWidth() / 2, 0 );

        textNode.setOffset( clip.getFullBounds().getWidth() / 2 - textNode.getFullBounds().getWidth() / 2, imageNode.getFullBounds().getHeight() - 3 );
        valueNode.setOffset( clip.getFullBounds().getWidth() / 2 - valueNode.getFullBounds().getWidth() / 2 + 2, textNode.getFullBounds().getMaxY() - 2 );

        valueNode.setScale( 1.0 );
        imageNode.setVisible( true );
        imageNode.setOffset( 1, 0 );

        double centerX = ( clip.getFullBounds().getMaxX() + imageNode.getFullBounds().getMaxX() ) / 2 - textNode.getFullBounds().getWidth() / 2;

        textNode.setOffset( centerX, 0 );
        valueNode.setOffset( textNode.getFullBounds().getCenterX() - valueNode.getFullBounds().getWidth() / 2, textNode.getFullBounds().getMaxY() );

        double imageY = ( textNode.getFullBounds().getY() + valueNode.getFullBounds().getMaxY() ) / 2 - imageNode.getFullBounds().getHeight() / 2;
        imageNode.setOffset( 1, imageY );

        if ( valueNode.getFullBounds().getMaxY() > availHeight ) {
            imageNode.setVisible( false );
            textNode.setOffset( 2, 0 );
            valueNode.setOffset( clip.getFullBounds().getMaxX() - valueNode.getFullBounds().getWidth() - 2, 0 );
        }

        barThumb.setPathTo( thumbLocation.getThumbShape( stackedBarNode.getBarWidth() ) );
    }

    private Rectangle2D.Double createShape() {
        return new Rectangle2D.Double( 0, 0, stackedBarNode.getBarWidth(), stackedBarNode.modelToView( barChartElement.getValue() ) );
    }

    public BarChartElement getBarChartElement() {
        return barChartElement;
    }
}
