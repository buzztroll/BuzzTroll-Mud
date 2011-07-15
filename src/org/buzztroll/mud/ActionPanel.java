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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ozten.font.JFontChooser;

public class
ActionPanel
    extends TransformPanel
    implements ItemListener,
               ActionListener,
               ChangeListener
{
    protected JTextField            parseText;
    protected JComboBox             actionCombo;
    protected JComboBox             positionCombo;
    protected JList                 actionList;
    protected CardLayout            cardLayout;
    protected JTextField            runText;
    protected JTextField            urlText;
    protected JTextField            imageText;
    protected JColorChooser         colorPicker;
    protected JPanel                colorPanel;
    protected JButton               colorButton;
    protected JButton               fontButton;
    protected JSlider               intensitySlider;
    protected JButton               addButton;
    protected JButton               removeButton;
    protected MudFrame              owner;
    protected Vector                actionVector = new Vector(10, 10);

    protected JPanel                cardPanel;

    private static final String     RUN_CARD = "RUN_CARD";    
    private static final String     COLOR_CARD = "COLOR_CARD";    
    private static final String     NOTIFY_CARD = "NOTIFY_CARD";    
    private static final String     INTENSITY_CARD = "INTENSITY_CARD";    
    private static final String     FONT_CARD = "FONT_CARD";    
    private static final String     IGNORE_CARD = "IGNORE_CARD";    
    private static final String     PLAY_CARD = "PLAY_CARD";   
    private static final String     SPEAK_CARD = "SPEAK_CARD"; 
    private static final String     IMAGE_CARD = "IMAGE_CARD"; 

    public
    ActionPanel(
        MudFrame                    owner)
    {
        super();

        JPanel                      topPanel;
        JPanel                      buttonPanel;
        JPanel                      tempP;

        this.owner = owner;

        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        addButton.addActionListener(this);
        removeButton.addActionListener(this);

        JScrollPane scrollPane = new JScrollPane();

        parseText = new JTextField("");
        actionList = new JList(actionVector);
        scrollPane.getViewport().setView(actionList);
        MyCellRenderer cr = new MyCellRenderer();
        actionList.setCellRenderer(cr);

        positionCombo = new JComboBox();
        positionCombo.addItem("Starts with");
        positionCombo.addItem("Contains");
        positionCombo.addItem("Ends with");

        actionCombo = new JComboBox();
        actionCombo.addItem("Run");
        actionCombo.addItem("Color");
        actionCombo.addItem("Notify");
        actionCombo.addItem("Intensity");
        actionCombo.addItem("Font");
        actionCombo.addItem("Ignore");
	actionCombo.addItem("Play");
	actionCombo.addItem("Speak");
        actionCombo.addItem("Image");

    
        JPanel intensityPanel = new JPanel();
        intensityPanel.setLayout(new BorderLayout());
        intensitySlider = new JSlider(-100, 100, 0);
        intensityPanel.add("Center", intensitySlider);
        intensitySlider.addChangeListener(this);

        actionCombo.addItemListener(this);

        topPanel = new JPanel();

        colorPanel = new JPanel();

        runText = new JTextField("");
	urlText = new JTextField();
        imageText = new JTextField();

        colorPicker = new JColorChooser();
        
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);

        fontButton = new JButton("Font");
        fontButton.addActionListener(this);

        colorButton = new JButton("Color");
        colorButton.addActionListener(this);

        cardPanel.add(runText, RUN_CARD);
        cardPanel.add(colorButton, COLOR_CARD);
        cardPanel.add(new JPanel(), NOTIFY_CARD);
        cardPanel.add(intensityPanel, INTENSITY_CARD);
        cardPanel.add(fontButton, FONT_CARD);
        cardPanel.add(new JPanel(), IGNORE_CARD);
	cardPanel.add(urlText, PLAY_CARD);
	cardPanel.add(new JPanel(), SPEAK_CARD);
        cardPanel.add(imageText, IMAGE_CARD);

        cardLayout.show(cardPanel, RUN_CARD);

        topPanel.setLayout(new GridLayout(1, 4));
        topPanel.add(positionCombo); 
        topPanel.add(parseText);
        topPanel.add(actionCombo);
        topPanel.add(cardPanel);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1));
        buttonPanel.add(new JPanel());
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
    
        tempP = new JPanel();
        tempP.setLayout(new BorderLayout());
        tempP.add("North", topPanel);
        tempP.add("Center", scrollPane);
       
        this.setLayout(new BorderLayout());
        this.add("Center", tempP);
        this.add("East", buttonPanel);
    }

    public String
    getName()
    {
        return "Action";
    }

    public void
    clear()
    {
        actionVector.clear();
    }

    public DisplayMessage
    transform(
        DisplayMessage                          msg)
    {
        int                                     ctr;
    
        for(ctr = 0; ctr < actionVector.size(); ctr++)
        {
            MessageTransformer mt = (MessageTransformer)
                                        actionVector.elementAt(ctr);
            msg = mt.transform(msg);
        }

        return msg;
    }

    public void
    parse(
        Document                                doc)
    {
        Node                                    n;
        Node                                    nj;
        Element rootE = doc.getDocumentElement();

        n = rootE.getFirstChild();
        while(n != null)
        {
            if(Node.ELEMENT_NODE == n.getNodeType())
            {
                Element e = (Element)n;

                if(e.getTagName().equals("action"))
                {
                    nj = n.getFirstChild();
                    while(nj != null)
                    {
                        if(Node.ELEMENT_NODE == nj.getNodeType())
                        {
                            Element ej = (Element)nj;

                              /* get server name host port and auth */
                            if(ej.getTagName().equals("notify"))
                            {
                                NotifyMessageTransformer nmt = 
                                    new NotifyMessageTransformer(owner);
                                nmt.parse(ej);
                                actionVector.add(nmt);
                            }   
                            else if(ej.getTagName().equals("color"))
                            {
                                ColorMessageTransformer cmt = 
                                    new ColorMessageTransformer();
                                cmt.parse(ej);
                                actionVector.add(cmt);
                            }
                            else if(ej.getTagName().equals("run"))
                            {
                                RunMessageTransformer rmt = 
                                    new RunMessageTransformer();
                                rmt.parse(ej);
                                actionVector.add(rmt);
                            }
                            else if(ej.getTagName().equals("intensify"))
                            {
                                IntensityMessageTransformer imt = 
                                    new IntensityMessageTransformer();
                                imt.parse(ej);
                                actionVector.add(imt);
                            }
                            else if(ej.getTagName().equals("font"))
                            {
                                FontMessageTransformer fmt = 
                                    new FontMessageTransformer();
                                fmt.parse(ej);
                                actionVector.add(fmt);
                            }
                            else if(ej.getTagName().equals("ignore"))
                            {
                                IgnoreMessageTransformer imt = 
                                    new IgnoreMessageTransformer();
                                imt.parse(ej);
                                actionVector.add(imt);
                            }
			    else if(ej.getTagName().equals("play"))
                            {
                                PlayMessageTransformer imt = 
                                    new PlayMessageTransformer();
                                imt.parse(ej);
                                actionVector.add(imt);
                            }
			    else if(ej.getTagName().equals("speak"))
                            {
                                SpeechMessageTransformer imt = 
                                    new SpeechMessageTransformer();
                                imt.parse(ej);
                                actionVector.add(imt);
                            }
                            else if(ej.getTagName().equals("text2image"))
                            {
                                Text2ImageMessageTransformer imt = 
                                    new Text2ImageMessageTransformer();
                                imt.parse(ej);
                                actionVector.add(imt);
                            }
                        }
                        nj = nj.getNextSibling();
                    }
                }
            }
            n = n.getNextSibling();
        }
        actionList.updateUI();
    }

    public Node
    createDoc(
        Document                                doc)
            throws Exception
    {
        Element base = doc.createElement("action"); 

        for(int ctr = 0; ctr < actionVector.size(); ctr++)
        {
            MessageTransformer mt = (MessageTransformer)
                                        actionVector.elementAt(ctr);
            Element kid = mt.createDoc(doc);
            base.appendChild(kid);
        }

        return base;
    }

    public void
    actionPerformed(
        ActionEvent                         e)
    {
        if(e.getSource() == colorButton)
        {
            Color c = colorPicker.showDialog(this, "Pick Color", 
                    colorButton.getBackground());
            colorButton.setBackground(c);
            intensitySlider.setBackground(c);
        }
        else if(e.getSource() == addButton)
        {
            MessageTransformer              mt;

            int position = positionCombo.getSelectedIndex();

            if(actionCombo.getSelectedIndex() == 0)
            {
                RunMessageTransformer rmt = 
                    new RunMessageTransformer(
                            parseText.getText(),
                            runText.getText(),
                            position);

                mt = rmt;
            }
            else if(actionCombo.getSelectedIndex() == 1)
            {
                ColorMessageTransformer cmt = 
                    new ColorMessageTransformer(
                            parseText.getText(),
                            colorButton.getBackground(),
                            position);

                mt = cmt;
            }
            else if(actionCombo.getSelectedIndex() == 2)
            {
                NotifyMessageTransformer nmt = 
                    new NotifyMessageTransformer(
                            parseText.getText(),
                            this.owner,
                            position);

                mt = nmt;
            }
            else if(actionCombo.getSelectedIndex() == 3)
            {
                IntensityMessageTransformer nmt =
                    new IntensityMessageTransformer(
                            parseText.getText(),
                            intensitySlider.getValue(),
                            position);

                mt = nmt;
            }
            else if(actionCombo.getSelectedIndex() == 4)
            {
                FontMessageTransformer fmt =
                    new FontMessageTransformer(
                            parseText.getText(),
                            fontButton.getFont(),
                            position);

                mt = fmt;
            }
            else if(actionCombo.getSelectedIndex() == 5)
            {
                IgnoreMessageTransformer imt =
                    new IgnoreMessageTransformer(
                            parseText.getText(),
                            position);
                mt = imt;
            }
	    else if(actionCombo.getSelectedIndex() == 6)
            {
                PlayMessageTransformer imt =
                    new PlayMessageTransformer(parseText.getText(),
					       position,
					       urlText.getText());
                mt = imt;
            }
	    else if(actionCombo.getSelectedIndex() == 7)
            {
                SpeechMessageTransformer imt =
                    new SpeechMessageTransformer(parseText.getText(),
						 position);
                mt = imt;
            }
            else if(actionCombo.getSelectedIndex() == 8)
            {
                Text2ImageMessageTransformer imt =
                    new Text2ImageMessageTransformer(parseText.getText(),
                                                     position,
                                                     imageText.getText());
                mt = imt;
            }
            else
            {
                return;
            }

            actionVector.addElement(mt);
            actionList.updateUI();
        }
        else if(e.getSource() == removeButton)
        {
            int ndx = actionList.getSelectedIndex();
            actionVector.removeElementAt(ndx);
            actionList.updateUI();
        }
        else if(e.getSource() == fontButton)
        {
            Font f = JFontChooser.showDialog(this);
            fontButton.setFont(f);
        }
    }

    public void 
    stateChanged(
        ChangeEvent                             e) 
    {
        if(e.getSource() == intensitySlider)
        {
            Color c = IntensityMessageTransformer.intensify(
                        colorButton.getBackground(), 
                        intensitySlider.getValue());
            
            intensitySlider.setBackground(c);
        }
    }

    public void 
    itemStateChanged(
        ItemEvent                               e)
    {
        if(actionCombo.getSelectedIndex() == 0)
        {
            cardLayout.show(cardPanel, RUN_CARD);
        }
        else if(actionCombo.getSelectedIndex() == 1)
        {
            cardLayout.show(cardPanel, COLOR_CARD);
        }
        else if(actionCombo.getSelectedIndex() == 2)
        {
            cardLayout.show(cardPanel, NOTIFY_CARD);
        }
        else if(actionCombo.getSelectedIndex() == 3)
        {
            cardLayout.show(cardPanel, INTENSITY_CARD);
        }
        else if(actionCombo.getSelectedIndex() == 4)
        {
            cardLayout.show(cardPanel, FONT_CARD);
        }
        else if(actionCombo.getSelectedIndex() == 5)
        {
            cardLayout.show(cardPanel, IGNORE_CARD);
        }
	else if(actionCombo.getSelectedIndex() == 6)
        {
            cardLayout.show(cardPanel, PLAY_CARD);
        }
	else if(actionCombo.getSelectedIndex() == 7)
        {
            cardLayout.show(cardPanel, SPEAK_CARD);
        }
	else if(actionCombo.getSelectedIndex() == 8)
        {
            cardLayout.show(cardPanel, IMAGE_CARD);
        }
    }

    class
    MyCellRenderer
        implements ListCellRenderer
    {
        public
        MyCellRenderer()
        {
        }

        public Component 
        getListCellRendererComponent(
            JList list,
            Object value,            // value to display
            int index,               // cell index
            boolean isSelected,      // is the cell selected
            boolean cellHasFocus)    // the list and the cell have the focus
        {
            MessageTransformer mt = (MessageTransformer) value;
            JComponent c = mt.getRendererComponent();

            if(isSelected)
            {
                c.setBackground(list.getSelectionBackground());
            }
            else
            {
                c.setBackground(list.getBackground());
            }

            c.setEnabled(list.isEnabled());
            c.setOpaque(true);

            return c;
        }
    }
}
