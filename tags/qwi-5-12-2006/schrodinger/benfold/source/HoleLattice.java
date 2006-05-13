import java.awt.*;
import java.util.Vector;


/**
 * A simple model to describe the motion of holes through a
 * lattice.
 */
class HoleLattice {
    /**
     * Creates a new lattice.
     *
     * @param    w    The width of the lattice (number of bases across)
     * @param    h    The width of the lattice (number of bases down)
     */
    public HoleLattice( int w, int h ) {
        this.lw = w;
        this.lh = h;
        lattice = new Basis[w][h];
        for( int i = 0; i < w; i++ ) {
            for( int j = 0; j < h; j++ ) {
                lattice[i][j] = new Basis();
            }
        }
        spawnRate = 0.002;
        advanceRate = 4 * spawnRate;
        slideRate = (double)1 / 3;
        holes = new Vector();
    }


    /**
     * Paints the lattice (and associated displays) onto the specified
     * Graphics surface.
     *
     * @param    w    The width to scale the display to
     * @param    h    The height to scale the display to
     */
    public void paint( Graphics g, int w, int h ) {
        int basisW = w / lw;
        int basisH = h / lh;

        atomW = ( 3 * basisW ) / 7;
        atomH = ( 3 * basisH ) / 7;
        holeW = ( 2 * basisW ) / 7;
        holeH = ( 2 * basisH ) / 7;
        insetX = ( atomW - holeW ) / 2;
        insetY = ( atomH - holeH ) / 2;

        int keyH = getKeyHeight( g.getFontMetrics() );

        paintKey( g, w, h );

        g.translate( 0, keyH );

        for( int i = 0; i < lh; i++ ) {
            for( int j = 0; j < lw; j++ ) {
                lattice[j][i].paint( g );
                g.translate( basisW, 0 );
            }
            g.translate( -lw * basisW, basisH );
        }
        g.translate( 0, -lh * basisH );

        paintHoles( g, w, h );

        g.translate( 0, -keyH );
    }


    /**
     * Executes one step for the model; each (stationary) hole will have
     * a random chance to move to a nearby state.
     */
    public synchronized void advanceHoles() {
        for( int i = 0; i < holes.size(); i++ ) {
            Hole h = (Hole)holes.elementAt( i );

            if( !h.isStationary() ) {
                h.advance();
                if( h.isStationary() ) {
                    lattice[h.oldX][h.oldY].holes[h.oldPos] = true;
                }
                continue;
            }

            //	The rest of this assumes the particle is stationary

            if( Math.random() > advanceRate ) {
                continue;
            }

            int x = h.x, y = h.y, pos = h.pos;

            switch( pos ) {
                case BOTTOM:
                case TOP:
                    double d = Math.random();

                    if( ( d -= slideRate ) < 0 ) {
                        pos = LEFT;
                        break;
                    }

                    x--;

                    if( ( d -= slideRate / 2 ) < 0 ) {
                        pos = ( pos == TOP ) ? BOTTOM : TOP;
                        y += ( pos == TOP ) ? 1 : -1;
                    }

                    break;

                case RIGHT:
                    pos = ( Math.random() < 0.5 ) ? TOP : BOTTOM;
                    break;

                case LEFT:
                    pos = RIGHT;
                    x--;
                    break;
            }

            if( y < 0 || y >= lh ) {
                continue;
            }

            if( x < 0 ) {
                lattice[h.x][h.y].holes[h.pos] = true;
                holes.removeElementAt( i-- );
                continue;
            }

            if( lattice[x][y].holes[pos] ) {
                //lattice[h.x][h.y].holes[h.pos] = true;
                lattice[x][y].holes[pos] = false;
                h.moveTo( x, y, pos );
            }
        }
    }


    /**
     * Creates more holes at the far right-hand side of the lattice.
     */
    public void spawnHoles() {
        for( int i = 0; i < lh; i++ ) {
            Basis b = lattice[lw - 1][i];
            if( Math.random() < spawnRate && b.holes[RIGHT] ) {
                b.holes[RIGHT] = false;
                holes.addElement( new Hole( lw - 1, i, RIGHT ) );
            }
        }
    }


    /**
     * Paints the key onto the specified Graphics surface.
     *
     * @param    w    The width to scale the display to
     * @param    h    The height to scale the display to
     */
    public void paintKey( Graphics g, int w, int h ) {
        FontMetrics fm = g.getFontMetrics();
        int space = 8;
        int fontH = fm.getHeight();
        int lineH = Math.max( fontH, Math.max( holeH, atomH ) );

        int textY = ( lineH + fontH ) / 2 - 3;
        int arrowLen = 100;
        int arrowW = 5;

        g.setColor( Color.black );
        String s = "External Electric Field";
        int textX = ( w - ( fm.stringWidth( s ) + arrowLen + 10 ) ) / 2;
        g.drawString( s, textX + arrowLen + 10, textY );

        //	Draw line for arrow
        g.drawLine( textX, lineH / 2, textX + arrowLen, lineH / 2 );

        //	Draw arrowhead
        g.drawLine( textX, lineH / 2, textX + arrowW, lineH / 2 - arrowW );
        g.drawLine( textX, lineH / 2, textX + arrowW, lineH / 2 + arrowW );

        g.translate( 0, lineH );

        g.setColor( Color.blue );
        g.fillOval( 0, 0, atomW, atomH );
        g.setColor( Color.black );
        g.drawString( "Atoms", atomW + space, textY );

        g.fillOval( w / 3, insetY, holeW, holeH );
        g.drawString( "Electrons", w / 3 + holeW + space, textY );

        g.drawOval( 2 * w / 3, insetY, holeW, holeH );
        g.drawString( "Holes", 2 * w / 3 + holeW + space, textY );

        g.translate( 0, -lineH );

        g.drawLine( 0, 2 * lineH + space / 2, w - 1, 2 * lineH + space / 2 );

    }


    /**
     * Calculates the height (in pixels) of the key.
     *
     * @param    fm    The FontMetrics with which the key will be rendered
     */
    public int getKeyHeight( FontMetrics fm ) {
        int space = 8;
        int h = fm.getHeight();
        return 2 * Math.max( h, Math.max( holeH, atomH ) ) + space;
    }


    /**
     * Paints any holes/electrons currently in motion.
     *
     * @param    w    The width to scale the display to
     * @param    h    The height to scale the display to
     */
    public void paintHoles( Graphics g, int w, int h ) {
        int basisW = w / lw;
        int basisH = h / lh;

        g.setColor( Color.black );
        for( int i = 0; i < holes.size(); i++ ) {
            Hole _h = (Hole)holes.elementAt( i );
            if( !_h.isStationary() ) {
                Point start = lattice[_h.oldX][_h.oldY].getPosition( _h.oldPos );
                start.translate( _h.oldX * basisW, _h.oldY * basisH );
                Point end = lattice[_h.x][_h.y].getPosition( _h.pos );
                end.translate( _h.x * basisW, _h.y * basisH );
                double p = _h.progress;

                //	Add a p(1-p) to y to get a cute parabola
                int x = (int)( p * start.x + ( 1 - p ) * end.x );
                int y = (int)( p * start.y + ( 1 - p ) * end.y - holeH * p * ( 1 - p ) );
                g.fillOval( x, y, holeW, holeH );
            }
        }

    }


    /**
     * Turns animation on or off.
     *
     * @param    b    <code>true</code> if animation should be on, otherwise
     * <code>false</code>
     */
    public void setSmooth( boolean b ) {
        smooth = b;
    }


    /**
     * Set to <code>false</code> if electrons move instantly,
     * <code>true</code> if electrons move slowly.
     */
    protected boolean smooth = false;
    /**
     * A list of all the holes currently in the lattice
     */
    protected Vector holes;
    /**
     * The lattice itself, represented as an array of bases
     */
    protected Basis[][] lattice;
    /**
     * The width (in bases) of the lattice
     */
    protected int lw;
    /**
     * The height (in bases) of the lattice
     */
    protected int lh;
    /**
     * The rate at which new holes are created
     */
    protected double spawnRate;
    /**
     * The rate at which holes move across the lattice
     */
    protected double advanceRate;
    /**
     * The rate at which holes &quot;slide&quot; diagonally instead
     * of straight ahead
     */
    protected double slideRate;
    protected int atomW, atomH, holeW, holeH, insetX, insetY;
    public static final int
            TOP = 0,
            BOTTOM = 1,
            LEFT = 2,
            RIGHT = 3;


    /**
     * Represents a basis in the lattice, consisting of four holes/electrons
     * around an atom.
     */
    protected class Basis {
        /**
         * Creates a new basis, with every state filled.
         */
        public Basis() {
            holes = new boolean[]{true, true, true, true};
        }


        /**
         * Paints this basis onto the specified Graphics surface.
         */
        public void paint( Graphics g ) {
            g.setColor( Color.blue );
            g.fillOval( holeW, holeH, atomW, atomH );


            g.setColor( Color.black );
            paintElectron( g, holeW + insetX, 0, holeW, holeH, TOP );
            paintElectron( g, 0, holeH + insetY, holeW, holeH, LEFT );
            paintElectron( g, holeW + insetX, atomH + holeH, holeW, holeH, BOTTOM );
            paintElectron( g, atomW + holeW, holeH + insetY, holeW, holeH, RIGHT );
        }


        /**
         * Paints an electron.
         *
         * @param    g    The Graphics surface on which to paint
         * @param    x The x-coordinate (in pixels) at which to paint
         * @param    y The y-coordinate (in pixels) at which to paint
         * @param    w    The width (in pixels) of the electron
         * @param    h    The height (in pixels) of the electron
         * @param    which    The state number for the electron/hole
         */
        public void paintElectron( Graphics g, int x, int y, int w, int h, int which ) {
            if( holes[which] ) {
                g.fillOval( x, y, w, h );
            }
            else {
                g.drawOval( x, y, w, h );
            }
        }


        /**
         * Calculates the position of the specified state, with respect
         * to the top-left corner of the basis.
         */
        public Point getPosition( int pos ) {
            switch( pos ) {
                case TOP:
                    return new Point( holeW + insetX, 0 );
                case LEFT:
                    return new Point( 0, holeH + insetY );
                case BOTTOM:
                    return new Point( holeW + insetX, atomH + holeH );
                case RIGHT:
                    return new Point( atomW + holeW, holeH + insetY );
                default:
                    throw new RuntimeException( "invalid position:  " + pos );
            }
        }


        /**
         * Array indicating which of the states are filled
         * (<code>true</code>), and which are empty (<code>false</code>).
         */
        protected boolean[] holes;
    }


    /**
     Represents a hole/electron in motion.
     */
    protected class Hole {
        /**
         * Creates a new hole.
         *
         * @param    x    The x-coordinate (in the lattice)
         * @param    y    The y-coordinate (in the lattice)
         * @param    pos    The state number
         */
        public Hole( int x, int y, int pos ) {
            this.x = x;
            this.y = y;
            this.pos = pos;
            progress = 1;
        }


        /**
         * Advances the hole/electron towards the current target.  If
         * animation is disabled, the electron will make the transition
         * instantly.  If the hole is stationary, this method will have
         * no effect.
         */
        public void advance() {
            double increment = smooth ? 0.1 : 1;
            //System.err.println(progress);
            progress = Math.min( progress + increment, 1 );
        }

        public boolean isStationary() {
            return progress == 1;
        }


        /**
         * Advances the hole to the specified coordinates and state.  If
         * animation is enabled, the transition will occur gradually with
         * calls to {@link #advance() advance()}.
         */
        public void moveTo( int x, int y, int pos ) {
            oldX = this.x;
            oldY = this.y;
            oldPos = this.pos;
            this.x = x;
            this.y = y;
            this.pos = pos;
            progress = 0;
        }


        protected int x, y, pos, oldX, oldY, oldPos;
        /**    How close the hole/electron transition is to completion.
         A value of <code>0</code> indicates that the transition has just
         begun; <code>1</code> indicates that the transition is complete
			(ie. no transition is in progress).
		*/
		protected double progress;
	}
}