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
HostSetupPanel
    extends JPanel
{
    public  JTextField                          hostnameText;
    public  JTextField                          portText;
    public  JTextField                          contactText;
    public  JTextField                          usernameText;
    public  JPasswordField                      passwordText;
    protected String                            passwordString;

    public
    HostSetupPanel()
    {
        super();

        JPanel                                  tempP = new JPanel();
        JPanel                                  tempP2 = new JPanel();

        tempP.setLayout(new GridLayout(6, 1));
        tempP2.setLayout(new GridLayout(6, 1));

        hostnameText = new JTextField("");
        portText = new JTextField("");
        contactText = new JTextField("");
        usernameText = new JTextField("");
        passwordText = new JPasswordField();

        tempP.add(new JLabel("Hostname:"));
        tempP.add(new JLabel("Port:"));
        tempP.add(new JLabel("Name:"));
        tempP.add(new JLabel("User Name:"));
        tempP.add(new JLabel("Password:"));

        tempP2.add(hostnameText);
        tempP2.add(portText);
        tempP2.add(contactText);
        tempP2.add(usernameText);
        tempP2.add(passwordText);

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
    
        Element serverE = doc.createElement("server");
        serverE.setAttribute("name", this.getContactName());
        Element connectE = doc.createElement("connect");
        connectE.setAttribute("hostname", this.getHostname());
        connectE.setAttribute("port", this.portText.getText());
        Element authE = doc.createElement("auth");
        authE.setAttribute("username", this.getUsername());
        authE.setAttribute("password", this.getPassword());

        serverE.appendChild(connectE);
        serverE.appendChild(authE);
  
        return serverE;
    }

    public void
    parse(
        Document                                doc)
    {
        int                                     ctr;
        String                                  connectionName = "";
        Node                                    n;
        Node                                    nj;
        String                                  portS = "";
        String                                  hostname = "";
        String                                  username = "";
        String                                  password = "";

        Element rootE = doc.getDocumentElement();

        n = rootE.getFirstChild();
        while(n != null)
        {   
            if(Node.ELEMENT_NODE == n.getNodeType())
            {
                Element e = (Element)n;

                /* get server name host port and auth */
                if(e.getTagName().equals("server"))
                {
                    connectionName = e.getAttribute("name");
                    nj = n.getFirstChild();
                    while(nj != null)
                    {
                        if(Node.ELEMENT_NODE == nj.getNodeType())
                        {
                            Element ej = (Element)nj;

                            if(ej.getTagName().equals("connect"))
                            {
                                hostname = ej.getAttribute("hostname");
                                portS = ej.getAttribute("port");
                            }
                            else if(ej.getTagName().equals("auth"))
                            {
                                username = ej.getAttribute("username");
                                password = ej.getAttribute("password");
                            }
                        }
                        nj = nj.getNextSibling();
                    }
                } 
            }
            n = n.getNextSibling();
        }

        this.hostnameText.setText(hostname);
        this.portText.setText(portS);
        this.contactText.setText(connectionName);
        this.usernameText.setText(username);
        this.passwordText.setText(password);
    }

    public String
    getHostname()
    {
        return this.hostnameText.getText();
    }

    public int
    getPort()
    {
        return new Integer(this.portText.getText()).intValue();
    }

    public String
    getContactName()
    {
        return this.contactText.getText();
    }

    public String
    getUsername()
    {
        return this.usernameText.getText();
    }

    public String
    getPassword()
    {
        return new String(passwordText.getPassword());
    }

    public void
    reset()
    {
        contactText.setText("");
        portText.setText("");
        hostnameText.setText("");
    }
}
