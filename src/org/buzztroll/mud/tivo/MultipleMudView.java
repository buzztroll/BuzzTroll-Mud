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

public class MultipleMudView extends MudView {

    private boolean autoUpdate = true;
    private int startIndex = 0;

    private View contentView;
    private View views[] = new View[16];
    private int fontSize;

    public MultipleMudView(Mud mud) {
        super(mud);

        View root = this.mud.getRoot();
        contentView = new View(root,
                                Application.SAFE_ACTION_H + 10,
                                Application.SAFE_ACTION_V + 10,
                                root.getWidth() - (Application.SAFE_ACTION_H+5) * 2,
                                root.getHeight() - (Application.SAFE_ACTION_V+5) * 2);

        contentView.setResource(this.mud.preferencesPanel.getBackgroundColor());

        int panelHeight = contentView.getHeight() / views.length;

        this.fontSize = panelHeight - 3;
        for (int i = 0; i < views.length; ++i) {
            View panel = new View(contentView, 0, i * panelHeight,
                                  contentView.getWidth(), panelHeight);
            views[i] = new View(panel, 0, 0,
                                1024, panel.getHeight());
        }
    }

    public void reset() {
        this.mud.selectionIndex = -1;
        this.autoUpdate = true;
    }

    public void clearScreen() {
        if (!this.mud.list.isEmpty()) {
            this.mud.getRoot().setPainting(false);
            for (int i=0;i<this.views.length;i++) {
                views[i].clearResource();
            }
            this.mud.getRoot().setPainting(true);
            this.mud.flush();
            this.mud.list.clear();
        }
    }

    public void repaintScreen() {
        this.mud.getRoot().setPainting(false);
        
        if (this.autoUpdate) {
            if (this.mud.list.size() <= this.views.length) {
                this.startIndex = 0;
            } else {
                this.startIndex = this.mud.list.size() - this.views.length;
            }
        }
        
        for (int i=this.startIndex,n=0; 
             i<this.mud.list.size() && n<this.views.length; 
             i++,n++) {
            DisplayMessage msg = (DisplayMessage)this.mud.list.get(i);
            View view = views[n];
            
            if (i == this.mud.selectionIndex) {
                view.getParent().setResource(Color.blue);
            } else {
                view.getParent().setResource(Color.black);
            }

            String text = msg.getString();
            if (text.length() > 80) {
                text = text.substring(0, 80);
            }

            view.clearResource();
            view.setResource(this.mud.createText("default-" + this.fontSize + ".font",
                                                 msg.getColor(),
                                                 text),
                             Application.RSRC_VALIGN_TOP |
                             Application.RSRC_HALIGN_LEFT);
        }
        this.mud.getRoot().setPainting(true);
        this.mud.flush();
    }

    public void switchScreen() {
        View root = this.mud.getRoot();
        for (int i = 0;i<root.getChildCount();i++) {
            if (contentView != root.getChild(i)) {
                root.getChild(i).setVisible(false);
            }
        }
        contentView.setVisible(true);
        this.startIndex = (this.mud.selectionIndex / this.views.length) * this.views.length;
        if (this.startIndex+this.views.length > this.mud.list.size()) {
            this.startIndex = this.mud.list.size() - this.views.length;
        }
        repaintScreen();
    }

    public boolean handleKeyPress(int code, long rawcode) {
        switch (code) {
        case Application.KEY_UP:
            System.out.println("up: " + 
                               this.startIndex + " " + this.mud.selectionIndex);
            this.autoUpdate = false;
            if (this.mud.selectionIndex == -1) {
                this.mud.selectionIndex = 
                    this.startIndex + this.views.length - 1;
                repaintScreen();
            } else if (this.mud.selectionIndex > 0) {
                this.mud.selectionIndex--;
                if (this.mud.selectionIndex < this.startIndex) {
                    this.startIndex -= this.views.length;
                    if (this.startIndex < 0) {
                        this.startIndex = 0;
                    }
                }
                repaintScreen();
            }
            return true;

        case Application.KEY_CHANNELUP:
            System.out.println("channel up: " +
                               this.startIndex + " " + this.mud.selectionIndex);
            if (this.startIndex > 0) {
                this.autoUpdate = false;
                if (this.mud.selectionIndex == -1) {
                    this.mud.selectionIndex =
                        this.startIndex + this.views.length - 1;
                } else {
                    this.startIndex -= this.views.length;
                    this.mud.selectionIndex -= this.views.length;
                }

                if (this.startIndex < 0) {
                    this.mud.selectionIndex -= this.startIndex;
                    this.startIndex = 0;
                }
                repaintScreen();
            } else if (this.startIndex == 0 && this.mud.selectionIndex > 0) {
                this.mud.selectionIndex = 0;
                repaintScreen();
            }
            return true;

        case Application.KEY_DOWN:
            System.out.println("down: " + 
                               this.startIndex + " " + this.mud.selectionIndex);
            if (this.mud.selectionIndex == -1) {
                // do nothing
            } else if (this.mud.selectionIndex+1 < this.mud.list.size()) {
                this.mud.selectionIndex++;
                if (this.mud.selectionIndex >= this.startIndex+this.views.length) {
                    this.startIndex += this.views.length;
                    if (this.startIndex+this.views.length > this.mud.list.size()) {
                        this.startIndex = this.mud.list.size() - this.views.length;
                    }
                }
                repaintScreen();
            } else {
                this.mud.selectionIndex = -1;
                this.autoUpdate = true;
                repaintScreen();
            }
            return true;

        case Application.KEY_CHANNELDOWN:
            System.out.println("channel down: " + 
                               this.startIndex + " " + this.mud.selectionIndex);
            if (this.mud.selectionIndex == -1) {
                // do nothing
            } else if (this.mud.selectionIndex + this.views.length < this.mud.list.size()) {
                this.mud.selectionIndex += this.views.length;
                if (this.mud.selectionIndex >= this.startIndex+this.views.length) {
                    this.startIndex += this.views.length;
                    if (this.startIndex+this.views.length > this.mud.list.size()) {
                        int adj = this.mud.list.size() - 
                            (this.startIndex+this.views.length);
                        this.startIndex += adj;
                        this.mud.selectionIndex += adj;
                    }
                }
                repaintScreen();
            } else if (this.mud.selectionIndex+1 == this.mud.list.size()) {
                this.mud.selectionIndex = -1;
                this.autoUpdate = true;
                repaintScreen();
            } else if (this.mud.selectionIndex + this.views.length > this.mud.list.size()) {
                this.mud.selectionIndex = 
                    this.startIndex + this.views.length - 1;
                repaintScreen();
            }
            return true;

        case Application.KEY_RIGHT:
            if (this.mud.selectionIndex != -1) {
                this.mud.mudView = this.mud.singleMudView;
                this.mud.mudView.switchScreen();
            }
            return true;
        case Application.KEY_LEFT:
            this.mud.setActive(false);
            return true;
        }
        return this.mud.handleKeyPressSub(code, rawcode);
    }
    
}
