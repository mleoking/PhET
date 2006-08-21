import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Applet to show holes/electrons moving through a lattice.
 * <p/>
 * This simulation is based on a probabilistic model devised solely to
 * &quot;give a feel for&quot; the real thing. Don't take it too seriously.
 */
public class HoleApplet extends BaseApplet2 {
    public void init() {
        super.init();
        lattice = new HoleLattice( 8, 8 );
        smoothAnimBox = addCheckbox( "Show electrons moving", false );
    }

    public void start() {
        super.start();
        startAnim();
    }

    public void stop() {
        stopAnim();
        super.stop();
    }

    public void actionPerformed( ActionEvent ae ) {
        lattice.setSmooth( smoothAnimBox.getState() );
    }

    public void doPaint( Graphics g ) {
        int w = getSize().width;
        int h = getSize().height; //getGraphHeight();

        lattice.paint( g, w, h );
    }


    /**
     * Requests that the animation be started.  Has no effect if an animation
     * is already running.  The applet may, at its discretion, use a call to
     * this method to &quot;cancel out&quot; a call to {@link #stopAnim()
     * stopAnim()}, if the thread has not yet responded to the latter.
     */
    //	To achive that, move the "killAnim = false" to the top of the method.
    public synchronized void startAnim() {
        if( animThread != null ) {
            return;
        }

        killAnim = false;

        animThread = new Thread() {
            public void run() {
                setPriority( Thread.MIN_PRIORITY );

                while( !killAnim ) {
                    lattice.spawnHoles();
                    lattice.advanceHoles();
                    refresh();
                    try {
                        Thread.sleep( delay );
                    }
                    catch( InterruptedException e ) {
                    }
                }

                synchronized( this ) {
                    animThread = null;
                }
            }
        };

        animThread.start();
    }


    /**
     * Requests that the animation be stopped at the next convenient
     * moment.
     */
    public synchronized void stopAnim() {
        killAnim = true;
        if( animThread != null ) {
            animThread.interrupt();
        }
    }


    /**
     * The time delay between logic cycles (milliseconds)
     */
    protected int delay = 10;
    /**
     * The model used.  This could potentially be exchanged for an accurate
     * model.
     */
    protected HoleLattice lattice;
    /**
     * The thread performing the animation, or <code>null</code> if no
     * animation is in progress.
     */
    protected Thread animThread;
    /**
     * <code>true</code> if the animation should halt at the next
     * opportunity, else <code>false</code>.
     */
    protected boolean killAnim = false;
    /**    Checkbox for the user to choose between smooth animation (electrons
     can be seen moving), or instant changes (hole disappears from one
     place, appears in another).
     */
    protected Checkbox smoothAnimBox;
}