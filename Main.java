import java.io.*;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.google.gson.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class Main {

    static HashMap<String, Integer> kapazitaeten = new HashMap<>();
    static HashMap<Integer, MatchData> matchDataHashMap = new HashMap<>();

    public static void main(String[] args) throws IOException, ParseException {

        File input = new File("/CachedData/stadien-raw.html");
        Document doc = Jsoup.parse(input, "UTF-8", "http://www.transfermarkt.de");
        System.out.println(doc.select("table.items > tbody > tr").size());
        for (Element element : doc.select("table.items > tbody > tr")) {
            String verein = element.select(".inline-table tr").get(1).text();
            Integer kapazitaet = Integer.parseInt(element.select("td.rechts").get(0).text().replace(".", ""));
            kapazitaeten.put(verein, kapazitaet);
        }

        //Gson gson = new GsonBuilder().create();
        for (String verein : kapazitaeten.keySet()) {
            System.out.println("Verein: " + verein + " - Kapazität: " + kapazitaeten.get(verein));

        }
        Gson gson = new Gson();
        String json = gson.toJson(kapazitaeten);
        System.out.println(json);


        File folder = new File("/CachedData/extractedMatchdata/2016_v1");
        if (folder != null && folder.isDirectory()) {

            for (final File file : folder.listFiles((dir, name) -> name.endsWith(".json"))) {

                JsonStreamParser parser = new JsonStreamParser(new FileReader(file));

                while (parser.hasNext()) {
                    JsonElement object = parser.next();
                    MatchData match = new MatchData();

                    int id = object.getAsJsonObject().get("MatchID").getAsInt();
                    match.setId(id);

                    match.setTeam1(object.getAsJsonObject().get("TeamName1").getAsString());
                    match.setTeam2(object.getAsJsonObject().get("TeamName2").getAsString());
                    match.setTore1(object.getAsJsonObject().get("MatchResults1").getAsInt());
                    match.setTore2(object.getAsJsonObject().get("MatchResults2").getAsInt());

                    JsonElement location = object.getAsJsonObject().get("LocationCity");
                    JsonElement zuschauer = object.getAsJsonObject().get("NumberOfViewers");
                    if (location != JsonNull.INSTANCE && zuschauer != JsonNull.INSTANCE) {
                        match.setLocation(location.getAsString());
                        match.setZuschauer(zuschauer.getAsInt());

                    }
                    String zeit = object.getAsJsonObject().get("Zeitpunkt").getAsString();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    match.setDatum(formatter.parse(zeit));
                    matchDataHashMap.put(id, match);


                }
            }
        }

        System.out.println("Anzahl der Datensätze: " + matchDataHashMap.size());

        for (MatchData match : matchDataHashMap.values()) {

            Format dateFormat = new SimpleDateFormat("yyyyMMdd");
            String datum = dateFormat.format(match.getDatum());


            Format timeFormat = new SimpleDateFormat("HH:mm");
            String zeit = timeFormat.format(match.getDatum());

            //   System.out.println(match.getLocation() + " " + zeit + " " + datum + " Zuschauer: " + match.getZuschauer() + "\n" + match.getTeam1() + " " + match.getTore1() + " - " + match.getTeam2() + " " + match.getTore2());
            System.out.println(match.getLocation() + " " + zeit + " " + datum + " Zuschauer: " + match.getZuschauer() + "\n" + match.getTeam1() + " " + match.getTore1() + " - " + match.getTeam2() + " " + match.getTore2());
            //System.out.println("http://api.wunderground.com/api/KEY/" + datum + "/q/CA/" + match.getLocation() + ".json\n");
            //System.out.println("wget -O $BASE/weatherdata_" + datum +"_"+ match.getTeam1() + ".json \"http://api.wunderground.com/api/KEY/" + datum + "/q/germany/" + match.getTeam1() + ".json\"\n");


        }

        //uhrzeit[1] = Uhrzeit, Uhrzeit[0] = Datum

        //Wetterdaten auslesen

        //http://api.wunderground.com/api/KEY/history_YYYYMMDD/q/CA/San_Francisco.json

    }


    File folder = new File("/CachedData/Weatherdatatest");

        if(folder !=null&&folder.isDirectory())

    {

        for (final File file : folder.listFiles((dir, name) -> name.endsWith(".json"))) {

            JsonStreamParser parser = new JsonStreamParser(new FileReader(file));

            while (parser.hasNext()) {

                JsonElement object = parser.next();
                String filename = file.getName();
                String location = filename.substring(filename.lastIndexOf("_") + 1, filename.lastIndexOf("."));
                System.out.println(location);


                JsonObject jsonObject = object.getAsJsonObject();

                try {
                    jsonObject.getAsJsonObject("history").remove("dailysummary");
                    jsonObject.getAsJsonObject("history").remove("stadt");
                    jsonObject.getAsJsonObject("history").remove("date");
                    jsonObject.getAsJsonObject("history").remove("utcdate");
                    jsonObject.remove("response");
                    JsonArray array = jsonObject.getAsJsonObject("history").getAsJsonArray("observations");
                    Iterator<JsonElement> iterator = array.iterator();
                    while (iterator.hasNext()) {
                        JsonElement element = iterator.next();
                        JsonObject object1 = element.getAsJsonObject();
                        object1.remove("utcdate");

                        JsonObject dateObject = object1.getAsJsonObject("date");
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, dateObject.get("year").getAsInt());
                        calendar.set(Calendar.MONTH, dateObject.get("mon").getAsInt() - 1);
                        calendar.set(Calendar.DAY_OF_MONTH, dateObject.get("mday").getAsInt());
                        calendar.set(Calendar.HOUR_OF_DAY, dateObject.get("hour").getAsInt());
                        calendar.set(Calendar.MINUTE, dateObject.get("min").getAsInt());
                        calendar.set(Calendar.SECOND, 0);

                        Date date = calendar.getTime();

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        object1.addProperty("zeitpunkt", format.format(date));

                        object1.addProperty("stadt", location);

                        object1.remove("date");
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }


                Gson gson = new Gson();

                // convert java object to JSON format,
                // and returned as JSON formatted string
                String json = gson.toJson(object);

                //write converted json data to a file named "CountryGSON.json"
                FileWriter writer = new FileWriter(file.getAbsolutePath());
                writer.write(json);
                writer.close();
            }
        }
    }

}