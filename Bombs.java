// Evan Cole
// 10/5/17
// Program 1 - Minesweeper
// Bombs object handles the game instance (windows, clicks, buttons, etc.)

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Bombs extends JFrame implements ActionListener,MouseListener {
    private ClassLoader loader = getClass().getClassLoader();
    private ImageIcon normal_reset = new ImageIcon(loader.getResource("smile.png"));
    private ImageIcon win_reset = new ImageIcon(loader.getResource("win-smile.jpg"));
    private ImageIcon lose_reset = new ImageIcon(loader.getResource("lose-smile.png"));

    private Board gameGrid;
    private int flagCounter;

    private JLabel timerLabel, flagLabel;
    private JPanel gridView, labelView;

    private JMenuBar menubar;
    JMenu game, settings, help; // menus
    JMenuItem newGame, quit, beginner, intermediate, expert, custom, instructions; // submenus

    private Timer gameTimer;
    int secondsElapsed;
    private boolean gameStarted;
    private boolean gameOver;

    private JButton reset;

    public Bombs() {
        super("Minesweeper");
        Container c = getContentPane();
        gameGrid = new Board(5, 5, 5, this);
        flagCounter = gameGrid.getBombCount();

        menubar = new JMenuBar();
        game = new JMenu("Game");
        settings = new JMenu("Settings");
        help = new JMenu("Help");
        menubar.add(game);
        menubar.add(settings);
        menubar.add(help);

        newGame = new JMenuItem("Start New Game");
        quit = new JMenuItem("Quit");
        game.add(newGame);
        game.addSeparator();
        game.add(quit);
        newGame.addActionListener(this);
        quit.addActionListener(this);

        beginner = new JMenuItem("Beginner (5x5, 5 bombs)");
        intermediate = new JMenuItem("Intermediate (8x8, 15 bombs)");
        expert = new JMenuItem("Expert (10x10, 30 bombs)");
        custom = new JMenuItem("Custom");
        settings.add(beginner);
        settings.add(intermediate);
        settings.add(expert);
        settings.add(custom);
        beginner.addActionListener(this);
        intermediate.addActionListener(this);
        expert.addActionListener(this);
        custom.addActionListener(this);

        instructions = new JMenuItem("Instructions");
        help.add(instructions);
        instructions.addActionListener(this);


        gridView = new JPanel();
        gridView.setLayout(new GridLayout(gameGrid.getHeight(), gameGrid.getWidth(), 2, 2));
        labelView = new JPanel();
        labelView.setLayout(new FlowLayout());
        gridView.setBackground(Color.BLACK);
        labelView.setBackground(Color.darkGray);

        timerLabel = new JLabel("Timer: 0");
        timerLabel.setForeground(Color.WHITE);
        flagLabel = new JLabel("Flags:" + flagCounter);
        flagLabel.setForeground(Color.WHITE);


        gameTimer = new Timer(1000, this);
        secondsElapsed = 0;

        gameStarted = false;
        gameOver = false;

        reset = new JButton();
        reset.setSize(30, 30);
        this.scaleResetIcons();
        reset.setIcon(normal_reset);
        reset.addActionListener(this);


        labelView.add(flagLabel);
        labelView.add(reset);
        labelView.add(timerLabel);

        gameGrid.fillGridView(gridView);

        this.setJMenuBar(menubar);
        c.add(labelView, BorderLayout.NORTH);
        c.add(gridView, BorderLayout.CENTER);

        setSize(440, 525);
        setResizable(false);
        setVisible(true);

        // we can't get button width and height until we make the buttons visible, so we have to scale the images after drawing and then redraw
        gameGrid.scaleGridIcons();
        revalidate();
        repaint();
    }

    // handles mouse clicks (cell clicks and flags)
    public void mouseClicked(MouseEvent e) {
        if (gameOver) // game doesn't respond to clicks if game is over - user must click reset until they can do anything again
        {
            return;
        }

        if (!gameStarted) // start timer when user makes their first click of the game
        {
            gameStarted = true;
            gameTimer.start();
        }

        Cell selectedCell = (Cell) e.getSource(); // get clicked cell

        if (selectedCell.hasBeenRevealed()) // can't do anything with an already-revealed cell
        {
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON1)
        {
            if (selectedCell.isFlagged()) // can't left click a flagged cell (have to unflag it before you can)
            {
                return;
            }

            if (selectedCell.hasBomb()) // game over - revealed a bomb (show all bombs on map, end game)
            {
                selectedCell.setRevealed(true);
                selectedCell.explodeMine();
                gameOver = true;
                gameTimer.stop();
                gameGrid.revealAllMines();
                reset.setIcon(lose_reset);
                revalidate();
                repaint();
            }
            else // reveal non-bomb cell(s)
            {
                gameGrid.revealCell(selectedCell);
                if (gameGrid.gameWon()) // check for game winning condition - if so, stop the time and end the game
                {
                    gameGrid.flagAllMines();
                    reset.setIcon(win_reset);
                    gameOver = true;
                    gameTimer.stop();
                    flagLabel.setText("Flags: 0");
                    revalidate();
                    repaint();
                }
            }
        }
        else {
            // right click to flag a cell
            if (e.getButton() == MouseEvent.BUTTON3)
            {
                if (!selectedCell.isFlagged()) // right click an unflagged cell - set flag (decrement flag label)
                {
                    selectedCell.setFlagged(true);
                    selectedCell.setFlagIcon();
                    flagCounter--;
                }
                else // right click a flagged cell - unset flag (increment flag label)
                {
                    selectedCell.setFlagged(false);
                    selectedCell.setDefaultIcon();
                    flagCounter++;
                }
                flagLabel.setText("Flags: " + flagCounter);
            }
        }
    }

    public void mouseExited(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == gameTimer) // increment timer label every second
        {
            secondsElapsed++;
            timerLabel.setText("Timer: " + secondsElapsed);
        }
        else if (e.getSource() == reset || e.getSource() == newGame) // reset/new game (start new game in menu and smiley click do the same thing)
        {

            gameGrid.reset();
            resetInstanceVariables();
            revalidate();
            repaint();
        }
        else if (e.getSource() == beginner || e.getSource() == intermediate || e.getSource() == expert || e.getSource() == custom) // resize game grid (preset or custom)
        {
            int newHeight;
            int newWidth;
            int newBombCount;

            // new grid sizes
            if (e.getSource() == beginner)
            {
                newHeight = 5;
                newWidth = 5;
                newBombCount = 5;
            }
            else if (e.getSource() == intermediate)
            {
                newHeight = 8;
                newWidth = 8;
                newBombCount = 15;
            }
            else if (e.getSource() == expert)
            {
                newHeight = 10;
                newWidth = 10;
                newBombCount = 30;
            }
            else // custom size
            {
                int[] successfulResize = customSizing();
                if(successfulResize != null) // successful resize
                {
                    newHeight = successfulResize[0];
                    newWidth = successfulResize[1];
                    newBombCount = successfulResize[2];
                }
                else // user cancelled or Xed out
                {
                    return;
                }
            }

        boolean sameDifficulty = (gameGrid.getHeight() == newHeight && gameGrid.getWidth() == newWidth && gameGrid.getBombCount() == newBombCount);

        if (!sameDifficulty) // draw game grid with new dimensions
        {
            newGrid(newHeight, newWidth, newBombCount);
            gameGrid.scaleGridIcons();
        }
        else // if dimensions and bomb count are the same as before, just reset the game instead of redrawing grid
        {
            gameGrid.reset();
        }

        // reset game variables and redraw
        resetInstanceVariables();
        revalidate();
        repaint();
    }

    else if(e.getSource()==instructions) // instruction window
    {
        displayHelp();
    }

    else if(e.getSource()==quit) // quit button
    {
        System.exit(0);
    }
}

    public void newGrid(int height, int width,int bomb_count)
    {
        // clear current grid panel, set new grid layout based on new dimensions
        gridView.removeAll();
        gridView.setLayout(new GridLayout(height, width, 2, 2));

        // initialize new grid and redraw
        gameGrid = new Board(height, width, bomb_count,this);
        gameGrid.fillGridView(gridView);
        revalidate();
        repaint();
    }

    // presents the custom sizing screen and handles the input/interactions appropriately
    // returns an integer array of the new dimensions/bomb count if input is valid; otherwise (user cancels or Xes out window)
    // this function was originally inside actionPerformed() but converted into its own function for the sake of readability
    public int[] customSizing()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3,2,2,2));

        // labels and number input boxes
        JLabel heightLabel  = new JLabel("Height (5-10): ");
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(gameGrid.getHeight(), 5, 10, 1));
        panel.add(heightLabel);
        panel.add(heightSpinner);

        JLabel widthLabel = new JLabel("Width (5-10): ");
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(gameGrid.getWidth(),5,10,1));
        panel.add(widthLabel);
        panel.add(widthSpinner);

        JLabel bombLabel = new JLabel("Bombs (1-half of total squares): ");

        // maximum is 50 here since that is the maximum number of bombs that the game can handle (since 10x10 is the largest possible grid size and 50 cells is half of the that
        // we want the user to input the number of bombs they WANT to have, not limited by the current amount they're playing with
        // we check if that input is valid once the user clicks OK
        JSpinner bombSpinner = new JSpinner(new SpinnerNumberModel(gameGrid.getBombCount(), 1, 50, 1));

        panel.add(bombLabel);
        panel.add(bombSpinner);

        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, panel.getPreferredSize().height));

        while(true) // this while loop enables the program to kick the user back to the input screen instead of back to the game window if an error is made (so they don't have to reclick the menu to try again)
        {
            int newHeight;
            int newWidth;
            int newBombCount;
            int option = JOptionPane.showOptionDialog(null,panel,"Custom Game Size",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE,null,null,null);
            if(option == JOptionPane.OK_OPTION) // user clicks OK button
            {
                // since we're using JSpinners, any improper input (non-numeric, negatives, outside of range specified earlier) is ignored - except for bomb count exceeding over half of the desired grid
                newHeight = (Integer) heightSpinner.getValue();
                newWidth = (Integer) widthSpinner.getValue();
                newBombCount = (Integer) bombSpinner.getValue();

                int maximumNumberOfBombs = (newHeight * newWidth) / 2;
                if(newBombCount > maximumNumberOfBombs) // check for invalid bomb count
                {
                    // if so, display error message
                    String error_msg = "ERROR: Number of bombs exceeds half of the desired grid. Must be between 1 and " + maximumNumberOfBombs + ".";
                    JOptionPane.showMessageDialog(null,error_msg,"Error",JOptionPane.ERROR_MESSAGE); // when user clicks OK on this, they'll be sent back to the input screen instead of back to the game window
                }
                else
                {
                    // valid input - return new dimensions/bomb count as an array
                    int[] size_arr = {newHeight,newWidth, newBombCount};
                    return size_arr;
                }
            }
            else // user clicks cancel or Xes out of window - return null
            {
                return null;
            }
        }
    }

    // help window
    public void displayHelp()
    {
        // instructions taken from http://www.freeminesweeper.org/help/minehelpinstructions.html and slightly altered to fit this version
        String rules =
                "Quick Start:\n"
                        + "- You are presented with a board of squares. Some squares contain mines (bombs), others don\'t. If you click on a square containing a bomb, you lose. If you manage to click all the squares (without clicking on any bombs) you win.\n"
                        + "- Clicking a square which doesn\'t have a bomb reveals the number of neighboring squares containing bombs. Use this information plus some guess work to avoid the bombs.\n "
                        + "- To open a square, point at the square and click on it. To mark (flag) a square you think is a bomb, point and right-click it.\n\n"

                        + "Detailed Instructions:\n"
                        + "- A square\'s neighbors are the squares adjacent above, below, left, right, and all 4 diagonals. Squares on the sides of the board or in a corner have fewer neighbors. The board does not wrap around the edges.\n"
                        + "- If you open a square with 0 neighboring bombs, all its neighbors will automatically open. This can cause a large area to automatically open.\n"
                        + "- To remove a flag from a square, point at it and right-click again.\n"
                        + "- If you mark a bomb incorrectly, you will have to correct the mistake before you can win. Incorrect bomb marking doesn't kill you, but it can lead to mistakes which do.\n"
                        + "- You don\'t have to mark all the bombs to win; you just need to open all non-bomb squares.\n"
                        + "- Click the yellow smiley face to start a new game.";

        // put text in a text pane and resize the window to a "nicer" size
        JTextPane jtp = new JTextPane();
        jtp.setText(rules);
        jtp.setSize(new Dimension(500,700)); // arbitrary, this just needs to be done for some reason
        jtp.setPreferredSize(new Dimension(500,jtp.getPreferredSize().height)); // actual resize
        JOptionPane.showMessageDialog(null, jtp, "Instructions",JOptionPane.INFORMATION_MESSAGE);

    }

    // resets variables in this class
    // used in conjunction with the Board.reset() class to completely reset the game
    public void resetInstanceVariables()
    {
        if(gameTimer.isRunning()) { gameTimer.stop(); }
        reset.setIcon(normal_reset);
        gameOver = false;
        gameStarted = false;
        secondsElapsed = 0;
        flagCounter = gameGrid.getBombCount();
        timerLabel.setText("Timer: 0");
        flagLabel.setText("Flags:" + flagCounter);
    }

    // scale smiley images
    public void scaleResetIcons()
    {
        normal_reset = scaleIcon(normal_reset);
        win_reset = scaleIcon(win_reset);
        lose_reset = scaleIcon(lose_reset);
    }

    // scales a given image (since we're only using this instance of the function to draw smileys of a known size, there's no need to get any width/height variables like when drawing the grid)
    public ImageIcon scaleIcon(ImageIcon icon)
    {
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance( 30, 30,  java.awt.Image.SCALE_SMOOTH );
        icon = new ImageIcon(newimg);
        return icon;
    }

    // driver
    public static void main(String args[])
    {
        Bombs B = new Bombs();
        B.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
}