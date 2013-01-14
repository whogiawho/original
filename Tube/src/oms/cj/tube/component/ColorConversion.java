package oms.cj.tube.component;

public class ColorConversion {
	
	/**
     * sRGB to XYZ conversion matrix
     */
    public static double[][] M   = {
    	{0.4124, 0.3576,  0.1805},
        {0.2126, 0.7152,  0.0722},
        {0.0193, 0.1192,  0.9505}
    };

    /**
     * XYZ to sRGB conversion matrix
     */
    public static double[][] Mi  = {
    	{ 3.2406, -1.5372, -0.4986},
        {-0.9689,  1.8758,  0.0415},
        { 0.0557, -0.2040,  1.0570}
    };
    public static double[] D65 = {95.0429, 100.0, 108.8900};
    public static double[] whitePoint = D65;
    
    /**
     * Convert RGB to XYZ
     * @param R
     * @param G
     * @param B
     * @return XYZ in double array.
     */
    public static double[] RGBtoXYZ(int R, int G, int B) {
      double[] result = new double[3];

      // convert 0..255 into 0..1
      double r = R / 255.0;
      double g = G / 255.0;
      double b = B / 255.0;

      // assume sRGB
      if (r <= 0.04045) {
        r = r / 12.92;
      }
      else {
        r = Math.pow(((r + 0.055) / 1.055), 2.4);
      }
      if (g <= 0.04045) {
        g = g / 12.92;
      }
      else {
        g = Math.pow(((g + 0.055) / 1.055), 2.4);
      }
      if (b <= 0.04045) {
        b = b / 12.92;
      }
      else {
        b = Math.pow(((b + 0.055) / 1.055), 2.4);
      }

      r *= 100.0;
      g *= 100.0;
      b *= 100.0;

      // [X Y Z] = [r g b][M]
      result[0] = (r * M[0][0]) + (g * M[0][1]) + (b * M[0][2]);
      result[1] = (r * M[1][0]) + (g * M[1][1]) + (b * M[1][2]);
      result[2] = (r * M[2][0]) + (g * M[2][1]) + (b * M[2][2]);

      return result;
    }
    
    /**
     * Convert XYZ to LAB.
     * @param X
     * @param Y
     * @param Z
     * @return Lab values
     */
    public static double[] XYZtoLAB(double X, double Y, double Z) {

      double x = X / whitePoint[0];
      double y = Y / whitePoint[1];
      double z = Z / whitePoint[2];

      if (x > 0.008856) {
        x = Math.pow(x, 1.0 / 3.0);
      }
      else {
        x = (7.787 * x) + (16.0 / 116.0);
      }
      if (y > 0.008856) {
        y = Math.pow(y, 1.0 / 3.0);
      }
      else {
        y = (7.787 * y) + (16.0 / 116.0);
      }
      if (z > 0.008856) {
        z = Math.pow(z, 1.0 / 3.0);
      }
      else {
        z = (7.787 * z) + (16.0 / 116.0);
      }

      double[] result = new double[3];

      result[0] = (116.0 * y) - 16.0;
      result[1] = 500.0 * (x - y);
      result[2] = 200.0 * (y - z);

      return result;
    }
    
    /**
     * @param R
     * @param G
     * @param B
     * @return Lab values
     */
    public static double[] RGBtoLAB(int R, int G, int B) {
      return XYZtoLAB(RGBtoXYZ(R, G, B));
    }

    /**
     * @param RGB
     * @return Lab values
     */
    public static double[] RGBtoLAB(int[] RGB) {
      return XYZtoLAB(RGBtoXYZ(RGB));
    }
    
    /**
     * Convert XYZ to LAB.
     * @param XYZ
     * @return Lab values
     */
    public static double[] XYZtoLAB(double[] XYZ) {
      return XYZtoLAB(XYZ[0], XYZ[1], XYZ[2]);
    }

    /**
     * Convert RGB to XYZ
     * @param RGB
     * @return XYZ in double array.
     */
    public static double[] RGBtoXYZ(int[] RGB) {
      return RGBtoXYZ(RGB[0], RGB[1], RGB[2]);
    }
    
    public static double[] RGBtoHSV(int r, int g, int b){
    	float[] fhsvs = new float[3];
    	fhsvs = RGBtoHSB(r, g, b, null);
    	
    	double[] hsvs = new double[3];
    	for(int i=0;i<hsvs.length;i++)
    		hsvs[i] = fhsvs[i];
    	
    	return hsvs;
    }
    public static float[] RGBtoHSB(int r, int g, int b, float[] hsbvals) {
        float hue, saturation, brightness;
        if (hsbvals == null) {
            hsbvals = new float[3];
        }
        int cmax = (r > g) ? r : g;
        if (b > cmax) cmax = b;
        int cmin = (r < g) ? r : g;
        if (b < cmin) cmin = b;
       
        brightness = ((float) cmax) / 255.0f;
        if (cmax != 0)
           saturation = ((float) (cmax - cmin)) / ((float) cmax);
        else
           saturation = 0;
        if (saturation == 0)
           hue = 0;
        else {
           float redc = ((float) (cmax - r)) / ((float) (cmax - cmin));
           float greenc = ((float) (cmax - g)) / ((float) (cmax - cmin));
           float bluec = ((float) (cmax - b)) / ((float) (cmax - cmin));
           if (r == cmax)
               hue = bluec - greenc;
           else if (g == cmax)
               hue = 2.0f + redc - bluec;
           else
               hue = 4.0f + greenc - redc;
           hue = hue / 6.0f;
           if (hue < 0)
               hue = hue + 1.0f;
        }
        hsbvals[0] = hue;
        hsbvals[1] = saturation;
        hsbvals[2] = brightness;
        return hsbvals;
    }

	public static double getLABDistance(Color c1, Color c2){
		double[] lab1 = ColorConversion.RGBtoLAB(c1.getR(), c1.getG(), c1.getB());
		double[] lab2 = ColorConversion.RGBtoLAB(c2.getR(), c2.getG(), c2.getB());
		
		double r1 = lab1[0] - lab2[0];
		double r2 = lab1[1] - lab2[1];
		double r3 = lab1[2] - lab2[2];
		
		return distance(r1, r2, r3);
	}

	public final static int R1SET = 0x4;
	public final static int R2SET = 0x2;
	public final static int R3SET = 0x1;
	public static double distance(double r1, double r2, double r3){
		return Math.sqrt(r1*r1+r2*r2+r3*r3); 
	}
	public static double distance(double r1, double r2, double r3, int flag){
		boolean br1=false, br2=false, br3=false;
		if((flag&R1SET)!=0)
			br1=true;
		if((flag&R2SET)!=0)
			br2=true;
		if((flag&R3SET)!=0)
			br3=true;
		
		return distance(br1?r1:0, br2?r2:0, br3?r3:0);
	}
	
	public final static int REDSET = R1SET;
	public final static int GREENSET = R2SET;
	public final static int BLUESET = R3SET;
	public static double getRGBDistance(Color c1, Color c2, int flag){
		boolean br1=false, br2=false, br3=false;
		if((flag&REDSET)!=0)
			br1=true;
		if((flag&GREENSET)!=0)
			br2=true;
		if((flag&BLUESET)!=0)
			br3=true;
		
		double r1 = c1._red * 255 - c2._red*255; 
		double r2 = c1._green * 255 - c2._green * 255;
		double r3 = c1._blue * 255 - c2._blue * 255;

		return distance(br1?r1:0, br2?r2:0, br3?r3:0);
	}
	public static double getRGBDistance(Color c1, Color c2){
		return getRGBDistance(c1, c2, REDSET|GREENSET|BLUESET);
	}

}
