#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <jni.h>

static int fd;

JNIEXPORT void JNICALL Java_edu_skku_lmjjni_PiezoJNI_open
  (JNIEnv * env, jobject obj){
	fd = open("/dev/lmj_piezo", O_WRONLY);
		assert(fd != -1);
}

JNIEXPORT void JNICALL Java_edu_skku_lmjjni_PiezoJNI_write
  (JNIEnv * env, jobject obj, jchar data){
	write(fd, &data, 1);
}

JNIEXPORT void JNICALL Java_edu_skku_lmjjni_PiezoJNI_close
  (JNIEnv * env, jobject obj){
	close(fd);
}

