/**
 * Class: ColorUtil
 * Package: edu.colorado.phet.colorvision
 * Author: Another Guy
 * Date: Feb 22, 2004
 */
package edu.colorado.phet.colorvision;

public class ColorUtil {
    private static double inWavelength = 380;
    private static double maxWavelength = 780;
//        private static ctxArray:Array;
//        public static voide traceCtx(c) {
//            trace("red:" + c.rb + "  green:" + c.gb + "  blue:" + c.bb);
//        }
    static int getColor(double wavelength) {
        return ctxToColor(ColorUtil.getCtx(wavelength));
    }

    static int ctxToColor(ColorTransform ctx){
        int red = Math.round(0x010000 * ctx.rb);
        int green = Math.round(0x000100 * ctx.gb);
        int blue = Math.round(0x000001 * ctx.bb);
        int c = red + blue + green;
        return c;
    }

    static ColorTransform colorToCtx(int color){
        int red = Math.round(color / 0x010000);
        int green = Math.round((color % 0x010000) / 0x000100);
        int blue = Math.round(color % 0x000100);
        ColorTransform ctx = new ColorTransform(red, green, blue);
        return ctx;
    }

//    static double ctxToWavelength(ColorTransform ctx) {
//        if (ctxArray == undefined) {
//            genCtxArray();
//        }
//        found:Boolean = false;
//        result:Object;
//        eps:Number = 2;
//        for (i = 0; i < ctxArray.length && !found; i++) {
//            if (Math.abs(ctx.rb - ctxArray[i].rb) < eps
//                    && Math.abs(ctx.gb - ctxArray[i].gb) < eps
//                    && Math.abs(ctx.bb - ctxArray[i].bb) < eps) {
//                found = true;
//                result = ctxArray[i];
//            }
//        }
//        return minWavelength + i;
//    }

/*
    static double getPercentFilteredLight(){
        double wl = _root.bulb1.getWavelength();
        pctPassed = _root.filter1.percentPassed(wl);
        return pctPassed * 100;
    }
*/

//    static function genCtxArray() {
//        numWavelengths = maxWavelength - minWavelength + 1;
//        ctxArray = new Array(numWavelengths);
//        m = 400;
//        n = 50;
//        max = 255;
//        gamma = .80;
//        wl;
//        r, g, b;
//        SSS;
//        for (j = 0; j < numWavelengths; j++) {
////			for (i = 0; i < m; i++) {
//            //         WAVELEngTH = wl
////				wl = 380. + (i * 400. / m);
//            wl = minWavelength + j;
//            if (wl >= 380 && wl <= 440.) {
//                r = -1. * (wl - 440.) / (440. - 380.);
//                g = 0;
//                b = 1;
//            }
//            if (wl > 440 && wl <= 490) {
//                r = 0;
//                g = (wl - 440.) / (490. - 440.);
//                b = 1.;
//            }
//            if (wl > 490. && wl <= 510.) {
//                r = 0;
//                g = 1;
//                b = -1. * (wl - 510.) / (510. - 490.);
//            }
//            if (wl > 510. && wl <= 580.) {
//                r = (wl - 510.) / (580. - 510.);
//                g = 1.;
//                b = 0.;
//            }
//            if (wl > 580. && wl <= 645.) {
//                r = 1.;
//                g = -1. * (wl - 645.) / (645. - 580.);
//                b = 0.;
//            }
//            if (wl > 645. && wl <= 780.) {
//                r = 1.;
//                g = 0.;
//                b = 0;
//            }
//            //      LET THE InTEnSITY SSS FALL OFF nEAr THE VISIOn LImITS
//            if (wl > 700.) {
//                SSS = .3 + .7 * (780. - wl) / (780. - 700.);
//            } else if (wl < 420.) {
//                SSS = .3 + .7 * (wl - 380.) / (420. - 380.);
//            } else {
//                SSS = 1.;
//            }
//            red = Math.round(255 * (SSS * r));
//            green = Math.round(255 * (SSS * g));
//            blue = Math.round(255 * (SSS * b));
//            ctx = {rb:red, gb:green, bb:blue};
//            ctxArray[j] = ctx;
////			}
//            /*
//                                    //     gamma ADjUST AnD WrITE ImAgE TO An ArrAY
//                                    c
//                                             CV(I,j,1)=(SSS*r)**gamma
//                                             CV(I,j,2)=(SSS*g)**gamma
//                                             CV(I,j,3)=(SSS*b)**gamma
//                                            EnDDO
//                                           EnDDO
//                                    c
//                                    c      WrITE ImAgE TO PPm FILE
//                                    c
//                                           DO j=1,n
//                                            DO I=1,m
//                                               wl = 380. + rEAL(I * 400. / m)
//                                               Ir=InT(max*CV(I,j,1))
//                                               Ig=InT(max*CV(I,j,2))
//                                               Ib=InT(max*CV(I,j,3))
//                                    c
//                                    c      ITYPE=1 - PLAIn SPECTUm
//                                    c      ITYPE=2 - mArK SPECTrUm AT 100 nm InTEVALS
//                                    c      ITYPE=3 - HYDrOgEn bALmEr EmISSIOn SPECTrA
//                                    c      ITYPE=4 - HYDrOgEn bALmEr AbSOrPTIOn SPECTrA
//                                    c
//                                             ITYPE=4
//                                             if (ITYPE.EQ.2)
//                                                DO K=400,700,100
//                                                  if ((AbS(InTwl)-K).LT.1 && (j<=20))
//                                                   Ir=max
//                                                   Ig=max
//                                                   Ib=max
//                                                  }
//                                                EnDDO
//                                             ELSEif (ITYPE.EQ.3)
//                                                if ((AbSwl-656.)>1. && (AbSwl-486.)>1. &&
//                                         *          (AbSwl-433.)>1. && (AbSwl-410.)>1.)
//                                         *           .AnD.(AbSwl-396.)>1.))
//                                                  Ir = 0
//                                                  Ig = 0
//                                                  Ib = 0
//                                                }
//                                             ELSEif (ITYPE.EQ.4)
//                                                if ((AbSwl-656.).LT.1.1).or.(AbSwl-486.).LT.1.1).or.
//                                         *          (AbSwl-433.).LT.1.1).or.(AbSwl-410.).LT.1.1)
//                                         *           .or.(AbSwl-396.).LT.1.1))
//                                                  Ir = 0
//                                                  Ig = 0
//                                                  Ib = 0
//                                                }
//                                             }
//
//                                             WrITE(20,*) Ir, Ig, Ib
//                                            EnDDO
//                                           EnDDO
//                                           STOP
//                                           EnD
//                                    */
//        }
//    }


    static ColorTransform getCtx(double wl) {
        double r = 0;
        double g = 0;
        double b = 0;
        double SSS;
        int red, green, blue;
        ColorTransform ctx = null;
        if (wl >= 380 && wl <= 440.) {
            r = -1. * (wl - 440.) / (440. - 380.);
            g = 0;
            b = 1;
        }
        if (wl > 440 && wl <= 490) {
            r = 0;
            g = (wl - 440.) / (490. - 440.);
            b = 1.;
        }
        if (wl > 490. && wl <= 510.) {
            r = 0;
            g = 1;
            b = -1. * (wl - 510.) / (510. - 490.);
        }
        if (wl > 510. && wl <= 580.) {
            r = (wl - 510.) / (580. - 510.);
            g = 1.;
            b = 0.;
        }
        if (wl > 580. && wl <= 645.) {
            r = 1.;
            g = -1. * (wl - 645.) / (645. - 580.);
            b = 0.;
        }
        if (wl > 645. && wl <= 780.) {
            r = 1.;
            g = 0.;
            b = 0;
        }
        //      LET THE InTEnSITY SSS FALL OFF nEAr THE VISIOn LImITS
        if (wl > 700.) {
            SSS = .3 + .7 * (780. - wl) / (780. - 700.);
        } else if (wl < 420.) {
            SSS = .3 + .7 * (wl - 380.) / (420. - 380.);
        } else {
            SSS = 1.;
        }

        if (wl == 0) {
            ctx = new ColorTransform(255, 255, 255);
        } else {
            red = (int) Math.round(255 * (SSS * r));
            green = (int) Math.round(255 * (SSS * g));
            blue = (int) Math.round(255 * (SSS * b));
            ctx = new ColorTransform( red, green, blue);
        }
        return ctx;
    }
    static class ColorTransform {
        public int rb, gb, bb;

        public ColorTransform(int rb, int gb, int bb) {
            this.rb = rb;
            this.gb = gb;
            this.bb = bb;
        }

    }
}

