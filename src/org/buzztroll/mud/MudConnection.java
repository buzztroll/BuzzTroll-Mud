package org.buzztroll.mud;

import java.lang.*;
import java.net.*;
import java.io.*;
import java.util.*;

public class
MudConnection
    implements Runnable
{
    protected Socket                        sock;
    protected String                        hostname;
    protected int                           port;
    protected Thread                        connectThread;
    protected Thread                        readerThread;
    protected Thread                        keepAliveThread;
    protected DataOutputStream              dos;
    protected boolean                       connected = false;
    protected boolean                       keepAlive = false;
    protected List                          listeners;
    protected BufferedReader                dis;

    private final int                       PING_TIME = 60 * 1000 * 10;

    public  
    MudConnection(
        String                              hostname,
        int                                 port,
        MudConnectionListener               l)
    {
        this.hostname = hostname;
        this.port = port;
	addListener(l);
    }

    public void addListener(MudConnectionListener listener) {
	if (this.listeners == null) {
	    this.listeners = new ArrayList();
	}
	this.listeners.add(listener);
    }

    public void removeListener(MudConnectionListener listener) {
	if (this.listeners != null) {
	    this.listeners.remove(listener);
	}
    }

    public void
    connect(
        String                              username,
        String                              password)
            throws Exception
    {
        this.sock = new Socket(this.hostname, this.port);
        this.sock.setKeepAlive(true);
        this.sock.setSoTimeout(30000);

        this.connected = true;

        dos = new DataOutputStream(sock.getOutputStream());
        if(username != null && password != null)
        {
            String tempS = new String("connect "+username+" "+password);
            sendLine(tempS);
        }
        dis = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));

        this.connected = true;
        readerThread = new Thread(this);
        readerThread.start();

//        keepAliveThread = new Thread(this);
//        keepAliveThread.start();
    }

    public void
    close()
        throws Exception
    {
        this.sock.close();
    }

    public void
    sendLine(
        String                              msg)
    {
        try
        {
            if(msg != null && !msg.equals(""))
            {
                dos.writeBytes(msg);
                dos.writeBytes("\r\n");
                dos.flush();
            }
        }
        catch(Exception e)
        {
            System.err.println("1 " + e);
	    for (int i=0;i<this.listeners.size();i++) {
		MudConnectionListener listener =
		    (MudConnectionListener)this.listeners.get(i);
		listener.disconnected();
	    }
            connected = false;
        }
    }

    public void
    run()
    {
        Thread                          thread;
        String                          tempS = null;

        thread = Thread.currentThread();

        try
        {
            if(thread == keepAliveThread)
            {
                while(connected)
                {
                    try
                    {
                        thread.sleep(this.PING_TIME);
                    }
                    catch(Exception e) {}
                    sendLine("mu me ping...");
                }
            }
            else if(thread == readerThread)
            {
                    tempS = dis.readLine();
                while(connected && tempS != null)
                {
		    for (int i=0;i<this.listeners.size();i++) {
			MudConnectionListener listener =
			    (MudConnectionListener)this.listeners.get(i);
			listener.messageReceived(tempS);
		    }
                    boolean real_read = false;
                    while(!real_read)
                    {
                        real_read = true;
                        try
                        {
                            tempS = dis.readLine();
                        }
                        catch(java.net.SocketTimeoutException soe)
                        {
                            real_read = false;
                        }
                    }
                }
            }
            else if(thread == connectThread)
            {
                while(!connected)
                {
                    try
                    {
                        thread.sleep(this.PING_TIME);
                    }
                    catch(Exception e) {}
		    for (int i=0;i<this.listeners.size();i++) {
			MudConnectionListener listener =
			    (MudConnectionListener)this.listeners.get(i);
			listener.connectTic();
		    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.err.println("2 " + e);
	    for (int i=0;i<this.listeners.size();i++) {
		MudConnectionListener listener =
		    (MudConnectionListener)this.listeners.get(i);
		listener.disconnected();
	    }
            connected = false;
        }
    }
}
