/**
 * Thread designed to animate a graph.  The parameter <code>t</code> of
 * some function is incremented repeatedly, while the {@link BaseApplet#refresh() refresh()} method of
 * a <code>BaseApplet</code> is called.
 *
 * @see BaseApplet
 */
class AnimThread implements Runnable {
    /**
     * @param fn     The function whose parameter should be varied
     * @param applet The applet which should be refreshed
     */
    public AnimThread( TimeDependent fn, BaseApplet applet ) {
        this.fn = fn;
        this.applet = applet;
        this.singleStep = false;
        if( singleStep ) {
            System.err.println( "AnimThread created - operating in single step mode" );
        }
    }

    /**
     * Performs the work of the thread.  This method should not be executed
     * directly, but via the {@link Thread#start() start()} method.
     */
    public void run() {
        t = fn.getT();
        try {
            while( !killed ) {
                if( fn instanceof TimeDepSchrodinger ) {
                    double d = ( (TimeDepSchrodinger)fn ).integralCheck();
                    ( (TimeDep)applet ).statusLabel.setText( "Integral:  " + d );
                }
                if( singleStep ) {
                    kill();
                }
                t += delay * speed / 1000;
                while( !killed && t > fn.getT() ) {
                    fn.advanceOnce();
                }
                applet.refresh();
                Thread.sleep( delay );
            }
        }
        catch( InterruptedException e ) {
        }
        thr = null;
    }

    /**
     * @return        <code>true</code> if the thread has started, else
     * <code>false</code>
     */
    public boolean hasStarted() {
        return t != 0;
    }

    /**
     * Begins execution in a separate thread.  If the thread was previously
     * {@link #kill() kill()}ed but has not yet terminated, then that thread
     * may, (at the discretion of the implementation) ignore the kill()
     * request.
     */
    public synchronized void start() {
        if( thr != null ) {
            return;
        }
        killed = false;
        ( thr = new Thread( this ) ).start();
    }

    /**
     * @return        <code>true</code> if the thread is currently running,
     * else <code>false</code>
     */
    public boolean isRunning() {
        return thr != null;
    }

    /**
     * Signals that the thread should stop running at the next convenient
     * moment.  The thread may increment the time parameter and/or refresh
     * the applet a maximum of one more time before halting.
     */
    public void kill() {
        killed = true;
        try {
            thr.interrupt();
        }
        catch( NullPointerException e ) {
        }
    }


    /**
     * Should be considered deprecated and non-functional; will be removed
     * shortly.
     */
    public void setRange( double min, double max ) {
    }

    /**
     * Sets the ratio between simulation time and real time.
     *
     * @param    speed    The simulation time increment per real second
     */
    public void setSpeed( double speed ) {
        this.speed = speed;
    }


    /**
     * @param    delay    The length of real time (in milliseconds) between each
     * frame of animation
     */
    public void setRefreshDelay( int delay ) {
        this.delay = delay;
    }


    protected Thread thr;
    protected double t, speed;
    protected int delay;
    protected boolean killed, singleStep;
    protected TimeDependent fn;
    protected BaseApplet applet;
}