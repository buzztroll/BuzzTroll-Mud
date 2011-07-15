package org.buzztroll.mud;

import java.lang.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.colorchooser.*;
import javax.swing.text.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class
ColorName
    extends JPanel
{
    public Color                                color;
    public String                               name;

    public 
    ColorName(
        String                                  name,
        Color                                   color)
    {
        super();

        this.name = new String(name);
        this.color = color;

        this.setBackground(new Color(0, 0, 0));
        this.setForeground(color);
    }

    public String
    toString()
    {
        return name;
    }
}

public class
NameSetupPanel
    extends TransformPanel
    implements ChangeListener,
                ActionListener
{
    protected JTextField                        newNameText;
    protected JList                             nameList;
    protected ColorPanel                        colorPanel;
    protected JButton                           addButton;
    protected JButton                           removeButton;
    protected DefaultListModel                  listModel;
    protected Vector                            nameVector;
    protected Hashtable                         nameLookup;
    protected Color                             toMeColor = Color.green;
    protected JButton                           toMeButton;
    protected JButton                           defaultButton;

    public
    NameSetupPanel()
    {
        super();

        JPanel                                  tempP;
        JPanel                                  tempP2;
        JPanel                                  tempP3;
        JPanel                                  buttonPanel;

        defaultButton = new JButton("default");
        defaultButton.addActionListener(this);
        defaultButton.setBackground(Color.lightGray);

        toMeButton = new JButton("To Me");
        toMeButton.addActionListener(this);
        toMeButton.setBackground(toMeColor);

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        newNameText = new JTextField("");
        colorPanel = new ColorPanel();

        nameLookup = new Hashtable();
        nameVector = new Vector();
        nameList = new JList(nameVector);

        // Or in two steps:
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getViewport().setView(nameList);
        MyCellRenderer cr = new MyCellRenderer();
        nameList.setCellRenderer(cr);

        newNameText.setBackground(new Color(0, 0, 0));
        newNameText.setForeground(new Color(255, 255, 255));

        nameList.setBackground(new Color(0, 0, 0));
        nameList.setForeground(new Color(255, 255, 255));

        tempP = new JPanel();
        tempP.setLayout(new BorderLayout());
        tempP.add("North", newNameText);
        tempP.add("Center", scrollPane);

        colorPanel = new ColorPanel();
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 5, 5));
        buttonPanel.add(new JPanel());
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        tempP2 = new JPanel();
        tempP2.setLayout(new BorderLayout(5, 5));
        tempP2.add("Center", tempP);
        tempP2.add("East", buttonPanel);

        tempP3 = new JPanel();
        tempP3.setLayout(new BorderLayout());
        tempP3.add("Center", colorPanel);
    
        JPanel colorButtonPanel = new JPanel();
        colorButtonPanel.setLayout(new GridLayout(2, 1));
        colorButtonPanel.add(toMeButton);
        colorButtonPanel.add(defaultButton);
        
        tempP3.add("South", colorButtonPanel);
        tempP2.add("West", tempP3);

        this.setLayout(new BorderLayout());
        this.add("Center", tempP2);

        addButton.addActionListener(this);
        removeButton.addActionListener(this);
        colorPanel.addChangeListener(this);

        newNameText.setForeground(colorPanel.getColor());
    }

    public String
    getName()
    {
        return "Names";
    }

    public Color getColor(String name) {
	ColorName cn = (ColorName) nameLookup.get(name);
	return (cn == null) ? Color.green : cn.color;
    }
    
    public Color getSelfColor() {
	return this.toMeColor;
    }

    // This mthid both creates the xml and adds values to the hastable
    public Node
    createDoc(
        Document                                doc)
        throws Exception
    {
        int                                     ctr;

        Element namesE = doc.createElement("names");

        nameLookup.clear();

        for(ctr = 0; ctr < nameVector.size(); ctr++)
        {
            ColorName cn = (ColorName)nameVector.elementAt(ctr);
            Element characterE = doc.createElement("character");

            // add to has table
            nameLookup.put(newNameText.getText(), cn);
            namesE.appendChild(characterE);
            characterE.setAttribute("name", cn.name);
            characterE.setAttribute("red", 
                new Integer(cn.color.getRed()).toString());
            characterE.setAttribute("green", 
                new Integer(cn.color.getGreen()).toString());
            characterE.setAttribute("blue", 
                new Integer(cn.color.getBlue()).toString());
        }

        Element toMeE = doc.createElement("toMe");
        toMeE.setAttribute("red",
            new Integer(toMeColor.getRed()).toString());
        toMeE.setAttribute("green",
            new Integer(toMeColor.getGreen()).toString());
        toMeE.setAttribute("blue",
                new Integer(toMeColor.getBlue()).toString());
        toMeButton.setBackground(toMeColor);
        namesE.appendChild(toMeE);

        Element dE = doc.createElement("default");
        Color dC = defaultButton.getBackground();
        dE.setAttribute("red",
            new Integer(dC.getRed()).toString());
        dE.setAttribute("green",
            new Integer(dC.getGreen()).toString());
        dE.setAttribute("blue",
                new Integer(dC.getBlue()).toString());
        namesE.appendChild(dE);

        return namesE;
    }

    public void
    parse(
        Document                                doc)
    {
        int                                     ctr;
        String                                  connectionName = "";
        Node                                    n;
        Node                                    nj;
        String                                  name = "";
        String                                  redS = "";
        String                                  greenS = "";
        String                                  blueS = "";

        Element rootE = doc.getDocumentElement();

        n = rootE.getFirstChild();
        while(n != null)
        {
            if(Node.ELEMENT_NODE == n.getNodeType())
            {
                Element e = (Element)n;

                /* get server name host port and auth */
                if(e.getTagName().equals("names"))
                {
                    nj = n.getFirstChild();
                    while(nj != null)
                    {
                        if(Node.ELEMENT_NODE == nj.getNodeType())
                        {
                            Element ej = (Element)nj;

                            if(ej.getTagName().equals("character"))
                            {
                                name = ej.getAttribute("name");
                                redS = ej.getAttribute("red");
                                greenS = ej.getAttribute("green");
                                blueS = ej.getAttribute("blue");

                                Color c = new Color(
                                                new Integer(redS).intValue(),
                                                new Integer(greenS).intValue(),
                                                new Integer(blueS).intValue());

                                ColorName cn = new ColorName(
                                                     name,
                                                     c);

                                nameLookup.put(name, cn);
                                nameVector.add(cn);
                            }
                            else if(ej.getTagName().equals("toMe"))
                            {
                                redS = ej.getAttribute("red");
                                greenS = ej.getAttribute("green");
                                blueS = ej.getAttribute("blue");

                                this.toMeColor = new Color(
                                            new Integer(redS).intValue(),
                                            new Integer(greenS).intValue(),
                                            new Integer(blueS).intValue());
                                this.toMeButton.setBackground(this.toMeColor);
                            }
                            else if(ej.getTagName().equals("default"))
                            {
                                redS = ej.getAttribute("red");
                                greenS = ej.getAttribute("green");
                                blueS = ej.getAttribute("blue");

                                Color c = new Color(
                                            new Integer(redS).intValue(),
                                            new Integer(greenS).intValue(),
                                            new Integer(blueS).intValue());
                                this.defaultButton.setBackground(c);
                            }
                        }
                        nj = nj.getNextSibling();
                    }
                }
            }
            n = n.getNextSibling();
        }
    }

    public DisplayMessage
    transform(
        DisplayMessage                          dmIn)
    {
        String                                  name;
        String                                  msg;
        DisplayMessage                          dm = dmIn;

        msg = dmIn.getString();
        try
        {
            name = parseName(msg);

	    if (name == null) {
		return new DisplayMessage(msg, 
					  defaultButton.getBackground(),
					  dm.getFont());
	    }

            ColorName cn = (ColorName) nameLookup.get(name);
            if(cn != null)
	    {
		dm = new DisplayMessage(msg, cn.color, dm.getFont());
	    }
            else if(msg.startsWith("(from ") ||
		    // this can be still optimized
                    msg.indexOf("to you]:") == name.length() + 2) 
            {
                dm = new DisplayMessage(msg, this.toMeColor, dm.getFont());
            }
            else
            {
                dm = new DisplayMessage(msg,
					defaultButton.getBackground(),
					dm.getFont());
            }
        }
        catch(Exception e)
        {
	    System.err.println(msg);
	    e.printStackTrace();
            dm = dmIn;
        } 

        return dm;
    }

    public static String
    parseName(
        String                              msg)
    {
        String                              name;

        if(msg.startsWith("(to")) 
        {
            return "You";
        }
        else if(msg.startsWith("(from "))
        {
            name = msg.substring(msg.indexOf(")") + 1).trim();
            name = name.substring(0, name.indexOf(" "));
            return name.trim();
        }
        else
        {
	    int pos = msg.indexOf(" ");
	    if (pos == -1) {
		return null;
	    } else {
		return msg.substring(0, pos).trim();
	    }
        }
    }

    public void 
    clear()
    {
        nameVector.clear();
        nameLookup.clear();
    }

    public void
    stateChanged(
        ChangeEvent                         e)
    {
        newNameText.setForeground(colorPanel.getColor());
    }

    public void
    actionPerformed(
        ActionEvent                         e)
    {
        if(e.getSource() == addButton)
        {
            if(newNameText.getText().equals(""))
            {
            }

            ColorName cn =  new ColorName(newNameText.getText(), 
                                    colorPanel.getColor());

            nameVector.add(cn);
            nameLookup.put(cn.toString(), cn);
            nameList.updateUI();
            System.out.println(newNameText.getText() + " added.");
        }
        else if(e.getSource() == removeButton)
        {
            int ndx = nameList.getSelectedIndex();
            ColorName cn = (ColorName) nameVector.elementAt(ndx);

            nameLookup.remove(cn.toString());
            nameVector.removeElementAt(ndx);
            nameList.updateUI();
        }
        else if(e.getSource() == toMeButton)
        {
            toMeColor = JColorChooser.showDialog(this, "Pick Color",
                             toMeButton.getBackground());
            toMeButton.setBackground(toMeColor);
        }
        else if(e.getSource() == defaultButton)
        {
            Color c = JColorChooser.showDialog(this, "Pick Color",
                             defaultButton.getBackground());
            defaultButton.setBackground(c);
        }
    } 

    protected class 
    MyCellRenderer 
        extends JLabel 
        implements ListCellRenderer 
    {
        public Component 
        getListCellRendererComponent(
            JList list,
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)    // the list and the cell have the focus
        {
            ColorName cn = (ColorName)value;
            setText(cn.toString());
        

            if(isSelected) 
            {
                setBackground(list.getSelectionBackground());
            }
            else 
            {
                setBackground(list.getBackground());
            }
        
            setForeground(cn.color);

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }


}
