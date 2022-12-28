package edu.skku.lmjjni;

public class DotmatrixJNI {
	static {
		System.loadLibrary("JNIDriver");
	}
	
	public native void DotMatrixControl(String str);
}
