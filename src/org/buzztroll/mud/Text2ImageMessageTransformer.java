package org.buzztroll.mud;

import java.net.URL;
import javax.swing.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

class Text2ImageMessageTransformer extends MessageTransformer {

    private int position;
    private String string;
    private String imageFile;
    private Icon image;
    
    public Text2ImageMessageTransformer() {
    }
    
    public Text2ImageMessageTransformer(String match,
                                        int pos,
                                        String url) {
        this.string = match;
        this.position = pos;
        this.imageFile = url;
        this.image = new ImageIcon(this.imageFile);
    }

    public DisplayMessage transform(DisplayMessage msg) {
        String text = msg.getString();
        if (this.position == STARTS_WITH && text.startsWith(this.string)) {
            String before = "";
            String after = text.substring(this.string.length());
            return new DisplayMessageWithIcon(before, 
                                              this.image,
                                              after,
                                              text, 
                                              msg.getColor(),
                                              msg.getFont());
        } else if (this.position == ENDS_WITH && text.endsWith(this.string)) {
            String before = text.substring(0, 
                                           text.length()-this.string.length());
            String after = "";
            return new DisplayMessageWithIcon(before, 
                                              this.image,
                                              after,
                                              text, 
                                              msg.getColor(),
                                              msg.getFont());
        } else if (this.position == CONTAINS) {
            int pos = text.indexOf(this.string);
            if (pos != -1) {
                String before = text.substring(0, pos);
                String after = text.substring(pos+this.string.length());
                return new DisplayMessageWithIcon(before, 
                                                  this.image,
                                                  after,
                                                  text, 
                                                  msg.getColor(),
                                                  msg.getFont());
            }
        }
        return msg;
    }
    
    public JComponent getRendererComponent() {
	StringBuffer buf = new StringBuffer();
        buf.append("Replace text with '").append(this.imageFile);
        buf.append("' image when message ");
        
        if(this.position == CONTAINS) {
            buf.append("contains ");
        } else if(this.position == ENDS_WITH) {
            buf.append("ends with ");
        } else if(position == STARTS_WITH) {
            buf.append("starts with ");
        }
        
        buf.append("'").append(this.string).append("'");
	
        JLabel jl = new JLabel(buf.toString());
	
        return jl;
    }

    public Element createDoc(Document doc)
        throws Exception {
        Element base = doc.createElement("text2image");
        base.setAttribute("match", this.string);
        base.setAttribute("position", String.valueOf(this.position));
        base.setAttribute("image", this.imageFile);
        return base;
    }
    
    public void parse(Element base) {
	this.string = base.getAttribute("match");
        this.position = Integer.parseInt(base.getAttribute("position"));
        this.imageFile = base.getAttribute("image");
        this.image = new ImageIcon(this.imageFile);
    }

}
