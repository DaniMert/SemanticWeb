import java.util.Date;

public class MatchData {
    private int id;
    private String team1;
    private String team2;
    private int tore1;
    private int tore2;
    private int zuschauer;
    private String location;
    private Date datum;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTeam1() {
        return team1;
    }

    public void setTeam1(String team1) {
        this.team1 = team1;
    }

    public String getTeam2() {
        return team2;
    }

    public void setTeam2(String team2) {
        this.team2 = team2;
    }

    public int getTore1() {
        return tore1;
    }

    public void setTore1(int tore1) {
        this.tore1 = tore1;
    }

    public int getTore2() {
        return tore2;
    }

    public void setTore2(int tore2) {
        this.tore2 = tore2;
    }

    public int getZuschauer() {
        return zuschauer;
    }

    public void setZuschauer(int zuschauer) {
        this.zuschauer = zuschauer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDatum() {
        return datum;
    }

    public void setDatum(Date datum) {
        this.datum = datum;
    }
}