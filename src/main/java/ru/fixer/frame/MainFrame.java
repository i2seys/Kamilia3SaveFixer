package ru.fixer.frame;

import ru.fixer.service.Fixer;
import ru.fixer.util.MainFrameUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class MainFrame extends JFrame {
    private static Logger log = Logger.getLogger(MainFrame.class.getName());
    public static final Integer WINDOW_HEIGHT = 440;
    public static final Integer WINDOW_WIDTH = 450;
    private JPanel textPanel;
    private JPanel componentsPanel;
    private JButton kamiliaDirectoryChooseBtn;
    private JLabel kamiliaDirectoryChooseLabel;
    private JButton fixButton;
    private JLabel fixLabel;
    private JButton exitButton;
    private final Icon crossImage = new ImageIcon(ClassLoader.getSystemResource("png/crosspng.png"));
    private final Icon checkmarkImage = new ImageIcon(ClassLoader.getSystemResource("png/checkmarkpng.png"));
    private final Image iconImage = new ImageIcon(ClassLoader.getSystemResource("png/kamiliaKey.png")).getImage();
    private final MainFrameUtil mainFrameUtil = new MainFrameUtil();
    private Fixer fixer;
    private final String baseDirectory = "C:\\";

    public MainFrame(){
        Toolkit tk = Toolkit.getDefaultToolkit();
        baseInitForMainFrame();

        //add menus
        JMenuBar menuBar = mainFrameUtil.createJMenuBar(this);
        setJMenuBar(menuBar);

        //create text panel
        textPanel = new JPanel();
        textPanel.setPreferredSize(new Dimension(WINDOW_WIDTH * 9 / 10, WINDOW_HEIGHT * 9/ 20));
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
                            JOptionPane.WARNING_MESSAGE);
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
                    log.severe("Can't create 'backup' directory.");
                    return;
                }

                //move wrong files
                if(!fixer.moveKamiliaFilesInBackup(this)){
                    log.severe("Can't move files in backup.");
                    return;
                }

                //show new button and checkmark
                kamiliaDirectoryChooseLabel.setIcon(checkmarkImage);
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
        kamiliaDirectoryChooseLabel = new JLabel(crossImage);
        componentsPanel.add(kamiliaDirectoryChooseLabel, constr);

        //create invisible fix button
        constr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.FIX_BUTTON);
        fixButton = new JButton("Fix");
        fixButton.addActionListener(e -> {
            if(!fixer.newFilesExist(MainFrame.this)){
                log.severe("New files don't exist.");
                return;
            }
            if(!fixer.fixAndMoveOldFiles(MainFrame.this)){
                log.severe("Can't fix or move old files.");
                return;
            }

            fixLabel.setIcon(checkmarkImage);
            exitButton.setVisible(true);
            textPanel.remove(0);
            textPanel.add(mainFrameUtil.getTextPane(MainFrameUtil.Step.THIRD));
            textPanel.setBorder(mainFrameUtil.getStepBorder(MainFrameUtil.Step.THIRD));
        });
        fixButton.setVisible(false);
        componentsPanel.add(fixButton, constr);

        //create invisible fix label
        constr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.FIX_LABEL);
        fixLabel = new JLabel(crossImage);
        fixLabel.setVisible(false);
        componentsPanel.add(fixLabel, constr);

        //create invisible exit button
        constr = mainFrameUtil.getGridBagConstrains(MainFrameUtil.ComponentName.EXIT_BUTTON);
        exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        exitButton.setVisible(false);
        componentsPanel.add(exitButton, constr);

        componentsPanel.setPreferredSize(new Dimension(WINDOW_WIDTH * 9 / 10, WINDOW_HEIGHT * 35 / 100));
        add(componentsPanel);



        setMainFrameBounds(tk);
    }
    private void baseInitForMainFrame(){
        try {
            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (ClassNotFoundException | InstantiationException |
                 IllegalAccessException |
                 UnsupportedLookAndFeelException e) {
            log.severe("LookAndFeel error: " + e.getMessage());
        }

        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

