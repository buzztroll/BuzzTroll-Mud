package org.buzztroll.mud.tivo;

import java.awt.*;

import java.net.*;
import java.io.*;

import java.util.*;

import com.tivo.hme.sdk.*;

import org.buzztroll.mud.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SingleMudView extends MudView {

    View contentView;
    private View textView;

    public SingleMudView(Mud mud) {
        super(mud);

        View root = this.mud.getRoot();
        contentView = new View(root,
                               Application.SAFE_ACTION_H + 30,
                               Application.SAFE_ACTION_V + 30,
                               root.getWidth() - (Application.SAFE_ACTION_H+30) * 2,
                               root.getHeight() - (Application.SAFE_ACTION_V+30) * 2);
        
        
        int shadow = 8;

        View back = new View(contentView,
                             shadow, shadow, 
                             contentView.getWidth(),
                             contentView.getHeight());
        
        Color c = intensify(Color.darkGray, -40);

        back.setResource(c);


        View front = new View(contentView,
                              0, 0, 
                              contentView.getWidth()-shadow, 
                              contentView.getHeight()-shadow);
        
        front.setResource(Color.darkGray);

        this.textView = new View(front,
                                 0, 0, 
                                 front.getWidth(), front.getHeight());

        contentView.setVisible(false);
    }

    public void clearScreen() {
    }
    
    public void repaintScreen() {
        int fontSize = 24;

        this.mud.getRoot().setPainting(false);

        DisplayMessage msg = 
            (DisplayMessage)this.mud.list.get(this.mud.selectionIndex);

        this.textView.clearResource();
        this.textView.setResource(this.mud.createText("default-" + fontSize + ".font",
                                        msg.getColor(),
                                        msg.getString()),
                                  Application.RSRC_VALIGN_TOP |
                                  Application.RSRC_TEXT_WRAP |
                                  Application.RSRC_HALIGN_LEFT);

        this.mud.getRoot().setPainting(true);
        this.mud.flush();
    }

    public void switchScreen() {
        View root = this.mud.getRoot();
        this.contentView.setVisible(true);
        repaintScreen();
    }

    public boolean handleKeyPress(int code, long rawcode) {
        switch (code) {
        case Application.KEY_UP:
            if (this.mud.selectionIndex > 0) {
                this.mud.selectionIndex--;
                repaintScreen();
            }
            return true;

        case Application.KEY_DOWN:
            if (this.mud.selectionIndex+1 < this.mud.list.size()) {
                this.mud.selectionIndex++;
                repaintScreen();
            }
            return true;

        case Application.KEY_LEFT:
            this.mud.mudView = this.mud.multipleMudView;
            this.mud.mudView.switchScreen();
            return true;
        }
        return this.mud.handleKeyPressSub(code, rawcode);
    }
    

    public static Color
    intensify(
        Color                           inColor,
        int                             degree)
    {
        float hsb[] = new float[3];

        Color.RGBtoHSB(inColor.getRed(), inColor.getGreen(),
                        inColor.getBlue(), hsb);

        if(degree < 0)
        {
                // get the percetage of darker
            float val = 1.0f -
                (float)Math.abs(degree) / 100.0f;
            // make b a lower number
            // handle s
            hsb[2] = hsb[2] * val;
        }
        else
        {
            float val =  1.0f - (float)degree / 100.0f;
            // handle b
            hsb[1] = hsb[1] * val;
        }

        Color c = Color.getHSBColor(hsb[0], hsb[1], hsb[2]);

        return c;
    }
}
