package puscas.mobilertapp;

import java.util.logging.Logger;

import java8.util.J8Arrays;

import static puscas.mobilertapp.ConstantsMethods.GET_NAMES;

/**
 * The available shaders for the Ray Tracer engine.
 */
enum Shader {

    /**
     * The NoShadows shader.
     */
    NOSHADOWS("NoShadows"),

    /**
     * The Whitted shader.
     */
    WHITTED("Whitted"),

    /**
     * The PathTracer shader.
     */
    PATHTRACER("PathTracer"),

    /**
     * The DepthMap shader.
     */
    DEPTHMAP("DepthMap"),

    /**
     * The Diffuse shader.
     */
    DIFFUSE("Diffuse");

    /**
     * The {@link Logger} for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Shader.class.getName());

    /**
     * @see Shader#getName()
     */
    private final String name;

    /**
     * The constructor for this {@link Enum}.
     *
     * @param name The new shader for the Ray Tracer engine.
     */
    Shader(final String name) {
        this.name = name;
    }

    /**
     * Gets the name of the shader for the Ray Tracer engine.
     */
    String getName() {
        return this.name;
    }

    /**
     * Gets the names of all available shaders.
     */
    static String[] getNames() {
        LOGGER.info(GET_NAMES);

        return J8Arrays.stream(values())
                .map(Shader::getName)
                .toArray(String[]::new);
    }
}
