/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.model.clock;

import junit.framework.TestCase;

import java.awt.*;
import java.awt.event.InputEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ZAbstractClockTester extends TestCase {

    public static interface ClockFactory{
        Clock createInstance(int defaultDelay, double v);
    }
    private static final int DEFAULT_DELAY = 10;
    
    private volatile Clock threadClock;
    private volatile MockClockListener clockListener;

    private final ClockFactory factory;

    public ZAbstractClockTester(ClockFactory factory) {
        this.factory = factory;
    }

    public void setUp() {
        this.clockListener = new MockClockListener();
        this.threadClock   = factory.createInstance(DEFAULT_DELAY, 1.0);
        this.threadClock.addClockListener(clockListener);

        threadClock.start();
    }

    public void tearDown() {
        this.threadClock.stop();
    }

    public void testThatClockListenerInvokedAtRegularIntervals() throws InterruptedException {
        while(clockListener.ticked < 4) {
            Thread.yield();
        }
    }

    public void testThatClockListenerInvokedAtSpecifiedIntervals() throws InterruptedException {
        double averageDelay = getAverageDelayBetweenTicks(100);

        System.out.println("average delay = " + averageDelay);

        assertEquals(DEFAULT_DELAY, averageDelay, 2);
    }

    public void testThatTicksAreCoalescedWhenListenerTakesMoreTimeThanDelay() {
        clockListener.maxDelay = DEFAULT_DELAY + DEFAULT_DELAY / 2;

        double averageDelay = getAverageDelayBetweenTicks(100);

        assertEquals(15.0, averageDelay, 2);
    }

    public void testThatSwingRunnablesAreProcessedWhenListenerTakesMoreTimeThanDelay() {
        clockListener.maxDelay = DEFAULT_DELAY + DEFAULT_DELAY / 2;

        while ( clockListener.ticked < 1 ) {
            Thread.yield();
        }

        final boolean[] invoked = new boolean[]{false};

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                invoked[0] = true;
            }
        });

        long start = System.currentTimeMillis();

        while ( !invoked[0] ) {
            Thread.yield();

            if ( System.currentTimeMillis() - start > 500 ) {
                assertFalse(false);
            }
        }
    }

    public void testThatInputEventsAreProcessedWhenListenerTakesMoreTimeThanDelay() throws AWTException {
        JFrame frame = new JFrame();
        frame.setSize(100, 100);
        frame.setLocation(0, 0);
        JButton button = new JButton();
        button.setSize(100, 100);
        frame.setContentPane(button);
        frame.setSize( 200,200);
        frame.setVisible(true);

        Robot robot = new Robot();

        clockListener.maxDelay = DEFAULT_DELAY + DEFAULT_DELAY / 2;

        while ( clockListener.ticked < 1 ) {
            Thread.yield();
        }

        final boolean[] invoked = new boolean[]{false};

        button.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent changeEvent) {
                invoked[0] = true;
            }
        });

        robot.mouseMove(50, 60);
        robot.mousePress(InputEvent.BUTTON1_MASK);

        long start = System.currentTimeMillis();

        while ( !invoked[0] ) {
            Thread.yield();

            if ( System.currentTimeMillis() - start > 500 ) {
                assertFalse(false);
            }
        }
        robot.mouseRelease( InputEvent.BUTTON1_MASK );
    }

    private double getAverageDelayBetweenTicks(int maxTicks) {
        long start = System.currentTimeMillis();

        while ( clockListener.ticked < maxTicks ) {
            Thread.yield();
        }

        threadClock.stop();

        long end = System.currentTimeMillis();

        return (end - start) / (double)clockListener.ticked;
    }

    private static class MockClockListener implements ClockListener {
        int ticked = 0;
        int maxDelay = 0;

        public void clockTicked(ClockEvent clockEvent) {
            if (maxDelay > 0) {
                try {
                    Thread.sleep(maxDelay);
                }
                catch (InterruptedException e) {
                }
            }
            
            ++ticked;
        }

        public void clockStarted(ClockEvent clockEvent) {
        }

        public void clockPaused(ClockEvent clockEvent) {
        }

        public void simulationTimeChanged(ClockEvent clockEvent) {
        }

        public void simulationTimeReset(ClockEvent clockEvent) {
        }
    }
}
