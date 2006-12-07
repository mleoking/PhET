/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view.computedimage;

import edu.colorado.phet.mri.model.SampleScannerB;
import edu.colorado.phet.mri.view.DetectorPaint;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * ComputedImage
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ComputedImagePNode extends PNode implements SampleScannerB.ChangeListener {
//public class ComputedImagePNode extends PNode implements SampleScanner.ChangeListener {
    private Rectangle2D sampleBounds;
    private double linearQuantization;
    private Rectangle2D displaySlice = new Rectangle2D.Double();

    public ComputedImagePNode( Rectangle2D sampleBounds, SampleScannerB sampleScanner ) {
        this.sampleBounds = sampleBounds;
        this.linearQuantization = sampleScanner.getStepSize();
        sampleScanner.addChangeListener( this );
    }

    public void addSpot( Point2D location, double density ) {
        addChild( new Spot( location, density, linearQuantization ) );
    }


    class Spot extends PPath {
        public Spot( Point2D location, double density, double linearQuantization ) {
            Rectangle2D bounds = new Rectangle2D.Double( location.getX() - linearQuantization / 2,
                                                         location.getY() - linearQuantization / 2,
                                                         linearQuantization,
                                                         linearQuantization );
            setPathTo( bounds );
            setPaint( new DetectorPaint( new Point2D.Double( bounds.getCenterX(), bounds.getCenterY() ),
                                         bounds.getWidth(), density, Color.white ) );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of SampleScanner.ChangeListener
    //--------------------------------------------------------------------------------------------------

    public void stateChanged( SampleScannerB.ChangeEvent event ) {
        SampleScannerB scanner = event.getSampleScanner();
//        Point2D p = new Point2D.Double( scanner.getPosition().getX() - sampleBounds.getX(),
//                                        scanner.getPosition().getY() - sampleBounds.getY() );
//        addSpot( p, Math.min( detector.getNumDetected() / 10, 1 ));
        displaySlice.setRect( scanner.getPosition().getX() - sampleBounds.getX(),
                              scanner.getPosition().getY() - sampleBounds.getY(),
                              scanner.getCurrentScanArea().getWidth(),
                              scanner.getCurrentScanArea().getHeight() );
        PPath scanArea = new PPath( displaySlice );
        double density = Math.max( Math.min( (double)scanner.getSignal() / 100, 1 ), 0 );
        int grayLevel = (int)( density * 255 );
        Color color = new Color( 0, 0, 0, grayLevel );
        scanArea.setPaint( color );
        addChild( scanArea );
    }

}
