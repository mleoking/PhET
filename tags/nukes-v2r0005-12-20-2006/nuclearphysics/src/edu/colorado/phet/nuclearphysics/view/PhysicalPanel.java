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
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.coreadditions.TxApparatusPanel;
import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.*;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.EventObject;
import java.util.EventListener;

/**
 * A panel thst shows the physical representation of what's going on.
 * <p/>
 * When a Nucleus instance is added to this panel, the panel calls a factory object to make a
 * graphic for the nucleus.
 * <p/>
 * Every time a nucleus is added to this panel, the panel binds a listener to the nucleus that
 * will remove it's associated graphic when the nucleus fires a leavingSystem() message.
 */
public class PhysicalPanel extends TxApparatusPanel {
    public static Color backgroundColor = new Color( 255, 255, 230 );

    private boolean init = false;
    private Point2D.Double origin = new Point2D.Double();
    private AffineTransform originTx = new AffineTransform();
    private AffineTransform scaleTx = new AffineTransform();
    private AffineTransform nucleonTx = new AffineTransform();
    private double nucleusLevel = Config.nucleusLevel;


    /**
     * Constructor
     *
     * @param clock
     */
    public PhysicalPanel( IClock clock, NuclearPhysicsModel model ) {
        super( clock );
        this.setBackground( backgroundColor );
        setPhysicalScale( 1 );

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                if( !init ) {
                    init = true;
                    setOrigin( new Point2D.Double( origin.getX() + getWidth() / 2, origin.getY() + getHeight() / 2 ) );
                }
            }

            public void componentShown( ComponentEvent e ) {
                if( !init ) {
                    init = true;
                    setOrigin( new Point2D.Double( origin.getX() + getWidth() / 2, origin.getY() + getHeight() / 2 ) );
                }
            }
        } );

        // Add a listener that will put graphics on the panel for nuclei, and create
        // listeners to remove the graphics if the nuclei leave the model
        model.addNucleusListener( new NuclearPhysicsModel.NucleusListener() {
            public void nucleusAdded( NuclearPhysicsModel.ChangeEvent event ) {
                Nucleus nucleus = event.getNucleus();
                NucleusGraphic ng = new NucleusGraphicFactory().create( nucleus );
                final TxGraphic txg = new TxGraphic( ng, nucleonTx );
                nucleus.addListener( new GraphicRemover( txg, nucleus ) );
                PhysicalPanel.super.addGraphic( txg, nucleusLevel );
                graphicListenerProxy.graphicAdded( new GraphicEvent( txg ) );
            }

            public void nucleusRemoved( NuclearPhysicsModel.ChangeEvent event ) {
                //noop
            }
        } );
    }

    public void foo() {
        setOrigin( new Point2D.Double( origin.getX() + getWidth() / 2, origin.getY() + getHeight() / 2 ) );
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

    public void addGraphic( PhetGraphic graphic ) {
        TxGraphic txg = new TxGraphic( graphic, nucleonTx );
        super.addGraphic( txg );
    }

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

    public void setOrigin( Point2D.Double origin ) {
        this.origin = origin;
        originTx.setToTranslation( origin.getX(), origin.getY() );
        nucleonTx.setToIdentity();
        nucleonTx.concatenate( originTx );
        nucleonTx.concatenate( scaleTx );
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
        private Nucleus nucleus;

        public GraphicRemover( PhetGraphic graphic, Nucleus nucleus ) {
            this.graphic = graphic;
            this.nucleus = nucleus;
        }

        public void leavingSystem( NuclearModelElement nme ) {
            removeGraphic( graphic );
            graphicListenerProxy.graphicRemoved( new GraphicEvent( graphic ) );
            nucleus.removeListener( this );
        }
    }


    //--------------------------------------------------------------------------------------------------
    // ChangeListener definition
    //--------------------------------------------------------------------------------------------------
    EventChannel changeEventChannel = new EventChannel( GraphicListener.class );
    GraphicListener graphicListenerProxy = (GraphicListener)changeEventChannel.getListenerProxy();

    public void addGraphicListener( GraphicListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeGraphicListener( GraphicListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class GraphicEvent extends EventObject {
        public GraphicEvent( PhetGraphic source ) {
            super( source );
        }

        public PhetGraphic getPhetGraphic() {
            return (PhetGraphic)getSource();
        }
    }

    public interface GraphicListener extends EventListener {
        void graphicAdded( GraphicEvent event );

        void graphicRemoved( GraphicEvent event );
    }
}