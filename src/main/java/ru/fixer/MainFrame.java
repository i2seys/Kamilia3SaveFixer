package ru.fixer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;

public class MainFrame extends JFrame {
    public static final Integer WINDOW_HEIGHT = 440;
    public static final Integer WINDOW_WIDTH = 450;
    private JPanel textPanel;
    private JPanel componentsPanel;
    private JButton kamiliaDirectoryChooseBtn;
    private JLabel kamiliaDirectoryChooseLabel;
    private JButton fixButton;
    private JLabel fixLabel;
    private JButton exitButton;
    private final Icon cross = new ImageIcon("png/crosspng.png");
    private final Icon checkmark = new ImageIcon("png/checkmarkpng.png");
    private final Image iconImage = new ImageIcon("png/kamiliaKey.png").getImage();
    private final MainFrameUtil mainFrameUtil = new MainFrameUtil();
    private Fixer fixer;
    private final String baseDirectory = "C:\\";

    public MainFrame(){
        Toolkit tk = Toolkit.getDefaultToolkit();
        baseInitForMainFrame();

        //create text panel
        textPanel = new JPanel();
        textPanel.setPreferredSize(new Dimension(WINDOW_WIDTH * 9 / 10, WINDOW_HEIGHT / 2));
        textPanel.add(mainFrameUtil.getTextPane(MainFrameUtil.Step.FIRST));
        textPanel.setBorder(mainFrameUtil.getStepBorder(MainFrameUtil.Step.FIRST));
        add(textPanel);

        //create buttons panel
        componentsPanel = new JPanel();
        componentsPanel.setLayout(new GridBagLayout());

        //create button for directory choosing
        GridBagConstraints constr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.KAMILIA_DIRECTORY_CHOOSE_BTN);
        kamiliaDirectoryChooseBtn = new JButton("Choose Kamilia 3 directory");
        kamiliaDirectoryChooseBtn.addActionListener(e -> {
            JFileChooser kamiliaDirectoryChooser = new JFileChooser();
            kamiliaDirectoryChooser.setCurrentDirectory(new File(baseDirectory));
            kamiliaDirectoryChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int option = kamiliaDirectoryChooser.showOpenDialog(MainFrame.this);
            if(option == JFileChooser.APPROVE_OPTION){
                File kamiliaDir = kamiliaDirectoryChooser.getSelectedFile();
                if(!Fixer.checkForCorrectKamiliaDirectory(kamiliaDir)){
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Choose correct Kamilia directory.",
                            "Wrong directory",
                            JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //init fixer class
                String kamiliaPath = kamiliaDir.getAbsolutePath();
                fixer = new Fixer(kamiliaPath);

                //create directory
                if(!fixer.createBackupDirectory()){
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Can't create 'backup' directory.",
                            "Directory error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                //move wrong files
                if(!fixer.moveKamiliaFilesInBackup(this)){
                    return;
                }

                //show new button
                kamiliaDirectoryChooseLabel.setIcon(checkmark);
                fixButton.setVisible(true);
                fixLabel.setVisible(true);

                //disable directory choose button
                ActionListener chooseDirectoryListener = kamiliaDirectoryChooseBtn.getActionListeners()[0];
                kamiliaDirectoryChooseBtn.removeActionListener(chooseDirectoryListener);

                //add whitespace for fix button
                Label tempLabel = new Label();
                GridBagConstraints tempConstr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.TEMP_LABEL);
                componentsPanel.add(tempLabel, tempConstr);

                //set new text
                textPanel.remove(0);
                textPanel.add(mainFrameUtil.getTextPane(MainFrameUtil.Step.SECOND));
                textPanel.setBorder(mainFrameUtil.getStepBorder(MainFrameUtil.Step.SECOND));

            }
        });
        componentsPanel.add(kamiliaDirectoryChooseBtn, constr);

        //add icon for kamiliaDirectoryChooseBtn
        constr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.KAMILIA_DIRECTORY_CHOOSE_LABEL);
        kamiliaDirectoryChooseLabel = new JLabel(cross);
        componentsPanel.add(kamiliaDirectoryChooseLabel, constr);

        //create invisible fix button
        constr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.FIX_BUTTON);
        fixButton = new JButton("Fix");
        fixButton.addActionListener(e -> {
            if(!fixer.newFilesExist(MainFrame.this)){
                return;
            }
            if(!fixer.fixAndMoveOldFiles(MainFrame.this)){
                return;
            }

            fixLabel.setIcon(checkmark);
            exitButton.setVisible(true);
            textPanel.remove(0);
            textPanel.add(mainFrameUtil.getTextPane(MainFrameUtil.Step.THIRD));
            textPanel.setBorder(mainFrameUtil.getStepBorder(MainFrameUtil.Step.THIRD));
        });
        fixButton.setVisible(false);
        componentsPanel.add(fixButton, constr);

        //create invisible fix label
        constr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.FIX_LABEL);
        fixLabel = new JLabel(cross);
        fixLabel.setVisible(false);
        componentsPanel.add(fixLabel, constr);

        //create invisible exit button
        constr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.EXIT_BUTTON);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setVisible(false);
        componentsPanel.add(exitButton, constr);

        componentsPanel.setPreferredSize(new Dimension(WINDOW_WIDTH * 9 / 10, WINDOW_HEIGHT * 2 / 5));
        add(componentsPanel);

        setMainFrameBounds(tk);
    }
    private void baseInitForMainFrame(){
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }

        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("I wanna kill the Kamilia 3 Save Fixer");
        setIconImage(iconImage);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    }

    private void setMainFrameBounds(Toolkit tk){
        Point startLocation = new Point(tk.getScreenSize().width / 2 - WINDOW_WIDTH / 2 ,
                tk.getScreenSize().height / 2 - WINDOW_HEIGHT / 2);
        setLocation(startLocation);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setResizable(false);
    }


}

