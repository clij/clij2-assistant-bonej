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
import net.haesleinhuepf.clij2.utilities.IsCategorized;
import org.bonej.plugins.ConnectedComponents;
import org.bonej.util.SkeletonUtils;
import org.scijava.plugin.Plugin;

@Plugin(type = CLIJMacroPlugin.class, name = "CLIJx_boneJSkeletonize3D")
public class BoneJSkeletonize3D extends AbstractCLIJ2Plugin implements CLIJMacroPlugin, CLIJOpenCLProcessor, OffersDocumentation, IsCategorized
{
    public BoneJSkeletonize3D() {
        super();
    }

    @Override
    public String getParameterHelpText() {
        return "Image input, ByRef Image destination";
    }

    @Override
    public boolean executeCL() {
        boolean result = bonejSkeletonize3D(getCLIJ2(), (ClearCLBuffer) (args[0]), (ClearCLBuffer) (args[1]));
        return result;
    }

    public static boolean bonejSkeletonize3D(CLIJ2 clij2, ClearCLBuffer input1, ClearCLBuffer output) {
        // pull image from GPU in ImageJ1 type
        ImagePlus input = clij2.pullBinary(input1);

        // process it using BoneJ
        ImagePlus result_imp = SkeletonUtils.getSkeleton(input);

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
        return "Apply BoneJ Skeletonize a binary image in 3D.";
    }

    @Override
    public String getAvailableForDimensions() {
        return "3D";
    }

    @Override
    public String getCategories() {
        return "Binary";
    }
}
