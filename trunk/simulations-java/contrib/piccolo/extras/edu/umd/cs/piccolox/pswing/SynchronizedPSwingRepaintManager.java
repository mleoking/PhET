package edu.umd.cs.piccolox.pswing;

import javax.swing.*;

/**
 * This is an experimental class, and not ready for general use.  The purpose is to reuse as much of swing's original RepaintManager as possible,
 * while gaining more control over the exact sequence of rendering.
 * <p/>
 * This is done by overriding the calls made by RepaintManager's calls SystemEventQueueUtilities.queueComponentWorkRequest(root);
 * <p/>
 * "Queues a Runnable that calls RepaintManager.validateInvalidComponents()
 * and RepaintManager.paintDirtyRegions() with SwingUtilities.invokeLater()."
 * <p/>
 * These calls then must be scheduled by the application explicitly.
 */
public class SynchronizedPSwingRepaintManager extends PSwingRepaintManager {
    private boolean synchronousPaint = true;

    public void validateInvalidComponents() {
    }


    public boolean isSynchronousPaint() {
        return synchronousPaint;
    }

    public void setSynchronousPaint( boolean synchronousPaint ) {
        this.synchronousPaint = synchronousPaint;
    }

    public void paintDirtyRegions() {
    }

    public void update() {
        //super.validateInvalidComponents();
        //super.paintDirtyRegions();
        super.doUpdateNow();
    }

    public void updateLater() {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                update();
            }
        } );
    }

}
