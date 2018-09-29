LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_CERTIFICATE := platform

LOCAL_STATIC_ANDROID_LIBRARIES := \
    android-support-v4 \
    android-support-v7-appcompat \
	
LOCAL_SRC_FILES := $(call all-java-files-under, app/src/main/java)
LOCAL_MANIFEST_FILE := app/src/main/AndroidManifest.xml
LOCAL_RESOURCE_DIR := \
                $(LOCAL_PATH)/app/src/main/res \
				frameworks/support/v7/appcompat/res

LOCAL_AAPT_FLAGS := \
        --auto-add-overlay \
        --extra-packages android.support.v7.appcompat
		
LOCAL_PACKAGE_NAME := CarLauncher
include $(BUILD_PACKAGE)