package ray.renderer;

import ray.brdf.BRDF;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Vector3;
import ray.misc.Color;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;
import ray.misc.Scene;

/**
 * This class computes direct illumination at a surface by the simplest possible approach: it estimates
 * the integral of incident direct radiance using Monte Carlo integration with a uniform sampling
 * distribution.
 * 
 * The class has two purposes: it is an example to serve as a starting point for other methods, and it
 * is a useful base class because it contains the generally useful <incidentRadiance> function.
 * 
 * @author srm, Changxi Zheng (at Columbia)
 */
public class ProjSolidAngleIlluminator extends DirectIlluminator 
{
	public void directIllumination(Scene scene, Vector3 incDir, Vector3 outDir,
			IntersectionRecord iRec, Point2 seed, Color outColor) 
	{
		//Declarations 
		Color BRDF = new Color();
		Color radiance = new Color();
		IntersectionRecord intersection_rec = new IntersectionRecord();
		LuminaireSamplingRecord lRec = new LuminaireSamplingRecord();
		Ray shadowRay = new Ray();

		//normal vector 
		Vector3 N = new Vector3();
		N.set(iRec.frame.w); N.normalize();
		//Random incident direction 
		Geometry.squareToPSAHemisphere(seed, incDir);
		iRec.frame.frameToCanonical(incDir); incDir.normalize();


		//Reflection = 2(N⋅L)N−L 
		outDir.set(N);
		outDir.scale(2 * N.dot(incDir));
		outDir.sub(incDir);
		outDir.normalize();

		
		shadowRay.set(iRec.frame.o, incDir);
		shadowRay.makeOffsetRay();
		//finds if it hits a surface
		if (scene.getFirstIntersection(intersection_rec, shadowRay)) 
		{
			//compute BRDF
			iRec.surface.getMaterial().getBRDF(iRec).evaluate(iRec.frame, incDir, outDir, BRDF);
			//compute emitted radiance if the surface is a light surface
			if (intersection_rec.surface.getMaterial().isEmitter())
			{
				intersection_rec.surface.getMaterial().emittedRadiance(lRec, radiance);
				radiance.scale(N.dot(incDir));
			}
			
			// pi * brdf * radiance 
			outColor.set(Math.PI);
			outColor.scale(BRDF);
			outColor.scale(radiance);

		} else outColor.set(0);
	}

}