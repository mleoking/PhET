/**
 * Class: PhysicalPanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 9:11:05 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.TxApparatusPanel;
import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.NuclearModelElement;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class PhysicalPanel extends TxApparatusPanel {
    //public class PhysicalPanel extends ApparatusPanel {
    public static Color backgroundColor = new Color( 255, 255, 230 );

    protected boolean init = false;
    protected Point2D.Double origin;
    protected AffineTransform originTx = new AffineTransform();
    protected AffineTransform scaleTx = new AffineTransform();
    protected AffineTransform nucleonTx = new AffineTransform();
    private double nucleusLevel = Config.nucleusLevel;

    public PhysicalPanel( BaseModel model ) {
        super( model );
        this.setBackground( backgroundColor );
        setScale( 1 );

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                if( !init ) {
                    init = true;
                    origin = new Point2D.Double( getWidth() / 2, getHeight() / 2 );
                    originTx.setToTranslation( origin.getX(), origin.getY() );
                    nucleonTx.setToIdentity();
                    nucleonTx.concatenate( originTx );
                    nucleonTx.concatenate( scaleTx );
                }
            }
        } );
    }

    public double getScale() {
        return scaleTx.getScaleX();
    }

    public void setScale( double scale ) {
        scaleTx.setToScale( scale, scale );
    }

    int nucleusCnt = 0;

    public void addNucleus( final Nucleus nucleus ) {
        NucleusGraphic ng = NucleusGraphicFactory.create( nucleus );
        final TxGraphic txg = new TxGraphic( ng, nucleonTx );
        nucleusCnt++;
        NuclearModelElement.Listener listener = new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                PhysicalPanel.this.removeGraphic( txg );
            }
        };
        nucleus.addListener( listener );
        addGraphic( txg, nucleusLevel );
    }

    public synchronized void addGraphic( Graphic graphic ) {
        TxGraphic txg = new TxGraphic( graphic, nucleonTx );
        super.addGraphic( txg );
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );
        GraphicsUtil.setAlpha( (Graphics2D)graphics, 1 );
        super.paintComponent( g2 );
        gs.restoreGraphics();
    }

    public void addOriginCenteredGraphic( Graphic graphic, double level ) {
        TxGraphic txg = new TxGraphic( graphic, this.nucleonTx );
        addGraphic( txg, level );
    }

    public AffineTransform getNucleonTx() {
        return nucleonTx;
    }
}
