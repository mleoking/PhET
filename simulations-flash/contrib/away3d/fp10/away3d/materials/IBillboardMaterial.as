package away3d.materials
{
    import away3d.core.draw.*;

    /**
    * Interface for materials that are capable of drawing billboards.
    */
    public interface IBillboardMaterial extends IMaterial
    {
    	/**
    	 * Sends data from the material coupled with data from the <code>DrawBillboard</code> primitive to the render session.
    	 */
        function renderBillboard(bill:DrawBillboard):void;
    }
}
