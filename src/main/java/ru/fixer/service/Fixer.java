package ru.fixer.service;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class Fixer {
    private static Logger log = Logger.getLogger(Fixer.class.getName());
    private String backupPath = null;
    private String kamiliaPath = null;
    private String dataPath = null;

    public String getBackupPath() {
        return backupPath;
    }

    public String getKamiliaPath() {
        return kamiliaPath;
    }

    public String getDataPath() {
        return dataPath;
    }

    public Fixer(String kamiliaAbsolutePath){
        kamiliaPath = kamiliaAbsolutePath;
        dataPath = kamiliaAbsolutePath + "\\Data";
        backupPath = kamiliaAbsolutePath + "\\Data\\backup" +  LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd(HH;mm;ss)"));
    }
    public static boolean checkForCorrectKamiliaDirectory(File kamiliaDir){
        //must contain:
        //Data folder
        //fmodex.dll
        //GMFMODSimple.dll
        File[] dataFile = kamiliaDir.listFiles(x -> x.getName().equals("Data"));
        File[] fmodexFile =  kamiliaDir.listFiles(x -> x.getName().equals("fmodex.dll"));
        File[] gmfmodsimpleFile = kamiliaDir.listFiles(x -> x.getName().equals("GMFMODSimple.dll"));
        if((dataFile != null && fmodexFile != null && gmfmodsimpleFile != null) &&
                (dataFile.length == 1 && fmodexFile.length == 1 && gmfmodsimpleFile.length == 1)){
            return true;
        }
        else{
            log.warning("Wrong Kamilia directory");
            return false;
        }
    }
    public boolean createBackupDirectory(){
        File backupDirectory = new File(backupPath);
        if(backupDirectory.mkdir()){
            return true;
        }
        else{
            log.severe("Can't create 'backup' directory.");
            return false;
        }
    }
    public boolean newFilesExist(JFrame frame){
        //files must exist:
        //saveData
        //DeathTime
        //saveData2
        if(!new File(dataPath + "\\saveData").exists()){
            JOptionPane.showMessageDialog(frame,
                    "Error: new \"saveData\" file doesn't exist. Check if the conditions written in the application are met.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("saveData doesn't exist.");
            return false;
        }
        if(!new File(dataPath + "\\saveData2").exists()){
            JOptionPane.showMessageDialog(frame,
                    "Error: new \"saveData2\" file doesn't exist. Check if the conditions written in the application are met",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("saveData2 doesn't exist.");
            return false;
        }
        if(!new File(dataPath + "\\DeathTime").exists()){
            JOptionPane.showMessageDialog(frame,
                    "Error: new \"DeathTime\" file doesn't exist. Check if the conditions written in the application are met",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("DeathTime doesn't exist.");
            return false;
        }
        return true;
    }
    public boolean fixAndMoveOldFiles(JFrame frame){
        if(!fixAndMoveOneFile("saveData")){
            JOptionPane.showMessageDialog(frame,
                    "Error: can't fix file \"saveData\".",
                    "File fix error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("Can't fix saveData.");
            return false;
        }
        if(!fixAndMoveOneFile("DeathTime")){
            JOptionPane.showMessageDialog(frame,
                    "Error: can't fix file \"DeathTime\".",
                    "File fix error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("Can't fix DeathTime.");
            return false;
        }
        if(!fixAndMoveOneFile("saveData2")){
            JOptionPane.showMessageDialog(frame,
                    "Warning: can't fix file \"saveData2\".",
                    "File fix warning",
                    JOptionPane.WARNING_MESSAGE);
            log.warning("Can't fix saveData2.");
        }
        if(!fixAndMoveOneFile("Config")){
            JOptionPane.showMessageDialog(frame,
                    "Warning: can't fix file \"Config\".",
                    "File fix warning",
                    JOptionPane.WARNING_MESSAGE);
            log.warning("Can't fix Config.");
        }
        if(!fixAndMoveOneFile("Snapshot.bmp")){
            JOptionPane.showMessageDialog(frame,
                    "Warning: can't fix file \"Snapshot.bmp\".",
                    "File fix warning",
                    JOptionPane.WARNING_MESSAGE);
            log.warning("Can't fix Snapshot.");
        }
        return true;
    }
    private boolean fixAndMoveOneFile(String fileName){
        try {
            String newFilePath  = dataPath + "\\" + fileName;
            String oldFilePath = backupPath + "\\" + fileName;
            byte[] oldFileBytes = Files.readAllBytes(Paths.get(oldFilePath));
            if(fileName.equals("saveData") || fileName.equals("saveData2") || fileName.equals("DeathTime")){
                //replace only frist 4 bytes in saveData2/saveData/DeathTime
                byte[] newFileBytes = Files.readAllBytes(Paths.get(newFilePath));
                oldFileBytes[0] = newFileBytes[0];
                oldFileBytes[1] = newFileBytes[1];
                oldFileBytes[2] = newFileBytes[2];
                oldFileBytes[3] = newFileBytes[3];
            }

            //delete new (with no progress) file with correct first 4 bytes (not old)
            if(!new File(newFilePath).delete()){
                //log "Cant delete new empty files"
                return false;
            }

            //create new correct file
            File newFile = new File(newFilePath);
            if(newFile.createNewFile()){
                try(FileOutputStream writer = new FileOutputStream(newFilePath, false))
                {
                    writer.write(oldFileBytes);
                }
                catch(IOException e){
                    log.severe("Write in file error:" + e.getMessage());
                    return false;
                }
            }
        } catch (IOException e) {
            log.severe("IOException: " + e.getMessage());
            return false;
        }
        return true;
    }
    public boolean moveKamiliaFilesInBackup(JFrame frame){
        //check DeathTime and saveData existence
        if(!new File(dataPath + "\\DeathTime").exists()){
            JOptionPane.showMessageDialog(frame,
                    "Error: File 'DeathTime' doesn't exist.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("DeathTime doesn't exist");
            return false;
        }
        if(!new File(dataPath + "\\saveData").exists()){
            JOptionPane.showMessageDialog(frame,
                    "Error: File 'saveData' doesn't exist.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("saveData doesn't exist");
            return false;
        }

        //move files to backup
        if(!moveFileToBackup("DeathTime")){
            JOptionPane.showMessageDialog(frame,
                    "Error: File 'DeathTime' doesn't exist.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("Can't move DeathTime to backup");
            return false;
        }
        if(!moveFileToBackup("saveData")){
            JOptionPane.showMessageDialog(frame,
                    "Error: Can't move 'saveData' file in backup directory.",
                    "Move error",
                    JOptionPane.ERROR_MESSAGE);
            log.severe("Can't move saveData to backup");
            return false;
        }
        if(!moveFileToBackup("saveData2")){
            JOptionPane.showMessageDialog(frame,
                    "Can't move 'saveData2' file in backup directory.",
                    "Move warning",
                    JOptionPane.WARNING_MESSAGE);
            log.warning("Can't move saveData2 to backup");
        }
        if(!moveFileToBackup("Config")){
            JOptionPane.showMessageDialog(frame,
                    "Can't move 'Config' file in backup directory.",
                    "Move warning",
                    JOptionPane.WARNING_MESSAGE);
            log.warning("Can't move Config to backup");
        }
        if(!moveFileToBackup("Snapshot.bmp")){
            JOptionPane.showMessageDialog(frame,
                    "Can't move 'Snapshot.bmp' file in backup directory.",
                    "Move warning",
                    JOptionPane.WARNING_MESSAGE);
            log.warning("Can't move Snapshot.bmp to backup");
        }
        return true;
    }
    private boolean moveFileToBackup(String fileName) {
        Path result = null;
        String from = dataPath + "\\" + fileName;
        String to = backupPath + "\\" + fileName;
        try {
            result = Files.move(Paths.get(from), Paths.get(to));
        } catch (IOException e) {
            log.severe("Can't move " + fileName + " to backup: " + e.getMessage());
            return false;
        }
        return true;
    }
}

