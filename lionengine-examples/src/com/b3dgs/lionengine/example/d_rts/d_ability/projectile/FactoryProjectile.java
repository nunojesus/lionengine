package com.b3dgs.lionengine.example.d_rts.d_ability.projectile;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.example.d_rts.d_ability.ProjectileType;
import com.b3dgs.lionengine.game.SetupSurfaceGame;
import com.b3dgs.lionengine.game.projectile.FactoryProjectileGame;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Factory projectile implementation.
 */
public final class FactoryProjectile
        extends FactoryProjectileGame<ProjectileType, Projectile, SetupSurfaceGame>
{
    /** Directory name from our resources directory containing our entities. */
    private static final String PROJECTILE_PATH = "projectiles";

    /**
     * Constructor.
     */
    public FactoryProjectile()
    {
        super(ProjectileType.class);
        loadAll(ProjectileType.values());
    }

    /*
     * FactoryProjectileGame
     */

    @Override
    public Projectile createProjectile(ProjectileType type, int id, int frame)
    {
        switch (type)
        {
            case SPEAR:
                return new Spear(getSetup(ProjectileType.SPEAR), id, frame);
            default:
                throw new LionEngineException("Projectile not found: " + type);
        }
    }

    @Override
    protected SetupSurfaceGame createSetup(ProjectileType id)
    {
        return new SetupSurfaceGame(new ConfigurableModel(), Media.get(FactoryProjectile.PROJECTILE_PATH, id + ".xml"),
                false);
    }
}
