package edu.colorado.phet.eatingandexercise.control;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.eatingandexercise.EatingAndExerciseConstants;
import edu.colorado.phet.eatingandexercise.module.eatingandexercise.EatingAndExerciseCanvas;
import edu.colorado.phet.eatingandexercise.view.EatingAndExercisePText;
import edu.colorado.phet.eatingandexercise.view.StackedBarChartNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Created by: Sam
 * Aug 18, 2008 at 11:21:02 AM
 */
public class BarChartNodeAxisTitleLabelNode extends PNode {
    private EatingAndExerciseCanvas canvas;
    private StackedBarChartNode stackedBarChart;
    private PNode parent;
    private PPath backgroundCoverup;

    public BarChartNodeAxisTitleLabelNode( EatingAndExerciseCanvas canvas, StackedBarChartNode stackedBarChart, PNode parent ) {
        this.canvas = canvas;
        this.stackedBarChart = stackedBarChart;
        this.parent = parent;
        EatingAndExercisePText text = new EatingAndExercisePText( stackedBarChart.getTitle() );
        text.setFont( new PhetFont( 20, true ) );

        canvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateLayout();
                updateLayout();
            }
        } );

        parent.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                updateLayout();
                updateLayout();
            }
        } );

        updateLayout();
        updateLayout();

        backgroundCoverup = new PhetPPath( text.getFullBounds(), EatingAndExerciseConstants.BACKGROUND );
        addChild( backgroundCoverup );
        addChild( text );
    }

    private void updateLayout() {
        PNode axisNode = stackedBarChart.getAxisNode();
        Point2D center = axisNode.getGlobalFullBounds().getCenter2D();
        parent.globalToLocal( center );
        setOffset( center.getX() - getFullBounds().getWidth() / 2, 0 );
    }
}
