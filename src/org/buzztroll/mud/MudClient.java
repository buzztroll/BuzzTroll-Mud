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

import com.ozten.font.JFontChooser;




public class
MudClient
    extends JPanel
    implements PopupMenuListener,
               KeyListener,
               ActionListener,
               WindowListener,
               MouseListener,
               MudConnectionListener
{
    protected TextDisplay               display;
    protected MudConnection             mudConnection;
    protected CardLayout                cardLayout;
    protected MudFrame                  frame;
    protected EmacsTextBox              commandText;
    protected JPopupMenu                menu;
    protected JMenuItem                 pasteMenu;
    protected JMenuItem                 pasteToMenu;
    protected JMenuItem                 pasteBoxMenu;
    protected JMenuItem                 configMenu;
    protected JMenuItem                 fontMenu;
    protected JMenuItem                 reconnectMenuItem;
    protected JMenuItem                 chatMenuItem;
    protected JMenuItem                 searchMenuItem;
    protected JMenuItem                 dictionaryMenuItem;
    protected JCheckBoxMenuItem         autoScrollMenuItem;
    protected Clipboard                 clipboard;
    protected PasteDialog               pd = null;
    protected JLabel                    connectLabel;
    protected ConfigFrame               configFrame;
    protected JProgressBar              progressBar;

    public
    MudClient(
        MudFrame                        frame)
            throws Exception
    {
        configFrame = new ConfigFrame(frame);
        configFrame.setVisible(true);

        if(configFrame.getResult())
        {
            this.init(frame);
        }
        else
        {
            throw new Exception();
        }
    }

    public 
    MudClient(
        MudFrame                        frame,
        String                          fname)
            throws Exception
    {
        super();

        configFrame = new ConfigFrame(frame, fname);
	
        this.init(frame);
    }

    public void
    init(
        MudFrame                        frame)
    {
        this.frame = frame;
        this.addKeyListener(this);
        this.clipboard = getToolkit().getSystemClipboard();
        this.menu = new JPopupMenu("BuzzTroll MUD");

        arrangeGUI();
        cardLayout.show(this, "TIC");

        try
        {
            mudConnection = new MudConnection(  
                                configFrame.getHostname(),
                                configFrame.getPort(),
                                this);
        
            System.out.println("Connecting to " +
                                configFrame.getUsername() + ":" +
                                configFrame.getPassword());

            mudConnection.connect(
                                configFrame.getUsername(),
                                configFrame.getPassword());
            cardLayout.show(this, "MAIN");
        }
        catch(Exception e)
        {
            System.err.println(e);
            connectLabel.setText("Could not connect.");
        }
    }

    public String
    toString()
    {
        return this.configFrame.getContactName();
    }

    public void
    connectTic()
    {
        progressBar.setValue(progressBar.getValue() + 5);
    }

    public void
    disconnected()
    {
        System.err.println("Disconnected");
        display.addMessage(new DisplayMessage("Server connection broken", Color.red, null));
        display.addMessage(new DisplayMessage("Reconnect or restart your client.", Color.red, null));
    }

    public void
    messageReceived(
        String                          message)
    {
        DisplayMessage dm = configFrame.transform(message);

        display.addMessage(dm);
    }


    protected void
    arrangeGUI()
    {
        JPanel                              ticPanel = new JPanel();
        JPanel                              mainPanel = new JPanel();

        cardLayout = new CardLayout();
        this.setLayout(cardLayout);

        progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 100);
        progressBar.setValue(0);

        commandText = new EmacsTextBox();
        commandText.addKeyListener(this);

        commandText.setRows(1);
        commandText.setBorder(new EtchedBorder());

        commandText.setLineWrap(true);
        commandText.setWrapStyleWord(true);

        display = new TextDisplay(this.configFrame);
        display.addMouseListener(this);
	display.setBackgroundColor(configFrame.getBackgroundColor());

        ticPanel.setLayout(new GridLayout(6, 1));
        mainPanel.setLayout(new BorderLayout()); //10, 10));
        
        mainPanel.add("Center", display);
        mainPanel.add("South", commandText);

        pasteMenu = menu.add("Paste");
        pasteMenu.addActionListener(this);
        pasteToMenu = menu.add("Paste To");
        pasteToMenu.addActionListener(this);
        pasteBoxMenu = menu.add("Paste Box");
        pasteBoxMenu.addActionListener(this);
        menu.addSeparator();

	chatMenuItem = menu.add("Chat");
	chatMenuItem.addActionListener(this);

	searchMenuItem = menu.add("Google");
	searchMenuItem.addActionListener(this);

	dictionaryMenuItem = menu.add("Dictionary");
	dictionaryMenuItem.addActionListener(this);

	menu.addSeparator();

	reconnectMenuItem = menu.add("Reconnect");
	reconnectMenuItem.addActionListener(this);

	menu.addSeparator();
        configMenu = menu.add("Configure");
        configMenu.addActionListener(this);
        fontMenu = menu.add("Font");
        fontMenu.addActionListener(this);
        autoScrollMenuItem = new JCheckBoxMenuItem("Auto scroll");
        autoScrollMenuItem.setState(true);
        autoScrollMenuItem.addActionListener(this);
        menu.add(autoScrollMenuItem);
	menu.addSeparator();
        menu.add("Dismiss");

        connectLabel = new JLabel("Attempting to connect...");
        ticPanel.add(connectLabel);
        ticPanel.add(progressBar);

        this.add("TIC", ticPanel);
        this.add("MAIN", mainPanel);

        this.addMouseListener(this);
    }

    public void
    keyPressed(
        KeyEvent                      ke)
    {
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
        if(ke.getKeyChar() == '\n')
        {
            mudConnection.sendLine(commandText.getText());
            commandText.setText("");
        }
    }

    public void
    setCommandFocus()
    {
        commandText.grabFocus();
    }

    public void
    popupMenuCanceled(
        PopupMenuEvent                   e) 
    {
    }

    public void
    popupMenuWillBecomeInvisible(
        PopupMenuEvent                   e) 
    {
    }

    public void
    popupMenuWillBecomeVisible(
        PopupMenuEvent                   e) 
    {
    }

    public void
    mouseClicked(MouseEvent e) 
    {
	if ( ((e.getModifiers() & InputEvent.BUTTON2_MASK) != 0) ||
	     ((e.getModifiers() & InputEvent.BUTTON3_MASK) != 0) ) {
	    
	    JComponent src = (JComponent)e.getSource();
	    Transferable tempT = clipboard.getContents(this);
	    
	    if( (tempT != null &&
		 tempT.isDataFlavorSupported (DataFlavor.stringFlavor))) {
		pasteMenu.setEnabled(true);
		pasteToMenu.setEnabled(true);
	    } else {
		pasteMenu.setEnabled(false);
		pasteToMenu.setEnabled(false);
	    }
	    
	    menu.show(src, e.getX(), e.getY());
	} else {
	    setCommandFocus();
	}
	
    }

    public void
    mouseEntered(MouseEvent e) 
    {
    }

    public void
    mouseExited(MouseEvent e) 
    {
    }

    public void
    mousePressed(MouseEvent event) 
    {
    }

    public void
    mouseReleased(MouseEvent e) 
    {
    }

    public void
    pasteAction(
        String                              text)
    {

    }

    public void
    actionPerformed(
        ActionEvent                         e) 
    {
        String                              command;

        if(e.getSource() == pasteMenu || e.getSource() == pasteToMenu)
        {
            try
            {
                Transferable          tempT = clipboard.getContents(this);
                String                tempS = null;

                if(tempT != null &&
                   tempT.isDataFlavorSupported (DataFlavor.stringFlavor))
                {
                    tempS = (String)tempT.getTransferData(
                                         DataFlavor.stringFlavor);
                }

                if(tempS != null)
                {
                    if(e.getSource() == pasteMenu)
                    {
                        command = "@paste \n";
                    }
                    else
                    {
			String text = display.getText();

			String who = (String)JOptionPane.showInputDialog(this, "To Who?", "Paste To",
									 JOptionPane.QUESTION_MESSAGE,
									 null,
									 null,
									 text);

			// check if user canceled
			if (who == null) {
			    return;
			}

			command =  "@pasteto " + who + "\n ";
                    }    
                    command = command.concat(tempS).concat("\n.\n");

                    mudConnection.sendLine(command);
                }
            } 
            catch (Exception ex2)
            {
                ex2.printStackTrace();
            }
        }
        else if(e.getSource() == pasteBoxMenu)
        {
	    if (pd == null) {
		pd = new PasteDialog(frame, mudConnection);
	    }
	    pd.setVisible(true);
        }
        else if(e.getSource() == configMenu)
        {
	    UITools.center(frame, configFrame);
            configFrame.setVisible(true);
        }
        else if(e.getSource() == fontMenu)
        {
            Font defaultFont = JFontChooser.showDialog(frame);
            display.setFont(defaultFont);
        }
	else if(e.getSource() == reconnectMenuItem) 
	{
	    try {
		mudConnection.close();
		display.clear();
	    } catch (Exception ex) {
		// this might thrown an exception but
		// it can be ignored
	    }

	    try {
		mudConnection.connect(configFrame.getUsername(),
				      configFrame.getPassword());
	    } catch (Exception ex) {
		JOptionPane.showMessageDialog(this, 
					      "Failed to reconnect : " + ex.getMessage(),
					      "Reconnect error",
					      JOptionPane.ERROR_MESSAGE);
	    }
	}
	else if (e.getSource() == chatMenuItem) {
	    String peer = display.getText();
	    if (peer != null) {
		String self = configFrame.getUsername();
		MudChatWindow chat = new MudChatWindow(this.mudConnection,
						       self,
						       peer,
						       this.configFrame);
		chat.setSize(400, 300);
		UITools.center(configFrame.getOwner(), chat);
		chat.setVisible(true);
	    }
	}
        else if (e.getSource() == autoScrollMenuItem) {
            display.setAutoScroll(autoScrollMenuItem.getState());
        }
        else if (e.getSource() == searchMenuItem) {
            doSearch(display.getSelectedText(), 
                     "http://www.google.com/search?q=");
        }
        else if (e.getSource() == dictionaryMenuItem) {
            doSearch(display.getSelectedText(),
                     "http://dictionary.reference.com/search?q=");
        }
    }
    
    private void doSearch(String text, String query) {
        if (text == null) {
            return;
        }
        text = text.trim();
        if (text.length() == 0) {
            return;
        }
        text = query + URLEncoder.encode(text);

    	String cmdstr = this.configFrame.getUrlViewerCmd();

	if (cmdstr == null || cmdstr.length() == 0) {
	    // FIXME: display error?
	    System.err.println("Url viewer cmd not defined");
	    return;
	}
        
	// on windows: rundll32 url.dll,FileProtocolHandler {0}
	String cmd = MessageFormat.format(cmdstr, new Object [] {text});
	
	try {
	    Process child = Runtime.getRuntime().exec(cmd);
	} catch (Exception e) {
	    System.err.println("Error starting external viewer: " +
			       e.getMessage());
	    e.printStackTrace();
	}
    }

    public void
    updateLaF()
    {
        try
        {
            SwingUtilities.updateComponentTreeUI(this);
            SwingUtilities.updateComponentTreeUI(configFrame);
        }
        catch(Exception ex)
        {
        }
    }


    public void
    windowOpened(
        WindowEvent                        we)
    {
    }
    public void
    windowClosed(
        WindowEvent                        we)
    {
    }
    public void
    windowClosing(
        WindowEvent                        we)
    {
    }
    public void
    windowDeactivated(
        WindowEvent                        we)
    {
        menu.setVisible(false);
    }
    public void
    windowActivated(
        WindowEvent                        we)
    {
        setCommandFocus();
    }
    public void
    windowIconified(
        WindowEvent                        we)
    {
    }
    public void
    windowDeiconified(
        WindowEvent                        we)
    {
    }
}


class
FakePasteArea
    extends TextField
    implements TextListener,
               PopupMenuListener,
               MouseListener
{
    protected MudConnection                     mc;
    protected boolean                           enabled = true;

    public
    FakePasteArea(
        MudConnection                           mc)
    {
        super();

//        setEditable(false);
//        this.addTextListener(this);

        this.mc = mc;
        this.addMouseListener(this);
    }


    public void 
    enableEvents(
        boolean                                 b)
    {
        enabled = b;
    }

    public void
    textValueChanged(
        TextEvent                               e) 
    {
    }
  
    public void
    popupMenuCanceled(
        PopupMenuEvent                              e) 
    {
    }
    public void
    popupMenuWillBecomeInvisible(
        PopupMenuEvent                              e) 
    {
    }

    public void
    mouseClicked(
        MouseEvent                                  e)
    {
    }


    public void
    popupMenuWillBecomeVisible(
        PopupMenuEvent                              e) 
    {
    }
    public void
    mouseExited(MouseEvent e)
    {
    }
    public void
    mouseEntered(MouseEvent e)
    {
    }


    public void
    mousePressed(MouseEvent e)
    {
        this.setEditable(true);
    }

    public void
    mouseReleased(MouseEvent e)
    {
        this.setEditable(false);
    }
  
}

class
PasteDialog
    extends Dialog
    implements ActionListener,
               ItemListener
{
    protected Button                    okButton;
    protected Button                    cancelButton;
    protected TextArea                  pasteText;
    protected MudConnection             mc;
    protected Checkbox                  toCheckbox;
    protected TextField                 nameText;
   
    public
    PasteDialog(
        MudFrame                        frame,
        MudConnection                   mc)
    {
        super(frame, "Paste", false);

        Panel                           tempP;
        Panel                           tempP2;
        Panel                           tempP3;

       
        this.mc = mc;
        this.okButton = new Button("Ok");
        this.okButton.addActionListener(this);
        this.cancelButton = new Button("Cancel");
        this.cancelButton.addActionListener(this);
        this.toCheckbox = new Checkbox("To:");
        this.toCheckbox.addItemListener(this);
        this.nameText = new TextField();

        this.pasteText = new TextArea();

        this.setLayout(new BorderLayout(5, 5));
        this.add("Center", pasteText);
      
        pasteText.setBackground(Color.black);
        pasteText.setForeground(Color.white);
 
  
        tempP = new Panel();
        tempP.add(okButton);
        tempP.add(cancelButton);
        tempP2 = new Panel();
        tempP2.setLayout(new BorderLayout(5, 5));
        tempP2.add("West", toCheckbox);
        tempP2.add("Center", nameText);
        nameText.setEnabled(false);
        tempP3 = new Panel();
        tempP3.setLayout(new GridLayout(2, 1));
        tempP3.add(tempP2);
        tempP3.add(tempP);
 
        this.setSize(320, 240);
        this.add("South", tempP3);
    }

    protected void
    sendPaste()
    {

    }

    public void
    actionPerformed(
        ActionEvent                         e)
    {
        Object                              src;
        String                              command;

        src = e.getSource();

        if(src == okButton)
        {
            String                          tempS;

            tempS = pasteText.getText();
            try
            {
                if(tempS != null)
                {
                    if(!toCheckbox.getState())
                    {
                        command = "@paste \n";
                    }
                    else
                    {
                        command = "@pasteto ";

                        String who = nameText.getText();
                        command = command.concat(who).concat("\n ");
                    }    
          
                    command = command.concat(tempS).concat("\n.\n");

                    mc.sendLine(command);
                }
            }
            catch(Exception e2) 
            {

            }
        }
        this.setVisible(false);
    }   

    public void
    itemStateChanged(
        ItemEvent                            e)
    {
        if(!toCheckbox.getState())
        {
            nameText.setEnabled(false);
        }
        else
        {
            nameText.setEnabled(true);
        }
    }   
}
