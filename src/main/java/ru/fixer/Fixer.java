package ru.fixer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Fixer {
    /*
     * Что должен сделать пользователь без проги:
     * 1)переместить свои текущие данные в бекап
     * 2)запустить игру заново
     * 3)дойти до первого сейва и сохраниться
     * 4)выйти из игры
     * 5)переместить из старых сейвов saveData, saveData2, deathTime первые 4 байта в новые файлы
     * 6)заменить новый конфиг на старый
     * 7)заменить новую картинку на старую
     *
     * Что должен сделать пользователь для моей проги:
     * 1) поместить файл jar в папку с игрой (именно в папку с k3)
     * 2) запустить файл через консоль  (или просто его запустить)
     * 3) откроется консоль. В консоли напишется стартовый текст "Напишите Yes, если хотите начать
     * (k3 должна быть выключена). Ваши файлы будут перенесены в папку backup и не будут изменяться и т.п"
     * 4) после ввода  yes Создастся папка backup, в которую перенесутся все сейв файлы и картинка
     * 5) далее прога скажет, что надо зайти в игру, дойти до первого сейва и сохраниться
     * 6) пользователь выходит из игры и пишет Continue, после чего сейвы машнятся и у пользователя новые сейв файлы
     * */
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";
    private static String BACKUP_PATH = null;
    private static String KAMILIA_PATH = null;
    private static String DATA_PATH = null;
    private static final String START_MESSAGE = String.format("""
            %sBefore starting the program, your save files %s(DeathTime, saveData, saveData2 (if it exists), Config, Snapshot)
            %swill be moved to the %sbackup %sfolder and will not be changed.
            ATTENTION: Kamilia must be turned off.
            Your progress will not be lost. Type %s'Yes' %sto continue and %s'Exit' %sto exit (without quotes).
            """, ANSI_PURPLE, ANSI_CYAN, ANSI_PURPLE, ANSI_CYAN, ANSI_PURPLE, ANSI_GREEN, ANSI_PURPLE, ANSI_RED, ANSI_PURPLE);

    public static void main(String[] args) {
        //также надо проверить папку на корректность
        //////////////////////////////////////////////

        System.out.print(START_MESSAGE);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                String readedLine = null;
                boolean typeYes = false;
                readedLine = reader.readLine().toLowerCase().strip();
                switch (readedLine) {
                    case "yes":
                        typeYes = true;
                        break;
                    case "exit":
                        return;
                    default:
                        System.out.printf("%sWrong keyword. Type 'Yes' or 'Exit' to continue (without quotes).%n", ANSI_YELLOW);
                }
                if(typeYes){
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("An error has been thrown.");
            e.printStackTrace();
        }

        System.out.println("Type Kamilia 3 directory path.");
        File kamiliaDirectory = null;
        // после ввода  yes Создастся папка backup, в которую перенесутся все сейв файлы и картинка
        try {
            KAMILIA_PATH = reader.readLine();
            kamiliaDirectory = new File(KAMILIA_PATH);
            KAMILIA_PATH = kamiliaDirectory.getAbsolutePath();

            BACKUP_PATH = KAMILIA_PATH + "/Data/backup";
            DATA_PATH = KAMILIA_PATH + "/Data";


            if(kamiliaDirectory.isDirectory()){
                File backupDirectory = new File(BACKUP_PATH);
                if(backupDirectory.mkdir()){
                    System.out.println("Backup directory is created");

                    //перекидываем файлы DeathTime, saveData, saveData2 (if it exists), Config, Snapshot в бэкап
                    moveKamiliaFilesInBackup();
                }
                else{
                    System.out.println("Backup directory cant be created");
                    return;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        // далее прога скажет, что надо зайти в игру, дойти до первого сейва и сохраниться
        System.out.println("Now turn on the game, go to the first save room, save and exit Kamilia. After that type 'Continue' to continue");
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
        fixOldFiles();

        try {
            reader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static void fixOldFiles(){
        //byte[] array = Files.readAllBytes(Paths.get(fileName));
        fixOneFile("saveData");
        fixOneFile( "saveData2");
        fixOneFile("Config");
        fixOneFile("DeathTime");
        fixOneFile( "Snapshot.bmp");
    }
    private static void fixOneFile(String fileName){
        //1)считать данные из старого файла по байтам (старый файл в бекапе (/Data/backup))
        //2)считать первые 4 байта из нового файла (новый файл в /Data)
        //3)в строке с данными из старого файла заменить первые 4 байта на новые
        //4)удалить новый файл и на его место поставить файл из байтов, полученных в пункте 3

        try {
            String newFilePath  = DATA_PATH + "/" + fileName;
            String oldFilePath = BACKUP_PATH + "/" + fileName;
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
    private static void moveKamiliaFilesInBackup(){
        //перекидываем файлы DeathTime, saveData, saveData2 (if it exists), Config, Snapshot в бэкап
        moveFileToBackup("DeathTime");
        moveFileToBackup("saveData");
        moveFileToBackup("saveData2");
        moveFileToBackup("Config");
        moveFileToBackup("Snapshot.bmp");
    }
    private static void moveFileToBackup(String fileName) {
        Path result = null;
        String from = DATA_PATH + "/" + fileName;
        String to = BACKUP_PATH + "/" + fileName;
        try {
            result =  Files.move(Paths.get(from), Paths.get(to));
        } catch (IOException e) {
            System.out.println("Exception while moving file: " + e.getMessage());
        }
        if(result != null) {
            System.out.println("File moved successfully.");
        }else{
            System.out.println("File movement failed.");
        }
    }
}

