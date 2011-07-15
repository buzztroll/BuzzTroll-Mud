package org.buzztroll.mud;

import java.lang.*;
import java.awt.*;
import java.util.*;
import java.net.*;
import java.io.*;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import javax.swing.plaf.metal.MetalLookAndFeel;

public class
MudFrame
    extends JFrame
    implements WindowListener,
               ActionListener,
               FilenameFilter,
               ItemListener
{
    protected JTabbedPane                   tabs;
    protected NotifyWindow                  notifyWindow;
    protected JMenuBar                      menuBar;
    protected JMenuItem                     addMenuItem;
    protected JMenuItem                     exitMenuItem;
    protected ButtonGroup                   lafMenuGroup;
    protected int                           connections = 0;
    protected UIManager.LookAndFeelInfo     lafInfo[];
    protected JRadioButtonMenuItem          labButton[];
    protected JSplitPane                    splitP = null;
    protected Vector                        connectionVector;

    public static void 
    main(
        String                              args[])
    {
        String                              fname;

        fname = System.getProperty("user.home") + 
                System.getProperty("file.separator") + ".btmudrc" +
                System.getProperty("file.separator");

        try
        {
            File f = new File(fname);
            if(!f.exists())
            {
                f.mkdir();
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
            return;
        }

        MudFrame mf = new MudFrame(fname);
    }

    public 
    MudFrame(
        String                              confDir)
    {
        super("BuzzTroll Mud");

        String                              fileList[];
        int                                 ctr;

        File fDir = new File(confDir);
        if(!fDir.isDirectory())
        {
            System.err.println("ERROR: " +confDir + " is not a directory.");
        }
        fileList = fDir.list(this);

        for(ctr = 0; ctr < fileList.length; ctr++)
        {
            fileList[ctr] = confDir.concat("/").concat(fileList[ctr]);
        }

        init(fileList);
    }

    public 
    MudFrame(
        String                              fileList[])
    {
        super("BuzzTroll Mud");

        init(fileList);
    }

    private void
    init(
        String                          fileList[])
    {
        int ctr;


        /* set up frame */
        this.addWindowListener(this);
        this.setSize(640, 500);
        this.setLocation(1, 15);
        this.setBackground(Color.black);
        this.setBackground(Color.black);

        connectionVector = new Vector(10, 10);

        tabs = new JTabbedPane();
        menuBar = new JMenuBar();
        JMenu jmu = new JMenu("File");
        addMenuItem = jmu.add("Add");
        exitMenuItem = jmu.add("Exit");
        menuBar.add(jmu);

        lafMenuGroup = new ButtonGroup();
        jmu = new JMenu("Look and Feel");
        lafInfo = UIManager.getInstalledLookAndFeels();
        LookAndFeel defaultLAF = UIManager.getLookAndFeel();
        String defaultName = (defaultLAF != null) ? defaultLAF.getName() : "";
        labButton = new JRadioButtonMenuItem[lafInfo.length];
        for(ctr = 0; ctr < lafInfo.length; ctr++)
        {
            labButton[ctr] = new JRadioButtonMenuItem(lafInfo[ctr].getName());
            labButton[ctr].addItemListener(this);
            if (lafInfo[ctr].getName().equals(defaultName)) {
                labButton[ctr].setSelected(true);
            }
            jmu.add(labButton[ctr]);
            lafMenuGroup.add(labButton[ctr]);

            // test to ssee if LaF is avalable
            try
            {
                Class lnfClass = Class.forName(lafInfo[ctr].getClassName());
                LookAndFeel newLAF = (LookAndFeel)(lnfClass.newInstance());
                labButton[ctr].setEnabled(newLAF.isSupportedLookAndFeel());
            }
            catch (Exception e)
            {   
                labButton[ctr].setEnabled(false);
            }
        }
        menuBar.add(jmu);

        addMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);
        this.setJMenuBar(menuBar);

        /* 
         * walk through all mud clients 
         * add new pane every other client
         */

        try
        {
            for(ctr = 0; ctr < fileList.length; ctr++)
            {
                try
                {
                    String name = new String(fileList[ctr]);
                    MudClient mc = new MudClient(this, name);

                    /* add new tab */
                    if(connections % 2 == 0)
                    {
                        splitP = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                        tabs.add(mc.toString(), splitP);
                    }

                    splitP.add(mc);
                    System.out.println(name); 
                    connectionVector.add(mc);
                    connections++;
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                    System.err.println(e);
                    System.out.println("couldn't use " + fileList[ctr]);
                }
            }

            this.getContentPane().setLayout(new BorderLayout());
            this.getContentPane().add("Center", tabs);

            this.setVisible(true);
            this.toFront();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e);
            System.exit(-2);
        }
   }

    public void
    actionPerformed(
        ActionEvent                         e)
    {
        if(e.getSource() == addMenuItem)
        {
            System.out.println("Trying to add new Client");
            try
            {
                MudClient mc = new MudClient(this);

                /* add new tab */
                if(connections % 2 == 0)
                {
                    splitP = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                    tabs.add(mc.toString(), splitP);
                }

                splitP.add(mc);
                connectionVector.add(mc);
                connections++;
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
                System.err.println(ex);
            }
        }
        else if(e.getSource() == exitMenuItem)
        {
            this.dispose();
            System.exit(0);
        }
    }


    public void 
    itemStateChanged(
        ItemEvent                                   e)
    {
        int                                         ctr;

        for(ctr = 0; ctr < labButton.length; ctr++)
        {
            if(e.getSource() == labButton[ctr])
            {
                try
                {
                    UIManager.setLookAndFeel(lafInfo[ctr].getClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    if (notifyWindow != null) {
                        SwingUtilities.updateComponentTreeUI(notifyWindow);
                    }

                    for(int ctr2 = 0; ctr2 < connectionVector.size(); ctr2++)
                    {
                        MudClient mc = (MudClient)
                                        connectionVector.elementAt(ctr2);
                        mc.updateLaF();
                    }
                }
                catch(Exception ex)
                {
                    System.out.println("couldn't load " + 
                        lafInfo[ctr].getName());
                    ex.printStackTrace();
                }
            }
        }
    }

    public void
    setNotification(
        String                              msg)
    {
	if (notifyWindow == null) {
	    notifyWindow = new NotifyWindow(this);
	}
        notifyWindow.addMsg(msg);
    }

    public void
    windowOpened(
        WindowEvent                        we)
    {
    }
    public void
    windowClosed(
        WindowEvent                        we)
    {
        System.exit(0);
    }
    public void
    windowClosing(
        WindowEvent                        we)
    {
        this.dispose();
    }
    public void
    windowDeactivated(
        WindowEvent                        we)
    {
    }
    public void
    windowActivated(
        WindowEvent                        we)
    {
    }
    public void
    windowIconified(
        WindowEvent                        we)
    {
    }
    public void
    windowDeiconified(
        WindowEvent                        we)
    {
    }

    public boolean
    accept(
        File                                f,
        String                              name)
    {
        return (name.endsWith(".xml"));
    }
}
