package pappu.com.cameraappopengl.datamodel;


public enum Orientation {
    LandscapeUp(0),
    PotraitUp(1),
    LandscapeDown(2),
    PotraitDown(3),
    Flat(4);

    private int value = 0;
    private Orientation(int val) {this.value = val;}
    public final int value() {return this.value;}
}
