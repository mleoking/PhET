/*  */
package edu.colorado.phet.balloons;

import edu.colorado.phet.balloons.common.paint.Painter;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: May 19, 2005
 * Time: 9:49:45 AM
 */

public class BalloonHelpPainter implements Painter {
    private BalloonsSimulationPanel balloonsSimulationPanel;

    public BalloonHelpPainter( BalloonsSimulationPanel balloonsSimulationPanel ) {
        this.balloonsSimulationPanel = balloonsSimulationPanel;
    }

    public void paint( Graphics2D g ) {
        g.setFont( new PhetFont( Font.BOLD, 14 ) );
        g.setColor( Color.blue );
        String rubTheBalloon = BalloonsResources.getString( "rub.the.balloon" );

        double height = g.getFont().getStringBounds( rubTheBalloon, g.getFontRenderContext() ).getHeight();

        g.drawString( rubTheBalloon, balloonsSimulationPanel.getSweaterMaxX(), 50 );
        g.drawString( BalloonsResources.getString( "on.the.sweater" ), balloonsSimulationPanel.getSweaterMaxX(), (int)( 50 + height ) );

        String bringNear = BalloonsResources.getString( "bring.the.balloon" );
        double w = g.getFont().getStringBounds( bringNear, g.getFontRenderContext() ).getWidth();
        int x = (int)( balloonsSimulationPanel.getWallX() - w - 10 );
        int y = (int)( balloonsSimulationPanel.getWallHeight() - height * 5 );
        g.drawString( bringNear, x, y );
        g.drawString( BalloonsResources.getString( "near.the.wall" ), x, (int)( y + height ) );
    }
}
