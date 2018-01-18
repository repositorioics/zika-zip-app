package ni.org.ics.zip.appmovil.domain;

/**
 * Created by A.L. on 23/11/2017.
 */

public class Provider extends BaseMetaData {

    private int id;
    private String name;
    private String us;
    private String speciality;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }


    public String getSpeciality() {
        return speciality;
    }
    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getUs() {
        return us;
    }

    public void setUs(String us) {
        this.us = us;
    }


}
