/**
 * Class: ThreeEnergyLevelPanel
 * Package: edu.colorado.phet.lasers.view
 * User: Ron LeMaster
 * Date: Mar 27, 2003
 * Time: 10:41:27 AM
 * To change this template use Options | File Templates.
 * Latest Change:
 *      $Author$
 *      $Date$
 *      $Name$
 *      $Revision$
 */
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.lasers.model.LaserModel;
import edu.colorado.phet.lasers.model.photon.Photon;
import edu.colorado.phet.lasers.model.atom.Atom;
import edu.colorado.phet.lasers.model.atom.AtomicState;
import edu.colorado.phet.lasers.model.atom.GroundState;
import edu.colorado.phet.lasers.model.atom.MiddleEnergyState;

import java.awt.*;
import java.awt.geom.Line2D;

public class ThreeEnergyLevelMonitorPanel extends MonitorPanel {

    static private int s_atomDiam = 14;

    static private double s_panelWidth = 600;
    static private double s_panelHeight = 150;

    static private double s_highLevelLineOriginX = 50;
    static private double s_highLevelLineOriginY = 30;
    static private double s_highLevelLineLength = s_panelWidth * 0.4f;

    static private double s_middleLevelLineOriginX = s_highLevelLineOriginX + s_highLevelLineLength;
    static private double s_middleLevelLineOriginY = s_highLevelLineOriginY + ( s_panelHeight / 3 );
    static private double s_middleLevelLineLength = s_highLevelLineLength;

    static private double s_groundLevelLineOriginX = 10;
    static private double s_groundLevelLineOriginY = s_panelHeight - 20;
    static private double s_groundLevelLineLength = s_panelWidth - 40;

    private Line2D highLevelLine;
    private Line2D middleLevelLine;
    private Line2D groundLevelLine;
    private LaserModel model;

    /**
     *
     */
    public ThreeEnergyLevelMonitorPanel( LaserModel model ) {
        init();
        model.addObserver( this );
        this.model = model;
    }

    /**
     *
     */
    private void init() {

        highLevelLine = new Line2D.Double( s_highLevelLineOriginX,
                                          s_highLevelLineOriginY,
                                          s_highLevelLineOriginX + s_highLevelLineLength,
                                          s_highLevelLineOriginY );
        middleLevelLine = new Line2D.Double( s_middleLevelLineOriginX,
                                            s_middleLevelLineOriginY,
                                            s_middleLevelLineOriginX + s_middleLevelLineLength,
                                            s_middleLevelLineOriginY );
        groundLevelLine = new Line2D.Double( s_groundLevelLineOriginX,
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
        numGroundLevel = model.getNumGroundStateAtoms();
        numMiddleLevel = model.getNumMiddleStateAtoms();
        numHighLevel = model.getNumHighStateAtoms();
        this.invalidate();
        this.repaint();
    }

}
