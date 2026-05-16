@rem
@rem Gradle start script for Windows
@rem
@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  Gradle start up script for Windows
@rem
@rem ##########################################################################

setlocal

set APP_BASE_NAME=%~n0
set APP_HOME=%CD%

@if not "%APP_HOME%"=="%~dp0" @(
  set APP_HOME=%~dp0
)

set GRADLE_USER_HOME=%USERPROFILE%\.gradle

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

@if exist "%CLASSPATH%" (
  goto execute
)

echo.
echo ERROR: gradle-wrapper.jar is missing.
echo Please run: gradle wrapper
echo or generate the wrapper using Android Studio.
echo.
exit /b 1

:execute
@rem Execute Gradle wrapper
java -Dorg.gradle.appname=%APP_BASE_NAME% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
