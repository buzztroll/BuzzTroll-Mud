package org.buzztroll.mud;

import java.lang.*;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element; 
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class
TransformPanel
    extends JPanel
{


    public abstract void
    parse(
        Document                                doc);

    public abstract DisplayMessage
    transform(
        DisplayMessage                          msg);

    public abstract Node
    createDoc(
        Document                                doc)
            throws Exception;

    public abstract String
    getName();

    public abstract void
    clear();    
}
