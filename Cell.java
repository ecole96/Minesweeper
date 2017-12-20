// Evan Cole
// 10/5/17
// Program 1 - Minesweeper
// Cell object handles each cell in the game board

import java.awt.*;
import javax.swing.*;

public class Cell extends JButton {
    private ClassLoader loader = getClass().getClassLoader();

    // since these images are just solid colors, there's no real reason to scale these
    private ImageIcon default_img = new ImageIcon(loader.getResource("light_gray.jpg"));
    private ImageIcon revealed_img = new ImageIcon(loader.getResource("Solid_gray.png"));

    // once we make the grid visible, we can grab the scaled versions of these images from the Board class
    private ImageIcon exploded_mine;
    private ImageIcon flag;
    private ImageIcon mine_revealed;

    private boolean hasBomb;
    private int row;
    private int col;
    private int adjacentBombCount;
    private boolean revealed;
    private boolean flagged;

    // default constructor
    public Cell() {super();}

    // constructor we actually use (initializes row and column)
    public Cell(int row, int col)
    {
        super();
        this.row = row;
        this.col = col;
        super.setIcon(default_img);
        hasBomb = false;
        adjacentBombCount = 0;
        super.setHorizontalTextPosition(JButton.CENTER);
        super.setVerticalTextPosition(JButton.CENTER);
        super.setText("");
        revealed = false;
        flagged = false;

    }

    // mutators
    public void setAdjacentBombCount(int n) { this.adjacentBombCount = n; }
    public void setBomb(boolean value) { this.hasBomb = value;}
    public void setRevealed(boolean value){ this.revealed = value;}
    public void setFlagged(boolean value) { this.flagged = value; }

    // icon mutators
    public void setDefaultIcon() {super.setIcon(default_img); }
    public void setRevealedIcon() { super.setIcon(revealed_img); }
    public void setFlagIcon() {super.setIcon(flag);}
    public void setRevealedMine() {super.setIcon(mine_revealed);}
    public void explodeMine() { super.setIcon(exploded_mine); }

    // color codes cell text based on the number of adjacent bombs of the cell
    // 0 (and bombs) don't get color coded, any counts greater than 4 are yellow
    public void colorCodeCell()
    {
        if(adjacentBombCount < 1) { return; }
        else if(adjacentBombCount == 1) { this.setForeground(Color.blue); }
        else if(adjacentBombCount == 2) { this.setForeground(Color.green); }
        else if(adjacentBombCount == 3) { this.setForeground(Color.red); }
        else { this.setForeground(Color.yellow); }
    }

    // accessors
    public int getAdjacentBombCount(){return this.adjacentBombCount;}
    public boolean hasBomb() {return this.hasBomb;}
    public int getRow() {return this.row;}
    public int getCol() {return this.col;}
    public boolean isFlagged(){return this.flagged;}
    public boolean hasBeenRevealed() {return this.revealed;}

    // get the scaled versions of needed icons
    public void getScaledIcons(ImageIcon scaled_exploded, ImageIcon scaled_flag, ImageIcon scaled_mine_revealed)
    {
        exploded_mine = scaled_exploded;
        flag = scaled_flag;
        mine_revealed = scaled_mine_revealed;
    }

}