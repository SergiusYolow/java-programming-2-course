package bsu.fpmi.edupract;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


public class EvtListener extends java.util.EventObject
{
    public static final int BUTTON = 0;// Button constants
    protected int id;                             // Which button was pressed?

    public EvtListener(Object source, int id)
    {
        super(source);
        this.id = id;
    }

    public int getID()
    {
        return id;
    }             // Return the button
}

