package ru.fixer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;

import static ru.fixer.MainFrame.WINDOW_HEIGHT;
import static ru.fixer.MainFrame.WINDOW_WIDTH;

public class MainFrameUtil {
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
                title = "Step 3/3 - fix saves.";
                break;
            default: throw new RuntimeException();
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
                        "When you do, click \"Fix\"."
                };
                break;
            case THIRD:
                initString = new String[]{
                        "Your files have been fixed. The old wrong save files are in the backup folder."
                };
                break;
            default:
                throw new RuntimeException();
        }

        JTextPane textPane = new JTextPane();

        //add init text
        StyledDocument doc = textPane.getStyledDocument();
        try {
            for (int i=0; i < initString.length; i++) {
                doc.insertString(doc.getLength(), initString[i], null);
            }
        } catch (BadLocationException ble) {
            ble.printStackTrace();
        }

        //add scroll
        JScrollPane paneScrollPane = new JScrollPane(textPane);

        //set prefered size
        paneScrollPane.setPreferredSize(new Dimension(WINDOW_WIDTH * 19 / 20, WINDOW_HEIGHT * 9 / 21));
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
                c.insets = new Insets(0, 0, 45, 0);
                break;
            default: throw new RuntimeException();
        }
        return c;
    }
}
