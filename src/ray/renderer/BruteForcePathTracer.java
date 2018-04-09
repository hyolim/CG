package ray.renderer;


import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

/*
 * Global(Indirect) Illumination 
 * receives light after the light rays interact with other objects in the scene. 
 * Make a recursive call to get all radiance incident on the surface
 */

public class BruteForcePathTracer extends PathTracer {
    /**
     * @param scene
     * @param ray
     * @param sampler
     * @param sampleIndex
     * @param outColor
     */
	
    protected void rayRadianceRecursive(Scene scene, Ray ray, 
            SampleGenerator sampler, int sampleIndex, int level, Color outColor) 
    {
    		IntersectionRecord iRec = new IntersectionRecord();
		Color radiance = new Color();

		// If the ray depth is less than the limit (depthLimit),
		if (level < depthLimit)
		{
			if(scene.getFirstIntersection(iRec, ray))
			{
				//1) compute the emitted light radiance from the current surface 
				//	 if the surface is a light surface
				if (iRec.surface.getMaterial().isEmitter())
				{
					emittedRadiance(iRec, ray.direction, outColor);
				}
				else
				{
					outColor.set(0);
				}
				
				//2)recursively compute reflected radiance from 
    				//  other lights and objects and add them to get the total radiance 
				gatherIllumination(scene, ray.direction, iRec, sampler, sampleIndex, level, radiance);
				outColor.add(radiance);
				
			}
			else //else just set background
			{
				scene.getBackground().evaluate(ray.direction, outColor);
			}
		}
    }

}
