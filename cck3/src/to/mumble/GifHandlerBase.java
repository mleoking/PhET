package to.mumble;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This class is the base class for each specialized BufferedImage writer. It
 * contains the base code to determine where to write in the BufferedImage.
 */

public abstract class GifHandlerBase {
    /// The decoder decoding the current image
    protected AnimGifDecoder m_gd;

    /// This-image's metadata,
    protected GifImaMeta m_im;

    /// The BufferedImage.TYPE of the output image
    protected int m_type;

    /// The BufferedImage being written
    protected BufferedImage m_bi;

    /// The INTERLACED phase indicator. 0 = no interlace (increment Y by 1),
    protected int m_interlace_phase;

    /// The current BUFFER offset for direct-writable buffers,
    protected int m_out_o;

    /// The current output position (x, y).
    protected int m_x, m_y;

    /// The current output offset's end,
    protected int m_out_e;

    /// The width and height of the image to render,
    protected int m_w, m_h;


    /**
     * Interlace format: contains pairs of (start line, increment) values. For
     * interlace phase 0 (not interlaced) these are (0, 1) meaning start at line
     * zero increment by 1.
     */
    static private int[] inter_data =
            {0, 1,
             0, 8, // Group 1
             4, 8, // Group 2
             2, 4, // Group 3
             1, 2		// Group 4.
            };


    public GifHandlerBase( AnimGifDecoder gd, GifImaMeta im, int type ) {
        m_gd = gd;
        m_im = im;
        m_type = type;

        //-- Init interlace stuff,
        m_w = m_im.m_w;
        m_h = m_im.m_h;
        m_out_o = 0;						// Initial output offset,
        m_x = 0;
        m_y = 0;

        if( m_im.m_interlaced ) {
            m_interlace_phase = 2;			// Start at index 2 - interlaced
            m_out_e = m_w;
        }
        else {
            m_interlace_phase = 0;			// Start at phase 0 - non-interlaced
            m_out_e = m_w * m_h;
        }
    }

    /**
     * Returns the #of pixels that can be written in one go, before end-of-line
     * processing has to take place.
     */
    protected int getRunMax() {
        return m_out_e - m_out_o;
    }


    /**
     * Increments the pixel position for when n pixels are written. If the
     * current position exceeds a line AND if we're interlaced a new position
     * will be calculated.
     */
    protected void incrementPos( int nwritten ) {
        m_out_o += nwritten;
        m_x += nwritten;
        if( m_out_o < m_out_e ) {
            return;				// Written & be done,
        }

        if( m_interlace_phase == 0 )					// No interlace? Then we're done
        {
            return;
        }

        //-- Handle increment to next interlace line,
        m_y += inter_data[m_interlace_phase + 1];		// Increment,
        if( m_y >= m_h )								// End reached??
        {
            //-- Auch! Move to the next interlace pass..
            m_interlace_phase += 2;
            if( m_interlace_phase >= inter_data.length )	// End of image!
            {
                return;
            }
            m_y = inter_data[m_interlace_phase];	// Set new start position
        }

        //-- Calculate new offset
        m_out_o = m_y * m_w;						// New offset,
        m_out_e = m_out_o + m_w;					// New end of line,
        m_x = 0;
    }


    protected int getOffset() {
        return m_out_o;
    }


    /**
     * Called when the image-to-decode's metadata is known and decoding will
     * commence. This will create the actual BufferedImage and will setCoefficient up the
     * code to write pixels to the image as fast as possible.
     */
    protected abstract BufferedImage prepare() throws IOException;


    /**
     * Called when a new setCoefficient of pixels is available. This writes the pixels to
     * the appropriate position within the image.
     */
    protected abstract void pixels( byte[] pix, int len ) throws IOException;

}

