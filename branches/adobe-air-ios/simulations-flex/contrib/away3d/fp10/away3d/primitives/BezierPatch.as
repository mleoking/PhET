package away3d.primitives
{
	import away3d.*;
	import away3d.core.base.*;
	import away3d.core.utils.*;
	import away3d.materials.*;
	import away3d.primitives.data.PatchData;
	import flash.utils.*;

	use namespace arcane;
	
	/**
	 * BezierPatch primitive creates a smooth mesh based on a 4x4 vertex patch using a cubic bezier curve.
	 */
	public class BezierPatch extends Mesh
	{
		public var patchData:PatchData;
		public var xOffset:Number;
		public var yOffset:Number;
		public var zOffset:Number;
		public var renderMode:Number;
		public var connectMirrors:Number;

		private var _pI:Array;
		private var _patchVertices:Dictionary = new Dictionary();
		private var _edgeCache:Array;
		private var _gen:Array = [];
		private var _normDir:Boolean;
		private var _material:ITriangleMaterial;
		       
		private const resol:int = 1000;
		public static const PATCH:int = 0;
		public static const WIRE_ONLY:int = 1;
		public static const BASEWIRE_ONLY:int = 2;
		public static const MIRRORWIRE_ONLY:int = 3;
		public static const NOTSET:int = 0;
		public static const N:int = 1;
		public static const X:int = 2;
		public static const XZ:int = 4;
		public static const Z:int = 8;
		public static const Y:int = 16;
		public static const XY:int = 32;
		public static const XYZ:int = 64;
		public static const YZ:int = 128;		
		public static const L:int = 256;
		public static const R:int = 512;
		public static const T:int = 1024;
		public static const B:int = 2048;
		
		public static const LX:int = X | L;
		public static const LY:int = Y | L;
		public static const LZ:int = Z | L;
		public static const RX:int = X | R;
		public static const RY:int = Y | R;
		public static const RZ:int = Z | R;
		public static const TX:int = X | T;
		public static const TY:int = Y | T;
		public static const TZ:int = Z | T;
		public static const BX:int = X | B;
		public static const BY:int = Y | B;
		public static const BZ:int = Z | B;
		                             
		public static const TOP:Array = [N, XZ, Z, X, Y, XYZ, XY, YZ];
		public static const BOTTOM:Array = [Y, XYZ, XY, YZ, N, XZ, Z, X];
		public static const FRONT:Array = [N, XY, X, Y, Z, XYZ, YZ, XZ];
		public static const BACK:Array = [Z, XYZ, YZ, XZ, N, XY, X, Y];
		public static const LEFT:Array = [N, YZ, Y, Z, X, XYZ, XZ, XY];
		public static const RIGHT:Array = [X, XYZ, XZ, XY, N, YZ, Y, Z];
		public static const TOPLEFT:int = 1;
		public static const TOPRIGHT:int = 2;
		public static const BOTTOMLEFT:int = 3;
		public static const BOTTOMRIGHT:int = 4;
		
		private static const OPPOSITE_OR:Array = [];
		OPPOSITE_OR[X | X] = N;
		OPPOSITE_OR[XY | X] = Y;
		OPPOSITE_OR[XZ | X] = Z;
		OPPOSITE_OR[XYZ | X] = YZ;

		OPPOSITE_OR[Y | Y] = N;
		OPPOSITE_OR[XY | Y] = X;
		OPPOSITE_OR[XYZ | Y] = XZ;
		OPPOSITE_OR[YZ | Y] = Z;
		
		OPPOSITE_OR[Z | Z] = N;
		OPPOSITE_OR[XZ | Z] = X;
		OPPOSITE_OR[XYZ | Z] = XY;
		OPPOSITE_OR[YZ | Z] = Y;
		           
		private static const SCALINGS:Array = [];
		SCALINGS[1] = [1, 1, 1];
		SCALINGS[2] = [-1, 1, 1];
		SCALINGS[4] = [-1, 1, -1];
		SCALINGS[8] = [1, 1, -1];
		SCALINGS[16] = [1, -1, 1];
		SCALINGS[32] = [-1, -1, 1];
		SCALINGS[64] = [-1, -1, -1];
		SCALINGS[128] = [1, -1, -1];

		/**
		 * Creates a new <code>BezierPatch</code> object.
		 * 
		 * @param	patchDataPrm   Patch definition for this object.
		 * @param	init           [optional]  An initialisation object for specifying default instance properties.
		 */
		public function BezierPatch(patchDataPrm:PatchData, init:Object = null) {
			
			super(init);

			_material = material as ITriangleMaterial;
			patchData = patchDataPrm;
			_pI = patchData.patchInfo;
			xOffset = ini.getNumber("xoffset", 0);
			yOffset = ini.getNumber("yoffset", 0);
			zOffset = ini.getNumber("zoffset", 0);
			renderMode = ini.getInt("renderMode", 0, { min:0, max:3 } );

			type = "primitive";
			type = "BezierPatch";
			buildPatch();
		}
		
		/**
		 * Generate the patch mesh based on the patch data and render modes.
		 */
		public function buildPatch():void {
			
			_patchVertices = new Dictionary();
			_edgeCache = [];
			geometry = new Geometry();
			
			// Iterate through all the items in the patch array
			for (var key:String in patchData.patchInfo) {
				if (renderMode == WIRE_ONLY || renderMode == BASEWIRE_ONLY || renderMode == MIRRORWIRE_ONLY) buildWirePatch( key );
				if (renderMode == PATCH) buildTrianglePatch( key );
			}
		}
		
		// Render the wireframe of the patch
		private function buildWirePatch( key:String):void {
			var or:int = patchData.patchInfo[key].oOr;				

			if (renderMode == BASEWIRE_ONLY) {
				or = N;
			}
			if (renderMode == MIRRORWIRE_ONLY) {
				or = or & ~N;
			}			
			
			for each (var orientation:int in [1, 2, 4, 8, 16, 32, 64, 128]) {
				if ((or & orientation)>0) {
					var xOr:Boolean = ((orientation & X) | (orientation & XY) | (orientation & XZ) | (orientation & XYZ)) > 0;
					var yOr:Boolean = ((orientation & Y) | (orientation & XY) | (orientation & YZ) | (orientation & XYZ)) > 0;
					var zOr:Boolean = ((orientation & Z) | (orientation & YZ) | (orientation & XZ) | (orientation & XYZ)) > 0;
					var xS:Number = (xOr ? -1 : 1);
					var yS:Number = (yOr ? -1 : 1);
					var zS:Number = (zOr ? -1 : 1);
					
					// Iterate through each key in the object
					for (var i:int = 0; i < patchData.nodes[key].length; i += 4 ) {
						var vA:Array = patchData.nodes[key];
						var v0:Vertex = new Vertex((patchData.vertices[vA[i  ]].x + xOffset) * xS, (patchData.vertices[vA[i  ]].y + yOffset) * yS, (patchData.vertices[vA[i  ]].z + zOffset) * zS);
						var v1:Vertex = new Vertex((patchData.vertices[vA[i+1]].x + xOffset) * xS, (patchData.vertices[vA[i+1]].y + yOffset) * yS, (patchData.vertices[vA[i+1]].z + zOffset) * zS);
						var v2:Vertex = new Vertex((patchData.vertices[vA[i+2]].x + xOffset) * xS, (patchData.vertices[vA[i+2]].y + yOffset) * yS, (patchData.vertices[vA[i+2]].z + zOffset) * zS);
						var v3:Vertex = new Vertex((patchData.vertices[vA[i+3]].x + xOffset) * xS, (patchData.vertices[vA[i+3]].y + yOffset) * yS, (patchData.vertices[vA[i+3]].z + zOffset) * zS);
						addSegment(new Segment(v0, v1));                                                                
						addSegment(new Segment(v1, v2));
						addSegment(new Segment(v2, v3));

							
						if (i+4<patchData.nodes[key].length) {
							var v4:Vertex = new Vertex((patchData.vertices[vA[i+4]].x + xOffset) * xS, (patchData.vertices[vA[i+4]].y + yOffset) * yS, (patchData.vertices[vA[i+4]].z + zOffset) * zS);
							var v5:Vertex = new Vertex((patchData.vertices[vA[i+5]].x + xOffset) * xS, (patchData.vertices[vA[i+5]].y + yOffset) * yS, (patchData.vertices[vA[i+5]].z + zOffset) * zS);
							var v6:Vertex = new Vertex((patchData.vertices[vA[i+6]].x + xOffset) * xS, (patchData.vertices[vA[i+6]].y + yOffset) * yS, (patchData.vertices[vA[i+6]].z + zOffset) * zS);
							var v7:Vertex = new Vertex((patchData.vertices[vA[i+7]].x + xOffset) * xS, (patchData.vertices[vA[i+7]].y + yOffset) * yS, (patchData.vertices[vA[i+7]].z + zOffset) * zS);
							addSegment(new Segment(v0, v4));                                                                
							addSegment(new Segment(v1, v5));
							addSegment(new Segment(v2, v6));
							addSegment(new Segment(v3, v7));
						}
					}
				}
			}
		}

		private var uva:UV;
		private var uvb:UV;
		private var uvc:UV;
		private var uvd:UV;
		private var u1Pos:Number;
		private var v1Pos:Number;
		private var u2Pos:Number;
		private var v2Pos:Number;	
		private var u1:Number;
		private var v1:Number;
		private var u2:Number;
		private var v2:Number;
		private var vx0:Vertex = new Vertex();
		private var vx1:Vertex = new Vertex();
		private var vx2:Vertex = new Vertex();
		private var vx3:Vertex = new Vertex();
		private var vx4:Vertex = new Vertex();
		private var vx5:Vertex = new Vertex();
		private var vx6:Vertex = new Vertex();
		private var vx7:Vertex = new Vertex();

		// Render the full uv mapped patch
		private function buildTrianglePatch( key:String ):void {	
			// Establish if UVs are present
			var pUV:Array;
			if (patchData.uvs && patchData.uvs[key]) 
				pUV = patchData.uvs[key];
			else 
				u1 = v1 = u2 = v2 = 1;

			// Iterate through each 4x4 sub-patch of the patch
			for (var p:int = 0; p < _pI[key].patchCount; p++) {	
				var x:Number;
				var y:Number;
				var thisOr:int;
				var orientation:int;
				
				definePatchData(key, p);
				
				// Split the patch into segments vertically
				for (y = 0; y <=  _pI[key].oSegH; y++) {
					// Split the patch into segments horizontally
					for (x = 0; x <=  _pI[key].oSegW; x++) {
						// Process each orientation to determine whether to add faces or not
						for each (orientation in [1, 2, 4, 8, 16, 32, 64, 128]) {

							thisOr = _pI[key].oOr & orientation;
						
							// If we have the correct orientation proceed
							if (thisOr>0) {								

								var xOr:Boolean = ((orientation & X) | (orientation & XY) | (orientation & XZ) | (orientation & XYZ)) > 0;
								var yOr:Boolean = ((orientation & Y) | (orientation & XY) | (orientation & YZ) | (orientation & XYZ)) > 0;
								var zOr:Boolean = ((orientation & Z) | (orientation & YZ) | (orientation & XZ) | (orientation & XYZ)) > 0;

								// Decide on the direction of the normals for the faces
								_normDir = ((((xOr ? 1 : 0) + (yOr ? 1 : 0) + (zOr ? 1 : 0)) % 2) > 0);
								
								// Only add faces to the patch when not top or left edge (y=0 & x=0)
								if (x > 0 && y>0) {	
									// Establish the UV range for this patch
									u1 = v1 = u2 = v2 = 1;
									if (patchData.uvs && patchData.uvs[key]) {									
										if (patchData.uvs[key][thisOr]) {																				
											// Get UV coords for this particular patch and orientation
											u1 = pUV[thisOr][p][0];
											v1 = pUV[thisOr][p][1];
											u2 = pUV[thisOr][p][2];
											v2 = pUV[thisOr][p][3];
										} else if (patchData.uvs[key][p]) {
											// Get the UV coords for the patch (same for all orientations)									
											u1 = pUV[p][0];
											v1 = pUV[p][1];
											u2 = pUV[p][2];
											v2 = pUV[p][3];
										}      
										if (_normDir) { 
											// Get the correct UV coordinates
											u2Pos = ((u1 - u2) * (x / _pI[key].oSegW)) + u2;
											v2Pos = ((v2 - v1) * (y / _pI[key].oSegH)) + v1;
											u1Pos = u2Pos - ((u1 - u2) * _pI[key].xStp);
											v1Pos = v2Pos - ((v2 - v1) * _pI[key].yStp);
											
											// Set up the UVs
											uva = new UV(u2Pos, 1 - v1Pos);
											uvb = new UV(u2Pos, 1 - v2Pos);
											uvc = new UV(u1Pos, 1 - v2Pos);
											uvd = new UV(u1Pos, 1 - v1Pos);
										} else {
											// Get the correct UV coordinates
											u2Pos = ((u2 - u1) * (x / _pI[key].oSegW)) + u1;
											v2Pos = ((v2 - v1) * (y / _pI[key].oSegH)) + v1;
											u1Pos = u2Pos - ((u2 - u1) * _pI[key].xStp);
											v1Pos = v2Pos - ((v2 - v1) * _pI[key].yStp);

											// Set up the UVs
											uva = new UV(u2Pos, 1 - v2Pos);
											uvb = new UV(u2Pos, 1 - v1Pos);
											uvc = new UV(u1Pos, 1 - v1Pos);
											uvd = new UV(u1Pos, 1 - v2Pos);
										}
									}

									// Get the stored vertices and switch face normal as necessary
									vx0 = _gen[p][y - 1][x][orientation];
									vx1 = _gen[p][y][x - 1][orientation];
									vx2 = _gen[p][y - 1][x - 1][orientation];
									vx3 = _gen[p][y][x][orientation];
									_patchVertices[vx0] = [key, p, y - 1, x, orientation];
									_patchVertices[vx1] = [key, p, y, x - 1, orientation];	
									_patchVertices[vx2] = [key, p, y - 1, x - 1, orientation];						
									_patchVertices[vx3] = [key, p, y, x, orientation];		

									// Add faces based on normal and if the vertices do not shared
									if (_normDir) {
										if (vx0 != vx1 && vx0 != vx3 && vx1 != vx3) 
											addFace(new Face(vx0, vx3, vx1, _material, uva, uvb, uvc));
										if (vx0 != vx1 && vx0 != vx2 && vx1 != vx2)
											addFace(new Face(vx0, vx1, vx2, _material, uva, uvc, uvd));
									} else {
										if (vx0 != vx1 && vx0 != vx3 && vx1 != vx3) 
											addFace(new Face(vx0, vx1, vx3, _material, uvb, uvd, uva));
										if (vx0 != vx1 && vx0 != vx2 && vx1 != vx2) 
											addFace(new Face(vx0, vx2, vx1, _material, uvb, uvc, uvd));
									}                     
								}	
								
								// Connect along the edges in the defined direction
								if (_pI[key].oCL > 0 ) connectEdge(pUV, X, key, p, x, y, 0, _normDir, orientation, _pI[key].oCL, L);
								if (_pI[key].oCR > 0 ) connectEdge(pUV, X, key, p, x, y, _pI[key].oSegW, !_normDir, orientation, _pI[key].oCR, R);
								if (_pI[key].oCT > 0 ) connectEdge(pUV, Y, key, p, x, y, 0, !_normDir, orientation, _pI[key].oCT, T);
								if (_pI[key].oCB > 0 ) connectEdge(pUV, Y, key, p, x, y, _pI[key].oSegH, _normDir, orientation, _pI[key].oCB, B);
							}
						}
					}				
				}

				// Fill in the holes between mirrored patches (e.g. vert0 on patches N, X, XZ, Z)
				fillPatchMirrorHoles(pUV, _pI[key].fillPoints, key, p, _pI[key].oSegW, _pI[key].oSegH);
			}
		}

		private function definePatchData(key:String, p:int):void {
			var xCtr:int = 0;
			var yCtr:int = 0;
			var thisOr:int = 0;

			_gen[p] = [];
			// Generate mesh for base patch and apply to the other orientations and store
			for (var yPos:Number = 0; yPos <= 1 + (_pI[key].yStp / 2); yPos += _pI[key].yStp) {
				_gen[p][yCtr] = [];
				xCtr = 0;				
				for (var xPos:Number = 0; xPos <= 1 + (_pI[key].xStp / 2); xPos += _pI[key].xStp) {
					_gen[p][yCtr][xCtr] = [];

					for each (var orientation:int in [1, 2, 4, 8, 16, 32, 64, 128]) {
						thisOr = _pI[key].oOr & orientation;
						if (thisOr > 0) {
							_gen[p][yCtr][xCtr][orientation] = vScaleXYZ( patchData.generatedPatch[key][p][yCtr][xCtr], xPos, yPos, SCALINGS[orientation][0], SCALINGS[orientation][1], SCALINGS[orientation][2] );

							// Cache the edges for re-use later
							if (xPos == 0 || yPos == 0 || (Math.round(xPos * resol)/resol) == 1 || (Math.round(yPos * resol)/resol) >= 1) {
								_edgeCache.push(_gen[p][yCtr][xCtr][orientation]);
							}
						}
					}
					xCtr++;
				}
				yCtr++;
			}
		}

		/**
		 * Refresh the patch with updated patch data information - this is far quicker than re-building the patch
		 */
		public function refreshPatch():void {
			var v:Vertex;
			var pId:int;
			var yId:int;
			var xId:int;
			var orId:int;
			
			patchData.build(true);

			// Iterate through all the items in the patch array
			for (var key:String in _pI) {
				
				// Iterate through each 4x4 sub-patch of the patch
				for (var p:int = 0; p < _pI[key].patchCount; p++) {	

					// Generate mesh for base patch and apply to the other orientations and store
					for each (v in vertices) {
						pId = _patchVertices[v][1];
						yId = _patchVertices[v][2];
						xId = _patchVertices[v][3];
						orId = _patchVertices[v][4];
						v.x = patchData.generatedPatch[key][pId][yId][xId].x; 
						v.y = patchData.generatedPatch[key][pId][yId][xId].y; 
						v.z = patchData.generatedPatch[key][pId][yId][xId].z; 
						vRescaleXYZ( v, SCALINGS[orId][0], SCALINGS[orId][1], SCALINGS[orId][2] );
					}
				}
			}
			_objectDirty = true;
			updateObject();
		}
		
		// Add connecting faces to the required edge and mirror faces
		private function connectEdge(pUV:Array, edge:int, key:String, p:int, x:int, y:int, pos:int, nDir:Boolean, orient:int, cOr:int, cPos:int):void {
			
			// Connect along x==0 for the left connection or x=o.segmentsW for right
			var thisOr:int = cOr | cPos;
			var oppOr:int = OPPOSITE_OR[orient | cOr];
			
			if (edge == X && x == pos && y > 0 && oppOr>0) {	
				if (pUV && pUV[thisOr]) {
					u1 = pUV[thisOr][p][0];
					v1 = pUV[thisOr][p][1];
					u2 = pUV[thisOr][p][2];
					v2 = pUV[thisOr][p][3];
				} 

				if (nDir) {
					vx0 = _gen[p][y-1][x][orient];
					vx1 = _gen[p][y][x][oppOr];
					vx2 = _gen[p][y-1][x][oppOr];
					vx3 = _gen[p][y][x][orient];

					// Get the correct UV coordinates
					u1Pos = ((u2 - u1) * (y / _pI[key].oSegH)) + u1;
					u2Pos = u1Pos - ((u2 - u1) * _pI[key].yStp);
				} else {
					vx0 = _gen[p][y][x][orient];
					vx1 = _gen[p][y-1][x][oppOr];
					vx2 = _gen[p][y][x][oppOr];
					vx3 = _gen[p][y-1][x][orient];

					// Get the correct UV coordinates
					u2Pos = ((u2 - u1) * (y / _pI[key].oSegH)) + u1;
					u1Pos = u2Pos - ((u2 - u1) * _pI[key].yStp);
				} 

				// Set up the UVs
				uva = new UV(u2Pos, 1 - v1);
				uvb = new UV(u2Pos, 1 - v2);
				uvc = new UV(u1Pos, 1 - v2);
				uvd = new UV(u1Pos, 1 - v1);
                           
				addFace(new Face(vx0, vx3, vx1, _material, uva, uvd, uvc));
				addFace(new Face(vx0, vx1, vx2, _material, uva, uvc, uvb));
			} 
			
			// Connect along y==0 for the top connection or y=o.segmentsH for bottom
			if (edge == Y && x > 0 && y == pos && oppOr>0) {
				if (pUV && pUV[thisOr]) {
					u1 = pUV[thisOr][p][0];
					v1 = pUV[thisOr][p][1];
					u2 = pUV[thisOr][p][2];
					v2 = pUV[thisOr][p][3];
				}      

				if (nDir) {
					vx0 = _gen[p][y][x-1][orient];
					vx1 = _gen[p][y][x][oppOr];
					vx2 = _gen[p][y][x-1][oppOr];
					vx3 = _gen[p][y][x][orient];

					u1Pos = ((u2 - u1) * (x / _pI[key].oSegW)) + u1;
					u2Pos = u1Pos - ((u2 - u1) * _pI[key].xStp);
				} else {
					vx0 = _gen[p][y][x][orient];
					vx1 = _gen[p][y][x-1][oppOr];
					vx2 = _gen[p][y][x][oppOr];
					vx3 = _gen[p][y][x - 1][orient];

					u2Pos = ((u2 - u1) * (x / _pI[key].oSegW)) + u1;
					u1Pos = u2Pos - ((u2 - u1) * _pI[key].xStp);
				}

				// Set up the UVs
				uva = new UV(u2Pos, 1 - v2);
				uvb = new UV(u2Pos, 1 - v1);
				uvc = new UV(u1Pos, 1 - v1);
				uvd = new UV(u1Pos, 1 - v2);

				addFace(new Face(vx0, vx3, vx1, _material, uva, uvd, uvc));
				addFace(new Face(vx0, vx1, vx2, _material, uva, uvc, uvb));
			}
		}
		
		private function fillPatchMirrorHoles(pUV:Array, fillPoints:Array, key:String, p:int, segW:int, segH:int):void {
			key;//TODO : FDT Warning
			for each (var vData:Array in fillPoints) {
				var vId:int = vData[0];
				var vOr:Array = vData[1];
				var x:int;
				var y:int;
				switch (vId) {
					case TOPLEFT: x = 0; y = 0; break;
					case TOPRIGHT: x = segW; y = 0; break;
					case BOTTOMLEFT: x = 0; y = segH; break;
					case BOTTOMRIGHT: x = segW; y = segH; break;
				}
				vx0 = _gen[p][y][x][vOr[0]];
				vx1 = _gen[p][y][x][vOr[1]];
				vx2 = _gen[p][y][x][vOr[2]];
				vx3 = _gen[p][y][x][vOr[3]];
				vx4 = _gen[p][y][x][vOr[4]];
				vx5 = _gen[p][y][x][vOr[5]];
				vx6 = _gen[p][y][x][vOr[6]];
				vx7 = _gen[p][y][x][vOr[7]];
				if (vx0 && vx1 && vx2 && vx3) {
					u1 = v1 = 0; u2 = v2 = 1;
					if (pUV) {
						if (pUV[vOr]) {
							u1 = pUV[vOr][p][0];
							v1 = pUV[vOr][p][1];
							u2 = pUV[vOr][p][2];
							v2 = pUV[vOr][p][3];
						}      
						// Set up the UVs
						uva = new UV(u2, 1 - v2);
						uvb = new UV(u2, 1 - v1);
						uvc = new UV(u1, 1 - v1);
						uvd = new UV(u1, 1 - v2);
					}
					addFace(new Face(vx0, vx3, vx1, _material, uva, uvb, uvc));
					addFace(new Face(vx0, vx1, vx2, _material, uva, uvc, uvd));
				} else if (vx4 && vx5 && vx6 && vx7) {
					u1 = v1 = 0; u2 = v2 = 1;
					if (pUV) {
						if (pUV[vOr]) {
							u1 = pUV[vOr][p][0];
							v1 = pUV[vOr][p][1];
							u2 = pUV[vOr][p][2];
							v2 = pUV[vOr][p][3];
						}      
						// Set up the UVs
						uva = new UV(u2, 1 - v2);
						uvb = new UV(u2, 1 - v1);
						uvc = new UV(u1, 1 - v1);
						uvd = new UV(u1, 1 - v2);
					}
					addFace(new Face(vx4, vx5, vx7, _material, uvb, uvc, uvd));
					addFace(new Face(vx4, vx6, vx5, _material, uvb, uvb, uvc));
				} else {
					trace("BezierPatch Error-fillPatchMirrorHoles: Incorrect orientations defined for this hole fill");
				}
			}
		}
		
		// Scale a Vertex and apply an x, y or z offset for a new vertex or a closely matching one
		private function vScaleXYZ(v:Vertex, x:Number, y:Number, xS:Number, yS:Number, zS:Number):Vertex {
			var sV:Vertex =  new Vertex(Math.round(((v.x * xS) + (xOffset * xS))*resol)/resol, Math.round(((v.y * yS) + (yOffset * yS))*resol)/resol, Math.round(((v.z * zS) + (zOffset * zS))*resol)/resol);

			if (x == 0 || (Math.round(x*resol)/resol) == 1 || y == 0 || (Math.round(y*resol)/resol) == 1) { 
				// Check to see if a pre vertex has been defined already (on and edge at least)
				for each (var cV:Vertex in _edgeCache) {
					if (sV.x == cV.x && sV.y == cV.y && sV.z == cV.z) {
						return cV;                                    
					}
				}
			}
			
			if (v.x == sV.x && v.y == sV.y && v.z == sV.z) 
				return v;
			else
				return sV;
		}

		// Scale a Vertex and apply an x, y or z offset for a new vertex - reuses vertex
		private function vRescaleXYZ(v:Vertex, xS:Number, yS:Number, zS:Number):void {
			v.x =(v.x * xS) + (xOffset * xS);
			v.y =(v.y * yS) + (yOffset * yS);
			v.z =(v.z * zS) + (zOffset * zS);
		}		
	}
}