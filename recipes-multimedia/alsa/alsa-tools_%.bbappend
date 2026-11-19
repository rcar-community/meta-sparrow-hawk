# hdajackretask is a GTK3 GUI tool that is not used on the target. Disable it
# to drop the gtk+3 build dependency.
PACKAGECONFIG:remove:sparrow-hawk = "hdajackretask"
