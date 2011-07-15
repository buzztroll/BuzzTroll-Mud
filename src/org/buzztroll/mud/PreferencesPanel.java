package org.buzztroll.mud;

import java.lang.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class
PreferencesPanel
    extends JPanel
    implements ActionListener
{
    protected JTextField urlViewerCmdTF;
    protected JButton backgroundButton;
    
    public
    PreferencesPanel()
    {
        super();

        JPanel                                  tempP = new JPanel();
        JPanel                                  tempP2 = new JPanel();

        tempP.setLayout(new GridLayout(6, 1));
        tempP2.setLayout(new GridLayout(6, 1));

        urlViewerCmdTF = new JTextField("");

        backgroundButton = new JButton("background");
        backgroundButton.addActionListener(this);

        tempP.add(new JLabel("URL Viewer Cmd Line:"));
        tempP.add(new JLabel("Background Color:"));

        tempP2.add(urlViewerCmdTF);
        tempP2.add(backgroundButton);

        this.setLayout(new BorderLayout(5, 5));
        this.add("West", tempP);
        this.add("Center", tempP2);
    }

    public Node
    createDoc(
        Document                                doc)
            throws Exception
    {
        int                                     ctr;

        Element preferencesE = doc.createElement("preferences");

        Element backgroundE = doc.createElement("background");
        backgroundE.setAttribute("red",
				 new Integer(getBackgroundColor().getRed()).toString());
        backgroundE.setAttribute("green",
				 new Integer(getBackgroundColor().getGreen()).toString());
        backgroundE.setAttribute("blue",
				 new Integer(getBackgroundColor().getBlue()).toString());
        preferencesE.appendChild(backgroundE);

        Element urlviewerE = doc.createElement("urlviewer");

        urlviewerE.setAttribute("cmd", this.urlViewerCmdTF.getText());

        preferencesE.appendChild(urlviewerE);
  
        return preferencesE;
    }

    public void
    parse(
        Document                                doc)
    {
        int                                     ctr;
        Node                                    n;
        Node                                    nj;

        String urlViewerCmd = null;;
        String                                  redS = "";
        String                                  greenS = "";
        String                                  blueS = "";

        this.backgroundButton.setBackground(Color.black);

        Element rootE = doc.getDocumentElement();

        n = rootE.getFirstChild();
        while(n != null)
        {   
            if(Node.ELEMENT_NODE == n.getNodeType())
            {
                Element e = (Element)n;

                if(e.getTagName().equals("preferences"))
                {
                    nj = n.getFirstChild();
                    while(nj != null)
                    {
                        if(Node.ELEMENT_NODE == nj.getNodeType())
                        {
                            Element ej = (Element)nj;

                            if(ej.getTagName().equalsIgnoreCase("urlviewer"))
                            {
                                urlViewerCmd = ej.getAttribute("cmd");
                            }
                            else if(ej.getTagName().equals("background"))
                            {
                                redS = ej.getAttribute("red");
                                greenS = ej.getAttribute("green");
                                blueS = ej.getAttribute("blue");

                                Color c = new Color(
						            new Integer(redS).intValue(),
						            new Integer(greenS).intValue(),
						            new Integer(blueS).intValue());
                                this.backgroundButton.setBackground(c);
                            }
                        }
                        nj = nj.getNextSibling();
                    }
                } 
            }
            n = n.getNextSibling();
        }

        this.urlViewerCmdTF.setText(urlViewerCmd);
    }

    public String
    getUrlViewerCmd() 
    {
        return this.urlViewerCmdTF.getText();
    }

    public Color 
    getBackgroundColor() 
    {
	return this.backgroundButton.getBackground();
    }

    public void
    reset()
    {
        urlViewerCmdTF.setText("");
    }

    public void
	actionPerformed(
        ActionEvent                         e)
    {
        if (e.getSource() == backgroundButton)
        {
            Color c = JColorChooser.showDialog(this, "Pick Color",
					       backgroundButton.getBackground());
            backgroundButton.setBackground(c);
        }
    } 
}
