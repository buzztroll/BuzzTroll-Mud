package org.buzztroll.mud;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.lang.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import org.apache.axis.utils.DOM2Writer;

public class
ConfigFrame
    extends JDialog
    implements ActionListener
{
    protected HostSetupPanel                    hostSetupPanel;
    protected ActionPanel                       actionPanel;
    public  NameSetupPanel                      namePanel;
    protected PreferencesPanel                  preferencesPanel;

    public  JButton                             saveButton;
    public  JButton                             cancelButton;

    public  JTextField                          hostnameText;
    public  JTextField                          portText;
    public  JTextField                          contactName;

    protected JTabbedPane                       tabs;

    protected String                            fileName = null;
    protected boolean                           result = false;

    protected Vector                            transformVector;

    public
    ConfigFrame(
        MudFrame                                owner)
            throws Exception
    {
        super(owner, "Setup new Connection", true);
        init(owner);
    }

    public
    ConfigFrame(
        MudFrame                                owner,
        String                                  fname)
            throws Exception
    {
        super(owner, "Setup " + fname, true);

        fileName = new String(fname);
        init(owner);

        Document doc = readFile(fname);
        hostSetupPanel.parse(doc);
        namePanel.parse(doc);
        actionPanel.parse(doc);
        preferencesPanel.parse(doc);
    }

    public void
    init(
        MudFrame owner)
    {
        JPanel                                  mainP = new JPanel();
        JPanel                                  buttonP = new JPanel();

        this.setSize(450, 300);

        transformVector = new Vector(10, 10);

        namePanel = new NameSetupPanel();
        hostSetupPanel = new HostSetupPanel();
        actionPanel = new ActionPanel(owner);
        preferencesPanel = new PreferencesPanel();

        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);

        buttonP.add(saveButton);
        buttonP.add(cancelButton);

        tabs = new JTabbedPane();
        tabs.add("Host", hostSetupPanel);

        addTransformPanel(namePanel);
        addTransformPanel(actionPanel);

        tabs.add("Preferences", preferencesPanel);

        mainP.setLayout(new BorderLayout());
        mainP.add("Center", tabs);
        mainP.add("South", buttonP);

        this.getContentPane().add(mainP);
    }

    public String
    getContactName()
    {
        return this.hostSetupPanel.getContactName();
    }

    public String
    getUsername()
    {
        return this.hostSetupPanel.getUsername();
    }

    public String
    getPassword()
    {
        return this.hostSetupPanel.getPassword();
    }

    public String
    getHostname()
    {
        return this.hostSetupPanel.getHostname();
    }

    public int
    getPort()
    {
        return this.hostSetupPanel.getPort();
    }

    public String
    getUrlViewerCmd() 
    {
        return this.preferencesPanel.getUrlViewerCmd();
    }

    public Color 
    getBackgroundColor() 
    {
        return this.preferencesPanel.getBackgroundColor();
    }

    public DisplayMessage
    transform(
        String                                  msg)
    {
        DisplayMessage                          dm;
        int                                     ctr;

        dm = new DisplayMessage(msg, Color.lightGray, null);
        for(ctr = 0; ctr < transformVector.size(); ctr++)
        {
            TransformPanel tp = (TransformPanel) transformVector.elementAt(ctr);
            dm = tp.transform(dm);
        }

        return dm;
    }

    public void
    writeFile(
        String                                  fname)
            throws Exception
    {
        DocumentBuilderFactory                  builderFactory;
        DocumentBuilder                         factory;
        FileWriter                              fw;

        builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(false);

        factory = builderFactory.newDocumentBuilder();

        Document doc = factory.newDocument();
        Element root = doc.createElement("mud");
        root.appendChild(namePanel.createDoc(doc));
        root.appendChild(hostSetupPanel.createDoc(doc));
        root.appendChild(actionPanel.createDoc(doc));
        root.appendChild(preferencesPanel.createDoc(doc));
        doc.appendChild(root);

        try
        {
            fw = new FileWriter(fname);

            DOM2Writer.serializeAsXML(doc, fw, true, true);
            fw.close();
        }
        catch(Exception ex)
        {
            System.err.println(ex);
            throw ex;
        }
    }

    public void
    pn(Node n)
    {
        if(n == null)
        {
            return;
        }

        pn(n.getFirstChild()); 
        pn(n.getNextSibling()); 
    }

    public Document
    readFile(
        String                                  fname)
            throws Exception
    {
        DocumentBuilderFactory builderFactory = 
            DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(false);

        DocumentBuilder factory = builderFactory.newDocumentBuilder();
        Document doc = factory.parse(fname);

        return doc;
    }
    
    protected void
    clear()
    {
        for(int ctr = 0; ctr < transformVector.size(); ctr++)
        {
            TransformPanel tp = (TransformPanel) transformVector.elementAt(ctr);
            tp.clear();
        }
    }

    public void
    actionPerformed(
        ActionEvent                         e)
    {
        try
        {
            if(fileName == null)
            {
                String fname = new String(
                    System.getProperty("user.home") +
                    System.getProperty("file.separator") + ".btmudrc" +
                    System.getProperty("file.separator") + getContactName() +
                    ".xml");

                fileName = fname;
            }

            if(e.getSource() == saveButton)
            {
                writeFile(fileName);
                clear();
                Document doc = readFile(fileName);
                hostSetupPanel.parse(doc);
                namePanel.parse(doc);
                actionPanel.parse(doc);
                preferencesPanel.parse(doc);
                result = true;
            }
            this.setVisible(false);
        }
        catch(Exception exp)
        {
            System.err.println(exp);
            exp.printStackTrace();            
        }
    }

    public boolean  
    getResult()
    {
        return this.result;
    }

    public void
    addTransformPanel(
        TransformPanel                          tp)
    {
        transformVector.add(tp);
        tabs.addTab(tp.getName(), tp);
    }
}
