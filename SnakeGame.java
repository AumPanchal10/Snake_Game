import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 20;
    private final int WIDTH = 30, HEIGHT = 20;
    private LinkedList<Point> snake1, snake2;
    private Point food, powerUp;
    private Timer gameTimer;
    private boolean running = false, isMultiplayer = false;
    private boolean powerUpActive = false;
    private char direction1 = 'R', direction2 = 'L';
    private int score1 = 0, score2 = 0;
    private Random random;

    public SnakeGame(boolean multiplayer) {
        this.isMultiplayer = multiplayer;
        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        random = new Random();
        startGame();
    }

    private void startGame() {
        running = true;
        snake1 = new LinkedList<>();
        snake2 = new LinkedList<>();
        snake1.add(new Point(5, 5));
        if (isMultiplayer) snake2.add(new Point(15, 15));
        spawnFood();
        spawnPowerUp();
        gameTimer = new Timer(100, this);
        gameTimer.start();
        this.requestFocusInWindow(); // Fix for keyboard input issue
    }

    private void spawnFood() {
        food = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
    }

    private void spawnPowerUp() {
        powerUp = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
    }

    private void move(LinkedList<Point> snake, char direction) {
        Point head = snake.getFirst();
        Point newHead = switch (direction) {
            case 'U' -> new Point(head.x, head.y - 1);
            case 'D' -> new Point(head.x, head.y + 1);
            case 'L' -> new Point(head.x - 1, head.y);
            case 'R' -> new Point(head.x + 1, head.y);
            default -> head;
        };
        snake.addFirst(newHead);
        if (!newHead.equals(food)) {
            snake.removeLast();
        } else {
            spawnFood();
            if (snake == snake1) score1++;
            else score2++;
        }
    }

    private void checkCollision() {
        Point head1 = snake1.getFirst();
        if (head1.equals(powerUp)) {
            powerUpActive = true;
            spawnPowerUp();
        }
        if (head1.x < 0 || head1.x >= WIDTH || head1.y < 0 || head1.y >= HEIGHT) running = false;
        if (isMultiplayer) {
            Point head2 = snake2.getFirst();
            if (head2.equals(powerUp)) powerUpActive = true;
            if (head2.x < 0 || head2.x >= WIDTH || head2.y < 0 || head2.y >= HEIGHT) running = false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move(snake1, direction1);
            if (isMultiplayer) move(snake2, direction2);
            checkCollision();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.RED);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        g.setColor(Color.GREEN);
        for (Point p : snake1) g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        if (isMultiplayer) {
            g.setColor(Color.BLUE);
            for (Point p : snake2) g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }
        g.setColor(Color.WHITE);
        g.drawString("P1 Score: " + score1, 10, 20);
        if (isMultiplayer) g.drawString("P2 Score: " + score2, 200, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP -> direction1 = 'U';
            case KeyEvent.VK_DOWN -> direction1 = 'D';
            case KeyEvent.VK_LEFT -> direction1 = 'L';
            case KeyEvent.VK_RIGHT -> direction1 = 'R';
            case KeyEvent.VK_W -> direction2 = 'U';
            case KeyEvent.VK_S -> direction2 = 'D';
            case KeyEvent.VK_A -> direction2 = 'L';
            case KeyEvent.VK_D -> direction2 = 'R';
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            boolean isMultiplayer = JOptionPane.showConfirmDialog(null, "Multiplayer?", "Snake Game", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
            JFrame frame = new JFrame("Snake Game - " + (isMultiplayer ? "Multiplayer" : "Singleplayer"));
            SnakeGame game = new SnakeGame(isMultiplayer);
            frame.add(game);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
