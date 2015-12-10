package GameState;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import GameState.GameStateManager;

import Handlers.Keys;

import Entity.*;
import Entity.Enemies.*;
import Main.GamePanel;
import TileMap.*;
import Audio.JukeBox;

public class Level0State extends GameState{

	private TileMap tileMap;
	private Background bg;
	private Console console;
	private FillScreen fs;
	private DialogBox dbox;
	private DialogBox dbox1;
	private TextPlayer player;
	
	//private ArrayList<Explosion> explosions; 
	private DialogBox[] dialog = {
			new DialogBox("Greetings Player! Welcome to GLITCH!", 2), 
			new DialogBox("Use the right and left arrow keys to move.", 2), 
			new DialogBox("Use Z to jump!", 2)}; 
	private int index;
	private int index2 = 0;
	private boolean keyPressed;
	private boolean talking;
	private boolean pauseKeyPressed;
	private boolean introduced = false;
	
	public static boolean playedOnce = false;
	
	public Level0State(GameStateManager gsm){
		super(gsm);
		init();
		try {
			gsm.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void init() {
		//initialize tile map
		tileMap = new TileMap(16);
		tileMap.loadTiles("/Tilesets/tileset.png");
		tileMap.loadMap("/Maps/intro.map");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/gamebg.png", 0.1);
		player = new TextPlayer(tileMap);
		//player.setSpawnPoint(489, 55);
		//player.setPosition(489, 55);
		player.setSpawnPoint(280, 206);
		player.setPosition(280, 206);
		//hud = new HUD(player);
		
		
		JukeBox.load("/Music/intro.mp3", "intro");
		JukeBox.load("/SFX/press.mp3", "press");
		JukeBox.loop("intro", 600, JukeBox.getFrames("intro") - 2200);
		
	}
	
	private void printDialogue() {
		if(index < dialog.length){
			fs = new FillScreen(Color.BLACK);
			dbox = dialog[index];
			JukeBox.stop("intro");
			talking = true;
		}else{
			fs.setRemove(true);
			if(dbox1 == null) talking = false;
		}
		
	}

	public void update() {
		
		printDialogue();
		if(!talking){
			handleInput();
			player.update();
			
			if(player.getx() < 205 && player.gety() < 75){
				if(dbox1 == null){
					dbox1 = new DialogBox("See that cat? You should try petting it!", 2);
					talking = true;
				}
			}
			
			if(player.getHealth() <= 0 && !playedOnce){
				JukeBox.stopAll();
			}
			
			if(playedOnce){
				gsm.setState(GameStateManager.LEVEL1STATE);
			}
			
			tileMap.setPosition(GamePanel.WIDTH / 2 - player.getx(), GamePanel.HEIGHT / 2 - player.gety());
			
			
			bg.setPosition(tileMap.getx(), tileMap.gety());
		}
		
		if(Keys.isPressed(Keys.BUTTON1)){
			if(dbox1 != null && !introduced){
				if(dbox1.isDone()){
					dbox1.setRemove(true);
					introduced = true;
					talking = false;
				}
			}
		}
		
		
	}

	
	public void draw(Graphics2D g) {
		
		//draw bg
		bg.draw(g);
		
		//draw tilemap
		tileMap.draw(g);
		
		//draw player
		player.draw(g);
		
		if(console != null) console.draw(g);
		//draw hud
		//hud.draw(g);
		
		if(dbox1 != null && !dbox1.shouldRemove()){
			dbox1.draw(g);
		}
		try{
			if(!fs.shouldRemove()){
				fs.draw(g);
			}else{
				if(index2 < 30){
					g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT / 2 - (index2 * GamePanel.HEIGHT / 60));
					g.fillRect(0, GamePanel.HEIGHT / 2 + (index2 * GamePanel.HEIGHT / 60), GamePanel.WIDTH, GamePanel.HEIGHT / 2);
					Thread.sleep(10);
					index2++;
				}else{
					JukeBox.resume("intro", true);
				}
			}
			
			if(!dbox.shouldRemove()){
				dbox.draw(g);
				if(Keys.isPressed(Keys.BUTTON1) && !keyPressed && dbox.isDone()){
					dbox.setRemove(true);
					keyPressed = true;
				}else if(!Keys.isPressed(Keys.BUTTON1)){
					keyPressed = false;
				}
			}
			else if(index < dialog.length){
				index++;
			}
		}catch(Exception e){
		}
	}
	
	public void handleInput() {
		
		if(Keys.isPressed(Keys.ESCAPE)){
			if(!pauseKeyPressed){
				gsm.setPaused(true);
				pauseKeyPressed = true;
			}
		}else{
			pauseKeyPressed = false;
		}
			
		
		if(player.getHealth() == 0) return;
		player.setUp(Keys.keyState[Keys.UP]);
		player.setLeft(Keys.keyState[Keys.LEFT]);
		player.setDown(Keys.keyState[Keys.DOWN]);
		player.setRight(Keys.keyState[Keys.RIGHT]);
		player.setJumping(Keys.keyState[Keys.BUTTON1]);
	}

	
	
}
