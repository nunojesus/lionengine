package com.b3dgs.lionengine.example.f_network;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Loader;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Sequence;

/**
 * Game loop designed to handle our little world.
 */
class Scene
        extends Sequence
{
    /** Native resolution. */
    private static final Resolution NATIVE = new Resolution(640, 480, 60);

    /** World server reference. */
    private final World<?> world;

    /**
     * Constructor.
     * 
     * @param loader The loader reference.
     */
    public Scene(Loader loader)
    {
        super(loader, Scene.NATIVE);
        setExtrapolated(true);

        final boolean server = false;

        if (server)
        {
            final WorldServer worldServer = new WorldServer(this);
            worldServer.startServer("Test", 7777, "Welcome !");
            world = worldServer;
        }
        else
        {
            final WorldClient worldClient = new WorldClient(this);
            worldClient.setName("Unnamed");
            worldClient.connect("127.0.0.1", 7777);
            world = worldClient;
        }
    }

    /*
     * Sequence
     */

    @Override
    protected void load()
    {
        world.loadFromFile(Media.get("level.dat"));
    }

    @Override
    protected void update(double extrp)
    {
        world.receiveMessages();
        world.update(extrp);
        world.sendMessages();
    }

    @Override
    protected void render(Graphic g)
    {
        world.render(g);
    }

    @Override
    protected void onTerminate(boolean hasNextSequence)
    {
        world.disconnect();
    }
}
