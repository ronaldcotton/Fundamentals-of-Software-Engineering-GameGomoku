// CS 320 - A2
// Programmed by: Alex C. and Ron C.

/*
Copyright (c) 2016 Alex C. and Ron C.

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package game;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Image;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

public class GameGomokuGUI extends JFrame {
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private int bgNum=0, bgMax=2, goBoard[][] = new int[15][15], clickedX, clickedY, i, j, k;
	private Image imgBackground[] = new Image[3];
	private Boolean playerMove, newGame=true, checkWin=false;			// false = black, true = white (black moves first)
	private double mouseX, mouseY;
	Stack<Integer> goHistory; 
	//private Graphics g;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GameGomokuGUI frame = new GameGomokuGUI();
					frame.setVisible(true);
					//frame.repaint();	//Tell me if this stops you from needing to drag GUI down to see jpanel
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GameGomokuGUI() {
		// Windows Focus Gained - If another window draws on top of the window, the window needs to be redrawn
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent arg0) {
				repaint();
			}
		}); // end Focus Listener - Focus Gained
		
		// Window Moved - If window reaches the edge of the screen, the window needs to repaint. 
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentMoved(ComponentEvent arg0) {
				repaint();
			}
		});// end Component Listener - Component Moved
		
		setSize(new Dimension(640, 680));
		setMinimumSize(new Dimension(640, 680));
		setMaximumSize(new Dimension(640, 680));
		
		
		setResizable(false);
		setTitle("Gomoku");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 460, 300);
		
		JMenuBar JMenuBar = new JMenuBar();
		setJMenuBar(JMenuBar);
		
		// System - menuDeselected - repaint()
		JMenu JMenu_System = new JMenu("System");
		// Menu Deselected - need to repaint() when the menu goes away so we can see board once again
		JMenu_System.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent arg0) {}
			public void menuDeselected(MenuEvent arg0) {
				repaint();
			}
			public void menuSelected(MenuEvent arg0) {}
		}); // end Menu Deselected
		
		JMenuBar.add(JMenu_System);
		
		JMenuItem JMenuItem_Change_Background = new JMenuItem("Change Background");
		// Change Background - Increment background until we reach the max number for the background
		JMenuItem_Change_Background.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				bgNum++;
				if (bgNum>bgMax) bgNum=0;
				repaint();
			}
		}); // end Action Listener - Change Background
		
		JMenu_System.add(JMenuItem_Change_Background);
		
		// Mouse Clicked Quit
		JMenuItem JMenuItem_Quit = new JMenuItem("Quit");
		JMenuItem_Quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});
		
		loadBackground(); // load Background Graphics in one swoop!
		
		JMenu_System.add(JMenuItem_Quit);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(5, 0));
		setContentPane(contentPane);
		
		JPanel pnlMain = new JPanel();
		// Mouse Clicked - When the mouse is clicked, find out what element of the array of the game board and add if piece is not there.
		pnlMain.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				mouseX = (double)(arg0.getX()-55)/35;
				mouseY = (double)(arg0.getY()-15)/35;

				if ((mouseX - Math.floor(mouseX))<.8) {
					if ((mouseY - Math.floor(mouseY))<.8) {
						clickedX = (int)(Math.floor(mouseX));
						clickedY = (int)(Math.floor(mouseY));
						if ((clickedX<0)||(clickedX>14)) return;
						if ((clickedY<0)||(clickedY>14)) return;
						if (goBoard[clickedX][clickedY]==0) {
							if (playerMove) goBoard[clickedX][clickedY] = 1; else goBoard[clickedX][clickedY] = -1;
								goHistory.push(clickedX); goHistory.push(clickedY);
								playerMove = !playerMove;
								checkWin = true;
								repaint();
						} // end if
					}
				}
			}
		});
		pnlMain.setBackground(Color.WHITE);
		contentPane.add(pnlMain, BorderLayout.CENTER);
		pnlMain.setLayout(null);
		
		JPanel pnlButton = new JPanel();
		pnlButton.setBackground(Color.WHITE);
		pnlButton.setBounds(0, 550, 626, 35);
		pnlMain.add(pnlButton);
		
		// Restart Button - calls the newGame function
		JButton btnRestart = new JButton("Restart");
		btnRestart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newGoGame();
			}
		}); // End Action Listener for Restart Button
		
		pnlButton.add(btnRestart);
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(20, 0));
		separator.setOrientation(SwingConstants.VERTICAL);
		pnlButton.add(separator);
		
		JButton btnBack = new JButton("Back");
		
		// Back Button - pops last move from history and clears element from goBoard array, then flips the playerMove
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (!goHistory.empty()) {
					int tempY=goHistory.pop();
					int tempX=goHistory.pop();
					goBoard[tempX][tempY]=0;
					playerMove=!(playerMove);
					repaint();
				}
			}
		}); // end Action Listener for Back Button
		
		btnBack.setPreferredSize(new Dimension(87, 25));
		btnBack.setMinimumSize(new Dimension(87, 25));
		btnBack.setMaximumSize(new Dimension(87, 25));
		pnlButton.add(btnBack);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setPreferredSize(new Dimension(20, 0));
		separator_1.setOrientation(SwingConstants.VERTICAL);
		pnlButton.add(separator_1);
		
		// Quit Button - Does a System.exit()
		JButton btnQuit = new JButton("Quit");
		btnQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}); // end Action Listener for Quit Buttin
		
		btnQuit.setPreferredSize(new Dimension(87, 25));
		btnQuit.setMinimumSize(new Dimension(87, 25));
		btnQuit.setMaximumSize(new Dimension(87, 25));
		pnlButton.add(btnQuit);
	} // end public GameGomokuGUI()
	
	@Override
	public void update(Graphics g){  					// overridden empty update to speed up the repaint() function
	}
	
	@Override
	public void paint(Graphics g) {
		//int windowX = this.getWidth(), windowY = this.getHeight();
		super.paint(g);									// If this method is reimplemented, super.paint(g) should be called so that lightweight components are properly rendered.
														// If a child component is entirely clipped by the current clipping setting in g, paint() will not be forwarded to that child.
														// from http://docs.oracle.com/javase/7/docs/api/java/awt/Container.html#paint(java.awt.Graphics)
		
		if (newGame) newGoGame();						// if new go game: setup goBoard array, goHistory, black player first
		if (checkWin) 									// if checkWin is true - check to see if the last move was a win
			if ((k=gameWin())!=0) {
				if (k==1)
					JOptionPane.showMessageDialog(this, "White player has won!", "Gomoku Winner", JOptionPane.PLAIN_MESSAGE);
				else 
					JOptionPane.showMessageDialog(this, "Black player has won!", "Gomoku Winner", JOptionPane.PLAIN_MESSAGE);
				newGoGame();
			}
		
		// Draw Background Image, depending on the index of bgNum
		g.drawImage(imgBackground[bgNum], 0, 54, Color.BLACK, null);  // 44
		
		// Draw Grid
		g.setColor(Color.BLACK);
		for (i=0; i<525; i+=35) {						// 15x15 grid - 35 pixels per side - offset by 75 pixels on x, 75 pixels on y
			g.drawLine(75, 75+i, 565, 75+i);		// draws horizontal lines
			g.drawLine(75+i, 75+0, 75+i, 565); 	// draws vertical lines
		} // end loop for drawing lines
		
		// draw pieces on board
		for (i=0; i<15; ++i) {
			for (j=0; j<15; ++j) {
				if ((k=goBoard[i][j])!=0) {
					if (k==1) { g.setColor(Color.WHITE); g.fillOval(61+(i*35),62+(j*35),30,30); }
					else { g.setColor(Color.BLACK); g.fillOval(61+(i*35),62+(j*35),30,30); }
				} // end if goBoard
			} // end j - inner loop for drawing game pieces
		} // end i - outer loop for drawing game pieces

		
		// To Do list
		// Check move - 5 in a row - (not with stack, but with int Array)

		
	} // end public void paint()
	
	private void loadBackground() {
		try {
			imgBackground[0] = ImageIO.read(new File(System.getProperty("user.dir")+"/assets/Background1.jpg")); // attempts to load the three
			imgBackground[1] = ImageIO.read(new File(System.getProperty("user.dir")+"/assets/Background2.jpg")); // default backgrounds from the
			imgBackground[2] = ImageIO.read(new File(System.getProperty("user.dir")+"/assets/Background3.jpg")); // assets folder
		} catch (IOException e) {
			e.printStackTrace();
		}
	} // end loadBackground()
	
	private void newGoGame() {
		for (int[] row : goBoard)				// Fills the goBoard with zeros
		    Arrays.fill(row, 0);
		goHistory = new Stack<Integer>();		// create a new stack of Object Integer						
		playerMove = false;						// starting player is black
		newGame = false;						// remove the newGame status
		repaint();
	} // end newGoGame()
	
	private int gameWin() {
		int pieceCheck;
		
		if (playerMove) pieceCheck=-1; else pieceCheck=1; 	// game player changed, so need to flip again!

		k=1;
		// check X coord
		// check +X
		for (i=1;i<5;++i) {
			if ((clickedX+i)>14) break;						// if outside the bounds, quit checking
			if (goBoard[(clickedX+i)][clickedY]==pieceCheck) ++k; 	// increment if it's a piece
			else break;
		} // end for i
		// check -X
		for (i=-1;i>-5;--i) {
			if ((clickedX+i)<0) break;					// if outside the bounds, quit checking
			if (goBoard[(clickedX+i)][clickedY]==pieceCheck) ++k;  // increment if it's a piece
			else break;
		} // end for i

		
		if (k>4) return pieceCheck;
		
		k=1;						// reset k
		// check Y coord
		// check +Y
		for (i=1;i<5;++i) {
			if ((clickedY+i)>14) break;							// if outside the bounds, quit checking
			if (goBoard[clickedX][clickedY+i]==pieceCheck) ++k;  // increment if it's a piece
			else break;											// otherwise quit checking that direction
		} // end for i
		// check -Y
		for (i=-1;i>-5;--i) {					   // if outside the bounds, quit checking
			if ((clickedY+i)<0) break;							// if outside the bounds, quit checking
			if (goBoard[clickedX][clickedY+i]==pieceCheck) ++k;    // increment if it's a piece
			else break;											   // otherwise quit checking that direction
		} // end for i
		
		if (k>4) return pieceCheck;
		
		k=1;						// reset k
		// check y=x
		for (i=1;i<5;++i) {
			if (((clickedY-i)<0)||((clickedX+i)>14)) break;	   // if outside the bounds, quit checking
			if (goBoard[clickedX+i][clickedY-i]==pieceCheck) ++k; // increment if it's a piece
			else break;											   // otherwise quit checking that direction
		} // end for i
		// check -y=-x
		for (i=1;i<5;++i) {
			if (((clickedY+i)>14)||((clickedX-i)<0)) break;   // if outside the bounds, quit checking
			if (goBoard[clickedX-i][clickedY+i]==pieceCheck) ++k;  // increment if it's a piece
			else break;	// otherwise quit checking that direction
		} // end for i
		
		if (k>4) return pieceCheck;
		
		k=1;
		// check y=-x
		for (i=1;i<5;++i) {
			if (((clickedY-i)<0)||((clickedX-i)<0)) break;		   // if outside the bounds, quit checking
			if (goBoard[clickedX-i][clickedY-i]==pieceCheck) ++k;   // increment if it's a piece
			else break;
		} // end for i
		// check -y=x
		for (i=1;i<5;++i) {
			if (((clickedY+i)>14)||((clickedX+i)>14)) break;	   // if outside the bounds, quit checking
			if (goBoard[clickedX+i][clickedY+i]==pieceCheck) ++k;  // increment if it's a piece
			else break;	
		} // end for i
		
		if (k>4) return pieceCheck;
		
		checkWin=false;
		return 0;
	} // end gameWin()
	
} // end public class GameGomokuGUI extends JFrame


