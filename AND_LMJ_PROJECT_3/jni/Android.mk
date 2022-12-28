LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := JNIDriver
LOCAL_SRC_FILES := dotmatrixJNI.c piezoJNI.c ledJNI.c fullcolorledJNI.c textlcdJNI.c

include $(BUILD_SHARED_LIBRARY)
