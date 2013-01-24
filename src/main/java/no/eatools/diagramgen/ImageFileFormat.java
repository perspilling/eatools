package no.eatools.diagramgen;

/**
 * @author AB22273
 * @date 14.nov.2008
 * @since 14.nov.2008 09:53:53
 */
public enum ImageFileFormat {
    WMF (0),
    EMF (0),
    GIF (1),
    BMP (1),
    JPG (1),
    PNG (1);

    private final int raster;

    ImageFileFormat(int raster) {
        this.raster = raster;
    }

    public int isRaster() {
        return raster;
    }

    public String getFileExtension() {
        return "." + toString().toLowerCase();
    }
}
