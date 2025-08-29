:: Build release APK
call gradlew assembleRelease
if %errorlevel% neq 0 exit /b %errorlevel%

call apksigner sign --ks ainaa.jks --ks-key-alias ainaa --out ainaa.apk app\build\outputs\apk\release\app-release-unsigned.apk

call apksigner verify ainaa.apk