import java.awt.*;


/**
 * Represents a multi-line graph label.
 */
class GraphLabel {
    /**
     * Creates a new label.  To have the label span several lines, insert
     * LF characters ('\n') to create line breaks.
     *
     * @param    s    The text for the label
     */
    public GraphLabel( String s ) {
        final char DELIM = '\n';
        /*
              Why on earth the StringTokenizer doesn't find LFs under
              Mozilla, I don't know.  I don't want to know.  Best
              emulate it instead I suppose.
          */
        String s2 = s;
        int count;
        int index;
        for( count = 0; ( index = s2.indexOf( DELIM ) ) != -1 && ( index + 1 ) < s2.length(); count++ ) {
            s2 = s2.substring( index + 1 );
        }
        //StringTokenizer st = new StringTokenizer(s,"\n");
        lines = new String[count + 1];
        for( int i = 0; i < lines.length; i++ ) {
            index = s.indexOf( DELIM );
            if( index != -1 ) {
                lines[i] = s.substring( 0, index );
                s = s.substring( index + 1 );
            }
            else {
                lines[i] = s;
            }
        }
    }


    /**
     * Calculates the width (in pixels) of this label.
     *
     * @param    fm    The FontMetrics with which the label will be rendered
     */
    public int getWidth( FontMetrics fm ) {
        int max = 0;
        for( int i = 0; i < lines.length; i++ ) {
            max = Math.max( max, fm.stringWidth( lines[i] ) );
        }
        return max;
    }


    /**
     * Calculates the height (in pixels) of this label.
     *
     * @param    fm    The FontMetrics with which the label will be rendered
     */
    public int getHeight( FontMetrics fm ) {
        return lines.length * fm.getHeight();
    }


    /**
     * Paints this label to the left of the specified point, centered
     * vertically.
     */
    public void paintToLeft( Graphics g, int x, int y ) {
        FontMetrics fm = g.getFontMetrics();
        int w = getWidth( fm );
        int h = getHeight( fm );
        paint( g, x - w, y - h / 2 );
    }


    /**
     * Paints this label below the specified point, centered
     * horizontally.
     */
    public void paintBelow( Graphics g, int x, int y ) {
        FontMetrics fm = g.getFontMetrics();
        int w = getWidth( fm );
        int h = getHeight( fm );
        paint( g, x - w / 2, y );
    }


    /**
     * Paints this label such that the top-left corner of the label
     * lies at the specified point
     */
    public void paint( Graphics g, int x, int y ) {
        int h = g.getFontMetrics().getHeight();
        for( int i = 0; i < lines.length; i++ ) {
            g.drawString( lines[i], x, y + ( i + 1 ) * h );
        }
    }


    /**
     * Calculates the number of (textual) lines occupied by this label.
     */
    public int countLines() {
        return lines.length;
    }


    protected String[] lines;
}