PACKAGECONFIG:append:sparrow-hawk = " gtk"
PACKAGECONFIG:append:sparrow-hawk = " opencl"
PACKAGECONFIG:append:sparrow-hawk = " dnn"

# Not used on the target and increase the build time
PACKAGECONFIG:remove:sparrow-hawk = "samples gapi gphoto2"
