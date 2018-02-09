package platformerControl;

import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.entity.Entities;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.CollidableComponent;
import com.almasb.fxgl.entity.component.ViewComponent;
import com.almasb.fxgl.entity.view.EntityView;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.HitBox;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.settings.GameSettings;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.Texture;

import javafx.util.Duration;

public class NinjaPlatformerMain extends GameApplication {
	Entity ninja;
	int speed = 400;
	Texture idleAnimation, runRightAnimation, jumpRightAnimation, slideRightAnimation;
	int jumpAnimationTime = 800;
	PhysicsComponent physics;
	Input input;

	@Override
	protected void initSettings(GameSettings settings) {
		// settings.setIntroEnabled(true);
		settings.setTitle("PlatformerControlledMovement");
		settings.setVersion("");
		// settings.setCloseConfirmation(true);
	}

	@Override
	protected void initGame() {
		initPlayer();
		initGround();
		input.mockKeyPress(KeyCode.RIGHT);
		input.mockKeyRelease(KeyCode.RIGHT);
		getMasterTimer().runAtInterval(() -> {
			physics.setAngularVelocity(ninja.getRotationComponent().getValue() * -1);
		}, Duration.millis(1));
	}

	@Override
	protected void initPhysics() {
		PhysicsWorld physicsWorld = getPhysicsWorld();
		physicsWorld.setGravity(0, 15);
	}

	protected void initGround() {
		PhysicsComponent groundPhysics = new PhysicsComponent();
		FixtureDef fd = new FixtureDef();
		fd.setDensity(1f);
		fd.setFriction(1f);
		fd.setRestitution(.0000001f);
		groundPhysics.setFixtureDef(fd);
		groundPhysics.setBodyType(BodyType.STATIC);
		Entity ground = Entities.builder().bbox(new HitBox("Ground", BoundingShape.box(getWidth(), 1)))
				.at(0, getHeight() - 50).with(new CollidableComponent(true)).type(NinjaPlatformerTypes.GROUND)
				.with(groundPhysics).viewFromNode(new Rectangle(getWidth(), 5, Color.BLACK))
				.buildAndAttach(getGameWorld());
	}

	@Override
	protected void initInput() {
		jumpRightAnimation = getAssetLoader().loadTexture("ninjaJumpAnimation.png").toAnimatedTexture(10,
				Duration.millis(jumpAnimationTime));
		runRightAnimation = getAssetLoader().loadTexture("ninjaRunningAnimation.png").toAnimatedTexture(10,
				Duration.millis(500));
		slideRightAnimation = getAssetLoader().loadTexture("ninjaSlidingAnimation.png").toAnimatedTexture(10,
				Duration.millis(500));
		jumpRightAnimation.setScaleY(.5);
		runRightAnimation.setScaleY(.5);
		slideRightAnimation.setScaleY(.5);
		input = getInput();
		input.addAction(new UserAction("Jump") {
			@Override
			protected void onAction() {
				input.mockKeyRelease(KeyCode.UP);
				ninja.setViewWithBBox(jumpRightAnimation);
				physics.setVelocityY(-500);
				getMasterTimer().runOnceAfter(() -> {
					if (jumpRightAnimation.getScaleX() < 0) {
						idleAnimation.setScaleX(-.5);
						ninja.setViewWithBBox(idleAnimation);
					} else {
						input.mockKeyRelease(KeyCode.UP);
						idleAnimation.setScaleX(.5);
						ninja.setViewWithBBox(idleAnimation);
					}
				}, Duration.millis(jumpAnimationTime));
			}

		}, KeyCode.UP);
		input.addAction(new UserAction("Run Right") {
			@Override
			protected void onActionBegin() {
				input.mockKeyRelease(KeyCode.LEFT);
				runRightAnimation.setScaleX(.5);
				jumpRightAnimation.setScaleX(.5);
				slideRightAnimation.setScaleX(.5);
				ninja.setViewWithBBox(runRightAnimation);
			}

			@Override
			protected void onAction() {
				physics.setVelocityX(speed);
			}

			@Override
			protected void onActionEnd() {
				idleAnimation.setScaleX(.5);
				ninja.setViewWithBBox(idleAnimation);
				physics.setVelocityX(0);
			}
		}, KeyCode.RIGHT);
		input.addAction(new UserAction("Run Left") {
			@Override
			protected void onActionBegin() {
				input.mockKeyRelease(KeyCode.RIGHT);
				runRightAnimation.setScaleX(-.5);
				jumpRightAnimation.setScaleX(-.5);
				slideRightAnimation.setScaleX(-.5);
				ninja.setViewWithBBox(runRightAnimation);
			}

			@Override
			protected void onAction() {
				physics.setVelocityX(-1 * speed);
			}

			@Override
			protected void onActionEnd() {
				idleAnimation.setScaleX(-.5);
				ninja.setViewWithBBox(idleAnimation);
				physics.setVelocityX(0);
			}
		}, KeyCode.LEFT);
		
		
		/*input.addAction(new UserAction("slideRight") {
			@Override
			protected void onActionBegin() {
				input.mockKeyRelease(KeyCode.DOWN);
				ninja.setViewWithBBox(slideRightAnimation);
				if (slideRightAnimation.getScaleX() > 0)
					physics.setVelocityX(speed);
				if (slideRightAnimation.getScaleX() < 0)
					physics.setVelocityX(-1 * speed);
				getMasterTimer().runOnceAfter(() -> {
					ninja.setView(idleAnimation);
					physics.setVelocityX(0);

				}, Duration.millis(500));
			}
		}, KeyCode.DOWN);*/
	}

	protected void initPlayer() {
		physics = new PhysicsComponent();
		FixtureDef fd = new FixtureDef();
		fd.setDensity(.000001f);
		fd.setRestitution(.000001f);
		fd.setFriction(.01f);
		physics.setFixtureDef(fd);
		physics.setBodyType(BodyType.DYNAMIC);

		idleAnimation = getAssetLoader().loadTexture("ninjaIdleAnimation.png").toAnimatedTexture(10,
				Duration.millis(500));
		idleAnimation.setScaleX(.5);
		idleAnimation.setScaleY(.5);
		ninja = Entities.builder().at(getWidth() / 2, 0).with(physics).type(NinjaPlatformerTypes.NINJA)
				.viewFromNode(idleAnimation)
				.bbox(new HitBox("body", BoundingShape.box(idleAnimation.getFitWidth(), idleAnimation.getFitHeight())))
				.buildAndAttach(getGameWorld());
	}

	public static void main(String[] args) {
		launch(args);
	}

}
