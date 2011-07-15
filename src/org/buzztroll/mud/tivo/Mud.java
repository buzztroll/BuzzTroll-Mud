package org.buzztroll.mud.tivo;

import java.awt.*;

import java.net.*;
import java.io.*;

import java.util.*;

import com.tivo.hme.util.ArgumentList;
import com.tivo.hme.sdk.*;

import org.buzztroll.mud.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Mud extends Application implements Runnable {

    public final static String URI = "Mud";

    private Socket socket;
    private OutputStream out;
    private InputStream in;

    protected MudView mudView;

    protected SingleMudView singleMudView;
    protected MultipleMudView multipleMudView;

    // shared
    protected int selectionIndex = -1;
    protected ArrayList list = new ArrayList();
    
    protected HostSetupPanel hostSetupPanel;
    protected NameSetupPanel nameSetupPanel;
    protected ActionPanel actionPanel;
    protected PreferencesPanel preferencesPanel;

    protected void init(Context context) throws Exception {

        this.hostSetupPanel = new HostSetupPanel();
        this.nameSetupPanel = new NameSetupPanel();
        this.actionPanel = new ActionPanel(null);
        this.preferencesPanel = new PreferencesPanel();
        
        String configFile = ((MudFactory)context.getFactory()).getConfigFile();

        Document doc = readFile(configFile);
        this.hostSetupPanel.parse(doc);
        this.nameSetupPanel.parse(doc);
        this.actionPanel.parse(doc);
        this.preferencesPanel.parse(doc);

        this.multipleMudView = new MultipleMudView(this);
        this.singleMudView = new SingleMudView(this);

        this.mudView = multipleMudView;

        connect();
    }

    private void connect() {
        byte[] login = ("connect " + this.hostSetupPanel.getUsername() +
            " " + this.hostSetupPanel.getPassword() + "\r\n").getBytes();
        
        try {
            this.socket = new Socket(this.hostSetupPanel.getHostname(),
                                     this.hostSetupPanel.getPort());
            this.out = this.socket.getOutputStream();
            this.in = this.socket.getInputStream();
            out.write(login);
            out.flush();
            //this.in = new FileInputStream("in.txt");
            new Thread(this).start();
        } catch (Exception e) {
            // ignore exception
            disconnect();
        }
    }

    private void disconnect() {
        if (this.socket != null) {
            try {
                this.socket.close();
            } catch (Exception e) {
                // ignore exception
            }
        }
    }
    
    private void repaintScreen() {
        this.mudView.repaintScreen();
    }

    private void clearScreen() {
        this.mudView.clearScreen();
    }
    
    public void run() {
        BufferedReader reader = 
            new BufferedReader(new InputStreamReader(this.in));
        String line = null;
        DisplayMessage dm;
        try {
            while((line = reader.readLine()) != null) {
                line = line.trim();

                dm = new DisplayMessage(line, Color.white, null);
                
                dm = this.nameSetupPanel.transform(dm);
                dm = this.actionPanel.transform(dm);

                this.list.add(dm);

                repaintScreen();
            }
        } catch (Exception e) {
            this.list.add(
                  new DisplayMessage("Connection failed: " + e.toString(),
                                     Color.red, null));
            e.printStackTrace();
        }
        disconnect();
    }

    public boolean handleKeyPress(int code, long rawcode) {
        switch (code) {
        case Application.KEY_CLEAR:
            // switch to default view
            this.mudView = this.multipleMudView;
            this.mudView.switchScreen();
            // disconnect
            disconnect();
            // reset the view
            this.multipleMudView.reset();
            // reconnect
            connect();
            return true;
        }
        return this.mudView.handleKeyPress(code, rawcode);
    }
    
    public boolean handleKeyPressSub(int code, long rawcode) {
        return super.handleKeyPress(code, rawcode);
    }

    public synchronized void close() {
        disconnect();
        this.list.clear();
        super.close();
    }

    public Document readFile(String fname) throws Exception {
        DocumentBuilderFactory builderFactory = 
            DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(false);
        
        DocumentBuilder factory = builderFactory.newDocumentBuilder();
        Document doc = factory.parse(fname);
        
        return doc;
    }    
    
    public static class MudFactory extends Factory {
        
        private String configFile;

        protected void init(ArgumentList args) {
            if (args.getRemainingCount() == 0) {
                usage();
            }
            this.configFile = args.shift();
        }

        private void usage() {
            System.err.println("Usage: Mud <configFile>");
            System.exit(1);
        }

        public String getConfigFile() {
            return this.configFile;
        }
    }
    
}
