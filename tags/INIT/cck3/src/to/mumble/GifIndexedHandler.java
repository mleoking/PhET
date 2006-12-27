package to.mumble;

import java.awt.image.*;

public class GifIndexedHandler extends GifHandlerBase {
    /// The color model for this image
    private IndexColorModel m_icm;

    /// The output data buffer,
    private DataBufferByte m_dbb;

    /// The databuffer's data buffer!
    private byte[] m_data;


    public GifIndexedHandler( AnimGifDecoder de, GifImaMeta im, int type ) {
        super( de, im, type );
    }

    /**
     * Prepare decoding the current image. Create a bufferedimage and create
     * the color table belonging to it from the global or local color table.
     */
    protected BufferedImage prepare() throws java.io.IOException {
        //-- Prepare the color model,
        if( m_im.m_transparant ) {
            m_icm = new IndexColorModel( m_im.m_bits_colortable, m_im.m_sz_colortable, m_im.m_reds, m_im.m_grns, m_im.m_blus, m_im.m_transparant_ix );
        }
        else {
            m_icm = new IndexColorModel( m_im.m_bits_colortable, m_im.m_sz_colortable, m_im.m_reds, m_im.m_grns, m_im.m_blus );
        }


        //-- Create the BufferedImage,
        m_bi = new BufferedImage( m_im.m_w, m_im.m_h, m_type, m_icm );

        //-- Get all writer data & check,
        Raster ras = m_bi.getRaster();
        SampleModel tsm = ras.getSampleModel();
        if( !( tsm instanceof PixelInterleavedSampleModel ) ) {
            return null;
        }
        PixelInterleavedSampleModel sm = (PixelInterleavedSampleModel)tsm;

        DataBuffer dbt = ras.getDataBuffer();
        if( dbt.getDataType() != DataBuffer.TYPE_BYTE ) {
            return null;
        }
        if( dbt.getNumBanks() != 1 ) {
            return null;
        }
        m_dbb = (DataBufferByte)dbt;

        m_data = m_dbb.getData();

        return m_bi;
    }


    protected void pixels( byte[] pix, int len ) throws java.io.IOException {
        while( len > 0 ) {
            int m = getRunMax();			// Get max #pixels allowed
            if( m > len ) {
                m = len;				// Truncate to availability,
            }
            int off = getOffset();
            System.arraycopy( pix, 0, m_data, off, m );
            len -= m;
            incrementPos( m );
        }
    }
}
