package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.item;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.Level;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.Sfx;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.effect.EffectType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.EntityType;
import com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player.Valdyn;

/**
 * Big Potion item. Give all heart to the player.
 */
public final class PotionBig
        extends EntityItem
{
    /**
     * Constructor.
     * 
     * @param level The level reference.
     */
    public PotionBig(Level level)
    {
        super(level, EntityType.POTION_BIG, EffectType.TAKEN);
    }

    /*
     * EntityItem
     */

    @Override
    protected void onTaken(Valdyn entity)
    {
        entity.stats.fillHeart();
        Sfx.ITEM_POTION_BIG.play();
    }
}
