package ray.surface;

import carbine.MathExt;
import ray.accel.AxisAlignedBoundingBox;
import ray.material.Material;
import ray.math.Geometry;
import ray.math.Point2;
import ray.math.Point3;
import ray.math.Vector3;
import ray.misc.IntersectionRecord;
import ray.misc.LuminaireSamplingRecord;
import ray.misc.Ray;

/**
 * Represents a sphere as a center and a radius.
 *
 * @author ags
 */
public class Sphere extends Surface {
    
    /** Material for this sphere. */
    protected Material material = Material.DEFAULT_MATERIAL;
    
    /** The center of the sphere. */
    protected final Point3 center = new Point3();
    
    /** The radius of the sphere. */
    protected double radius = 1.0;
    
    /**
     * Default constructor, creates a sphere at the origin with radius 1.0
     */
    public Sphere() {        
    }
    
    /**
     * The explicit constructor. This is the only constructor with any real code
     * in it. Values should be set here, and any variables that need to be
     * calculated should be done here.
     *
     * @param newCenter The center of the new sphere.
     * @param newRadius The radius of the new sphere.
     * @param newMaterial The material of the new sphere.
     */
    public Sphere(Vector3 newCenter, double newRadius, Material newMaterial) {        
        material = newMaterial;
        center.set(newCenter);
        radius = newRadius;
        updateArea();
    }
    
    public void updateArea() {
    	area = 4 * Math.PI * radius*radius;
    	oneOverArea = 1. / area;
    }
    
    /**
     * @see ray1.surface.Surface#getMaterial()
     */
    public Material getMaterial() {
        return material;
    }
    
    /**
     * @see ray1.surface.Surface#setMaterial(ray1.material.Material)
     */
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    /**
     * Returns the center of the sphere in the input Point3
     * @param outPoint output space
     */
    public void getCenter(Point3 outPoint) {        
        outPoint.set(center);        
    }
    
    /**
     * @param center The center to set.
     */
    public void setCenter(Point3 center) {        
        this.center.set(center);        
    }
    
    /**
     * @return Returns the radius.
     */
    public double getRadius() {
        return radius;
    }
    
    /**
     * @param radius The radius to set.
     */
    public void setRadius(double radius) {
        this.radius = radius;
        updateArea();
    }
    
    public boolean chooseSamplePoint(Point3 p, Point2 seed, LuminaireSamplingRecord lRec) {
        Geometry.squareToSphere(seed, lRec.frame.w);
        lRec.frame.o.set(center);
        lRec.frame.o.scaleAdd(radius, lRec.frame.w);
        lRec.frame.initFromW();
        lRec.pdf = oneOverArea;
        lRec.emitDir.sub(p, lRec.frame.o);
        return true;
    }
        
    /**
     * @see ray1.surface.Surface#intersect(ray1.misc.IntersectionRecord,
     *      ray1.misc.Ray)
     */
    
	
    // W4160 TODO (A)
	// In this function, you need to test if the given ray intersect with a sphere.
	// You should look at the intersect method in other classes such as ray.surface.Triangle.java
	// to see what fields of IntersectionRecord should be set correctly.
	// The following fields should be set in this function
	//     IntersectionRecord.t
	//     IntersectionRecord.frame
	//     IntersectionRecord.surface
	//
	// Note: Although a ray is conceptually a infinite line, in practice, it often has a length,
	//       and certain rendering algorithm relies on the length. Therefore, here a ray is a 
	//       segment rather than a infinite line. You need to test if the segment is intersect
	//       with the sphere. Look at ray.misc.Ray.java to see the information provided by a ray.
	
    public boolean intersect(IntersectionRecord outRecord, Ray ray) 
    {
    	
    		//compute discriminant B^2-4AC where
		//At^2+Bt+C = (d · d)t^2 + 2d · (e − c)t + (e − c) · (e − c) − R^2 = 0.

    		Vector3 c = new Vector3(ray.origin.x-this.center.x,
    								ray.origin.y-this.center.y,
    							    ray.origin.z-this.center.z);    		
		double A = ray.direction.dot(ray.direction);
		double B = 2 * ray.direction.dot(c);
		double C = c.dot(c) - this.radius * this.radius;
		double D = B * B - 4.0 * A * C;
		
		// if  D < 0       no intersection
		// if  D = 0       ray is tangent to sphere
        // if  D > 0      ray intersects sphere in two points

		double t=0;
		if (D < 0) return false;
		else if (D==0)
		{
			t = -B / 2 * A;
			if (t < ray.start || t > ray.end)  return false;
		}else
		{
			double sqrt_D = Math.sqrt(D);
			double t1 = (-B - sqrt_D) / (2.0 * A);
			double t2 = (-B + sqrt_D) / (2.0 * A);

			
			//choose closer one if both are valid
			if (t1 >= ray.start && t1 <= ray.end &&
				t2 >= ray.start && t2 <= ray.end)
				t = t1 < t2 ? t1 : t2;
			else if (t1 >= ray.start && t1 <= ray.end) t = t1;
			else if (t2 >= ray.start && t2 <= ray.end) t = t2;
			else return false;
		}
		
		
		//Keep track of intersection record

		outRecord.t = t;
		outRecord.surface = this;
		ray.evaluate(outRecord.frame.o, t);
		outRecord.frame.w.sub(outRecord.frame.o, this.center);
		outRecord.frame.w.normalize();
		outRecord.frame.initFromW();

		return true;
    }
    
    /**
     * @see Object#toString()
     */
    public String toString() {
        return "sphere " + center + " " + radius + " " + material + " end";
    }
    
    /**
     * @see ray1.surface.Surface#addToBoundingBox(ray1.surface.accel.AxisAlignedBoundingBox)
     */
    public void addToBoundingBox(AxisAlignedBoundingBox inBox) {
        inBox.add(center.x - radius, center.y - radius, center.z - radius);
        inBox.add(center.x + radius, center.y + radius, center.z + radius);        
    }
    
}
