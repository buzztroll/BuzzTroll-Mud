package org.buzztroll.mud;

import javax.swing.*;
import java.awt.*;

public class
DisplayMessage
{
    protected Color                         color;
    protected String                        message;
    protected Font                          font = null;

    public
    DisplayMessage(
        String                              msg,
        Color                               c,
        Font                                f)
    {
        this.color = c;
        this.message = msg;
        this.font = f;
    }

    public String
    getString()
    {
        return this.message;
    }

    public Color
    getColor()
    {
        return this.color;
    }

    public Font
    getFont()
    {
        return this.font;
    }

    public void
    setFont(
        Font                                f)
    {
        this.font = f;
    }
}
