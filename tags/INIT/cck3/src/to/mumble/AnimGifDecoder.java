package to.mumble;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * This class allows quick decoding of GIF images into BufferedImages. The main
 * use is in web applications. The image type generated can be specified during
 * the decode process. For maximum speed this class does not implement any of\
 * the standard Java interfaces like ImageConsumer and ImageProducer and the
 * like.
 */

public class AnimGifDecoder {
    /// The inputstream containing the images.
    private InputStream m_is;

    /// T if the image header e.a. has been read,
    private boolean m_has_header;

    /// The global color table arrays.
    private byte[] m_red, m_grn, m_blu;

    /// An array of offsets to images
    private long[] m_imaoff_ar;

    /// The current highest image number.
    private int m_n_images;

    /// Header data,
    public byte[] m_h_sig;

    public String m_h_version;

    /// The current BufferedImage being read,
    private BufferedImage m_bi;

    /// Logical screen width & height (compound of all embedded images)
    public int m_h_lw, m_h_lh;

    /// The current image's meta data.
    private GifImaMeta m_im;

    /// The handler for the output BufferedImage type.
    private GifHandlerBase m_h;

    public AnimGifDecoder( InputStream is ) {
        m_is = is;
    }

    /*--------------------------------------------------------------*/
    /*	CODING:	Reading data from the stream,						*/
    /*--------------------------------------------------------------*/
    private void readBytes( byte[] ar, int ln ) throws IOException {
        if( m_is.read( ar, 0, ln ) != ln ) {
            throw new IOException( "Input stream expected more data" );
        }

//		System.out.print("rd["+ar.length+"]: ");
//		for(int i = 0; i < ar.length; i++)
//		{
//			dbg(ar[i]);
//		}
//		System.out.println(";");

    }

    private int m_bc = 0;

    private void dbg( String s ) {
        if( m_bc != 0 ) {
            System.out.println( "" );
            m_bc = 0;
        }
        System.out.println( "DBG: " + s );
    }

    private void dbg( int c ) {
        if( m_bc > 16 ) {
            System.out.println( "" );
            m_bc = 0;
        }
        System.out.print( Integer.toString( c & 0xff, 16 ) + " " );
        m_bc++;
    }

    private void skipBytes( int sz ) throws IOException {
        m_is.skip( sz );
    }


    /**
     * Reads the next SIGNED byte from the input stream.
     */
    private byte rdByte() throws IOException {
        byte b = (byte)m_is.read();
//		dbg(b);

        return b;
    }

    /**
     * Reads the next UNSIGNED byte from the input stream.
     */
    private int rdUByte() throws IOException {
        int v = ( (int)m_is.read() & 0xff );
//		dbg(v);
        return v;
    }

    /**
     * Reads the next unsigned short and return as an int.
     */
    private int rdUShort() throws IOException {
        int rv = (int)rdByte() & 0xff;
        rv |= ( rdByte() << 8 ) & 0xff00;
        return rv;
    }


    /**
     * Returns T if the stream (seems) to represent a GIF file. Checks for
     * the GIF8xa marker.
     */
    public boolean canAccept() throws IOException {
        byte[] ar = new byte[6];
        try {
            m_is.mark( 10 );
            if( m_is.read( ar ) != 6 ) {
                return false;		// Cannot read -> invalid,
            }
            if( ar[0] == 'G' && ar[1] == 'I' && ar[2] == 'F' && ar[3] == '8' &&
                ( ar[4] == '7' || ar[4] == '9' ) && ar[5] == 'a' ) {
                return true;
            }
            return false;
        }
        finally {
            try {
                m_is.reset();
            }
            catch( Exception x ) {
            }
        }
    }


    /**
     * Reads the main GIF header and all assoc data. Assumes to be at start of
     * GIF header.
     */
    private void readMainHeader() throws IOException {
        if( m_has_header ) {
            return;
        }
        byte[] ar = new byte[6];
        readBytes( ar, 6 );

        if( !( ar[0] == 'G' && ar[1] == 'I' && ar[2] == 'F' && ar[3] == '8' && ( ar[4] == '7' || ar[4] == '9' ) && ar[5] == 'a' ) ) {
            throw new IOException( "File not recognised as a GIF image" );
        }
        m_h_sig = ar;
        StringBuffer sb = new StringBuffer( 3 );
        sb.append( (char)m_h_sig[3] );
        sb.append( (char)m_h_sig[4] );
        sb.append( (char)m_h_sig[5] );
        m_h_version = sb.toString();

        //-- Followed by the Logical Screen Descriptor
        m_h_lw = rdUShort();
        m_h_lh = rdUShort();
        byte f = rdByte();			// Packed fields
        m_h_bgindex = rdByte();			// Background color index,
        m_h_pxaspect = rdByte();			// Pixel aspect ratio
        m_h_has_global_ct = ( f & 0x80 ) != 0;
        m_h_color_res = ( f >> 4 ) & 0x7;
        m_h_sorted = ( f & 0x8 ) != 0;
        m_h_sz_global_ct = f & 0x7;
        m_n_gc_colors = 2 << m_h_sz_global_ct;

        //-- If a global color table is present- read it here,
        if( m_h_has_global_ct ) {
            byte[] gct = new byte[m_n_gc_colors * 3];	// Create holding area
            readBytes( gct, m_n_gc_colors * 3 );			// Read entire table,

            //-- Now make the RGB arrays from this,
            m_gc_reds = new byte[m_n_gc_colors];
            m_gc_grns = new byte[m_n_gc_colors];
            m_gc_blus = new byte[m_n_gc_colors];

            //-- Read the table,
            int j = 0;
            for( int i = 0; i < m_n_gc_colors; i++ ) {
                m_gc_reds[i] = gct[j++];
                m_gc_grns[i] = gct[j++];
                m_gc_blus[i] = gct[j++];
            }
        }
//		dbg("End of GLOBAL data");
        m_has_header = true;
    }

    /// The global color table entries
    private byte[] m_gc_reds, m_gc_blus, m_gc_grns;

    public int m_n_gc_colors;

    public boolean m_h_sorted;

    public int m_h_sz_global_ct;

    /// T if this has a global color table.
    public boolean m_h_has_global_ct;

    public int m_h_color_res;

    /// Pixel aspect ratio
    public byte m_h_pxaspect;

    /// Background color index
    public byte m_h_bgindex;


    /**
     * Called when a read has failed or has completed, this release ALL non-
     * permanent resources.
     */
    private void cleanupRead() {
        m_h = null;
    }


    /**
     * Reads the specified image number from the GIF, creating a BufferedImage
     * of the specified type.
     */
    public BufferedImage read( int bufimatype ) throws IOException {
        try {
            readMainHeader();				// Make sure the main header has been read,
            readImageMeta();				// Read image metadata & move to compressed,
            createHandler( bufimatype );		// Create the most optimal pixel writer
            runDecompressor();				// Initialize the compression stuff
            return m_bi;					// Return the image just read,
        }
        catch( IOException x ) {
            //-- Release all non-pertinent data,
            m_bi = null;
            m_im = null;
            throw x;
        }
        finally {
            cleanupRead();
        }
    }


    /**
     * Creates the most optimal image writer for the BufferedImage's type.
     */
    private void createHandler( int bufimatype ) throws IOException {
        switch( bufimatype ) {
            default:
                throw new IllegalArgumentException( "Unsupported buffered image type" );

            case BufferedImage.TYPE_BYTE_INDEXED:
                m_h = new GifIndexedHandler( this, m_im, bufimatype );
                break;
        }

        m_bi = m_h.prepare();					// Prepare the read,
        if( m_bi == null ) {
            throw new IOException( "Unexpected format of recognised BufferedImage type!?" );
        }
    }


    /**
     * Called when at the start of a new image, this reads all image meta data,
     * and returns when the stream has reached the compressed pixels.
     */
    private void readImageMeta() throws IOException {
        m_im = new GifImaMeta();				// Allocate new metadata

        //-- For all non-image-data blocks preceding the image do...
        for( ; ; ) {
            int blocktype = rdUByte();
            switch( blocktype ) {
                default:
                    throw new IOException( "AnimGifDecoder: unexpected block type " + Integer.toString( blocktype, 16 ) );

                case 0x2c:
                    //-- Image descriptor.
                    readImageDescriptor();
                    return;						// Compressed data follows this!

                case 0x21:
                    readExtensionBlock();
                    break;
            }
        }
    }


    /**
     * Read the image descriptor block. When done the current file position is
     * at the image's compressed data.
     */
    private void readImageDescriptor() throws IOException {
//		dbg("Reading Image Descriptor");

        //-- Read position and size of the image
        m_im.m_bx = rdUShort();				// bx of image,
        m_im.m_by = rdUShort();
        m_im.m_w = rdUShort();
        m_im.m_h = rdUShort();

        int pf = rdUByte();					// Get packed fields
        m_im.m_haslocalcolortable = ( pf & 0x80 ) != 0;		// Has a local color table
        m_im.m_interlaced = ( pf & 0x40 ) != 0;		// Image is interlaced
        m_im.m_sorted = ( pf & 0x20 ) != 0;		// Color table is sorted
        m_im.m_bits_colortable = ( pf & 0x7 );			// Size indicator of LCT

        if( m_im.m_haslocalcolortable ) {
            //-- Read the local color table,
            int nb = m_im.m_bits_colortable + 1;
            m_im.m_sz_colortable = 1 << nb;
            byte[] gct = new byte[3 * m_im.m_sz_colortable];
            readBytes( gct, m_im.m_sz_colortable * 3 );

            //-- Fill the RGB arrays,
            m_im.m_reds = new byte[m_im.m_sz_colortable];
            m_im.m_grns = new byte[m_im.m_sz_colortable];
            m_im.m_blus = new byte[m_im.m_sz_colortable];

            //-- Read the table,
            int j = 0;
            for( int i = 0; i < m_im.m_sz_colortable; i++ ) {
                m_im.m_reds[i] = gct[j++];
                m_im.m_grns[i] = gct[j++];
                m_im.m_blus[i] = gct[j++];
            }
        }
        else {
            //-- Copy global color table entries
            m_im.m_sz_colortable = m_n_gc_colors;
            m_im.m_reds = m_gc_reds;
            m_im.m_blus = m_gc_blus;
            m_im.m_grns = m_gc_grns;

            m_im.m_bits_colortable = m_h_sz_global_ct;
        }

        //-- Now positioned at start of LZW data
    }


    /**
     * Reads any kind of EXTENSION block, or skips the block if it's not known.
     */
    private void readExtensionBlock() throws IOException {
        int len = 0;
        int label = rdUByte();
        switch( label ) {
            default:
                //-- Skip unknown extension,
                dbg( "Skip unknown extension label=" + Integer.toString( label, 16 ) );
                do {
                    len = rdUByte();				// Read block size,
                    skipBytes( len );					// Pass these soonest ;-)
                } while( len > 0 );
                return;								// And be done!

            case 0xf9:
                {
                    //-- Graphics Control Extension
//					dbg("Graphics Control Extension found");
                    len = rdUByte();
                    int pf = rdUByte();			// Get packed fields,
                    m_im.m_disposalmethod = ( pf >> 2 ) & 0X3;
                    m_im.m_userinputflag = ( pf & 0x2 ) != 0;
                    m_im.m_transparant = ( pf & 0x1 ) != 0;
                    m_im.m_delaytime = rdUShort();
                    m_im.m_transparant_ix = rdUByte();
                    rdUByte();						// Read terminator

                }
                return;
        }
    }


    private int m_out;

    /**
     * Called by the decompressor when it has found a setCoefficient of pixels. This method
     * must take care of writing the pixels to the buffer.
     */
    private void pixels( byte[] ar, int len ) throws IOException {
        m_h.pixels( ar, len );
        m_out += len;

    }

    /**
     * Called when the current compressed data has reached EOF.
     */
    private void imageEof() {
        int x = ( m_im.m_h * m_im.m_w );
        if( x == m_out ) {
            return;
        }

        System.out.println( "Total bytes output is " + m_out );
        System.out.println( "Total bytes expected was " + ( m_im.m_h * m_im.m_w ) );
    }


    /*--------------------------------------------------------------*/
    /*	CODING:	Decompressor...										*/
    /*--------------------------------------------------------------*/
    /// Decompressor: Initial LZH code size,
    private int m_initcodesize;

    /// The input-file's block buffer (max. 255 bytes)
    private byte[] m_block;

    /// The size of the current block in the block buffer,
    private int m_blocklen;

    /// The current index in the m_block data buffer.
    private int m_block_ix;

    /// The 32-bits bitshift code buffer.
    private int m_32bits;

    /// The current BIT position within the 32bit buffer?
    private int m_bitpos;

    /// T if last block was read from file
    private boolean m_lastblock;

    /// The clear code for the curren bit depth
    private int m_clearcode;

    /// The EOFcode for the current bit depth
    private int m_eofcode;

    /// The current code size, code mask and code end code.
    private int m_codesize, m_codemask, m_codeend;


    /**
     * The decompressor works by scanning the image data and expanding the LZW
     * data into a byte[] buffer. When some byte(s) have been expanded the byte
     * buffer is passed to the appropriate handler. The handler depends on the
     * BufferedImage type and handles the writing of pixels in the most
     * optimal way.
     * The GifImageHandler is a base class that knows how to move from pixel to
     * pixel. Derived classes know how to write every BufferedImage type that
     * is supported. Since each derived class knows the exact format of the
     * databuffer e.a. it can write the data as quick as possible without
     * using the generisized pixel put methods..
     * <p/>
     * <p/>
     * Runs the data decompressor, calling the image writer for each setCoefficient of
     * bytes obtained from the image.
     */
    private void runDecompressor() throws IOException {
        m_out = 0;								// DEBUG

        m_initcodesize = rdUByte();				// Get initial code size,

        //-- Prepare to read the 1st data block,
        m_block = new byte[255];					// One complete block of data,
        m_blocklen = rdUByte();
        readBytes( m_block, m_blocklen );				// Read one complete block
        m_lastblock = false;
        m_bitpos = 0;
        init32Bits();								// Init the 32bit buffer,
        m_clearcode = 1 << m_initcodesize;
        m_eofcode = m_clearcode + 1;

        int code, oldcode = 0;
        int[] prefix = new int[4096];
        byte[] suffix = new byte[4096];
        byte[] initial = new byte[4096];
        int[] length = new int[4096];
        byte[] buf = new byte[4096];			// Output code buffer (decompressed)

        initStringTable( prefix, suffix, initial, length );	// All empty tables,

        int tableix = ( 1 << m_initcodesize ) + 2;
        m_codesize = m_initcodesize + 1;
        m_codeend = ( 1 << m_codesize );
        m_codemask = m_codeend - 1;

        for( ; ; ) {
            code = getCode();
            if( code == m_clearcode )							// Clear the string tables & start anew??
            {
                //-- Reset the code table
                initStringTable( prefix, suffix, initial, length );
                tableix = ( 1 << m_initcodesize ) + 2;
                m_codesize = m_initcodesize + 1;
                m_codeend = ( 1 << m_codesize );
                m_codemask = m_codeend - 1;

                //-- And get the next code.
                code = getCode();
                if( code == m_eofcode ) {
                    imageEof();
                    return;
                }
                /*
                 *	?? Is this correct?? It misses every code output after a
                 *	clear code????
                 */
            }
            else if( code == m_eofcode ) {
                //-- The image data is complete!!
                imageEof();
                return;
            }
            else {
                int newsufindex;

                if( code < tableix ) {
                    newsufindex = code;
                }
                else {
                    // Code == tableix
                    newsufindex = oldcode;
                    if( code != tableix ) {
                        //-- Code out of sequence..
                        dbg( "Out-of-sequence code" );
                    }
                }

                //-- Construe a new table entry,
//				int	oc	= oldcode;

                prefix[tableix] = oldcode;
                suffix[tableix] = initial[newsufindex];
                initial[tableix] = initial[oldcode];
                length[tableix] = length[oldcode] + 1;

                tableix++;

                //-- Have we reached the max code possible for this codesize?
                if( ( tableix == m_codeend ) && ( tableix < 4096 ) ) {
                    m_codesize++;
                    m_codeend = ( 1 << m_codesize );
                    m_codemask = m_codeend - 1;
                }
            }

            //-- Now write the code-sequence from the table,
            int c = code;
            int len = length[c];
            for( int i = len - 1; i >= 0; i-- ) {
                buf[i] = suffix[c];
                c = prefix[c];
            }

            pixels( buf, len );					// Write pixels to output medium,
            oldcode = code;
        }
    }


    /**
     * Gets the next code from the buffers. If the current block buffer is
     * exhausted the next buffer is read.
     */
    private int getCode() throws IOException {
        if( m_bitpos + m_codesize > 32 ) {
            return m_eofcode;// No more data!
        }
        int code = ( m_32bits >> m_bitpos ) & m_codemask;	// Get the current code
        m_bitpos += m_codesize;

        //-- Now shift in new data into the 32bit buffer..
        while( m_bitpos >= 8 && !m_lastblock ) {
            m_32bits >>>= 8;							// Shift out used byte,
            m_bitpos -= 8;

            //-- Is the current block completely used?
            if( m_block_ix >= m_blocklen )				// All bytes in block used?
            {
                //-- Get next block,
                m_blocklen = rdUByte();				// Get next block's size,
                if( m_blocklen == 0 )						// Size 0 == no more data
                {
                    m_lastblock = true;					// No more data in file!
                    return code;
                }
                readBytes( m_block, m_blocklen );			// Read entire block,
                m_block_ix = 0;						// And start at byte 0,
            }

            m_32bits |= m_block[m_block_ix++] << 24;	// Shift in new byte at the top
        }
        return code;
    }


    /**
     * Initializes the LZH tables.
     */
    private void initStringTable( int[] prefix, byte[] suffix, byte[] init, int[] length ) {
        int numentries = 1 << m_initcodesize;
        for( int i = 0; i < numentries; i++ ) {
            prefix[i] = -1;
            suffix[i] = (byte)i;
            init[i] = (byte)i;
            length[i] = 1;
        }

        //-- Fill the rest of the table to be prepared against out-of-sequence codes,
        for( int i = numentries; i < 4096; i++ ) {
            prefix[i] = -1;
            length[i] = 1;
        }
    }

    /**
     * Initializes the 32-bits buffer. It fills the m_32bits buffer with the
     * first 4 bytes of the m_block buffer.
     */
    private void init32Bits() {
        m_32bits = m_block[0] & 0xff;
        m_32bits |= ( m_block[1] & 0xff ) << 8;
        m_32bits |= ( m_block[2] & 0xff ) << 16;
        m_32bits |= m_block[3] << 24;
        m_block_ix = 4;
    }

}



