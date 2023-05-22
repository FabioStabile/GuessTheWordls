import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;
import java.util.Vector;

public class Menu extends JFrame {
   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel lblImage;
    private JButton btnStart;
    private ImageIcon image;
    private Vector<String> originalFileContent;
    private Vector<Integer> files;
    
    public Menu(){
    	setLayout(null);
    	
    	image = new ImageIcon("GuessTheWorld.jpeg");
        lblImage = new JLabel(image);
        // set the dimension of the image
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width= (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        try {
            image.setImage((ImageIO.read(new File("GuessTheWorld.jpeg"))).getScaledInstance(width,height,Image.SCALE_DEFAULT));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setContentPane(lblImage);

        btnStart = new JButton("START");
        btnStart.setBackground(new Color(255, 215, 0));
        btnStart.setOpaque(true);
        btnStart.setBorderPainted(false);
        btnStart.setFont(new Font("Arial", Font.BOLD, 20));
        //btnStart.setBounds(900, 734, 100, 30);
        btnStart.setBounds(655, 615, 100, 30);
        // Remove the default possibility to activate the button with the key "SPACE"
        btnStart.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
        // Remove the default selection box
        btnStart.setFocusPainted(false);
        btnStart.addActionListener(e ->{
        	files = new Vector<>();
        	// see which files have been used
        	try (
            		BufferedReader reader = new BufferedReader(new FileReader("UsedFiles.txt"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int number = Integer.parseInt(line);
                        files.add(number);
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        	
        	// control if the size of file exceeds or equals the number of fails, if it does it show a message with two option
        	if(files.size() >= 5) {
        		int btn = JOptionPane.showOptionDialog(null, "All files have been used!", "Custom Button Dialog", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Refresh", "Close"}, "Refresh");
        		// refresh button
        		if(btn == 0) {
        			try {
        				BufferedWriter writer = new BufferedWriter(new FileWriter("UsedFiles.txt"));
        				
        				writer.write("");
        				
        				writer.close();
        			}catch(IOException e1) {
        				e1.printStackTrace();
        			}
        		// close button
        		}else if(btn == 1) {
        			try {
        				BufferedWriter writer = new BufferedWriter(new FileWriter("UsedFiles.txt"));
        				
        				writer.write("");
        				
        				writer.close();
        			}catch(IOException e1) {
        				e1.printStackTrace();
        			}
        			System.exit(0);
        		}	
        	// if it doesn't exceed or it isn't equals, than it generate random a number that has not yet been used
        	}else {
        		int rand = new Random().nextInt(5) + 1;
            	
            	while(files.contains(rand))
            		rand = new Random().nextInt(5) + 1;
            	
        		int rows = 0;
	        	String file = "" + rand + ".txt", firstLine = null, nextLine = null;
	        	try {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					firstLine = reader.readLine();
					nextLine = reader.readLine();
					
					while(nextLine != null) {
						if(nextLine.equals("stop"))
							rows++;
						nextLine = reader.readLine();
					}
					
					
					
					reader.close();
	        	} catch (IOException  e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	
	        	try {
	        		BufferedWriter writer = new BufferedWriter(new FileWriter("UsedFiles.txt", true));
	        		
	        		writer.append("" + rand);
	        		writer.newLine();
	        		writer.flush();
	        		writer.close();
	        	}catch(IOException e1) {
	        		e1.printStackTrace();
	        	}
	        	
	        	// set content of the file in the vector
	        	originalFileContent = new Vector<>();
	        	try {
	        		BufferedReader reader = new BufferedReader(new FileReader("OriginalContent.txt"));
	        		nextLine = reader.readLine();
	        		
	        		while(nextLine != null) {
	        			originalFileContent.add(nextLine);
	        			nextLine = reader.readLine();
	        		}
	        		
	        		reader.close();
	        	}catch(IOException e1) {
	        		e1.printStackTrace();
	        	}
	        	
	        	// open a new class
	        	ProgressBar bar = new ProgressBar(file, rows, firstLine.length(), originalFileContent);
	        	bar.setVisible(true);	
	        	dispose();	
        	}
        });
        
        buttonKey(btnStart, KeyEvent.VK_ENTER);
        add(btnStart);
        
        setTitle("Welcome to crossWord!");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setVisible(true);
    }
    
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

	    // String actionKey = "buttonAction" + key;
	        
	    // Set the btn actionCommand to the key to identify which action should be performed 
	    btn.setActionCommand("buttonAction" + key);
	       
	    // Associate the action object to the keyboard key
	    btn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), "buttonAction" + key);
	    btn.getActionMap().put("buttonAction" + key, action);
	}
    
    public static void main(String[] args) {
        new Menu();
    }
}
