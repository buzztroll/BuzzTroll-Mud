package org.buzztroll.mud;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;
import java.util.*;
import java.text.*;

public class 
TextDisplay
    extends DisplayInterface
    implements ComponentListener,
	       MouseMotionListener,
	       MouseListener,
               AdjustmentListener
{
    protected TextCanvas                    textPane;
    protected DefaultStyledDocument         doc;
    protected StyleSheet                    sc;
    protected JScrollPane                   scrollPane;
    protected Font                          defaultFont = null;
    protected Color                         backgroundColor = Color.black;
    protected Point                         lastMousePosition = null;
    private String                          ls = null;
    protected boolean                       autoScroll = true;

    private ConfigFrame configFrame;

    public 
    TextDisplay(ConfigFrame configFrame)
    {
	this.configFrame = configFrame;
        
	ls = System.getProperty("line.separator");

        MutableAttributeSet                attr;

        attr = new SimpleAttributeSet();
        StyleConstants.ColorConstants.setBackground(attr, backgroundColor);
        StyleConstants.setBackground(attr, backgroundColor);
        sc = new StyleSheet();
        doc = new DefaultStyledDocument(sc);

        this.setBackground(Color.black);     
        this.setLayout(new BorderLayout());
        textPane = new TextCanvas(doc);
        textPane.setCharacterAttributes(attr, true); 
        textPane.setOpaque(true);
        textPane.setEditable(false);
        textPane.setBackground(backgroundColor);
        textPane.setForeground(backgroundColor);

        this.setBackground(Color.black); 
        this.setOpaque(false);
        
        this.addComponentListener(this);      
        textPane.addComponentListener(this);      
        scrollPane = new JScrollPane(textPane);
        scrollPane.setHorizontalScrollBarPolicy(
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.black);
        scrollPane.setOpaque(false);
        scrollPane.getVerticalScrollBar().addAdjustmentListener(this);

        int fontSize = StyleConstants.FontConstants.getFontSize(attr);
        String fontName = StyleConstants.FontConstants.getFontFamily(attr);
        defaultFont = new Font(fontName, Font.PLAIN, fontSize);

        this.add("Center", scrollPane);
	textPane.addMouseMotionListener(this);
	textPane.addMouseListener(this);
        bottomOut();
    }

    public void setAutoScroll(boolean autoScroll) {
        this.autoScroll = autoScroll;
    }

    public void 
    setBackgroundColor(Color color) 
    {
	this.backgroundColor = color;
	textPane.setBackground(backgroundColor);
        textPane.setForeground(backgroundColor);
    }

    public void 
    addMouseListener(
        MouseListener                      l) 
    {
        super.addMouseListener(l);
        textPane.addMouseListener(l);
    }

    private void appendMessageWithImage(DisplayMessageWithIcon dm) {
        MutableAttributeSet attr;

        attr = getAttributeSet(dm);
        
        try
        {
            int start = doc.getLength();
            if (start == 0) {
                doc.insertString(start, dm.getBeforeMessage(), attr);
            } else {
		doc.insertString(start, ls + dm.getBeforeMessage(), attr);
	    }
        } 
        catch(BadLocationException e)
        {
            System.err.println(e);
        } 

        attr = getAttributeSet(dm);
        StyleConstants.setIcon(attr, dm.getIcon());

        try
        {
            doc.insertString(doc.getLength(), " ", attr);
        } 
        catch(BadLocationException e)
        {
            System.err.println(e);
        } 

        attr = getAttributeSet(dm);

        try
        {
            doc.insertString(doc.getLength(), dm.getAfterMessage(), attr);
        } 
        catch(BadLocationException e)
        {
            System.err.println(e);
        } 
    }

    private void appendMessage(DisplayMessage dm) {
        MutableAttributeSet attr = getAttributeSet(dm);
        int start = doc.getLength();
        String message = dm.getString();

        try
        {
	    if (start == 0) {
		doc.insertString(start, message, attr);
	    } else {
		doc.insertString(start, ls + message, attr);
	    }
        } 
        catch(BadLocationException e)
        {
            System.err.println(e);
        } 
    }

    public void 
    addMessage( 
        DisplayMessage                      dm)
    {
        String message = dm.getString();

        if(message == null || message.equals(""))
        {
            return;
        }

        if (dm instanceof DisplayMessageWithIcon) {
            appendMessageWithImage((DisplayMessageWithIcon)dm);
        } else {
            appendMessage(dm);
        }

        repaint();
    }

    private SimpleAttributeSet getAttributeSet(DisplayMessage dm) {
        Color color = dm.getColor();

        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.ColorConstants.setForeground(attr, color);
        StyleConstants.ParagraphConstants.setLineSpacing(attr, 5.0f);

        Font f = dm.getFont();
        if(f == null)
        {
            f = this.defaultFont;
        }

        int fontSize = f.getSize();
        String fontName = f.getFamily();

        StyleConstants.FontConstants.setFontFamily(attr, fontName);
        StyleConstants.FontConstants.setFontSize(attr, fontSize);
        StyleConstants.FontConstants.setBold(attr, f.isBold());
        StyleConstants.FontConstants.setItalic(attr, f.isItalic());

        return attr;
    }

    public void 
    adjustmentValueChanged(
        AdjustmentEvent                     e)
    {
    }
  
    public void
    paintComponent(
        Graphics                             g)
    {
        super.paintComponent(g);

        if (this.autoScroll) {
            bottomOut();
        }
    }

    public void
    setFont(
        Font                                f)
    {
        this.defaultFont = f;
    }

    public void clear() {
	textPane.setText("");
    }

    public Font
    getFont()
    {
        return this.defaultFont;
    }

    protected void
    bottomOut()
    {
        JScrollBar jsb = scrollPane.getVerticalScrollBar();
        jsb.setValue(jsb.getMaximum());
    }

    public void
    componentHidden(ComponentEvent e) 
    {
    }
    public void
    componentMoved(ComponentEvent e) 
    {
    }
    public void
    componentResized(ComponentEvent e) 
    {
        if (this.autoScroll) {
            bottomOut();
        }
    }
    public void
    componentShown(ComponentEvent e) 
    {
        if (this.autoScroll) {
            bottomOut();
        }
    }


    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent event) {}
    public void mouseReleased(MouseEvent e) {}

    public void mouseClicked(MouseEvent e) {
        if ((e.getModifiers() &  MouseEvent.BUTTON3_MASK) == 0) {
	    checkLinkClick(e);
	}
    }
    
    private void checkLinkClick(MouseEvent event) {
	String url = getText(event.getPoint());
	
	if (url == null || url.indexOf("://") == -1) {
	    return;
	}

	if (url.charAt(0) == '"' ||
	    url.charAt(0) == '\'') {
	    url = url.substring(1);
	}

	int len =  url.length();
	if (len > 0 && (url.charAt(len-1) == '"' ||
			url.charAt(len-1) == '\'')) {
	    url = url.substring(0, len-1);
	}

	String cmdstr = this.configFrame.getUrlViewerCmd();

	if (cmdstr == null || cmdstr.length() == 0) {
	    // FIXME: display error?
	    System.err.println("Url viewer cmd not defined");
	    return;
	}

	// on windows: rundll32 url.dll,FileProtocolHandler {0}
	String cmd = MessageFormat.format(cmdstr, new Object [] {url});
	
	try {
	    Process child = Runtime.getRuntime().exec(cmd);
	} catch (Exception e) {
	    System.err.println("Error starting external viewer: " +
			       e.getMessage());
	    e.printStackTrace();
	}
    }
    
    public String getSelectedText() {
        return this.textPane.getSelectedText();
    }

    public String getText() {
	return getText(lastMousePosition);
    }

    public String getText(Point p) 
    {
	int pos = textPane.viewToModel(p);
	
	try 
        {
	    if (Character.isWhitespace(doc.getText(pos, 1).charAt(0))) 
	    {
		return null;
	    }
	} 
        catch (Exception ex) 
        {
	    return null;
	}
	    
	// we should be over some text definitely now
	
        Element elem = doc.getParagraphElement(pos);
        int start = elem.getStartOffset();
        int end = elem.getEndOffset();
	    
        int parOff = pos-start;
	
        String line = null;
        try {
            line = doc.getText(start, end-start);
        } catch (Exception ee) {
        return null;
        }
	    
        int i = parOff;
        while( i < line.length() && !Character.isWhitespace(line.charAt(i)) ) {
            i++;
        }
        end = i;
	
        i = parOff;
        while( i > 0 && line.charAt(i-1) != ' ') {
             i--;
        }
        start = i;
	
        return line.substring(start, end);
    }

    class 
    TextCanvas
    extends JTextPane
    {
	    
	public 
	TextCanvas(
		   StyledDocument                    doc)
	{
	    super();
	    this.setDocument(doc);
	    this.setOpaque(true);
	    this.setBackground(backgroundColor);
	}
	    
    }

    // highlighting code

    public void mouseMoved(MouseEvent event) {
	lastMousePosition = event.getPoint();
	int pos = textPane.viewToModel(lastMousePosition);
	
	try {
	    if (Character.isWhitespace(doc.getText(pos, 1).charAt(0))) {
		if (highlight != null) {
		    MutableAttributeSet attr = highlight.getAttribute();
		    StyleConstants.setUnderline(attr, false);
		    doc.setCharacterAttributes(highlight.getPosition(), highlight.getLength(), attr, false);
		    highlight = null;
		}
		return;
	    }
	} catch (Exception ex) {
	    return;
	}
	    
	// we should be over some text definitely now

	if (highlight != null) {
	    if (highlight.isHighlighted(pos)) {
		return;
	    } else {
		MutableAttributeSet attr = highlight.getAttribute();
		StyleConstants.setUnderline(attr, false);
		doc.setCharacterAttributes(highlight.getPosition(), highlight.getLength(), attr, false);
		highlight = null;
	    }
	}
	
	Element elem = doc.getParagraphElement(pos);
	int start = elem.getStartOffset();
	int end = elem.getEndOffset();
	
	String line = null;
	try {
	    line = doc.getText(start, end-start);
	} catch (Exception ee) {
	    return;
	}
	
	int parOff = pos-start;

	int i = parOff;
        while( i < line.length() && !Character.isWhitespace(line.charAt(i)) ) {
            i++;
        }
        end = i;
	
        i = parOff;
        while( i > 0 && line.charAt(i-1) != ' ') {
	    i--;
        }
        start = i;
	
	line = line.substring(start, end);

	// not an url
	if (line.indexOf("://") == -1) {
	    return;
	}

	if (highlight == null) {
	    highlight = new Highlight();
	}

	MutableAttributeSet attr = new SimpleAttributeSet(elem.getAttributes());
	
	highlight.setPosition(pos - (parOff -start));
	highlight.setLength(end-start);
	highlight.setAttribute(attr);
	
	StyleConstants.setUnderline(attr, true);
	doc.setCharacterAttributes(pos - (parOff -start), end-start, attr, false);
    }

    public void mouseDragged(MouseEvent event) {}

    Highlight highlight;

    class Highlight {
	int pos;
	int len;
	MutableAttributeSet attr;

	public void setPosition(int pos) {
	    this.pos = pos;
	}

	public void setLength(int len) {
	    this.len = len;
	}
	
	public int getLength() {
	    return this.len;
	}

	public int getPosition() {
	    return this.pos;
	}

	public void setAttribute(MutableAttributeSet attr) {
	    this.attr = attr;
	}

	public MutableAttributeSet getAttribute() {
	    return this.attr;
	}

	public boolean isHighlighted(int off) {
	    return (off > pos && off < pos + len);
	}

    }

}
