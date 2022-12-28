package edu.skku.lmjjni;

public class LedJNI {
	static {
		System.loadLibrary("JNIDriver");
	}
	
	public native void on(char data);
}
