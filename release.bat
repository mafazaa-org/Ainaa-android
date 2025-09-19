@echo off
setlocal enabledelayedexpansion
chcp 65001

if "%~1"=="" (
    echo Usage: release.bat [version]
    echo Example: release.bat 1.0.0
    exit /b 1
)
set NEW_VERSION=%~1
set NEW_VERSION_V=v%NEW_VERSION%



set /p NEW_VERSION_CODE=<versionCode
set /a NEW_VERSION_CODE=%NEW_VERSION_CODE%+1

:: Update version in build.gradle.kts
powershell -Command "(Get-Content app\build.gradle.kts) -replace 'versionCode = \d+', 'versionCode = %NEW_VERSION_CODE%' | Set-Content app\build.gradle.kts"
powershell -Command "(Get-Content app\build.gradle.kts) -replace 'versionName = \".*\"', 'versionName = \"%NEW_VERSION_V%\"' | Set-Content app\build.gradle.kts"

echo Building release APK and Bundle...

:: renaming old app to keep it
del _ainaa.apk
del _ainaa.apk.idsig
del _ainaa.aab
ren ainaa.apk _ainaa.apk
ren ainaa.apk.idsig _ainaa.apk.idsig
ren ainaa.aab _ainaa.aab

:: Clean the project
call gradlew clean
if %errorlevel% neq 0 exit /b %errorlevel%

call build_release.bat

:: Build AAB
call gradlew bundleRelease
if %errorlevel% neq 0 exit /b %errorlevel%


:: Signing the aab
call jarsigner -keystore ainaa.jks -signedjar ainaa.aab app\build\outputs\bundle\release\app-release.aab ainaa

call apksigner verify --print-certs ainaa.aab

echo %NEW_VERSION_CODE% > versionCode

git add .
git commit -m "updated version info to %NEW_VERSION_V%"

git tag %NEW_VERSION_V% -f
git push upstream
git push --tags upstream -f

:: Create GitHub release with both APK and AAB
gh release create %NEW_VERSION_V% --generate-notes ^
    "ainaa.apk#ainaa.apk"

echo.
echo Release %NEW_VERSION_V% completed successfully!
echo APK location: ainaa.apk
echo Bundle location: ainaa.aab
echo.


endlocal