package edu.colorado.phet.colorvision;

import java.util.ArrayList;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User Another Guy
 * Date Feb 21, 2004
 * Time 64443 AM
 * To change this template use File | Settings | File Templates.
 */
public class PhotonBeam implements ColorListener {

    private boolean running;
    // The x coord at which photons are deleted
//	private int xCutoff;
    private double rate;
    // NOTE!!! The following line results in the Array being allocated
    // statically to the class!!! I have to allocate it in the constructor;
    //	private photonsArray = new Array();
    private ArrayList photons = new ArrayList();
    private double rateAtEyeball;
    private int xLoc;
    private int yLoc;
    private double theta;
//	private double color;
    private int alpha;
    private double wavelength;
//	private Number intervalID;
    private Color color;


    public PhotonBeam() {
        rate = 0;
        alpha = 100;
        rateAtEyeball = 0;
        running = false;
//		xCutoff = _root.head._x;
    }


    public void setLocation(int xLoc, int yLoc) {
        this.xLoc = xLoc;
        this.yLoc = yLoc;
    }

    public void setTheta(double theta) {
        this.theta = theta;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

//	public void setColor(int color) {
//		this.color = color;
//		ColorUtil.ColorTransform ctx = ColorUtil.colorToCtx(color);
//		this.wavelength = ColorUtil.ctxToWavelength(ctx);
//		if (color == 0xFFFFFF) {
//			this.wavelength = 0;
//		}
//	}

    public void setWavelength(double wavelength) {
        this.wavelength = wavelength;
        ColorUtil.ColorTransform ctx = ColorUtil.getCtx(wavelength);
//		this.color = ColorUtil.ctxToColor(ctx);
        this.color = new Color(ctx.rb, ctx.gb, ctx.bb);
    }

    /**
     * Get a photon to a given specification.
     */
    private Photon allocatePhoton(int xLoc, int yLoc, double theta,
                                  double wavelength, double rate) {
        Photon p = null;
        // Look through the photon records we already have to see if there is one that is not in use
        for (int i = 0; i < photons.size() && p == null; i++) {
            if (!((PhotonGeneratorRecordEntry) photons.get(i)).isInUse()) {
                p = ((PhotonGeneratorRecordEntry) photons.get(i)).getPhoton();
                p.setLocation(xLoc, yLoc);
                p.setWavelength(wavelength);
                ((PhotonGeneratorRecordEntry) photons.get(i)).setRate(rate);
                ((PhotonGeneratorRecordEntry) photons.get(i)).setInUse(true);
                p.setIsVisible(true);
            }
        }
        // If we didn't find an unused photon that is not in use, allocate one
        if (p == null) {
            p = new Photon(xLoc, yLoc, theta, wavelength);
            photons.add(new PhotonGeneratorRecordEntry(p, rate));
        }
        return p;
    }

    public void paint(Graphics g) {
//	public void onEnterFrame() {

        g.clearRect(0, 0, this.width, this.height );
//        this.clear();
        if (running == true) {
            double wl;
            Photon p;
            for (int n = 0; n < Math.round(rate); n++) {
                if (this.wavelength == 0) {
                    wl = Math.random() * (Config.maxWavelength - Config.minWavelength) + Config.minWavelength;
                } else {
                    wl = this.wavelength;
                }
                //				p = new Photon(xLoc, yLoc, genTheta(theta * Math.PI / 180), wl);
                //				photons.push(new PhotonGeneratorRecordEntry(p, rate));
                p = allocatePhoton(xLoc, yLoc, genTheta(theta * Math.PI / 180), wl, rate);
            }
            // Paint or prune the photons, as need be
            boolean somePhotonInUse = false;
            for (int i = 0; i < photons.size(); i++) {
                if (((PhotonGeneratorRecordEntry) photons.get(i)).isInUse()) {
                    somePhotonInUse = true;
                    p = ((PhotonGeneratorRecordEntry) photons.get(i)).getPhoton();
                    if (p != null && p.getX() <= Config.xCutoff) {
//                  if (p != null && p.getX() <= Config.head._x) {
                        p.paint(g);
//						p.paint(this);
                    } else {
                        ((PhotonGeneratorRecordEntry) photons.get(i)).setInUse(false);
                        this.rateAtEyeball = ((PhotonGeneratorRecordEntry) photons.get(i)).getRate();
                        //					photons.splice(i, 1);
                        //					Photon.deleteInstance(p);
                    }
                }
            }
            //			if (photons.length == 0) {
            if (!somePhotonInUse) {
                this.rateAtEyeball = 0;
            }
        }
    }

    public void stop() {
        running = false;
//		super.stop();
    }

    public void start() {
        running = true;
    }

    public boolean isRunning() {
        return running;
    }

    public void activate() {
//		super.activate();
        this.start();
    }

    public void deactivate() {
//		super.deactivate();
        this.stop();
    }

    public double getMaxRate() {
        return 8;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getRateAtEyeball() {
        return this.rateAtEyeball;
    }

    private double genTheta(double theta0) {
        double d_theta = Math.random() * Math.PI / 16 - Math.PI / 32;
        double angle = theta0 + d_theta;
        return angle;
    }

    public void colorChanged(double wavelength) {
        this.setWavelength(wavelength);
    }


    class PhotonGeneratorRecordEntry {
        private Photon p;
        private double rate;
        private boolean inUse;

        PhotonGeneratorRecordEntry(Photon p, double rate) {
            this.p = p;
            this.rate = rate;
            this.inUse = true;
        }

        Photon getPhoton() {
            return this.p;
        }

        double getRate() {
            return this.rate;
        }

        void setRate(double rate) {
            this.rate = rate;
        }

        void setInUse(boolean inUse) {
            this.inUse = inUse;
        }

        boolean isInUse() {
            return this.inUse;
        }
    }
}
