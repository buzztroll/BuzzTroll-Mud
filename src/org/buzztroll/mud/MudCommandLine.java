package org.buzztroll.mud;

import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Vector;
import java.io.*;
import java.net.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;

public class
MudCommandLine
    implements MudConnectionListener,
               Runnable
{
    protected MudConnection             mc;
    protected Thread                    tempThread;
    protected Thread                    readThread;
    protected StringBuffer              sb;
    protected Vector                    transformVector;

    public static void
    main(
        String                          args[])
    {

        try
        {
            MudCommandLine mcl = new MudCommandLine("mud.mcs.anl.gov", 
                                            7779, "Mr.Coffee", "etygixo"); 
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public
    MudCommandLine(
        String                          host,
        int                             port,
        String                          username,
        String                          password)
            throws Exception
    {
        this.mc = new MudConnection(host, port, this);
        this.mc.connect(username, password);

        transformVector = new Vector();
        readThread = new Thread(this);
        readThread.start();

        sb = new StringBuffer();
    }


    public void
    connectTic()
    {
    }

    public void
    disconnected()
    {
        System.out.println("disconnected");
    }

    public void
    messageReceived(
        String                          message)
    {
        DisplayMessage dm = new DisplayMessage(message, Color.red, null);

        for(int ctr = 0; ctr < transformVector.size(); ctr++)
        {
            MessageTransformer mt = (MessageTransformer) 
                                        transformVector.elementAt(ctr);
            dm = mt.transform(dm);
        }
        System.out.write('\r');
        System.out.println(dm.getString());
        System.out.print("> " + sb.toString());
        System.out.flush();
    }

    public void
    run()
    {

        while(true)
        {
            if(Thread.currentThread() == tempThread)
            {
                try
                {
                    Thread.currentThread().sleep(3000);
                    this.messageReceived("Test string");
                }
                catch(Exception e)
                {
                    System.err.println(e);
                }
            }
            else
            {
                try
                {
                    byte b[] = new byte[1];
                    System.in.read(b);
                    char ch = (char)b[0];
                    sb.append(ch);
                    if(ch == '\n')
                    {
                        System.out.write('\r');
                        this.mc.sendLine(sb.toString());
                        sb = new StringBuffer();
                    }
                }
                catch(Exception e)
                {
                    System.err.println(e);
                }
            }
        }
    }

    public void
    readFile(
        String                                  fname)
            throws Exception
    {
        DocumentBuilderFactory builderFactory =
            DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(false);

        DocumentBuilder factory = builderFactory.newDocumentBuilder();
        Document doc = factory.parse(fname);

                Node                                    n;
        Node                                    nj;
        Element rootE = doc.getDocumentElement();

        n = rootE.getFirstChild();
        while(n != null)
        {
            if(Node.ELEMENT_NODE == n.getNodeType())
            {
                Element e = (Element)n;
                if(e.getTagName().equals("run"))
                {
                    RunMessageTransformer rmt = 
                        new RunMessageTransformer();
                    rmt.parse(e);
                    transformVector.add(rmt);
                }
            }
            n = n.getNextSibling();
        }
    }

}
