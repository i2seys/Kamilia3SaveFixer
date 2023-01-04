package ru.fixer.util;

import ru.fixer.frame.MainFrame;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

import static ru.fixer.frame.MainFrame.WINDOW_HEIGHT;
import static ru.fixer.frame.MainFrame.WINDOW_WIDTH;

public class MainFrameUtil {
    private static Logger log = Logger.getLogger(MainFrameUtil.class.getName());
    public enum Step{
        FIRST,
        SECOND,
        THIRD
    }
    public enum ComponentName{
        KAMILIA_DIRECTORY_CHOOSE_BTN,
        KAMILIA_DIRECTORY_CHOOSE_LABEL,
        FIX_BUTTON,
        FIX_LABEL,
        EXIT_BUTTON,
        TEMP_LABEL
    }
    public Border getStepBorder(Step step){
        String title;
        switch (step){
            case FIRST:
                title = "Step 1/3 - prepare directory.";
                break;
            case SECOND:
                title = "Step 2/3 - create empty saves.";
                break;
            case THIRD:
                title = "Step 3/3 - saves fixed.";
                break;
            default:
                log.severe("Wrong step border(can this error ever be thrown?...).");
                return null;
        }
        return BorderFactory.createTitledBorder(title);
    }
    public JScrollPane getTextPane(Step step){
        String[] initString = null;
        switch (step){
            case FIRST:
                initString = new String[]{
                        "To get started, go to the folder with Kamilia 3.\n",
                        "In it, go to the \"Data\" folder. If your working saves are there, then move them to another location\n",
                        "The \"Data\" folder must contain non-working saves with the progress you want to get.\n",
                        "After you make sure that there are \"wrong\" save files in the Data folder - select the Kamilia directory.\n",
                        "Files required for transfer: \"saveData\", \"DeathTime\"\n",
                        "If there is no \"saveData2\" file in the \"Data\" folder, then you will not receive information about the crystals.\n",
                        "If there is no \"Config\" file, the game will be on default settings."
                };
                break;
            case SECOND:
                initString = new String[]{
                        "Your incorrect saves have been moved to the \"Data\\backup(time)\" folder. ",
                        "Now you need to enter the game, click \"Start game\" and exit the game. ",
                        "After that click \"Fix\"."
                };
                break;
            case THIRD:
                initString = new String[]{
                        "Your files have been fixed. The old wrong save files are in the backup folder."
                };
                break;
            default:
                log.severe("Wrong step border(can this error ever be thrown?...).");
                return null;
        }

        JTextPane textPane = new JTextPane();
        textPane.setEditable(false);

        //add init text
        StyledDocument doc = textPane.getStyledDocument();
        try {
            for (int i=0; i < initString.length; i++) {
                doc.insertString(doc.getLength(), initString[i], null);
            }
        } catch (BadLocationException ble) {
            log.severe("BadLocationException while inserting text in Pane: " + ble.getMessage());
            return null;
        }

        //add scroll
        JScrollPane paneScrollPane = new JScrollPane(textPane);
        //set prefered size
        paneScrollPane.setPreferredSize(new Dimension(WINDOW_WIDTH * 19 / 20, WINDOW_HEIGHT * 9 / 25));
        paneScrollPane.setWheelScrollingEnabled(true);
        return paneScrollPane;
    }
    public GridBagConstraints getGridBagConstrains(ComponentName componentName){
        Insets inset = new Insets(13,15,13,15);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridheight = 1;
        c.gridwidth = 1;
        c.insets = inset;
        switch (componentName) {
            case KAMILIA_DIRECTORY_CHOOSE_BTN:
                c.gridx = 0;
                c.gridy = 0;
                c.anchor = GridBagConstraints.FIRST_LINE_START;
                break;
            case KAMILIA_DIRECTORY_CHOOSE_LABEL:
                c.gridx = 1;
                c.gridy = 0;
                c.anchor = GridBagConstraints.PAGE_START;
                break;
            case FIX_BUTTON:
                c.gridx = 0;
                c.gridy = 1;
                c.anchor = GridBagConstraints.LINE_START;
                break;
            case FIX_LABEL:
                c.gridx = 1;
                c.gridy = 1;
                c.anchor = GridBagConstraints.CENTER;
                break;
            case EXIT_BUTTON:
                c.gridx = 0;
                c.gridy = 2;
                c.anchor = GridBagConstraints.LAST_LINE_START;
                break;
            case TEMP_LABEL:
                c.gridx = 1;
                c.gridy = 2;
                c.anchor = GridBagConstraints.PAGE_END;
                c.insets = new Insets(15, 0, 30, 0);
                break;
            default:
                log.severe("Wrong component name(can this error ever be thrown?...).");
                return null;
        }
        return c;
    }
    public JMenuBar createJMenuBar(JFrame frame){
        Icon gitImage =  new ImageIcon(ClassLoader.getSystemResource("png/gitIcon.png"));
        Icon readmeImage = new ImageIcon(ClassLoader.getSystemResource("png/readmeIcon.png"));
        Icon exitImage = new ImageIcon(ClassLoader.getSystemResource("png/exitpng.png"));
        //create empty JMenuBar
        JMenuBar menuBar = new JMenuBar();

        //create menu ""
        JMenu aboutMenu = new JMenu("About");
        JMenuItem githubRef = new JMenuItem("GitHub", gitImage);
        githubRef.addActionListener(e -> {
            String url = "https://github.com/i2seys/Kamilia3SaveFixer";
            try {
                Desktop desktop = Desktop.getDesktop();
                desktop.browse(new URL(url).toURI());
            }
            catch (IOException | URISyntaxException ex){
                log.warning("Can't automatically open GitHub reference: " + ex);
                JOptionPane.showMessageDialog(frame,
                        "Can't automatically open GitHub reference. Type in browser: \"github.com/i2seys/Kamilia3SaveFixer\".",
                        "Open link error.",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        aboutMenu.add(githubRef);

        //create menu "help"
        JMenu helpMenu = new JMenu("Help");
        JMenuItem readme = new JMenuItem("Readme", readmeImage);
        readme.addActionListener(e -> {
            //open README
            File readmeFile = new File("README.txt");
            if(!readmeFile.exists()){
                log.info("Can't find README file.");
                JOptionPane.showMessageDialog(frame,
                        "Can't find README file.\nYou can find it on GitHub: \"github.com/i2seys/Kamilia3SaveFixer\"",
                        "File not exist.",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.open(readmeFile);
            } catch (IOException ex) {
                log.info("Can't find README file.");
                JOptionPane.showMessageDialog(frame,
                        "Can't open README file.\nYou can find it in fixer folder or GitHub: \"github.com/i2seys/Kamilia3SaveFixer\"",
                        "File not exist.",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        helpMenu.add(readme);
        JMenuItem exit = new JMenuItem("Exit", exitImage);
        exit.addActionListener(e -> System.exit(0));
        helpMenu.add(exit);


        menuBar.add(aboutMenu);
        menuBar.add(helpMenu);
        return menuBar;

    }
    public JPanel createTextPanel(){
        JPanel textPanel = new JPanel();
        textPanel.setPreferredSize(new Dimension(WINDOW_WIDTH * 9 / 10, WINDOW_HEIGHT * 9/ 20));
        textPanel.add(getTextPane(MainFrameUtil.Step.FIRST));
        textPanel.setBorder(getStepBorder(MainFrameUtil.Step.FIRST));
        return textPanel;
    }
}
