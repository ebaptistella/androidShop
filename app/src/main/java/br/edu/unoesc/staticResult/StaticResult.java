package br.edu.unoesc.staticResult;

/**
 * Created by eurides on 01/08/15.
 */
public enum StaticResult {

    RR_CAMERA(100), RR_MAPS_LATITUDE_LONGITUDE(110);

    private int value;

    StaticResult(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
