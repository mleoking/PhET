/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.view.AxisNode;
import edu.colorado.phet.molecularreactions.MRConfig;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.geom.Rectangle2D;
import java.awt.*;

public class CurvePane extends PPath {
    private final Color energyPaneBackgroundColor = MRConfig.ENERGY_PANE_BACKGROUND;
    private final Color curveColor = MRConfig.POTENTIAL_ENERGY_COLOR;
    private final Insets curveAreaInsets = new Insets( 20, 30, 40, 10 );
    private final Dimension curvePaneSize, curveAreaSize;

    private volatile EnergyProfileGraphic energyProfileGraphic;

    private volatile EnergyLine energyLine;
    private EnergyCursor energyCursor;
    private MRModel mrModel;
    private CurvePane.CurveCreatingModelListener curveCreatingModelListener;

    public CurvePane(final MRModule module, Dimension upperPaneSize) {
        super( new Rectangle2D.Double( 0,
              0,
              upperPaneSize.width - 1,
              (int)( MRConfig.ENERGY_VIEW_SIZE.getHeight() )
                - upperPaneSize.height
                - MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.height
        ));

        mrModel = module.getMRModel();

        curvePaneSize = new Dimension( upperPaneSize.width, (int)( MRConfig.ENERGY_VIEW_SIZE.getHeight() )
                                              - upperPaneSize.height
                                              - MRConfig.ENERGY_VIEW_REACTION_LEGEND_SIZE.height );

        curveAreaSize = new Dimension( (int)curvePaneSize.getWidth() - curveAreaInsets.left - curveAreaInsets.right,
                                       (int)curvePaneSize.getHeight() - curveAreaInsets.top - curveAreaInsets.bottom );



        PNode totalEnergyLineLayer = new PNode();
        totalEnergyLineLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );

        PNode curveLayer = new PNode();
        curveLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );
        curveLayer.setWidth(  curveAreaSize.getWidth() );
        curveLayer.setHeight( curveAreaSize.getHeight() );

        PNode cursorLayer = new PNode();
        cursorLayer.setOffset( curveAreaInsets.left, curveAreaInsets.top );

        // the -1 adjusts for a stroke width issue between this pane and the chart pane.

        setOffset( 0, upperPaneSize.getHeight() );
        setPaint( energyPaneBackgroundColor );
        setStrokePaint( new Color( 0, 0, 0, 0 ) );
        addChild( totalEnergyLineLayer );
        addChild( curveLayer );
        addChild( cursorLayer );

        // Create the line that shows total energy, and a legend for it
        energyLine = new EnergyLine( curveAreaSize, mrModel, module.getClock() );
        totalEnergyLineLayer.addChild( energyLine );

        // Create the curve, and add a listener to the model that will update the curve if the
        // model's energy profile changes
        createCurve( mrModel, curveLayer );

        curveCreatingModelListener = new CurveCreatingModelListener( mrModel, curveLayer );

        mrModel.addListener( curveCreatingModelListener );

        // Create the cursor
        energyCursor = new EnergyCursor( curveAreaSize.getHeight(), 0, curveAreaSize.getWidth(), mrModel );
        energyCursor.setVisible( false );

        cursorLayer.addChild( energyCursor );

        // Add axes
        RegisterablePNode xAxis = new RegisterablePNode( new AxisNode( SimStrings.get( "EnergyView.ReactionCoordinate" ),
                                                                       200,
                                                                       MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                                       AxisNode.HORIZONTAL,
                                                                       AxisNode.BOTTOM ) );
        xAxis.setRegistrationPoint( xAxis.getFullBounds().getWidth() / 2, 0 );
        xAxis.setOffset( this.getFullBounds().getWidth() / 2 + curveAreaInsets.left / 2,
                         this.getHeight() - 25 );
        addChild( xAxis );

        RegisterablePNode yAxis = new RegisterablePNode( new AxisNode( "Energy", 200,
                                                                       MRConfig.ENERGY_PANE_TEXT_COLOR,
                                                                       AxisNode.VERTICAL,
                                                                       AxisNode.TOP ) );
        yAxis.setRegistrationPoint( yAxis.getFullBounds().getWidth() / 2,
                                    -yAxis.getFullBounds().getHeight() / 2 );
        yAxis.setOffset( curveAreaInsets.left / 2, this.getFullBounds().getHeight() / 2 );
        
        addChild( yAxis );
    }

    public void terminate() {
        mrModel.removeListener( curveCreatingModelListener );
    }

    public void setVisible( boolean isVisible ) {
        super.setVisible( isVisible );
    }

    public Dimension getSize() {
        return curvePaneSize;
    }

    public Dimension getCurveAreaSize() {
        return curveAreaSize;
    }

    public Insets getCurveAreaInsets() {
        return curveAreaInsets;
    }

    public Color getCurveColor() {
        return curveColor;
    }

    public void setEnergyCursorOffset(double offset) {
        energyCursor.setOffset( offset, 0 );
    }

    private void createCurve( MRModel model, PNode curveLayer ) {
        if( energyProfileGraphic != null ) {
            try {
                curveLayer.removeChild( energyProfileGraphic );
            }
            catch (ArrayIndexOutOfBoundsException e) {

            }
        }
        energyProfileGraphic = new EnergyProfileGraphic( model.getEnergyProfile(),
                                                         curveAreaSize,
                                                         curveColor );
        curveLayer.addChild( energyProfileGraphic );
    }

    public void setProfileManipulable(boolean manipulable ) {
        energyProfileGraphic.setManipulable( manipulable ); 
    }

    public void setTotalEnergyLineVisible( boolean visible ) {
        energyLine.setVisible( visible );
    }

    public void setLegendVisible(boolean visible) {
        energyLine.setLegendVisible( visible );
        energyProfileGraphic.setVisible(visible);
    }

    public double getIntersectionWithHorizontal(double x) {
        return energyProfileGraphic.getIntersectionWithHorizontal( energyLine.getEnergyLineY(), x);
    }

    public void setManualControlEnabled( boolean manualControl ) {
        energyCursor.setManualControlEnabled( manualControl );
    }

    public void setEnergyCursorVisible( boolean b ) {
        energyCursor.setVisible( b );
    }

    private class CurveCreatingModelListener extends MRModel.ModelListenerAdapter {
        private final MRModel model;
        private final PNode curveLayer;

        public CurveCreatingModelListener( MRModel model, PNode curveLayer ) {
            this.model = model;
            this.curveLayer = curveLayer;
        }

        public void notifyEnergyProfileChanged( EnergyProfile profile ) {
            createCurve( model, curveLayer );
        }
    }
}
