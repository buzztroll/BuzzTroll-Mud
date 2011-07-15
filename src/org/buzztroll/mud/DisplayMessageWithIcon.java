package org.buzztroll.mud;

import javax.swing.*;
import java.awt.*;

public class DisplayMessageWithIcon extends DisplayMessage {

    protected String beforeText;
    protected Icon icon;
    protected String afterText;

    public DisplayMessageWithIcon(String beforeText, 
                                  Icon icon, 
                                  String afterText,
                                  String text,
                                  Color color,
                                  Font font) {
        super(text, color, font);
        this.beforeText = beforeText;
        this.icon = icon;
        this.afterText = afterText;
    }
    
    public String getBeforeMessage() {
        return this.beforeText;
    }

    public Icon getIcon() {
        return this.icon;
    }

    public String getAfterMessage() {
        return this.afterText;
    }

}
