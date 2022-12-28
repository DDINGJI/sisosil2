#include <unistd.h>
#include <fcntl.h>
#include <assert.h>
#include <jni.h>

JNIEXPORT void JNICALL Java_edu_skku_lmjjni_LedJNI_on
  (JNIEnv * env, jobject obj, jchar data){
	int fd;

		fd = open("/dev/lmj_led", O_WRONLY);
		assert(fd != 0);

		write(fd, &data, 1);
		close(fd);
}

