package MultiLevelMaze;

import com.almasb.fxgl.entity.*;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.gameplay.Level;

import java.util.ArrayList;
import java.util.Arrays;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.settings.GameSettings;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;

public class MultiLevelMazeMain extends GameApplication {
	Entity player;
	int levelCount = 0;
	simpleTranslateControl control;
	int[] startingPos = { 75, 20 };
	ArrayList<int[][]> maps = new ArrayList<int[][]>();
	ArrayList<Entity> tiles;

	@Override
	protected void initSettings(GameSettings settings) {
		// TODO Auto-generated method stub
		settings.setIntroEnabled(false);
		settings.setCloseConfirmation(false);
		settings.setMenuEnabled(false);
		settings.setTitle("MultiLevelMaze");
		settings.setVersion("");
		settings.setProfilingEnabled(false);
	}

	@Override
	protected void initPhysics() {
		PhysicsWorld physicsWorld = getPhysicsWorld();
		physicsWorld.addCollisionHandler(new CollisionHandler(MultiLevelMazeTypes.CIRCLE, MultiLevelMazeTypes.WALL) {
			@Override
			protected void onCollisionBegin(Entity circle, Entity wall) {
				circle.setPosition(startingPos[0], startingPos[1]);
			}
		});
		physicsWorld.addCollisionHandler(new CollisionHandler(MultiLevelMazeTypes.CIRCLE, MultiLevelMazeTypes.GOAL) {
			@Override
			protected void onCollisionBegin(Entity circle, Entity goal) {
				startingPos[0] = (int) circle.getX();
				startingPos[1] = (int) circle.getY();
				clearMap();
				levelCount++;
				if (levelCount < 3) {
					buildMap(maps.get(levelCount));
				} else {
					getGameWorld().removeEntity(player);
					getGameScene().addGameView(new EntityView(getAssetLoader().loadTexture("beautifulFace.jpg")));
					getAudioPlayer().playSound(getAssetLoader().loadSound("scream.wav"));
				}
			}
		});
	}

	protected void clearMap() {
		for (int i = 0; i < tiles.size(); i++) {
			getGameWorld().removeEntity(tiles.get(i));
		}
	}

	@Override
	protected void initGame() {
		getGameWorld().setEntityFactory(new MultiLevelMazeFactory());
		player = getGameWorld().spawn("player", 75, 20);
		control = player.getControl(simpleTranslateControl.class);
		int[][] map1 = { { 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1 }, { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 1 },
				{ 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 1, 1 }, { 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1 },
				{ 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 1 }, { 1, 1, 1, 0, 1, 1, 0, 0, 0, 0, 1, 1 },
				{ 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1 } };
		int[][] map2 = { { 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 2 }, { 1, 1, 1, 0, 0, 0, 0, 0, 1, 1, 0, 0 },
				{ 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0 }, { 1, 1, 1, 0, 1, 1, 0, 1, 1, 0, 0, 0 },
				{ 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 0 }, { 1, 0, 0, 0, 1, 1, 0, 1, 0, 0, 0, 0 },
				{ 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 1, 0 },
				{ 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0 } };
		int[][] map3 = { { 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0 }, { 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 0, 0 },
				{ 2, 0, 0, 0, 0, 1, 0, 0, 0, 1, 1, 1 }, { 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1 },
				{ 1, 1, 1, 1, 0, 1, 0, 1, 1, 1, 1, 1 }, { 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1, 1 },
				{ 1, 1, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1 }, { 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 1, 1 },
				{ 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 } };
		maps.add(map1);
		maps.add(map2);
		maps.add(map3);
		buildMap(maps.get(levelCount));

	}

	// MUST BE A 9X12 ARRAY FOR DEFAULT SCEEN SIZE
	protected void buildMap(int[][] map) {
		tiles = new ArrayList<Entity>();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				if (map[i][j] == 1) {
					tiles.add(getGameWorld().spawn("mapTile", j * 70, i * 70));
				} else if (map[i][j] == 2) {
					tiles.add(getGameWorld().spawn("goal", j * 70, i * 70));
				}
			}
		}
	}

	@Override
	protected void initInput() {
		Input input = getInput();
		input.addAction(new UserAction("up") {
			@Override
			public void onAction() {
				control.up();
			}
		}, KeyCode.UP);
		input.addAction(new UserAction("down") {
			@Override
			public void onAction() {
				control.down();
			}
		}, KeyCode.DOWN);
		input.addAction(new UserAction("left") {
			@Override
			public void onAction() {
				control.left();
			}
		}, KeyCode.LEFT);
		input.addAction(new UserAction("right") {
			@Override
			public void onAction() {
				control.right();
			}
		}, KeyCode.RIGHT);

	}

	public static void main(String[] args) {
		launch(args);
	}

}
