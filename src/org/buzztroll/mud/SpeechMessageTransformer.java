package org.buzztroll.mud;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.audio.JavaClipAudioPlayer;
import com.sun.speech.freetts.en.us.CMULexicon;

import javax.swing.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

class SpeechMessageTransformer extends MessageTransformer {

    protected String matchString;
    protected int position;

    protected static Voice voice;
    protected static SpeechQueue queue;

    public SpeechMessageTransformer() {
	init();
    }
    
    public SpeechMessageTransformer(String match,
				    int pos) {
        this.position = pos;
        this.matchString = match;
	init();
    }

    private static synchronized void init() {

	if (voice != null) {
	    return;
	}

	String voiceClassName = "com.sun.speech.freetts.en.us.CMUDiphoneVoice";
	
	try {
	    Class voiceClass = Class.forName(voiceClassName);
	    
	    // instantiate the Voice
	    voice = (Voice) voiceClass.newInstance();

	    voice.getFeatures().setObject(Voice.DATABASE_NAME, 
					  "cmu_kal/diphone_units16.bin");

	    // sets the lexicon to CMU lexicon
	    voice.setLexicon(new CMULexicon());

	    // sets the AudioPlayer to the Java clip player
	    voice.setAudioPlayer(new JavaClipAudioPlayer());
	    
	    // loads the Voice, which mainly is loading the lexicon
	    voice.load();
	} catch (Exception e) {
	    System.err.println("Failed to initialize speech engine. Speech disabled.");
	    e.printStackTrace();
	}

    }

    public DisplayMessage transform(DisplayMessage msg) {
        if(voice != null && stringMatch(this.position, 
					msg.getString(), 
					this.matchString)) {
	    if (queue == null) {
		queue = new SpeechQueue();
		queue.start();
	    }
	    queue.queue(msg.getString());
        }
        return msg;
    }
    
    public JComponent getRendererComponent() {
	StringBuffer buf = new StringBuffer();
        buf.append("Synthesize speech when message ");
	
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

        Element base = doc.createElement("speak");
        base.setAttribute("match", this.matchString);
        base.setAttribute("position", new Integer(position).toString());
        return base;
    }

    public void parse(Element base) {

        this.matchString = base.getAttribute("match");
        this.position = new Integer(base.getAttribute("position")).intValue();

        System.out.println(this.matchString + ":" + this.position);
    }

    class SpeechQueue extends Queue {

	public void execute(Object tmp) {
	    voice.speak((String)tmp);
	}
	
    }
    
}
