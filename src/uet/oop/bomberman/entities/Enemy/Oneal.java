package uet.oop.bomberman.entities.Enemy;

import javafx.scene.image.Image;
import uet.oop.bomberman.GameEngine.BombermanGame;
import uet.oop.bomberman.GameEngine.GameManager;
import uet.oop.bomberman.entities.World.Bomber;
import uet.oop.bomberman.entities.World.Entity;
import uet.oop.bomberman.entities.World.Wall;
import uet.oop.bomberman.entities.World.Brick;
import uet.oop.bomberman.graphics.Sprite;

import java.util.*;

public class Oneal extends Enemy {
    private int randomMoveCounter = 0;
    private static final int RANDOM_MOVE_DURATION = 30; // Frames before changing random direction
    private int[] lastBomberPosition = null; // Last known position of Bomber (tileX, tileY)
    private int lastDirection = -1; // Last direction moved toward Bomber
    private int persistenceCounter = 0; // Frames to persist in last direction after losing Bomber
    private static final int PERSISTENCE_DURATION = 60; // Frames to continue in last direction
    private int stuckCounter = 0; // Counter to detect if Oneal is stuck
    private static final int STUCK_THRESHOLD = 60; // Frames to determine if stuck
    private int[] lastPosition = new int[]{0, 0}; // Last position to detect movement progress
    private List<Integer> preferredDirections = new ArrayList<>(); // Preferred directions to try

    public Oneal(int xUnit, int yUnit, Image img, GameManager game) {
        super(xUnit, yUnit, img, game);
        this.speed = 1; // Speed as set previously
        lastPosition[0] = x;
        lastPosition[1] = y;
    }

    @Override
    protected Image getDeathSprite() {
        switch (deathFrame) {
            case 0:
                return Sprite.oneal_dead.getFxImage();
            case 1:
                return Sprite.oneal_dead.getFxImage();
            case 2:
            default:
                return Sprite.oneal_dead.getFxImage();
        }
    }

    @Override
    protected void chooseSprite() {
        switch (direction) {
            case 0: // Up
            case 2: // Down
                img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, animate, 20).getFxImage();
                break;
            case 1: // Right
                img = Sprite.movingSprite(Sprite.oneal_right1, Sprite.oneal_right2, Sprite.oneal_right3, animate, 20).getFxImage();
                break;
            case 3: // Left
                img = Sprite.movingSprite(Sprite.oneal_left1, Sprite.oneal_left2, Sprite.oneal_left3, animate, 20).getFxImage();
                break;
        }
    }

    @Override
    protected void move() {
        // Check if Oneal is stuck
        if (Math.abs(x - lastPosition[0]) < speed && Math.abs(y - lastPosition[1]) < speed) {
            stuckCounter++;
        } else {
            stuckCounter = 0;
            lastPosition[0] = x;
            lastPosition[1] = y;
        }

        // Find Bomber using BFS across the entire map
        Bomber bomber = findBomberWithBFS();

        if (bomber != null && bomber.isAlive()) {
            // Bomber found, update last known position and reset persistence
            lastBomberPosition = new int[]{bomber.getX() / Sprite.SCALED_SIZE, bomber.getY() / Sprite.SCALED_SIZE};
            lastDirection = direction; // Store the direction we're moving toward Bomber
            persistenceCounter = 0;
            randomMoveCounter = 0;
        } else {
            // Bomber not found or not alive
            if (lastBomberPosition != null && persistenceCounter < PERSISTENCE_DURATION) {
                // Continue moving toward last known position of Bomber
                persistenceCounter++;
                // Predict Bomber's movement by extending last known position
                int predictedTileX = lastBomberPosition[0];
                int predictedTileY = lastBomberPosition[1];
                if (lastDirection != -1) {
                    switch (lastDirection) {
                        case 0: predictedTileY--; break; // Up
                        case 1: predictedTileX++; break; // Right
                        case 2: predictedTileY++; break; // Down
                        case 3: predictedTileX--; break; // Left
                    }
                    // Check if predicted position is walkable
                    if (isWalkable(predictedTileX, predictedTileY)) {
                        lastBomberPosition[0] = predictedTileX;
                        lastBomberPosition[1] = predictedTileY;
                    }
                }
                // Move toward predicted position
                moveTowardLastKnownPosition();
            } else {
                // No Bomber found and persistence duration exceeded, move randomly like Balloon
                lastBomberPosition = null; // Reset last known position
                lastDirection = -1;
                randomMoveCounter++;
                if (randomMoveCounter >= RANDOM_MOVE_DURATION) {
                    direction = random.nextInt(4); // Change direction every 30 frames
                    randomMoveCounter = 0;
                }
            }
        }

        // If stuck, try alternative directions
        if (stuckCounter >= STUCK_THRESHOLD) {
            tryAlternativeDirections();
            stuckCounter = 0;
        }

        // Attempt to move with smoother wall sliding
        attemptSmoothMove();
    }

    private void attemptSmoothMove() {
        double newX = x;
        double newY = y;

        switch (direction) {
            case 0: newY -= speed; break; // Up
            case 1: newX += speed; break; // Right
            case 2: newY += speed; break; // Down
            case 3: newX -= speed; break; // Left
        }

        // Check if primary direction is blocked
        if (canMove(newX, newY)) {
            x = (int) newX;
            y = (int) newY;
            preferredDirections.clear();
            preferredDirections.add(direction);
        } else {
            // Try alternative directions based on current direction
            if (preferredDirections.isEmpty()) {
                preferredDirections = getPreferredDirections(direction);
            }

            boolean moved = false;
            for (int altDirection : preferredDirections) {
                newX = x;
                newY = y;
                switch (altDirection) {
                    case 0: newY -= speed; break;
                    case 1: newX += speed; break;
                    case 2: newY += speed; break;
                    case 3: newX -= speed; break;
                }
                if (canMove(newX, newY)) {
                    x = (int) newX;
                    y = (int) newY;
                    direction = altDirection;
                    moved = true;
                    break;
                }
            }

            if (!moved) {
                // If no direction works, reverse direction or pick a new one
                tryAlternativeDirections();
            }
        }

        chooseSprite(); // Update sprite based on direction
    }

    private List<Integer> getPreferredDirections(int currentDirection) {
        List<Integer> directions = new ArrayList<>();
        // Prefer directions that keep Oneal moving along walls/corners
        switch (currentDirection) {
            case 0: // Up: prefer left or right
                directions.add(3); // Left
                directions.add(1); // Right
                directions.add(2); // Down (reverse)
                break;
            case 1: // Right: prefer up or down
                directions.add(0); // Up
                directions.add(2); // Down
                directions.add(3); // Left (reverse)
                break;
            case 2: // Down: prefer left or right
                directions.add(3); // Left
                directions.add(1); // Right
                directions.add(0); // Up (reverse)
                break;
            case 3: // Left: prefer up or down
                directions.add(0); // Up
                directions.add(2); // Down
                directions.add(1); // Right (reverse)
                break;
        }
        return directions;
    }

    private void tryAlternativeDirections() {
        List<Integer> possibleDirections = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i != direction) {
                double testX = x;
                double testY = y;
                switch (i) {
                    case 0: testY -= speed; break;
                    case 1: testX += speed; break;
                    case 2: testY += speed; break;
                    case 3: testX -= speed; break;
                }
                if (canMove(testX, testY)) {
                    possibleDirections.add(i);
                }
            }
        }

        if (!possibleDirections.isEmpty()) {
            direction = possibleDirections.get(random.nextInt(possibleDirections.size()));
            preferredDirections.clear();
            preferredDirections.add(direction);
        } else {
            // If completely stuck, reverse direction
            direction = (direction + 2) % 4;
            preferredDirections.clear();
            preferredDirections.add(direction);
        }
    }

    private void moveTowardLastKnownPosition() {
        if (lastBomberPosition == null) return;

        int currentTileX = x / Sprite.SCALED_SIZE;
        int currentTileY = y / Sprite.SCALED_SIZE;
        int targetTileX = lastBomberPosition[0];
        int targetTileY = lastBomberPosition[1];

        // Use BFS to find path to last known position
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        queue.add(new int[]{currentTileX, currentTileY});
        visited.add(currentTileX + "," + currentTileY);
        parent.put(currentTileX + "," + currentTileY, null);

        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};

        boolean found = false;
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int tileX = pos[0];
            int tileY = pos[1];

            if (tileX == targetTileX && tileY == targetTileY) {
                found = true;
                // Reconstruct path to determine direction
                String current = tileX + "," + tileY;
                while (parent.get(current) != null) {
                    String prev = parent.get(current);
                    String[] parts = prev.split(",");
                    int prevX = Integer.parseInt(parts[0]);
                    int prevY = Integer.parseInt(parts[1]);

                    if (prevX == currentTileX && prevY == currentTileY) {
                        if (tileX > prevX) direction = 1; // Right
                        else if (tileX < prevX) direction = 3; // Left
                        else if (tileY > prevY) direction = 2; // Down
                        else if (tileY < prevY) direction = 0; // Up
                        preferredDirections.clear();
                        preferredDirections.add(direction);
                        break;
                    }
                    current = prev;
                    tileX = prevX;
                    tileY = prevY;
                }
                break;
            }

            // Prioritize straighter paths by exploring directions in a preferred order
            List<Integer> directionOrder = getPreferredDirections(direction);
            directionOrder.add((direction + 2) % 4); // Add reverse direction last

            for (int i : directionOrder) {
                int newTileX = tileX + dx[i];
                int newTileY = tileY + dy[i];
                String key = newTileX + "," + newTileY;

                if (!visited.contains(key) && isWalkable(newTileX, newTileY)) {
                    visited.add(key);
                    queue.add(new int[]{newTileX, newTileY});
                    parent.put(key, tileX + "," + tileY);
                }
            }
        }

        if (!found) {
            // If no path to last known position, reset and move randomly
            lastBomberPosition = null;
            lastDirection = -1;
            randomMoveCounter = 0;
        }
    }

    private Bomber findBomberWithBFS() {
        int currentTileX = x / Sprite.SCALED_SIZE;
        int currentTileY = y / Sprite.SCALED_SIZE;

        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> parent = new HashMap<>();

        queue.add(new int[]{currentTileX, currentTileY});
        visited.add(currentTileX + "," + currentTileY);
        parent.put(currentTileX + "," + currentTileY, null);

        Bomber bomber = null;
        int bomberTileX = -1, bomberTileY = -1;

        int[] dx = {0, 1, 0, -1};
        int[] dy = {-1, 0, 1, 0};

        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int tileX = pos[0];
            int tileY = pos[1];

            // Check if Bomber is in this tile
            for (Entity entity : game.getEntities()) {
                if (entity instanceof Bomber && ((Bomber) entity).isAlive()) {
                    bomber = (Bomber) entity;
                    bomberTileX = bomber.getX() / Sprite.SCALED_SIZE;
                    bomberTileY = bomber.getY() / Sprite.SCALED_SIZE;
                    if (bomberTileX == tileX && bomberTileY == tileY) {
                        // Found Bomber, reconstruct path to determine direction
                        String current = tileX + "," + tileY;
                        while (parent.get(current) != null) {
                            String prev = parent.get(current);
                            String[] parts = prev.split(",");
                            int prevX = Integer.parseInt(parts[0]);
                            int prevY = Integer.parseInt(parts[1]);

                            if (prevX == currentTileX && prevY == currentTileY) {
                                if (tileX > prevX) direction = 1; // Right
                                else if (tileX < prevX) direction = 3; // Left
                                else if (tileY > prevY) direction = 2; // Down
                                else if (tileY < prevY) direction = 0; // Up
                                preferredDirections.clear();
                                preferredDirections.add(direction);
                                break;
                            }
                            current = prev;
                            tileX = prevX;
                            tileY = prevY;
                        }
                        return bomber;
                    }
                }
            }

            // Prioritize straighter paths in BFS
            List<Integer> directionOrder = getPreferredDirections(direction);
            directionOrder.add((direction + 2) % 4); // Add reverse direction last

            for (int i : directionOrder) {
                int newTileX = tileX + dx[i];
                int newTileY = tileY + dy[i];
                String key = newTileX + "," + newTileY;

                if (!visited.contains(key) && isWalkable(newTileX, newTileY)) {
                    visited.add(key);
                    queue.add(new int[]{newTileX, newTileY});
                    parent.put(key, tileX + "," + tileY);
                }
            }
        }

        return null; // Bomber not found
    }

    private boolean isWalkable(int tileX, int tileY) {
        if (tileX < 0 || tileX >= BombermanGame.WIDTH || tileY < 0 || tileY >= BombermanGame.HEIGHT) {
            return false;
        }

        for (Entity entity : game.getStillObjects()) {
            int entityTileX = entity.getX() / Sprite.SCALED_SIZE;
            int entityTileY = entity.getY() / Sprite.SCALED_SIZE;
            if (entityTileX == tileX && entityTileY == tileY &&
                    (entity instanceof Wall || entity instanceof Brick)) {
                return false;
            }
        }

        return true;
    }
}