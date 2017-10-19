LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE := gdal
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/gdal/include
LOCAL_SRC_FILES := libgdal.a
LOCAL_EXPORT_LDLIBS := -lz
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gdaljni
LOCAL_SRC_FILES := gdal_wrap.cpp
LOCAL_STATIC_LIBRARIES := gdal
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := gdalconstjni
LOCAL_SRC_FILES := gdalconst_wrap.c
LOCAL_STATIC_LIBRARIES := gdal
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := ogrjni
LOCAL_SRC_FILES := ogr_wrap.cpp
LOCAL_STATIC_LIBRARIES := gdal
include $(BUILD_SHARED_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := osrjni
LOCAL_SRC_FILES := osr_wrap.cpp
LOCAL_STATIC_LIBRARIES := gdal
include $(BUILD_SHARED_LIBRARY)
