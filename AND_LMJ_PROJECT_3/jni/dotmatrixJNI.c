#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <stdio.h>
#include <assert.h>
#include <jni.h>

JNIEXPORT void JNICALL Java_edu_skku_lmjjni_DotmatrixJNI_DotMatrixControl(
		JNIEnv* env, jobject thiz, jstring str){
	const char *pStr;
	int fd, len;

	pStr = (*env)->GetStringUTFChars(env, str, 0);
	len = (*env)->GetStringLength(env, str);

	fd = open("/dev/lmj_dotmatrix", O_RDWR | O_SYNC);

	write(fd, pStr, len);
	close(fd);

	(*env)->ReleaseStringUTFChars(env, str, pStr);
}

