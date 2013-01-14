package oms.cj.tube;

import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

public class Ray {
	public Vector3f mvOrigin = new Vector3f();
	public Vector3f mvDirection = new Vector3f();

	private static final float EPSILON = 0.000001f;

	
	//只考虑1side的face
	public boolean intersectoneside(Vector3f v0, Vector3f v1, Vector3f v2, Vector4f loc) {
		Vector3f diff = new Vector3f();
		Vector3f edge1 = new Vector3f();
		Vector3f edge2 = new Vector3f();
		Vector3f norm = new Vector3f();
		Vector3f tmp = new Vector3f();
		diff.sub(mvOrigin, v0);
		edge1.sub(v1, v0);
		edge2.sub(v2, v0);
		norm.cross(edge1, edge2);

		float dirDotNorm = mvDirection.dot(norm);
		float sign = 0.0f;

		if (dirDotNorm > EPSILON) {
			sign = 1;
			return false;	//do not consider the positive intersection 
		} else if (dirDotNorm < -EPSILON) {
			sign = -1;
			dirDotNorm = -dirDotNorm;
		} else {
			// 射线和三角形平行，不可能相交
			return false;
		}

		tmp.cross(diff, edge2);
		float dirDotDiffxEdge2 = sign * mvDirection.dot(tmp);
		if (dirDotDiffxEdge2 >= 0.0f) {
			tmp.cross(edge1, diff);
			float dirDotEdge1xDiff = sign * mvDirection.dot(tmp);
			if (dirDotEdge1xDiff >= 0.0f) {
				if (dirDotDiffxEdge2 + dirDotEdge1xDiff <= dirDotNorm) {
					float diffDotNorm = -sign * diff.dot(norm);
					if (diffDotNorm >= 0.0f) {
						//检测到相交事件
						//如果不需要计算精确相交点，则直接返回
						if (loc == null) {
							return true;
						}
						// 计算相交点具体位置，存储在Vector4f的x,y,z中，把距离存储在w中
						float inv = 1f / dirDotNorm;
						float t = diffDotNorm * inv;
						
						loc.set(mvOrigin);
						loc.add(mvDirection.x * t, mvDirection.y * t,
								mvDirection.z * t);
						loc.w = t;

						return true;
					}
				}
			}
		}

		return false;		
	}
}
