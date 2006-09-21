package smooth;

/**
 * Generic interface for a smooth look and feel.
 *
 * @author James Shiell
 * @version 1.0
 */
public interface SmoothLookAndFeel {

    public static final String SMOOTH_BASIC = "smooth.basic.";

    /**
     * Turn anti-aliasing on or off.
     *
     * @param on true to turn AA on or false for off.
     */
    public void setAntiAliasing( boolean on );

    /**
     * Retrieve the current status of AA.
     *
     * @return true if anti-aliasing, false otherwise.
     */
    public boolean isAntiAliasing();

}
