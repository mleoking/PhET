// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.rotation.graphs;

import edu.colorado.phet.common.motion.graphs.*;
import edu.colorado.phet.common.motion.model.TimeData;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationModel;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * Author: Sam Reid
 * Jul 20, 2007, 11:08:26 AM
 */
public class AngularUnitGraph extends RotationGraph {
    private AngleUnitModel angleUnitModel;
    private String unitsRad;
    private String unitsDeg;
    private double minRad;
    private double maxRad;

    public AngularUnitGraph( PhetPCanvas pSwingCanvas, ControlGraphSeries series, String label, String title, AngleUnitModel angleUnitModel, String unitsRad, String unitsDeg, double minRad, double maxRad, RotationModel motionModel, boolean editable, TimeSeriesModel timeSeriesModel, UpdateStrategy updateStrategy, double maxDomainValue, RotationPlatform iPositionDriven ) {
        super( pSwingCanvas, series, label, title, unitsRad, minRad, maxRad, editable, timeSeriesModel, updateStrategy, maxDomainValue, iPositionDriven );
        this.angleUnitModel = angleUnitModel;
        this.unitsRad = unitsRad;
        this.unitsDeg = unitsDeg;
        this.minRad = minRad;
        this.maxRad = maxRad;
        angleUnitModel.addListener( new AngleUnitModel.Listener() {
            public void changed() {
                updateUnits();
            }
        } );
        updateUnits();
    }

    public void addSeries( final ControlGraphSeries series ) {
        super.addSeries( series );
        series.setUnits( getUnitsString() );
    }

    public void resetRange() {
        setVerticalRange( getDisplayValue( getDefaultMinY() ), getDisplayValue( getDefaultMaxY() ) );
        setDomain( 0, getDefaultMaxX() );
    }

    private void updateUnits() {
        getVerticalAxis().setLabel( getTitle() + " (" + getUnitsString() + ")" );
        setVerticalRange( getDisplayValue( minRad ), getDisplayValue( maxRad ) );
        for ( int i = 0; i < getSeriesCount(); i++ ) {
            getControlGraphSeries( i ).setUnits( getUnitsString() );
        }
        getDynamicJFreeChartNode().clear();
        //add back all existing data in the right units
        for ( int i = 0; i < getSeriesCount(); i++ ) {
            ControlGraphSeries series = getControlGraphSeries( i );
            for ( int k = 0; k < series.getTemporalVariable().getSampleCount(); k++ ) {
                TimeData data = series.getTemporalVariable().getData( k );
                getDynamicJFreeChartNode().addValue( getSeriesIndex( series ), data.getTime(), getDisplayValue( data ) );
            }
        }
        updateSliderValue();

        forceUpdateAll();
    }

    protected GraphTimeControlNode createGraphTimeControlNode( TimeSeriesModel timeSeriesModel ) {
        return new AngularGraphTimeControlNode( timeSeriesModel );
    }

    class AngularGraphTimeControlNode extends GraphTimeControlNode {
        public AngularGraphTimeControlNode( TimeSeriesModel timeSeriesModel ) {
            super( timeSeriesModel );
        }

        protected GraphControlSeriesNode createGraphControlSeriesNode( ControlGraphSeries series ) {
            return new AngularGraphControlSeriesNode( series );
        }
    }

    class AngularGraphControlSeriesNode extends GraphControlSeriesNode {
        public AngularGraphControlSeriesNode( ControlGraphSeries series ) {
            super( series );
        }

        protected GraphControlTextBox createGraphControlTextBox( ControlGraphSeries series ) {
            return new AngularGraphControlTextBox( series );
        }
    }

    class AngularGraphControlTextBox extends GraphControlTextBox {
        public AngularGraphControlTextBox( ControlGraphSeries series ) {
            super( series );
            series.addListener( new ControlGraphSeries.Listener() {
                public void visibilityChanged() {
                }

                public void unitsChanged() {
                    update();
                }
            } );
        }

        protected double getDisplayValue() {
            return AngularUnitGraph.this.getDisplayValue( getSimVarValue() );
        }

        protected double getModelValue() {
            return viewToModel( super.getTextFieldValue() );
        }
    }

    protected ReadoutTitleNode createReadoutTitleNode( ControlGraphSeries series ) {
        return new AngularReadoutTitleNode( series );
    }

    class AngularReadoutTitleNode extends ReadoutTitleNode {
        public AngularReadoutTitleNode( ControlGraphSeries series ) {
            super( series );
        }

        protected double getValueToDisplay() {
            return getDisplayValue( super.getValueToDisplay() );
        }
    }

    private double getDisplayValue( double radians ) {
        return isRadians() ? radians : toDegrees( radians );
    }

    private double getDisplayValue( TimeData data ) {
        return getDisplayValue( data.getValue() );
    }

    //todo: use super.addValue
    protected void handleDataAdded( int seriesIndex, TimeData timeData ) {
        addValue( seriesIndex, timeData.getTime(), getDisplayValue( timeData ) );
    }

    private boolean isRadians() {
        return angleUnitModel == null || angleUnitModel.isRadians();
    }

    private double toDegrees( double rad ) {
        return rad * 360.0 / 2 / Math.PI;
    }

    private double toRadians( double degrees ) {
        return degrees / 360.0 * 2 * Math.PI;
    }

    private String getUnitsString() {
        return isRadians() ? unitsRad : unitsDeg;
    }

    protected void handleValueChanged() {
        getSimulationVariable().setValue( getModelValue() );
    }

    public double getModelValue() {
        return viewToModel( getSliderValue() );
    }

    private double viewToModel( double value ) {
        return isRadians() ? value : toRadians( value );
    }

    protected void updateSliderValue() {
        getJFreeChartSliderNode().setValue( getDisplayValue( getSimulationVariable().getValue() ) );
    }
}
