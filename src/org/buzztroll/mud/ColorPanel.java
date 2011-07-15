package org.buzztroll.mud;

import java.lang.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;


public class
ColorPanel
    extends JPanel
    implements ChangeListener
{
    protected JSlider               redSlider;
    protected JSlider               greenSlider;
    protected JSlider               blueSlider;

    protected JLabel                redLabel;
    protected JLabel                greenLabel;
    protected JLabel                blueLabel;

    protected JPanel                displayC;

    public
    ColorPanel()
    {
        super();

        JPanel                      tempP;
        JPanel                      tempP2;

        tempP = new JPanel();
        tempP.setLayout(new GridLayout(1, 3, 5, 5));

        redLabel = new JLabel("255");
        greenLabel = new JLabel("255");
        blueLabel = new JLabel("255");
        
        redSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
        greenSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);
        blueSlider = new JSlider(JSlider.VERTICAL, 0, 255, 255);

        tempP2 = new JPanel();
        tempP2.setLayout(new BorderLayout());
        tempP2.add("North", new JLabel("red"));
        tempP2.add("Center", redSlider);
        tempP2.add("South", redLabel);
        tempP.add(tempP2);

        tempP2 = new JPanel();
        tempP2.setLayout(new BorderLayout());
        tempP2.add("North", new JLabel("green"));
        tempP2.add("Center", greenSlider);
        tempP2.add("South", greenLabel);
        tempP.add(tempP2);

        tempP2 = new JPanel();
        tempP2.setLayout(new BorderLayout());
        tempP2.add("North", new JLabel("blue"));
        tempP2.add("Center", blueSlider);
        tempP2.add("South", blueLabel);
        tempP.add(tempP2);

        displayC = new JPanel();
        displayC.setBorder(new EtchedBorder(EtchedBorder.RAISED));

        this.setLayout(new BorderLayout());
        this.add("Center", tempP);
        this.add("South", displayC);

        redSlider.addChangeListener(this);
        greenSlider.addChangeListener(this);
        blueSlider.addChangeListener(this);
    }

    public int
    getRed()
    {
        return this.redSlider.getValue();
    }

    public int
    getGreen()
    {
        return this.greenSlider.getValue();
    }

    public int
    getBlue()
    {
        return this.blueSlider.getValue();
    }

    public Color
    getColor()
    {
        return new Color(this.getRed(), this.getGreen(), this.getBlue());
    }

    public void
    addChangeListener(
        ChangeListener                      cl)
    {
        this.redSlider.addChangeListener(cl);
        this.greenSlider.addChangeListener(cl);
        this.blueSlider.addChangeListener(cl);
    }

    public void 
    stateChanged(
        ChangeEvent                         e)
    {
        JSlider                             js;
        String                              s;

        if(this.redSlider == e.getSource())
        {
            s = new Integer(this.redSlider.getValue()).toString();
            s = s.concat("   ").substring(0, 3);
            this.redLabel.setText(s);
        }
        else if(this.greenSlider == e.getSource())
        {
            s = new Integer(this.greenSlider.getValue()).toString();
            s = s.concat("   ").substring(0, 3);
            this.greenLabel.setText(s);
        }
        else if(this.blueSlider == e.getSource())
        {
            s = new Integer(this.blueSlider.getValue()).toString();
            s = s.concat("   ").substring(0, 3);
            this.blueLabel.setText(s);
        }

        Color c = new Color(this.redSlider.getValue(), 
                        this.greenSlider.getValue(), 
                        this.blueSlider.getValue());

        displayC.setBackground(c);
    }
}
