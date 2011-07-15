package org.buzztroll.mud;

import java.util.StringTokenizer;
import java.util.Hashtable;
import java.util.Vector;
import java.awt.*;
import java.io.*;
import java.text.*;
import java.net.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class MudChatWindow extends JFrame
    implements MudConnectionListener {
    
    private static final String PAGES =
	" pages, ";
    
    private static final String SAYS = 
	" [to you]: ";

    private static final String WHISPER =
	" whispers to you, ";

    private static final String FROM =
	"(from ";

    protected JPopupMenu menu;
    protected JMenuItem pasteMenu;
    protected TextDisplay display;
    protected EmacsTextBox commandText;

    private JRadioButton privateReplyCB;
    private JRadioButton publicReplyCB;

    private Color peerColor = Color.red;
    private Color selfColor = Color.green;

    private MudConnection connection;
    private String who = null;
    private int whoLen;
    private String self = null;

    public MudChatWindow(MudConnection connection,
			 String self,
			 String peer,
			 ConfigFrame configFrame) {
	super("Chat - [" + peer + "]");

	this.connection = connection;
	this.connection.addListener(this);

	this.who = peer;
	this.whoLen = who.length();
	
	this.self = self;

	this.display = new TextDisplay(configFrame);
	this.display.addMouseListener(new ChatMouseListener());

	this.commandText = new EmacsTextBox();
	this.commandText.addKeyListener(new ChatKeyListener());
	this.commandText.setRows(1);
        this.commandText.setBorder(new EtchedBorder());
	this.commandText.setLineWrap(true);
        this.commandText.setWrapStyleWord(true);

	this.getContentPane().setLayout(new BorderLayout());
	this.getContentPane().add("Center", this.display);
	this.getContentPane().add("South", this.commandText);

	this.addWindowListener(new ChatWindowListener(this));

	this.menu = new JPopupMenu("BuzzTroll Chat");
	this.pasteMenu = menu.add("Paste");
        this.pasteMenu.addActionListener(new ChatActionListener());

	JMenu typeMenu = new JMenu("Reply");
	this.privateReplyCB = new JRadioButton("Private");
	this.publicReplyCB = new JRadioButton("Public");

	typeMenu.add(this.privateReplyCB);
	typeMenu.add(this.publicReplyCB);

	ButtonGroup group = new ButtonGroup();
	group.add(this.privateReplyCB);
	group.add(this.publicReplyCB);

	this.menu.add(typeMenu);

	this.menu.addSeparator();
        this.menu.add("Dismiss");

	this.peerColor = configFrame.namePanel.getColor(peer);
	this.selfColor = configFrame.namePanel.getSelfColor();

	this.privateReplyCB.setSelected(true);
	this.commandText.grabFocus();
    }

    public void disconnected() {
	display.addMessage(new DisplayMessage("Connection broken",
					      Color.red, 
					      null));
    }

    public void messageReceived(String message) {
	if (message == null) {
	    return;
	}
	
	String msg = null;

	if (message.regionMatches(true, 0, who, 0, whoLen)) {
	    if (message.regionMatches(true, whoLen, PAGES, 
				      0, PAGES.length())) {
		msg = message.substring(whoLen + PAGES.length() + 1,
					message.length()-1);
	    } else if (message.regionMatches(true, whoLen, SAYS, 
					     0, SAYS.length())) {
		msg = message.substring(whoLen + SAYS.length());
	    } else if (message.regionMatches(true, whoLen, WHISPER,
					     0, WHISPER.length())) {
		msg = message.substring(whoLen + WHISPER.length() + 1,
					message.length()-1);
	    } else {
		return;
	    }
	} else if (message.startsWith(FROM)) {
	    int pos = message.indexOf(')', FROM.length());
	    if (pos == -1) {
		return;
	    }
	    if (message.regionMatches(true, pos+2, who, 0, whoLen)) {
		msg = message.substring(pos+2+whoLen+1);
	    } else {
		return;
	    }
	} else {
	    return;
	}

	msg = who + ": " + msg;
	this.display.addMessage(new DisplayMessage(msg, peerColor, null));

	commandText.grabFocus();
    }

    public void connectTic() {}

    class ChatKeyListener implements KeyListener {
	public void keyPressed(KeyEvent ke) {}
	public void keyReleased(KeyEvent ke) {}

	public void keyTyped(KeyEvent ke) {
	    if( ke.getKeyChar() == '\n') {
		String msg = commandText.getRawText();
		if (msg.length() == 0) {
		    return;
		}
		if (privateReplyCB.isSelected()) {
		    connection.sendLine("page " + who + " " + msg);
		} else {
		    connection.sendLine(who + ", " + msg);
		}
		commandText.setText("");
		display.addMessage(new DisplayMessage(self + ": " + msg, 
						      selfColor, null));
	    }
	}
    }
    
    class ChatWindowListener extends WindowAdapter {

	MudChatWindow window;

	public ChatWindowListener(MudChatWindow window) {
	    this.window = window;
	}

	public void windowClosing(WindowEvent we) {
	    connection.removeListener(this.window);
	    this.window.dispose();
	}
    }

    class ChatActionListener implements ActionListener {
	
	public void actionPerformed(ActionEvent e) {
	    if (e.getSource() == pasteMenu) {
		Clipboard clipboard = getToolkit().getSystemClipboard();
		Transferable data = clipboard.getContents(this);
		if(data == null ||
                   !data.isDataFlavorSupported (DataFlavor.stringFlavor)) {
		    return;
		}

		String strData = null;
		try {
		    strData = 
			(String)data.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception ex) {
		    System.err.println(ex.getMessage());
		    return;
		}
		
		StringBuffer buf = new StringBuffer();
		buf.append("@pasteto ").append(who).append("\n");
		buf.append(strData);
		buf.append("\n.\n");
		
		connection.sendLine(buf.toString());
	    }
	}
    }
    
    class ChatMouseListener extends MouseAdapter {
	public void mouseClicked(MouseEvent e) {
	    if ( ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) ||
		 ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) ) {
		// TBD: disable paste button if nothing in clipboard
		// just like in MudClient.java
		menu.show(e.getComponent(), e.getX(), e.getY());
	    } else {
		commandText.grabFocus();
	    }
	}
    }
}
