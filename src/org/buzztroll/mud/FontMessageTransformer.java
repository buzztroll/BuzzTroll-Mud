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

public class
FontMessageTransformer
    extends MessageTransformer
{
    protected String                            matchString;
    protected Font                              font;
    protected int                               position;


    public
    FontMessageTransformer()
    {
    }

    public
    FontMessageTransformer(
        String                                  match,
        Font                                    f,
        int                                     pos)
    {
        this.position = pos;
        this.font = f;
        this.matchString = match;
    }

    public DisplayMessage
    transform(
        DisplayMessage                          msg)
    {
        if(stringMatch(this.position, msg.getString(), this.matchString))
        {
            return new DisplayMessage(msg.getString(),
                            msg.getColor(),
                            this.font);
        }

        return msg;
    }

    public JComponent
    getRendererComponent()
    {
        String                             textString = "Use font ";

        textString = textString.concat(font.getName() + " when string ");

        if(this.position == CONTAINS)
        {
            textString = textString.concat("contains ");
        }
        else if(this.position == ENDS_WITH)
        {
            textString = textString.concat("ends with ");
        }
        else if(position == STARTS_WITH)
        {
            textString = textString.concat("starts with ");
        }

        textString = textString.concat(this.matchString);
        textString = textString.concat(".");

        JLabel jl = new JLabel(textString);
        jl.setFont(this.font);

        return jl;
    }

    public Element
    createDoc(
        Document                                doc)
        throws Exception
    {
        String                                  bold = "N";
        String                                  italic = "N";

        Element base = doc.createElement("font");
        base.setAttribute("match", this.matchString);
        base.setAttribute("position", new Integer(position).toString());
        base.setAttribute("name", font.getName());
        base.setAttribute("size", new Integer(this.font.getSize()).toString());

        if(this.font.isBold())
        {
            bold = "Y";
        }
        if(this.font.isItalic())
        {
            italic = "Y";
        }

        base.setAttribute("bold", bold);
        base.setAttribute("italic", italic);

        return base;
    }

    public void
    parse(
        Element                                 base)
    {
        int                                     size;
        int                                     style = Font.PLAIN;
        String                                  name;
        String                                  bold;
        String                                  italic;

        this.matchString = base.getAttribute("match");
        size = new Integer(base.getAttribute("size")).intValue();
        name = base.getAttribute("name");
        bold = base.getAttribute("bold");
        italic = base.getAttribute("italic");
        this.position = new Integer(base.getAttribute("position")).intValue();

        if(bold.equals("Y"))
        {
            style |= Font.BOLD;
        }
        if(italic.equals("Y"))
        {
            style |= Font.ITALIC;
        }

        this.font = new Font(name, style, size);
    }
}

class
ColorMessageTransformer
    extends MessageTransformer
{
    protected String                            matchString;
    protected Color                             color;
    protected int                               position;


    public
    ColorMessageTransformer()
    {
    }

    public
    ColorMessageTransformer(
        String                                  match,
        Color                                   color,
        int                                     pos)
    {
        this.position = pos;
        this.color = color;
        this.matchString = match;
    }

    public DisplayMessage
    transform(
        DisplayMessage                          msg)
    {
        if(stringMatch(this.position, msg.getString(), this.matchString))
        {
            return new DisplayMessage(msg.getString(), this.color,
                            msg.getFont());
        }

        return msg;
    }

    public JComponent
    getRendererComponent()
    {
        String                             textString = "Color Strings that ";

        if(this.position == CONTAINS)
        {
            textString = textString.concat("contain ");
        }
        else if(this.position == ENDS_WITH)
        {
            textString = textString.concat("end with ");
        }
        else if(position == STARTS_WITH)
        {
            textString = textString.concat("start with ");
        }

        textString = textString.concat(this.matchString);
        textString = textString.concat(".");

        JLabel jl = new JLabel(textString);
        jl.setForeground(this.color);

        return jl;
    }

    public Element
    createDoc(
        Document                                doc)
        throws Exception
    {
        Element base = doc.createElement("color");
        base.setAttribute("match", this.matchString);
        base.setAttribute("position", new Integer(position).toString());
        base.setAttribute("red", new Integer(this.color.getRed()).toString());
        base.setAttribute("green", new Integer(this.color.getGreen()).toString());
        base.setAttribute("blue", new Integer(this.color.getBlue()).toString());
        return base;
    }

    public void
    parse(
        Element                                 base)
    {
        int                                     red;
        int                                     green;
        int                                     blue;

        this.matchString = base.getAttribute("match");
        red = new Integer(base.getAttribute("red")).intValue();
        green = new Integer(base.getAttribute("green")).intValue();
        blue = new Integer(base.getAttribute("blue")).intValue();
        this.position = new Integer(base.getAttribute("position")).intValue();
        this.color = new Color(red, green, blue);

        System.out.println(this.matchString + ":" + this.color +":"+this.position);
    }
}

class
RunMessageTransformer
    extends MessageTransformer
{
    protected String                            matchString;
    protected String                            command;
    protected int                               position;

    public
    RunMessageTransformer()
    {
    }

    public
    RunMessageTransformer(
        String                                  match,
        String                                  command,
        int                                     pos)
    {
        this.position = pos;
        this.command = command;
        this.matchString = match;
    }

    public DisplayMessage
    transform(
        DisplayMessage                          msg)
    {
        if(stringMatch(this.position, msg.getString(), this.matchString))
        {
            try
            {
                Runtime rt = Runtime.getRuntime();
                System.err.println("RUNNING: " + this.command);
                rt.exec(this.command);
            }
            catch(Exception e)
            {
                System.err.println(e);
            }
        }

        return msg;
    }

    public JComponent
    getRendererComponent()
    {
        String                             textString = "Run <";

        textString = textString.concat(this.command + "> when string ");

        if(this.position == CONTAINS)
        {
            textString = textString.concat("contains ");
        }
        else if(this.position == ENDS_WITH)
        {
            textString = textString.concat("ends with ");
        }
        else if(position == STARTS_WITH)
        {
            textString = textString.concat("starts with ");
        }

        textString = textString.concat(this.matchString);
        textString = textString.concat(".");

        JLabel jl = new JLabel(textString);

        return jl;
    }

    public Element
    createDoc(
        Document                                doc)
        throws Exception
    {
        Element base = doc.createElement("run");
        base.setAttribute("match", this.matchString);
        base.setAttribute("command", this.command);
        base.setAttribute("position", new Integer(position).toString());

        return base;
    }

    public void
    parse(
        Element                                 base)
    {
        this.matchString = base.getAttribute("match");
        this.command = base.getAttribute("command");
        this.position = new Integer(base.getAttribute("position")).intValue();
    }
}

class
NotifyMessageTransformer
    extends MessageTransformer
{
    protected String                            matchString;
    protected int                               position;
    protected MudFrame                          mf;

    public
    NotifyMessageTransformer(
        MudFrame                                mf)
    {
        this.mf = mf;
    }

    public
    NotifyMessageTransformer(
        String                                  match,
        MudFrame                                mf,
        int                                     pos)
    {
        this.mf = mf;
        this.position = pos;
        this.matchString = match;
    }

    public DisplayMessage
    transform(
        DisplayMessage                          msg)
    {
        if(stringMatch(this.position, msg.getString(), this.matchString)
            && mf != null)
        {
            mf.setNotification(msg.getString());
        }

        return msg;
    }

    public JComponent
    getRendererComponent()
    {
        String                        textString = "Notify user when string ";

        if(this.position == CONTAINS)
        {
            textString = textString.concat("contains ");
        }
        else if(this.position == ENDS_WITH)
        {
            textString = textString.concat("ends with ");
        }
        else if(position == STARTS_WITH)
        {
            textString = textString.concat("starts with ");
        }

        textString = textString.concat(this.matchString);
        textString = textString.concat(".");

        JLabel jl = new JLabel(textString);

        return jl;
    }

    public Element
    createDoc(
        Document                                doc)
        throws Exception
    {
        Element base = doc.createElement("notify");
        base.setAttribute("match", this.matchString);
        base.setAttribute("position", new Integer(position).toString());

        return base;
    }

    public void
    parse(
        Element                                 base)
    {
        this.matchString = base.getAttribute("match");
        this.position = new Integer(base.getAttribute("position")).intValue();
    }
}

class
IntensityMessageTransformer
    extends MessageTransformer
{
    protected String                            matchString;
    protected int                               position;
    protected int                               intensity;

    public
    IntensityMessageTransformer()
    {
    }

    public
    IntensityMessageTransformer(
        String                                  match,
        int                                     intensity,
        int                                     pos)
    {
        this.intensity = intensity;
        this.position = pos;
        this.matchString = match;
    }

    public DisplayMessage
    transform(
        DisplayMessage                          msg)
    {
        if(stringMatch(this.position, msg.getString(), this.matchString))
        {
            Color c = intensify(msg.getColor(), this.intensity);
            return new DisplayMessage(msg.getString(), c, msg.getFont());
        }
        return msg;
    }

    public JComponent
    getRendererComponent()
    {
        String                        textString = "Intensify color by ";

        textString = textString.concat(new Integer(this.intensity).toString());
        textString = textString.concat("% when string ");

        if(this.position == CONTAINS)
        {
            textString = textString.concat("contains ");
        }
        else if(this.position == ENDS_WITH)
        {
            textString = textString.concat("ends with ");
        }
        else if(position == STARTS_WITH)
        {
            textString = textString.concat("starts with ");
        }

        textString = textString.concat(this.matchString);
        textString = textString.concat(".");

        JLabel jl = new JLabel(textString);

        return jl;
    }

    public Element
    createDoc(
        Document                                doc)
        throws Exception
    {
        Element base = doc.createElement("intensify");
        base.setAttribute("match", this.matchString);
        base.setAttribute("position", new Integer(position).toString());
        base.setAttribute("value", new Integer(this.intensity).toString());

        return base;
    }

    public void
    parse(
        Element                                 base)
    {
        this.matchString = base.getAttribute("match");
        this.position = new Integer(base.getAttribute("position")).intValue();
        this.intensity = new Integer(base.getAttribute("value")).intValue();
    }

    public static Color
    intensify(
        Color                           inColor,
        int                             degree)
    {
        float hsb[] = new float[3];

        Color.RGBtoHSB(inColor.getRed(), inColor.getGreen(),
                        inColor.getBlue(), hsb);

        if(degree < 0)
        {
                // get the percetage of darker
            float val = 1.0f -
                (float)Math.abs(degree) / 100.0f;
            // make b a lower number
            // handle s
            hsb[2] = hsb[2] * val;
        }
        else
        {
            float val =  1.0f - (float)degree / 100.0f;
            // handle b
            hsb[1] = hsb[1] * val;
        }

        Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);

        return c;
    }
}

class
IgnoreMessageTransformer
    extends MessageTransformer
{
    protected String                            matchString;
    protected int                               position;

    public
    IgnoreMessageTransformer()
    {
    }

    public
    IgnoreMessageTransformer(
        String                                  match,
        int                                     pos)
    {
        this.position = pos;
        this.matchString = match;
    }

    public DisplayMessage
    transform(
        DisplayMessage                          msg)
    {
        if(stringMatch(this.position, msg.getString(), this.matchString))
        {
            System.out.println("match" + this.matchString);
            return new DisplayMessage("", msg.getColor(), msg.getFont());
        }
        return msg;
    }

    public JComponent
    getRendererComponent()
    {
        String                        textString = "Ignore message when it ";

        if(this.position == CONTAINS)
        {
            textString = textString.concat("contains ");
        }
        else if(this.position == ENDS_WITH)
        {
            textString = textString.concat("ends with ");
        }
        else if(position == STARTS_WITH)
        {
            textString = textString.concat("starts with ");
        }

        textString = textString.concat(this.matchString);
        textString = textString.concat(".");

        JLabel jl = new JLabel(textString);

        return jl;
    }

    public Element
    createDoc(
        Document                                doc)
        throws Exception
    {
        Element base = doc.createElement("ignore");
        base.setAttribute("match", this.matchString);
        base.setAttribute("position", new Integer(position).toString());

        return base;
    }

    public void
    parse(
        Element                                 base)
    {
        this.matchString = base.getAttribute("match");
        this.position = new Integer(base.getAttribute("position")).intValue();
    }
}
