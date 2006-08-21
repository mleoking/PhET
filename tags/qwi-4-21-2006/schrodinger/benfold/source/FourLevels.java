import java.awt.*;


/**
 * As for the {@link FeedbackApplet Feedback/Emission/Absorption Applet}, but
 * with an extra two energy levels.  Internally, the energy levels are
 * numbered from the top down, starting at zero.
 */
public class FourLevels extends FeedbackApplet {
    public void init() {
        upperDecayRate = lowerDecayRate = 1;
        super.init();
        pump = 0.5;
        pumpField.setText( "" + pump );
    }


    /**
     * Adds the text fields to the GUI
     */
    protected void addFields() {
        super.addFields();
        setLayout( new GridLayout( 6, 4 ) );
        upperDecayField = addField( "Upper decay rate", upperDecayRate );
        lowerDecayField = addField( "Lower decay rate", lowerDecayRate );
    }


    public void updateInput() {
        double upper = getDouble( upperDecayField );
        double lower = getDouble( lowerDecayField );

        if( upper < 0 ) {
            throw new IllegalArgumentException( "Need upper rate >= 0" );
        }
        if( upper < 0 ) {
            throw new IllegalArgumentException( "Need lower rate >= 0" );
        }

        super.updateInput();

        this.upperDecayRate = upper;
        this.lowerDecayRate = lower;
    }


    public void doPaint( Graphics g ) {
        FontMetrics fm = g.getFontMetrics();

        int w = getSize().width;
        int h = getGraphHeight();

        int graphH = h / 4;
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

        energyLevelY = new int[]{
                0,
                vMargin,
                pictureH - 3 * vMargin,
                pictureH - 2 * vMargin,
        };

        excitedY = 0;
        normalY = pictureH - 2 * vMargin;

        double xscl = ( w - 2 * hMargin ) / simW;
        double yscl = normalY / simH;

        int rectH = 5;
        int rectW = w - 2 * hMargin;
        g.setColor( new Color( 0xc0c0c0 ) );
        g.fillRect( 0, -rectH, rectW, rectH );
        g.fillRect( 0, -rectH + vMargin + rectH / 2, rectW, rectH / 2 );
        g.fillRect( 0, normalY - vMargin, rectW, rectH / 2 );
        g.fillRect( 0, normalY, rectW, rectH );

        g.translate( (int)( xscl * hGap / 2 ), 0 );

        g.translate( 0, vMargin );
        paintPhotons( g, xscl, 0.6 * yscl );
        g.translate( 0, -vMargin );

        paintElectrons( g, xscl, yscl );
        g.translate( (int)( -xscl * hGap / 2 ), 0 );

        int textH = -rectH - ( vMargin - rectH - g.getFontMetrics().getHeight() ) / 2;
        g.setColor( new Color( 0x000000 ) );
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


    /**
     * Override superclass method, so that this applet's modified electron
     * will be used.
     */
    public void createElectrons() {
        int n = (int)( simW / hGap );
        electrons = new Electron[n];
        for( int i = 0; i < n; i++ ) {
            electrons[i] = new Electron( i * hGap, 3 );
        }
    }


    /**
     * An array of four <i>y</i> co-ordinates (measured in pixels), one for
     * each energy level.
     */
    protected int[] energyLevelY;

    /**
     * The rate at which electrons move from the topmost state to the state
     * immediately below.
     */
    protected double upperDecayRate;

    /**
     * The rate at which electrons move to the bottommost state from the
     * state immediately above.
     */
    protected double lowerDecayRate;


    protected TextField upperDecayField, lowerDecayField;


    class Electron extends EmissionApplet.Electron {
        /**
         * Creates a new electron
         *
         * @param    x    The <i>x</i> position
         * @param    level    An integer in the range [0,3] specifying the
         * initial energy level
         * @throws IllegalArgumentException    if <code>level</code> is less
         * than 0 or greateer than 3
         */
        public Electron( double x, int level ) {
            super( x );
            if( level < 0 || level > 3 ) {
                throw new IllegalArgumentException();
            }
            energyLevel = level;
        }

        /**
         * Creates a new electron
         *
         * @param    x    The <i>x</i> position
         */
        public Electron( double x ) {
            this( x, 2 );
        }


        /**
         * Displays this Electron
         *
         * @param    g        The Graphics surface on which to paint
         * @param    xscl Multiplier to convert simulation <i>x</i>
         * coordinates	to screen coordinates
         * @param    yscl Multiplier to convert simulation <i>y</i>
         * coordinates to screen coordinates
         */
        public void paint( Graphics g, double xscl, double yscl ) {
            int r = 3;
            int x = (int)( xscl * this.x );

            int destY = energyLevelY[energyLevel];
            int srcY = energyLevelY[oldLevel];

            int y = (int)( progress * destY + ( 1 - progress ) * srcY );

            Color col = ( energyLevel == 0 || energyLevel == 3 )
                        ? new Color( 0xc000ff )
                        : Color.blue;
            g.setColor( col );
            g.fillOval( x - r, y - r, 2 * r, 2 * r );
        }


        /**
         * Absorbs the specified photon, causing this electron to become
         * excited.
         */
        public void absorb( Photon p ) {
            p.die();
            moveToLevel( 1 );
        }


        /**
         * Emits the specified photon, causing this electron to become
         * non-excited.
         */
        public void emit( Photon p ) {
            photons.addElement( p = (Photon)p.clone() );
            p.x -= dt;
            moveToLevel( 2 );
        }


        /**
         * Moves this electron to the upper energy level
         */
        public void pump() {
            moveToLevel( 0 );
        }


        /**
         * For backward compatibility, returns the same result as
         * {@link #canEmit() canEmit()}.
         */
        public boolean isExcited() {
            return energyLevel == 1;
        }


        public boolean canAbsorb() {
            return energyLevel == 2;
        }

        public boolean canEmit() {
            return energyLevel == 1;
        }


        /**
         * Causes the electron to begin moving to a specified energy level.
         */
        public void moveToLevel( int l ) {
            progress = 0;
            oldLevel = energyLevel;
            energyLevel = l;
        }


        /**
         * Advances this photon through time by the specified interval
         */
        public void update( double dt ) {
            double speedMultiplier = 1;

            //	Messy hack to make electrons move at the right speed on the screen
            if( energyLevel == 3 || oldLevel == 0 ) {
                speedMultiplier = 4;
            }

            progress = Math.min( progress + speedMultiplier * electronSpeed * dt, 1 );

            if( isMoving() ) {
                return;
            }

            double r = Math.random() / dt;

            switch( energyLevel ) {
                case 0:
                    if( r < upperDecayRate ) {
                        moveToLevel( 1 );
                    }
                    break;

                case 1:
                    if( r < spont ) {
                        emit( new Photon( x ) );
                    }
                    break;

                case 2:
                    if( r < lowerDecayRate ) {
                        moveToLevel( 3 );
                    }
                    break;

                case 3:
                    if( r < pump ) {
                        moveToLevel( 0 );
                    }
                    break;
            }
        }


        /**
         * For backwards compatibility.  Sets the energy level to one of the
         * two centre states.
         *
         * @param    b    <code>true</code> if the electron is to be moved to
         * state 1, <code>false</code> if it is to move to state
         * 2.
         */
        public void setExcited( boolean b ) {
            energyLevel = b ? 1 : 2;
            progress = 1;
        }


        /**
         * The present energy level, or the energy level toward which the
         * particle is moving.
         */
        protected int energyLevel;

        /**    The previoud energy level, or the energy level from which the
         particle is moving.  This value will be zero if the electron
			has never changed energy levels.
		*/
		protected int oldLevel;
	}
}