package edu.colorado.phet.fitness.control;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.motion.graphs.ControlGraph;
import edu.colorado.phet.common.motion.graphs.ControlGraphSeries;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.motion.graphs.MinimizableControlGraph;
import edu.colorado.phet.common.motion.model.DefaultTemporalVariable;
import edu.colorado.phet.common.motion.model.MotionTimeSeriesModel;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.barchart.BarChartNode;
import edu.colorado.phet.common.timeseries.model.TestTimeSeries;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.fitness.FitnessResources;
import edu.colorado.phet.fitness.model.Human;
import edu.colorado.phet.fitness.module.fitness.FitnessModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Created by: Sam
 * Apr 9, 2008 at 8:59:34 PM
 */
public class CaloriePanel extends PNode {
    private CalorieSlider calorieSlider;
    private CalorieSlider burnSlider;
    private PImage plateNode;

    public CaloriePanel( final FitnessModel model, PhetPCanvas phetPCanvas ) {
        plateNode = new PImage( FitnessResources.getImage( "plate.png" ) );
        plateNode.setOffset( 0, 70 );

        IconStrip.IconItem[] iconItem = new IconStrip.IconItem[model.getFoodItems().length];
        for ( int i = 0; i < iconItem.length; i++ ) {
            final int i1 = i;
            iconItem[i] = new IconStrip.IconItem( model.getFoodItems()[i].getImage() ) {
                //add any additional code required to decorate a created PNode here
                FoodItem foodItem;

                public void decorateNode( final PNode node ) {
                    super.decorateNode( node );
                    node.addInputEventListener( new PDragEventHandler() {
                        protected void startDrag( PInputEvent event ) {
                            if ( foodItem == null ) {
                                foodItem = model.getFoodItems()[i1].copy();
                                System.out.println( "created: "+foodItem );
                            }
                        }
                    } );
//                    node.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, new PropertyChangeListener() {
//                        public void propertyChange( PropertyChangeEvent evt ) {
//                            if ( foodItem != null && node.getFullBounds().intersects( plateNode.getFullBounds() ) ) {
//                                model.getHuman().addFoodItem( foodItem );
//                            }
//                            else {
//                                model.getHuman().removeFoodItem( foodItem );
//                            }
//                        }
//                    } );
                }
            };
        }
        PNode foodStrip = new IconStrip( iconItem );
        calorieSlider = new CalorieSlider( "Daily Caloric Intake", Color.green, true );
//        model.getHuman().addListener( new Human.Adapter() {
//            public void foodItemsChanged() {
//                calorieSlider.setArrowLocation( model.getHuman().getDailyCaloricIntake() / 20 );//todo: fix slider scaling
//            }
//        } );
        addChild( calorieSlider );

        burnSlider = new CalorieSlider( "Daily Caloric Burn", Color.red, false );
        burnSlider.addRegion( 0, 50, new Color( 225, 138, 138 ) );
        addChild( burnSlider );


        addChild( plateNode );

        addChild( foodStrip );
        calorieSlider.setOffset( 0, 175 );
        burnSlider.setOffset( 0, calorieSlider.getFullBounds().getMaxY() + 5 );

        BarChartNode foodCompositionBarChart = new BarChartNode( "Food Composition", 200, Color.white, 200 );
        foodCompositionBarChart.setVerticalAxisLabel( "amount (grams)", Color.blue );
        foodCompositionBarChart.setVerticalAxisLabelShadowVisible( false );
        foodCompositionBarChart.init( new BarChartNode.Variable[]{
                new BarChartNode.Variable( "Carbs", 0.7, Color.green ),
                new BarChartNode.Variable( "Proteins", 0.3, Color.red ),
                new BarChartNode.Variable( "Lipids", 0.5, Color.blue ),
        } );
        foodCompositionBarChart.setOffset( calorieSlider.getFullBounds().getMaxX() + 30, foodStrip.getFullBounds().getMaxY() + 5 );
        addChild( foodCompositionBarChart );


        BarChartNode exerciseChart = new BarChartNode( "Exercise", 200 / 24, Color.white, 200 );//hours
        exerciseChart.setVerticalAxisLabel( "activity (hours)", Color.blue );
        exerciseChart.setVerticalAxisLabelShadowVisible( false );
        exerciseChart.init( new BarChartNode.Variable[]{
                new BarChartNode.Variable( "Activity", 10, Color.red ),
                new BarChartNode.Variable( "Relaxing", 12, Color.blue ),
                new BarChartNode.Variable( "Sleep", 8, Color.green ),
        } );
//        exerciseChart.setOffset( foodCompositionBarChart.getFullBounds().getX(), burnSlider.getFullBounds().getY() );
        exerciseChart.setOffset( foodCompositionBarChart.getFullBounds().getMaxX() + 50, foodCompositionBarChart.getFullBounds().getY() );
        addChild( exerciseChart );

        PhetPPath activityAcceptor = new PhetPPath( new Rectangle2D.Double( 0, 0, 50, 50 ), new BasicStroke( 2 ), Color.black );
        PText activityText = new PText( "Activity" );
        activityText.setOffset( 3, 1 );
        PNode activityAcceptorNode = new PNode();
        activityAcceptorNode.addChild( activityAcceptor );
        activityAcceptorNode.addChild( activityText );
        activityAcceptorNode.setOffset( burnSlider.getFullBounds().getCenterX() - activityAcceptorNode.getFullBounds().getWidth() / 2, burnSlider.getFullBounds().getMaxY() + 5 );
        addChild( activityAcceptorNode );

        PNode exerciseStrip = new IconStrip( new IconStrip.IconItem[]{
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
                new IconStrip.IconItem( "bike.png" ),
                new IconStrip.IconItem( "swim.png" ),
        } );
        exerciseStrip.setOffset( 0, activityAcceptorNode.getFullBounds().getMaxY() + 10 );
        addChild( exerciseStrip );

        GraphSuiteSet graphSuiteSet = new GraphSuiteSet();
        TimeSeriesModel tsm = new MotionTimeSeriesModel( new TestTimeSeries.MyRecordableModel(), new ConstantDtClock( 30, 1 ) );
        ControlGraph controlGraph = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Weight", Color.blue, "weight", "lbs", "human", new DefaultTemporalVariable() ), "Weight", 0, 100, tsm );
        controlGraph.setEditable( false );
        MinimizableControlGraph a = new MinimizableControlGraph( "Weight", controlGraph );
        ControlGraph controlGraph1 = new ControlGraph( phetPCanvas, new ControlGraphSeries( "Calories", Color.blue, "Cal", "Cal", "human", new DefaultTemporalVariable() ), "Calories", 0, 100, tsm );
        controlGraph1.setEditable( false );
        MinimizableControlGraph b = new MinimizableControlGraph( "Calories", controlGraph1 );
        a.setAvailableBounds( 600, 125 );
        b.setAvailableBounds( 600, 125 );
        MinimizableControlGraph[] graphs = {a, b};
        a.setAlignedLayout( graphs );
        b.setAlignedLayout( graphs );
        graphSuiteSet.addGraphSuite( graphs );
        a.setOffset( 0, exerciseStrip.getFullBounds().getMaxY() );
        b.setOffset( 0, a.getFullBounds().getMaxY() );
        addChild( a );

        addChild( b );
    }

    static class BoxedImage extends PImage {
        private boolean drawBorder = true;

        public BoxedImage( BufferedImage image ) {
            super( image );
        }

        public boolean isDrawBorder() {
            return drawBorder;
        }

        public void setDrawBorder( boolean drawBorder ) {
            this.drawBorder = drawBorder;
        }

        protected void paint( PPaintContext paintContext ) {
            if ( drawBorder ) {
                Graphics2D g2 = paintContext.getGraphics();
                g2.setPaint( Color.black );
                g2.setStroke( new BasicStroke( 1 ) );
                g2.draw( getBoundsReference() );
            }
            super.paint( paintContext );
        }
    }

}
