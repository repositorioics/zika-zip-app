package ni.org.ics.zip.appmovil.parsers;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import java.util.Date;

/**
 * Created by FIRSTICT on 10/6/2016.
 * V1.0
 */
public class Zp01StudyEntrySectionAtoDXml{

    @Element(required=true)
    private Date seaVdate;
    @Element(required=false)
    private Date seaPtdate;
    @Element(required=false)
    private String seaTresults;
    @Element(required=false)
    private Date seaLmpdate;
    @Element(required=false)
    private String seaLmpunknown;
    @Element(required=false)
    private Integer seaGaWeek;
    @Element(required=false)
    private Integer seaGaDay;
    @Element(required=false)
    private Date seaEddLmp;
    @Element(required=false)
    private String seaTriultrasound;
    @Element(required=false)
    private String seaUltravailable;
    @Element(required=false)
    private String seaUltraDay;
    @Element(required=false)
    private String seaUltraMonth;
    @Element(required=false)
    private String seaUltraYear;
    @Element(required=false)
    private Integer seaAgweeks;
    @Element(required=false)
    private Integer seaAgdays;
    @Element(required=false)
    private Date seaEdd;
    @Element(required=false)
    private String seaEddUsed;
    @Element(required=false)
    private Float seaPreWt;
    @Element(required=false)
    private String seaPrewtUnit;
    @Element(required=false)
    private Float seaCurHt;
    @Element(required=false)
    private String seaCurhtUnit;
    @Element(required=false)
    private Float seaMotherWt;
    @Element(required=false)
    private String seaMotherwtUnit;
    @Element(required=false)
    private Float seaHem;
    @Element(required=false)
    private Integer seaSystolic;
    @Element(required=false)
    private Integer seaDiastolic;
    @Element(required=false)
    private Float seaTemp;
    @Element(required=false)
    private String seaTmpUnit;
    @Element(required=false)
    private String seaCity;
    @Element(required=false)
    private String seaState;
    @Element(required=false)
    private String seaCountry;
    @Element(required=false)
    private String seaLive;
    @Element(required=false)
    private Integer seaAgeLeave;
    @Element(required=false)
    private String seaLeavena;
    @Element(required=false)
    private String seaMstatus;
    @Element(required=false)
    private String seaRace;
    @Element(required=false)
    private String seaEthnicityOther;
    @Element(required=false)
    private String seaDegreeYou;
    @Element(required=false)
    private Float seaYdegreeYears;
    @Element(required=false)
    private String seaDegreeSpouse;
    @Element(required=false)
    private Float seaSdegreeYears;

    @Element(required=false)
    private String question1;
    @Element(required=false)
    private String question2;
    @Element(required=false)
    private String note1;
    @Element(required=false)
    private String note2;
    @Element(required=false)
    private String note3;
    @Element(required=false)
    private String group1;
    @Element(required=false)
    private String group2;
    @Element(required=false)
    private String group3;
    @Element(required=false)
    private String group4;
    @Element(required=false)
    private String group5;
    @Element(required=false)
    private String group6;
    @Element(required=false)
    private String group7;
    @Element(required=false)
    private String group8;
    @Element(required=false)
    private String group9;
    @Element(required=false)
    private String group10;
    @Element(required=false)
    private String group11;
    @Element(required=false)
    private String group12;
    @Element(required=false)
    private String group13;
    @Element(required=false)
    private String group14;
    @Element(required=false)
    private String group15;
    @Element(required=false)
    private String group16;

    @Attribute
    private String id;
    @Element(required=false)
    private Meta meta;

    @Element(required=false)
    private String start;
    @Element(required=false)
    private String end;
    @Element(required=false)
    private String deviceid;
    @Element(required=false)
    private String simserial;
    @Element(required=false)
    private String phonenumber;
    @Element(required=false)
    private String imei;
    @Element(required=false)
    private Date today;

    public Date getSeaVdate() {
        return seaVdate;
    }

    public Date getSeaPtdate() {
        return seaPtdate;
    }

    public String getSeaTresults() {
        return seaTresults;
    }

    public Date getSeaLmpdate() {
        return seaLmpdate;
    }

    public String getSeaLmpunknown() {
        return seaLmpunknown;
    }

    public Integer getSeaGaWeek() {
        return seaGaWeek;
    }

    public Integer getSeaGaDay() {
        return seaGaDay;
    }

    public Date getSeaEddLmp() {
        return seaEddLmp;
    }

    public String getSeaTriultrasound() {
        return seaTriultrasound;
    }

    public String getSeaUltravailable() {
        return seaUltravailable;
    }

    public String getSeaUltraDay() {
        return seaUltraDay;
    }

    public String getSeaUltraMonth() {
        return seaUltraMonth;
    }

    public String getSeaUltraYear() {
        return seaUltraYear;
    }

    public Integer getSeaAgweeks() {
        return seaAgweeks;
    }

    public Integer getSeaAgdays() {
        return seaAgdays;
    }

    public Date getSeaEdd() {
        return seaEdd;
    }

    public String getSeaEddUsed() {
        return seaEddUsed;
    }

    public Float getSeaPreWt() {
        return seaPreWt;
    }

    public String getSeaPrewtUnit() {
        return seaPrewtUnit;
    }

    public Float getSeaCurHt() {
        return seaCurHt;
    }

    public String getSeaCurhtUnit() {
        return seaCurhtUnit;
    }

    public Float getSeaMotherWt() {
        return seaMotherWt;
    }

    public String getSeaMotherwtUnit() {
        return seaMotherwtUnit;
    }

    public Float getSeaHem() {
        return seaHem;
    }

    public Integer getSeaSystolic() {
        return seaSystolic;
    }

    public Integer getSeaDiastolic() {
        return seaDiastolic;
    }

    public Float getSeaTemp() {
        return seaTemp;
    }

    public String getSeaTmpUnit() {
        return seaTmpUnit;
    }

    public String getSeaCity() {
        return seaCity;
    }

    public String getSeaState() {
        return seaState;
    }

    public String getSeaCountry() {
        return seaCountry;
    }

    public String getSeaLive() {
        return seaLive;
    }

    public Integer getSeaAgeLeave() {
        return seaAgeLeave;
    }

    public String getSeaLeavena() {
        return seaLeavena;
    }

    public String getSeaMstatus() {
        return seaMstatus;
    }

    public String getSeaRace() {
        return seaRace;
    }

    public String getSeaEthnicityOther() {
        return seaEthnicityOther;
    }

    public String getSeaDegreeYou() {
        return seaDegreeYou;
    }

    public Float getSeaYdegreeYears() {
        return seaYdegreeYears;
    }

    public String getSeaDegreeSpouse() {
        return seaDegreeSpouse;
    }

    public Float getSeaSdegreeYears() {
        return seaSdegreeYears;
    }

    //movil y metadata
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Meta getMeta() {
        return meta;
    }
    public void setMeta(Meta meta) {
        this.meta = meta;
    }
    public String getStart() {
        return start;
    }
    public void setStart(String start) {
        this.start = start;
    }
    public String getEnd() {
        return end;
    }
    public void setEnd(String end) {
        this.end = end;
    }
    public String getDeviceid() {
        return deviceid;
    }
    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }
    public String getSimserial() {
        return simserial;
    }
    public void setSimserial(String simserial) {
        this.simserial = simserial;
    }
    public String getPhonenumber() {
        return phonenumber;
    }
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
    public String getImei() {
        return imei;
    }
    public void setImei(String imei) {
        this.imei = imei;
    }
    public Date getToday() {
        return today;
    }
    public void setToday(Date today) {
        this.today = today;
    }
}
