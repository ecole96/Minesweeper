// Evan Cole
// 10/5/17
// Program 1 - Minesweeper
// Board object handles the game grid

import java.awt.*;
import java.awt.event.MouseListener;
import javax.swing.*;
import java.util.Random;

public class Board {
    private int height;
    private int width;
    private int bomb_count;
    private Cell[][] grid;
    private ClassLoader loader = getClass().getClassLoader();

    // these variables are also in Cell since they're the icons displayed by each cell
    // since square sizes differ depending on grid dimensions, we need to be able to resize these images accordingly
    // however, if these images are only in Cell,  each image would have to be scaled for each new Cell - this is too slow (it takes several seconds after game play is started to scale each and every cell)
    // so, by putting them in Board as well, we can scale them once and then just put the scaled image in every instance of Cell instead of scaling every time
    private ImageIcon exploded_mine = new ImageIcon(loader.getResource("redmine.jpg"));
    private ImageIcon flag = new ImageIcon(loader.getResource("flag.png"));
    private ImageIcon mine_revealed = new ImageIcon(loader.getResource("mine_revealed.jpg"));

    public Board (int height, int width, int bomb_count,MouseListener ML)
    {
        this.height = height;
        this.width = width;
        this.bomb_count = bomb_count;
        grid = new Cell[height][width];

        // initialize grid array
        for(int i = 0; i < this.height; i++)
        {
            for(int j = 0; j < this.width; j++)
            {
                grid[i][j] = new Cell(i, j);
                grid[i][j].addMouseListener(ML);
            }
        }

        // put bombs on the board, set the number of adjacent bombs for each cell
        this.populateMines();
        this.InitializeAdjacentBombs();
    }

    // reveals left-clicked cell, displays the number of bombs adjacent to that cell
    // if there are no bombs adjacent to the cell, clearCells() is called, also revealing all neighbors (and any neighbors of those neighbors) with no adjacent bombs
    // works recursively with clearCells()
    public void revealCell(Cell selectedCell)
    {
        selectedCell.setText("" + selectedCell.getAdjacentBombCount());
        selectedCell.setRevealed(true);
        selectedCell.setRevealedIcon();
        if(selectedCell.getAdjacentBombCount() == 0)
        {
            selectedCell.setText("");
            clearCells(selectedCell);
        }
        else
        {
            selectedCell.setText("" + selectedCell.getAdjacentBombCount());
        }

    }

    // gets neighbors of Cell c; if they have no adjacent bombs, reveal them, and do the same thing with the neighbors of those neighbors (and so on)
    // works recursively with revealCell()
    public void clearCells(Cell c)
    {
        int[][] neighbors = getNeighbors(c);
        int neighborRow;
        int neighborCol;
        Cell neighborCell;
        for(int i = 0; i < neighbors.length; i++)
        {
            neighborRow = neighbors[i][0];
            neighborCol = neighbors[i][1];
            neighborCell = grid[neighborRow][neighborCol];
            if(!neighborCell.hasBeenRevealed() && !neighborCell.isFlagged()) {
                revealCell(neighborCell);
            }
        }
    }

    // add game board to display panel
    public void fillGridView(JPanel view)
    {
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                view.add(grid[i][j]);
            }
        }
    }

    // put mines into randomly selected cells on the board
    public void populateMines()
    {
        int randRow;
        int randCol;
        boolean bombPlanted;
        Random rand = new Random();

        // for every bomb
        for(int i = 0; i < bomb_count; i++)
        {
            // get randomly-decided coordinates for bomb location
            // if location already has a mine, keep randomly choosing spots until a cell without a mine is found
            // then, mark the cell has having a mine and move onto the next bomb until all bombs have been placed
            bombPlanted = false;
            while(!bombPlanted)
            {
                randRow = rand.nextInt(height);
                randCol = rand.nextInt(width);
                if(!grid[randRow][randCol].hasBomb())
                {
                    grid[randRow][randCol].setBomb(true);
                    //grid[randRow][randCol].setText("bomb");
                    bombPlanted = true;
                }

            }
        }
    }

    // initialize adjacent bomb count for each cell on the board using helper functions getNeighbors() and getBombsFromNeighbors()
    // unless a cell has a bomb, in which case the adjacent bomb variable is set to arbitrary constant -1
    public void InitializeAdjacentBombs()
    {
        int [][] neighbors;
        int bombsFound;
        for(int i = 0; i < this.height; i++)
        {
            for(int j = 0; j < this.width; j++)
            {
                // set arbitrary constant for adjacent bomb count
                if(grid[i][j].hasBomb())
                {
                    grid[i][j].setAdjacentBombCount(-1);
                }
                else
                {
                    // get neighbors of cell, check if those neighbors have a bomb, set adjacent bomb count accordingly
                    neighbors = getNeighbors(grid[i][j]);
                    bombsFound = getBombsFromNeighbors(neighbors);
                    grid[i][j].setAdjacentBombCount(bombsFound);
                }
                grid[i][j].colorCodeCell();
            }
        }

    }

    // big stack of conditionals for deciding neighboring cells (covers normal, edge, and corner cases)
    // returns an array of cell coordinates adjacent to parameter Cell c
    public int[][] getNeighbors(Cell c)
    {
        int[][] neighbors;
        int row = c.getRow();
        int col = c.getCol();

        // corner spaces (3 adjacent cells)
        if(row == 0 && col == 0) // upper left
        { neighbors = new int[][] { {row,col+1}, {row+1, col}, {row+1,col+1} }; }

        else if (row == 0 && col== width-1 )  // upper right
        { neighbors = new int[][] { {row,col-1}, {row+1, col}, {row+1,col-1} }; }

        else if(row == height-1 && col == 0) // lower left
        { neighbors = new int[][] { {row-1,col}, {row-1, col+1}, {row,col+1} }; }

        else if (row == height-1 && col == width-1) // lower right
        { neighbors = new int[][] { {row,col-1}, {row-1,col}, {row-1,col-1} }; }

        //edge spaces (5 adjacent cells)
        else if(row == 0) // top edge
        { neighbors = new int[][] { {row,col-1}, {row, col+1}, {row+1,col+1},{row+1,col-1}, {row+1,col}}; }

        else if(col == 0) // left edge
        { neighbors = new int[][] { {row-1,col}, {row-1, col+1}, {row,col+1},{row+1,col+1},{row+1,col} }; }

        else if(row == height-1) // bottom edge
        { neighbors = new int[][] { {row,col-1}, {row-1,col}, {row,col+1}, {row-1,col-1}, {row-1,col+1} }; }

        else if(col == width-1) // right edge
        { neighbors = new int[][] { {row,col-1}, {row+1,col}, {row-1,col}, {row-1,col-1}, {row+1,col-1} }; }

        // normal spaces (8 adjacent cells)
        else { neighbors = new int[][] {{row,col-1}, {row, col+1}, {row-1,col},{row+1,col}, {row-1,col-1},{row-1,col+1},{row+1,col-1},{row+1,col+1}}; }

        return neighbors;
    }

    // after getting a cell's neighbors using getNeighbors(), check if each neighbor has a bomb
    // if it does, increment a counter indicating bombs adjacent to the given cell (called as a parameter in getNeighbors()).
    public int getBombsFromNeighbors(int[][] neighbors)
    {
        int bombsFound = 0;
        int neighborRow;
        int neighborCol;
        for(int i = 0; i < neighbors.length;i++)
        {
            neighborRow = neighbors[i][0];
            neighborCol = neighbors[i][1];
            if(grid[neighborRow][neighborCol].hasBomb())
            {
                bombsFound++;
            }
        }
        return bombsFound++;
    }

    // when game is lost, reveal all mines on the board
    public void revealAllMines()
    {
        for(int i = 0; i < height; i++)
        {
            for(int j=0; j < width; j++)
            {
                if(grid[i][j].hasBomb() && !grid[i][j].hasBeenRevealed())
                {
                    grid[i][j].setRevealed(true);
                    grid[i][j].setRevealedMine();
                }
            }
        }
    }

    // when the game is won, flag all the mines
    public void flagAllMines()
    {
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                if(grid[i][j].hasBomb() && !grid[i][j].isFlagged())
                {
                    grid[i][j].setFlagged(true);
                    grid[i][j].setFlagIcon();
                }
            }
        }
    }

    // reset game board
    public void reset()
    {
        // set initial values to every cell on the game board
        for(int i = 0; i < this.height; i++)
        {
            for(int j = 0; j < this.width; j++)
            {
                grid[i][j].setRevealed(false);
                grid[i][j].setBomb(false);
                grid[i][j].setAdjacentBombCount(0);
                grid[i][j].setFlagged(false);
                grid[i][j].setDefaultIcon();
                grid[i][j].setText("");
            }
        }

        // put bombs on the board, set the number of adjacent bombs for each cell
        this.populateMines();
        this.InitializeAdjacentBombs();
    }

    // check for game winning condition (number of revealed cells = (total cells - # of bombs)
    // increment a counter for every cell that has been revealed and check against win condition
    public boolean gameWon()
    {
        int totalCells = height * width;
        int cellsRevealed = 0;
        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j< width; j++)
            {
                if(grid[i][j].hasBeenRevealed())
                {
                    cellsRevealed++;
                }
            }
        }

        if(cellsRevealed == (totalCells - bomb_count))
        { return true; }
        else
        { return false; }
    }

    // scales a given image to a specified width and height
    public ImageIcon scaleIcon(ImageIcon icon,int width, int height)
    {
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance( width, height,  java.awt.Image.SCALE_SMOOTH );
        icon = new ImageIcon(newimg);
        return icon;
    }

    // scales the mine/flag icons to button size of the game grid and then passes the scaled images to every Cell on the grid
    // way faster than scaling during the initialization of each Cell
    public void scaleGridIcons()
    {
        // all buttons are the same size so just get the dimensions of the first button on the grid
        int buttonWidth = grid[0][0].getWidth();
        int buttonHeight = grid[0][0].getHeight();
        exploded_mine = scaleIcon(exploded_mine,buttonWidth,buttonHeight);
        flag = scaleIcon(flag,buttonWidth,buttonHeight);
        mine_revealed = scaleIcon(mine_revealed,buttonWidth,buttonHeight);

        for(int i = 0; i < height; i++)
        {
            for(int j = 0; j < width; j++)
            {
                grid[i][j].getScaledIcons(exploded_mine,flag,mine_revealed);
            }
        }
    }

    // accessors
    public int getBombCount() {return bomb_count;}
    public int getHeight() {return height;}
    public int getWidth() {return width;}
}