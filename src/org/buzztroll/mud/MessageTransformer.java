package org.buzztroll.mud;

import java.lang.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class
MessageTransformer
{
    public static final int                     STARTS_WITH = 0;
    public static final int                     CONTAINS = 1;
    public static final int                     ENDS_WITH = 2;

    public static final int                     MAX_TYPE = 5;

    public abstract DisplayMessage
    transform(
        DisplayMessage                          msg);


    public static boolean
    stringMatch(
        int                                     position,
        String                                  msg,
        String                                  match)
    {
        if(match == null || match.equals("") ||
            msg == null || msg.equals("") ||
            msg.length() < match.length())
        {
            return false;
        }

        if(position == CONTAINS && msg.indexOf(match) > -1)
        {
            return true;
        }
        else if(position == STARTS_WITH && 
		msg.startsWith(match)) 
        {
            return true;
        }
        else if(position == ENDS_WITH &&
		msg.endsWith(match)) 
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public abstract JComponent
    getRendererComponent();

    public abstract Element
    createDoc(
        Document                                doc)
        throws Exception;

    public abstract void
    parse( 
        Element                                 element);
}
