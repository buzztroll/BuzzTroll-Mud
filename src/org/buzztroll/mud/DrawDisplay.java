// not used in this build

package org.buzztroll.mud;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.lang.*;
import java.util.*;

public class 
DrawDisplay
    extends DisplayInterface
{
    protected Canvas                      canvas;
    protected JScrollPane                 scrollPane;

    public
    DrawDisplay()
    {
        super();
        canvas = new Canvas(this);
        scrollPane = new JScrollPane(canvas);
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.setLayout(new BorderLayout());
        this.add("Center", scrollPane);
    }

    public void 
    addMessage( 
        DisplayMessage                      dm)
    {
        String                             message;
        Color                              color;
        JScrollBar                         jsb;

        message = dm.getString();
        color = dm.getColor();

        jsb = scrollPane.getVerticalScrollBar();
        jsb.setValue(jsb.getMaximum());        

        canvas.addMessage(message, color);
    }
}


class 
Canvas
    extends JPanel
{
    protected int                         maxMessages;
    protected Vector                      messageList;
    protected FontMetrics                 fontMetrics;
    protected int                         fontHeight = -1;
    protected int                         maxListSize = 500;
    protected static final int            STD_BUFFER = 5;
    protected boolean                     firstPaint = true;
    protected DrawDisplay                 dd;

    public 
    Canvas(
        DrawDisplay                       dd)
    {
        super();

        Font                              f;
        this.setBorder(
            new CompoundBorder(
                new EtchedBorder(
                       EtchedBorder.RAISED),
                       new EmptyBorder(3, 3, 3, 3)));

        this.dd = dd;
        messageList = new Vector(10, 10);
        firstPaint = true;

        this.setBackground(Color.black);
       
 
        f = Font.getFont("Monospaced", getFont());
        this.setFont(f);
    }

    public void 
    addMessage( 
        String                             message,
        Color                              color)
    {
        ColorMessage                       cm;

        cm = new ColorMessage(message, color);

        messageList.addElement(cm);

        if(messageList.size() > maxListSize)
        { 
            messageList.removeElementAt(0);
        }

        this.setPreferredSize(new Dimension(dd.getWidth(), 
         maxListSize  * 
             (int)(fontMetrics.getHeight()+fontMetrics.getHeight()*0.2+2)));
 
        repaint();
    }

    public void
    paintComponent(
        Graphics                           g)
    {
        int                                ctr;
        int                                ctr2;
        ColorMessage                       cm;
        int                                bottom;
        int                                x;
        Vector                             strings;

        g.fillRect(0, 0, getWidth(), getHeight()); 
        x = this.getX() + STD_BUFFER;

        bottom = this.getHeight() - STD_BUFFER;

        ctr = messageList.size() - 1;
        while(ctr >= 0)
        {
            int                           nextHeight;

            cm = (ColorMessage) messageList.elementAt(ctr);

            g.setColor(cm.color); 
 
            strings = stringBreakPoint(cm.message, 0); 
            nextHeight = bottom - (strings.size() * (fontHeight+1));
            bottom = nextHeight;
            for(ctr2 = 0; ctr2 < strings.size(); ctr2++)
            { 
                String                     tempS;

                tempS = (String)strings.elementAt(ctr2);
       
                g.drawString(tempS, x, nextHeight);
                nextHeight += (1 + fontHeight);    
            }
            ctr--;
            bottom -= (STD_BUFFER); // + fontHeight);
        }
    }

    protected Vector
    stringBreakPoint(
        String                            inS,
        int                               offset)
    {
        int                                maxLength;
        int                                endIndex = 0;
        boolean                            done = false;
        boolean                            done2 = false;
        int                                end;
        Vector                             strings;
        String                             s;

        s = inS.substring(offset);
        strings = new Vector(10, 10);
        end = this.getWidth() - (2 * STD_BUFFER);
        while(!done)
        {
            if(fontMetrics.stringWidth(s) < end)
            {
                strings.addElement(s);
                done = true;
            }
            else
            {
                endIndex = s.length();
                done2 = false;
                while(!done2)
                {
                    endIndex = s.lastIndexOf(' ', endIndex);
                    if(fontMetrics.stringWidth(
                           s.substring(0, endIndex)) < end)
                    {
                        done2 = true;
                        strings.addElement(s.substring(0, endIndex));
                        s = s.substring(endIndex);
                    }   
                    else if(endIndex <= 0)
                    {
                        done2 = true;
                        done = true;
                        strings.addElement(s);
                    }
                    endIndex--;
                }
            }
        } 

        return strings;
    }

    public void
    setFont(
        Font                              f)
    {
        super.setFont(f);

        fontMetrics = this.getFontMetrics(f);
        fontHeight = fontMetrics.getHeight();
    }
}

class
ColorMessage
{
    Color                                  color;
    String                                 message;

    ColorMessage(
        String                             message,
        Color                              color)
    {
        this.message = message;
        this.color = color;
    }
}
