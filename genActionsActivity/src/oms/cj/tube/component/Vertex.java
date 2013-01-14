/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package oms.cj.tube.component;

import android.util.Log;

@SuppressWarnings("unused")
public class Vertex {
	private static final String TAG="Vertex";
	
	public float x;
	public float y;
	public float z;
	
	Vertex() {
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

	public Vertex(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		//Log.i(TAG, "Vertex(...): " + "x=" + Float.toString(x));
		//Log.i(TAG, "Vertex(...): " + "y=" + Float.toString(y));
		//Log.i(TAG, "Vertex(...): " + "z=" + Float.toString(z));
	}
	
	public boolean equals(Object other) {
		if (other instanceof Vertex) {
			Vertex v = (Vertex)other;
			return (x == v.x && y == v.y && z == v.z);
		}
		return false;
	}
}
