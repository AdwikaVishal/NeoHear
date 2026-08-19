cmake_minimum_required(VERSION 3.22.1)
project("neohear")

# Native library: JNI bridge + DSP core
add_library(neohear SHARED
    native-bridge.cpp
    dsp/signal_processing.cpp
)

target_include_directories(neohear PRIVATE
    "${CMAKE_CURRENT_SOURCE_DIR}"
    "${CMAKE_CURRENT_SOURCE_DIR}/dsp"
)

find_library(log-lib log)

target_link_libraries(neohear
    ${log-lib}
    c++_shared
)
