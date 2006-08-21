import java.awt.*;


/**
 * Class to calculate depletion width, and display a diagram.
 */
class DepletionWidth implements PhysicsConstants {
    /**
     * Creates a new DepletionWidth object.  The minimum voltage must be
     * known in order for the possible range of the depletion width to be
     * calculated.
     *
     * @param    Na    The acceptor density
     * @param    Nd    The donor density
     * @param    minV    The minimum voltage
     */
    public DepletionWidth( double Na, double Nd, double minV ) {
        setN( Na, Nd );
    }


    /**
     * Returns x<sub>n</sub>, the electron depletion width
     */
    public double getXn( double v ) {
        return Math.sqrt( nMultiplier * ( v0 - v ) );
    }


    /**
     * Returns x<sub>p</sub>, the hole depletion width
     */
    public double getXp( double v ) {
        return Math.sqrt( pMultiplier * ( v0 - v ) );
    }


    /**
     * Returns W = x<sub>n</sub> + x<sub>n</sub>, the sum of the depletion
     * widths.
     */
    public double getW( double v ) {
        return Math.sqrt( wMultiplier * ( v0 - v ) );
    }


    /**
     * All the constants are combined into a multiplier, for tidiness and
     * efficiency.  This method will be automatically invoked when any of
     * the constants involved changes, and will recalculate the multipliers.
     */
    protected void recalcMultipliers() {
        double _2e_over_q = 2 * epsilon / q;
        nMultiplier = _2e_over_q * Na / ( Nd * ( Na + Nd ) );
        pMultiplier = _2e_over_q * Nd / ( Na * ( Na + Nd ) );
        wMultiplier = _2e_over_q * ( Na + Nd ) / ( Na * Nd );
        setMinV( minV );
    }


    /**
     * Returns the acceptor density
     */
    public double getNa() {
        return Na;
    }

    /**
     * Returns the donor density
     */
    public double getNd() {
        return Nd;
    }

    /**
     * Sets the acceptor density
     */
    public void setNa( double d ) {
        Na = d;
        recalcMultipliers();
    }

    /**
     * Sets the donor density
     */
    public void setNd( double d ) {
        Nd = d;
        recalcMultipliers();
    }

    /**
     * Sets the acceptor and donor densities to the specified values.
     */
    public void setN( double Na, double Nd ) {
        this.Na = Na;
        this.Nd = Nd;
        recalcMultipliers();
    }


    /**
     * Draws a depletion diagram.
     *
     * @param    g    The Graphics surface on which to paint
     * @param    v    The voltage for which the diagram should be drawn
     * @param    w    The width (in pixels) availabe for the diagram
     * @param    h    The height (in pixels) available for the diagram
     */
    public void paint( Graphics g, double v, int w, int h ) {
        FontMetrics fm = g.getFontMetrics();

        double rangeX = maxX - minX;
        int barW = w - 5;

        int x0 = barW / 2;

        int n = (int)( ( getXn( v ) - 0 ) * 0.5 * barW / rangeX );
        int p = (int)( ( getXp( v ) - 0 ) * 0.5 * barW / rangeX );

        int lineH = fm.getHeight();
        int vGap = 2;
        int barY = lineH + vGap;
        int barH = h - 2 * barY;

        g.setColor( Color.blue );
        g.fillRect( x0, barY, n, barH );
        g.setColor( Color.red );
        g.fillRect( x0 - p, barY, p, barH );

        g.setColor( Color.black );
        g.drawRect( 0, barY, barW, barH );
        g.drawLine( x0, barY, x0, barY + barH );

        double v1 = -getXp( v );
        double v2 = +getXn( v );
        String lab1 = Double.isNaN( v1 ) ? "0" : BaseApplet2.toString( v1 );
        String lab2 = Double.isNaN( v2 ) ? "0" : BaseApplet2.toString( v2 );


        g.drawLine( x0 - p, h - lineH, x0 - p, h );
        g.drawString( "-Xp = " + lab1, x0 - p + 5, h - 1 );

        g.drawLine( x0 + n, 0, x0 + n, lineH );
        g.drawString( "Xn = " + lab2, x0 + n + 5, lineH - 1 );
    }


    /**
     * Used to indicate that the minimum voltage has changed.
     */
    public void setMinV( double minV ) {
        this.minV = minV;
        minX = -( maxX = Math.max( getXn( minV ), getXp( minV ) ) );
    }


    /**
     * Returns the value of v<sub>0</sub>
     */
    public double getV0() {
        return v0;
    }

    /**
     * Sets the value of v<sub>0</sub>
     */
    public void setV0( double d ) {
        v0 = d;
        recalcMultipliers();
    }


    /**
     * The acceptor density
     */
    protected double Na;
    /**
     * The donor density
     */
    protected double Nd;
    /**
     * The precalculated electron depletion multiplier
     */
    protected double nMultiplier;
    /**
     * The precalculated hole depletion multiplier
     */
    protected double pMultiplier;
    /**
     * The precalculated total depletion multiplier
     */
    protected double wMultiplier;
    /**
     * The minimum depletion value that will be visible on this diagram
     */
    protected double minX;
    /**
     * The maximum depletion value that will be visible on this diagram
     */
    protected double maxX;
    /**
     * The value of v<sub>0</sub>
     */
    protected double v0;
    /**    The minimum voltage for which this diagram may be displayed	*/
	protected double minV;
}