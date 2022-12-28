package edu.skku.lmjjni;

public class FullcolorledJNI {
	static {
		System.loadLibrary("JNIDriver");
	}
	
	public native void FLEDControl(int ledNum, int red, int green, int blue);
}
