import java.awt.*;


/**
 * As for the {@link EmissionApplet Emission/Absorption Applet}, but with
 * the addition of feedback and an exit rate graph.
 */
public class FeedbackApplet extends EmissionApplet {
    public void init() {
        feedbackRate = 0.1;
        observations = 600;
        sampleTime = 0.025;
        exitDensity = new TimeGraph( observations );
        exitDensity.setSkipRatio( 0 );
        plotter = new Plotter2( exitDensity );
        plotter.setGraphColor( Color.black );
        plotter.setViewAndRange( 0, 1 );
        plotter.bottomMargin = 0;

        super.init();
    }

    protected void addFields() {
        setLayout( new GridLayout( 5, 4 ) );

        super.addFields();

        feedbackField = addField( "Feedback rate", feedbackRate );
        dtField = addField( "dt", dt );
    }


    /**
     * As for the {@link EmissionApplet#cullPhotons() superclass method},
     * but also introduces a probality (the feedback rate) that a photon
     * returns to the start of the simulation.  (This obviously does not
     * apply to absorbed photons.)
     * <p/>
     * The number returned does not include absorbed photons.
     */
    protected int cullPhotons() {
        int count = 0;
        for( int i = 0; i < photons.size(); i++ ) {
            Photon p = (Photon)photons.elementAt( i );

            if( !destroyPhotons && !p.isFinished() ) {
                continue;
            }

            if( !p.dead ) {
                count++;
            }

            if( !destroyPhotons && !p.dead && Math.random() < feedbackRate ) {
                p.x -= simW * loopTime;
                continue;
            }

            photons.removeElementAt( i-- );
        }

        destroyPhotons = false;
        return count;
    }


    /**
     * As for the {@link EmissionApplet#cullPhotons() superclass method},
     * but also keeps track of the number of electrons leaving.
     */
    public void updatePhotons( double dt ) {
        if( photons.isEmpty() ) {
            spawnPhoton();
            destroyPhotons = false;
        }

        for( int i = 0; i < photons.size(); i++ ) {
            ( (Photon)photons.elementAt( i ) ).update( dt );
        }

        int leaving = cullPhotons();

        exited += leaving;
        obsCount += leaving;

        t += dt;
        obsTimer += dt;

        if( t >= loopTime ) {
            exitMessage = "Exit rate:  " + exited;
            t -= loopTime;
            exited = 0;
        }
        if( obsTimer >= sampleTime ) {
            //if(dt==0)
            //	System.err.println("huh?");
            exitDensity.addObservation( obsCount );
            //System.err.println("observation:  "+obsCount);
            obsTimer -= sampleTime;
            obsCount = 0;
        }
    }

    public void doPaint( Graphics g ) {
        FontMetrics fm = g.getFontMetrics();

        int w = getSize().width;
        int h = getGraphHeight();

        int graphH = h / 3;
        int pictureH = h - graphH;

        vMargin = (int)( 0.1 * h );
        hMargin = (int)( 0.1 * w );

        plotter.setRect( 0, pictureH, w, graphH );
        plotter.updateSolution( observations );
        plotter.scaleToFit();
        plotter.setRangeY( plotter.minY, Math.max( plotter.maxY, 1 ) );
        plotter.paint( g );

        //	Draw a "now line"
        int nowX = plotter.toScreenX( exitDensity.getCurrentPos( 0, (double)1 / observations ) );

        g.setColor( new Color( 0xc0c0c0 ) );
        g.drawLine( nowX, plotter.toScreenY( 0 ), nowX, plotter.toScreenY( plotter.maxY ) );


        g.translate( hMargin, vMargin );

        excitedY = 0;
        normalY = pictureH - 2 * vMargin;

        double xscl = ( w - 2 * hMargin ) / simW;
        double yscl = normalY / simH;

        int rectH = 5;
        g.setColor( new Color( 0xc0c0c0 ) );
        g.fillRect( 0, -rectH, w - 2 * hMargin, rectH );
        g.fillRect( 0, normalY, w - 2 * hMargin, rectH );

        g.translate( (int)( xscl * hGap / 2 ), 0 );
        paintPhotons( g, xscl, yscl );
        paintElectrons( g, xscl, yscl );
        g.translate( (int)( -xscl * hGap / 2 ), 0 );

        int textH = -rectH - ( vMargin - rectH - g.getFontMetrics().getHeight() ) / 2;
        double inv = calcInversion();
        if( !Double.isNaN( inv ) ) {
            g.drawString( "Inversion:  " + toString( inv ), 0, textH );
        }

        g.translate( -hMargin, -vMargin );


        g.translate( 0, vMargin );
        g.setColor( new Color( 0x000000 ) );
        g.drawString( exitMessage, w - hMargin - fm.stringWidth( exitMessage ), textH );
        g.translate( 0, -vMargin );
    }


    public void updateInput() {
        double feedback = getDouble( feedbackField );
        double dt = getDouble( dtField );


        if( feedback < 0 ) {
            throw new IllegalArgumentException( "Need feedback >= 0" );
        }
        if( feedback > 1 ) {
            throw new IllegalArgumentException( "Need feedback <= 1" );
        }

        if( dt < 0 ) {
            throw new IllegalArgumentException( "Need dt >= 0" );
        }
        if( dt > hGap ) {
            throw new IllegalArgumentException( "Need dt <= " + hGap );
        }


        super.updateInput();

        this.feedbackRate = feedback;
        this.dt = dt;
    }


    /**
     * The number of electrons that have left since the &quot;Exit rate&quot;
     * label was last updated
     */
    protected int exited;

    /**
     * Number of samples to fit on the &quot;exit rate&quot; graph
     */
    protected int observations;

    /**
     * The number of electrons that have left since the last observation was
     * added to the &quot;exit rate&quot; graph
     */
    protected int obsCount;

    /**
     * The text of the &quot;Exit rate&quot; label
     */
    protected String exitMessage = "";

    /**
     * Time elapsed since last update to the exit rate label
     */
    protected double t = 0;

    /**
     * Time elapsed since last observation was added to the exit rate graph
     */
    protected double obsTimer;

    /**
     * Time between updates to the exit rate label
     */
    protected double loopTime = 1.25;

    /**
     * Time between updates to the exit rate label
     */
    protected double sampleTime;

    /**
     * Average proportion of photons leaving the simulation (by reaching
     * the extreme right of the display, not by absorption) which will
     * be returned to the start.
     */
    protected double feedbackRate;

    protected TextField feedbackField, dtField;
    protected TimeGraph exitDensity;
	protected Plotter2 plotter;
}