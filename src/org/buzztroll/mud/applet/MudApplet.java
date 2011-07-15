package org.buzztroll.mud.applet;

import org.buzztroll.mud.*;
import java.applet.*;
import java.awt.*;
import javax.swing.*;
 

public class MudApplet extends JApplet
{
    private MudClient mc;

     public void init()
     {
        try
        {
            this.getContentPane().add(new JLabel("Hello World"));
        }
        catch(Exception ex)
        {
        }
     }
 
     public void stop()
     {
     }
 

}
  
