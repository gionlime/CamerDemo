# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

cmake_minimum_required(VERSION 3.4.1)


add_library(naenginelib SHARED na-engine-jni.cpp na-engine.cpp)

find_library(log-lib log)
find_library(OpenGLES_LIBRARIES GLESv2)


target_link_libraries(naenginelib ${log-lib} ${OpenGLES_LIBRARIES})
