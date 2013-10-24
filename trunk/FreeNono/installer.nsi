; Installer script for FreeNono installation under Windows
; 2013-10-12 - FreeNono Development Team


!include "MUI.nsh"
 
;---------- General ----------

  !define EXEC_FILE "FreeNono.exe"
  
  Name "${PRODUCT} ${PRODUCT_VERSION}"
  Icon "src/resources/icon/icon_freenono.ico" 
  OutFile "${PRODUCT}-${PRODUCT_VERSION}-installer.exe"
  InstallDir "$PROGRAMFILES\${PRODUCT}"
  InstallDirRegKey HKCU "Software\${PRODUCT}" ""
  LicenseText "dist\LICENSE"
  
  ShowInstDetails show
  ShowUninstDetails show 
  CRCCheck On
  SetCompressor lzma
  ;Request application privileges for Windows Vista
  ;RequestExecutionLevel user


;---------- Pages Configuration ----------

  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "dist\LICENSE"
  !insertmacro MUI_PAGE_DIRECTORY
  !insertmacro MUI_PAGE_INSTFILES
 
    # These indented statements modify settings for MUI_PAGE_FINISH
    !define MUI_FINISHPAGE_NOAUTOCLOSE
    !define MUI_FINISHPAGE_RUN
    !define MUI_FINISHPAGE_RUN_NOTCHECKED
    !define MUI_FINISHPAGE_RUN_TEXT "Start FreeNono now"
    !define MUI_FINISHPAGE_RUN_FUNCTION .execProgram
    ;!define MUI_FINISHPAGE_SHOWREADME_NOTCHECKED
    ;!define MUI_FINISHPAGE_SHOWREADME "$INSTDIR\README"
  !insertmacro MUI_PAGE_FINISH
  
  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
 
  !insertmacro MUI_LANGUAGE "English"

 
;---------- Installer Sections ----------
Section "install" Installation
 
  ; Add files in installation directory
  SetOutPath "$INSTDIR"
  File "dist\${EXEC_FILE}"
  File "dist\*.jar"
  File "dist\LICENSE"
  File "dist\CHANGELOG"
  File "dist\LICENSE"
  File "dist\README"
  File /oname=FreeNono.ico "src/resources/icon/icon_freenono.ico"
  
  SetOutPath "$INSTDIR\lib"
  File "dist\lib\*.jar"
  SetOutPath "$INSTDIR\docs"
  File "dist\docs\*.*"
  SetOutPath "$INSTDIR\nonograms"
  File "dist\nonograms\*.nonopack"
  
  ; Set out path as working directory and create start-menu items
  SetOutPath "$INSTDIR"
  CreateDirectory "$SMPROGRAMS\${PRODUCT}"
  CreateShortCut "$SMPROGRAMS\${PRODUCT}\Uninstall.lnk" "$INSTDIR\Uninstall.exe" "" "$INSTDIR\Uninstall.exe" 0
  CreateShortCut "$SMPROGRAMS\${PRODUCT}\${PRODUCT}.lnk" "$INSTDIR\${EXEC_FILE}" "" "$INSTDIR\FreeNono.ico"
 
  ; Write uninstall information to the registry
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "DisplayName" "${PRODUCT}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteUninstaller "$INSTDIR\Uninstall.exe"
 
SectionEnd
 
 
;---------- Uninstaller Section ----------  
Section "Uninstall"
 
  ;Remove the installation directory 
  RMDir /r "$INSTDIR\*.*"    
  RMDir "$INSTDIR"
 
  ;Delete Start Menu Shortcuts
  Delete "$SMPROGRAMS\${PRODUCT}\*.*"
  RmDir  "$SMPROGRAMS\${PRODUCT}"
 
  ;Delete Uninstaller And Unistall Registry Entries
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\${PRODUCT}"
  DeleteRegKey HKEY_LOCAL_MACHINE "SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}"  
 
SectionEnd
 
 
;---------- Helper functions ----------    

Function .execProgram
  ExecShell "open" "$INSTDIR\${EXEC_FILE}"
FunctionEnd

Function .onInstSuccess
  MessageBox MB_OK "You have successfully installed ${PRODUCT}."
FunctionEnd
 
Function un.onUninstSuccess
  MessageBox MB_OK "You have successfully uninstalled ${PRODUCT}."
FunctionEnd
 
