/**
 * Class: ThreeEnergyLevelPanel
 * Package: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 10:41:27 AM
 * To change this template use Options | File Templates.
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.model.LaserModel;

import java.awt.*;
import java.awt.geom.Line2D;

public class ThreeEnergyLevelMonitorPanel extends MonitorPanel {

    private int numGroundLevel;
    private int numHighLevel;
    private int numMiddleLevel;
    private Line2D.Float highLevelLine;
    private Line2D.Float middleLevelLine;
    private Line2D.Float groundLevelLine;
    private LaserModel model;

    /**
     *
     */
    public ThreeEnergyLevelMonitorPanel( LaserModel model ) {
        init();
        model.addObserver( this );
        this.model = model;
//        PhetApplication.instance().getPhysicalSystem().addObserver( this );
    }

    /**
     *
     */
    private void init() {

        highLevelLine = new Line2D.Float( s_highLevelLineOriginX,
                                          s_highLevelLineOriginY,
                                          s_highLevelLineOriginX + s_highLevelLineLength,
                                          s_highLevelLineOriginY );
        middleLevelLine = new Line2D.Float( s_middleLevelLineOriginX,
                                            s_middleLevelLineOriginY,
                                            s_middleLevelLineOriginX + s_middleLevelLineLength,
                                            s_middleLevelLineOriginY );
        groundLevelLine = new Line2D.Float( s_groundLevelLineOriginX,
                                            s_groundLevelLineOriginY,
                                            s_groundLevelLineOriginX + s_groundLevelLineLength,
                                            s_groundLevelLineOriginY );
        setPreferredSize( new Dimension( (int) s_panelWidth, (int) s_panelHeight ) );
    }

    /**
     *
     * @param graphics
     */
    protected synchronized void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );

        Graphics2D g2 = (Graphics2D) graphics;
        g2.draw( highLevelLine );
        g2.draw( middleLevelLine );
        g2.draw( groundLevelLine );

        // Draw ground level atoms
        g2.setColor( Color.gray );
        for( int i = 0; i < numGroundLevel; i++ ) {
            g2.fillArc( (int) ( s_groundLevelLineOriginX + ( s_atomDiam * i ) ),
                        (int) ( s_groundLevelLineOriginY - s_atomDiam ),
                        s_atomDiam,
                        s_atomDiam,
                        0, 360 );
        }

        // Draw middle level atoms
        g2.setColor( Color.red );
        for( int i = 0; i < numMiddleLevel; i++ ) {
            g2.fillArc( (int) ( s_middleLevelLineOriginX + ( s_atomDiam * i ) ),
                        (int) ( s_middleLevelLineOriginY - s_atomDiam ),
                        s_atomDiam,
                        s_atomDiam,
                        0, 360 );
        }

        // Draw high level atoms
        g2.setColor( Color.blue );
        for( int i = 0; i < numHighLevel; i++ ) {
            g2.fillArc( (int) ( s_highLevelLineOriginX + ( s_atomDiam * i ) ),
                        (int) ( s_highLevelLineOriginY - s_atomDiam ),
                        s_atomDiam,
                        s_atomDiam,
                        0, 360 );
        }
    }

    public void clear() {
        // NOP
    }

    public synchronized void update() {
//    public synchronized void update( Observable o, Object arg ) {
        LaserModel laserSystem = model;
//        LaserModel laserSystem = (LaserModel) o;
        numGroundLevel = laserSystem.getNumGroundStateAtoms();
        numMiddleLevel = laserSystem.getNumMiddleStateAtoms();
        numHighLevel = laserSystem.getNumHighStateAtoms();
        this.invalidate();
        this.repaint();
    }

    //
    // Static fields and methods
    //
    static private int s_atomDiam = 14;

    static private float s_panelWidth = 600;
    static private float s_panelHeight = 150;

    static private float s_highLevelLineOriginX = 50;
    static private float s_highLevelLineOriginY = 30;
    static private float s_highLevelLineLength = s_panelWidth * 0.4f;

    static private float s_middleLevelLineOriginX = s_highLevelLineOriginX + s_highLevelLineLength;
    static private float s_middleLevelLineOriginY = s_highLevelLineOriginY + ( s_panelHeight / 3 );
    static private float s_middleLevelLineLength = s_highLevelLineLength;

    static private float s_groundLevelLineOriginX = 10;
    static private float s_groundLevelLineOriginY = s_panelHeight - 20;
    static private float s_groundLevelLineLength = s_panelWidth - 40;

}
