package jass.render;

/**
 Utility class to convert sample types.
 @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public final class FormatUtils {
    /** Endianness. */
    public static boolean bigEndian = false;

    /** Convert short array to float array.
     @param buf User provided float array to return result in.
     @param sbuf User provided sort array to convert.
     @param bufsz Sumber of float samples to write.
     */
    static final public void shortToFloat(float[] buf, short[] sbuf, int bufsz) {
        for (int i = 0; i < bufsz; i++) {
            buf[i] = (float) (sbuf[i] / 32768.);
        }
    }

    /** Convert float array (in range [-1 1]) to short array.
     @param shortSound User provided short array to return result in.
     @param dbuf User provided float array to convert.
     */
    static final public void floatToShort(short[] shortSound, float[] dbuf) {
        int bufsz = dbuf.length;
        for (int i = 0; i < bufsz; i++) {
            shortSound[i] = (short) (32767. * dbuf[i]);
        }
    }

    /** Convert float array to byte array.
     @param byteSound User provided byte array to return result in.
     @param dbuf User provided float array to convert.
     */
    static final public void floatToByte(byte[] byteSound, float[] dbuf) {
        int bufsz = dbuf.length;
        int ib = 0;
        if (bigEndian) {
            for (int i = 0; i < bufsz; i++) {
                short y = (short) (32767. * dbuf[i]);
                byteSound[ib] = (byte) (y >> 8);
                ib++;
                byteSound[ib] = (byte) (y & 0x00ff);
                ib++;
            }
        } else {
            for (int i = 0; i < bufsz; i++) {
                short y = (short) (32767. * dbuf[i]);
                byteSound[ib] = (byte) (y & 0x00ff);
                ib++;
                byteSound[ib] = (byte) (y >> 8);
                ib++;
            }
        }
    }

    /** Convert byte array to float array.
     @param dbuf User provided float array to return result in.
     @param dbuf User provided byte array to convert.
     @param bufsz Sumber of float samples to write.
     */
    static final public void byteToFloat(float[] dbuf, byte[] bbuf, int bufsz) {
        int ib = 0;
        if (bigEndian) {
            for (int i = 0; i < bufsz; i++) {
                short y = bbuf[ib];
                y = (short) (y << 8);
                y += bbuf[ib + 1];
                ib += 2;
                dbuf[i] = y / 32768.f;
            }
        } else {
            for (int i = 0; i < bufsz; i++) {
                short y = (short) bbuf[ib + 1];
                y = (short) (y << 8);
                y += bbuf[ib];
                ib += 2;
                dbuf[i] = y / 32768.f;
            }
        }
    }

}


