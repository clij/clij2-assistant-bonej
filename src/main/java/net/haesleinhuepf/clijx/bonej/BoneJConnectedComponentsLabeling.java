package net.haesleinhuepf.clijx.bonej;


import ij.ImagePlus;
import ij.ImageStack;
import ij.process.FloatProcessor;
import net.haesleinhuepf.clij.clearcl.ClearCLBuffer;
import net.haesleinhuepf.clij.macro.CLIJMacroPlugin;
import net.haesleinhuepf.clij.macro.CLIJOpenCLProcessor;
import net.haesleinhuepf.clij.macro.documentation.OffersDocumentation;
import net.haesleinhuepf.clij2.AbstractCLIJ2Plugin;
import net.haesleinhuepf.clij2.CLIJ2;
import net.haesleinhuepf.clij2.utilities.HasAuthor;
import net.haesleinhuepf.clij2.utilities.HasClassifiedInputOutput;
import net.haesleinhuepf.clij2.utilities.HasLicense;
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.bonej.plugins.ConnectedComponents;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_boneJConnectedComponentsLabeling")
public class BoneJConnectedComponentsLabeling extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized, HasClassifiedInputOutput
{
    public BoneJConnectedComponentsLabeling() {
        super();
    }

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = bonejConnectedComponentsLabeling(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]));
        return result;
    }

    public static boolean bonejConnectedComponentsLabeling(CLIJ2 clij2, ClearCLBuffer input1, ClearCLBuffer output) {
        // pull image from GPU in ImageJ1 type
        ImagePlus input = clij2.pullBinary(input1);

        // process it using BoneJ
        // See also https://github.com/bonej-org/BoneJ2/blob/8c8b5031fb98cfafaaff221a98cdfbc50573ffe3/Legacy/bonej/src/main/java/org/bonej/plugins/ConnectedComponents.java#L109
        int[][] result = new ConnectedComponents().run(input, ConnectedComponents.FORE);

        // convert result to ImageJ1
        ImageStack stack = new ImageStack(input.getWidth(), input.getHeight());
        for (int i = 0; i < result.length; i++) {
            FloatProcessor fp = new FloatProcessor(input.getWidth(), input.getHeight());
            float[] pixels = (float[]) fp.getPixels();
            for (int j = 0; j < pixels.length; j++) {
                pixels[j] = result[i][j];
            }
            stack.addSlice(fp);
        }
        ImagePlus result_imp = new ImagePlus("title", stack);

        // push result back
        ClearCLBuffer result_buffer = clij2.push(result_imp);

        // save it in the right place
        clij2.copy(result_buffer, output);

        // clean up
        result_buffer.close();

        return true;
    }

    @Override
    public String getDescription() {
        return "Apply BoneJ Connected Components to an image.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "2D, 3D";
    }

    @Override
    public String getCategories() {
        return "Labeling";
    }

    public static void main(String[] args) {
        CLIJ2 clij2 = CLIJ2.getInstance();

        ClearCLBuffer input = clij2.pushString("" +
                "0 0 0 1\n" +
                "1 0 0 1\n" +
                "\n" +
                "1 1 0 1\n" +
                "1 1 0 0");

        ClearCLBuffer output = clij2.create(input);

        bonejConnectedComponentsLabeling(clij2, input, output);

        clij2.print(output);

    }

    @Override
    public String getInputType() {
        return "Binary Image";
    }

    @Override
    public String getOutputType() {
        return "Label Image";
    }
}
