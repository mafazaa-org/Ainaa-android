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

:: Check current branch
git branch --show-current > current_branch.txt
set /p CURRENT_BRANCH=<current_branch.txt
del current_branch.txt
if not "%CURRENT_BRANCH%"=="prod" (
    echo Must be on production branch to release.
    exit /b 1
)


git diff --cached --quiet
if %ERRORLEVEL% neq 0 (
    echo Warning: There are staged but uncommitted changes.
    git diff --cached --name-only
    endlocal
    exit /b 1
)

git diff --quiet
if %ERRORLEVEL% neq 0 (
    echo Error: There are unstaged changes in the working directory.
    git diff --name-only
    endlocal
    exit /b 1
)

:: Check for untracked files
git status --porcelain | findstr "^??" >nul
if %ERRORLEVEL% equ 0 (
    echo Error: There are untracked files in the repository.
    git status --porcelain | findstr "^??"
    endlocal
    exit /b 1
)

git fetch upstream
if %ERRORLEVEL% neq 0 (
    echo Error: Failed to fetch from upstream.
    exit /b 1
)

:: Compare local branch with upstream branch
git status -uno | findstr /C:"Your branch is up to date with" >nul
if %ERRORLEVEL% equ 0 (
    echo The local branch '%CURRENT_BRANCH%' is up to date with remote upstream.
) else (
    git status -uno | findstr /C:"Your branch is behind" >nul
    if %ERRORLEVEL% equ 0 (
        echo The local branch '%CURRENT_BRANCH%' is behind remote upstream. Run 'git pull' to update.
        exit /b 1
    ) else (
        git status -uno | findstr /C:"Your branch is ahead of" >nul
        if %ERRORLEVEL% equ 0 (
            echo The local branch '%CURRENT_BRANCH%' is ahead of remote upstream. Local changes need to be pushed or reset.
            exit /b 1
        ) else (
            echo The local branch '%CURRENT_BRANCH%' has diverged from remote upstream. Resolve conflicts or reset as needed.
            exit /b 1
        )
    )
)

gh release view %NEW_VERSION_V% -R https://github.com/mafazaa-org/Ainaa-android >nul 2>&1
if %ERRORLEVEL% equ 0 (
    echo Release with tag '%NEW_VERSION_V%' already exists!
    exit /b 1
) else (
    echo there's no release for the version '%NEW_VERSION_V%' on github
)

echo Repository is clean: no unstaged changes, untracked files, or staged changes.


set /p NEW_VERSION_CODE=<versionCode
set /a NEW_VERSION_CODE=%NEW_VERSION_CODE%+1

:: Update version in build.gradle.kts
powershell -Command "(Get-Content app\build.gradle.kts) -replace 'versionCode = \d+', 'versionCode = 1' | Set-Content app\build.gradle.kts"
powershell -Command "(Get-Content app\build.gradle.kts) -replace 'versionName = \".*\"', 'versionName = \"%NEW_VERSION_V%\"' | Set-Content app\build.gradle.kts"

echo Building release APK and Bundle...

:: Clean the project
call gradlew clean
if %errorlevel% neq 0 exit /b %errorlevel%

:: Build release APK
call gradlew assembleRelease
if %errorlevel% neq 0 exit /b %errorlevel%

:: Build Android App Bundle
call gradlew bundleRelease
if %errorlevel% neq 0 exit /b %errorlevel%

git add .
git commit -m "updated version info to %NEW_VERSION_V%"

git tag %NEW_VERSION_V% -f
git push upstream
git push --tags upstream -f

:: Create GitHub release with both APK and AAB
gh release create %NEW_VERSION_V% --generate-notes ^
    "app\build\outputs\apk\release\app-release.apk#app-release.apk" ^
    "app\build\outputs\bundle\release\app-release.aab#app-release.aab"

echo.
echo Release %NEW_VERSION_V% completed successfully!
echo APK location: app\build\outputs\apk\release\app-release.apk
echo AAB location: app\build\outputs\bundle\release\app-release.aab
echo.

echo %NEW_VERSION_CODE% > versionCode

endlocal