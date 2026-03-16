public class DataRecord {
    private String title;
    private String console;
    private String genre;
    private String publisher;
    private String developer;
    private double totalSales;
    private double naSales;
    private double jpSales;
    private double palSales;
    private double otherSales;
    private String releaseDate;

    public DataRecord(String title, String console, String genre, String publisher,
                      String developer, double totalSales, double naSales,
                      double jpSales, double palSales, double otherSales, String releaseDate) {
        this.title       = title;
        this.console     = console;
        this.genre       = genre;
        this.publisher   = publisher;
        this.developer   = developer;
        this.totalSales  = totalSales;
        this.naSales     = naSales;
        this.jpSales     = jpSales;
        this.palSales    = palSales;
        this.otherSales  = otherSales;
        this.releaseDate = releaseDate;
    }

    public String getTitle()       { return title; }
    public String getConsole()     { return console; }
    public String getGenre()       { return genre; }
    public String getPublisher()   { return publisher; }
    public String getDeveloper()   { return developer; }
    public double getTotalSales()  { return totalSales; }
    public double getNaSales()     { return naSales; }
    public double getJpSales()     { return jpSales; }
    public double getPalSales()    { return palSales; }
    public double getOtherSales()  { return otherSales; }
    public String getReleaseDate() { return releaseDate; }
}
