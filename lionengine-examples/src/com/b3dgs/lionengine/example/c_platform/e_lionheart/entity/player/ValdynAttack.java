package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.anim.AnimState;
import com.b3dgs.lionengine.anim.Animation;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.AppLionheart;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityAction;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeEntityCollisionTile;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.TypeState;
import com.b3dgs.lionengine.file.XmlNode;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.Movement;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
import com.b3dgs.lionengine.game.purview.Collidable;
import com.b3dgs.lionengine.game.purview.model.CollidableModel;
import com.b3dgs.lionengine.input.Keyboard;

/**
 * Handle the Valdyn attacks.
 */
final class ValdynAttack
{
    /** Valdyn reference. */
    private final Valdyn valdyn;
    /** Valdyn movement. */
    private final Movement movement;
    /** Shade surface. */
    private final SpriteAnimated shade;
    /** Attack data. */
    private final EnumMap<TypeValdynState, Set<Attack>> attacks;
    /** Animations shade list. */
    private final EnumMap<TypeValdynState, Animation> shades;
    /** List of starting frames when shades are enabled. */
    private final EnumMap<TypeValdynState, Integer> shadesEnabled;
    /** Attack collision with monster. */
    private final Collidable attackCollision;
    /** Attack state. */
    private TypeValdynState attack;
    /** Attack prepared. */
    private boolean attackPrepared;
    /** Attacking state. */
    private boolean attacking;
    /** Attacked state. */
    private boolean attacked;
    /** Shade can be played. */
    private boolean shadeCanBePlayed;
    /** Attack key state. */
    private boolean keyAttack;

    /**
     * Constructor.
     * 
     * @param valdyn The valdyn reference.
     * @param movement The movement reference.
     */
    ValdynAttack(Valdyn valdyn, Movement movement)
    {
        this.valdyn = valdyn;
        this.movement = movement;
        shade = Drawable.loadSpriteAnimated(Media.get(AppLionheart.ENTITIES_DIR, "players", "shade.png"), 7, 7);
        shade.load(false);
        shades = new EnumMap<>(TypeValdynState.class);
        shadesEnabled = new EnumMap<>(TypeValdynState.class);
        attacks = new EnumMap<>(TypeValdynState.class);
        attackCollision = new CollidableModel(valdyn);
        loadAttacks();
        addShadeAnimation(TypeValdynState.ATTACK_UP, 1);
        addShadeAnimation(TypeValdynState.ATTACK_HORIZONTAL, 1);
        addShadeAnimation(TypeValdynState.ATTACK_TURNING, 2);
        addShadeAnimation(TypeValdynState.ATTACK_JUMP, 1);
        addShadeAnimation(TypeValdynState.ATTACK_SLIDE, 1);
        addShadeAnimation(TypeValdynState.ATTACK_LIANA, 1);
    }

    /**
     * Add a shade animation for the sword attack effect.
     * 
     * @param state The state enum.
     * @param startAtFrame The frame index (relative to current animation) where it should start.
     */
    void addShadeAnimation(TypeValdynState state, int startAtFrame)
    {
        shades.put(state, valdyn.getDataAnimation("shade_" + state.getAnimationName()));
        shadesEnabled.put(state, Integer.valueOf(startAtFrame));
    }

    /**
     * Called when valdyn respawn.
     */
    void respawn()
    {
        attack = null;
        attacking = false;
        attackPrepared = false;
    }

    /**
     * Update the controls.
     * 
     * @param keyboard The keyboard reference.
     */
    void updateControl(Keyboard keyboard)
    {
        if (!valdyn.isDead())
        {
            keyAttack = valdyn.isEnabled(TypeEntityAction.ATTACK);
        }
        else
        {
            keyAttack = false;
        }
    }

    /**
     * Update the mirror.
     * 
     * @param mirror The mirror state.
     */
    void updateMirror(boolean mirror)
    {
        shade.setMirror(mirror);
    }

    /**
     * Update the shade animation.
     * 
     * @param extrp The extrapolation value.
     */
    void updateAnimationShade(double extrp)
    {
        final TypeState state = valdyn.status.getState();
        if (valdyn.status.stateChanged())
        {
            if (shades.containsKey(state))
            {
                shadeCanBePlayed = true;
            }
            else
            {
                shade.stopAnimation();
            }
        }
        if (shadeCanBePlayed && shadesEnabled.containsKey(state))
        {
            final int index = valdyn.getFrame() - valdyn.getDataAnimation(state.getAnimationName()).getFirst();
            if (index >= shadesEnabled.get(state).intValue())
            {
                shade.play(shades.get(state));
                shadeCanBePlayed = false;
            }
        }
        shade.updateAnimation(extrp);
    }

    /**
     * Update the attack action.
     */
    void updateActionAttack()
    {
        // Attack ended
        final boolean attackFinished = valdyn.getAnimState() == AnimState.FINISHED;
        final boolean fallAttack = valdyn.status.getState() == TypeValdynState.ATTACK_FALL;
        // Stop if attack finished and if no longer attacking, or fall attack (special case)
        if (attacking && attackFinished && !fallAttack || (!attacking || fallAttack) && !keyAttack)
        {
            updateActionAttackFinished();
        }
        if (keyAttack)
        {
            // Prepare attack
            if (!attacking && valdyn.isOnGround() && !valdyn.isSliding())
            {
                updateActionAttackPrepare(attackFinished);
            }
            updateActionAttackSelect(attackPrepared);
        }
        if (attack != null && valdyn.isOnGround() && !valdyn.isSliding())
        {
            movement.reset();
            valdyn.stopTimerFallen();
        }
    }

    /**
     * Update the states.
     * 
     * @return <code>true</code> if updated, <code>false</code> else.
     */
    boolean updateStates()
    {
        final boolean updated;
        if (attack != null)
        {
            valdyn.status.setState(attack);
            updated = true;
        }
        else
        {
            updated = false;
        }
        return updated;
    }

    /**
     * Update the collisions.
     */
    void updateCollisions()
    {
        // Stop attack if collide
        if (valdyn.status.collisionChangedFromTo(TypeEntityCollisionTile.NONE, TypeEntityCollisionTile.GROUND)
                || valdyn.status.collisionChangedFromTo(TypeEntityCollisionTile.NONE, TypeEntityCollisionTile.LIANA))
        {
            attack = null;
            attacking = false;
            attackPrepared = false;
        }

        if (valdyn.status.isState(TypeValdynState.CROUCH, TypeValdynState.ATTACK_DOWN_LEG,
                TypeValdynState.ATTACK_PREPARING_DOWN, TypeValdynState.ATTACK_PREPARED_DOWN))
        {
            valdyn.setCollision(valdyn.getCollisionData(TypeValdynCollision.CROUCH));
        }
        else if (valdyn.status.getState() == TypeValdynState.ATTACK_FALL)
        {
            valdyn.setCollision(valdyn.getCollisionData(TypeValdynCollision.ATTACK_FALL));
        }
        else
        {
            valdyn.setCollision(valdyn.getCollisionData(TypeValdynCollision.STAND));
        }
        attackCollision.setCollision(null);
        if (!valdyn.status.stateChanged() && valdyn.getAnimState() == AnimState.PLAYING)
        {
            updateAttackCollision();
        }
        attackCollision.updateCollision();

        if (valdyn.status.getCollision() == TypeEntityCollisionTile.GROUND && attack == TypeValdynState.ATTACK_FALL)
        {
            attacking = false;
        }
    }

    /**
     * Render the attack collisions.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    void render(Graphic g, CameraPlatform camera)
    {
        if (shade.getAnimState() == AnimState.PLAYING)
        {
            valdyn.renderAnim(g, shade, camera);
        }
    }

    /**
     * Render the attack collisions.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    void renderCollision(Graphic g, CameraGame camera)
    {
        attackCollision.renderCollision(g, camera);
    }

    /**
     * Get the attack state.
     * 
     * @return The attack state.
     */
    TypeValdynState getState()
    {
        return attack;
    }

    /**
     * Get the attack collision.
     * 
     * @return The attack collision.
     */
    Collidable getCollisionAttack()
    {
        return attackCollision;
    }

    /**
     * Check if valdyn is attacking.
     * 
     * @return <code>true</code> if attacking, <code>false</code> else.
     */
    boolean isAttacking()
    {
        return attacking;
    }

    /**
     * Update the prepare attack action.
     * 
     * @param attackFinished The attack finished state.
     */
    private void updateActionAttackPrepare(boolean attackFinished)
    {
        if (attackFinished
                && (attack == TypeValdynState.ATTACK_PREPARING_DOWN || attack == TypeValdynState.ATTACK_PREPARING))
        {
            attackPrepared = true;
        }
        if (attackPrepared)
        {
            computeAttackPrepare(TypeValdynState.ATTACK_PREPARED, TypeValdynState.ATTACK_PREPARED_DOWN);
        }
        else
        {
            computeAttackPrepare(TypeValdynState.ATTACK_PREPARING, TypeValdynState.ATTACK_PREPARING_DOWN);
        }
        attacking = false;
    }

    /**
     * List of implemented attacks.
     * 
     * @param attackPrepared <code>true</code> if attack need to be prepared, <code>false</code> else.
     */
    private void updateActionAttackSelect(boolean attackPrepared)
    {
        final boolean keyLeft = valdyn.isEnabled(TypeEntityAction.MOVE_LEFT);
        final boolean keyRight = valdyn.isEnabled(TypeEntityAction.MOVE_RIGHT);
        final boolean keyDown = valdyn.isEnabled(TypeEntityAction.MOVE_DOWN);
        final boolean keyUp = valdyn.isEnabled(TypeEntityAction.JUMP);
        final boolean mirror = valdyn.getMirror();
        final boolean goodWay = !mirror && keyLeft || mirror && keyRight;
        final boolean wrongWay = mirror && keyLeft || !mirror && keyRight;
        if (valdyn.isSliding())
        {
            setAttack(TypeValdynState.ATTACK_SLIDE);
        }
        else if (valdyn.isLiana())
        {
            setAttack(TypeValdynState.ATTACK_LIANA);
        }
        else if (!valdyn.isOnGround() && keyDown)
        {
            setAttack(TypeValdynState.ATTACK_FALL);
            attacked = false;
        }
        else if (!valdyn.isOnGround())
        {
            setAttack(TypeValdynState.ATTACK_JUMP);
        }
        else if (attackPrepared && valdyn.isOnGround() && keyDown && wrongWay)
        {
            setAttack(TypeValdynState.ATTACK_DOWN_LEG);
        }
        else if (attackPrepared && valdyn.isOnGround() && !keyDown && keyUp)
        {
            setAttack(TypeValdynState.ATTACK_UP);
        }
        else if (attackPrepared && valdyn.isOnGround() && !keyDown && wrongWay)
        {
            setAttack(TypeValdynState.ATTACK_HORIZONTAL);
        }
        else if (attackPrepared && valdyn.isOnGround() && !keyDown && goodWay)
        {
            setAttack(TypeValdynState.ATTACK_TURNING);
        }
        else
        {
            attacked = false;
        }
    }

    /**
     * Update the attack finished state.
     */
    private void updateActionAttackFinished()
    {
        boolean attackTurned = false;
        if (attack == TypeValdynState.ATTACK_TURNING)
        {
            valdyn.mirror(!valdyn.getMirror());
            valdyn.updateMirror();
            attackTurned = true;
        }
        if (keyAttack)
        {
            if (valdyn.isOnGround() && !valdyn.isSliding())
            {
                attack = TypeValdynState.ATTACK_PREPARED;
            }
            else
            {
                attack = null;
            }
            if (!attackTurned)
            {
                attackPrepared = true;
            }
        }
        else
        {
            attack = null;
            attackPrepared = false;
            attacked = false;
        }
        attacking = false;
    }

    /**
     * Update the collision during attack.
     */
    private void updateAttackCollision()
    {
        final Set<Attack> set = attacks.get(valdyn.status.getState());
        if (set != null)
        {
            for (final Attack attack : set)
            {
                if (valdyn.getFrameAnim() == attack.getFrame())
                {
                    attackCollision.setCollision(attack.getCollision());
                    break;
                }
            }
        }
    }

    /**
     * Compute the prepare attack state (function used for refactoring).
     * 
     * @param prepare Prepare state.
     * @param prepareDown Prepare down state.
     */
    private void computeAttackPrepare(TypeValdynState prepare, TypeValdynState prepareDown)
    {
        if (valdyn.isEnabled(TypeEntityAction.MOVE_DOWN))
        {
            attack = prepareDown;
        }
        else
        {
            attack = prepare;
        }
    }

    /**
     * Set the next attack. To be called only by {@link #updateActionAttack()}
     * 
     * @param nextAttack The next attack type.
     */
    private void setAttack(TypeValdynState nextAttack)
    {
        if (!attacked)
        {
            attack = nextAttack;
            attacking = true;
            attacked = true;
            attackPrepared = false;
        }
    }

    /**
     * Load all attacks data.
     */
    private void loadAttacks()
    {
        for (final XmlNode animation : valdyn.getDataRoot().getChildren("animation"))
        {
            final Set<Attack> set = new HashSet<>(1);
            for (final XmlNode attackNode : animation.getChildren("attack"))
            {
                final Attack attack = new Attack(attackNode);
                set.add(attack);
            }
            if (!set.isEmpty())
            {
                final String attackName = animation.readString("name").toUpperCase(Locale.ENGLISH);
                attacks.put(TypeValdynState.valueOf(attackName), set);
            }
        }
    }
}
