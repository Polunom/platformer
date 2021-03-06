package GameState;

import java.awt.*;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;

import Audio.JukeBox;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import Handlers.Keys;

import TileMap.Background;

public class MenuState extends GameState {
	
	private Background bg;
	
	private int currentChoice = 0;
	private String[] options = {"New", "Load", "Controls", "Quit"};
	private String copyright = "2016 Kelvin Zhang";
	private String version = "Version 2.3";
	private boolean buttonPressed = false;
	BufferedImage logo;
	BufferedImage icon;
	
	private Font font;
	

	public MenuState(GameStateManager gsm){
		
		super(gsm);
		
		try{
			bg = new Background("/Backgrounds/menubg.gif", 1);
			
			font = new Font("Arial", Font.PLAIN, 12);
			logo = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/logo.png"));
			icon = ImageIO.read(getClass().getResourceAsStream("/Backgrounds/pointer.png"));
			JukeBox.load("/SFX/option.mp3", "option");
			JukeBox.load("/SFX/select.mp3", "select");
			JukeBox.load("/Music/menu.mp3", "menu");
			if(!JukeBox.isPlaying("menu")) JukeBox.loop("menu", 600, JukeBox.getFrames("menu") - 2200);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void init() {
		
	}

	public void update() {
		handleInput();
		bg.update();
	}

	public void draw(Graphics2D g) {
		
		bg.draw(g);
		
		g.drawImage(logo, 70, 30, null);
		
		g.setFont(font);
		for (int i = 0; i < options.length; i++) {
			if( i == currentChoice){
				g.setColor(Color.GREEN);
			}else{
				g.setColor(Color.WHITE);
			}
			if(currentChoice == 0){
				g.drawImage(icon, 120, 108, null);
			}else if(currentChoice == 1){
				g.drawImage(icon, 120, 123, null);
			}else if(currentChoice == 2){
				g.drawImage(icon, 120, 138, null);
			}else if(currentChoice == 3){
				g.drawImage(icon, 120, 153, null);
			}
			g.drawString(options[i], 145, 120 + i * 15);
		}
		
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.setColor(Color.WHITE);
		g.drawString(copyright, 113, 240-30);
		g.drawString(version, 133, 240-20);
	}

	private void select(){
		
		if(currentChoice == 0){
			//new game
			gsm.setState(GameStateManager.INTROSTATE);
			try{
				Writer wr = new BufferedWriter(new FileWriter("save.txt"));
				wr.append("2");
				wr.close();
				JukeBox.stop("menu");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		if(currentChoice == 1){
			//load
			try{
				Scanner in = new Scanner(new File("save.txt"));
				while(in.hasNextLine()){
					gsm.setState(in.nextInt());
				}
				in.close();
				JukeBox.stop("menu");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		if(currentChoice == 2){
			//options
			gsm.setState(GameStateManager.OPTIONSSTATE);
		}
		
		if(currentChoice == 3){
			//exit
			JukeBox.stop("menu");
			System.exit(0);
		}
	}

	public void handleInput() {
		
		if(Keys.isPressed(Keys.ENTER)){
			JukeBox.play("select");
			select();
		}
		else if(Keys.isPressed(Keys.UP)){
			if(!buttonPressed){
				currentChoice--;
				if(currentChoice == -1){
					currentChoice = options.length - 1;
				}
				buttonPressed = true;
				JukeBox.play("option");
			}
		}
		else if(Keys.isPressed(Keys.DOWN)){
			
			if(!buttonPressed){
				currentChoice++;
				if(currentChoice == options.length){
					currentChoice = 0;
				}
				buttonPressed = true;
				JukeBox.play("option");
			}
		}else{
			buttonPressed = false;
		}
		
	}

}
