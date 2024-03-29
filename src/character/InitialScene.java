package character;

import javax.swing.*;
import java.awt.*; 
import java.awt.event.*;
import java.io.IOException;


public class InitialScene extends MouseAdapter{
		
		JButton button_start = new JButton();
		ImageIcon image_b1 = new ImageIcon("image/button1.png");
		ImageIcon image_b2 = new ImageIcon("image/button2.png");
		
		public InitialScene() {
 
		JFrame frame = new JFrame();
		JLabel label = new JLabel();
		ImageIcon image_bg = new ImageIcon("image/background.png");
		Image img = image_bg.getImage();
		img = img.getScaledInstance(1024, 576, Image.SCALE_DEFAULT);
		image_bg.setImage(img);
		label.setIcon(image_bg);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.black);
		panel.setBounds(452, 380, image_b1.getIconWidth(), image_b1.getIconHeight());
		
		button_start.setOpaque(false);
		button_start.setContentAreaFilled(false);
		button_start.setFocusPainted(false);
		button_start.setBorder(null);
		button_start.setBounds(452, 380, image_b1.getIconWidth(), image_b1.getIconHeight());
		button_start.setIcon(image_b1);

		button_start.addMouseListener(this);
		button_start.addMouseWheelListener(this);
		button_start.addMouseMotionListener(this);
		 
		button_start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				frame.setVisible(false);
				try {
					GameFrame gf = new GameFrame();		    		
				}
				catch(Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		
		frame.setSize(1024, 576);
		frame.setTitle("Stick Fight");
		frame.setLocationRelativeTo((Component)(null));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);		
		ImageIcon image_logo = new ImageIcon("image/logo.png");
		frame.setIconImage(image_logo.getImage());
		frame.setResizable(false);
		frame.setVisible(true);
		
		panel.add(button_start);
		label.add(panel);
		frame.add(label);
		
	}

		public void mousePressed(MouseEvent e){
			button_start.setIcon(this.image_b2);
		}

		public void mouseReleased(MouseEvent e) {
			button_start.setIcon(this.image_b1);
		}
		
}
