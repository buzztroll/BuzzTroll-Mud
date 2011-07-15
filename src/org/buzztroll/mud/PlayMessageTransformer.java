package org.buzztroll.mud;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import javax.swing.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.sound.sampled.*;

class PlayMessageTransformer extends MessageTransformer {

    protected String matchString;
    protected int position;

    protected String url;
    protected Clip clip;

    protected static Object sem = new Object();
    protected static PlayQueue queue;

    public PlayMessageTransformer() {
    }
    
    public PlayMessageTransformer(String match,
				  int pos,
				  String url) {
        this.position = pos;
        this.matchString = match;
	this.url = url;
    }
    
    public DisplayMessage transform(DisplayMessage msg) {
        if(stringMatch(this.position, msg.getString(), this.matchString)) {
	    try {
		if (clip == null) {
		    AudioInputStream stream = AudioSystem.getAudioInputStream(new URL(url));
	
		    AudioFormat format = stream.getFormat();

		    // Create the clip
		    DataLine.Info info = 
			new DataLine.Info(Clip.class, 
					  stream.getFormat(), 
					  ((int)stream.getFrameLength()*format.getFrameSize()));
		    clip = (Clip) AudioSystem.getLine(info);
		
		    clip.addLineListener(new LineListener() {
			    public void update(LineEvent event) {
				LineEvent.Type type = event.getType();
				if (type == LineEvent.Type.STOP) {
				    synchronized(sem) {
					sem.notify();
				    }
				}
			    }
			});

		    // This method does not return until the audio file is completely loaded
		    clip.open(stream);
		}

	    } catch (Exception e) {
		e.printStackTrace();
		return msg;
	    }

	    if (queue == null) {
		queue = new PlayQueue();
		queue.start();
	    }

	    queue.queue(clip);

	    /*
	    try {
		File sf = new File(
	    try {
		AudioClip au = Applet.newAudioClip(new URL(url));
		au.play();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	    */
        }
        return msg;
    }
    
    public JComponent getRendererComponent() {
	StringBuffer buf = new StringBuffer();
        buf.append("Play '").append(this.url);
	buf.append("' sound when message ");
	
        if(this.position == CONTAINS) {
            buf.append("contains ");
        } else if(this.position == ENDS_WITH) {
            buf.append("ends with ");
        } else if(position == STARTS_WITH) {
            buf.append("starts with ");
        }

	buf.append("'").append(this.matchString).append("'");
	
        JLabel jl = new JLabel(buf.toString());
	
        return jl;
    }

    public Element createDoc(Document doc)
        throws Exception {

        Element base = doc.createElement("play");
        base.setAttribute("match", this.matchString);
        base.setAttribute("position", new Integer(position).toString());
        base.setAttribute("url", this.url);
        return base;
    }

    public void parse(Element base) {

        this.matchString = base.getAttribute("match");
	this.url = base.getAttribute("url");
        this.position = new Integer(base.getAttribute("position")).intValue();

        System.out.println(this.matchString + ":" + this.url +":"+this.position);
    }

    class PlayQueue extends Queue {

	public void execute(Object tmp) {
	    Clip clip = (Clip)tmp;
	    clip.setFramePosition(0);
	    clip.start();
	    synchronized(sem) {
		try {
		    sem.wait();
		} catch (Exception e) {
		}
	    }
	}
	
    }
}
