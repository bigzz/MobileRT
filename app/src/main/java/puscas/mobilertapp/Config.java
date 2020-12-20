package puscas.mobilertapp;

import java.util.Locale;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;

/**
 * The configurator for the Ray Tracer engine.
 */
public final class Config {

    /**
     * The {@link Logger} for this class.
     */
    private static final Logger LOGGER = Logger.getLogger(Config.class.getName());

    /**
     * The scene.
     *
     * @see Config#getScene()
     */
    private final int scene;

    /**
     * The shader.
     *
     * @see Config#getShader()
     */
    private final int shader;

    /**
     * The accelerator.
     *
     * @see Config#getAccelerator()
     */
    private final int accelerator;

    /**
     * The objFilePath.
     *
     * @see Config#getObjFilePath()
     */
    private final String objFilePath;

    /**
     * The matFilePath.
     *
     * @see Config#getMatFilePath()
     */
    private final String matFilePath;

    /**
     * The camFilePath.
     *
     * @see Config#getCamFilePath()
     */
    private final String camFilePath;

    /**
     * The configSamples.
     *
     * @see Config#getConfigSamples
     */
    private final ConfigSamples configSamples;

    /**
     * The configResolution.
     *
     * @see Config#getConfigResolution()
     */
    private final ConfigResolution configResolution;

    /**
     * The number of threads.
     *
     * @see Config#getThreads()
     */
    private final int threads;

    /**
     * Whether the Ray Tracing engine should render a preview frame.
     *
     * @see Config#shouldRasterize()
     */
    private final boolean rasterize;

    /**
     * A private constructor to force the usage of the {@link Config.Builder}.
     *
     * @param builder The {@link Config.Builder} for this class.
     */
    private Config(@Nonnull final Config.Builder builder) {
        LOGGER.info("Config");

        this.scene = builder.getScene();
        this.shader = builder.getShader();
        this.accelerator = builder.getAccelerator();
        this.configResolution = builder.getConfigResolution();
        this.configSamples = builder.getConfigSamples();
        this.objFilePath = builder.getObjFilePath();
        this.matFilePath = builder.getMatFilePath();
        this.camFilePath = builder.getCamFilePath();
        this.threads = builder.getThreads();
        this.rasterize = builder.shouldRasterize();
    }


    /**
     * Gets the index of the scene.
     */
    @Contract(pure = true)
    public int getScene() {
        return this.scene;
    }

    /**
     * Gets the index of the shader.
     */
    @Contract(pure = true)
    public int getShader() {
        return this.shader;
    }

    /**
     * Gets the index of the acceleration structure.
     */
    @Contract(pure = true)
    public int getAccelerator() {
        return this.accelerator;
    }


    /**
     * Gets the path to the OBJ file containing the geometry of the scene.
     */
    @Contract(pure = true)
    @Nonnull
    String getObjFilePath() {
        return this.objFilePath;
    }

    /**
     * Gets the path to the MTL file containing the materials of the scene.
     */
    @Contract(pure = true)
    @Nonnull
    String getMatFilePath() {
        return this.matFilePath;
    }

    /**
     * Gets the path to the CAM file containing the camera in the scene.
     */
    @Contract(pure = true)
    @Nonnull
    String getCamFilePath() {
        return this.camFilePath;
    }

    /**
     * Gets the configuration for the number of samples.
     */
    @Contract(pure = true)
    @Nonnull
    ConfigSamples getConfigSamples() {
        return this.configSamples;
    }

    /**
     * Gets the configuration for the resolution of the image plane.
     */
    @Contract(pure = true)
    @Nonnull
    ConfigResolution getConfigResolution() {
        return this.configResolution;
    }

    /**
     * Gets the number of threads to be used in the Ray Tracer.
     *
     * @return The number of threads to be used in the Ray Tracer.
     */
    @Contract(pure = true)
    int getThreads() {
        return this.threads;
    }

    /**
     * Gets whether the Ray Tracing engine should render a preview frame.
     *
     * @return Whether the Ray Tracing engine should render a preview frame.
     */
    @Contract(pure = true)
    boolean shouldRasterize() {
        return this.rasterize;
    }


    /**
     * The builder for this class.
     */
    static final class Builder {

        /**
         * The {@link Logger} for this class.
         */
        private static final Logger LOGGER_BUILDER = Logger.getLogger(
            Config.Builder.class.getName());

        /**
         * The scene.
         *
         * @see Config.Builder#withScene(int)
         */
        private int scene = 0;

        /**
         * The shader.
         *
         * @see Config.Builder#withShader(int)
         */
        private int shader = 0;

        /**
         * The accelerator.
         *
         * @see Config.Builder#withAccelerator(int)
         */
        private int accelerator = 0;

        /**
         * The path to the OBJ file.
         */
        private String objFilePath = "";

        /**
         * The path to the MTL file.
         */
        private String matFilePath = "";

        /**
         * The path to the CAM file.
         */
        private String camFilePath = "";

        /**
         * The configSamples.
         *
         * @see Config.Builder#getConfigSamples
         */
        private ConfigSamples configSamples = new ConfigSamples.Builder()
            .withSamplesPixel(0)
            .withSamplesLight(0)
            .build();

        /**
         * The configResolution.
         *
         * @see Config#getConfigResolution()
         */
        private ConfigResolution configResolution = new ConfigResolution.Builder()
            .withWidth(0)
            .withHeight(0)
            .build();

        /**
         * The number of threads.
         *
         * @see Config#getThreads()
         */
        private int threads = 1;

        /**
         * Whether the Ray Tracing engine should render a preview frame.
         *
         * @see Config#shouldRasterize()
         */
        private boolean rasterize = false;

        /**
         * Sets the scene of {@link Config}.
         *
         * @param scene The new value for the {@link Config#scene} field.
         * @return The builder with {@link Config.Builder#scene} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withScene(final int scene) {
            final String message = String.format(Locale.US, "withScene: %d", scene);
            LOGGER_BUILDER.info(message);

            this.scene = scene;
            return this;
        }

        /**
         * Sets the shader of {@link Config}.
         *
         * @param shader The new value for the {@link Config#shader} field.
         * @return The builder with {@link Config.Builder#shader} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withShader(final int shader) {
            final String message = String.format(Locale.US, "withShader: %d", shader);
            LOGGER_BUILDER.info(message);

            this.shader = shader;
            return this;
        }

        /**
         * Sets the {@link Config#accelerator}.
         *
         * @param accelerator The new value for the {@link Config#accelerator} field.
         * @return The builder with {@link Config.Builder#accelerator} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withAccelerator(final int accelerator) {
            final String message = String.format(Locale.US, "withAccelerator: %d", accelerator);
            LOGGER_BUILDER.info(message);

            this.accelerator = accelerator;
            return this;
        }

        /**
         * Sets the resolution of {@link Config}.
         *
         * @param configResolution The new value for the {@link Config#configResolution} field.
         * @return The builder with {@link Config.Builder#configResolution} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withConfigResolution(final ConfigResolution configResolution) {
            LOGGER_BUILDER.info("withConfigResolution");

            this.configResolution = configResolution;
            return this;
        }

        /**
         * Sets the path to the OBJ file of {@link Config}.
         *
         * @param objFilePath The new value for the {@link Config#objFilePath} field.
         * @return The builder with {@link Config.Builder#objFilePath} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withObj(@Nonnull final String objFilePath) {
            final String message = String.format(Locale.US, "withOBJ: %s", objFilePath);
            LOGGER_BUILDER.info(message);

            this.objFilePath = objFilePath;
            return this;
        }

        /**
         * Sets the path to the MTL file of {@link Config}.
         *
         * @param matFilePath The new value for the {@link Config#matFilePath} field.
         * @return The builder with {@link Config.Builder#matFilePath} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withMaterial(@Nonnull final String matFilePath) {
            final String message = String.format(Locale.US, "withMAT: %s", matFilePath);
            LOGGER_BUILDER.info(message);

            this.matFilePath = matFilePath;
            return this;
        }

        /**
         * Sets the path to the CAM file of {@link Config}.
         *
         * @param camFilePath The new value for the {@link Config#camFilePath} field.
         * @return The builder with {@link Config.Builder#camFilePath} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withCamera(@Nonnull final String camFilePath) {
            final String message = String.format(Locale.US, "withCAM: %s", camFilePath);
            LOGGER_BUILDER.info(message);

            this.camFilePath = camFilePath;
            return this;
        }

        /**
         * Sets the number of samples to be used in the {@link Config}.
         *
         * @param configSamples The new value for the {@link Config#configSamples} field.
         * @return The builder with {@link Config.Builder#camFilePath} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withConfigSamples(@Nonnull final ConfigSamples configSamples) {
            final String message = String.format(Locale.US, "withConfigSamples: %s",
                configSamples);
            LOGGER_BUILDER.info(message);

            this.configSamples = configSamples;
            return this;
        }

        /**
         * Sets the number of threads of {@link Config}.
         *
         * @param threads The new value for the {@link Config#threads} field.
         * @return The builder with {@link Config.Builder#threads} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withThreads(final int threads) {
            LOGGER_BUILDER.info("withThreads");

            this.threads = threads;
            return this;
        }

        /**
         * Sets whether the Ray Tracing engine should render a preview frame.
         *
         * @param rasterize The new value for the {@link Config#rasterize} field.
         * @return The builder with {@link Config.Builder#rasterize} already set.
         */
        @Contract("_ -> this")
        @Nonnull
        Config.Builder withRasterize(final boolean rasterize) {
            LOGGER_BUILDER.info("withRasterize");

            this.rasterize = rasterize;
            return this;
        }


        /**
         * Builds a new instance of {@link Config}.
         *
         * @return A new instance of {@link Config}.
         */
        @Contract(" -> new")
        @Nonnull
        Config build() {
            LOGGER_BUILDER.info("build");

            return new Config(this);
        }


        /**
         * Gets the scene index.
         *
         * @return The scene index.
         */
        @Contract(pure = true)
        int getScene() {
            return this.scene;
        }

        /**
         * Gets the shader index.
         *
         * @return The shader index.
         */
        @Contract(pure = true)
        int getShader() {
            return this.shader;
        }

        /**
         * Gets the accelerator index.
         *
         * @return The accelerator index.
         */
        @Contract(pure = true)
        int getAccelerator() {
            return this.accelerator;
        }

        /**
         * Gets the path to the OBJ file.
         *
         * @return The path to the OBJ file.
         */
        @Contract(pure = true)
        String getObjFilePath() {
            return this.objFilePath;
        }

        /**
         * Gets the path to the MTL file.
         *
         * @return The path to the MTL file.
         */
        @Contract(pure = true)
        String getMatFilePath() {
            return this.matFilePath;
        }

        /**
         * Gets the path to the CAM file.
         *
         * @return The path to the CAM file.
         */
        @Contract(pure = true)
        String getCamFilePath() {
            return this.camFilePath;
        }

        /**
         * Gets the configuration for the number of samples.
         *
         * @return The configuration for the number of samples.
         */
        @Contract(pure = true)
        ConfigSamples getConfigSamples() {
            return this.configSamples;
        }

        /**
         * Gets the configuration for the resolution of the image plane in the
         * {@link android.graphics.Bitmap}.
         *
         * @return The configuration for the resolution of the image plane in the
         * {@link android.graphics.Bitmap}.
         */
        @Contract(pure = true)
        ConfigResolution getConfigResolution() {
            return this.configResolution;
        }

        /**
         * Gets the number of threads to be used in the Ray Tracer.
         *
         * @return The number of threads to be used in the Ray Tracer.
         */
        @Contract(pure = true)
        int getThreads() {
            return this.threads;
        }

        /**
         * Gets whether the Ray Tracing engine should render a preview frame.
         *
         * @return Whether the Ray Tracing engine should render a preview frame.
         */
        @Contract(pure = true)
        boolean shouldRasterize() {
            return this.rasterize;
        }

    }

}
