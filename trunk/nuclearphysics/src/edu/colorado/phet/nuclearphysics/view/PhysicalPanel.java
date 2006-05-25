/**
 * Class: PhysicalPanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 9:11:05 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.coreadditions.TxApparatusPanel;
import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.NuclearModelElement;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Rubidium;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public class PhysicalPanel extends TxApparatusPanel {
    //public class PhysicalPanel extends ApparatusPanel {
    public static Color backgroundColor = new Color( 255, 255, 230 );

    private boolean init = false;
    private Point2D.Double origin;
    private AffineTransform originTx = new AffineTransform();
    private AffineTransform scaleTx = new AffineTransform();
    private AffineTransform nucleonTx = new AffineTransform();
    private double nucleusLevel = Config.nucleusLevel;


    /**
     * Constructor
     * @param clock
     */
    public PhysicalPanel( IClock clock ) {
        super( clock );
        this.setBackground( backgroundColor );
        setPhysicalScale( 1 );

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

    public void setPhysicalScale( double scale ) {
        scaleTx.setToScale( scale, scale );

        nucleonTx.setToIdentity();
        nucleonTx.concatenate( originTx );
        nucleonTx.concatenate( scaleTx );
    }

    public void addNucleus( final Nucleus nucleus ) {
        NucleusGraphic ng = NucleusGraphicFactory.create( nucleus );
        final TxGraphic txg = new TxGraphic( ng, nucleonTx );
        nucleus.addListener( new GraphicRemover( txg ));
        super.addGraphic( txg, nucleusLevel );
    }

    public void addGraphic( PhetGraphic graphic ) {
        TxGraphic txg = new TxGraphic( graphic, nucleonTx );
        super.addGraphic( txg );
    }

    /**
     * todo: Is this needed?
     * @param graphics
     */
//    protected void paintComponent( Graphics graphics ) {
//        Graphics2D g2 = (Graphics2D)graphics;
//        GraphicsState gs = new GraphicsState( g2 );
//        GraphicsUtil.setAlpha( (Graphics2D)graphics, 1 );
//        super.paintComponent( g2 );
//        gs.restoreGraphics();
//    }

    public void addOriginCenteredGraphic( PhetGraphic graphic, double level ) {
        TxGraphic txg = new TxGraphic( graphic, this.nucleonTx );
        addGraphic( txg, level );
    }

    public AffineTransform getNucleonTx() {
        return nucleonTx;
    }

    public AffineTransform getGraphicTx() {
        return super.getGraphicTx();
    }

    protected Point2D.Double getOrigin() {
        return origin;
    }

    protected AffineTransform getOriginTx() {
        return originTx;
    }

    //----------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------
    public class GraphicRemover implements NuclearModelElement.Listener {
        private PhetGraphic graphic;

        public GraphicRemover( PhetGraphic graphic ) {
            this.graphic = graphic;
        }

        public void leavingSystem( NuclearModelElement nme ) {
            removeGraphic( graphic );
        }
    }
}