@echo off
REM Dispatcher Windows script (copied from project root)
set "ROOT=%~dp0.."
set "VERSION=${tinystructVersion}"

set "MAVEN_REPO=%USERPROFILE%\.m2\repository\org\tinystruct\tinystruct"
set "DEFAULT_JAR_FILE=%MAVEN_REPO%\%VERSION%\tinystruct-%VERSION%.jar"
set "DEFAULT_JAR_FILE_WITH_DEPENDENCIES=%MAVEN_REPO%\%VERSION%\tinystruct-%VERSION%-jar-with-dependencies.jar"

if exist "%DEFAULT_JAR_FILE_WITH_DEPENDENCIES%" (
    set "JAR_PATH=%DEFAULT_JAR_FILE_WITH_DEPENDENCIES%"
) else (
    set "JAR_PATH=%DEFAULT_JAR_FILE%"
)

if "%JAVA_HOME%"=="" (
    echo Error: JAVA_HOME not found in your environment. >&2
    exit /B 1
)

set "JAVA_CMD=%JAVA_HOME%\bin\java.exe"

if not exist "mvnw" (
    echo Maven Wrapper not found. Extracting from JAR...
    %JAVA_CMD% -cp "%JAR_PATH%" org.tinystruct.system.Dispatcher maven-wrapper --jar-file-path "%JAR_PATH%" --destination-dir "%ROOT%"
    if exist "%ROOT%\maven-wrapper.zip" (
        powershell -Command "Expand-Archive -Path '%ROOT%\\maven-wrapper.zip' -DestinationPath '%ROOT%'"
        del /F /Q "%ROOT%\maven-wrapper.zip"
        echo Maven wrapper setup completed.
    ) else (
        echo Error: Maven wrapper ZIP file not found in JAR.
        exit /B 1
    )
)

set "classpath=%ROOT%\target\classes;%ROOT%\lib\tinystruct-%VERSION%-jar-with-dependencies.jar;%ROOT%\lib\tinystruct-%VERSION%.jar;%ROOT%\lib\*;%ROOT%\WEB-INF\lib\*;%ROOT%\WEB-INF\classes;%USERPROFILE%\.m2\repository\org\tinystruct\tinystruct\%VERSION%\tinystruct-%VERSION%-jar-with-dependencies.jar;%USERPROFILE%\.m2\repository\org\tinystruct\tinystruct\%VERSION%\tinystruct-%VERSION%.jar"

%JAVA_CMD% -cp "%classpath%" org.tinystruct.system.Dispatcher %*
