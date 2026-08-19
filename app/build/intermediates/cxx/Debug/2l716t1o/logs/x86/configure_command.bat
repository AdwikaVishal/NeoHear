@echo off
"C:\\Users\\Saatvika Reddy\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HC:\\Users\\Saatvika Reddy\\NeoHear\\app\\src\\main\\cpp" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=26" ^
  "-DANDROID_PLATFORM=android-26" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=C:\\Users\\Saatvika Reddy\\AppData\\Local\\Android\\Sdk\\ndk\\27.1.12297006" ^
  "-DCMAKE_ANDROID_NDK=C:\\Users\\Saatvika Reddy\\AppData\\Local\\Android\\Sdk\\ndk\\27.1.12297006" ^
  "-DCMAKE_TOOLCHAIN_FILE=C:\\Users\\Saatvika Reddy\\AppData\\Local\\Android\\Sdk\\ndk\\27.1.12297006\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=C:\\Users\\Saatvika Reddy\\AppData\\Local\\Android\\Sdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_CXX_FLAGS=-std=c++17" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=C:\\Users\\Saatvika Reddy\\NeoHear\\app\\build\\intermediates\\cxx\\Debug\\2l716t1o\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=C:\\Users\\Saatvika Reddy\\NeoHear\\app\\build\\intermediates\\cxx\\Debug\\2l716t1o\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BC:\\Users\\Saatvika Reddy\\NeoHear\\app\\.cxx\\Debug\\2l716t1o\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
