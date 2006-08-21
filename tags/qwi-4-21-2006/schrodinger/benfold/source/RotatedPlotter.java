import java.awt.*;


/**
 * A plotter with the horizontal and vertical axes swapped, and all
 * necessary methods overridden to reflect this change.
 */
class RotatedPlotter extends Plotter {
    public RotatedPlotter( Solvable s ) {
        super( s );
    }

    /*
         Scale values to fit into graph area;
         Plotted region extends from (viewLeft, minY) to (viewRight, maxY)
         This should now all be independent of integration range [minX,maxX]
     */
    public void paintGraph( Graphics g, int w, int h ) {
        double xscl = w / rangeX;
        double yscl = h / viewRange;
        double stepSize = (double)rangeY / ( soln.length - 1 );
        double y = viewLeft;
        int steps = soln.length - 1;
        int interpolation = this.interpolate ? 1 : 0;

        int left = (int)( steps * ( viewLeft - minY ) / rangeY );
        int right = (int)( steps * ( viewRight - minY ) / rangeY );

        for( int i = left; i < right; i++ ) {
            //double y1 = soln[i], y2 = soln[i+interpolation];
            if( i < 0 || i >= steps || soln[i] < minX || soln[i] > maxX || soln[i + interpolation] < minX || soln[i + interpolation] > maxX )// || y1<minY || y2>maxY || Double.isInfinite(y1) || Double.isInfinite(y2) || Double.isNaN(y1) || Double.isNaN(y2))
            {
                y += stepSize;
                continue;
            }

            double newY = y + stepSize;
            g.drawLine(
                    (int)( xscl * ( soln[i] - minX ) ),
                    h - (int)( yscl * ( y - viewLeft ) ),
                    (int)( xscl * ( soln[i + interpolation] - minX ) ),
                    h - (int)( yscl * ( newY - viewLeft ) )
            );
            y = newY;
        }
    }

    protected void updateSolution( int steps ) {
        //	Choose step size, prepare array
        soln = new double[steps];
        double step = rangeY / steps;

        //	Get function
        eqn.solve( minY, step, soln );

        if( log ) {
            for( int i = 0; i < steps; i++ ) {
                soln[i] = Math.log( soln[i] );
            }
        }
    }

    public int toScreenX( double x, int w ) {
        double xscl = w / rangeX;
        return (int)( xscl * ( x - minX ) );
    }

    public int toScreenY( double y, int h ) {
        double yscl = h / viewRange;
        return h - (int)( yscl * ( y - viewLeft ) );
    }

    public double fromScreenX( int x, int w ) {
        double xscl = w / rangeX;
        return x / xscl + minX;
    }

    public double fromScreenY( int y, int h ) {
        double yscl = h / viewRange;
        return y / yscl + viewLeft;
    }

    protected void scaleToFit() {
        int left = (int)( ( viewLeft - minY ) * soln.length / rangeY );
        int right = (int)( ( viewRight - minY ) * soln.length / rangeY );
        scaleToFit( left, right );
    }

    protected void scaleToFit( int left, int right ) {
        if( left < 0 ) {
            left = 0;
        }
        if( right >= soln.length ) {
            right = soln.length - 1;
        }
        //System.err.println(soln.length);

        //	Decide on region to search
        minX = getMin( soln, left, right );
        maxX = getMax( soln, left, right );
        rangeX = maxX - minX;

        //infY = maxY==ABS_MAX;

        //	Adjust limits to leave a small margin
        maxX += topMargin * rangeX;
        minX -= bottomMargin * rangeX;
        rangeX = maxX - minX;
    }


    public void setViewAndRange( double min, double max ) {
        setRangeY( min, max );
        setView( min, max );
    }

    public void labelAxisX( Graphics g, int w, int h ) {
        int ox = 0; //toScreenX(0,w);
        int oy = h; //toScreenY(0,h);

        double minorSpacing = chooseTickSize( rangeX );
        if( minorSpacing == 0 ) {
            minorSpacing = rangeX / 7;
        }

        double x = snap( fromScreenX( 0, w ), minorSpacing );
        int i = (int)Math.rint( x / minorSpacing );

        for( ; toScreenX( x, w ) < w; x += minorSpacing, i++ ) {
            x = snap( x, minorSpacing );
            int tx = toScreenX( x, w );
            int tickSize = ( i % tickRatio == 0 ) ? majorTickSize : minorTickSize;
            if( !noUnitsX ) {
                g.drawLine( tx, oy, tx, oy - tickSize );
            }
            if( !noUnitsX && ( i % tickRatio == 0 ) ) {
                paintLabelBelow( g, toString( x ), tx, oy );
            }
        }

        if( !noUnitsX ) {
            oy += g.getFontMetrics().getHeight();
        }

        if( xLabel != null )
        //paintLabelBelow(g,xLabel,w/2,oy+g.getFontMetrics().getHeight());
        {
            xLabel.paintBelow( g, w / 2, oy );
        }
    }

    public void labelAxisY( Graphics g, int w, int h ) {
        int ox = 0; //toScreenX(0,w);
        int oy = h; //toScreenY(0,h);

        g.drawLine( ox, oy, w, oy );
        g.drawLine( ox, oy, ox, 0 );

        if( infY ) {
            paintLabelToLeft( g, "-Infinity", ox, toScreenY( minY + 0.1 * rangeY, h ) );
            ox -= g.getFontMetrics().stringWidth( "-8.888" );
            if( yLabel != null ) {
                yLabel.paintToLeft( g, ox, h / 2 );
            }
            return;
        }

        double minorSpacing = chooseTickSize( viewRange );
        if( minorSpacing == 0 || noUnitsY ) {
            //	Just put in one mark at the top
            double originalRange = rangeY / ( 1 + bottomMargin + topMargin );
            minorSpacing = originalRange * ( 1 + bottomMargin ) / tickRatio;
            //minorSpacing = rangeY/7;
        }

        double y = snap( fromScreenY( 0, h ), minorSpacing );
        int i = (int)Math.rint( y / minorSpacing );

        for( ; toScreenY( y, h ) > 0; y += minorSpacing, i++ ) {
            y = snap( y, minorSpacing );
            int ty = toScreenY( y, h );
            int tickSize = ( ( i % tickRatio ) == 0 ) ? majorTickSize : minorTickSize;
            if( !noUnitsY ) {
                g.drawLine( ox, ty, ox + tickSize, ty );
            }
            if( !noUnitsY && ( i % tickRatio == 0 ) ) {
                paintLabelToLeft( g, toString( y ), ox, ty );
            }
        }

        if( !noUnitsY ) {
            ox -= g.getFontMetrics().stringWidth( "-8.888" );
        }

        if( yLabel != null )
        //paintLabelToLeft(g,yLabel,ox-g.getFontMetrics().stringWidth("-8.888"),h/2);
        {
            yLabel.paintToLeft( g, ox, h / 2 );
        }
    }

    public String toString( double d ) {
        String s = "" + d;
        int i = Math.max( s.indexOf( 'e' ), s.indexOf( 'E' ) );
        if( i < 0 ) {
            i = s.length();
        }
        String exp = s.substring( i );
        String mantissa = s.substring( 0, i );
        i = Math.max( s.indexOf( '.' ), 6 );
        i = Math.min( mantissa.length(), i );
        return mantissa.substring( 0, i ) + exp;
    }

}