package ray.renderer;

import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.Ray;
import ray.misc.Scene;
import ray.sampling.SampleGenerator;

public abstract class PathTracer extends DirectOnlyRenderer {

    protected int depthLimit = 5;
    protected int backgroundIllumination = 1;

    public void setDepthLimit(int depthLimit) { this.depthLimit = depthLimit; }
    public void setBackgroundIllumination(int backgroundIllumination) { this.backgroundIllumination = backgroundIllumination; }

    @Override
    public void rayRadiance(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, Color outColor) {
    
        rayRadianceRecursive(scene, ray, sampler, sampleIndex, 0, outColor);
    }

    protected abstract void rayRadianceRecursive(Scene scene, Ray ray, SampleGenerator sampler, int sampleIndex, int level, Color outColor);

    public void gatherIllumination(Scene scene, Vector3 outDir, 
            IntersectionRecord iRec, SampleGenerator sampler, 
            int sampleIndex, int level, Color outColor) 
    {
    			//inner loop of recursive call 
    			if ( level > depthLimit) return;
    			
    			//Declarations 
			IntersectionRecord intersection_rec = new IntersectionRecord();
			Color BRDF = new Color();
			Color radiance = new Color();

			//for incident direction
			Point2 seed = new Point2();
    			sampler.sample(sampleIndex, level, seed);
    			
    			//Generate a random incident direction according to proj solid angle
    			Vector3 incDir = new Vector3();
    			Geometry.squareToPSAHemisphere(seed, incDir);
    			iRec.frame.frameToCanonical(incDir); incDir.normalize();
    			
    			Ray shadowRay = new Ray(iRec.frame.o, incDir);
    			shadowRay.makeOffsetRay();
    			if (scene.getFirstIntersection(intersection_rec, shadowRay))
    			{
    				// compute BRDF
    				iRec.surface.getMaterial().getBRDF(iRec).evaluate(iRec.frame, outDir, incDir, BRDF);
    				
    				// RECURSIVELY find incident radiance from that direction
    				rayRadianceRecursive(scene, shadowRay, sampler, sampleIndex, level + 1, radiance);

    				//Estimate the reflected radiance
    				// pi * brdf * radiance
    				outColor.set(Math.PI);
    				outColor.scale(BRDF);
    				outColor.scale(radiance);
    			}
    			else
    			{
    				outColor.set(0);
    			}
    }
}
