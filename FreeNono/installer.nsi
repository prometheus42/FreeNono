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
  !define HELPURL "http://www.freenono.org"
  !define UPDATEURL "http://www.freenono.org"
  !define ABOUTURL "http://www.freenono.org"
  
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
  !insertmacro MUI_UNPAGE_FINISH
 
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
 
  ; compute estimated size for uninstaller
  !include "FileFunc.nsh"
  ${GetSize} "$INSTDIR" "/S=0K" $0 $1 $2
  IntFmt $0 "0x%08X" $0
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "EstimatedSize" "$0"
 
  ; Write uninstall information to the registry
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "DisplayName" "${PRODUCT}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "UninstallString" "$INSTDIR\Uninstall.exe"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "InstallLocation" "$INSTDIR"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "Publisher" "FreeNono Development Team"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "DisplayIcon" "$\"$INSTDIR\FreeNono.ico$\""
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "HelpLink" "$\"${HELPURL}$\""
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "URLUpdateInfo" "$\"${UPDATEURL}$\""
  WriteRegStr HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "URLInfoAbout" "$\"${ABOUTURL}$\""
  ; There is no option for modifying or repairing the install
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "NoModify" 1
  WriteRegDWORD HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" "NoRepair" 1
  
  WriteUninstaller "$INSTDIR\Uninstall.exe"
 
SectionEnd
 
 
;---------- Uninstaller Section ----------  
Section "Uninstall"

  ;Remove the installation directory
  RMDir /R "$INSTDIR"
 
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
  ;MessageBox MB_OK "You have successfully installed ${PRODUCT}."
FunctionEnd
 
Function un.onUninstSuccess
  ;MessageBox MB_OK "You have successfully uninstalled ${PRODUCT}."
FunctionEnd


Function un.deleteUserData
  SetShellVarContext all
  MessageBox MB_OK "Deleting user data for ${PRODUCT} from $LOCALAPPDATA ."
  ;RMDir /R "$LOCALAPPDATA\$PRODUCT"
  
  ;Delete "$LOCALAPPDATA\$PRODUCT\*.*"
  ;Delete /REBOOTOK "<filename>"
  ;RMDir /R /REBOOTOK directoryname
FunctionEnd


Function .onInit
  ReadRegStr $R0 HKLM \
  "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT}" \
  "UninstallString"
  StrCmp $R0 "" done
 
  MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION \
  "${PRODUCT} is already installed. $\n$\nClick $\"OK$\" remove the \
  previous version or $\"Cancel$\" to cancel this upgrade." \
  IDOK uninst
  Abort

  uninst:
    ClearErrors
    HideWindow
    ExecWait '$R0 _?=$INSTDIR' ;Do not copy the uninstaller to a temp file
    ; OR ->  Exec $INSTDIR\uninst.exe ; instead of the ExecWait line
     
    IfErrors no_remove_uninstaller done
      ;You can either use Delete /REBOOTOK in the uninstaller or add some code
      ;here to remove the uninstaller. Use a registry key to check
      ;whether the user has chosen to uninstall. If you are using an uninstaller
      ;components page, make sure all sections are uninstalled.
    no_remove_uninstaller:
  done:
    BringToFront
FunctionEnd

