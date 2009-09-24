package org.papervision3d.core.render
{
	
	/**
	 * @Author Ralph Hauwert
	 */
	import org.papervision3d.core.proto.CameraObject3D;
	import org.papervision3d.core.proto.SceneObject3D;
	import org.papervision3d.core.render.command.IRenderListItem;
	import org.papervision3d.core.render.command.RenderableListItem;
	import org.papervision3d.core.render.data.RenderStatistics;
	import org.papervision3d.view.Viewport3D;	 

	public interface IRenderEngine
	{
		function renderScene(scene:SceneObject3D, camera:CameraObject3D, viewPort:Viewport3D):RenderStatistics
		function addToRenderList(renderCommand:RenderableListItem):int;
		function removeFromRenderList(renderCommand:IRenderListItem):int;
	}

}