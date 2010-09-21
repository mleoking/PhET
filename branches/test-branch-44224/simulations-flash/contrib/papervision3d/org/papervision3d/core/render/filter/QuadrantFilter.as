package org.papervision3d.core.render.filter
{
	
	import flash.utils.*;
	
	import org.papervision3d.cameras.Camera3D;
	import org.papervision3d.core.clipping.draw.Clipping;
	import org.papervision3d.core.geom.renderables.Vertex3D;
	import org.papervision3d.core.geom.renderables.Vertex3DInstance;
	import org.papervision3d.core.math.Number3D;
	import org.papervision3d.core.math.NumberUV;
	import org.papervision3d.core.render.command.RenderLine;
	import org.papervision3d.core.render.command.RenderTriangle;
	import org.papervision3d.core.render.command.RenderableListItem;
	import org.papervision3d.core.render.data.QuadTree;
	import org.papervision3d.scenes.Scene3D;

    /**
    * Splits all intersecting triangles
    */
    public class QuadrantFilter extends AbstractQuadrantFilter 
    {
    	
        private var maxdelay:int;
        
    	private var start:int;
        private var check:int;
        
        private var primitives:Array;
        private var pri:RenderableListItem
        private var turn:int;
        private var leftover:Array;
        
        private var rivals:Array;
        private var rival:RenderableListItem;
        
        private var parts:Array;
        private var part:RenderableListItem;
        private var subst:Array;
        private var focus:Number;
        
        private var av0z:Number;
        private var av0p:Number;
        private var av0x:Number;
        private var av0y:Number;

        private var av1z:Number;
        private var av1p:Number;
        private var av1x:Number;
        private var av1y:Number;

        private var av2z:Number;
        private var av2p:Number;
        private var av2x:Number;
        private var av2y:Number;

        private var ad1x:Number;
        private var ad1y:Number;
        private var ad1z:Number;

        private var ad2x:Number;
        private var ad2y:Number;
        private var ad2z:Number;

        private var apa:Number;
        private var apb:Number;
        private var apc:Number;
        private var apd:Number;
        
        private var tv0z:Number;
        private var tv0p:Number;
        private var tv0x:Number;
        private var tv0y:Number;

        private var tv1z:Number;
        private var tv1p:Number;
        private var tv1x:Number;
        private var tv1y:Number;

        private var tv2z:Number;
        private var tv2p:Number;
        private var tv2x:Number;
        private var tv2y:Number;

        private var sv0:Number;
        private var sv1:Number;
        private var sv2:Number;
        
        private var td1x:Number;
        private var td1y:Number;
        private var td1z:Number;

        private var td2x:Number;
        private var td2y:Number;
        private var td2z:Number;

        private var tpa:Number;
        private var tpb:Number;
        private var tpc:Number;
        private var tpd:Number;
        
        private var sav0:Number;
        private var sav1:Number;
        private var sav2:Number;
        
        private var tv0:Vertex3D;
        private var tv1:Vertex3D;
        private var tv2:Vertex3D;
        
        private var q0x:Number;
        private var q0y:Number;
        private var q1x:Number;
        private var q1y:Number;
        private var q2x:Number;
        private var q2y:Number;
        
        private var w0x:Number;
        private var w0y:Number;
        private var w1x:Number;
        private var w1y:Number;
        private var w2x:Number;
        private var w2y:Number;
        
        private var ql01a:Number;
        private var ql01b:Number;
        private var ql01c:Number;
        private var ql01s:Number;
        private var ql01w0:Number;
        private var ql01w1:Number;
        private var ql01w2:Number;
        
        private var ql12a:Number;
        private var ql12b:Number;
        private var ql12c:Number;
        private var ql12s:Number;
        private var ql12w0:Number;
        private var ql12w1:Number;
        private var ql12w2:Number;
        
        private var ql20a:Number;
        private var ql20b:Number;
        private var ql20c:Number;
        private var ql20s:Number;
        private var ql20w0:Number;
        private var ql20w1:Number;
        private var ql20w2:Number;
		
        private var wl01a:Number;
        private var wl01b:Number;
        private var wl01c:Number;
        private var wl01s:Number;
        private var wl01q0:Number;
        private var wl01q1:Number;
        private var wl01q2:Number;
		
        private var wl12a:Number;
        private var wl12b:Number;
        private var wl12c:Number;
        private var wl12s:Number;
        private var wl12q0:Number;
        private var wl12q1:Number;
        private var wl12q2:Number;
		
        private var wl20a:Number;
        private var wl20b:Number;
        private var wl20c:Number;
        private var wl20s:Number;
        private var wl20q0:Number;
        private var wl20q1:Number;
        private var wl20q2:Number;
        
        private var d:Number;
        private var k0:Number;
        private var k1:Number;

        private var tv01z:Number;
        private var tv01p:Number;
        private var tv01x:Number;
        private var tv01y:Number;
        private var v01:Vertex3DInstance = new Vertex3DInstance();
        
    	private function riddle(q:RenderableListItem, w:RenderableListItem):Array
        {
            if (q is RenderTriangle && q.instance.testQuad)
            { 
                if (w is RenderTriangle && w.instance.testQuad)
                    return riddleTT(RenderTriangle(q),RenderTriangle(w));
               
            }
           
            return null;
        }
        
        private final function riddleTT(q:RenderTriangle, w:RenderTriangle):Array
        {
        	//return if triangle area below 10 or if actual rival triangles do not overlap
            if (q.area < 10 || w.area < 10 || !overlap(q, w))
                return null;
                
			//deperspective rival v0 
            av0z = w.v0.z;
            av0p = 1 + av0z / focus;
            av0x = w.v0.x * av0p;
            av0y = w.v0.y * av0p;
			
			//deperspective rival v1
            av1z = w.v1.z;
            av1p = 1 + av1z / focus;
            av1x = w.v1.x * av1p;
            av1y = w.v1.y * av1p;
			
			//deperspective rival v2
            av2z = w.v2.z;
            av2p = 1 + av2z / focus;
            av2x = w.v2.x * av2p;
            av2y = w.v2.y * av2p;
			
			//calculate rival face normal
            ad1x = av1x - av0x;
            ad1y = av1y - av0y;
            ad1z = av1z - av0z;

            ad2x = av2x - av0x;
            ad2y = av2y - av0y;
            ad2z = av2z - av0z;

            apa = ad1y*ad2z - ad1z*ad2y;
            apb = ad1z*ad2x - ad1x*ad2z;
            apc = ad1x*ad2y - ad1y*ad2x;
            
            //calculate the dot product of the rival normal and rival v0
            apd = apa*av0x + apb*av0y + apc*av0z;
            			
			//return if normal length is less than 1
            if (apa*apa + apb*apb + apc*apc < 1)
                return null;
			
			//deperspective v0
            tv0z = q.v0.z;
            tv0p = 1 + tv0z / focus;
            tv0x = q.v0.x * tv0p;
            tv0y = q.v0.y * tv0p;

			//deperspective v1
            tv1z = q.v1.z;
            tv1p = 1 + tv1z / focus;
            tv1x = q.v1.x * tv1p;
            tv1y = q.v1.y * tv1p;
			
			//deperspective v2
            tv2z = q.v2.z;
            tv2p = 1 + tv2z / focus;
            tv2x = q.v2.x * tv2p;
            tv2y = q.v2.y * tv2p;
            
            //calculate the dot product of v0, v1 and v2 to the rival normal
            sv0 = apa*tv0x + apb*tv0y + apc*tv0z - apd;
            sv1 = apa*tv1x + apb*tv1y + apc*tv1z - apd;
            sv2 = apa*tv2x + apb*tv2y + apc*tv2z - apd;

            if (sv0*sv0 < 0.001)
                sv0 = 0;
            if (sv1*sv1 < 0.001)
                sv1 = 0;
            if (sv2*sv2 < 0.001)
                sv2 = 0;

            if (sv0*sv1 >= -0.01 && sv1*sv2 >= -0.01 && sv2*sv0 >= -0.01)
                return null;
			
			//calulate face normal
            td1x = tv1x - tv0x;
            td1y = tv1y - tv0y;
            td1z = tv1z - tv0z;

            td2x = tv2x - tv0x;
            td2y = tv2y - tv0y;
            td2z = tv2z - tv0z;

            tpa = td1y*td2z - td1z*td2y;
            tpb = td1z*td2x - td1x*td2z;
            tpc = td1x*td2y - td1y*td2x;
            
            //calculate the dot product of the face normal and v0
            tpd = tpa*tv0x + tpb*tv0y + tpc*tv0z;

			//return if normal length is less than 1
            if (tpa*tpa + tpb*tpb + tpc*tpc < 1)
                return null;

			//calculate the dot product of rival v0, v1 and v2 to the face normal
            sav0 = tpa*av0x + tpb*av0y + tpc*av0z - tpd;
            sav1 = tpa*av1x + tpb*av1y + tpc*av1z - tpd;
            sav2 = tpa*av2x + tpb*av2y + tpc*av2z - tpd;

            if (sav0*sav0 < 0.001)
                sav0 = 0;
            if (sav1*sav1 < 0.001)
                sav1 = 0;
            if (sav2*sav2 < 0.001)
                sav2 = 0;
                
             if ((sav0*sav1 >= -0.01) && (sav1*sav2 >= -0.01) && (sav2*sav0 >= -0.01))
                return null; 

			
            tv0 = q.v0.deperspective(focus);
            tv1 = q.v1.deperspective(focus);
            tv2 = q.v2.deperspective(focus);
            
           
                
            if (sv1*sv2 >= -1)
            {
                return q.fivepointcut(q.v2,  Vertex3D.weighted(tv2, tv0, -sv0, sv2).perspective(focus), q.v0, Vertex3D.weighted(tv0, tv1, sv1, -sv0).perspective(focus), q.v1,
                    q.uv2, NumberUV.weighted(q.uv2, q.uv0, -sv0, sv2), q.uv0, NumberUV.weighted(q.uv0, q.uv1, sv1, -sv0), q.uv1);
            }                                                           
            else                                                        
            if (sv0*sv1 >= -1)                                           
            {
                return q.fivepointcut(q.v1,  Vertex3D.weighted(tv1, tv2, -sv2, sv1).perspective(focus), q.v2, Vertex3D.weighted(tv2, tv0, sv0, -sv2).perspective(focus), q.v0,
                    q.uv1, NumberUV.weighted(q.uv1, q.uv2, -sv2, sv1), q.uv2, NumberUV.weighted(q.uv2, q.uv0, sv0, -sv2), q.uv0);
            }                                                           
            else                                                        
            {                                                           
                return q.fivepointcut(q.v0,  Vertex3D.weighted(tv0, tv1, -sv1, sv0).perspective(focus), q.v1, Vertex3D.weighted(tv1, tv2, sv2, -sv1).perspective(focus), q.v2,
                    q.uv0, NumberUV.weighted(q.uv0, q.uv1, -sv1, sv0), q.uv1, NumberUV.weighted(q.uv1, q.uv2, sv2, -sv1), q.uv2);
            }

            return null;    
        }
         
        private function overlap(q:RenderTriangle, w:RenderTriangle):Boolean
        {
        
            q0x = q.v0.x;
            q0y = q.v0.y;
            q1x = q.v1.x;
            q1y = q.v1.y;
            q2x = q.v2.x;
            q2y = q.v2.y;
        
            w0x = w.v0.x;
            w0y = w.v0.y;
            w1x = w.v1.x;
            w1y = w.v1.y;
            w2x = w.v2.x;
            w2y = w.v2.y;
        
            ql01a = q1y - q0y;
            ql01b = q0x - q1x;
            ql01c = -(ql01b*q0y + ql01a*q0x);
            ql01s = ql01a*q2x + ql01b*q2y + ql01c;
            ql01w0 = (ql01a*w0x + ql01b*w0y + ql01c) * ql01s;
            ql01w1 = (ql01a*w1x + ql01b*w1y + ql01c) * ql01s;
            ql01w2 = (ql01a*w2x + ql01b*w2y + ql01c) * ql01s;
        
            if ((ql01w0 <= 0.0001) && (ql01w1 <= 0.0001) && (ql01w2 <= 0.0001))
                return false;
        
            ql12a = q2y - q1y;
            ql12b = q1x - q2x;
            ql12c = -(ql12b*q1y + ql12a*q1x);
            ql12s = ql12a*q0x + ql12b*q0y + ql12c;
            ql12w0 = (ql12a*w0x + ql12b*w0y + ql12c) * ql12s;
            ql12w1 = (ql12a*w1x + ql12b*w1y + ql12c) * ql12s;
            ql12w2 = (ql12a*w2x + ql12b*w2y + ql12c) * ql12s;
        
            if ((ql12w0 <= 0.0001) && (ql12w1 <= 0.0001) && (ql12w2 <= 0.0001))
                return false;
        
            ql20a = q0y - q2y;
            ql20b = q2x - q0x;
            ql20c = -(ql20b*q2y + ql20a*q2x);
            ql20s = ql20a*q1x + ql20b*q1y + ql20c;
            ql20w0 = (ql20a*w0x + ql20b*w0y + ql20c) * ql20s;
            ql20w1 = (ql20a*w1x + ql20b*w1y + ql20c) * ql20s;
            ql20w2 = (ql20a*w2x + ql20b*w2y + ql20c) * ql20s;
        
            if ((ql20w0 <= 0.0001) && (ql20w1 <= 0.0001) && (ql20w2 <= 0.0001))
                return false;
        
            wl01a = w1y - w0y;
            wl01b = w0x - w1x;
            wl01c = -(wl01b*w0y + wl01a*w0x);
            wl01s = wl01a*w2x + wl01b*w2y + wl01c;
            wl01q0 = (wl01a*q0x + wl01b*q0y + wl01c) * wl01s;
            wl01q1 = (wl01a*q1x + wl01b*q1y + wl01c) * wl01s;
            wl01q2 = (wl01a*q2x + wl01b*q2y + wl01c) * wl01s;
        
            if ((wl01q0 <= 0.0001) && (wl01q1 <= 0.0001) && (wl01q2 <= 0.0001))
                return false;
        
            wl12a = w2y - w1y;
            wl12b = w1x - w2x;
            wl12c = -(wl12b*w1y + wl12a*w1x);
            wl12s = wl12a*w0x + wl12b*w0y + wl12c;
            wl12q0 = (wl12a*q0x + wl12b*q0y + wl12c) * wl12s;
            wl12q1 = (wl12a*q1x + wl12b*q1y + wl12c) * wl12s;
            wl12q2 = (wl12a*q2x + wl12b*q2y + wl12c) * wl12s;
        
            if ((wl12q0 <= 0.0001) && (wl12q1 <= 0.0001) && (wl12q2 <= 0.0001))
                return false;
        
            wl20a = w0y - w2y;
            wl20b = w2x - w0x;
            wl20c = -(wl20b*w2y + wl20a*w2x);
            wl20s = wl20a*w1x + wl20b*w1y + wl20c;
            wl20q0 = (wl20a*q0x + wl20b*q0y + wl20c) * wl20s;
            wl20q1 = (wl20a*q1x + wl20b*q1y + wl20c) * wl20s;
            wl20q2 = (wl20a*q2x + wl20b*q2y + wl20c) * wl20s;
        
            if ((wl20q0 <= 0.0001) && (wl20q1 <= 0.0001) && (wl20q2 <= 0.0001))
                return false;
            
            return true;
        }
        
		/**
		 * Creates a new <code>QuadrantFilter</code> object.
		 *
		 * @param	maxdelay	[optional]		The maximum time the filter can take to resolve z-depth before timing out.
		 */
        public function QuadrantFilter(maxdelay:int = 60000)
        {
            this.maxdelay = maxdelay;
        }
        
		/**
		 * @inheritDoc
		 */
        public override function filterTree(tree:QuadTree, scene:Scene3D, camera:Camera3D, clip:Clipping):void
        {
            start = getTimer();
            check = 0;
    		focus = camera.focus;
    		
            primitives = tree.list();
            turn = 0;
            
            while (primitives.length > 0)
            {
                var leftover:Array = new Array();
                for each (pri in primitives)
                {
                    
                    check++;
                    if (check == 10)
                        if (getTimer() - start > maxdelay)
                            return;
                        else
                            check = 0;
                    
                    rivals = tree.getOverlaps(pri, pri.instance);
                    
                    for each (rival in rivals)
                    {
                        if (rival == pri)
                            continue;
                        
                        if (rival.minZ >= pri.maxZ)
                            continue;
                        if (rival.maxZ <= pri.minZ)
                            continue;
                        
                        parts = riddle(pri, rival);
                        
                        if (parts != null){
                          
	                        tree.remove(pri);
	                        for each (part in parts)
	                        {
	                            leftover.push(part);
	                            tree.add(part);
	                        }
	                        break;
                        }
                    }
                }
                primitives = leftover;
                turn += 1;
                if (turn == 40)
                    break;
            }
        }
        
		/**
		 * Used to trace the values of a filter.
		 * 
		 * @return A string representation of the filter object.
		 */
        public function toString():String
        {
            return "QuadrantFilter" + ((maxdelay == 60000) ? "" : "("+maxdelay+"ms)");
        }
    }

}