import java.awt.*; 
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

// =============== README ===============
// Use wd to move mario around the screen
// Space to juempwq
// Toggle music on and off with the playMusic boolean at the bottom of the file

public class Mario {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Mario();
        });
    }

    public Mario() {
        setup();
        startup();
    }

    private void setup() {
        System.out.println("setup");
        backgroundX = 0;
        backgroundY = 0;
        backgroundStep = 2;
        frameWidth = 525;
        frameHeight = 265;
        marioOriginalX = 90.0;
        marioOriginalY = 185.0;
        marioFurthestLeft = 5.0;
        marioFurthestRight = 332.0;
        marioMaxX = 255;
        cameraMaxLeft = 0;
        cameraMaxRight = -2932;
        songPath = "res/sounds/theme.wav";
        playing = false;
        loopAudio = true;

        // image processing
        try {
            background = ImageIO.read(new File("res/images/1-1.png"));
            marioStanding = ImageIO.read(new File("res/images/marioStanding.png"));
            // marioRunning = ImageIO.read(new File("res/images/marioRunning.gif"));
            marioJumping = ImageIO.read(new File("res/images/marioJumping.png"));
            marioSkidding = ImageIO.read(new File("res/images/marioSkidding.png"));
            superMarioStanding = ImageIO.read(new File("res/images/superMarioStanding.png"));
            // superMarioRunning = ImageIO.read(new File("res/images/superMarioRunning.gif"));
            superMarioJumping = ImageIO.read(new File("res/images/superMarioJumping.png"));
            superMarioCrouching = ImageIO.read(new File("res/images/superMarioCrouching.png"));
            superMarioSkidding = ImageIO.read(new File("res/images/superMarioSkidding.png"));
            goombaImage = ImageIO.read(new File("res/images/goomba.gif"));


            // boundsKey = ImageIO.read(new File("res/images/BOUNDARY_REGION.png"));
            // hardBoundaries = loadRegion(boundsKey, regionRED);

        } catch (IOException e) {
            e.printStackTrace();
        }

        marioImage = marioStanding;
    }

    private void startup() {
        JFrame frame = new JFrame("Mario");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameWidth, frameHeight);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);

        gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipady = 50;
        gbc.ipadx = 100;
        gbc.insets = new Insets(0, 10, 50, 10);

        // game panel
        JPanel gamePanel = new GamePanel();
        gamePanel.setLayout(new GridBagLayout());
        gamePanel.setVisible(true);
        frame.add(gamePanel);

        // bind keys
        bindKey(gamePanel, "SPACE");
        bindKey(gamePanel, "S");
        bindKey(gamePanel, "A");
        bindKey(gamePanel, "D");

        if (playMusic) playAudio(songPath);
        
        // temporarily have no main menu and just launch the game
        Thread t1 = new Thread(new Game((GamePanel) gamePanel));
        t1.start();
    }

    private static class Game implements Runnable { // switch to implements ActionListener
        private final GamePanel gamePanel;

        public Game(GamePanel gamePanel) {
            this.gamePanel = gamePanel;
        }

        @Override
        public void run() { // switch to actionPerformed(ActionEvent e)
            // set main menu visible to false
            
            wPressed = false;
            sPressed = false;
            aPressed = false;
            dPressed = false;

            mario = new ImageObject(marioOriginalX, marioOriginalY, 12, 16); // small mario width and height
            gamePanel.startTimer();

            playing = true;

            Thread t1 = new Thread(new PlayerMover());
            t1.start();
        }
    }

    public static class GamePanel extends JPanel {
        private Timer timer;

        public GamePanel() {
            timer = new Timer(32, new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (playing) {
                        repaint();
                    }
                }
            });
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;
            if (playing) {
                // draw background
                g2D.drawImage(background, backgroundX, backgroundY, null);
                g2D.drawImage(boundsKey, backgroundX, backgroundY, null);





                                //EVERYTHING GOOMBA RELATED -->
                                System.out.println(goombax);
                                //draw goomba
                                
                                if (!goombaDead){
                                g2D.drawImage(goombaImage, goombax + backgroundX, goombay, null); // Adjust Goomba position based on background movement
                                }
                                // Define constants for the left and right boundaries
                                final int LEFT_BOUNDARY = 100;
                                final int RIGHT_BOUNDARY = 250;
                                // Define a variable to keep track of the movement increment
                                int movementIncrement = 2;

                                // Update goomba movement logic
                                if (!goombaDead) {
                                    if (movingLeft) {
                                        // Move left
                                        goombax -= movementIncrement;
                                        
                                        // Check if reached the left boundary
                                        if (goombax <= LEFT_BOUNDARY) {
                                            movingLeft = false;
                                        }
                                    } else {
                                        // Move right
                                        goombax += movementIncrement;
                                        
                                        // Check if reached the right boundary
                                        if (goombax >= RIGHT_BOUNDARY) {
                                            movingLeft = true;
                                        }
                                    }
                                }
                                if (goombaDead!= true && mario.getX() > goombax && mario.getX() < goombax+10 && mario.getY() > 190) {
                                    goombaDead = true;
                                    loopAudio = false;
                                    playAudio("res/sounds/goomba_stomp.wav");
                                    
                                }
                                if (mario.getY() > 250) {
                                    mario.setX(80);
                                    mario.setY(190);
                                    goombaDead = false;
                                    backgroundX = 0;
                                }



                
                // draw player

                g2D.drawImage(marioImage, (int) mario.getX(), (int) mario.getY(), null);
                g2D.dispose();
            }
        }

        public void startTimer(){
            timer.start();
        }
    }

//     |-------------------- PLAYER FUNCTIONS -------------------|




    private static class PlayerMover implements Runnable {
        double ACCEL_X = 1.05;                //  how fast mario reaches top speed in X direction
        double SLOWDOWN_X = 0.15;             //  how fast mario slows down after he stops moving
        double MAX_SPEED_X = 2.0;             //  max speed mario can go in X direction
        boolean NOTHING_PRESSED_X = false;    //  A or D not pressed

        double GRAVITY = .2;                 //  downward force of gravity
        double ALTITUDE = 0.0;                //  how high is mario
        double JUMP_SPEED = 1.3;              //  rate mario jumps at
        // double INIT_MARIO_ALT;                //  [TESTING] initial alt before hard barriers are made
        
        boolean CAN_JUMP = true;
        boolean JUMPING = false;
        boolean ASCENDING = false;

        public PlayerMover() {
            MARIO_SPEED_X = 0.0;
        }

        @Override
        public void run() {
            while (playing) {

                try {
					Thread.sleep(9); // change to 10 if everything is too fast on your system
				} catch (InterruptedException e) { }

                Rectangle2D.Double collisionBox = mario.isColliding(); // will be nullBox if no collision

                if (!collisionBox.equals(nullBox)) {
                    System.out.println("COLLISION");
                    JUMPING = false;
                    mario.correctCollision(collisionBox);
                    CAN_JUMP = true;
                    ALTITUDE = 0.0;
                    JUMP_SPEED = 1.3; // RESET TO DEFAULT
                }

                if (!aPressed && !dPressed) {
                    NOTHING_PRESSED_X = true;
                } else {
                    NOTHING_PRESSED_X = false;
                }

                if (NOTHING_PRESSED_X) {
                    if (MARIO_SPEED_X > 0) {
                        MARIO_SPEED_X -= SLOWDOWN_X;
                        if ((MARIO_SPEED_X - SLOWDOWN_X) < 0) {
                            MARIO_SPEED_X = 0;
                        }
                    } else {
                        MARIO_SPEED_X += SLOWDOWN_X;
                        if ((MARIO_SPEED_X + SLOWDOWN_X) > 0) {
                            MARIO_SPEED_X = 0;
                        }
                    }
                }

                if (wPressed && CAN_JUMP == true) {
                    JUMPING = true;
                    ASCENDING = true;
                    CAN_JUMP = false;
                }

                if (JUMPING) {
                    if (ASCENDING) {
                        if (JUMP_SPEED > 0) {
                            // mario ascends
                            // - add JUMP_SPEED to altitude
                            // - affected by gravity
                            // - JUMP_SPEED slowly decreases and mario reaches point where JUMP_SPEED = 0

                            ALTITUDE = ALTITUDE + JUMP_SPEED;
                            JUMP_SPEED -= GRAVITY;
                            
                        } else {
                            JUMP_SPEED = 0;
                            ASCENDING = false;
                        }
                    } else {
                        // mario descends
                        // - affected by gravity
                        ALTITUDE -= GRAVITY;
                    }
                }

                if (sPressed) { // crouch, add check for super mario
                    //mario.setY(mario.getY() + marioStep);
                }

                if (dPressed && !aPressed) {
                    if (MARIO_SPEED_X < 0) {
                        // displaySkidSprite
                    }
                    MARIO_SPEED_X += (0.075)*ACCEL_X;
                    if (MARIO_SPEED_X >= MAX_SPEED_X) {
                        MARIO_SPEED_X = MAX_SPEED_X;
                    }
                }
                if (aPressed && !dPressed) {
                    if (MARIO_SPEED_X > 0) {
                        // displaySkidSprite
                    }
                    MARIO_SPEED_X += (-0.075)*ACCEL_X;
                    if (MARIO_SPEED_X <= -MAX_SPEED_X) {
                        MARIO_SPEED_X = -MAX_SPEED_X;
                    }
                }
                
                double new_x = mario.getX() + MARIO_SPEED_X;
                double new_y = mario.getY() - ALTITUDE + GRAVITY;

                // move mario
                mario.setX(new_x);
                mario.setY(new_y);
                
                updateBackground();
                checkBounds();
            }
        }
    }

    private static class ImageObject {
        //private BoundingBox marioBox;
        private Rectangle2D.Double marioBox;
        
        public ImageObject(double x, double y, double width, double height) {
            this.marioBox = new Rectangle2D.Double(x, y, width, height);
        }

        public double getX() {
            return marioBox.x;
        }

        public double getY() {
            return marioBox.y;
        }

        public void setX(double x) {
            marioBox.x = x;
        }

        public void setY(double y) {
            marioBox.y = y;
        }

        public double getWidth() {
            return marioBox.width;
        }

        public double getHeight() {
            return marioBox.height;
        }

        public Rectangle2D.Double isColliding() { // check list of hard bounds, if mario collides return the box he collides with 
            // check the edges
            double m_left_edge = getX();
            double m_top_edge = getY();
            double m_right_edge = getX() + getWidth();
            double m_bottom_edge = getY() + getHeight();

            for (Rectangle2D.Double box : barrierBoxes) {
                double b_left_edge = box.x + backgroundX;
                double b_top_edge = box.y;
                double b_right_edge = box.x + box.width + backgroundX;
                double b_bottom_edge = box.y + box.height;

                if ((m_left_edge < b_right_edge && m_right_edge > b_left_edge &&
                    m_top_edge < b_bottom_edge && m_bottom_edge > b_top_edge)) {
                    return box;
                }
            }
            return nullBox; // no box found
        }
    
        public void correctCollision(Rectangle2D.Double cb) {
            Double leftCheck = (getX()+getWidth()) - (cb.x+backgroundX);      // mbox right - cbox left
            Double rightCheck = (cb.x+cb.width+backgroundX) - getX();         // cbox right - mbox left
            Double topCheck = (getY()+getHeight()) - cb.y;                    // mbox bottom - cbox top
            Double bottomCheck = (cb.y+cb.height) - getY();                   // cbox bottom - mbox top

            Double min = Collections.min(new ArrayList<Double>() {            // check which is the smallest
                    {
                        add(leftCheck);
                        add(rightCheck);
                        add(topCheck);
                        add(bottomCheck);
                    }
                }
            );

            if (min.equals(leftCheck)) {            // we need a correction on LEFT SIDE
                setX(getX() - leftCheck);
            } else if (min.equals(rightCheck)) {    // we need a correction on RIGHT SIDE
                setX(getX() - rightCheck);
            } else if (min.equals(topCheck)) {      // we need a correction on TOP SIDE
                setY(getY() - topCheck);
            } else if (min.equals(bottomCheck)) {   // we need a correction on BOTTOM SIDE
                setY(getY());
            }
        }
    }

//     |---------------------- MISC FUNCTIONS -------------------|

    private static void playAudio(String path){
        File soundFile = new File(path);
        AudioInputStream inputStream = null;

        // basic play music with looping
        try { // get input stream
            Clip clip = AudioSystem.getClip();
            inputStream = AudioSystem.getAudioInputStream(soundFile);
            clip.open(inputStream);
            if (loopAudio) { 
                clip.loop(Clip.LOOP_CONTINUOUSLY); 
            } else {
                clip.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void adjustVolume(float volumeMultiplier, Clip clip) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float currentGain = gainControl.getValue();
        float targetGain = currentGain + (20.0f * (float) Math.log10(volumeMultiplier));
        gainControl.setValue(targetGain);
    }

    private static void updateBackground() {
        if (mario.getX() > marioMaxX && backgroundX > cameraMaxRight) {
            backgroundX -= backgroundStep;
            mario.setX(marioMaxX);
        }
        
        // in the original Super Mario Bros,
        // Mario is not permitted to move backwards

        // if (mario.getX() < marioOriginalX && backgroundX < cameraMaxLeft) {
        //     backgroundX += backgroundStep;
        //     mario.setX(marioOriginalX);
        // }
    }

    private static void checkBounds() {
        if (mario.getX() < marioFurthestLeft) {
            mario.setX(marioFurthestLeft);
            // System.out.println("Touching edge of screen");
        }
    }

//     |-------------- KEY BINDING AND DETECTION --------------|

    private static class KeyPressed extends AbstractAction {
        private String action;

        public KeyPressed(String input){
            action = input;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            // listen for specific key and change pressed variable
            if(action.equals("SPACE")){
                loopAudio = false;
                playAudio("res/sounds/jump.wav");
                wPressed = true;
            }
            if(action.equals("S")){
                sPressed = true;
            }
            if(action.equals("A")){
                aPressed = true;
            }
            if(action.equals("D")){
                dPressed = true;
            }
        }
    }

    private static class KeyReleased extends AbstractAction {
        private String action;
        public KeyReleased(String input){
            action = input;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(action.equals("SPACE")){
                wPressed = false;
            }
            if(action.equals("S")){
                sPressed = false;
            }
            if(action.equals("A")){
                aPressed = false;
            }
            if(action.equals("D")){
                dPressed = false;
            }
        }
    }

    private static void bindKey(JPanel myPanel, String input) {
        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("pressed " + input), input + " pressed");
        myPanel.getActionMap().put(input + " pressed", new KeyPressed(input));

        myPanel.getInputMap(IFW).put(KeyStroke.getKeyStroke("released " + input), input + " released");
        myPanel.getActionMap().put(input + " released", new KeyReleased(input));
    }

//     |---------------------- VARIABLES -----------------------|
    private static boolean wPressed, sPressed, aPressed, dPressed;
    private static boolean playing, loopAudio;
    private static boolean playMusic = true;

    private static String songPath;

    static boolean goombaDead = false;
    static boolean goombaArrivedLeft = false;
    static boolean goombaArrivedRight = false;
    // Define a variable to keep track of the direction of movement (left or right)
    static boolean movingLeft = true;


    private static int  goombax = 200;
    private static int  goombay = 190;
    private static int  goombaadjuster = 190;

    private static int backgroundX, backgroundY, backgroundStep, cameraMaxRight, cameraMaxLeft;
    private static int frameWidth, frameHeight;

    private static double marioOriginalX, marioOriginalY, marioMaxX, marioFurthestLeft, marioFurthestRight;
    private static double MARIO_SPEED_X; // global var for mario's speed in X plane

    private static ImageObject mario;

    private static BufferedImage background, boundsKey, marioImage, goombaImage;
    private static BufferedImage marioStanding, marioRunning, marioJumping, marioCrouching, marioSkidding;
    private static BufferedImage superMarioStanding, superMarioRunning, superMarioJumping, superMarioCrouching, superMarioSkidding;

    private static ArrayList<Rectangle2D.Double> barrierBoxes = new ArrayList<Rectangle2D.Double>() {
        {
            add(new Rectangle2D.Double(0, 208, 1104, 3));       // floor1
            add(new Rectangle2D.Double(1136, 208, 240, 3));     // floor2
            add(new Rectangle2D.Double(1424, 208, 1328, 3));    // floor3
            add(new Rectangle2D.Double(2206, 208, 36, 3));      // floor4
            add(new Rectangle2D.Double(2302, 208, 68, 3));      // floor5
            add(new Rectangle2D.Double(2542, 208, 68, 3));      // floor6
            add(new Rectangle2D.Double(2638, 208, 228, 3));     // floor7
            add(new Rectangle2D.Double(3038, 208, 131, 3));     // floor8
            add(new Rectangle2D.Double(3183, 208, 401, 3));     // floor9

            add(new Rectangle2D.Double(1100, 208, 4, 32));      // wall1
            add(new Rectangle2D.Double(1136, 208, 4, 32));      // wall2

            add(new Rectangle2D.Double(256, 144, 16, 16));      // block1
            add(new Rectangle2D.Double(320, 144, 144, 16));     // block2
            add(new Rectangle2D.Double(352, 80, 16, 16));       // block3
            add(new Rectangle2D.Double(1232, 144, 48, 16));     // block4
            add(new Rectangle2D.Double(1280, 80, 128, 16));     // block5
            add(new Rectangle2D.Double(1456, 80, 64, 16));      // block6
            add(new Rectangle2D.Double(1504, 144, 16, 16));     // block7
            add(new Rectangle2D.Double(1600, 144, 32, 16));     // block8
            add(new Rectangle2D.Double(1696, 144, 16, 16));     // block9
            add(new Rectangle2D.Double(1744, 144, 16, 16));     // block10
            add(new Rectangle2D.Double(1792, 144, 16, 16));     // block11
            add(new Rectangle2D.Double(1744, 80, 16, 16));      // block12
            add(new Rectangle2D.Double(1888, 144, 16, 16));     // block13
            add(new Rectangle2D.Double(1936, 80, 48, 16));      // block14
            add(new Rectangle2D.Double(2048, 80, 64, 16));      // block15
            add(new Rectangle2D.Double(2064, 144, 32, 16));     // block16
            add(new Rectangle2D.Double(2688, 144, 64, 16));     // block17

        }
    };

    private static Rectangle2D.Double nullBox = new Rectangle2D.Double(9999999, 9999999, 9999999, 9999999);

    private static GridBagConstraints gbc;

    private static float musicVolumeMultiplier;
    private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
}