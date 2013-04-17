package away3d.materials
{
    import away3d.core.draw.*;

    /**
    * Interface for materials that are capable of drawing line segments.
    */
    public interface ISegmentMaterial extends IMaterial
    {
    	/**
    	 * Sends data from the material coupled with data from the <code>DrawSegment</code> primitive to the render session.
    	 */
        function renderSegment(seg:DrawSegment):void;
    }
}
