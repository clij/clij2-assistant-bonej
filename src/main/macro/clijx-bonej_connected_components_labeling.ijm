
open("C:/structure/data/blobs.tif");
run("CLIJ2 Macro Extensions", "cl_device=");

// median
image1 = getTitle();
Ext.CLIJ2_push(image1);

// Threshold
Ext.CLIJ2_thresholdOtsu(image1, image2);

// Connected Components labeling
Ext.CLIJx_boneJConnectedComponentsLabeling(image2, image3);
Ext.CLIJ2_pull(image3);
