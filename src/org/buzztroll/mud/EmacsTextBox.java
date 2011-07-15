package org.buzztroll.mud;

import java.lang.*;
import java.util.Vector;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;


public
class EmacsTextBox
    extends JTextArea
    implements KeyListener
{
    protected Vector                  bangVector = new Vector(25, 25);
    protected int                     bangIndex = 0;


    public
    EmacsTextBox()
    {
        super();

        addKeyListener(this);
    }    

    public String getRawText() {
	String txt = getText();
	int len = txt.length();
	if (len > 0 && txt.charAt(len - 1) == '\n') {
	    return txt.substring(0, len-1);
	} else {
	    return txt;
	}
    }

    protected
    void processComponentKeyEvent(
        KeyEvent                      e)  
    {
        if(e == null)
        {
            return; 
        }
        if(e.getKeyChar() != '\n')
        {
            super.processComponentKeyEvent(e);
        }
        else
        {
            String msg = this.getText();

            if(msg.equals(""))
            {
                return;
            }
            if(msg.charAt(0) == '\n' &&
              msg.length() > 1)
            {
                msg = msg.substring(1);
            }
            if(msg.charAt(msg.length() - 1) == '\n')
            {
                msg = msg.substring(0, msg.length()-1);
            }

            if(msg.length() > 0)
            {
                bangVector.addElement(msg);
                bangIndex = bangVector.size();
            }

            this.setText(msg);
        }
    }

    public void
    keyReleased(
        KeyEvent                      ke)
    {
    }

    public void
    keyTyped(
        KeyEvent                      ke)
    {
    }

    public void
    keyPressed(
        KeyEvent                      ke)
    {
        int                           keyCode;
        String                        msg;

        keyCode = ke.getKeyCode();

        try
        {
            if(keyCode == KeyEvent.VK_UP)
            {
                bangIndex--;
                if(bangIndex >= 0)
                {
                    msg = (String)bangVector.elementAt(bangIndex);
                    this.setText(msg);
                }
                else if(bangIndex == -1)
                {
                    bangIndex = 0;
                    msg = (String)bangVector.elementAt(bangIndex);
                    this.setText(msg);
                }
            }
            else if(keyCode == KeyEvent.VK_DOWN)
            {
                bangIndex++;
                if(bangIndex < bangVector.size())
                {
                    msg = (String)bangVector.elementAt(bangIndex);
                    this.setText(msg);
                }
                else
                {
                    bangIndex = bangVector.size();
                    this.setText("");
                }
            }
            else if(keyCode == KeyEvent.VK_ENTER)
            {
            }
        }
        catch(Exception e)
        {
            bangIndex = 0;
        }
    }
}
