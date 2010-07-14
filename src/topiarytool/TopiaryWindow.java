package topiarytool;

import com.sun.opengl.util.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.media.opengl.*;
import java.sql.*;
import javax.swing.table.*;
import java.io.*;
import javax.jnlp.*;

public class TopiaryWindow extends JFrame {
    ColorByMenu colorBy;// = new ColorByMenu();
    MainFrame frame = null;
    TreeWindow tWindow = null;

    public TopiaryWindow(MainFrame _frame) {
        colorBy = new ColorByMenu(frame,this);
    }

}
