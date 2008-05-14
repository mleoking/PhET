package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;


public interface IBoreholeProducer {

    public Borehole addBorehole( Point2D position );
    
    public void removeBorehole( Borehole borehole );
    
    public void removeAllBoreholes();
    
    public interface IBoreholeProducerListener {
        public void boreholeAdded( Borehole borehole );
        public void boreholeRemoved( Borehole borehole );
    }
    
    public void addBoreholeProducerListener( IBoreholeProducerListener listener );
    
    public void removeBoreholeProducerListener( IBoreholeProducerListener listener );
}
