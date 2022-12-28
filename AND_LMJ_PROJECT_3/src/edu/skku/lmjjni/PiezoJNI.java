package edu.skku.lmjjni;

public class PiezoJNI {
	static {
		System.loadLibrary("JNIDriver");
	}
	
	public native void open();
	public native void write(char data);
	public native void close();
}
