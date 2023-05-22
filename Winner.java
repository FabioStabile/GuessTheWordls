import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
// DA AGGIUNGERE: layout, non dare sempre lo stesso file
public class Winner extends JFrame{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel pnlSouth, pnlCenter, pnlNorth;
	private JLabel lblPoints, lblResult, lblErrors;
	private JButton btnBack, btnNew, btnExit;
	
	private final int maxPoints = 300;
	private final int maxTime = 600; //10 minuti
	private double remainingTime;
	private int score;
	private Vector<String> fileContent;
	private Vector<String> originalFileContent;
	
	public Winner(GUI gui, int count, int points, int countErrors, Vector<String> fileContent, Vector<String> originalFileContent) {
		this.fileContent = fileContent;
		this.originalFileContent = originalFileContent;

		setLayout(new BorderLayout());
		setTitle("Winner");
		
		pnlNorth = new JPanel(new BorderLayout());
		pnlNorth.setBackground(new Color(224,176,255));
		
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
		
		// When the mouse cursor pass on the button X, the background color get changed to red
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
		
		pnlNorth.add(btnExit, BorderLayout.EAST);
		
		pnlCenter = new JPanel();
		pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
		pnlCenter.setBackground(new Color(224,176,255));
		
		lblResult = new JLabel();
		lblResult.setOpaque(true);
		lblResult.setBackground(new Color(224,176,255));
		lblResult.setForeground(new Color(49,0,98));
		lblResult.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblResult.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		if(count <= 0)
			lblResult.setText("You've lost due to time!");
		else
			lblResult.setText("You've won, congratulations!");
		
		lblPoints = new JLabel();
		lblPoints.setOpaque(true);
		lblPoints.setBackground(new Color(224,176,255));
		lblPoints.setForeground(new Color(49,0,98));
		lblPoints.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblPoints.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		if(count <= 0)
			lblPoints.setText("Unlucky, you got " + 0 + " points");
		else if(count >= maxTime) 
			lblPoints.setText("You win " + maxTime + " points!");
		else {
			// Calculate the score
			remainingTime = (double) count / maxTime;
            score = (int) (maxPoints *  remainingTime);
           
            lblPoints.setText("You win " + score + " points! ");
		}
		
		score += points;
		// change total points in the vector
		for(int i = 0; i < fileContent.size(); i++) {
			if(fileContent.get(i).equals("POINTS"))
				fileContent.set(i + 1, "" + score);
        }
		
		// change the content of the whole file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("Numbers.txt"));
			
			for(int i = 0; i < fileContent.size(); i++) {
				writer.write(fileContent.get(i));
				writer.newLine();
			}
			
			writer.close();
		}catch(IOException e1) {
			e1.printStackTrace();
		}
		
		lblErrors = new JLabel("You have commited " + countErrors + " errors!");
		lblErrors.setOpaque(true);
		lblErrors.setBackground(new Color(224,176,255));
		lblErrors.setForeground(new Color(49,0,98));
		lblErrors.setFont(new Font("Times New Roman", Font.BOLD, 20));
		lblErrors.setAlignmentX(JLabel.CENTER_ALIGNMENT);
		
		// Add a empty space at the top
		pnlCenter.add(Box.createVerticalGlue());
		// Add a rigid structure to create a vertical empty space
		pnlCenter.add(Box.createVerticalStrut(10));
		pnlCenter.add(lblResult);
		pnlCenter.add(lblPoints);
		pnlCenter.add(lblErrors);
		// Add a rigid structure to create a vertical empty space
		pnlCenter.add(Box.createVerticalStrut(10));
		// Add a empty space at the bottom
		pnlCenter.add(Box.createVerticalGlue());
		
		pnlSouth = new JPanel(new BorderLayout());
		pnlSouth.setBackground(new Color(224,176,255));

		btnBack = new JButton("Back");
		btnBack.setBackground(new Color(224,176,255));
		btnBack.setForeground(new Color(49,0,98));
		btnBack.setOpaque(true);
		btnBack.setBorderPainted(false);
		btnBack.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnBack.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
		btnBack.setFocusPainted(false);
		btnBack.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				btnBack.setBackground(new Color(218,112,214));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnBack.setBackground(new Color(224,176,255));
			}
        	
        });
		btnBack.addActionListener(e ->{
			gui.setVisible(true);
			dispose();
		});
		
		btnNew = new JButton("New game");
		btnNew.setBackground(new Color(224,176,255));
		btnNew.setForeground(new Color(49,0,98));
		btnNew.setOpaque(true);
		btnNew.setBorderPainted(false);
		btnNew.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnNew.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "none");
		btnNew.setFocusPainted(false);
		btnNew.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				btnNew.setBackground(new Color(218,112,214));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btnNew.setBackground(new Color(224,176,255));
			}
        	
        });
		
		btnNew.addActionListener(e ->{
			new Menu();
			dispose();
		});
		
		// associating btnBack to the keyboard key: "Left arrow"
		buttonKey(btnBack, KeyEvent.VK_LEFT);
		// associating btnNew to the keyboard key: "Right arrow"
		buttonKey(btnNew, KeyEvent.VK_RIGHT);
		// associating btnExit to the keyboard key: "Esc"
		buttonKey(btnExit, KeyEvent.VK_ESCAPE);
		
		pnlSouth.add(btnBack, BorderLayout.WEST);
		pnlSouth.add(btnNew, BorderLayout.EAST);
		
		add(pnlNorth, BorderLayout.NORTH);
		add(pnlCenter, BorderLayout.CENTER);
		add(pnlSouth, BorderLayout.SOUTH);
		
		setSize(400, 400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);
		setUndecorated(true);
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
        
        // Set the btn actionCommand to the key to identify which action should be performed 
        btn.setActionCommand("buttonAction" + key);
       
        // Associate the action object to the keyboard key
        btn.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key, 0), "buttonAction" + key);
        btn.getActionMap().put("buttonAction" + key, action);
    }
}
