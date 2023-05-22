import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.LineBorder;

/*
CASE SENSITIVE
NEX BLOCK
*/
public class GUI extends JFrame {
    /**
     *
     */
    ///////
    /// COMPONENT JFRAME
    ///////
    private static final long serialVersionUID = 1L;
    private JPanel pnlCenter, pnlSouth, pnlBtn, pnlHelp, pnlShop, pnlNorth, pnlGame, pnlBottom, pnlCheck, pnlBottomArrow, pnlTopArrow;
    private JTextField[][] tf;
    private JLabel[][] lbl;
    private JLabel lblHelp, lblPoints, lblGame;
    private JButton btnCheck, btnHelp, btnShop, btnExit, btnTopArrow, btnBottomArrow;
    private JTextArea ta;

    ///////
    /// OTHERS
    ///////
    // number helps
    private int helps; //= 3;
    // total points of the game
    private int points; // = 0;
    // shop point's pack and cost
    private final String[] packs = {"2 helps: 20 points", "6 helps: 50 points", "12 helps: 85 points", "32 helps: 150 points", "50 helps, 200 points"};
    // used in btnCheck to see if a line has already been completed
    private boolean[] done;  
    // used in the textFields to see if the word is completed, if it is then it doesn't showw anything in the textArea
    private boolean[] complete;
    // number of words guessed
    private int guessedWords = 0;
    // to see if the program is blocked due to time, if it is blocked the check open a new frame
    private boolean block = false;  
    // used in the gui's blockProgram function to print the showMessageDialog just one time
    private boolean dialog = false;
    // to see if the game is finished, used to block the help if it is
    private boolean finish = false;
   // see, when the user make a mistake, if the program found a empty cell
    private boolean foundFocusable;
    // used to see in which line the user is writing
    private int currentRow = -1;
    // set the background color of each textField
    private Color[][] tfBackgrounds;
    // content of the file
    private Vector<String> fileContent;
    // to reset the file once we close the program
    private Vector<String> originalFileContent;
    // count the numbers of errors
    private int countErrors = 0;
    ///////
    /// TIMER
    ///////
    // calculate the point with the remaining game time
    private Timer timer;

    ///////
    ///
    ///////
    public GUI(String file, int rows, int columns, Vector<String> originalFileContent) {
    	
    	// set the number of helps and points of the game, if this isn't the second game it initialize them with the remaining helps and total points of the previous game
    	fileContent = new Vector<>();
    	String str = null, nHelps = "", score = "";
    	try {
    		BufferedReader reader = new BufferedReader(new FileReader("Numbers.txt"));
    		
    		str = reader.readLine();
    		
    		while(str != null) {
    			fileContent.add(str);
    			if(str.equals("HELPS")) {
    				str = reader.readLine();
    				fileContent.add(str);
    				nHelps += str;
    			}else if(str.equals("POINTS")){
    				str = reader.readLine();
    				fileContent.add(str);
    				score += str;
    			}
    			str = reader.readLine();
    		}
    		
    		reader.close();
    	}catch(IOException e1) {
    		e1.printStackTrace();
    	}
    	
    	helps = Integer.parseInt(nHelps);
    	points = Integer.parseInt(score);
    	
    	setLayout(new BorderLayout());

        pnlNorth = new JPanel(new BorderLayout());
        pnlGame = new JPanel(new FlowLayout());


        pnlNorth.setBackground(new Color(224, 176, 255));
        pnlGame.setBackground(new Color(224, 176, 255));
        
        timer = new Timer(this);
        timer.setOpaque(true);
        timer.setBackground(new Color(224,176,255));
        timer.setForeground(new Color(49,0,98));
        timer.setFont(new Font("Times New Roman", Font.BOLD, 20));

        lblGame = new JLabel("GuessTheWord");
        lblGame.setOpaque(true);
        lblGame.setBackground(new Color(224,176,255));
        lblGame.setForeground(new Color(102, 0, 102));
        lblGame.setFont(new Font("Times new roman", Font.BOLD, 30));

        btnExit = new JButton("X");
        btnExit.setBackground(new Color(224,176,255));
        btnExit.setForeground(Color.BLACK);
        btnExit.setOpaque(true);
        btnExit.setBorderPainted(false);
        btnExit.setFont(new Font("Times New Roman", Font.BOLD, 20));
        btnExit.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        btnExit.setFocusPainted(false);
        btnExit.addActionListener(e ->{     	
        	// revert the content of numbers to the default one
        	try {
    			BufferedWriter writer = new BufferedWriter(new FileWriter("Numbers.txt"));
    			
    			for(int i = 0; i < originalFileContent.size(); i++) {
    				writer.write(originalFileContent.get(i));
    				writer.newLine();
    			}
    			
    			writer.close();
    		}catch(IOException e1) {
    			e1.printStackTrace();
    		}
        	// empty the file UsedFiles
        	try {
				BufferedWriter writer = new BufferedWriter(new FileWriter("UsedFiles.txt"));
				
				writer.write("");
				
				writer.close();
			}catch(IOException e1) {
				e1.printStackTrace();
			}
        	
            System.exit(0);
        });
        
        // When the mouse cursor pass on the button ì, the background color get changed to red
        btnExit.addMouseListener(new MouseAdapter(){
     		
        	@Override
     		public void mouseEntered(MouseEvent e) {
     			btnExit.setBackground(Color.RED);
     		}

     		@Override
     		public void mouseExited(MouseEvent e) {
     			btnExit.setBackground(new Color(224,176,255));
     		}	
     	});

        pnlGame.add(lblGame, BorderLayout.CENTER);
        
        pnlNorth.add(timer, BorderLayout.WEST);
        pnlNorth.add(pnlGame, BorderLayout.CENTER);
        pnlNorth.add(btnExit, BorderLayout.EAST);

        pnlCenter = new JPanel(new GridLayout(rows, columns));
        pnlCenter.setBackground(new Color(224,176,255));

        tfBackgrounds = new Color[rows][columns];
        for(int i = 0;  i < rows; i++) {
        	for(int j = 0; j < columns; j++) {
        		tfBackgrounds[i][j] = new Color(204,255,255);
        	}
        }
        
        // initializing the word's grid
        lbl = new JLabel[rows][columns];
        tf = new JTextField[rows][columns];
        complete = new boolean[rows];
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
 
            for (int i = 0; i < rows; i++) {
                String nextLine = reader.readLine();
                boolean found = false;
                
                while (found == false) {
                	// search for the next word with lenght = columns
                    if (nextLine.length() == columns) {
                    	int line = i;
                    	
                    	 int previousLine = line - 1;
                    	 int afterLine = line + 1;
                        for (int j = 0; j < columns; j++) {
                            lbl[i][j] = new JLabel(String.valueOf(nextLine.charAt(j)));
                            lbl[i][j].setHorizontalAlignment(JLabel.CENTER);
                            lbl[i][j].setOpaque(true);
                            lbl[i][j].setBackground(Color.RED);
                            lbl[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
                           
                            tf[i][j] = new JTextField();
                            tf[i][j].setHorizontalAlignment(JTextField.CENTER);
                            tf[i][j].setOpaque(true);
                            tf[i][j].setBackground(new Color(204, 255, 255));
                            tf[i][j].setForeground(Color.BLACK); 
                            
                            int pillar = j;
                            // print in the TextArea the description of the word
                            tf[i][j].addMouseListener(new MouseAdapter() {             
								@Override
								public void mouseReleased(MouseEvent e) {
									for(int k = 0; k < rows; k++) {
										for(int y = 0; y < columns; y++) {
											if(!(k == line)) {
												tf[k][y].setBackground(tfBackgrounds[k][y]);
											}
										}
									}
									
									if(!complete[line]) {
										int count = 0;
						                String nextLine;
						                try {
						                    boolean exit = false;
						                    BufferedReader reader = new BufferedReader(new FileReader(file));
						                    nextLine = reader.readLine();
	
						                    while (exit == false) {
						                        if (nextLine.length() == columns) {
						                            if (line == count) {
						                                nextLine = reader.readLine();
						                                while (!nextLine.contains("stop")) {
						                                    ta.setText(nextLine);
						                                    nextLine = reader.readLine();
						                                }
						                                exit = true;
						                            } else {
						                                count++;
						                                nextLine = reader.readLine();
						                            }
						                        } else {
						                            nextLine = reader.readLine();
						                        }
						                    }
						                    reader.close();
						                } catch (IOException e1) {
						                    e1.printStackTrace();
						                }					                
									}
		
									if(currentRow != -1) {
										for(int j = 0; j < columns; j++) {
											if(tfBackgrounds[currentRow][j] == Color.YELLOW)
												tf[currentRow][j].setBackground(Color.YELLOW);
											else if(tfBackgrounds[currentRow][j] == Color.GREEN)
												tf[currentRow][j].setBackground(Color.GREEN);
											else
												tf[currentRow][j].setBackground(new Color(204, 255, 255));
										}
									}
									
								
									
									for(int j = 0; j < columns; j++) {
										if(j == pillar)
											tf[line][pillar].setBackground(new Color(51, 204, 255));
										else if(tfBackgrounds[line][j] != Color.YELLOW)
											tf[line][j].setBackground(new Color(0, 255, 204));
									}
									currentRow = line;
								}
                            });                         
                            // set the color of the selected line, the cell were we're writing will be different respect the rest of the line
                            tf[i][j].addFocusListener(new FocusListener() {

								@Override	
								public void focusGained(FocusEvent e) {
									// set the backGround of the previous line
									if(previousLine != -1) {
										//if(pillar == 0) {
											for(int k = 0; k < columns; k++) {
												tf[previousLine][k].setBackground(tfBackgrounds[previousLine][k]);
											}
										//}
									}
									// set the background of the nextLine
									if(afterLine != rows) {
										//if(pillar == 0) {
											for(int k = 0; k < columns; k++) {
												tf[afterLine][k].setBackground(tfBackgrounds[afterLine][k]);
											}
										//
											
									
									}
									if(pillar == 0) {
										for(int k = 0; k < columns; k++) {
											if(k == pillar)
												tf[line][pillar].setBackground(new Color(51, 204, 255));
											else {
												// doesn't convert the yellows textFields
												if(tfBackgrounds[line][k] != Color.YELLOW)
													tf[line][k].setBackground(new Color(0, 255, 204));
											}
										}	
									}
									JTextField focus = (JTextField) e.getSource();
									focus.setBackground(new Color(51, 204, 255));
								}

								@Override
								public void focusLost(FocusEvent e) {
									JTextField lost = (JTextField) e.getSource();
									lost.setBackground(new Color(0, 255, 204));									
								}           	
                            });
 
                            // change textFiled's current cell to the first one empty once a character is digited
                           tf[i][j].addKeyListener(new KeyAdapter() {

								@Override
								public void keyReleased(KeyEvent e) {
									int countColumns = 0, countRows = 0, nLine = line, nPillar = pillar, countCells = 0;
									boolean row = false, column = false, increment = false;
									
									for(int k = 0; k < columns; k++) {
										if(!tf[line][k].getText().equals(""))
											countCells++;
									}
									if(countCells == columns) {
										btnCheck.doClick();
									}
							         if (tf[nLine][nPillar].getText().length() > 0) {
							        	 if (nPillar < columns - 1) {
							                while(!column) {
							                	if(nPillar + countColumns == columns - 1) {
							                		if(!tf[line][nPillar + countColumns].getText().equals("")) {
							                			nLine++;
							                			nPillar = 0;
							                			countColumns = 0;
							                		}
							                	}
								                if(tf[nLine][nPillar + countColumns].getText().equals("")) {
								                	tf[nLine][nPillar + countColumns].requestFocus();
								                	column = true;
								                }else {
								                	countColumns++;
								                	if(countColumns == columns)
								                		countColumns = 1;
								                }
							                }
							        	 }else if (nLine < rows - 1) {
							        		 while(!row) {
							                	for(int k = 0; k < columns; k++) {
									                if(tf[nLine + countRows][k].getText().equals("")) {
									                	tf[nLine + countRows][k].requestFocus();
									                	row = true;
									                	increment = true;
									                	k = columns - 1;
									                } 	
							                	}
							                	if(!increment) {
								                	countRows++;
								                	if(countRows == columns)
								                		countRows = 1;
								                	}
							                	}
							                }
							            }
							            
							         	// print the description of the new world in the textArea
							            int count = 0;
						                String nextLine;
						                try {
						                    boolean exit = false;
						                    BufferedReader reader = new BufferedReader(new FileReader(file));
						                    nextLine = reader.readLine();
	
						                    while (exit == false) {
						                        if (nextLine.length() == columns) {
						                            if (nLine + countRows == count) {
						                                nextLine = reader.readLine();
						                                while (!nextLine.contains("stop")) {
						                                    ta.setText(nextLine);
						                                    nextLine = reader.readLine();
						                                }
						                                exit = true;
						                            } else {
						                                count++;
						                                nextLine = reader.readLine();
						                            }
						                        } else {
						                            nextLine = reader.readLine();
						                        }
						                    }
						                    reader.close();
						                } catch (IOException e1) {
						                    e1.printStackTrace();
						                }	
									}          	
                            });
                           
                            // set the Border of every textField
                            if (tf[i][j].getText().isEmpty()) 
                            	tf[i][j].setBorder(new LineBorder(new Color(102, 0, 102)));
                            
                            tf[i][j].setDocument(new Format(1));
                            pnlCenter.add(tf[i][j]);
                        }
                        found = true;

                    } else {
                        nextLine = reader.readLine();
                    }

                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        pnlSouth = new JPanel(new GridLayout(2, 1));
        pnlSouth.setBackground(new Color(224,176,255));
        pnlBtn = new JPanel(new BorderLayout());
        pnlBtn.setBackground(new Color(224,176,255));
        pnlHelp = new JPanel(new GridLayout(2, 1));
        pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBackground(new Color(224,176,255));
        pnlCheck = new JPanel(new FlowLayout());
        pnlCheck.setBackground(new Color(224,176,255));
        pnlTopArrow = new JPanel(new FlowLayout());
        pnlTopArrow.setBackground(new Color(224,176,255));
        pnlBottomArrow = new JPanel(new FlowLayout());
        pnlBottomArrow.setBackground(new Color(224,176,255));
        
        // go to the first empty cell to the top
        btnTopArrow = new JButton();
        btnTopArrow.setPreferredSize(new Dimension(1, 1));
        btnTopArrow.setBackground(new Color(224,176,255));
        btnTopArrow.setOpaque(true);
        btnTopArrow.setBorderPainted(false);
        btnTopArrow.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        btnTopArrow.setFocusPainted(false);
        btnTopArrow.addActionListener(e ->{
        	for(int i = 0; i < rows; i++) {
        		for(int j = 0; j < columns; j++) {
	        		if(tf[i][j].isFocusOwner()) {						
	        			int newLine = i;
	        			boolean focus = false;
	        			
	        			while(!focus){
	        				newLine = newLine - 1;
	        				if(newLine == -1)
	        					newLine = rows - 1;
	        				for(int k = 0; k < columns; k++) {
	        					if(tf[newLine][k].getText().equals("")) {
	        						tf[newLine][k].requestFocus();
	        						focus = true;
	        						k = columns - 1;
	        					}
	        				}		
	        			}    			
	        		}
        		}
        	}
        });
        
        // go to the first empty cell to the bottom
        btnBottomArrow = new JButton();
        btnBottomArrow.setPreferredSize(new Dimension(1, 1));
        btnBottomArrow.setBackground(new Color(224,176,255));
        btnBottomArrow.setOpaque(true);
        btnBottomArrow.setBorderPainted(false);
        btnBottomArrow.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        btnBottomArrow.setFocusPainted(false);
        btnBottomArrow.addActionListener(e ->{
        	for(int i = 0; i < rows; i++) {
        		for(int j = 0; j < columns; j++) {
        			if(tf[i][j].isFocusOwner()) {
	        			int newLine = i;
	        			boolean focus = false;
	        			
	        			while(!focus){
	        				newLine = newLine + 1;
	        				
	        				if(newLine == rows)
	        					newLine = 0;
	        				
	        				for(int k = 0; k < columns; k++) {
	        					if(tf[newLine][k].getText().equals("")) {
	        						tf[newLine][k].requestFocus();
	        						focus = true;
	        						k = columns - 1;
	        					}
	        				}
	        			}
	        		}
        		}
        	}
        });
        
        pnlTopArrow.add(btnTopArrow);
        pnlBottomArrow.add(btnBottomArrow);
        
        done = new boolean[rows];
        btnCheck = new JButton("Check");
        btnCheck.setBackground(new Color(224,176,255));
        btnCheck.setForeground(new Color(49,0,98));
        btnCheck.setOpaque(true);
        btnCheck.setBorderPainted(false);
        btnCheck.setFont(new Font("Times New Roman", Font.BOLD, 20));
        btnCheck.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        btnCheck.setFocusPainted(false);
        
        btnCheck.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				btnCheck.setBackground(new Color(218,112,214));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnCheck.setBackground(new Color(224,176,255));
			}
        	
        }); 
        
        // CHECK
        btnCheck.addActionListener(e -> {
        	if(block == false) {
	            boolean correct = true;
	            boolean alreadyIncremented = false;
	            int half = (int) rows/2,  threeQuarters = (int)rows * 3 / 4;
	            
	            for (int i = 0; i < rows; i++) {
	                int cont = 0;
	                for (int j = 0; j < columns; j++) {
	                    if (tf[i][j].getText().equals(lbl[i][j].getText())) {
	                        tf[i][j].setEditable(false);
	                        tf[i][j].setBackground(Color.YELLOW);
	                        // remove the textField's mouseListener
                            for(MouseListener ml : tf[i][j].getMouseListeners()) {
                            	tf[i][j].removeMouseListener(ml);
                            }
	                        tfBackgrounds[i][j] = Color.YELLOW;
	                        lbl[i][j].setBackground(Color.GREEN.darker().brighter().brighter());
	                        
	                        // GREEN IF COMPLETE
	                       if (!tf[i][j].isEditable()) {
	                            cont++;
	                            // All columns are yellow
	                            if(cont==columns) {
	                                for (int n = 0; n < columns; n++) {
	                                    tf[i][n].setBackground(Color.GREEN);
	                                    tfBackgrounds[i][n] = Color.GREEN;
	                                    ta.setText(null);
	                                    if(!alreadyIncremented) {
	                                    	guessedWords++;
	                                    	alreadyIncremented = true;
	                                    }
	                                }
	                                //num.
	                                int max;
	                                if(guessedWords <= half) 
	                                	max = 3;
	                                else if(guessedWords > half && guessedWords <= threeQuarters)
	                                	max = 1;
	                                else
	                                	max = 0;
	                                
	                                if(max != 0) {
		                                if(done[i] == false) {
		                                    for (int s = 0; s < max; s++) {
		                                        boolean k = false;
		
		                                        while (k == false) {
		                                            int r = new Random().nextInt(rows);
		                                            int c = new Random().nextInt(columns);
		
		                                            if (tf[r][c].getText().equals("")) {
		                                                tf[r][c].setText(lbl[r][c].getText());
		                                                tf[r][c].setBackground(Color.YELLOW);
		                                                tf[r][c].setFocusable(false);
		                                                tf[r][c].setEditable(false);
		                                                tfBackgrounds[r][c] = Color.YELLOW;
		                                                lbl[r][c].setBackground(Color.GREEN.darker().brighter().brighter());
		                                                k = true;
		                                                done[i] = true;
		                                                
		                                                int check = 0;
		                                                
		                                                // see if the line is completed after have generated a character
		                                                for(int p = 0; p < columns; p++) {
		                                                	if (!tf[i][p].isEditable()) {
		                                                		check++;
		                                                        // All columns are yellow
		                                                        if (check == columns) {
		                                                            for (int n = 0; n < columns; n++) {
		                                                                tf[i][n].setBackground(Color.GREEN);
		                                                                tfBackgrounds[i][n] = Color.GREEN;
		                                                                ta.setText(null);
		                                                                complete[i] = true;
		                                                            }
		                                                        }
		                                                	}else
		                                                		check = 0;
		                                                } 
		                                            }
		                                        }
		                                    }
		                                }
	                                }else complete[i] = true;
	                            }
	                        } else cont = 0;
	                    } else  if(!tf[i][j].getText().equals("")){   
		                    tf[i][j].setBackground(Color.RED);
		                    int line = i, pillar = j;
		                    foundFocusable = false;
		                    
		                    Thread thread = new Thread(new Runnable() {
	
								@Override
								public void run() {
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										e1.printStackTrace();
									}
									
									tf[line][pillar].setBackground(new Color(204, 255, 255));
				                   	tf[line][pillar].setText(null);
				                   	
				                   	// set the focus to the first empty cell of the same line or above
				                   	int nLine = line - 1;
				                   	foundFocusable = false;
				                   	while(foundFocusable == false) {
				                   		nLine = nLine + 1;
				                    		
				                   		if(nLine == rows)
			    	                   		nLine = 0;
			    	                   	
			    	                       for(int k = 0; k < columns; k++) {
				    	                       	if(tf[nLine][k].getText().equals("")) {
				    	                       		tf [nLine][k].requestFocus();
				    	                       		foundFocusable = true;
				    	                       		k = columns - 1;
				    	                       	}
			    	                       }
				                   	}
								}
		                   	});
		                   	thread.start(); 
		                   	countErrors++;
	                    }            
	                }
	            }
	            
	            // see if there're any cells left empty
	            for(int i = 0; i < rows; i++) {
	            	for(int j = 0; j < columns; j++) {
	            		if(tf[i][j].isEditable()) 
	            			correct = false;
	            	}
	            }
	            
	            // if there aren't the game is finished
	            if (correct == true) {
	                for (int i = 0; i < rows; i++) {
	                    for (int j = 0; j < columns; j++) {
	                        pnlCenter.remove(tf[i][j]);
	                        pnlCenter.add(lbl[i][j]);
	                    }
	                }
	
	                pnlCenter.revalidate();
	                pnlCenter.repaint();
	                timer.stop();
	                
	                finish = true;
	                blockProgram(this, 1);
	                
	                Winner winner = new Winner(this, timer.getCount(), points, countErrors, fileContent, originalFileContent);
	               	winner.setVisible(true);
	                setVisible(false);      
	            }
        	}else {
        		 Winner winner = new Winner(this, timer.getCount(), points, countErrors, fileContent, originalFileContent);
	             winner.setVisible(true);
	             setVisible(false);  
        	}
        });

        pnlCheck.add(btnCheck);
        
        lblHelp = new JLabel("Helps remaining: " + helps);
        lblHelp.setOpaque(true);
        lblHelp.setBackground(new Color(224,176,255));
        lblHelp.setForeground(new Color(49,0,98));

        btnHelp = new JButton();
        btnHelp.setIcon(new ImageIcon("lampada.PNG"));
        btnHelp.setBackground(new Color(224,176,255));
        btnHelp.setBorderPainted(false);
        btnHelp.setOpaque(true);
        btnHelp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        btnHelp.setFocusPainted(false);
        btnHelp.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				//btnHelp.setBackground(new Color(0, 51, 204));
				btnHelp.setBackground(new Color(218,112,214));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnHelp.setBackground(new Color(224,176,255));
			}
        	
        });
        ta = new JTextArea();
        // generate random character, depends on how many words remain.
        // less than half = 3 char, more than a half less threequarter = 1, otherwise 0
        btnHelp.addActionListener(e -> {
        	if(!finish) {
        		int emptyCells = 0;
        		// see how mani empety cells there are
	        	for(int i = 0; i < rows; i++) {
	        		for(int j = 0; j < columns; j++) {
	        			String color = "" + lbl[i][j].getBackground();
	        			if(color.equals("java.awt.Color[r=255,g=0,b=0]")) 
	        				emptyCells++;	
	        		}
	        	}
	      
	            if (helps > 0) {
	                int n, m;
	                if(emptyCells != 0) {
		                if(emptyCells >= 3) {
			                for (int i = 0; i < 3; i++) {
			                    boolean different = false;
			
			                    while (different == false) {
			                        n = new Random().nextInt(rows);
			                        m = new Random().nextInt(columns);
			
			                        if (tf[n][m].getText().equals("")) {
			                            tf[n][m].setText(lbl[n][m].getText());
			                            tf[n][m].setBackground(Color.YELLOW);
			                            tf[n][m].setEditable(false);
			                            tfBackgrounds[n][m] = Color.YELLOW;
			                            lbl[n][m].setBackground(Color.GREEN.darker().brighter().brighter());
			                            different = true;
			                        }
			                    }
			                }
		                }else {
		                	boolean different = false;
		            		
		                    while (different == false) {
		                        n = new Random().nextInt(rows);
		                        m = new Random().nextInt(columns);
		
		                        if (tf[n][m].getText().equals("")) {
		                            tf[n][m].setText(lbl[n][m].getText());
		                            tf[n][m].setBackground(Color.YELLOW);
		                            tfBackgrounds[n][m] = Color.YELLOW;
		                            lbl[n][m].setBackground(Color.GREEN.darker().brighter().brighter());	
		                            tf[n][m].setEditable(false);
		                            different = true;
		                        }
		                    }
		                        
		                }
		
		                helps--;
		                lblHelp.setText("Helps remaining: " + helps);
	                }else JOptionPane.showMessageDialog(null, "There aren't no longer any empty cells", "Conferma", JOptionPane.INFORMATION_MESSAGE);
	                	
	            } else  JOptionPane.showMessageDialog(null, "Helps finished, buy more", "Conferma", JOptionPane.INFORMATION_MESSAGE);
        	}
        	
        	// updating the number of remaining helps in the vector
            for(int i = 0; i < fileContent.size(); i++) {
            	if(fileContent.get(i).equals("HELPS"))
            		fileContent.set(i + 1, "" + helps);
            }
        });

        pnlHelp.add(btnHelp);
        pnlHelp.add(lblHelp);

        pnlShop = new JPanel(new GridLayout(2, 1));

        lblPoints = new JLabel("Total points: " + points);
        lblPoints.setOpaque(true);
        lblPoints.setBackground(new Color(224,176,255));
        lblPoints.setForeground(new Color(49,0,98));

        btnShop = new JButton("Shop");
        btnShop.setBackground(new Color(224,176,255));
        btnShop.setForeground(new Color(49,0,98));
        btnShop.setOpaque(true);
        btnShop.setBorderPainted(false);
        btnShop.setFont(new Font("Times New Roman", Font.BOLD, 20));
        btnShop.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        btnShop.setFocusPainted(false);
        btnShop.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				btnShop.setBackground(new Color(218,112,214));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnShop.setBackground(new Color(224,176,255));
			}
        	
        });
        
        // open the shop where you can buy helps in exchange of points
        btnShop.addActionListener(e -> {
            String selectedPack = (String) JOptionPane.showInputDialog(this, "Select a pack", "Options", JOptionPane.PLAIN_MESSAGE, null, packs, packs[0]);
            int price = 0;
            int h = 0;
            switch (selectedPack) {
                case "2 helps: 20 points":
                    price = 20;
                    h = 2;
                    break;
                case "6 helps: 50 points":
                    price = 50;
                    h = 6;
                    break;
                case "12 helps: 85 points":
                    price = 85;
                    h = 12;
                    break;
                case "32 helps: 150 points":
                    price = 150;
                    h = 32;
                    break;
                case "50 helps, 200 points":
                    price = 200;
                    h = 50;
                    break;
            }
            
            if (points >= price) {
            	helps += h;
                points -= price;
                lblHelp.setText("Helps remaining: " + helps);
                lblPoints.setText("Total points: " + points);
                JOptionPane.showMessageDialog(null, "You have purchased with success " + h + " helps!\n" + "Helps remaining: " + helps + "\nPoints remaining: " + points, "confirms", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Insufficient points!", "confirms", JOptionPane.INFORMATION_MESSAGE);
            }
            

            for(int i = 0; i < fileContent.size(); i++) {
            	if(fileContent.get(i).equals("HELPS"))
            		fileContent.set(i + 1, "" + helps);
            	else if(fileContent.get(i).equals("POINTS"))
            		fileContent.set(i + 1, "" + points);
            }
        });

        pnlShop.add(btnShop);
        pnlShop.add(lblPoints);

        ta = new JTextArea();
        //ta.setPreferredSize(new Dimension(getWidth(), 20));
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setBackground(new Color(224,176,255));
        ta.setForeground(new Color(49,0,98));
        ta.setFont(new Font("Times New Roman", Font.BOLD, 20));
        ta.setEditable(false);

        // Associating btnHelp botton with the key: "F1"
        buttonKey(btnHelp, KeyEvent.VK_F1);
        // Associating btnCheck botton with the key: "Enter"
        buttonKey(btnCheck, KeyEvent.VK_ENTER);
        // Associating btnShop botton with the key: "F2"
        buttonKey(btnShop, KeyEvent.VK_F2);
        // Associating btnShop botton with the key: "ESC"
        buttonKey(btnExit, KeyEvent.VK_ESCAPE);
        // Associating btnShop botton with the key: "Up arrow"
        buttonKey(btnTopArrow, KeyEvent.VK_UP);
        // Associating btnShop botton with the key: "Down arrow"
        buttonKey(btnBottomArrow, KeyEvent.VK_DOWN);
        
        pnlBottom.add(pnlTopArrow, BorderLayout.NORTH);
        pnlBottom.add(pnlCheck, BorderLayout.CENTER);
        pnlBottom.add(pnlBottomArrow, BorderLayout.SOUTH);
        
        pnlBtn.add(pnlBottom, BorderLayout.CENTER);
        pnlBtn.add(pnlHelp, BorderLayout.WEST);
        pnlBtn.add(pnlShop, BorderLayout.EAST);
        pnlSouth.add(pnlBtn);
        pnlSouth.add(ta);

        add(pnlNorth, BorderLayout.NORTH);
        add(pnlCenter, BorderLayout.CENTER);
        add(pnlSouth, BorderLayout.SOUTH);
     
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
    }
    
    // block the program once it's completed, we pass n to recognize who is calling the function, the btnCheck or the timer
    // if it's the timer we need to add something to the function
    public void blockProgram(GUI gui, int n) {	
    	btnHelp.setEnabled(false);
    	btnShop.setEnabled(false);
    	ta.setText(null);
    	// we don't need to do that to the arrows botton too becuase if the game is completed there aren't anymore empty cells, so the arrows can't operate since they need empty cells to act
    	
    	for(int i = 0; i  < tf.length; i++) {
    		for(int j = 0; j < tf[0].length; j++) {
    			pnlCenter.remove(tf[i][j]);
                pnlCenter.add(lbl[i][j]);
            }
        }
        pnlCenter.revalidate();
        pnlCenter.repaint();
        
        if(dialog == false) {
        	if(n == 2)
        		JOptionPane.showMessageDialog(null, "You've runned out of time!");
	        Winner winner = new Winner(this, timer.getCount(), points, countErrors, fileContent, originalFileContent);
	       	winner.setVisible(true);
	       	setVisible(false);
        }
        
        dialog = true;
        block = true;
    }
    
    // Function to associate a keyboard key to a button
    public static void buttonKey(JButton btn, int key) {
    	Action action = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
            	btn.doClick();
            }
        };
        // Set the btn actionCommand to the key to identify which action should be performed 
        btn.setActionCommand("buttonAction" + key);
       
        // Associate the action object to the keyboard key
        btn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), "buttonAction" + key);
        btn.getActionMap().put("buttonAction" + key, action);
    }
}
