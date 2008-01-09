package edu.colorado.phet.glaciers.model;

import java.awt.geom.Point2D;


public interface IToolProducer {
    
    public BoreholeDrill addBoreholeDrill( Point2D position );
    
    public GlacialBudgetMeter addGlacialBudgetMeter( Point2D position );
    
    public GPSReceiver createGPSReceiver( Point2D position );
    
    public IceThicknessTool addIceThicknessTool( Point2D position );
    
    public Thermometer addThermometer( Point2D position );
    
    public TracerFlag addTracerFlag( Point2D position );
    
    public void remove( AbstractTool tool );
    
    public static interface ToolProducerListener {
        public void toolAdded( AbstractTool tool );
        public void toolRemoved( AbstractTool tool );
    }
    
    public void addListener( ToolProducerListener listener );
    
    public void removeListener( ToolProducerListener listener );
}
