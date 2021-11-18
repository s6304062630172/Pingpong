
package pingpong;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;


public class GamePanel extends JPanel implements Runnable{

	static final int GAME_WIDTH = 1000; //ความกว้างของโต๊ะ
	static final int GAME_HEIGHT = (int)(GAME_WIDTH * (0.5555)); //ความยาวโต๊ะ
	static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT); //จุดศูนย์กลางของโต๊ะ
	static final int BALL_DIAMETER = 20; //จุดศูนย์กลางของลูกปิงปองกำหนดให้เป๋น 20
	static final int PADDLE_WIDTH = 25; //ความกว้างของไม้ปิงปอง
	static final int PADDLE_HEIGHT = 100; //ความยาวของไม้ปิงปอง
	Thread gameThread; //กำหนดเธรด
	Image image;
	Graphics graphics;
	Random random;
	Paddle paddle1;  //ไม้ปิงปองของผู้เล่นคนที่ 1
	Paddle paddle2; //ไม้ปิงปองของผู้เล่นคนที่ 2
	Ball ball;  //ลูกปิงปอง
	Score score; //คะแนน
	
	GamePanel(){
		newPaddles();
		newBall();
		score = new Score(GAME_WIDTH,GAME_HEIGHT);
		this.setFocusable(true); //set focus ถ้ากดคีย์จะโฟกัสและอ่าน
		this.addKeyListener(new AL());  //เพิ่ม action listener
		this.setPreferredSize(SCREEN_SIZE);
		
		gameThread = new Thread(this); //สร้างเธรดตรงนี้จะทำงานอยู่คลอดเวลา
		gameThread.start();
	}
	
	 public void newBall() {
		random = new Random();
		ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2),random.nextInt(GAME_HEIGHT-BALL_DIAMETER),BALL_DIAMETER,BALL_DIAMETER);
	} 
	public void newPaddles() {
		paddle1 = new Paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,1);
		paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);
	}
	public void paint(Graphics g) {  //ประมวลผลค่ากราฟฟืก
		image = createImage(getWidth(),getHeight()); //เรียกwidthจาก panel นี้
		graphics = image.getGraphics();
		draw(graphics); //ทำการเรียก draw method เพื่อวาด component และส่งกราฟฟิคที่สร้างจาก image มาใส่ในdraw
		g.drawImage(image,0,0,this);  //ตั้งค่า x=0 y=0 ส่งไปยัง JPanel ที่เรียกจากGamePanel
	}
	public void draw(Graphics g) { 
		paddle1.draw(g);
		paddle2.draw(g);
		ball.draw(g);
		score.draw(g);
                                            Toolkit.getDefaultToolkit().sync(); 

	}
	public void move() {
		paddle1.move();
		paddle2.move();
		ball.move();
	}
	public void checkCollision() {
            
                                            
		
		//ลูกปิงปองเด้งอยู่ในจอwindow
		if(ball.y <=0) {
			ball.setYDirection(-ball.yVelocity);
		}
		if(ball.y >= GAME_HEIGHT-BALL_DIAMETER) {
			ball.setYDirection(-ball.yVelocity);
		}
		//ลูกปิงปองเด้งหลังจากถูกไม้ปิงปองตี
		if(ball.intersects(paddle1)) {
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++; //เพิ่มความเร็วของลูกปิงปอง
			if(ball.yVelocity>0)
				ball.yVelocity++; //เพิ่มความเร็วของลูกปิงปอง
			else
				ball.yVelocity--;
			ball.setXDirection(ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}
		if(ball.intersects(paddle2)) {
			ball.xVelocity = Math.abs(ball.xVelocity);
			ball.xVelocity++; //เพิ่มความเร็วของลูกปิงปอง
			if(ball.yVelocity>0)
				ball.yVelocity++; //เพิ่มความเร็วของลูกปิงปอง
			else
				ball.yVelocity--;
			ball.setXDirection(-ball.xVelocity);
			ball.setYDirection(ball.yVelocity);
		}
		//ทำให้ไม้ปิงปองอยูาในขอบจอ
                
		if(paddle1.y<=0)
			paddle1.y=0;
		if(paddle1.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
		if(paddle2.y<=0)
			paddle2.y=0;
		if(paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
			paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;
                
		// ให้คะแนนผู้เล่นและเซ็ตลูกปิงปองกับไม้ปิงปองใหม่
		if(ball.x <=0) {
			score.player2++;
			newPaddles();
			newBall();
			System.out.println("Player 2: "+score.player2);
		}
		if(ball.x >= GAME_WIDTH-BALL_DIAMETER) {
			score.player1++;
			newPaddles();
			newBall();
			System.out.println("Player 1: "+score.player1);
		}
	}
	public void run() {
		//ลูปเกม
		long lastTime = System.nanoTime();
		double amountOfTicks =60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;  //เก็บสถานะการทำงาน
		while(true) {
			long now = System.nanoTime();
			delta += (now -lastTime)/ns;
			lastTime = now;
			if(delta >=1) {
				move();  //เรียกใช้เมธอด move
				checkCollision();  //เรียกใช้เมธอด checkCollision
				repaint();  //หารวาดภาพกราฟฟืกใหม่
				delta--;
			}
		}
	}
	public class AL extends KeyAdapter{  //คลาสประเภท evnent adapter
		public void keyPressed(KeyEvent e) {
			paddle1.keyPressed(e);
			paddle2.keyPressed(e);
		}
		public void keyReleased(KeyEvent e) {
			paddle1.keyReleased(e);
			paddle2.keyReleased(e);
		}
	}
}