package ru.fixer;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Fixer {
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
            return false;
        }
    }
    public boolean createBackupDirectory(){
        File backupDirectory = new File(backupPath);
        if(backupDirectory.mkdir()){
            return true;
        }
        else{
            return false;
        }
    }
    public static void main(String[] args) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));


        // далее прога скажет, что надо зайти в игру, дойти до первого сейва и сохраниться
         try{
            while(true) {
                String readedLine = reader.readLine().toLowerCase().strip();
                if(readedLine.equals("continue")){
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // в каждом старом файле надо поменять первые 4 байта на 4 новые байта
        // после этого надо удалить НОВЫЕ сейвы и на их место переместить СТАРЫЕ
       ///////////////////////////////////////////////////////////////////////////fixOldFiles();

        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private void fixOldFiles(){
        fixOneFile("saveData");
        fixOneFile( "saveData2");
        fixOneFile("Config");
        fixOneFile("DeathTime");
        fixOneFile( "Snapshot.bmp");
    }
    private void fixOneFile(String fileName){
        //1)считать данные из старого файла по байтам (старый файл в бекапе (/Data/backup))
        //2)считать первые 4 байта из нового файла (новый файл в /Data)
        //3)в строке с данными из старого файла заменить первые 4 байта на новые
        //4)удалить новый файл и на его место поставить файл из байтов, полученных в пункте 3

        try {
            String newFilePath  = dataPath + "\\" + fileName;
            String oldFilePath = backupPath + "\\" + fileName;
            byte[] newFileBytes = Files.readAllBytes(Paths.get(newFilePath));
            byte[] oldFileBytes = Files.readAllBytes(Paths.get(oldFilePath));
            if(fileName.equals("saveData") || fileName.equals("saveData2") || fileName.equals("DeathTime")){
                oldFileBytes[0] = newFileBytes[0];
                oldFileBytes[1] = newFileBytes[1];
                oldFileBytes[2] = newFileBytes[2];
                oldFileBytes[3] = newFileBytes[3];
            }

            if(new File(newFilePath).delete()){
                System.out.println("New file delete success ");
            }
            else{
                System.out.println("Delete error");
                throw new RuntimeException();
            }

            //создаём готовый файл с заменёнными байтами
            File newFile = new File(newFilePath);
            if(newFile.createNewFile()){
                try(FileOutputStream writer = new FileOutputStream(newFilePath, false))
                {
                    // запись всей строки
                    writer.write(oldFileBytes);
                }
                catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean moveKamiliaFilesInBackup(JFrame frame){
        //check DeathTime and saveData existence
        if(!new File(dataPath + "\\DeathTime").exists()){
            JOptionPane.showMessageDialog(frame,
                    "Error: File 'DeathTime' doesn't exist.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(!new File(dataPath + "\\saveData").exists()){
            JOptionPane.showMessageDialog(frame,
                    "Error: File 'saveData' doesn't exist.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        //move files to backup
        if(!moveFileToBackup("DeathTime")){
            JOptionPane.showMessageDialog(frame,
                    "Error: File 'DeathTime' doesn't exist.",
                    "File error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(!moveFileToBackup("saveData")){
            JOptionPane.showMessageDialog(frame,
                    "Error: Can't move 'saveData' file in backup directory.",
                    "Move error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if(!moveFileToBackup("saveData2")){
            JOptionPane.showMessageDialog(frame,
                    "Can't move 'saveData2' file in backup directory.",
                    "Move warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if(!moveFileToBackup("Config")){
            //log
            JOptionPane.showMessageDialog(frame,
                    "Can't move 'Config' file in backup directory.",
                    "Move warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        if(!moveFileToBackup("Snapshot.bmp")){
            JOptionPane.showMessageDialog(frame,
                    "Can't move 'Snapshot.bmp' file in backup directory.",
                    "Move warning",
                    JOptionPane.WARNING_MESSAGE);
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
            System.out.println("Exception while moving file: " + e.getMessage());
        }
        if(result == null){
            return false;
        }
        else{
            return true;
        }
    }
}

