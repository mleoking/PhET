class ColorUtil {
	private static var minWavelength:Number = 380;
	private static var maxWavelength:Number = 780;
	private static var ctxArray:Array;
	static function traceCtx(c) {
		trace("red:" + c.rb + "  green:" + c.gb + "  blue:" + c.bb);
	}
	static function getColor(wavelength:Number) {
		return ctxToColor( ColorUtil.getCtx( wavelength));
	}
	static function ctxToColor(ctx):Number {
		var red = Math.round(0x010000 * ctx.rb);
		var green = Math.round(0x000100 * ctx.gb);
		var blue = Math.round(0x000001 * ctx.bb);
		var c:Number = red + blue + green;
		return c;
	}
	static function colorToCtx(color:Number){
		var red = Math.round(color / 0x010000);
		var green = Math.round( (color % 0x010000) / 0x000100 );
		var blue = Math.round(color % 0x000100);
		var ctx = {rb:red, gb:green, bb:blue};
		return ctx;
	}
	static function ctxToWavelength(ctx){
		if(ctxArray == undefined ) {
			genCtxArray();
		}
		var found:Boolean = false;
		var result:Object;
		var eps:Number = 2;
		for(var i = 0; i < ctxArray.length && !found; i++){
			if( Math.abs(ctx.rb - ctxArray[i].rb) < eps
			 && Math.abs(ctx.gb - ctxArray[i].gb) < eps
			 && Math.abs(ctx.bb - ctxArray[i].bb) < eps){
				found = true;
				result = ctxArray[i];
			}		
		}
		return minWavelength + i;
	}
	static function getPercentFilteredLight():Number {
		var wl = _root.bulb1.getWavelength();
		var pctPassed = _root.filter1.percentPassed(wl);
		return pctPassed * 100;
	}
	static function genCtxArray() {
		var numWavelengths = maxWavelength - minWavelength + 1;
		ctxArray = new Array(numWavelengths);
		var m = 400;
		var n = 50;
		var max = 255;
		var gamma = .80;
		var wl;
		var r, g, b;
		var SSS;
		for (var j = 0; j < numWavelengths; j++) {
//			for (var i = 0; i < m; i++) {
				//         WAVELEngTH = wl
//				wl = 380. + (i * 400. / m);
				wl = minWavelength + j;
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
				}
				else if (wl < 420.) {
					SSS = .3 + .7 * (wl - 380.) / (420. - 380.);
				}
				else {
					SSS = 1.;
				}
				var red = Math.round(255 * (SSS * r));
				var green = Math.round(255 * (SSS * g));
				var blue = Math.round(255 * (SSS * b));
				var ctx = {rb:red, gb:green, bb:blue};
				ctxArray[j] = ctx;
//			}
			/*
									//     gamma ADjUST AnD WrITE ImAgE TO An ArrAY
									c
									         CV(I,j,1)=(SSS*r)**gamma
									         CV(I,j,2)=(SSS*g)**gamma
									         CV(I,j,3)=(SSS*b)**gamma
									        EnDDO
									       EnDDO
									c
									c      WrITE ImAgE TO PPm FILE
									c
									       DO j=1,n
									        DO I=1,m
									           wl = 380. + rEAL(I * 400. / m)        
									           Ir=InT(max*CV(I,j,1))
									           Ig=InT(max*CV(I,j,2))
									           Ib=InT(max*CV(I,j,3))
									c
									c      ITYPE=1 - PLAIn SPECTUm
									c      ITYPE=2 - mArK SPECTrUm AT 100 nm InTEVALS
									c      ITYPE=3 - HYDrOgEn bALmEr EmISSIOn SPECTrA
									c      ITYPE=4 - HYDrOgEn bALmEr AbSOrPTIOn SPECTrA
									c  
									         ITYPE=4
									         if (ITYPE.EQ.2) 
									            DO K=400,700,100
									              if ((AbS(InTwl)-K).LT.1 && (j<=20)) 
									               Ir=max
									               Ig=max
									               Ib=max
									              }
									            EnDDO
									         ELSEif (ITYPE.EQ.3) 
									            if ((AbSwl-656.)>1. && (AbSwl-486.)>1. && 
									     *          (AbSwl-433.)>1. && (AbSwl-410.)>1.)
									     *           .AnD.(AbSwl-396.)>1.)) 
									              Ir = 0
									              Ig = 0
									              Ib = 0
									            }
									         ELSEif (ITYPE.EQ.4) 
									            if ((AbSwl-656.).LT.1.1).or.(AbSwl-486.).LT.1.1).or.
									     *          (AbSwl-433.).LT.1.1).or.(AbSwl-410.).LT.1.1)
									     *           .or.(AbSwl-396.).LT.1.1)) 
									              Ir = 0
									              Ig = 0
									              Ib = 0
									            }
									         }
									
									         WrITE(20,*) Ir, Ig, Ib
									        EnDDO
									       EnDDO
									       STOP
									       EnD 
									*/
		}
	}
	static function getCtx(wl) {
		var r:Number;
		var g:Number;
		var b:Number;
		var SSS;
		var red, green, blue;
		var ctx;
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
		}
		else if (wl < 420.) {
			SSS = .3 + .7 * (wl - 380.) / (420. - 380.);
		}
		else {
			SSS = 1.;
		}
		
		if( wl == 0 ) {
			ctx = {rb:255, gb:255, bb: 255};
		}
		else {
		red = Math.round(255 * (SSS * r));
		green = Math.round(255 * (SSS * g));
		blue = Math.round(255 * (SSS * b));
		ctx = {rb:red, gb:green, bb:blue};
		}
		return ctx;
	}
}
