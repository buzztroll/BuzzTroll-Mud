package org.buzztroll.mud;

import java.lang.*;
import java.awt.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;

public class
NotifyWindow
    extends JFrame
    implements ActionListener
{
    public    JButton                okButton;
    public    JButton                ignoreButton;
    protected JFrame                 mainFrame;
    protected JTextArea              pokesText;

    public 
    NotifyWindow(
        JFrame                       mFrame)
    {
        super("Notification");
        JPanel                       tempP;
    
        this.mainFrame = mFrame; 
 
        okButton = new JButton("Ok");
        ignoreButton = new JButton("Ignore");
        okButton.addActionListener(this);
        ignoreButton.addActionListener(this);
 
        tempP = new JPanel();
        tempP.setLayout(new GridLayout(1, 2, 5, 5));
        tempP.add(okButton);
        tempP.add(ignoreButton);

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pokesText = new JTextArea();
        pokesText.setRows(5);
        pokesText.setEnabled(false);
        JScrollPane sp = new JScrollPane(pokesText);

	this.getContentPane().setLayout(new BorderLayout());
        this.getContentPane().add(sp, BorderLayout.CENTER);
	this.getContentPane().add(tempP, BorderLayout.SOUTH);
        this.setSize(200, 150);

	UITools.center(mFrame, this);
    }

    public void
    actionPerformed(
        ActionEvent                  ae)
    {
        if(ae.getSource() == okButton)
        {
            mainFrame.setState(Frame.NORMAL);
            mainFrame.toFront();
            pokesText.setText("");
        }
        this.dispose();
    }

    public void 
    addMsg(
        String                      msg)
    {
        this.setVisible(true);
        pokesText.append(msg);
	pokesText.append("\n");
        repaint();
    }
}
