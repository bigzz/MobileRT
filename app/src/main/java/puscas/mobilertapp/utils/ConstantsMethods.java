package puscas.mobilertapp.utils;

import android.view.View;
import java.util.logging.Logger;
import puscas.mobilertapp.Config;
import puscas.mobilertapp.DrawView;
import puscas.mobilertapp.MainActivity;
import puscas.mobilertapp.MainRenderer;
import puscas.mobilertapp.RenderTask;

/**
 * Utility class with the text constants for the names of methods.
 */
public final class ConstantsMethods {

    /**
     * The constant used when a method is about to return.
     */
    public static final String FINISHED = " finished";

    /**
     * The name of the {@link MainRenderer#setBitmap()}
     */
    public static final String SET_BITMAP = "setBitmap";

    /**
     * The name of the {@link DrawView#renderScene(Config, int, boolean)} method.
     */
    public static final String RENDER_SCENE = "renderScene";

    /**
     * The name of the {@link MainActivity#startRender(View)} method.
     */
    public static final String START_RENDER = "startRender";

    /**
     * The name of the {@link MainActivity#onDestroy()} method.
     */
    public static final String ON_DESTROY = "onDestroy";

    /**
     * The name of the {@link MainActivity#onDetachedFromWindow()} method.
     */
    public static final String ON_DETACHED_FROM_WINDOW = "onDetachedFromWindow";

    /**
     * The name of the {@link RenderTask#onCancelled()} and
     * {@link RenderTask#onCancelled(Void)} methods.
     */
    public static final String ON_CANCELLED = "onCancelled";

    /**
     * The name of the {@link RenderTask#timer} {@link Runnable} field.
     */
    public static final String TIMER = "RenderTask timer";

    /**
     * The name of the "getNames" in the {@link Enum} methods.
     */
    static final String GET_NAMES = "getNames";

    /**
     * The name of the {@link UtilsGL#run} methods.
     */
    static final String RUN = "run";

    /**
     * The {@link Logger} for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(ConstantsMethods.class.getName());

    /**
     * A private constructor in order to prevent instantiating this helper class.
     */
    private ConstantsMethods() {
        LOGGER.info("ConstantsMethods");
    }

}
