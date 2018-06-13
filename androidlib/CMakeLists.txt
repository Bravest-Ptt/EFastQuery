# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)

add_library( AndroidLib SHARED src/main/jni/bmob_key_provider.c )
set_target_properties(AndroidLib PROPERTIES LINKER_LANGUAGE C)

include_directories(src/main/jni/include/)

find_library( log-lib log )
find_library( z-lib z )
find_library( m-lib m )
find_library( android-lib android )

target_link_libraries( AndroidLib ${log-lib} ${z-lib} ${m-lib} ${android-lib} )